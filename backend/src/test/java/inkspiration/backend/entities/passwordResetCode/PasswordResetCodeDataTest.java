package inkspiration.backend.entities.passwordResetCode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.PasswordResetCode;

@DisplayName("Testes de validação de datas - PasswordResetCode")
public class PasswordResetCodeDataTest {

    private PasswordResetCode passwordResetCode;

    @BeforeEach
    void setUp() {
        passwordResetCode = new PasswordResetCode();
    }

    @Test
    @DisplayName("Deve definir data de criação válida")
    void deveDefinirDataCriacaoValida() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetCode.setCreatedAt(now);
        assertEquals(now, passwordResetCode.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir data de criação no passado")
    void deveDefinirDataCriacaoNoPassado() {
        LocalDateTime passado = LocalDateTime.now().minusHours(2);
        passwordResetCode.setCreatedAt(passado);
        assertEquals(passado, passwordResetCode.getCreatedAt());
    }

    @Test
    @DisplayName("Deve definir data de criação no futuro")
    void deveDefinirDataCriacaoNoFuturo() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);
        passwordResetCode.setCreatedAt(futuro);
        assertEquals(futuro, passwordResetCode.getCreatedAt());
    }

    @Test
    @DisplayName("Não deve aceitar data de criação nula")
    void naoDeveAceitarDataCriacaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCreatedAt(null);
        });
        assertEquals("A data de criação não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve definir data de expiração válida")
    void deveDefinirDataExpiracaoValida() {
        LocalDateTime future = LocalDateTime.now().plusHours(1);
        passwordResetCode.setExpiresAt(future);
        assertEquals(future, passwordResetCode.getExpiresAt());
    }

    @Test
    @DisplayName("Deve definir data de expiração distante no futuro")
    void deveDefinirDataExpiracaoDistanteNoFuturo() {
        LocalDateTime futuroDistante = LocalDateTime.now().plusDays(30);
        passwordResetCode.setExpiresAt(futuroDistante);
        assertEquals(futuroDistante, passwordResetCode.getExpiresAt());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração nula")
    void naoDeveAceitarDataExpiracaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setExpiresAt(null);
        });
        assertEquals("A data de expiração não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração anterior à criação")
    void naoDeveAceitarDataExpiracaoAnteriorACriacao() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetCode.setCreatedAt(now);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setExpiresAt(now.minusHours(1));
        });
        assertEquals("A data de expiração deve ser posterior à data de criação", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de expiração igual à criação")
    void naoDeveAceitarDataExpiracaoIgualACriacao() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetCode.setCreatedAt(now);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setExpiresAt(now);
        });
        assertEquals("A data de expiração deve ser posterior à data de criação", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar data de expiração posterior à criação por 1 segundo")
    void deveAceitarDataExpiracaoPosteriorPor1Segundo() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetCode.setCreatedAt(now);
        
        LocalDateTime expirationTime = now.plusSeconds(1);
        passwordResetCode.setExpiresAt(expirationTime);
        assertEquals(expirationTime, passwordResetCode.getExpiresAt());
    }

    @Test
    @DisplayName("Deve permitir definir expiração sem data de criação definida")
    void devePermitirDefinirExpiracaoSemDataCriacao() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);
        passwordResetCode.setExpiresAt(futuro);
        assertEquals(futuro, passwordResetCode.getExpiresAt());
    }

    @Test
    @DisplayName("Deve verificar se código expirou")
    void deveVerificarSeCodigoExpirou() {
        passwordResetCode.setExpiresAt(LocalDateTime.now().minusHours(1));
        assertTrue(passwordResetCode.isExpired());
        
        passwordResetCode.setExpiresAt(LocalDateTime.now().plusHours(1));
        assertFalse(passwordResetCode.isExpired());
    }

    @Test
    @DisplayName("Deve verificar se código é válido baseado em datas")
    void deveVerificarSeCodigoEhValidoBaseadoEmDatas() {
        // Código válido (não usado e não expirado)
        passwordResetCode.setExpiresAt(LocalDateTime.now().plusHours(1));
        passwordResetCode.setUsed(false);
        assertTrue(passwordResetCode.isValid());
        
        // Código expirado
        passwordResetCode.setExpiresAt(LocalDateTime.now().minusHours(1));
        passwordResetCode.setUsed(false);
        assertFalse(passwordResetCode.isValid());
    }
} 