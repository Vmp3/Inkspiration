package inkspiration.backend.entities.twoFactorRecoveryCode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;

@DisplayName("Testes de validação de userId - TwoFactorRecoveryCode")
public class TwoFactorRecoveryCodeUserIdTest {

    private TwoFactorRecoveryCode code;

    @BeforeEach
    void setUp() {
        code = new TwoFactorRecoveryCode();
    }

    @Test
    @DisplayName("Deve definir userId válido")
    void deveDefinirUserIdValido() {
        code.setUserId(123L);
        assertEquals(123L, code.getUserId());
    }

    @Test
    @DisplayName("Deve aceitar userId com valor 1")
    void deveAceitarUserIdCom1() {
        code.setUserId(1L);
        assertEquals(1L, code.getUserId());
    }

    @Test
    @DisplayName("Deve aceitar userId grande")
    void deveAceitarUserIdGrande() {
        Long userIdGrande = 999999999L;
        code.setUserId(userIdGrande);
        assertEquals(userIdGrande, code.getUserId());
    }

    @Test
    @DisplayName("Não deve aceitar userId nulo")
    void naoDeveAceitarUserIdNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setUserId(null);
        });
        assertEquals("O ID do usuário não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar userId zero")
    void naoDeveAceitarUserIdZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setUserId(0L);
        });
        assertEquals("O ID do usuário deve ser positivo", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar userId negativo")
    void naoDeveAceitarUserIdNegativo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setUserId(-1L);
        });
        assertEquals("O ID do usuário deve ser positivo", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar userId muito negativo")
    void naoDeveAceitarUserIdMuitoNegativo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setUserId(-999L);
        });
        assertEquals("O ID do usuário deve ser positivo", exception.getMessage());
    }
} 