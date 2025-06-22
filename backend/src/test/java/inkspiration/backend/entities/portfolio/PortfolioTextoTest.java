package inkspiration.backend.entities.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Portfolio;

@DisplayName("Testes de validação de campos de texto - Portfolio")
public class PortfolioTextoTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
    }

    @Test
    @DisplayName("Deve aceitar descrição válida")
    void deveAceitarDescricaoValida() {
        String descricao = "Descrição do portfolio";
        portfolio.setDescricao(descricao);
        assertEquals(descricao, portfolio.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição com 2000 caracteres")
    void deveAceitarDescricaoCom2000Caracteres() {
        String descricao = "A".repeat(2000);
        portfolio.setDescricao(descricao);
        assertEquals(descricao, portfolio.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição nula")
    void deveAceitarDescricaoNula() {
        portfolio.setDescricao(null);
        assertNull(portfolio.getDescricao());
    }

    @Test
    @DisplayName("Não deve aceitar descrição muito longa")
    void naoDeveAceitarDescricaoMuitoLonga() {
        String descricao = "A".repeat(2001);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setDescricao(descricao);
        });
        assertEquals("A descrição não pode exceder 2000 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar experiência válida")
    void deveAceitarExperienciaValida() {
        String experiencia = "10 anos de experiência";
        portfolio.setExperiencia(experiencia);
        assertEquals(experiencia, portfolio.getExperiencia());
    }

    @Test
    @DisplayName("Deve aceitar experiência com 1000 caracteres")
    void deveAceitarExperienciaCom1000Caracteres() {
        String experiencia = "B".repeat(1000);
        portfolio.setExperiencia(experiencia);
        assertEquals(experiencia, portfolio.getExperiencia());
    }

    @Test
    @DisplayName("Deve aceitar experiência nula")
    void deveAceitarExperienciaNula() {
        portfolio.setExperiencia(null);
        assertNull(portfolio.getExperiencia());
    }

    @Test
    @DisplayName("Não deve aceitar experiência muito longa")
    void naoDeveAceitarExperienciaMuitoLonga() {
        String experiencia = "B".repeat(1001);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setExperiencia(experiencia);
        });
        assertEquals("A experiência não pode exceder 1000 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar especialidade válida")
    void deveAceitarEspecialidadeValida() {
        String especialidade = "Tatuagens realistas";
        portfolio.setEspecialidade(especialidade);
        assertEquals(especialidade, portfolio.getEspecialidade());
    }

    @Test
    @DisplayName("Deve aceitar especialidade com 500 caracteres")
    void deveAceitarEspecialidadeCom500Caracteres() {
        String especialidade = "C".repeat(500);
        portfolio.setEspecialidade(especialidade);
        assertEquals(especialidade, portfolio.getEspecialidade());
    }

    @Test
    @DisplayName("Deve aceitar especialidade nula")
    void deveAceitarEspecialidadeNula() {
        portfolio.setEspecialidade(null);
        assertNull(portfolio.getEspecialidade());
    }

    @Test
    @DisplayName("Não deve aceitar especialidade muito longa")
    void naoDeveAceitarEspecialidadeMuitoLonga() {
        String especialidade = "C".repeat(501);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setEspecialidade(especialidade);
        });
        assertEquals("A especialidade não pode exceder 500 caracteres", exception.getMessage());
    }
} 