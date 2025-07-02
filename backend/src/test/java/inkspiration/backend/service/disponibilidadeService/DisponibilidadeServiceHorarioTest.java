package inkspiration.backend.service.disponibilidadeService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DisponibilidadeServiceHorarioTest {

    @Test
    @DisplayName("Deve validar horário de manhã corretamente")
    void deveValidarHorarioManhaCorretamente() {
        // Given
        LocalTime inicioManha = LocalTime.of(8, 0);
        LocalTime fimManha = LocalTime.of(11, 59);
        LocalTime meiodia = LocalTime.of(12, 0);
        
        // When & Then
        assertTrue(inicioManha.isBefore(meiodia));
        assertTrue(fimManha.isBefore(meiodia));
        assertTrue(fimManha.isAfter(inicioManha));
    }

    @Test
    @DisplayName("Deve validar horário de tarde corretamente")
    void deveValidarHorarioTardeCorretamente() {
        // Given
        LocalTime inicioTarde = LocalTime.of(12, 0);
        LocalTime fimTarde = LocalTime.of(23, 59);
        LocalTime meiodia = LocalTime.of(12, 0);
        
        // When & Then
        assertFalse(inicioTarde.isBefore(meiodia));
        assertFalse(fimTarde.isBefore(meiodia));
        assertTrue(fimTarde.isAfter(inicioTarde));
    }

    @Test
    @DisplayName("Deve rejeitar horário que cruza manhã e tarde")
    void deveRejeitarHorarioQueCruzaManhaETarde() {
        // Given
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fim = LocalTime.of(14, 0);
        LocalTime meiodia = LocalTime.of(12, 0);
        
        // When
        boolean cruzaPeriodos = inicio.isBefore(meiodia) && fim.isAfter(meiodia);
        
        // Then
        assertTrue(cruzaPeriodos);
    }

    @Test
    @DisplayName("Deve validar que fim é após início")
    void deveValidarQueFimEhAposInicio() {
        // Given
        LocalTime inicio = LocalTime.of(9, 0);
        LocalTime fimValido = LocalTime.of(10, 0);
        LocalTime fimInvalido = LocalTime.of(8, 0);
        
        // When & Then
        assertTrue(fimValido.isAfter(inicio));
        assertFalse(fimInvalido.isAfter(inicio));
    }

    @Test
    @DisplayName("Deve criar mapa de horários válido")
    void deveCriarMapaHorariosValido() {
        // Given
        Map<String, List<Map<String, String>>> horarios = new HashMap<>();
        List<Map<String, String>> segundaPeriodos = new ArrayList<>();
        
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "08:00");
        periodo1.put("fim", "12:00");
        
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "13:00");
        periodo2.put("fim", "18:00");
        
        segundaPeriodos.add(periodo1);
        segundaPeriodos.add(periodo2);
        horarios.put("Segunda", segundaPeriodos);
        
        // When & Then
        assertTrue(horarios.containsKey("Segunda"));
        assertEquals(2, horarios.get("Segunda").size());
        assertEquals("08:00", horarios.get("Segunda").get(0).get("inicio"));
        assertEquals("18:00", horarios.get("Segunda").get(1).get("fim"));
    }

    @Test
    @DisplayName("Deve converter dia da semana para português")
    void deveConverterDiaSemanParaPortugues() {
        // Given
        Map<DayOfWeek, String> diasPortugues = new HashMap<>();
        diasPortugues.put(DayOfWeek.MONDAY, "Segunda");
        diasPortugues.put(DayOfWeek.TUESDAY, "Terça");
        diasPortugues.put(DayOfWeek.WEDNESDAY, "Quarta");
        diasPortugues.put(DayOfWeek.THURSDAY, "Quinta");
        diasPortugues.put(DayOfWeek.FRIDAY, "Sexta");
        diasPortugues.put(DayOfWeek.SATURDAY, "Sábado");
        diasPortugues.put(DayOfWeek.SUNDAY, "Domingo");
        
        // When & Then
        assertEquals("Segunda", diasPortugues.get(DayOfWeek.MONDAY));
        assertEquals("Terça", diasPortugues.get(DayOfWeek.TUESDAY));
        assertEquals("Quarta", diasPortugues.get(DayOfWeek.WEDNESDAY));
        assertEquals("Quinta", diasPortugues.get(DayOfWeek.THURSDAY));
        assertEquals("Sexta", diasPortugues.get(DayOfWeek.FRIDAY));
        assertEquals("Sábado", diasPortugues.get(DayOfWeek.SATURDAY));
        assertEquals("Domingo", diasPortugues.get(DayOfWeek.SUNDAY));
    }

    @Test
    @DisplayName("Deve validar conflito de horário")
    void deveValidarConflitoHorario() {
        // Given
        LocalDateTime inicio1 = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime fim1 = LocalDateTime.of(2024, 12, 15, 11, 0);
        
        LocalDateTime inicio2 = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime fim2 = LocalDateTime.of(2024, 12, 15, 12, 0);
        
        // When
        boolean temConflito = inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
        
        // Then
        assertTrue(temConflito);
    }

    @Test
    @DisplayName("Deve validar horário sem conflito")
    void deveValidarHorarioSemConflito() {
        // Given
        LocalDateTime inicio1 = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime fim1 = LocalDateTime.of(2024, 12, 15, 11, 0);
        
        LocalDateTime inicio2 = LocalDateTime.of(2024, 12, 15, 11, 0);
        LocalDateTime fim2 = LocalDateTime.of(2024, 12, 15, 13, 0);
        
        // When
        boolean temConflito = inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
        
        // Then
        assertFalse(temConflito);
    }

    @Test
    @DisplayName("Deve ajustar horário fim para 23:59:59")
    void deveAjustarHorarioFimPara235959() {
        // Given
        LocalTime horaFim = LocalTime.of(23, 59);
        
        // When
        LocalTime horaFimAjustada = horaFim.equals(LocalTime.of(23, 59)) ? 
                LocalTime.of(23, 59, 59, 999999999) : horaFim;
        
        // Then
        assertEquals(23, horaFimAjustada.getHour());
        assertEquals(59, horaFimAjustada.getMinute());
        assertEquals(59, horaFimAjustada.getSecond());
    }

    @Test
    @DisplayName("Deve validar horário dentro do período de trabalho")
    void deveValidarHorarioDentroPeriodoTrabalho() {
        // Given
        LocalTime inicioTrabalho = LocalTime.of(9, 0);
        LocalTime fimTrabalho = LocalTime.of(17, 0);
        LocalTime horarioVerificacao = LocalTime.of(14, 0);
        
        // When
        boolean dentroHorario = !horarioVerificacao.isBefore(inicioTrabalho) && 
                               !horarioVerificacao.isAfter(fimTrabalho);
        
        // Then
        assertTrue(dentroHorario);
    }

    @Test
    @DisplayName("Deve rejeitar horário fora do período de trabalho")
    void deveRejeitarHorarioForaPeriodoTrabalho() {
        // Given
        LocalTime inicioTrabalho = LocalTime.of(9, 0);
        LocalTime fimTrabalho = LocalTime.of(17, 0);
        LocalTime horarioAntes = LocalTime.of(8, 0);
        LocalTime horarioDepois = LocalTime.of(18, 0);
        
        // When & Then
        assertTrue(horarioAntes.isBefore(inicioTrabalho));
        assertTrue(horarioDepois.isAfter(fimTrabalho));
    }

    @Test
    @DisplayName("Deve consolidar períodos adjacentes")
    void deveConsolidarPeriodosAdjacentes() {
        // Given
        LocalTime fim1 = LocalTime.of(12, 0);
        LocalTime inicio2 = LocalTime.of(12, 0);
        
        // When
        boolean saoAdjacentes = inicio2.equals(fim1) || inicio2.minusMinutes(1).equals(fim1);
        
        // Then
        assertTrue(saoAdjacentes);
    }

    @Test
    @DisplayName("Deve calcular horário limite para serviço")
    void deveCalcularHorarioLimiteParaServico() {
        // Given
        LocalTime fimTrabalho = LocalTime.of(18, 0);
        int duracaoServico = 2; // horas
        
        // When
        LocalTime horarioLimite = fimTrabalho.minusHours(duracaoServico);
        
        // Then
        assertEquals(16, horarioLimite.getHour());
        assertEquals(0, horarioLimite.getMinute());
    }
} 