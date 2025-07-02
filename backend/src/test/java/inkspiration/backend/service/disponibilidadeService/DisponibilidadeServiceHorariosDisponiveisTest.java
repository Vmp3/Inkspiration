package inkspiration.backend.service.disponibilidadeService;

import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.repository.DisponibilidadeRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeConsultaException;
import inkspiration.backend.exception.disponibilidade.TipoServicoInvalidoDisponibilidadeException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

@DisplayName("DisponibilidadeService - Testes de Horários Disponíveis")
class DisponibilidadeServiceHorariosDisponiveisTest {

    @Mock
    private DisponibilidadeRepository disponibilidadeRepository;
    
    @Mock
    private ProfissionalRepository profissionalRepository;
    
    @Mock
    private AgendamentoRepository agendamentoRepository;
    
    @Mock
    private AuthorizationService authorizationService;
    
    @InjectMocks
    private DisponibilidadeService disponibilidadeService;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private Profissional profissional;
    private Usuario usuario;
    private Disponibilidade disponibilidade;
    private LocalDate dataConsulta;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        MockitoAnnotations.openMocks(this);
        
        
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Profissional Teste");
        usuario.setEmail("profissional@teste.com");
        
        
        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        
        
        dataConsulta = LocalDate.now().plusDays(7); 
        
        
        Map<String, List<Map<String, String>>> horarios = new HashMap<>();
        List<Map<String, String>> segundaPeriodos = new ArrayList<>();
        
        Map<String, String> manha = new HashMap<>();
        manha.put("inicio", "08:00");
        manha.put("fim", "11:59"); 
        
        Map<String, String> tarde = new HashMap<>();
        tarde.put("inicio", "13:00");
        tarde.put("fim", "18:00");
        
        segundaPeriodos.add(manha);
        segundaPeriodos.add(tarde);
        
        
        String diaSemana = switch (dataConsulta.getDayOfWeek()) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        horarios.put(diaSemana, segundaPeriodos);
        
        String jsonHorarios = objectMapper.writeValueAsString(horarios);
        
