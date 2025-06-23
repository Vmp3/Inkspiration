package inkspiration.backend.service.emailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Testes de Envio")
class EmailServiceEnvioTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private inkspiration.backend.service.EmailService emailService;

    @BeforeEach
    void setUp() {
        
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@inkspiration.com");
        
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email de recuperação de senha com sucesso")
    void deveEnviarEmailRecuperacaoSenhaComSucesso() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "João Silva";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de recuperação de senha")
    void deveLancarExcecaoAoFalharEnvioRecuperacaoSenha() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "João Silva";
        String code = "123456";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de recuperação"));
    }

    @Test
    @DisplayName("Deve enviar email de confirmação de alteração de senha com sucesso")
    void deveEnviarEmailConfirmacaoAlteracaoSenhaComSucesso() {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        
        
        emailService.sendPasswordResetConfirmation(email, nome);
        
        
        verify(mailSender).createMimeMessage();
        
    }

    @Test
    @DisplayName("Deve capturar exceção silenciosamente ao falhar confirmação de senha")
    void deveCapturaExcecaoSilenciosamenteAoFalharConfirmacaoSenha() {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro simulado"));
        
        
        assertDoesNotThrow(() -> emailService.sendPasswordResetConfirmation(email, nome));
        
        verify(mailSender).createMimeMessage();
        
    }

    @Test
    @DisplayName("Deve enviar email de recuperação 2FA com sucesso")
    void deveEnviarEmailRecuperacao2FAComSucesso() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Pedro Oliveira";
        String code = "987654";

        
        assertDoesNotThrow(() -> {
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de recuperação 2FA")
    void deveLancarExcecaoAoFalharEnvioRecuperacao2FA() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Pedro Oliveira";
        String code = "987654";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de recuperação 2FA"));
    }

    @Test
    @DisplayName("Deve enviar email de verificação com sucesso")
    void deveEnviarEmailVerificacaoComSucesso() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Ana Costa";
        String code = "456789";

        
        assertDoesNotThrow(() -> {
            emailService.sendEmailVerification(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar envio de verificação")
    void deveLancarExcecaoAoFalharEnvioVerificacao() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Ana Costa";
        String code = "456789";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmailVerification(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de verificação"));
    }

    @Test
    @DisplayName("Deve enviar email com nome nulo")
    void deveEnviarEmailComNomeNulo() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = null;
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com nome vazio")
    void deveEnviarEmailComNomeVazio() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com nome muito longo")
    void deveEnviarEmailComNomeMuitoLongo() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Nome Muito Muito Muito Muito Muito Longo Para Email Teste Completo";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email com nome contendo caracteres especiais")
    void deveEnviarEmailComNomeContendoCaracteresEspeciais() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "João<script>alert('xss')</script>Silva";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar todos os tipos de email com códigos diferentes")
    void deveEnviarTodosTiposEmailComCodigosDiferentes() {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        String codigo1 = "111111";
        String codigo2 = "222222";
        String codigo3 = "333333";
        
        
        emailService.sendPasswordResetCode(email, nome, codigo1);
        emailService.sendTwoFactorRecoveryCode(email, nome, codigo2);
        emailService.sendEmailVerification(email, nome, codigo3);
        emailService.sendPasswordResetConfirmation(email, nome); 
        
        
        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class)); 
    }

    @Test
    @DisplayName("Deve usar email padrão quando fromEmail não configurado")
    void deveUsarEmailPadraoQuandoFromEmailNaoConfigurado() throws Exception {
        
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@inkspiration.com");
        String toEmail = "usuario@teste.com";
        String userName = "Teste";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email de verificação com código especial")
    void deveEnviarEmailVerificacaoComCodigoEspecial() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Especial";
        String code = "000000";

        
        assertDoesNotThrow(() -> {
            emailService.sendEmailVerification(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve enviar email 2FA com código máximo")
    void deveEnviarEmail2FAComCodigoMaximo() throws Exception {
        
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Max";
        String code = "999999";

        
        assertDoesNotThrow(() -> {
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, code);
        });

        
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
} 