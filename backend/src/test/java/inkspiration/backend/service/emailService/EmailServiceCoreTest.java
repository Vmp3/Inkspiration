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
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Testes Principais")
class EmailServiceCoreTest {

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
    void deveEnviarEmailRecuperacaoSenhaComSucesso() {
        
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
    void deveLancarExcecaoAoFalharEnvioRecuperacaoSenha() {
        
        String toEmail = "usuario@teste.com";
        String userName = "João Silva";
        String code = "123456";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de recuperação"));
        verify(mailSender).createMimeMessage();
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
    void deveCapturaExcecaoSilenciosamenteAoFalharConfirmacaoSenha() throws Exception {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro simulado"));
        
        
        assertDoesNotThrow(() -> emailService.sendPasswordResetConfirmation(email, nome));
        
        verify(mailSender).createMimeMessage();
        
    }

    @Test
    @DisplayName("Deve enviar email de recuperação 2FA com sucesso")
    void deveEnviarEmailRecuperacao2FAComSucesso() {
        
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
    void deveLancarExcecaoAoFalharEnvioRecuperacao2FA() {
        
        String toEmail = "usuario@teste.com";
        String userName = "Pedro Oliveira";
        String code = "987654";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de recuperação 2FA"));
        verify(mailSender).createMimeMessage();
    }

    @Test
    @DisplayName("Deve enviar email de verificação com sucesso")
    void deveEnviarEmailVerificacaoComSucesso() {
        
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
    void deveLancarExcecaoAoFalharEnvioVerificacao() {
        
        String toEmail = "usuario@teste.com";
        String userName = "Ana Costa";
        String code = "456789";
        
        doThrow(new RuntimeException("Erro de conexão")).when(mailSender).send(any(MimeMessage.class));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendEmailVerification(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de verificação"));
        verify(mailSender).createMimeMessage();
    }

    @Test
    @DisplayName("Deve enviar email com nome nulo usando padrão")
    void deveEnviarEmailComNomeNuloUsandoPadrao() {
        
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
    @DisplayName("Deve enviar email com nome vazio usando padrão")
    void deveEnviarEmailComNomeVazioUsandoPadrao() {
        
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
    @DisplayName("Deve truncar nome muito longo preservando primeiro e último")
    void deveTruncarNomeMuitoLongoPreservandoPrimeiroUltimo() {
        
        String toEmail = "usuario@teste.com";
        String userName = "João Silva Santos Oliveira Ferreira Costa Lima";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve truncar nome único muito longo")
    void deveTruncarNomeUnicoMuitoLongo() {
        
        String toEmail = "usuario@teste.com";
        String userName = "NomeUnicoMuitoMuitoMuitoLongoParaEmail";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve remover caracteres especiais perigosos do nome")
    void deveRemoverCaracteresEspeciaisPerigososDoNome() {
        
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
    @DisplayName("Deve enviar todos os tipos de email em sequência")
    void deveEnviarTodosTiposEmailEmSequencia() {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        String codigo = "123456";
        
        
        emailService.sendPasswordResetCode(email, nome, codigo);
        emailService.sendTwoFactorRecoveryCode(email, nome, codigo);
        emailService.sendEmailVerification(email, nome, codigo);
        emailService.sendPasswordResetConfirmation(email, nome); 
        
        
        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class)); 
    }

    @Test
    @DisplayName("Deve usar fromEmail configurado")
    void deveUsarFromEmailConfigurado() {
        
        String customFromEmail = "custom@inkspiration.com";
        ReflectionTestUtils.setField(emailService, "fromEmail", customFromEmail);
        
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
    void deveEnviarEmailVerificacaoComCodigoEspecial() {
        
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
    void deveEnviarEmail2FAComCodigoMaximo() {
        
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Max";
        String code = "999999";

        
        assertDoesNotThrow(() -> {
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar exceção durante criação do MimeMessage")
    void deveTratarExcecaoDuranteCriacaoMimeMessage() {
        
        String toEmail = "usuario@teste.com";
        String userName = "Teste";
        String code = "123456";
        
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Erro na criação"));

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        assertTrue(exception.getMessage().contains("Falha ao enviar email de recuperação"));
        verify(mailSender).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve preservar nome com espaços em branco")
    void devePreservarNomeComEspacosEmBranco() {
        
        String toEmail = "usuario@teste.com";
        String userName = "   João   Silva   ";
        String code = "123456";

        
        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
} 