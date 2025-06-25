package inkspiration.backend.entities.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;

@DisplayName("Testes gerais da entidade Disponibilidade")
public class DisponibilidadeEntityTest {

    @Test
    @DisplayName("Deve criar disponibilidade com construtor padrão")
    void deveCriarDisponibilidadeComConstrutorPadrao() {
        Disponibilidade disponibilidade = new Disponibilidade();
        
        assertNotNull(disponibilidade);
        assertNull(disponibilidade.getIdDisponibilidade());
        assertNull(disponibilidade.getHrAtendimento());
        assertNull(disponibilidade.getProfissional());
    }

    @Test
    @DisplayName("Deve definir e obter ID da disponibilidade")
    void deveDefinirEObterIdDaDisponibilidade() {
        Disponibilidade disponibilidade = new Disponibilidade();
        Long id = 456L;
        
        disponibilidade.setIdDisponibilidade(id);
        assertEquals(id, disponibilidade.getIdDisponibilidade());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        Disponibilidade disponibilidade = new Disponibilidade();
        
        disponibilidade.setIdDisponibilidade(null);
        assertNull(disponibilidade.getIdDisponibilidade());
    }

    @Test
    @DisplayName("Deve criar disponibilidade completa válida")
    void deveCriarDisponibilidadeCompletaValida() {
        Disponibilidade disponibilidade = new Disponibilidade();
        Profissional profissional = new Profissional();
        String horarios = "Segunda a Sexta: 08:00-18:00";
        
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(horarios);
        
        assertEquals(profissional, disponibilidade.getProfissional());
        assertEquals(horarios, disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve criar disponibilidade mínima válida sem horários")
    void deveCriarDisponibilidadeMinimaValidaSemHorarios() {
        Disponibilidade disponibilidade = new Disponibilidade();
        Profissional profissional = new Profissional();
        
        disponibilidade.setProfissional(profissional);
        
        assertEquals(profissional, disponibilidade.getProfissional());
        assertNull(disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Profissional é obrigatório")
    void profissionalEhObrigatorio() {
        Disponibilidade disponibilidade = new Disponibilidade();
        
        // Deve falhar ao tentar definir profissional nulo
        assertThrows(IllegalArgumentException.class, () -> {
            disponibilidade.setProfissional(null);
        });
    }

    @Test
    @DisplayName("Horários são opcionais")
    void horariossSaoOpcionais() {
        Disponibilidade disponibilidade = new Disponibilidade();
        
        // Deve aceitar horários nulos
        disponibilidade.setHrAtendimento(null);
        assertNull(disponibilidade.getHrAtendimento());
        
        // Deve aceitar horários vazios (convertidos para null)
        disponibilidade.setHrAtendimento("");
        assertNull(disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve validar limites de caracteres corretamente")
    void deveValidarLimitesDeCaracteresCorretamente() {
        Disponibilidade disponibilidade = new Disponibilidade();
        
        // Limite válido - exatamente 5000 caracteres
        String horariosValidos = "H".repeat(5000);
        disponibilidade.setHrAtendimento(horariosValidos);
        assertEquals(horariosValidos, disponibilidade.getHrAtendimento());
        
        // Limite inválido - mais de 5000 caracteres
        String horariosInvalidos = "H".repeat(5001);
        assertThrows(IllegalArgumentException.class, () -> {
            disponibilidade.setHrAtendimento(horariosInvalidos);
        });
    }

    @Test
    @DisplayName("Deve manter integridade entre campos")
    void deveManterIntegridadeEntreCampos() {
        Disponibilidade disponibilidade = new Disponibilidade();
        Profissional profissional = new Profissional();
        String horarios = "08:00-18:00";
        
        // Define ambos os campos
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(horarios);
        
        // Verifica se ambos mantêm seus valores
        assertEquals(profissional, disponibilidade.getProfissional());
        assertEquals(horarios, disponibilidade.getHrAtendimento());
        
        // Altera horários e verifica se profissional se mantém
        disponibilidade.setHrAtendimento("09:00-17:00");
        assertEquals(profissional, disponibilidade.getProfissional());
        assertEquals("09:00-17:00", disponibilidade.getHrAtendimento());
    }
} 