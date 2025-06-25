package inkspiration.backend.controller.passwordResetController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import inkspiration.backend.controller.PasswordResetController;
import inkspiration.backend.service.PasswordResetService;
import inkspiration.backend.dto.ForgotPasswordDTO;
import inkspiration.backend.dto.ResetPasswordDTO;
import inkspiration.backend.exception.passwordreset.*;
import inkspiration.backend.exception.UsuarioValidationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordResetController - Testes Completos")
class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    private ForgotPasswordDTO forgotPasswordDTO;
    private ResetPasswordDTO resetPasswordDTO;
    private final String CPF = "12345678901";
    private final String CODE = "123456";
    private final String NEW_PASSWORD = "NovaS3nha@123";

    @BeforeEach
    void setUp() {
        // Setup mock ForgotPasswordDTO
        forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setCpf(CPF);

        // Setup mock ResetPasswordDTO
        resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCpf(CPF);
        resetPasswordDTO.setCode(CODE);
        resetPasswordDTO.setNewPassword(NEW_PASSWORD);
    }

    // Testes para forgotPassword()
    @Test
    @DisplayName("Deve gerar código de recuperação com sucesso")
    void deveGerarCodigoRecuperacaoComSucesso() {
        // Arrange
        String expectedMessage = "Código de recuperação enviado para t***@email.com";
        when(passwordResetService.gerarCodigoRecuperacaoComValidacao(CPF))
            .thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = passwordResetController.forgotPassword(forgotPasswordDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedMessage, response.getBody());
        verify(passwordResetService).gerarCodigoRecuperacaoComValidacao(CPF);
    }

    @Test
    @DisplayName("Deve propagar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(passwordResetService.gerarCodigoRecuperacaoComValidacao(CPF))
            .thenThrow(new PasswordResetGeracaoException("Usuário não encontrado com o CPF informado"));

        // Act & Assert
        PasswordResetGeracaoException exception = assertThrows(
            PasswordResetGeracaoException.class,
            () -> passwordResetController.forgotPassword(forgotPasswordDTO)
        );

        assertEquals("Usuário não encontrado com o CPF informado", exception.getMessage());
        verify(passwordResetService).gerarCodigoRecuperacaoComValidacao(CPF);
    }

    @Test
    @DisplayName("Deve propagar exceção quando exceder limite de tentativas")
    void deveLancarExcecaoQuandoExcederLimiteTentativas() {
        // Arrange
        when(passwordResetService.gerarCodigoRecuperacaoComValidacao(CPF))
            .thenThrow(new PasswordResetGeracaoException("Muitas tentativas. Tente novamente em 15 minutos"));

        // Act & Assert
        PasswordResetGeracaoException exception = assertThrows(
            PasswordResetGeracaoException.class,
            () -> passwordResetController.forgotPassword(forgotPasswordDTO)
        );

        assertEquals("Muitas tentativas. Tente novamente em 15 minutos", exception.getMessage());
        verify(passwordResetService).gerarCodigoRecuperacaoComValidacao(CPF);
    }

    @Test
    @DisplayName("Deve propagar exceção quando falhar envio de email")
    void deveLancarExcecaoQuandoFalharEnvioEmail() {
        // Arrange
        when(passwordResetService.gerarCodigoRecuperacaoComValidacao(CPF))
            .thenThrow(new PasswordResetProcessamentoException("Falha ao enviar email de recuperação. Tente novamente."));

        // Act & Assert
        PasswordResetProcessamentoException exception = assertThrows(
            PasswordResetProcessamentoException.class,
            () -> passwordResetController.forgotPassword(forgotPasswordDTO)
        );

        assertEquals("Falha ao enviar email de recuperação. Tente novamente.", exception.getMessage());
        verify(passwordResetService).gerarCodigoRecuperacaoComValidacao(CPF);
    }

    // Testes para resetPassword()
    @Test
    @DisplayName("Deve redefinir senha com sucesso")
    void deveRedefinirSenhaComSucesso() {
        // Arrange
        String expectedMessage = "Senha redefinida com sucesso";
        when(passwordResetService.redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD))
            .thenReturn(expectedMessage);

        // Act
        ResponseEntity<String> response = passwordResetController.resetPassword(resetPasswordDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedMessage, response.getBody());
        verify(passwordResetService).redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Deve propagar exceção quando código inválido")
    void deveLancarExcecaoQuandoCodigoInvalido() {
        // Arrange
        when(passwordResetService.redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD))
            .thenThrow(new PasswordResetValidacaoException("Código inválido ou expirado"));

        // Act & Assert
        PasswordResetValidacaoException exception = assertThrows(
            PasswordResetValidacaoException.class,
            () -> passwordResetController.resetPassword(resetPasswordDTO)
        );

        assertEquals("Código inválido ou expirado", exception.getMessage());
        verify(passwordResetService).redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Deve propagar exceção quando senha inválida")
    void deveLancarExcecaoQuandoSenhaInvalida() {
        // Arrange
        when(passwordResetService.redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD))
            .thenThrow(new UsuarioValidationException.SenhaInvalidaException("A senha deve ter pelo menos 8 caracteres"));

        // Act & Assert
        UsuarioValidationException.SenhaInvalidaException exception = assertThrows(
            UsuarioValidationException.SenhaInvalidaException.class,
            () -> passwordResetController.resetPassword(resetPasswordDTO)
        );

        assertEquals("A senha deve ter pelo menos 8 caracteres", exception.getMessage());
        verify(passwordResetService).redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer erro interno")
    void deveLancarExcecaoQuandoOcorrerErroInterno() {
        // Arrange
        when(passwordResetService.redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD))
            .thenThrow(new PasswordResetProcessamentoException("Erro interno do servidor. Tente novamente mais tarde."));

        // Act & Assert
        PasswordResetProcessamentoException exception = assertThrows(
            PasswordResetProcessamentoException.class,
            () -> passwordResetController.resetPassword(resetPasswordDTO)
        );

        assertEquals("Erro interno do servidor. Tente novamente mais tarde.", exception.getMessage());
        verify(passwordResetService).redefinirSenhaComValidacao(CPF, CODE, NEW_PASSWORD);
    }
} 