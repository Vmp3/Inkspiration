package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;

@DisplayName("Testes de validação de datas - Agendamento")
public class AgendamentoDataTest {

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }

    // Testes para Data de Início
    @Test
    @DisplayName("Deve aceitar data de início no futuro")
    void deveAceitarDataInicioNoFuturo() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(2);
        agendamento.setDtInicio(futuro);
        assertEquals(futuro, agendamento.getDtInicio());
    }

    @Test
    @DisplayName("Deve aceitar data de início distante no futuro")
    void deveAceitarDataInicioDistanteNoFuturo() {
        LocalDateTime futuroDistante = LocalDateTime.now().plusMonths(6);
        agendamento.setDtInicio(futuroDistante);
        assertEquals(futuroDistante, agendamento.getDtInicio());
    }

    @Test
    @DisplayName("Não deve aceitar data de início nula")
    void naoDeveAceitarDataInicioNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtInicio(null);
        });
        assertEquals("Data de início não pode ser nula", exception.getMessage());
    }
    // Testes para Data de Fim
    @Test
    @DisplayName("Deve aceitar data de fim posterior à data de início")
    void deveAceitarDataFimPosteriorADataInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusHours(2);
        
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
    }

    @Test
    @DisplayName("Deve aceitar data de fim 1 minuto após início")
    void deveAceitarDataFim1MinutoAposInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusMinutes(1);
        
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        
        assertEquals(fim, agendamento.getDtFim());
    }

    @Test
    @DisplayName("Não deve aceitar data de fim nula")
    void naoDeveAceitarDataFimNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtFim(null);
        });
        assertEquals("Data de fim não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de fim anterior à data de início")
    void naoDeveAceitarDataFimAnteriorADataInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.minusHours(1);
        
        agendamento.setDtInicio(inicio);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtFim(fim);
        });
        assertEquals("Data de fim deve ser posterior à data de início", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar data de fim igual à data de início")
    void naoDeveAceitarDataFimIgualADataInicio() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        
        agendamento.setDtInicio(inicio);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtFim(inicio);
        });
        assertEquals("Data de fim deve ser diferente da data de início", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir definir data de fim sem data de início")
    void devePermitirDefinirDataFimSemDataInicio() {
        LocalDateTime futuro = LocalDateTime.now().plusHours(2);
        agendamento.setDtFim(futuro);
        assertEquals(futuro, agendamento.getDtFim());
    }

    // Testes combinados
    @Test
    @DisplayName("Deve validar ordem das datas quando início for definido após fim")
    void deveValidarOrdemDatasQuandoInicioForDefinidoAposFim() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusHours(1);
        
        // Define fim primeiro
        agendamento.setDtFim(fim);
        assertEquals(fim, agendamento.getDtFim());
        
        // Define início depois
        agendamento.setDtInicio(inicio);
        assertEquals(inicio, agendamento.getDtInicio());
    }

    @Test
    @DisplayName("Deve aceitar agendamento de duração longa")
    void deveAceitarAgendamentoDuracaoLonga() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(8); // 8 horas de duração
        
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
    }

    @Test
    @DisplayName("Deve aceitar agendamento de duração curta")
    void deveAceitarAgendamentoDuracaoCurta() {
        LocalDateTime inicio = LocalDateTime.now().plusHours(1);
        LocalDateTime fim = inicio.plusMinutes(30); // 30 minutos de duração
        
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
    }
} 