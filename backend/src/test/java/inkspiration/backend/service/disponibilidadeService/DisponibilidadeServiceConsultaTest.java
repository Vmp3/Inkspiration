package inkspiration.backend.service.disponibilidadeService;

import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.repository.DisponibilidadeRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeConsultaException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeAcessoException;

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
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@DisplayName("DisponibilidadeService - Testes de Consulta")
class DisponibilidadeServiceConsultaTest {

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
    private Map<String, List<Map<String, String>>> horariosEsperados;
    private String jsonHorarios;

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
        
        
        horariosEsperados = new HashMap<>();
        List<Map<String, String>> segundaPeriodos = new ArrayList<>();
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "08:00");
        periodo1.put("fim", "12:00");
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "13:00");
        periodo2.put("fim", "18:00");
        segundaPeriodos.add(periodo1);
        segundaPeriodos.add(periodo2);
        horariosEsperados.put("Segunda", segundaPeriodos);
        
        List<Map<String, String>> tercaPeriodos = new ArrayList<>();
        Map<String, String> periodo3 = new HashMap<>();
        periodo3.put("inicio", "09:00");
        periodo3.put("fim", "17:00");
        tercaPeriodos.add(periodo3);
        horariosEsperados.put("Terça", tercaPeriodos);
        
        jsonHorarios = objectMapper.writeValueAsString(horariosEsperados);
        
        
        disponibilidade = new Disponibilidade();
        disponibilidade.setIdDisponibilidade(1L);
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(jsonHorarios);
    }

    @Test
    @DisplayName("Deve obter disponibilidade com sucesso")
    void deveObterDisponibilidadeComSucesso() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidade(idProfissional);
        
        
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("Segunda"));
        assertTrue(resultado.containsKey("Terça"));
        assertEquals(2, resultado.get("Segunda").size());
        assertEquals(1, resultado.get("Terça").size());
        assertEquals("08:00", resultado.get("Segunda").get(0).get("inicio"));
        assertEquals("18:00", resultado.get("Segunda").get(1).get("fim"));
        assertEquals("09:00", resultado.get("Terça").get(0).get("inicio"));
        assertEquals("17:00", resultado.get("Terça").get(0).get("fim"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado na consulta")
    void deveLancarExcecaoQuandoProfissionalNaoEncontradoNaConsulta() {
        
        Long idProfissional = 999L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.obterDisponibilidade(idProfissional);
        });
        
        assertTrue(exception.getMessage().contains("Profissional não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando disponibilidade não cadastrada")
    void deveLancarExcecaoQuandoDisponibilidadeNaoCadastrada() {
        
        Long idProfissional = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.obterDisponibilidade(idProfissional);
        });
        
        assertTrue(exception.getMessage().contains("Disponibilidade não cadastrada"));
    }

    @Test
    @DisplayName("Deve buscar disponibilidade por profissional DTO com sucesso")
    void deveBuscarDisponibilidadePorProfissionalDTOComSucesso() {
        
        Long idProfissional = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        DisponibilidadeDTO resultado = disponibilidadeService.buscarPorProfissionalDTO(idProfissional);
        
        
        assertNotNull(resultado);
        assertEquals(disponibilidade.getIdDisponibilidade(), resultado.getIdDisponibilidade());
        assertEquals(profissional.getIdProfissional(), resultado.getIdProfissional());
        assertEquals(jsonHorarios, resultado.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado para DTO")
    void deveLancarExcecaoQuandoProfissionalNaoEncontradoParaDTO() {
        
        Long idProfissional = 999L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.buscarPorProfissionalDTO(idProfissional);
        });
        
        assertTrue(exception.getMessage().contains("Profissional não encontrado"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando disponibilidade não cadastrada para DTO")
    void deveLancarExcecaoQuandoDisponibilidadeNaoCadastradaParaDTO() {
        
        Long idProfissional = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.buscarPorProfissionalDTO(idProfissional);
        });
        
        assertTrue(exception.getMessage().contains("Disponibilidade não cadastrada"));
    }

    @Test
    @DisplayName("Deve obter disponibilidade com JSON complexo")
    void deveObterDisponibilidadeComJsonComplexo() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosComplexos = new HashMap<>();
        
        
        List<Map<String, String>> segundaPeriodos = new ArrayList<>();
        Map<String, String> manha = new HashMap<>();
        manha.put("inicio", "08:00");
        manha.put("fim", "11:59");
        Map<String, String> tarde = new HashMap<>();
        tarde.put("inicio", "13:00");
        tarde.put("fim", "17:00");
        Map<String, String> noite = new HashMap<>();
        noite.put("inicio", "19:00");
        noite.put("fim", "22:00");
        segundaPeriodos.add(manha);
        segundaPeriodos.add(tarde);
        segundaPeriodos.add(noite);
        horariosComplexos.put("Segunda", segundaPeriodos);
        
        
        List<Map<String, String>> sabadoPeriodos = new ArrayList<>();
        Map<String, String> sabadoManha = new HashMap<>();
        sabadoManha.put("inicio", "08:00");
        sabadoManha.put("fim", "12:00");
        sabadoPeriodos.add(sabadoManha);
        horariosComplexos.put("Sábado", sabadoPeriodos);
        
        String jsonComplexo = objectMapper.writeValueAsString(horariosComplexos);
        disponibilidade.setHrAtendimento(jsonComplexo);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidade(idProfissional);
        
        
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("Segunda"));
        assertTrue(resultado.containsKey("Sábado"));
        assertEquals(3, resultado.get("Segunda").size());
        assertEquals(1, resultado.get("Sábado").size());
        assertEquals("08:00", resultado.get("Segunda").get(0).get("inicio"));
        assertEquals("22:00", resultado.get("Segunda").get(2).get("fim"));
        assertEquals("08:00", resultado.get("Sábado").get(0).get("inicio"));
        assertEquals("12:00", resultado.get("Sábado").get(0).get("fim"));
    }

    

    @Test
    @DisplayName("Deve obter disponibilidade com validação de acesso")
    void deveObterDisponibilidadeComValidacaoAcesso() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional);
        
        
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("Segunda"));
        assertTrue(resultado.containsKey("Terça"));
    }

    @Test
    @DisplayName("Deve lançar exceção de consulta para erro de JSON")
    void deveLancarExcecaoConsultaParaErroJSON() {
        
        Long idProfissional = 1L;
        disponibilidade.setHrAtendimento("json inválido");
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        DisponibilidadeConsultaException exception = assertThrows(DisponibilidadeConsultaException.class, () -> {
            disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional);
        });
        
        assertTrue(exception.getMessage().contains("Erro ao processar JSON"));
    }

    @Test
    @DisplayName("Deve buscar por profissional DTO com validação de acesso")
    void deveBuscarPorProfissionalDTOComValidacaoAcesso() {
        
        Long idProfissional = 1L;
        Long idUsuario = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        doNothing().when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeDTO resultado = disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, idUsuario);
        
        
        assertNotNull(resultado);
        assertEquals(disponibilidade.getIdDisponibilidade(), resultado.getIdDisponibilidade());
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso negado na consulta")
    void deveLancarExcecaoAcessoNegadoNaConsulta() {
        
        Long idProfissional = 1L;
        Long idUsuario = 999L;
        
        doThrow(new RuntimeException("acesso negado")).when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeAcessoException exception = assertThrows(DisponibilidadeAcessoException.class, () -> {
            disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, idUsuario);
        });
        
        assertTrue(exception.getMessage().contains("Acesso negado"));
    }

    @Test
    @DisplayName("Deve lançar exceção de consulta para erro geral")
    void deveLancarExcecaoConsultaParaErroGeral() {
        
        Long idProfissional = 1L;
        Long idUsuario = 1L;
        
        when(profissionalRepository.findById(idProfissional)).thenThrow(new RuntimeException("Erro de conexão"));
        doNothing().when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeConsultaException exception = assertThrows(DisponibilidadeConsultaException.class, () -> {
            disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, idUsuario);
        });
        
        assertTrue(exception.getMessage().contains("Erro ao consultar disponibilidade"));
    }

    @Test
    @DisplayName("Deve processar JSON com diferentes formatos de horário")
    void deveProcessarJsonComDiferentesFormatosHorario() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosVariados = new HashMap<>();
        
        List<Map<String, String>> quartaPeriodos = new ArrayList<>();
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "07:30");
        periodo1.put("fim", "11:30");
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "14:15");
        periodo2.put("fim", "18:45");
        quartaPeriodos.add(periodo1);
        quartaPeriodos.add(periodo2);
        horariosVariados.put("Quarta", quartaPeriodos);
        
        String jsonVariado = objectMapper.writeValueAsString(horariosVariados);
        disponibilidade.setHrAtendimento(jsonVariado);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        
        
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidade(idProfissional);
        
        
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("Quarta"));
        assertEquals(2, resultado.get("Quarta").size());
        assertEquals("07:30", resultado.get("Quarta").get(0).get("inicio"));
        assertEquals("11:30", resultado.get("Quarta").get(0).get("fim"));
        assertEquals("14:15", resultado.get("Quarta").get(1).get("inicio"));
        assertEquals("18:45", resultado.get("Quarta").get(1).get("fim"));
    }

    @Test
    @DisplayName("Deve processar disponibilidade com todos os dias da semana")
    void deveProcessarDisponibilidadeComTodosDiasSemana() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> todosDias = new HashMap<>();
        
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        for (String dia : dias) {
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
        
        
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidade(idProfissional);
        
        
        assertNotNull(resultado);
        assertEquals(7, resultado.size());
        for (String dia : dias) {
            assertTrue(resultado.containsKey(dia));
            assertEquals(1, resultado.get(dia).size());
            assertEquals("09:00", resultado.get(dia).get(0).get("inicio"));
            assertEquals("17:00", resultado.get(dia).get(0).get("fim"));
        }
    }
} 