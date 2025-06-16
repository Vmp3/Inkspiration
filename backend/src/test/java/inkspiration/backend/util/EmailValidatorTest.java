package inkspiration.backend.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.EmailValidator;

public class EmailValidatorTest {

    @Test
    void testEmailsValidos() {
        String[] emailsValidos = {
            "usuario@exemplo.com",
            "teste123@teste.com.br",
            "email.valido@site.org",
            "user+tag@domain.net",
            "admin@subdomain.example.co.uk",
            "123@numbers.com",
            "test_email@test-domain.com"
        };

        for (String email : emailsValidos) {
            assertTrue(EmailValidator.isValid(email), "Email válido deveria ser aceito: " + email);
        }
    }

    @Test
    void testEmailsInvalidos() {
        String[] emailsInvalidos = {
            null,
            "",
            "   ",
            "@domain.com",
            "usuario@",
            "usuario@.com",
            "usuario..duplo@domain.com",
            "usuario@domain",
            "usuario@domain.",
            "usuario.@domain.com",
            ".usuario@domain.com",
            "usuario@",
            "usuario",
            "usuario@domain..com",
            "usuario@.domain.com",
            "usuario@domain.com.",
            "usuario@domain.c",
            "email.com.@domain"
        };

        for (String email : emailsInvalidos) {
            assertFalse(EmailValidator.isValid(email), "Email inválido deveria ser rejeitado: " + email);
        }
    }

    @Test
    void testEmailNulo() {
        assertFalse(EmailValidator.isValid(null), "Email nulo deve ser considerado inválido");
    }

    @Test
    void testEmailVazio() {
        assertFalse(EmailValidator.isValid(""), "Email vazio deve ser considerado inválido");
        assertFalse(EmailValidator.isValid("   "), "Email apenas com espaços deve ser considerado inválido");
    }

    @Test
    void testEmailComCaracteresEspeciais() {
        // Testa emails com caracteres especiais válidos
        String[] emailsEspeciais = {
            "user+tag@example.com",
            "user_name@example.com",
            "user-name@example.com",
            "123user@example.com",
            "user123@example.com"
        };

        for (String email : emailsEspeciais) {
            assertTrue(EmailValidator.isValid(email), "Email com caracteres especiais válidos deveria ser aceito: " + email);
        }
    }

    @Test
    void testEmailComCaracteresInvalidos() {
        String[] emailsInvalidos = {
            "user@exam ple.com", // espaço
            "user@example..com", // ponto duplo
            "user@.example.com", // começa com ponto
            "user@example.com.", // termina com ponto
            "user..name@example.com", // ponto duplo no nome
            ".user@example.com", // começa com ponto no nome
            "user.@example.com" // termina com ponto no nome
        };

        for (String email : emailsInvalidos) {
            assertFalse(EmailValidator.isValid(email), "Email com caracteres inválidos deveria ser rejeitado: " + email);
        }
    }

    @Test
    void testDominiosVariados() {
        String[] dominiosValidos = {
            "usuario@example.com",
            "usuario@sub.example.com",
            "usuario@example.org",
            "usuario@example.net",
            "usuario@example.edu",
            "usuario@example.gov",
            "usuario@example.mil",
            "usuario@example.br",
            "usuario@example.co.uk"
        };

        for (String email : dominiosValidos) {
            assertTrue(EmailValidator.isValid(email), "Email com domínio válido deveria ser aceito: " + email);
        }
    }

    @Test
    void testTLDsValidos() {
        String[] tldsValidos = {
            "usuario@example.com",
            "usuario@example.org",
            "usuario@example.net",
            "usuario@example.edu",
            "usuario@example.br",
            "usuario@example.io"
        };

        for (String email : tldsValidos) {
            assertTrue(EmailValidator.isValid(email), "Email com TLD válido deveria ser aceito: " + email);
        }
    }

    @Test
    void testEmailsLimite() {
        // Email muito longo (se houver limite)
        String emailLongo = "a".repeat(100) + "@" + "b".repeat(100) + ".com";
        // O comportamento pode variar dependendo da implementação
        assertDoesNotThrow(() -> EmailValidator.isValid(emailLongo), 
                          "Validação de email longo não deve lançar exceção");
    }

    @Test
    void testCaseSensitive() {
        // Emails deveriam ser case-insensitive no domínio
        String[] emailsCase = {
            "usuario@EXAMPLE.COM",
            "USUARIO@example.com",
            "Usuario@Example.Com"
        };

        for (String email : emailsCase) {
            assertTrue(EmailValidator.isValid(email), "Email deveria ser válido independente do case: " + email);
        }
    }

    @Test
    void testEmailsBrasileiros() {
        String[] emailsBrasileiros = {
            "usuario@exemplo.com.br",
            "teste@uol.com.br",
            "admin@gov.br",
            "contato@empresa.org.br",
            "suporte@site.net.br"
        };

        for (String email : emailsBrasileiros) {
            assertTrue(EmailValidator.isValid(email), "Email brasileiro deveria ser aceito: " + email);
        }
    }

    @Test
    void testEmailsInternacionais() {
        String[] emailsInternacionais = {
            "user@example.co.uk",
            "admin@site.fr",
            "test@domain.de",
            "contact@company.ca",
            "info@business.au"
        };

        for (String email : emailsInternacionais) {
            assertTrue(EmailValidator.isValid(email), "Email internacional deveria ser aceito: " + email);
        }
    }
} 