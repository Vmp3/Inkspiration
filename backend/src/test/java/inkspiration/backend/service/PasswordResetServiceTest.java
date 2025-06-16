package inkspiration.backend.service;

import inkspiration.backend.entities.PasswordResetCode;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.repository.PasswordResetCodeRepository;
import inkspiration.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PasswordResetService")
class PasswordResetServiceTest {

    @Mock
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Usuario usuario;
    private UsuarioAutenticar usuarioAutenticar;
    private PasswordResetCode passwordResetCode;
    private final String cpf = "12345678901";
    private final String email = "test@test.com";
    private final String nome = "João Silva";

    @BeforeEach
    void setUp() {
        usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf(cpf);
        usuarioAutenticar.setSenha("senhaHasheada");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setNome(nome);
        usuario.setUsuarioAutenticar(usuarioAutenticar);

        passwordResetCode = new PasswordResetCode();
        passwordResetCode.setCpf(cpf);
        passwordResetCode.setCode("123456");
        passwordResetCode.setCreatedAt(LocalDateTime.now());
        passwordResetCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        passwordResetCode.setUsed(false);
    }

    @Test
    @DisplayName("Deve gerar código de recuperação com sucesso")
    void deveGerarCodigoRecuperacaoComSucesso() {
        // Given
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.countRecentCodesByCpf(anyString(), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString(), anyString());

        // When
        String resultado = passwordResetService.generatePasswordResetCode(cpf);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.contains("Código de recuperação enviado para"));
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(passwordResetCodeRepository, times(1)).markAllAsUsedByCpf(cpf);
        verify(passwordResetCodeRepository, times(1)).save(any(PasswordResetCode.class));
        verify(emailService, times(1)).sendPasswordResetCode(eq(email), eq(nome), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.generatePasswordResetCode(cpf)
        );

        assertEquals("Usuário não encontrado com o CPF informado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(passwordResetCodeRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando muitas tentativas")
    void deveLancarExcecaoQuandoMuitasTentativas() {
        // Given
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.countRecentCodesByCpf(anyString(), any(LocalDateTime.class))).thenReturn(3);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.generatePasswordResetCode(cpf)
        );

        assertEquals("Muitas tentativas. Tente novamente em 15 minutos", exception.getMessage());
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(passwordResetCodeRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve processar CPF formatado")
    void deveProcessarCpfFormatado() {
        // Given
        String cpfFormatado = "123.456.789-01";
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.countRecentCodesByCpf(anyString(), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        doNothing().when(emailService).sendPasswordResetCode(anyString(), anyString(), anyString());

        // When
        String resultado = passwordResetService.generatePasswordResetCode(cpfFormatado);

        // Then
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(passwordResetCodeRepository, times(1)).markAllAsUsedByCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando falhar ao enviar email")
    void deveLancarExcecaoQuandoFalharEnviarEmail() {
        // Given
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.countRecentCodesByCpf(anyString(), any(LocalDateTime.class))).thenReturn(0);
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        doThrow(new RuntimeException("Erro no envio")).when(emailService)
            .sendPasswordResetCode(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.generatePasswordResetCode(cpf)
        );

        assertEquals("Falha ao enviar email de recuperação. Tente novamente.", exception.getMessage());
        verify(emailService, times(1)).sendPasswordResetCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve resetar senha com sucesso")
    void deveResetarSenhaComSucesso() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.of(passwordResetCode));
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(emailService).sendPasswordResetConfirmation(anyString(), anyString());

        // When
        assertDoesNotThrow(() -> passwordResetService.resetPassword(cpf, codigo, novaSenha));

        // Then
        verify(passwordResetCodeRepository, times(1)).findByCpfAndCodeAndUsedFalse(cpf, codigo);
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(passwordResetCodeRepository, times(1)).save(passwordResetCode);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(emailService, times(1)).sendPasswordResetConfirmation(email, nome);
        assertTrue(passwordResetCode.isUsed());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código inválido ou expirado")
    void deveLancarExcecaoQuandoCodigoInvalidoOuExpirado() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.resetPassword(cpf, codigo, novaSenha)
        );

        assertEquals("Código inválido ou expirado", exception.getMessage());
        verify(passwordResetCodeRepository, times(1)).findByCpfAndCodeAndUsedFalse(cpf, codigo);
        verify(usuarioRepository, never()).findByCpf(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando código expirado")
    void deveLancarExcecaoQuandoCodigoExpirado() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        
        // Simular código expirado
        passwordResetCode.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.of(passwordResetCode));

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.resetPassword(cpf, codigo, novaSenha)
        );

        assertEquals("Código expirado", exception.getMessage());
        verify(passwordResetCodeRepository, times(1)).findByCpfAndCodeAndUsedFalse(cpf, codigo);
        verify(usuarioRepository, never()).findByCpf(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado no reset")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoNoReset() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.of(passwordResetCode));
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> passwordResetService.resetPassword(cpf, codigo, novaSenha)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(passwordResetCodeRepository, times(1)).findByCpfAndCodeAndUsedFalse(cpf, codigo);
        verify(usuarioRepository, times(1)).findByCpf(cpf);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve resetar senha mesmo quando usuário não tem UsuarioAutenticar")
    void deveResetarSenhaMesmoQuandoUsuarioNaoTemUsuarioAutenticar() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        usuario.setUsuarioAutenticar(null);
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.of(passwordResetCode));
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        doNothing().when(emailService).sendPasswordResetConfirmation(anyString(), anyString());

        // When
        assertDoesNotThrow(() -> passwordResetService.resetPassword(cpf, codigo, novaSenha));

        // Then
        verify(passwordResetCodeRepository, times(1)).save(passwordResetCode);
        verify(usuarioRepository, times(1)).save(usuario);
        assertTrue(passwordResetCode.isUsed());
    }

    @Test
    @DisplayName("Deve limpar códigos expirados")
    void deveLimparCodigosExpirados() {
        // When
        assertDoesNotThrow(() -> passwordResetService.cleanupExpiredCodes());

        // Then
        verify(passwordResetCodeRepository, times(1)).deleteExpiredCodes(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando falhar ao enviar confirmação")
    void naoDeveLancarExcecaoQuandoFalharEnviarConfirmacao() {
        // Given
        String novaSenha = "novaSenha123";
        String codigo = "123456";
        
        when(passwordResetCodeRepository.findByCpfAndCodeAndUsedFalse(cpf, codigo))
            .thenReturn(Optional.of(passwordResetCode));
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(passwordResetCodeRepository.save(any(PasswordResetCode.class))).thenReturn(passwordResetCode);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        doThrow(new RuntimeException("Erro no envio")).when(emailService)
            .sendPasswordResetConfirmation(anyString(), anyString());

        // When & Then
        assertDoesNotThrow(() -> passwordResetService.resetPassword(cpf, codigo, novaSenha));

        verify(emailService, times(1)).sendPasswordResetConfirmation(email, nome);
        verify(usuarioRepository, times(1)).save(usuario);
        assertTrue(passwordResetCode.isUsed());
    }
} 