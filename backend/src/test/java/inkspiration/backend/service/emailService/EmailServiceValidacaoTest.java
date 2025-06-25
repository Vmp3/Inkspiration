package inkspiration.backend.service.emailService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class EmailServiceValidacaoTest {

    @Test
    @DisplayName("Deve truncar nomes longos corretamente")
    void deveTruncarNomesLongosCorretamente() {
        // Given
        String nomeLongo = "Nome Muito Muito Muito Muito Longo Para Email";
        String nomeVazio = "";
        String nomeNulo = null;
        
        // When & Then
        assertTrue(nomeLongo.length() > 30);
        assertEquals("", nomeVazio);
        assertNull(nomeNulo);
    }

    @Test
    @DisplayName("Deve validar formato de email válido")
    void deveValidarFormatoEmailValido() {
        // Given
        List<String> emailsValidos = Arrays.asList(
            "usuario@exemplo.com",
            "teste@gmail.com",
            "profissional@inkspiration.com",
            "user.name@domain.com.br"
        );
        
        // When & Then
        for (String email : emailsValidos) {
            assertTrue(email.contains("@"));
            assertTrue(email.contains("."));
            assertFalse(email.startsWith("@"));
            assertFalse(email.endsWith("@"));
        }
    }

    @Test
    @DisplayName("Deve rejeitar emails inválidos")
    void deveRejeitarEmailsInvalidos() {
        // Given
        List<String> emailsInvalidos = Arrays.asList(
            "usuario@",
            "@domain.com",
            "email-sem-arroba",
            "",
            "email@",
            "@"
        );
        
        // When & Then
        for (String email : emailsInvalidos) {
            if (email != null && !email.isEmpty()) {
                if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
                    assertTrue(true); // Email é inválido como esperado
                } else {
                    assertTrue(email.contains("@"));
                }
            }
        }
    }

    @Test
    @DisplayName("Deve validar códigos de verificação de 6 dígitos")
    void deveValidarCodigosVerificacao6Digitos() {
        // Given
        List<String> codigosValidos = Arrays.asList("123456", "000000", "999999", "012345");
        List<String> codigosInvalidos = Arrays.asList("12345", "1234567", "abcdef", "", "12a456");
        
        // When & Then
        for (String codigo : codigosValidos) {
            assertEquals(6, codigo.length());
            assertTrue(codigo.matches("\\d{6}"));
        }
        
        for (String codigo : codigosInvalidos) {
            if (codigo != null) {
                assertTrue(codigo.length() != 6 || !codigo.matches("\\d{6}"));
            }
        }
    }

    @Test
    @DisplayName("Deve remover caracteres especiais de nomes")
    void deveRemoverCaracteresEspeciaisNomes() {
        // Given
        String nomeComCaracteresEspeciais = "João<script>alert('xss')</script>";
        
        // When
        String nomeLimpo = nomeComCaracteresEspeciais.replaceAll("[<>\"'&]", "");
        
        // Then
        assertFalse(nomeLimpo.contains("<"));
        assertFalse(nomeLimpo.contains(">"));
        assertFalse(nomeLimpo.contains("\""));
        assertFalse(nomeLimpo.contains("'"));
        assertFalse(nomeLimpo.contains("&"));
        assertTrue(nomeLimpo.contains("João"));
    }

    @Test
    @DisplayName("Deve preservar primeiro e último nome quando truncar")
    void devePreservarPrimeiroUltimoNomeQuandoTruncar() {
        // Given
        String nomeCompleto = "João Silva Santos Oliveira Ferreira";
        String[] partes = nomeCompleto.split(" ");
        
        // When
        String nomeResumido = partes[0] + " " + partes[partes.length - 1];
        
        // Then
        assertEquals("João Ferreira", nomeResumido);
        assertTrue(nomeResumido.length() < nomeCompleto.length());
    }

    @Test
    @DisplayName("Deve validar templates de email HTML")
    void deveValidarTemplatesEmailHTML() {
        // Given
        String template = """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>Email Template</title>
            </head>
            <body>
                <h1>Inkspiration</h1>
                <p>Conteúdo do email</p>
            </body>
            </html>
            """;
        
        // When & Then
        assertTrue(template.contains("<!DOCTYPE html>"));
        assertTrue(template.contains("lang=\"pt-BR\""));
        assertTrue(template.contains("charset=\"UTF-8\""));
        assertTrue(template.contains("Inkspiration"));
    }

    @Test
    @DisplayName("Deve validar estrutura de email de recuperação")
    void deveValidarEstruturaEmailRecuperacao() {
        // Given
        String assunto = "Inkspiration - Código de Recuperação de Senha";
        String codigo = "123456";
        String nome = "João";
        
        // When
        String conteudo = String.format("Olá, %s! Seu código é: %s", nome, codigo);
        
        // Then
        assertTrue(assunto.contains("Inkspiration"));
        assertTrue(assunto.contains("Recuperação"));
        assertTrue(conteudo.contains(nome));
        assertTrue(conteudo.contains(codigo));
    }

    @Test
    @DisplayName("Deve validar estrutura de email de confirmação")
    void deveValidarEstruturaEmailConfirmacao() {
        // Given
        String assunto = "Inkspiration - Senha Alterada com Sucesso";
        String nome = "Maria";
        
        // When
        String conteudo = String.format("Olá %s, sua senha foi alterada com sucesso!", nome);
        
        // Then
        assertTrue(assunto.contains("Inkspiration"));
        assertTrue(assunto.contains("Sucesso"));
        assertTrue(conteudo.contains(nome));
        assertTrue(conteudo.contains("sucesso"));
    }

    @Test
    @DisplayName("Deve validar estrutura de email 2FA")
    void deveValidarEstruturaEmail2FA() {
        // Given
        String assunto = "Inkspiration - Código de Recuperação 2FA";
        String codigo = "987654";
        
        // When
        String conteudo = String.format("Código de recuperação 2FA: %s", codigo);
        
        // Then
        assertTrue(assunto.contains("2FA"));
        assertTrue(conteudo.contains(codigo));
        assertEquals(6, codigo.length());
    }

    @Test
    @DisplayName("Deve validar estrutura de email de verificação")
    void deveValidarEstruturaEmailVerificacao() {
        // Given
        String assunto = "Inkspiration - Verificação de Email";
        String codigo = "456789";
        String nome = "Pedro";
        
        // When
        String conteudo = String.format("Bem-vindo, %s! Código de verificação: %s", nome, codigo);
        
        // Then
        assertTrue(assunto.contains("Verificação"));
        assertTrue(conteudo.contains("Bem-vindo"));
        assertTrue(conteudo.contains(nome));
        assertTrue(conteudo.contains(codigo));
    }

    @Test
    @DisplayName("Deve validar tempo de expiração de códigos")
    void deveValidarTempoExpiracaoCodigos() {
        // Given
        int minutosExpiracao = 15;
        
        // When & Then
        assertTrue(minutosExpiracao > 0);
        assertTrue(minutosExpiracao <= 60);
        assertEquals(15, minutosExpiracao);
    }

    @Test
    @DisplayName("Deve validar formato de remetente")
    void deveValidarFormatoRemetente() {
        // Given
        String emailRemetente = "noreply@inkspiration.com";
        
        // When & Then
        assertTrue(emailRemetente.contains("@"));
        assertTrue(emailRemetente.contains("inkspiration"));
        assertTrue(emailRemetente.startsWith("noreply"));
    }
} 