package inkspiration.backend.entities.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Disponibilidade;

@DisplayName("Testes de validação de horários - Disponibilidade")
public class DisponibilidadeHorariosTest {

    private Disponibilidade disponibilidade;

    @BeforeEach
    void setUp() {
        disponibilidade = new Disponibilidade();
    }

    @Test
    @DisplayName("Deve aceitar horários válidos simples")
    void deveAceitarHorariosValidosSimples() {
        String horarios = "08:00-18:00";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários válidos complexos")
    void deveAceitarHorariosValidosComplexos() {
        String horarios = "Segunda: 08:00-12:00, 14:00-18:00\nTerça: 08:00-17:00\nQuarta: 09:00-18:00";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários com caracteres especiais")
    void deveAceitarHorariosComCaracteresEspeciais() {
        String horarios = "Seg-Sex: 8h às 18h | Sáb: 8h às 12h";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários em JSON")
    void deveAceitarHorariosEmJson() {
        String horarios = "{\"segunda\": \"08:00-18:00\", \"terca\": \"08:00-17:00\"}";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários com até 5000 caracteres")
    void deveAceitarHorariosComAte5000Caracteres() {
        String horarios = "H".repeat(5000);
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas dos horários")
    void deveRemoverEspacosDosBordaoDosHorarios() {
        disponibilidade.setHrAtendimento("  08:00-18:00  ");
        assertEquals("08:00-18:00", disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários nulos")
    void deveAceitarHorariosNulos() {
        disponibilidade.setHrAtendimento(null);
        assertNull(disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários vazios e converter para null")
    void deveAceitarHorariosVaziosEConverterParaNull() {
        disponibilidade.setHrAtendimento("");
        assertNull(disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários com apenas espaços e converter para null")
    void deveAceitarHorariosComApenasEspacosEConverterParaNull() {
        disponibilidade.setHrAtendimento("   ");
        assertNull(disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Não deve aceitar horários com mais de 5000 caracteres")
    void naoDeveAceitarHorariosComMaisDe5000Caracteres() {
        String horarios = "H".repeat(5001);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            disponibilidade.setHrAtendimento(horarios);
        });
        assertEquals("Horários de atendimento não podem exceder 5000 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar horários com quebras de linha")
    void deveAceitarHorariosComQuebrasLinha() {
        String horarios = "Segunda-feira:\n08:00 - 12:00\n14:00 - 18:00\n\nTerça-feira:\n08:00 - 17:00";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários com emojis")
    void deveAceitarHorariosComEmojis() {
        String horarios = "🕘 Segunda: 08:00-18:00 ✅\n🕘 Terça: 08:00-17:00 ✅";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar horários com acentos")
    void deveAceitarHorariosComAcentos() {
        String horarios = "Segunda à Sexta: 8h às 18h\nSábado: 8h às 12h";
        disponibilidade.setHrAtendimento(horarios);
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }
} 