        disponibilidade = new Disponibilidade();
        disponibilidade.setIdDisponibilidade(1L);
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(jsonHorarios);
    }

    @Test
    @DisplayName("Deve obter horários disponíveis para tatuagem pequena sem conflitos")
    void deveObterHorariosDisponiveisTatuagemPequenaSemConflitos() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA; 
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, dataConsulta, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
        
        
        
        
        assertTrue(horariosDisponiveis.contains("08:00"));
        assertTrue(horariosDisponiveis.contains("09:00"));
        assertTrue(horariosDisponiveis.contains("13:00"));
        assertTrue(horariosDisponiveis.contains("14:00"));
        assertTrue(horariosDisponiveis.contains("15:00"));
        assertTrue(horariosDisponiveis.contains("16:00"));
        assertFalse(horariosDisponiveis.contains("10:00")); 
        assertFalse(horariosDisponiveis.contains("17:00")); 
    }

    @Test
    @DisplayName("Deve considerar agendamentos existentes")
    void deveConsiderarAgendamentosExistentes() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA; 
        
        
        Agendamento agendamentoExistente = new Agendamento();
        agendamentoExistente.setIdAgendamento(1L);
        agendamentoExistente.setProfissional(profissional);
        agendamentoExistente.setDtInicio(dataConsulta.atTime(9, 0));
        agendamentoExistente.setDtFim(dataConsulta.atTime(11, 0));
        agendamentoExistente.setStatus(StatusAgendamento.AGENDADO);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(Arrays.asList(agendamentoExistente));
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, dataConsulta, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        
        
        
        assertFalse(horariosDisponiveis.contains("08:00"));
        assertFalse(horariosDisponiveis.contains("09:00"));
        
        
        assertTrue(horariosDisponiveis.contains("13:00"));
        assertTrue(horariosDisponiveis.contains("14:00"));
        assertTrue(horariosDisponiveis.contains("15:00"));
        assertTrue(horariosDisponiveis.contains("16:00"));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para dia não trabalhado")
    void deveRetornarListaVaziaParaDiaNaoTrabalhado() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        LocalDate domingo = LocalDate.now().with(java.time.DayOfWeek.SUNDAY); 
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA;
        
        
        
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, domingo, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        assertTrue(horariosDisponiveis.isEmpty());
    }

    @Test
    @DisplayName("Deve consolidar períodos adjacentes")
    void deveConsolidarPeriodosAdjacentes() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA;
        
        
        Map<String, List<Map<String, String>>> horariosAdjacentes = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "08:00");
        periodo1.put("fim", "11:59");
        
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "12:00"); 
        periodo2.put("fim", "18:00");
        
        periodos.add(periodo1);
        periodos.add(periodo2);
        
        
        String diaSemana = switch (dataConsulta.getDayOfWeek()) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        horariosAdjacentes.put(diaSemana, periodos);
        
        String jsonAdjacentes = objectMapper.writeValueAsString(horariosAdjacentes);
        disponibilidade.setHrAtendimento(jsonAdjacentes);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, dataConsulta, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
        
        
        
        
        assertTrue(horariosDisponiveis.contains("10:00")); 
        assertTrue(horariosDisponiveis.contains("12:00")); 
    }

    @Test
    @DisplayName("Deve filtrar horários passados para data atual")
    void deveFiltrarHorariosPassadosParaDataAtual() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        LocalDate hoje = LocalDate.now();
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA;
        
        
        Map<String, String> nomeDias = Map.of(
            "MONDAY", "Segunda", "TUESDAY", "Terça", "WEDNESDAY", "Quarta",
            "THURSDAY", "Quinta", "FRIDAY", "Sexta", "SATURDAY", "Sábado", "SUNDAY", "Domingo"
        );
        
        String diaHoje = nomeDias.get(hoje.getDayOfWeek().toString());
        
        Map<String, List<Map<String, String>>> horariosHoje = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        periodo.put("fim", "20:00");
        periodos.add(periodo);
        horariosHoje.put(diaHoje, periodos);
        
        String jsonHoje = objectMapper.writeValueAsString(horariosHoje);
        disponibilidade.setHrAtendimento(jsonHoje);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, hoje, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        
        
        
        for (String horario : horariosDisponiveis) {
            assertFalse(java.time.LocalTime.parse(horario).isBefore(java.time.LocalTime.now()),
                "Horário " + horario + " não deveria estar disponível (já passou)");
        }
    }

    @Test
    @DisplayName("Deve lidar com disponibilidade até 23:59")
    void deveLidarComDisponibilidadeAte2359() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        TipoServico tipoServico = TipoServico.TATUAGEM_PEQUENA; 
        
        
        Map<String, List<Map<String, String>>> horarios2359 = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "20:00");
        periodo.put("fim", "23:59");
        periodos.add(periodo);
        
        String diaSemana = switch (dataConsulta.getDayOfWeek()) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        horarios2359.put(diaSemana, periodos);
        
        String json2359 = objectMapper.writeValueAsString(horarios2359);
        disponibilidade.setHrAtendimento(json2359);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, dataConsulta, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
        
        
        assertTrue(horariosDisponiveis.contains("20:00"));
        assertTrue(horariosDisponiveis.contains("21:00"));
        
        assertTrue(horariosDisponiveis.contains("22:00"));
    }

    

    @Test
    @DisplayName("Deve obter horários disponíveis com validação")
    void deveObterHorariosDisponiveisComValidacao() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        String tipoServicoStr = "pequena";
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveisComValidacao(
            idProfissional, dataConsulta, tipoServicoStr);
        
        
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de serviço inválido")
    void deveLancarExcecaoParaTipoServicoInvalido() {
        
        Long idProfissional = 1L;
        String tipoServicoInvalido = "gigante";
        
        
        TipoServicoInvalidoDisponibilidadeException exception = assertThrows(
            TipoServicoInvalidoDisponibilidadeException.class, () -> {
                disponibilidadeService.obterHorariosDisponiveisComValidacao(
                    idProfissional, dataConsulta, tipoServicoInvalido);
            });
        
        assertTrue(exception.getMessage().contains("Tipos válidos: pequena, media, grande, sessao"));
    }

    @Test
    @DisplayName("Deve lançar exceção de consulta para erro de JSON")
    void deveLancarExcecaoConsultaParaErroJSON() {
        
        Long idProfissional = 1L;
        String tipoServicoStr = "pequena";
        disponibilidade.setHrAtendimento("json inválido");
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        DisponibilidadeConsultaException exception = assertThrows(DisponibilidadeConsultaException.class, () -> {
            disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, dataConsulta, tipoServicoStr);
        });
        
        assertTrue(exception.getMessage().contains("Erro ao processar JSON"));
    }

    @Test
    @DisplayName("Deve testar todos os tipos de serviço válidos")
    void deveTestarTodosTiposServicoValidos() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        String[] tiposValidos = {"pequena", "media", "grande", "sessao"};
        
        
        Map<String, List<Map<String, String>>> horariosAmplos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        periodo.put("fim", "20:00");
        periodos.add(periodo);
        
        
        String diaSemana = switch (dataConsulta.getDayOfWeek()) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        horariosAmplos.put(diaSemana, periodos);
        
        String jsonAmplo = objectMapper.writeValueAsString(horariosAmplos);
        disponibilidade.setHrAtendimento(jsonAmplo);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        for (String tipoServico : tiposValidos) {
            assertDoesNotThrow(() -> {
                List<String> horarios = disponibilidadeService.obterHorariosDisponiveisComValidacao(
                    idProfissional, dataConsulta, tipoServico);
                assertNotNull(horarios, "Horários não devem ser null para tipo: " + tipoServico);
            }, "Não deveria lançar exceção para tipo válido: " + tipoServico);
        }
    }

    @Test
    @DisplayName("Deve calcular corretamente horários para sessão (6 horas)")
    void deveCalcularCorretamenteHorariosSessao() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        TipoServico tipoServico = TipoServico.SESSAO; 
        
        
        Map<String, List<Map<String, String>>> horariosLongos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        periodo.put("fim", "20:00"); 
        periodos.add(periodo);
        
        
        String diaSemana = switch (dataConsulta.getDayOfWeek()) {
            case MONDAY -> "Segunda";
            case TUESDAY -> "Terça";
            case WEDNESDAY -> "Quarta";
            case THURSDAY -> "Quinta";
            case FRIDAY -> "Sexta";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
        horariosLongos.put(diaSemana, periodos);
        
        String jsonLongo = objectMapper.writeValueAsString(horariosLongos);
        disponibilidade.setHrAtendimento(jsonLongo);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(agendamentoRepository.findByProfissionalAndPeriod(eq(idProfissional), any(), any()))
            .thenReturn(new ArrayList<>());
        
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
            idProfissional, dataConsulta, tipoServico);
        
        
        assertNotNull(horariosDisponiveis);
        assertFalse(horariosDisponiveis.isEmpty());
        
        
        
        assertTrue(horariosDisponiveis.contains("08:00")); 
        
        assertFalse(horariosDisponiveis.contains("14:00")); 
        assertFalse(horariosDisponiveis.contains("15:00")); 
    }
} 