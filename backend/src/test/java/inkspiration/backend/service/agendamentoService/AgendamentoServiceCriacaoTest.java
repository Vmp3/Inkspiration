package inkspiration.backend.service.agendamentoService;

import inkspiration.backend.service.AgendamentoService;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.exception.agendamento.AutoAgendamentoException;
import inkspiration.backend.exception.agendamento.DataInvalidaAgendamentoException;
import inkspiration.backend.exception.agendamento.TipoServicoInvalidoException;
import inkspiration.backend.exception.agendamento.HorarioConflitanteException;
import inkspiration.backend.exception.agendamento.ProfissionalIndisponivelException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

class AgendamentoServiceCriacaoTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    
    @Mock
    private ProfissionalRepository profissionalRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @Mock
    private DisponibilidadeService disponibilidadeService;
    
    @InjectMocks
    private AgendamentoService agendamentoService;
    
    private Usuario usuario;
    private Profissional profissional;
    private Usuario usuarioProfissional;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup usuario cliente
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Cliente Teste");
        usuario.setEmail("cliente@teste.com");
        
        // Setup usuario profissional
        usuarioProfissional = new Usuario();
        usuarioProfissional.setIdUsuario(2L);
        usuarioProfissional.setNome("Profissional Teste");
        usuarioProfissional.setEmail("profissional@teste.com");
        
        // Setup profissional
        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuarioProfissional);
    }

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() throws Exception {
        // Given
        Long idUsuario = 1L;
        Long idProfissional = 1L;
        String tipoServico = "pequena";
        String descricao = "Tatuagem simples";
        LocalDateTime dtInicio = LocalDate.now().plusDays(2).atTime(14, 0);
        BigDecimal valor = new BigDecimal("150.00");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.existsConflitingSchedule(any(), any(), any())).thenReturn(false);
        
        Agendamento agendamentoSalvo = new Agendamento();
        agendamentoSalvo.setIdAgendamento(1L);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoSalvo);
        
        // When
        Agendamento resultado = agendamentoService.criarAgendamento(
            idUsuario, idProfissional, tipoServico, descricao, dtInicio, valor);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdAgendamento());
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        Long idUsuario = 999L;
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.criarAgendamento(
                idUsuario, 1L, "pequena", "Descrição de teste", 
                LocalDate.now().plusDays(2).atTime(14, 0), 
                new BigDecimal("150.00"));
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado")
    void deveLancarExcecaoQuandoProfissionalNaoEncontrado() {
        // Given
        Long idProfissional = 999L;
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, idProfissional, "pequena", "Descrição de teste", 
                LocalDate.now().plusDays(2).atTime(14, 0), 
                new BigDecimal("150.00"));
        });
        
        assertEquals("Profissional não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para auto agendamento")
    void deveLancarExcecaoParaAutoAgendamento() {
        // Given
        usuario.setIdUsuario(1L);
        usuarioProfissional.setIdUsuario(1L); // Mesmo ID
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        
        // When & Then
        AutoAgendamentoException exception = assertThrows(AutoAgendamentoException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "pequena", "Descrição de teste", 
                LocalDate.now().plusDays(2).atTime(14, 0), 
                new BigDecimal("150.00"));
        });
        
        assertEquals("Não é possível agendar consigo mesmo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para data inválida - passado")
    void deveLancarExcecaoParaDataInvalidaPassado() {
        // Given
        LocalDateTime ontem = LocalDate.now().minusDays(1).atTime(14, 0);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        
        // When & Then
        DataInvalidaAgendamentoException exception = assertThrows(DataInvalidaAgendamentoException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "pequena", "Descrição de teste", ontem, new BigDecimal("150.00"));
        });
        
        assertEquals("Só é possível fazer agendamentos a partir do dia seguinte", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de serviço inválido")
    void deveLancarExcecaoParaTipoServicoInvalido() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        
        // When & Then
        TipoServicoInvalidoException exception = assertThrows(TipoServicoInvalidoException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "invalido", "Descrição de teste", 
                LocalDate.now().plusDays(2).atTime(14, 0), 
                new BigDecimal("150.00"));
        });
        
        assertTrue(exception.getMessage().contains("Tipo de serviço inválido"));
    }

    @Test
    @DisplayName("Deve lançar exceção para conflito de horário do usuário")
    void deveLancarExcecaoParaConflitoHorarioUsuario() {
        // Given
        LocalDateTime dtInicio = LocalDate.now().plusDays(2).atTime(14, 0);
        
        Agendamento agendamentoExistente = new Agendamento();
        agendamentoExistente.setDtInicio(dtInicio);
        agendamentoExistente.setDtFim(dtInicio.plusHours(2));
        agendamentoExistente.setStatus(StatusAgendamento.AGENDADO);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(List.of(agendamentoExistente));
        
        // When & Then
        HorarioConflitanteException exception = assertThrows(HorarioConflitanteException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "pequena", "Descrição de teste", dtInicio, new BigDecimal("150.00"));
        });
        
        assertTrue(exception.getMessage().contains("Você já possui outro agendamento nesse horário"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional indisponível")
    void deveLancarExcecaoQuandoProfissionalIndisponivel() throws Exception {
        // Given
        LocalDateTime dtInicio = LocalDate.now().plusDays(2).atTime(14, 0);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(false);
        
        // When & Then
        ProfissionalIndisponivelException exception = assertThrows(ProfissionalIndisponivelException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "pequena", "Descrição de teste", dtInicio, new BigDecimal("150.00"));
        });
        
        assertTrue(exception.getMessage().contains("O profissional não está trabalhando nesse horário"));
    }

    @Test
    @DisplayName("Deve lançar exceção para conflito de horário do profissional")
    void deveLancarExcecaoParaConflitoHorarioProfissional() throws Exception {
        // Given
        LocalDateTime dtInicio = LocalDate.now().plusDays(2).atTime(14, 0);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.existsConflitingSchedule(any(), any(), any())).thenReturn(true);
        
        // When & Then
        HorarioConflitanteException exception = assertThrows(HorarioConflitanteException.class, () -> {
            agendamentoService.criarAgendamento(
                1L, 1L, "pequena", "Descrição de teste", dtInicio, new BigDecimal("150.00"));
        });
        
        assertTrue(exception.getMessage().contains("O profissional já possui outro agendamento nesse horário"));
    }

    @Test
    @DisplayName("Deve ajustar horário de início corretamente")
    void deveAjustarHorarioInicioCorretamente() throws Exception {
        // Given
        LocalDateTime dtInicioComMinutos = LocalDate.now().plusDays(2).atTime(14, 30, 45);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.existsConflitingSchedule(any(), any(), any())).thenReturn(false);
        
        Agendamento agendamentoCapturado = new Agendamento();
        when(agendamentoRepository.save(argThat(agendamento -> {
            // Verifica se o horário foi ajustado para 14:00:00
            return agendamento.getDtInicio().getMinute() == 0 && 
                   agendamento.getDtInicio().getSecond() == 0 &&
                   agendamento.getDtInicio().getNano() == 0;
        }))).thenReturn(agendamentoCapturado);
        
        // When
        agendamentoService.criarAgendamento(
            1L, 1L, "pequena", "Descrição de teste para agendamento", dtInicioComMinutos, new BigDecimal("150.00"));
        
        // Then
        verify(agendamentoRepository).save(any(Agendamento.class));
    }
}