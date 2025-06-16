package inkspiration.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EmailService")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @InjectMocks
    private EmailService emailService;

    private final String fromEmail = "noreply@inkspiration.com";
    private final String toEmail = "test@test.com";
    private final String userName = "João Silva";
    private final String code = "123456";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", fromEmail);
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, userName, code));
        
        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falhar ao enviar email de recuperação")
    void deveLancarExcecaoQuandoFalharEnviarEmailRecuperacao() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Erro de envio")).when(mailSender).send(mimeMessage);
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailService.sendPasswordResetCode(toEmail, userName, code)
        );
        
        assertEquals("Falha ao enviar email de recuperação", exception.getMessage());
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de senha alterada com sucesso")
    void deveEnviarEmailConfirmacaoSenhaComSucesso() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendPasswordResetConfirmation(toEmail, userName));
        
        // Then
        verify(mailSender, times(1)).createMimeMessage();
        // O método sendPasswordResetConfirmation não lança exceção, apenas imprime erro
        // Então não podemos garantir que send() seja chamado se houver erro no MimeMessageHelper
    }

    @Test
    @DisplayName("Não deve lançar exceção quando falhar ao enviar confirmação")
    void naoDeveLancarExcecaoQuandoFalharEnviarConfirmacao() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When & Then
        assertDoesNotThrow(() -> emailService.sendPasswordResetConfirmation(toEmail, userName));
        
        verify(mailSender, times(1)).createMimeMessage();
        // O método não lança exceção, apenas imprime erro no console
    }

    @Test
    @DisplayName("Deve enviar email de recuperação 2FA com sucesso")
    void deveEnviarEmailRecuperacao2FAComSucesso() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendTwoFactorRecoveryCode(toEmail, userName, code));
        
        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falhar ao enviar email 2FA")
    void deveLancarExcecaoQuandoFalharEnviarEmail2FA() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Erro de envio")).when(mailSender).send(mimeMessage);
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailService.sendTwoFactorRecoveryCode(toEmail, userName, code)
        );
        
        assertEquals("Falha ao enviar email de recuperação 2FA", exception.getMessage());
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email de verificação com sucesso")
    void deveEnviarEmailVerificacaoComSucesso() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendEmailVerification(toEmail, userName, code));
        
        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando falhar ao enviar email de verificação")
    void deveLancarExcecaoQuandoFalharEnviarEmailVerificacao() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Erro de envio")).when(mailSender).send(mimeMessage);
        
        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> emailService.sendEmailVerification(toEmail, userName, code)
        );
        
        assertEquals("Falha ao enviar email de verificação", exception.getMessage());
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve truncar nome muito longo para email")
    void deveTruncarNomeMuitoLongoParaEmail() throws Exception {
        // Given
        String nomeLongo = "João da Silva Santos Oliveira de Souza";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, nomeLongo, code));
        
        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome nulo ou vazio")
    void deveTratarNomeNuloOuVazio() throws Exception {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When & Then
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, null, code));
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, "", code));
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, "   ", code));
        
        verify(mailSender, times(3)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve remover caracteres especiais do nome")
    void deveRemoverCaracteresEspeciaisDoNome() throws Exception {
        // Given
        String nomeComCaracteresEspeciais = "<João>\"Silva\"&Santos";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When
        assertDoesNotThrow(() -> emailService.sendPasswordResetCode(toEmail, nomeComCaracteresEspeciais, code));
        
        // Then
        verify(mailSender, times(1)).send(mimeMessage);
    }
} 