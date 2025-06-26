package inkspiration.backend.entities.profissional;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Profissional;

@DisplayName("Testes de validação de nota - Profissional")
public class ProfissionalNotaTest {

    private Profissional profissional;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
    }

    @Test
    @DisplayName("Deve aceitar nota válida")
    void deveAceitarNotaValida() {
        BigDecimal nota = new BigDecimal("4.5");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota());
    }

    @Test
    @DisplayName("Deve aceitar nota 0")
    void deveAceitarNotaZero() {
        BigDecimal nota = BigDecimal.ZERO;
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota());
    }

    @Test
    @DisplayName("Deve aceitar nota 5")
    void deveAceitarNotaCinco() {
        BigDecimal nota = new BigDecimal("5.0");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota());
    }

    @Test
    @DisplayName("Deve aceitar nota decimal")
    void deveAceitarNotaDecimal() {
        BigDecimal nota = new BigDecimal("3.7");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota());
    }

    @Test
    @DisplayName("Deve aceitar nota nula")
    void deveAceitarNotaNula() {
        profissional.setNota(null);
        assertNull(profissional.getNota());
    }

    @Test
    @DisplayName("Não deve aceitar nota negativa")
    void naoDeveAceitarNotaNegativa() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profissional.setNota(new BigDecimal("-0.1"));
        });
        assertEquals("A nota deve ser maior ou igual a 0", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar nota muito negativa")
    void naoDeveAceitarNotaMuitoNegativa() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profissional.setNota(new BigDecimal("-10.0"));
        });
        assertEquals("A nota deve ser maior ou igual a 0", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar nota maior que 5")
    void naoDeveAceitarNotaMaiorQue5() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profissional.setNota(new BigDecimal("5.1"));
        });
        assertEquals("A nota deve ser menor ou igual a 5", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar nota muito alta")
    void naoDeveAceitarNotaMuitoAlta() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profissional.setNota(new BigDecimal("10.0"));
        });
        assertEquals("A nota deve ser menor ou igual a 5", exception.getMessage());
    }
} 