package inkspiration.backend.entities.twoFactorRecoveryCode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;

@DisplayName("Testes de validação de código - TwoFactorRecoveryCode")
public class TwoFactorRecoveryCodeCodigoTest {

    private TwoFactorRecoveryCode code;

    @BeforeEach
    void setUp() {
        code = new TwoFactorRecoveryCode();
    }

    @Test
    @DisplayName("Deve definir código válido com letras maiúsculas")
    void deveDefinirCodigoValidoComLetrasMaiusculas() {
        code.setCode("ABC123");
        assertEquals("ABC123", code.getCode());
    }

    @Test
    @DisplayName("Deve definir código válido apenas com números")
    void deveDefinirCodigoValidoApenasComNumeros() {
        code.setCode("123456");
        assertEquals("123456", code.getCode());
    }

    @Test
    @DisplayName("Deve definir código válido apenas com letras")
    void deveDefinirCodigoValidoApenasComLetras() {
        code.setCode("ABCDEF");
        assertEquals("ABCDEF", code.getCode());
    }

    @Test
    @DisplayName("Deve converter código para maiúsculo")
    void deveConverterCodigoParaMaiusculo() {
        code.setCode("abc123");
        assertEquals("ABC123", code.getCode());
    }

    @Test
    @DisplayName("Deve converter código misto para maiúsculo")
    void deveConverterCodigoMistoParaMaiusculo() {
        code.setCode("aBc123");
        assertEquals("ABC123", code.getCode());
    }

    @Test
    @DisplayName("Deve remover espaços do código")
    void deveRemoverEspacosDocodigo() {
        code.setCode(" ABC123 ");
        assertEquals("ABC123", code.getCode());
    }

    @Test
    @DisplayName("Deve remover espaços do início e fim")
    void deveRemoverEspacosDoInicioEFim() {
        code.setCode("   xyz789   ");
        assertEquals("XYZ789", code.getCode());
    }

    @Test
    @DisplayName("Não deve aceitar código nulo")
    void naoDeveAceitarCodigoNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode(null);
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código vazio")
    void naoDeveAceitarCodigoVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("");
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com apenas espaços")
    void naoDeveAceitarCodigoComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("   ");
        });
        assertEquals("O código não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com menos de 6 caracteres")
    void naoDeveAceitarCodigoComMenosDe6Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("ABC12");
        });
        assertEquals("O código deve ter exatamente 6 caracteres alfanuméricos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com mais de 6 caracteres")
    void naoDeveAceitarCodigoComMaisDe6Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("ABC1234");
        });
        assertEquals("O código deve ter exatamente 6 caracteres alfanuméricos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com caracteres especiais")
    void naoDeveAceitarCodigoComCaracteresEspeciais() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("ABC@#!");
        });
        assertEquals("O código deve ter exatamente 6 caracteres alfanuméricos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com espaços no meio")
    void naoDeveAceitarCodigoComEspacosNoMeio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("AB C123");
        });
        assertEquals("O código deve ter exatamente 6 caracteres alfanuméricos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar código com símbolos")
    void naoDeveAceitarCodigoComSimbolos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCode("ABC-123");
        });
        assertEquals("O código deve ter exatamente 6 caracteres alfanuméricos", exception.getMessage());
    }
} 