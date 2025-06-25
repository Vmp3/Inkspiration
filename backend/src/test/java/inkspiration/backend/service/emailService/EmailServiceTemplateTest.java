package inkspiration.backend.service.emailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import inkspiration.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Testes de Templates")
class EmailServiceTemplateTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@inkspiration.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar template de recuperação de senha válido")
    void deveGerarTemplateRecuperacaoSenhaValido() {
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
    @DisplayName("Deve gerar template de confirmação de senha válido")
    void deveGerarTemplateConfirmacaoSenhaValido() {
        
        String email = "usuario@teste.com";
        String nome = "João Silva";
        
        
        emailService.sendPasswordResetConfirmation(email, nome);
        
        
        verify(mailSender).createMimeMessage();
        
    }

    @Test
    @DisplayName("Deve gerar template de recuperação 2FA válido")
    void deveGerarTemplateRecuperacao2FAValido() {
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
    @DisplayName("Deve gerar template de verificação de email válido")
    void deveGerarTemplateVerificacaoEmailValido() {
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
    @DisplayName("Deve gerar templates com códigos especiais")
    void deveGerarTemplatesComCodigosEspeciais() {
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Teste";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, "000000");
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, "999999");
            emailService.sendEmailVerification(toEmail, userName, "123456");
        });

        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar templates com nomes especiais")
    void deveGerarTemplatesComNomesEspeciais() {
        String toEmail = "usuario@teste.com";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, null, code);
            emailService.sendPasswordResetCode(toEmail, "", code);
            emailService.sendPasswordResetCode(toEmail, "   ", code);
            emailService.sendPasswordResetCode(toEmail, "Nome<script>alert('xss')</script>", code);
        });

        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(4)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar templates com diferentes configurações de email")
    void deveGerarTemplatesComDiferentesConfiguracoes() {
        
        ReflectionTestUtils.setField(emailService, "fromEmail", "custom@inkspiration.com");
        
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Personalizado";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar todos os templates em sequência")
    void deveGerarTodosTemplatesEmSequencia() {
        
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
    @DisplayName("Deve gerar templates com códigos de diferentes tamanhos")
    void deveGerarTemplatesComCodigosDiferentesTamanhos() {
        String toEmail = "usuario@teste.com";
        String userName = "Usuário Códigos";

        assertDoesNotThrow(() -> {
            
            emailService.sendPasswordResetCode(toEmail, userName, "123456");
            emailService.sendTwoFactorRecoveryCode(toEmail, userName, "654321");
            emailService.sendEmailVerification(toEmail, userName, "789012");
        });

        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar templates com nomes de diferentes tamanhos")
    void deveGerarTemplatesComNomesDiferentesTamanhos() {
        String toEmail = "usuario@teste.com";
        String code = "123456";

        assertDoesNotThrow(() -> {
            
            emailService.sendPasswordResetCode(toEmail, "Ana", code);
            
            
            emailService.sendPasswordResetCode(toEmail, "João Silva Santos", code);
            
            
            emailService.sendPasswordResetCode(toEmail, "João Silva Santos Oliveira Ferreira Costa Lima", code);
            
            
            emailService.sendPasswordResetCode(toEmail, "SupercalifragilisticexpialidociousNome", code);
        });

        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(4)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar templates com caracteres especiais em nomes")
    void deveGerarTemplatesComCaracteresEspeciaisEmNomes() {
        String toEmail = "usuario@teste.com";
        String code = "123456";

        assertDoesNotThrow(() -> {
            
            emailService.sendPasswordResetCode(toEmail, "João<Silva", code);
            emailService.sendPasswordResetCode(toEmail, "Maria>Santos", code);
            emailService.sendPasswordResetCode(toEmail, "Pedro\"Oliveira", code);
            emailService.sendPasswordResetCode(toEmail, "Ana'Costa", code);
            emailService.sendPasswordResetCode(toEmail, "Carlos&Lima", code);
        });

        verify(mailSender, times(5)).createMimeMessage();
        verify(mailSender, times(5)).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve gerar template de confirmação sem código")
    void deveGerarTemplateConfirmacaoSemCodigo() {
        
        String email = "usuario@teste.com";
        String nome = "Maria Santos";
        
        
        emailService.sendPasswordResetConfirmation(email, nome);
        
        
        verify(mailSender).createMimeMessage();
        
    }
} 