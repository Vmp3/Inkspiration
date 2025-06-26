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
import inkspiration.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService - Testes de Truncamento de Nome")
class EmailServiceTruncateTest {

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
    @DisplayName("Deve usar nome padrão quando nome é nulo")
    void deveUsarNomePadraoQuandoNomeNulo() {
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
    @DisplayName("Deve usar nome padrão quando nome é vazio")
    void deveUsarNomePadraoQuandoNomeVazio() {
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
    @DisplayName("Deve usar nome padrão quando nome só tem espaços")
    void deveUsarNomePadraoQuandoNomeSoTemEspacos() {
        String toEmail = "usuario@teste.com";
        String userName = "   ";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve remover caracteres especiais perigosos")
    void deveRemoverCaracteresEspeciaisPerigosos() {
        String toEmail = "usuario@teste.com";
        String userName = "João<>\"'&Silva";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve preservar primeiro e último nome quando nome é longo")
    void devePreservarPrimeiroUltimoNomeQuandoNomeLongo() {
        String toEmail = "usuario@teste.com";
        String userName = "João Silva Santos Oliveira Ferreira Costa Lima Pereira";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve truncar nome único muito longo com reticências")
    void deveTruncarNomeUnicoMuitoLongoComReticencias() {
        String toEmail = "usuario@teste.com";
        String userName = "NomeUnicoMuitoMuitoMuitoMuitoLongoParaEmail";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve manter nome normal sem modificações")
    void deveManterNomeNormalSemModificacoes() {
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
    @DisplayName("Deve manter nome no limite de 30 caracteres")
    void deveManterNomeNoLimite30Caracteres() {
        String toEmail = "usuario@teste.com";
        String userName = "João Silva Santos Oliveira"; 
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve testar truncamento em todos os tipos de email")
    void deveTestarTruncamentoEmTodosTiposEmail() {
        
        String email = "usuario@teste.com";
        String nomeLongo = "Este é um nome muito longo que deveria ser truncado para evitar problemas no template HTML";
        String codigo = "123456";
        
        
        emailService.sendPasswordResetCode(email, nomeLongo, codigo);
        emailService.sendTwoFactorRecoveryCode(email, nomeLongo, codigo);
        emailService.sendEmailVerification(email, nomeLongo, codigo);
        emailService.sendPasswordResetConfirmation(email, nomeLongo); 
        
        
        verify(mailSender, times(4)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class)); 
    }

    @Test
    @DisplayName("Deve tratar nome com caracteres especiais variados")
    void deveTratarNomeComCaracteresEspeciaisVariados() {
        String toEmail = "usuario@teste.com";
        String userName = "João<script>alert('xss')</script>Silva&amp;Test\"Quote'Single";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome com apenas caracteres especiais")
    void deveTratarNomeComApenasCaracteresEspeciais() {
        String toEmail = "usuario@teste.com";
        String userName = "<>\"'&";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome com espaços múltiplos")
    void deveTratarNomeComEspacosMultiplos() {
        String toEmail = "usuario@teste.com";
        String userName = "João    Silva    Santos";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome com uma única palavra longa")
    void deveTratarNomeComUnicaPalavraLonga() {
        String toEmail = "usuario@teste.com";
        String userName = "SupercalifragilisticexpialidociousNome";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome com duas palavras longas")
    void deveTratarNomeComDuasPalavrasLongas() {
        String toEmail = "usuario@teste.com";
        String userName = "SupercalifragilisticexpialidociousNome SupercalifragilisticexpialidociousApelido";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve tratar nome com muitas palavras pequenas")
    void deveTratarNomeComMuitasPalavrasPequenas() {
        String toEmail = "usuario@teste.com";
        String userName = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String code = "123456";

        assertDoesNotThrow(() -> {
            emailService.sendPasswordResetCode(toEmail, userName, code);
        });

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
} 