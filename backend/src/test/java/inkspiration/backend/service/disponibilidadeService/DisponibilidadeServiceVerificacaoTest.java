package inkspiration.backend.service.disponibilidadeService;

import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.repository.DisponibilidadeRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;

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
import java.time.DayOfWeek;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@DisplayName("DisponibilidadeService - Testes de Verificação")
class DisponibilidadeServiceVerificacaoTest {

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
        
        
        Map<String, List<Map<String, String>>> horariosSemanais = new HashMap<>();
        
        String[] diasUteis = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta"};
        for (String dia : diasUteis) {
            List<Map<String, String>> periodos = new ArrayList<>();
            
            Map<String, String> manha = new HashMap<>();
            manha.put("inicio", "08:00");
            manha.put("fim", "12:00");
            
            Map<String, String> tarde = new HashMap<>();
            tarde.put("inicio", "13:00");
            tarde.put("fim", "18:00");
            
            periodos.add(manha);
            periodos.add(tarde);
            horariosSemanais.put(dia, periodos);
        }
        
        
        List<Map<String, String>> sabadoPeriodos = new ArrayList<>();
        Map<String, String> sabadoManha = new HashMap<>();
        sabadoManha.put("inicio", "08:00");
        sabadoManha.put("fim", "12:00");
        sabadoPeriodos.add(sabadoManha);
        horariosSemanais.put("Sábado", sabadoPeriodos);
        
        String jsonHorarios = objectMapper.writeValueAsString(horariosSemanais);
        
