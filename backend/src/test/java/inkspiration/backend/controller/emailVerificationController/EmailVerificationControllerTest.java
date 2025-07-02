package inkspiration.backend.controller.emailVerificationController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.EmailVerificationController;
import inkspiration.backend.dto.EmailVerificationRequest;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.emailverification.EmailVerificationCriacaoUsuarioException;
import inkspiration.backend.exception.emailverification.EmailVerificationEnvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationReenvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationValidacaoException;
import inkspiration.backend.service.EmailVerificationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailVerificationController - Testes Completos")
class EmailVerificationControllerTest {

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private EmailVerificationController emailVerificationController;

    private UsuarioDTO usuarioDTO;
    private EmailVerificationRequest emailVerificationRequest;
    private Usuario usuario;
    private Map<String, String> resendRequest;

    @BeforeEach
    void setUp() {
        setupUsuarioDTO();
        setupEmailVerificationRequest();
        setupUsuario();
        setupResendRequest();
    }

    private void setupUsuarioDTO() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("joao@email.com");
        usuarioDTO.setDataNascimento("15/05/1990");
        usuarioDTO.setTelefone("11987654321");
        usuarioDTO.setSenha("minhasenha123");
        usuarioDTO.setRole(UserRole.ROLE_USER.name());
    }

    private void setupEmailVerificationRequest() {
        emailVerificationRequest = new EmailVerificationRequest();
        emailVerificationRequest.setEmail("joao@email.com");
        emailVerificationRequest.setCode("123456");
    }

    private void setupUsuario() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
    }

    private void setupResendRequest() {
        resendRequest = new HashMap<>();
        resendRequest.put("email", "joao@email.com");
    }

    // =================== TESTES DE SOLICITAÇÃO DE VERIFICAÇÃO ===================

    @Test
    @DisplayName("Deve solicitar verificação de email com sucesso")
    void deveSolicitarVerificacaoDeEmailComSucesso() {
        // Arrange
        doNothing().when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.requestEmailVerification(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Email de verificação enviado com sucesso", responseBody.get("message"));
        assertEquals("joao@email.com", responseBody.get("email"));

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve solicitar verificação para usuário profissional")
    void deveSolicitarVerificacaoParaUsuarioProfissional() {
        // Arrange
        usuarioDTO.setRole(UserRole.ROLE_PROF.name());
        usuarioDTO.setEmail("maria.tattoo@studio.com");
        doNothing().when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.requestEmailVerification(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("maria.tattoo@studio.com", responseBody.get("email"));

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao solicitar verificação com dados inválidos")
    void deveLancarExcecaoAoSolicitarVerificacaoComDadosInvalidos() {
        // Arrange
        doThrow(new EmailVerificationEnvioException("Dados do usuário inválidos"))
                .when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act & Assert
        assertThrows(EmailVerificationEnvioException.class, () -> {
            emailVerificationController.requestEmailVerification(usuarioDTO);
        });

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao solicitar verificação com email já verificado")
    void deveLancarExcecaoAoSolicitarVerificacaoComEmailJaVerificado() {
        // Arrange
        doThrow(new EmailVerificationEnvioException("Email já foi verificado"))
                .when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act & Assert
        assertThrows(EmailVerificationEnvioException.class, () -> {
            emailVerificationController.requestEmailVerification(usuarioDTO);
        });

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao solicitar verificação com falha no envio")
    void deveLancarExcecaoAoSolicitarVerificacaoComFalhaNoEnvio() {
        // Arrange
        doThrow(new EmailVerificationEnvioException("Falha ao enviar email"))
                .when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act & Assert
        assertThrows(EmailVerificationEnvioException.class, () -> {
            emailVerificationController.requestEmailVerification(usuarioDTO);
        });

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    // =================== TESTES DE VERIFICAÇÃO DE EMAIL ===================

    @Test
    @DisplayName("Deve verificar email e criar usuário com sucesso")
    void deveVerificarEmailECriarUsuarioComSucesso() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenReturn(usuario);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.verifyEmail(emailVerificationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Conta criada com sucesso!", responseBody.get("message"));
        assertEquals(usuario, responseBody.get("usuario"));

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    @Test
    @DisplayName("Deve verificar email com código diferente")
    void deveVerificarEmailComCodigoDiferente() {
        // Arrange
        emailVerificationRequest.setCode("789012");
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                "789012"))
                .thenReturn(usuario);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.verifyEmail(emailVerificationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuario, response.getBody().get("usuario"));

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                "joao@email.com", "789012");
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar com código inválido")
    void deveLancarExcecaoAoVerificarComCodigoInvalido() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenThrow(new EmailVerificationValidacaoException("Código inválido"));

        // Act & Assert
        assertThrows(EmailVerificationValidacaoException.class, () -> {
            emailVerificationController.verifyEmail(emailVerificationRequest);
        });

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar com código expirado")
    void deveLancarExcecaoAoVerificarComCodigoExpirado() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenThrow(new EmailVerificationValidacaoException("Código expirado"));

        // Act & Assert
        assertThrows(EmailVerificationValidacaoException.class, () -> {
            emailVerificationController.verifyEmail(emailVerificationRequest);
        });

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar com email não encontrado")
    void deveLancarExcecaoAoVerificarComEmailNaoEncontrado() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenThrow(new EmailVerificationValidacaoException("Email não encontrado"));

        // Act & Assert
        assertThrows(EmailVerificationValidacaoException.class, () -> {
            emailVerificationController.verifyEmail(emailVerificationRequest);
        });

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar com falha na criação do usuário")
    void deveLancarExcecaoAoVerificarComFalhaNaCriacaoDoUsuario() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenThrow(new EmailVerificationCriacaoUsuarioException("Falha ao criar usuário"));

        // Act & Assert
        assertThrows(EmailVerificationCriacaoUsuarioException.class, () -> {
            emailVerificationController.verifyEmail(emailVerificationRequest);
        });

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    // =================== TESTES DE REENVIO DE CÓDIGO ===================

    @Test
    @DisplayName("Deve reenviar código de verificação com sucesso")
    void deveReenviarCodigoDeVerificacaoComSucesso() {
        // Arrange
        String email = "joao@email.com";
        doNothing().when(emailVerificationService)
                .resendVerificationCodeComValidacao(email);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.resendVerificationCode(resendRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Código de verificação reenviado com sucesso", responseBody.get("message"));

        verify(emailVerificationService).resendVerificationCodeComValidacao(email);
    }

    @Test
    @DisplayName("Deve reenviar código para email diferente")
    void deveReenviarCodigoParaEmailDiferente() {
        // Arrange
        String emailDiferente = "maria@studio.com";
        Map<String, String> requestDiferente = new HashMap<>();
        requestDiferente.put("email", emailDiferente);
        
        doNothing().when(emailVerificationService)
                .resendVerificationCodeComValidacao(emailDiferente);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.resendVerificationCode(requestDiferente);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("success"));

        verify(emailVerificationService).resendVerificationCodeComValidacao(emailDiferente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reenviar para email não encontrado")
    void deveLancarExcecaoAoReenviarParaEmailNaoEncontrado() {
        // Arrange
        String email = "joao@email.com";
        doThrow(new EmailVerificationReenvioException("Email não encontrado"))
                .when(emailVerificationService)
                .resendVerificationCodeComValidacao(email);

        // Act & Assert
        assertThrows(EmailVerificationReenvioException.class, () -> {
            emailVerificationController.resendVerificationCode(resendRequest);
        });

        verify(emailVerificationService).resendVerificationCodeComValidacao(email);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reenviar com falha no envio")
    void deveLancarExcecaoAoReenviarComFalhaNoEnvio() {
        // Arrange
        String email = "joao@email.com";
        doThrow(new EmailVerificationReenvioException("Falha ao enviar email"))
                .when(emailVerificationService)
                .resendVerificationCodeComValidacao(email);

        // Act & Assert
        assertThrows(EmailVerificationReenvioException.class, () -> {
            emailVerificationController.resendVerificationCode(resendRequest);
        });

        verify(emailVerificationService).resendVerificationCodeComValidacao(email);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reenviar com muitas tentativas")
    void deveLancarExcecaoAoReenviarComMuitasTentativas() {
        // Arrange
        String email = "joao@email.com";
        doThrow(new EmailVerificationReenvioException("Muitas tentativas de reenvio"))
                .when(emailVerificationService)
                .resendVerificationCodeComValidacao(email);

        // Act & Assert
        assertThrows(EmailVerificationReenvioException.class, () -> {
            emailVerificationController.resendVerificationCode(resendRequest);
        });

        verify(emailVerificationService).resendVerificationCodeComValidacao(email);
    }

    // =================== TESTES DE CENÁRIOS DE BORDA ===================

    @Test
    @DisplayName("Deve lidar com request de reenvio com email nulo")
    void deveLidarComRequestDeReenvioComEmailNulo() {
        // Arrange
        Map<String, String> requestComEmailNulo = new HashMap<>();
        requestComEmailNulo.put("email", null);
        
        doThrow(new EmailVerificationReenvioException("Email não pode ser nulo"))
                .when(emailVerificationService)
                .resendVerificationCodeComValidacao(null);

        // Act & Assert
        assertThrows(EmailVerificationReenvioException.class, () -> {
            emailVerificationController.resendVerificationCode(requestComEmailNulo);
        });

        verify(emailVerificationService).resendVerificationCodeComValidacao(null);
    }

    @Test
    @DisplayName("Deve processar solicitação com campos opcionais nulos")
    void deveProcessarSolicitacaoComCamposOpcionaisNulos() {
        // Arrange
        usuarioDTO.setTelefone(null);
        usuarioDTO.setImagemPerfil(null);
        doNothing().when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.requestEmailVerification(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve verificar estrutura completa da resposta de solicitação")
    void deveVerificarEstruturaCompletaDaRespostaDeSolicitacao() {
        // Arrange
        doNothing().when(emailVerificationService)
                .requestEmailVerificationComValidacao(usuarioDTO);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.requestEmailVerification(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(3, responseBody.size()); // success, message, email
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("email"));
        
        assertEquals(true, responseBody.get("success"));
        assertEquals("Email de verificação enviado com sucesso", responseBody.get("message"));
        assertEquals("joao@email.com", responseBody.get("email"));

        verify(emailVerificationService).requestEmailVerificationComValidacao(usuarioDTO);
    }

    @Test
    @DisplayName("Deve verificar estrutura completa da resposta de verificação")
    void deveVerificarEstruturaCompletaDaRespostaDeVerificacao() {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode()))
                .thenReturn(usuario);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.verifyEmail(emailVerificationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(3, responseBody.size()); // success, message, usuario
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("message"));
        assertTrue(responseBody.containsKey("usuario"));
        
        assertEquals(true, responseBody.get("success"));
        assertEquals("Conta criada com sucesso!", responseBody.get("message"));
        assertEquals(usuario, responseBody.get("usuario"));

        verify(emailVerificationService).verifyEmailAndCreateUserComValidacao(
                emailVerificationRequest.getEmail(), 
                emailVerificationRequest.getCode());
    }

    @Test
    @DisplayName("Deve verificar estrutura completa da resposta de reenvio")
    void deveVerificarEstruturaCompletaDaRespostaDeReenvio() {
        // Arrange
        String email = "joao@email.com";
        doNothing().when(emailVerificationService)
                .resendVerificationCodeComValidacao(email);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                emailVerificationController.resendVerificationCode(resendRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size()); // success, message
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("message"));
        
        assertEquals(true, responseBody.get("success"));
        assertEquals("Código de verificação reenviado com sucesso", responseBody.get("message"));

        verify(emailVerificationService).resendVerificationCodeComValidacao(email);
    }
}