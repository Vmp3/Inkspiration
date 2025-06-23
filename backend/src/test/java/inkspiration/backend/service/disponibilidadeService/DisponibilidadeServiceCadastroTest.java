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
import inkspiration.backend.exception.DisponibilidadeException.HorarioInvalidoException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeCadastroException;
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
import java.util.Arrays;

@DisplayName("DisponibilidadeService - Testes de Cadastro")
class DisponibilidadeServiceCadastroTest {

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
    private Map<String, List<Map<String, String>>> horariosValidos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Profissional Teste");
        usuario.setEmail("profissional@teste.com");
        
        
        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        
        
        disponibilidade = new Disponibilidade();
        disponibilidade.setIdDisponibilidade(1L);
        disponibilidade.setProfissional(profissional);
        
        
        horariosValidos = new HashMap<>();
        List<Map<String, String>> segundaPeriodos = new ArrayList<>();
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "08:00");
        periodo1.put("fim", "11:59"); 
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "13:00");
        periodo2.put("fim", "18:00");
        segundaPeriodos.add(periodo1);
        segundaPeriodos.add(periodo2);
        horariosValidos.put("Segunda", segundaPeriodos);
    }

    @Test
    @DisplayName("Deve cadastrar disponibilidade com sucesso")
    void deveCadastrarDisponibilidadeComSucesso() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        String jsonEsperado = objectMapper.writeValueAsString(horariosValidos);
        disponibilidade.setHrAtendimento(jsonEsperado);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);
        
        
        Disponibilidade resultado = disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosValidos);
        
        
        assertNotNull(resultado);
        assertEquals(profissional, resultado.getProfissional());
        verify(disponibilidadeRepository).save(any(Disponibilidade.class));
    }

    @Test
    @DisplayName("Deve atualizar disponibilidade existente")
    void deveAtualizarDisponibilidadeExistente() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Disponibilidade disponibilidadeExistente = new Disponibilidade();
        disponibilidadeExistente.setIdDisponibilidade(1L);
        disponibilidadeExistente.setProfissional(profissional);
        disponibilidadeExistente.setHrAtendimento("{\"Segunda\":[{\"inicio\":\"09:00\",\"fim\":\"17:00\"}]}");
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidadeExistente));
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidadeExistente);
        
        
        Disponibilidade resultado = disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosValidos);
        
        
        assertNotNull(resultado);
        verify(disponibilidadeRepository).save(any(Disponibilidade.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado")
    void deveLancarExcecaoQuandoProfissionalNaoEncontrado() {
        
        Long idProfissional = 999L;
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosValidos);
        });
        
        assertTrue(exception.getMessage().contains("Profissional não encontrado"));
    }

    @Test
    @DisplayName("Deve cadastrar disponibilidade DTO com sucesso")
    void deveCadastrarDisponibilidadeDTOComSucesso() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        String jsonEsperado = objectMapper.writeValueAsString(horariosValidos);
        disponibilidade.setHrAtendimento(jsonEsperado);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);
        
        
        DisponibilidadeDTO resultado = disponibilidadeService.cadastrarDisponibilidadeDTO(idProfissional, horariosValidos);
        
        
        assertNotNull(resultado);
        assertEquals(disponibilidade.getIdDisponibilidade(), resultado.getIdDisponibilidade());
        assertEquals(profissional.getIdProfissional(), resultado.getIdProfissional());
    }

    @Test
    @DisplayName("Deve validar horários com período da manhã válido")
    void deveValidarHorariosComPeriodoManhaValido() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosManha = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        periodo.put("fim", "11:59");
        periodos.add(periodo);
        horariosManha.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);
        
        
        assertDoesNotThrow(() -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosManha);
        });
    }

    @Test
    @DisplayName("Deve validar horários com período da tarde válido")
    void deveValidarHorariosComPeriodoTardeValido() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosTarde = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "13:00");
        periodo.put("fim", "18:00");
        periodos.add(periodo);
        horariosTarde.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);
        
        
        assertDoesNotThrow(() -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosTarde);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para horário que cruza manhã e tarde")
    void deveLancarExcecaoParaHorarioQueCruzaManhaETarde() {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "10:00");
        periodo.put("fim", "14:00"); 
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        
        
        HorarioInvalidoException exception = assertThrows(HorarioInvalidoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosInvalidos);
        });
        
        assertTrue(exception.getMessage().contains("deve ser inteiramente de manhã"));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário fim menor que início")
    void deveLancarExcecaoParaHorarioFimMenorQueInicio() {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "15:00");
        periodo.put("fim", "14:00"); 
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        
        
        HorarioInvalidoException exception = assertThrows(HorarioInvalidoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosInvalidos);
        });
        
        assertTrue(exception.getMessage().contains("o fim deve ser maior que o início"));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário incompleto")
    void deveLancarExcecaoParaHorarioIncompleto() {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        
        
        HorarioInvalidoException exception = assertThrows(HorarioInvalidoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosInvalidos);
        });
        
        assertTrue(exception.getMessage().contains("início e fim devem ser informados"));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário vazio")
    void deveLancarExcecaoParaHorarioVazio() {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "");
        periodo.put("fim", "18:00");
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        
        
        HorarioInvalidoException exception = assertThrows(HorarioInvalidoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosInvalidos);
        });
        
        assertTrue(exception.getMessage().contains("início e fim devem ser informados"));
    }

    @Test
    @DisplayName("Deve lançar exceção para formato de horário inválido")
    void deveLancarExcecaoParaFormatoHorarioInvalido() {
        
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "25:00"); 
        periodo.put("fim", "18:00");
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        
        
        HorarioInvalidoException exception = assertThrows(HorarioInvalidoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidade(idProfissional, horariosInvalidos);
        });
        
        assertTrue(exception.getMessage().contains("Formato de horário inválido"));
    }

    

    @Test
    @DisplayName("Deve cadastrar disponibilidade com validação de acesso")
    void deveCadastrarDisponibilidadeComValidacaoAcesso() throws JsonProcessingException {
        
        Long idProfissional = 1L;
        Long idUsuario = 1L;
        String jsonEsperado = objectMapper.writeValueAsString(horariosValidos);
        disponibilidade.setHrAtendimento(jsonEsperado);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeDTO resultado = disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(
            idProfissional, horariosValidos, idUsuario);
        
        
        assertNotNull(resultado);
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso negado")
    void deveLancarExcecaoAcessoNegado() {
        
        Long idProfissional = 1L;
        Long idUsuario = 999L;
        
        doThrow(new RuntimeException("acesso negado")).when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeAcessoException exception = assertThrows(DisponibilidadeAcessoException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(idProfissional, horariosValidos, idUsuario);
        });
        
        assertTrue(exception.getMessage().contains("Acesso negado"));
    }

    @Test
    @DisplayName("Deve lançar exceção de cadastro para horário inválido com validação")
    void deveLancarExcecaoCadastroParaHorarioInvalidoComValidacao() {
        
        Long idProfissional = 1L;
        Long idUsuario = 1L;
        Map<String, List<Map<String, String>>> horariosInvalidos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "15:00");
        periodo.put("fim", "14:00");
        periodos.add(periodo);
        horariosInvalidos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        doNothing().when(authorizationService).requireUserAccessOrAdmin(idUsuario);
        
        
        DisponibilidadeCadastroException exception = assertThrows(DisponibilidadeCadastroException.class, () -> {
            disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(idProfissional, horariosInvalidos, idUsuario);
        });
        
        assertTrue(exception.getMessage().contains("o fim deve ser maior que o início"));
    }
} 