        disponibilidade = new Disponibilidade();
        disponibilidade.setIdDisponibilidade(1L);
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(jsonHorarios);
    }

    @Test
    @DisplayName("Deve validar que profissional está disponível em horário de trabalho")
    void deveValidarQueProfissionalEstaDisponivelEmHorarioTrabalho() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(10, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(11, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar que profissional está disponível em horário da tarde")
    void deveValidarQueProfissionalEstaDisponivelEmHorarioTarde() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 25).atTime(14, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 25).atTime(16, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar horário fora do período de trabalho")
    void deveRejeitarHorarioForaPeriodoTrabalho() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 26).atTime(19, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 26).atTime(20, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar horário no domingo (dia não trabalhado)")
    void deveRejeitarHorarioNoDomingo() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 30).atTime(10, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 30).atTime(11, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar horário no sábado (só manhã)")
    void deveValidarHorarioNoSabado() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 29).atTime(9, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 29).atTime(11, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar horário da tarde no sábado")
    void deveRejeitarHorarioTardeNoSabado() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 29).atTime(14, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 29).atTime(16, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar quando data fim é antes da data início")
    void deveRejeitarQuandoDataFimAntesDataInicio() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(11, 0);
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(10, 0); 
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar horário que termina exatamente no fim do expediente")
    void deveValidarHorarioQueTerminaExatamenteNoFimExpediente() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 27).atTime(17, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 27).atTime(18, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar horário que começa exatamente no início do expediente")
    void deveValidarHorarioQueComecaExatamenteNoInicioExpediente() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 28).atTime(8, 0); 
        LocalDateTime fim = LocalDate.of(2024, 6, 28).atTime(9, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve rejeitar horário que cruza o intervalo do almoço")
    void deveRejeitarHorarioQueCruzaIntervaloAlmoco() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(11, 30); 
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(13, 30);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar horário com disponibilidade 24h")
    void deveValidarHorarioComDisponibilidade24h() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        
        Map<String, List<Map<String, String>>> horarios24h = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "00:00");
        periodo.put("fim", "23:59");
        periodos.add(periodo);
        horarios24h.put("Segunda", periodos);
        
        String json24h = objectMapper.writeValueAsString(horarios24h);
        disponibilidade.setHrAtendimento(json24h);
        
        
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(22, 0);
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(23, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        
        
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar conversão correta dos dias da semana")
    void deveValidarConversaoCorretaDiasSemana() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        
        Map<String, List<Map<String, String>>> todosDias = new HashMap<>();
        String[] diasPortugues = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        LocalDate[] datas = {
            LocalDate.of(2024, 6, 24), 
            LocalDate.of(2024, 6, 25), 
            LocalDate.of(2024, 6, 26), 
            LocalDate.of(2024, 6, 27), 
            LocalDate.of(2024, 6, 28), 
            LocalDate.of(2024, 6, 29), 
            LocalDate.of(2024, 6, 30)  
        };
        
        for (String dia : diasPortugues) {
            List<Map<String, String>> periodos = new ArrayList<>();
            Map<String, String> periodo = new HashMap<>();
            periodo.put("inicio", "09:00");
            periodo.put("fim", "17:00");
            periodos.add(periodo);
            todosDias.put(dia, periodos);
        }
        
        String jsonTodosDias = objectMapper.writeValueAsString(todosDias);
        disponibilidade.setHrAtendimento(jsonTodosDias);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        for (LocalDate data : datas) {
            LocalDateTime inicio = data.atTime(10, 0);
            LocalDateTime fim = data.atTime(11, 0);
            
            boolean resultado = disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
            assertTrue(resultado, "Deveria estar disponível em " + data.getDayOfWeek());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado na verificação")
    void deveLancarExcecaoQuandoProfissionalNaoEncontradoNaVerificacao() {
        
        Long idProfissional = 999L;
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(10, 0);
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(11, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        });
        
        assertTrue(exception.getMessage().contains("Profissional não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando disponibilidade não cadastrada na verificação")
    void deveLancarExcecaoQuandoDisponibilidadeNaoCadastradaNaVerificacao() {
        
        Long idProfissional = 1L;
        LocalDateTime inicio = LocalDate.of(2024, 6, 24).atTime(10, 0);
        LocalDateTime fim = LocalDate.of(2024, 6, 24).atTime(11, 0);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio, fim);
        });
        
        assertTrue(exception.getMessage().contains("Disponibilidade não cadastrada"));
    }

    @Test
    @DisplayName("Deve validar horário com múltiplos períodos no mesmo dia")
    void deveValidarHorarioComMultiplosPeriodosMesmoDia() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        
        Map<String, List<Map<String, String>>> horariosMultiplos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        
        Map<String, String> manha = new HashMap<>();
        manha.put("inicio", "08:00");
        manha.put("fim", "10:00");
        
        Map<String, String> meio = new HashMap<>();
        meio.put("inicio", "11:00");
        meio.put("fim", "13:00");
        
        Map<String, String> tarde = new HashMap<>();
        tarde.put("inicio", "14:00");
        tarde.put("fim", "18:00");
        
        periodos.add(manha);
        periodos.add(meio);
        periodos.add(tarde);
        horariosMultiplos.put("Segunda", periodos);
        
        String jsonMultiplos = objectMapper.writeValueAsString(horariosMultiplos);
        disponibilidade.setHrAtendimento(jsonMultiplos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        
        LocalDateTime inicio1 = LocalDate.of(2024, 6, 24).atTime(8, 30);
        LocalDateTime fim1 = LocalDate.of(2024, 6, 24).atTime(9, 30);
        assertTrue(disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio1, fim1));
        
        
        LocalDateTime inicio2 = LocalDate.of(2024, 6, 24).atTime(11, 30);
        LocalDateTime fim2 = LocalDate.of(2024, 6, 24).atTime(12, 30);
        assertTrue(disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio2, fim2));
        
        
        LocalDateTime inicio3 = LocalDate.of(2024, 6, 24).atTime(15, 0);
        LocalDateTime fim3 = LocalDate.of(2024, 6, 24).atTime(17, 0);
        assertTrue(disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio3, fim3));
        
        
        LocalDateTime inicio4 = LocalDate.of(2024, 6, 24).atTime(10, 30);
        LocalDateTime fim4 = LocalDate.of(2024, 6, 24).atTime(10, 45);
        assertFalse(disponibilidadeService.isProfissionalDisponivel(idProfissional, inicio4, fim4));
    }
} 