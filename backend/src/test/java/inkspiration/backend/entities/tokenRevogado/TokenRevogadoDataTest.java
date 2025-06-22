package inkspiration.backend.entities.tokenRevogado;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TokenRevogado;

@DisplayName("Testes de validação de data - TokenRevogado")
public class TokenRevogadoDataTest {

    private TokenRevogado tokenRevogado;

    @BeforeEach
    void setUp() {
        tokenRevogado = new TokenRevogado();
    }

    @Test
    @DisplayName("Deve definir data de revogação válida")
    void deveDefinirDataRevogacaoValida() {
        LocalDateTime now = LocalDateTime.now();
        tokenRevogado.setDataRevogacao(now);
        assertEquals(now, tokenRevogado.getDataRevogacao());
    }

    @Test
    @DisplayName("Não deve aceitar data de revogação nula")
    void naoDeveAceitarDataRevogacaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setDataRevogacao(null);
        });
        assertEquals("A data de revogação não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar se token expirou - nunca expira com horas <= 0")
    void deveVerificarExpiracaoComHorasInvalidas() {
        tokenRevogado.setDataRevogacao(LocalDateTime.now().minusDays(1));
        
        assertFalse(tokenRevogado.isExpired(0));
        assertFalse(tokenRevogado.isExpired(-1));
    }

    @Test
    @DisplayName("Deve verificar se token não expirou")
    void deveVerificarTokenNaoExpirado() {
        tokenRevogado.setDataRevogacao(LocalDateTime.now().minusHours(1));
        
        assertFalse(tokenRevogado.isExpired(2)); // 2 horas, revogado há 1 hora
    }

    @Test
    @DisplayName("Deve verificar se token expirou")
    void deveVerificarTokenExpirado() {
        tokenRevogado.setDataRevogacao(LocalDateTime.now().minusHours(3));
        
        assertTrue(tokenRevogado.isExpired(2)); // 2 horas, revogado há 3 horas
    }

    @Test
    @DisplayName("Deve verificar expiração no limite")
    void deveVerificarExpiracaoNoLimite() {
        tokenRevogado.setDataRevogacao(LocalDateTime.now().minusHours(2).minusMinutes(1));
        
        assertTrue(tokenRevogado.isExpired(2)); // Passou 1 minuto do limite
    }
} 