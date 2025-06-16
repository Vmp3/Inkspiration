package inkspiration.backend.service;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EmailVerificationService")
class EmailVerificationServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        // Limpar o mapa de registros pendentes antes de cada teste
        ConcurrentHashMap<String, Object> pendingRegistrations = new ConcurrentHashMap<>();
        ReflectionTestUtils.setField(emailVerificationService, "pendingRegistrations", pendingRegistrations);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setEmail("test@test.com");
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setCpf("12345678901");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("test@test.com");
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve solicitar verificação de email com sucesso")
    void deveSolicitarVerificacaoEmailComSucesso() {
        // Given
        when(usuarioService.buscarPorEmailOptional("test@test.com")).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> emailVerificationService.requestEmailVerification(usuarioDTO));

        // Then
        verify(usuarioService, times(1)).buscarPorEmailOptional("test@test.com");
        verify(usuarioService, times(1)).buscarPorCpfOptional("12345678901");
        verify(emailService, times(1)).sendEmailVerification(eq("test@test.com"), eq("João Silva"), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Given
        when(usuarioService.buscarPorEmailOptional("test@test.com")).thenReturn(usuario);

        // When & Then
        UsuarioException.EmailJaExisteException exception = assertThrows(
            UsuarioException.EmailJaExisteException.class,
            () -> emailVerificationService.requestEmailVerification(usuarioDTO)
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioService, times(1)).buscarPorEmailOptional("test@test.com");
        verify(emailService, never()).sendEmailVerification(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        // Given
        when(usuarioService.buscarPorEmailOptional("test@test.com")).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(usuario);

        // When & Then
        UsuarioException.CpfJaExisteException exception = assertThrows(
            UsuarioException.CpfJaExisteException.class,
            () -> emailVerificationService.requestEmailVerification(usuarioDTO)
        );

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(usuarioService, times(1)).buscarPorEmailOptional("test@test.com");
        verify(usuarioService, times(1)).buscarPorCpfOptional("12345678901");
        verify(emailService, never()).sendEmailVerification(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando falhar ao enviar email")
    void deveLancarExcecaoQuandoFalharEnviarEmail() {
        // Given
        when(usuarioService.buscarPorEmailOptional("test@test.com")).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doThrow(new RuntimeException("Erro ao enviar email")).when(emailService)
            .sendEmailVerification(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailVerificationService.requestEmailVerification(usuarioDTO)
        );

        assertTrue(exception.getMessage().contains("Erro ao enviar email de verificação"));
        verify(emailService, times(1)).sendEmailVerification(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve verificar email e criar usuário com sucesso")
    void deveVerificarEmailECriarUsuarioComSucesso() {
        // Given
        String email = "test@test.com";
        
        // Primeiro, simular a solicitação de verificação
        when(usuarioService.buscarPorEmailOptional(email)).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());
        
        emailVerificationService.requestEmailVerification(usuarioDTO);
        
        // Obter o código gerado através do mapa de registros pendentes
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Object> pendingRegistrations = 
            (ConcurrentHashMap<String, Object>) ReflectionTestUtils.getField(emailVerificationService, "pendingRegistrations");
        
        Object pendingReg = pendingRegistrations.get(email);
        String generatedCode = (String) ReflectionTestUtils.getField(pendingReg, "verificationCode");
        
        // Mockar a criação do usuário
        when(usuarioService.criar(any(UsuarioDTO.class))).thenReturn(usuario);
        doNothing().when(usuarioService).salvar(any(Usuario.class));

        // When
        Usuario resultado = emailVerificationService.verifyEmailAndCreateUser(email, generatedCode);

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(usuarioService, times(1)).criar(any(UsuarioDTO.class));
        verify(usuarioService, times(1)).salvar(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar código não encontrado")
    void deveLancarExcecaoAoVerificarCodigoNaoEncontrado() {
        // Given
        String email = "test@test.com";
        String code = "123456";

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailVerificationService.verifyEmailAndCreateUser(email, code)
        );

        assertEquals("Código de verificação não encontrado ou expirado", exception.getMessage());
        verify(usuarioService, never()).criar(any(UsuarioDTO.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar código inválido")
    void deveLancarExcecaoAoVerificarCodigoInvalido() {
        // Given
        String email = "test@test.com";
        String codeCorreto = "123456";
        String codeIncorreto = "654321";
        
        // Primeiro, simular a solicitação de verificação
        when(usuarioService.buscarPorEmailOptional(email)).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());
        
        emailVerificationService.requestEmailVerification(usuarioDTO);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailVerificationService.verifyEmailAndCreateUser(email, codeIncorreto)
        );

        assertEquals("Código de verificação inválido", exception.getMessage());
        verify(usuarioService, never()).criar(any(UsuarioDTO.class));
    }

    @Test
    @DisplayName("Deve reenviar código de verificação com sucesso")
    void deveReenviarCodigoVerificacaoComSucesso() {
        // Given
        String email = "test@test.com";
        
        // Primeiro, simular a solicitação de verificação
        when(usuarioService.buscarPorEmailOptional(email)).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());
        
        emailVerificationService.requestEmailVerification(usuarioDTO);

        // When
        assertDoesNotThrow(() -> emailVerificationService.resendVerificationCode(email));

        // Then
        verify(emailService, times(2)).sendEmailVerification(eq(email), eq("João Silva"), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao reenviar código para email não encontrado")
    void deveLancarExcecaoAoReenviarCodigoEmailNaoEncontrado() {
        // Given
        String email = "test@test.com";

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailVerificationService.resendVerificationCode(email)
        );

        assertEquals("Nenhuma solicitação de verificação encontrada para este email", exception.getMessage());
        verify(emailService, never()).sendEmailVerification(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro ao criar usuário")
    void deveLancarExcecaoQuandoErroAoCriarUsuario() {
        // Given
        String email = "test@test.com";
        
        // Primeiro, simular a solicitação de verificação
        when(usuarioService.buscarPorEmailOptional(email)).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());
        
        emailVerificationService.requestEmailVerification(usuarioDTO);
        
        // Obter o código gerado
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Object> pendingRegistrations = 
            (ConcurrentHashMap<String, Object>) ReflectionTestUtils.getField(emailVerificationService, "pendingRegistrations");
        
        Object pendingReg = pendingRegistrations.get(email);
        String generatedCode = (String) ReflectionTestUtils.getField(pendingReg, "verificationCode");
        
        // Mockar erro na criação do usuário
        when(usuarioService.criar(any(UsuarioDTO.class))).thenThrow(new RuntimeException("Erro na criação"));

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailVerificationService.verifyEmailAndCreateUser(email, generatedCode)
        );

        assertTrue(exception.getMessage().contains("Erro ao criar usuário"));
        verify(usuarioService, times(1)).criar(any(UsuarioDTO.class));
        verify(usuarioService, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve processar CPF com formatação")
    void deveProcessarCpfComFormatacao() {
        // Given
        usuarioDTO.setCpf("123.456.789-01");
        when(usuarioService.buscarPorEmailOptional("test@test.com")).thenReturn(null);
        when(usuarioService.buscarPorCpfOptional("12345678901")).thenReturn(null);
        doNothing().when(emailService).sendEmailVerification(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> emailVerificationService.requestEmailVerification(usuarioDTO));

        // Then
        verify(usuarioService, times(1)).buscarPorCpfOptional("12345678901");
        verify(emailService, times(1)).sendEmailVerification(anyString(), anyString(), anyString());
    }
} 