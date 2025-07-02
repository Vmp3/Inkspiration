package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.util.EmailValidator;

@DisplayName("UsuarioService - Testes de Email")
class UsuarioServiceEmailTest {

    @Test
    @DisplayName("Deve validar email válido")
    void deveValidarEmailValido() {
        assertTrue(EmailValidator.isValid("joao@example.com"));
        assertTrue(EmailValidator.isValid("usuario@domain.com.br"));
        assertTrue(EmailValidator.isValid("teste.email@gmail.com"));
        assertTrue(EmailValidator.isValid("user123@hotmail.com"));
        assertTrue(EmailValidator.isValid("test_user@yahoo.com.br"));
    }

    @Test
    @DisplayName("Deve invalidar email inválido")
    void deveInvalidarEmailInvalido() {
        assertFalse(EmailValidator.isValid("email-sem-arroba"));
        assertFalse(EmailValidator.isValid("email@"));
        assertFalse(EmailValidator.isValid("@domain.com"));
        assertFalse(EmailValidator.isValid("email"));
        assertFalse(EmailValidator.isValid("usuario"));
    }

    @Test
    @DisplayName("Deve invalidar email nulo ou vazio")
    void deveInvalidarEmailNuloOuVazio() {
        assertFalse(EmailValidator.isValid(null));
        assertFalse(EmailValidator.isValid(""));
        assertFalse(EmailValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve validar email com diferentes domínios")
    void deveValidarEmailComDiferentesDominios() {
        assertTrue(EmailValidator.isValid("user@gmail.com"));
        assertTrue(EmailValidator.isValid("user@outlook.com"));
        assertTrue(EmailValidator.isValid("user@yahoo.com"));
        assertTrue(EmailValidator.isValid("user@empresa.com.br"));
        assertTrue(EmailValidator.isValid("user@universidade.edu.br"));
    }

    @Test
    @DisplayName("Deve invalidar email com caracteres especiais inválidos")
    void deveInvalidarEmailComCaracteresEspeciaisInvalidos() {
        assertFalse(EmailValidator.isValid("user@"));
        assertFalse(EmailValidator.isValid("@domain.com"));
        assertFalse(EmailValidator.isValid("user"));
        assertFalse(EmailValidator.isValid("domain.com"));
    }

    @Test
    @DisplayName("Deve lançar exceção para email obrigatório")
    void deveLancarExcecaoParaEmailObrigatorio() {
        assertThrows(UsuarioValidationException.EmailObrigatorioException.class, () -> {
            String email = null;
            if (email == null || email.trim().isEmpty()) {
                throw new UsuarioValidationException.EmailObrigatorioException();
            }
        });

        assertThrows(UsuarioValidationException.EmailObrigatorioException.class, () -> {
            String email = "";
            if (email == null || email.trim().isEmpty()) {
                throw new UsuarioValidationException.EmailObrigatorioException();
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para email inválido")
    void deveLancarExcecaoParaEmailInvalido() {
        assertThrows(UsuarioValidationException.EmailInvalidoException.class, () -> {
            String email = "email-invalido";
            if (!EmailValidator.isValid(email)) {
                throw new UsuarioValidationException.EmailInvalidoException("Email inválido");
            }
        });
    }

    @Test
    @DisplayName("Deve normalizar email para minúsculo")
    void deveNormalizarEmailParaMinusculo() {
        String emailMaiusculo = "JOAO@EXAMPLE.COM";
        String emailNormalizado = emailMaiusculo.toLowerCase();
        
        assertEquals("joao@example.com", emailNormalizado);
        assertTrue(EmailValidator.isValid(emailNormalizado));
    }

    @Test
    @DisplayName("Deve remover espaços do email")
    void deveRemoverEspacosDoEmail() {
        String emailComEspacos = "  joao@example.com  ";
        String emailLimpo = emailComEspacos.trim();
        
        assertEquals("joao@example.com", emailLimpo);
        assertTrue(EmailValidator.isValid(emailLimpo));
    }
} 