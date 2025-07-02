package inkspiration.backend.entities.passwordResetCode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.PasswordResetCode;

@DisplayName("Testes de validação de código - PasswordResetCode")
public class PasswordResetCodeCodigoTest {

    private PasswordResetCode passwordResetCode;

    @BeforeEach
    void setUp() {
        passwordResetCode = new PasswordResetCode();
    }

    @Test
    @DisplayName("Deve aceitar código válido com 6 caracteres")
    void deveAceitarCodigoValidoCom6Caracteres() {
        String codigo = "ABC123";
        passwordResetCode.setCode(codigo);
        assertEquals(codigo, passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve aceitar código válido com 8 caracteres")
    void deveAceitarCodigoValidoCom8Caracteres() {
        String codigo = "ABCD1234";
        passwordResetCode.setCode(codigo);
        assertEquals(codigo, passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve aceitar código válido com 7 caracteres")
    void deveAceitarCodigoValidoCom7Caracteres() {
        String codigo = "ABC1234";
        passwordResetCode.setCode(codigo);
        assertEquals(codigo, passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve aceitar código apenas com números")
    void deveAceitarCodigoApenasComNumeros() {
        String codigo = "123456";
        passwordResetCode.setCode(codigo);
        assertEquals(codigo, passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve aceitar código apenas com letras")
    void deveAceitarCodigoApenasComLetras() {
        String codigo = "ABCDEF";
        passwordResetCode.setCode(codigo);
        assertEquals(codigo, passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve converter código para maiúsculo")
    void deveConverterCodigoParaMaiusculo() {
        passwordResetCode.setCode("abc123");
        assertEquals("ABC123", passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Deve remover espaços do código")
    void deveRemoverEspacosDocodigo() {
        passwordResetCode.setCode(" ABC123 ");
        assertEquals("ABC123", passwordResetCode.getCode());
    }

    @Test
    @DisplayName("Não deve aceitar código nulo")
    void naoDeveAceitarCodigoNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode(null);
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código vazio")
    void naoDeveAceitarCodigoVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("");
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com apenas espaços")
    void naoDeveAceitarCodigoComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("   ");
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com menos de 6 caracteres")
    void naoDeveAceitarCodigoComMenosDe6Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("ABC12");
        });
        assertEquals("O código deve ter entre 6 e 8 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com mais de 8 caracteres")
    void naoDeveAceitarCodigoComMaisDe8Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("ABCD12345");
        });
        assertEquals("O código deve ter entre 6 e 8 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com caracteres especiais")
    void naoDeveAceitarCodigoComCaracteresEspeciais() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("ABC@#!");
        });
        assertEquals("O código deve conter apenas letras maiúsculas e números", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com espaços no meio")
    void naoDeveAceitarCodigoComEspacosNoMeio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("AB C123");
        });
        assertEquals("O código deve conter apenas letras maiúsculas e números", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com símbolos")
    void naoDeveAceitarCodigoComSimbolos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCode("ABC-123");
        });
        assertEquals("O código deve conter apenas letras maiúsculas e números", exception.getMessage());
    }
} 