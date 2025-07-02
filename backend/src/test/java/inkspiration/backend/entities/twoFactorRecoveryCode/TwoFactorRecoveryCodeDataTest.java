package inkspiration.backend.entities.twoFactorRecoveryCode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;

@DisplayName("Testes de validação de datas - TwoFactorRecoveryCode")
public class TwoFactorRecoveryCodeDataTest {

    private TwoFactorRecoveryCode code;

    @BeforeEach
    void setUp() {
        code = new TwoFactorRecoveryCode();
    }

    @Test
    @DisplayName("Deve definir data de criação válida")
    void deveDefinirDataCriacaoValida() {
        LocalDateTime now = LocalDateTime.now();
        code.setCreatedAt(now);
        assertEquals(now, code.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir data de criação no passado")
    void deveDefinirDataCriacaoNoPassado() {
        LocalDateTime passado = LocalDateTime.now().minusHours(2);
        code.setCreatedAt(passado);
        assertEquals(passado, code.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir data de criação no futuro")
    void deveDefinirDataCriacaoNoFuturo() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);
        code.setCreatedAt(futuro);
        assertEquals(futuro, code.getCreatedAt());
    }

    @Test
    @DisplayName("Não deve aceitar data de criação nula")
    void naoDeveAceitarDataCriacaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setCreatedAt(null);
        });
        assertEquals("A data de criação não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve definir data de expiração válida")
    void deveDefinirDataExpiracaoValida() {
        LocalDateTime future = LocalDateTime.now().plusHours(1);
        code.setExpiresAt(future);
        assertEquals(future, code.getExpiresAt());
    }

    @Test
    @DisplayName("Deve definir data de expiração distante no futuro")
    void deveDefinirDataExpiracaoDistanteNoFuturo() {
        LocalDateTime futuroDistante = LocalDateTime.now().plusDays(30);
        code.setExpiresAt(futuroDistante);
        assertEquals(futuroDistante, code.getExpiresAt());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração nula")
    void naoDeveAceitarDataExpiracaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setExpiresAt(null);
        });
        assertEquals("A data de expiração não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração anterior à criação")
    void naoDeveAceitarDataExpiracaoAnteriorACriacao() {
        LocalDateTime now = LocalDateTime.now();
        code.setCreatedAt(now);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setExpiresAt(now.minusHours(1));
        });
        assertEquals("A data de expiração deve ser posterior à data de criação", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração igual à criação")
    void naoDeveAceitarDataExpiracaoIgualACriacao() {
        LocalDateTime now = LocalDateTime.now();
        code.setCreatedAt(now);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            code.setExpiresAt(now);
        });
        assertEquals("A data de expiração deve ser posterior à data de criação", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar data de expiração posterior à criação por 1 segundo")
    void deveAceitarDataExpiracaoPosteriorPor1Segundo() {
        LocalDateTime now = LocalDateTime.now();
        code.setCreatedAt(now);
        
        LocalDateTime expirationTime = now.plusSeconds(1);
        code.setExpiresAt(expirationTime);
        assertEquals(expirationTime, code.getExpiresAt());
    }

    @Test
    @DisplayName("Deve permitir definir expiração sem data de criação definida")
    void devePermitirDefinirExpiracaoSemDataCriacao() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);
        code.setExpiresAt(futuro);
        assertEquals(futuro, code.getExpiresAt());
    }
} 