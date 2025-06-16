package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.EmailValidator;

public class ValidaEmailTest {

    @Test
    void testEmailValido() {
        assertTrue(EmailValidator.isValid("usuario@exemplo.com"), "Email válido deve ser aceito");
        assertTrue(EmailValidator.isValid("test@test.org"), "Email válido deve ser aceito");
        assertTrue(EmailValidator.isValid("user.name@domain.com"), "Email com ponto no nome deve ser válido");
        assertTrue(EmailValidator.isValid("user+tag@domain.com"), "Email com + deve ser válido");
        assertTrue(EmailValidator.isValid("user_name@domain.co.uk"), "Email com _ deve ser válido");
        assertTrue(EmailValidator.isValid("123@domain.com"), "Email com números deve ser válido");
    }

    @Test
    void testEmailInvalido() {
        assertFalse(EmailValidator.isValid("email_sem_arroba.com"), "Email sem @ deve ser inválido");
        assertFalse(EmailValidator.isValid("@domain.com"), "Email sem nome de usuário deve ser inválido");
        assertFalse(EmailValidator.isValid("usuario@"), "Email sem domínio deve ser inválido");
        assertFalse(EmailValidator.isValid("usuario@.com"), "Email com domínio iniciando com ponto deve ser inválido");
        assertFalse(EmailValidator.isValid("usuario..duplo@domain.com"), "Email com pontos duplos deve ser inválido");
        assertFalse(EmailValidator.isValid("usuario@domain"), "Email sem TLD deve ser inválido");
    }

    @Test
    void testEmailVazio() {
        assertFalse(EmailValidator.isValid(""), "Email vazio deve ser inválido");
        assertFalse(EmailValidator.isValid("   "), "Email apenas com espaços deve ser inválido");
        assertFalse(EmailValidator.isValid(null), "Email nulo deve ser inválido");
    }

    @Test
    void testEmailComEspacos() {
        assertFalse(EmailValidator.isValid(" usuario@domain.com "), "Email com espaços deve ser inválido");
        assertFalse(EmailValidator.isValid("user io@domain.com"), "Email com espaço no meio deve ser inválido");
    }

    @Test
    void testEmailsLimite() {
        // Email muito longo
        String emailLongo = "a".repeat(100) + "@domain.com";
        assertTrue(EmailValidator.isValid(emailLongo), "Email longo mas válido deve ser aceito");
        
        // Email com domínio longo
        String dominioLongo = "usuario@" + "a".repeat(50) + ".com";
        assertTrue(EmailValidator.isValid(dominioLongo), "Email com domínio longo deve ser aceito");
    }

    @Test
    void testEmailsComCaracteresEspeciais() {
        assertTrue(EmailValidator.isValid("user-name@domain.com"), "Email com hífen deve ser válido");
        assertTrue(EmailValidator.isValid("user.name+tag@domain.com"), "Email com ponto e + deve ser válido");
        assertFalse(EmailValidator.isValid("user#name@domain.com"), "Email com # deve ser inválido");
        assertFalse(EmailValidator.isValid("user*name@domain.com"), "Email com * deve ser inválido");
    }
} 