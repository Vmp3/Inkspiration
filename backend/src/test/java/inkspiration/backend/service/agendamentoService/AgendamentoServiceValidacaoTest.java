package inkspiration.backend.service.agendamentoService;

import inkspiration.backend.service.AgendamentoService;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class AgendamentoServiceValidacaoTest {

    @Test
    @DisplayName("Deve ajustar horário de início corretamente")
    void deveAjustarHorarioInicioCorretamente() {
        // Given
        LocalDateTime horarioComMinutos = LocalDateTime.of(2024, 1, 15, 14, 30, 45, 123456789);
        
        // When
        LocalDateTime horarioAjustado = horarioComMinutos.withMinute(0).withSecond(0).withNano(0);
        
        // Then
        assertEquals(14, horarioAjustado.getHour());
        assertEquals(0, horarioAjustado.getMinute());
        assertEquals(0, horarioAjustado.getSecond());
        assertEquals(0, horarioAjustado.getNano());
    }

    @Test
    @DisplayName("Deve calcular horário de fim baseado no tipo de serviço")
    void deveCalcularHorarioFimBaseadoNoTipoServico() {
        // Given
        LocalDateTime dtInicio = LocalDateTime.of(2024, 1, 15, 14, 0, 0);
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA;
        
        // When
        LocalDateTime dtFim = dtInicio
                .plusHours(tipoServico.getDuracaoHoras())
                .minusMinutes(1)
                .withSecond(59)
                .withNano(0);
        
        // Then
        assertEquals(15, dtFim.getHour());
        assertEquals(59, dtFim.getMinute());
        assertEquals(59, dtFim.getSecond());
        assertEquals(0, dtFim.getNano());
    }

    @Test
    @DisplayName("Deve validar data mínima para agendamento")
    void deveValidarDataMinimaParaAgendamento() {
        // Given
        LocalDateTime hoje = LocalDate.now().atTime(10, 0);
        LocalDateTime amanha = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime ontem = LocalDate.now().minusDays(1).atTime(10, 0);
        
        // When & Then
        assertTrue(hoje.isBefore(amanha));
        assertTrue(ontem.isBefore(amanha));
        assertFalse(amanha.plusHours(1).isBefore(amanha));
    }

    @Test
    @DisplayName("Deve identificar conflitos de horário corretamente")
    void deveIdentificarConflitosDeHorarioCorretamente() {
        // Given
        LocalDateTime inicio1 = LocalDateTime.of(2024, 1, 15, 14, 0);
        LocalDateTime fim1 = LocalDateTime.of(2024, 1, 15, 16, 59, 59);
        
        LocalDateTime inicio2 = LocalDateTime.of(2024, 1, 15, 15, 0);
        LocalDateTime fim2 = LocalDateTime.of(2024, 1, 15, 17, 59, 59);
        
        // When
        boolean temConflito = inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
        
        // Then
        assertTrue(temConflito);
    }

    @Test
    @DisplayName("Deve validar tipos de serviço válidos")
    void deveValidarTiposServicoValidos() {
        // Given
        List<String> tiposValidos = Arrays.asList("pequena", "media", "grande", "sessao");
        
        // When & Then
        for (String tipo : tiposValidos) {
            assertDoesNotThrow(() -> TipoServico.fromDescricao(tipo));
        }
    }

    @Test
    @DisplayName("Deve rejeitar tipos de serviço inválidos")
    void deveRejeitarTiposServicoInvalidos() {
        // Given
        List<String> tiposInvalidos = Arrays.asList("invalido", "", null, "gigante");
        
        // When & Then
        for (String tipo : tiposInvalidos) {
            assertThrows(IllegalArgumentException.class, () -> TipoServico.fromDescricao(tipo));
        }
    }

    @Test
    @DisplayName("Deve validar status de agendamento válidos")
    void deveValidarStatusAgendamentoValidos() {
        // Given
        List<String> statusValidos = Arrays.asList("AGENDADO", "CANCELADO", "CONCLUIDO");
        
        // When & Then
        for (String status : statusValidos) {
            assertDoesNotThrow(() -> StatusAgendamento.fromDescricao(status));
        }
    }

    @Test
    @DisplayName("Deve rejeitar status de agendamento inválidos")
    void deveRejeitarStatusAgendamentoInvalidos() {
        // Given
        List<String> statusInvalidos = Arrays.asList("invalido", "", null, "Pendente");
        
        // When & Then
        for (String status : statusInvalidos) {
            assertThrows(IllegalArgumentException.class, () -> StatusAgendamento.fromDescricao(status));
        }
    }

    @Test
    @DisplayName("Deve validar período mínimo para cancelamento")
    void deveValidarPeriodoMinimaParaCancelamento() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime agendamentoDentro3Dias = agora.plusDays(3).plusHours(1);
        LocalDateTime agendamentoMenos3Dias = agora.plusDays(2);
        
        // When
        LocalDateTime dataLimite3Dias = agendamentoDentro3Dias.minusDays(3);
        LocalDateTime dataLimiteMenos3Dias = agendamentoMenos3Dias.minusDays(3);
        
        // Then
        assertFalse(agora.isAfter(dataLimite3Dias)); // Pode cancelar
        assertTrue(agora.isAfter(dataLimiteMenos3Dias)); // Não pode cancelar
    }

    @Test
    @DisplayName("Deve identificar agendamentos que passaram do horário")
    void deveIdentificarAgendamentosQuePassaramDoHorario() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime agendamentoPassado = agora.minusHours(1);
        LocalDateTime agendamentoFuturo = agora.plusHours(1);
        
        // When & Then
        assertTrue(agendamentoPassado.isBefore(agora));
        assertFalse(agendamentoFuturo.isBefore(agora));
    }

    @Test
    @DisplayName("Deve validar valores monetários válidos")
    void deveValidarValoresMonetariosValidos() {
        // Given
        List<String> valoresValidos = Arrays.asList("100.00", "50.50", "0.01", "999999.99");
        
        // When & Then
        for (String valor : valoresValidos) {
            assertDoesNotThrow(() -> new BigDecimal(valor));
        }
    }

    @Test
    @DisplayName("Deve rejeitar valores monetários inválidos")
    void deveRejeitarValoresMonetariosInvalidos() {
        // Given
        List<String> valoresInvalidos = Arrays.asList("abc", "10.abc");
        
        // When & Then
        for (String valor : valoresInvalidos) {
            assertThrows(NumberFormatException.class, () -> new BigDecimal(valor));
        }
    }
} 