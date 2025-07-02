package inkspiration.backend.service.agendamentoService;

import inkspiration.backend.service.AgendamentoService;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

class AgendamentoServiceConsultaTest {

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
    private Agendamento agendamento;

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
        
        // Setup agendamento
        agendamento = new Agendamento();
        agendamento.setIdAgendamento(1L);
        agendamento.setUsuario(usuario);
        agendamento.setProfissional(profissional);
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao("Descrição de teste para agendamento");
        agendamento.setDtInicio(LocalDateTime.now().plusDays(1));
        agendamento.setDtFim(LocalDateTime.now().plusDays(1).plusHours(2));
        agendamento.setValor(new BigDecimal("150.00"));
        agendamento.setStatus(StatusAgendamento.AGENDADO);
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID com sucesso")
    void deveBuscarAgendamentoPorIdComSucesso() {
        // Given
        Long id = 1L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When
        Agendamento resultado = agendamentoService.buscarPorId(id);
        
        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdAgendamento());
        assertEquals("Descrição de teste para agendamento", resultado.getDescricao());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não encontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        // Given
        Long id = 999L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.buscarPorId(id);
        });
        
        assertEquals("Agendamento não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID e retornar DTO")
    void deveBuscarAgendamentoPorIdERetornarDTO() {
        // Given
        Long id = 1L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When
        AgendamentoDTO resultado = agendamentoService.buscarPorIdDTO(id);
        
        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário")
    void deveListarAgendamentosPorUsuario() {
        // Given
        Long idUsuario = 1L;
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(agendamentos);
        
        // When
        List<Agendamento> resultado = agendamentoService.listarPorUsuario(idUsuario);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendamento.getIdAgendamento(), resultado.get(0).getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário com paginação")
    void deveListarAgendamentosPorUsuarioComPaginacao() {
        // Given
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        
        // When
        Page<Agendamento> resultado = agendamentoService.listarPorUsuario(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário e retornar DTOs")
    void deveListarAgendamentosPorUsuarioERetornarDTOs() {
        // Given
        Long idUsuario = 1L;
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(agendamentos);
        
        // When
        List<AgendamentoDTO> resultado = agendamentoService.listarPorUsuarioDTO(idUsuario);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendamento.getIdAgendamento(), resultado.get(0).getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário com paginação e retornar DTOs")
    void deveListarAgendamentosPorUsuarioComPaginacaoERetornarDTOs() {
        // Given
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        
        // When
        Page<AgendamentoDTO> resultado = agendamentoService.listarPorUsuarioDTO(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional")
    void deveListarAgendamentosPorProfissional() {
        // Given
        Long idProfissional = 1L;
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissional(profissional)).thenReturn(agendamentos);
        
        // When
        List<Agendamento> resultado = agendamentoService.listarPorProfissional(idProfissional);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendamento.getIdAgendamento(), resultado.get(0).getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional com paginação")
    void deveListarAgendamentosPorProfissionalComPaginacao() {
        // Given
        Long idProfissional = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissional(profissional, pageable)).thenReturn(page);
        
        // When
        Page<Agendamento> resultado = agendamentoService.listarPorProfissional(idProfissional, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional e retornar DTOs")
    void deveListarAgendamentosPorProfissionalERetornarDTOs() {
        // Given
        Long idProfissional = 1L;
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissional(profissional)).thenReturn(agendamentos);
        
        // When
        List<AgendamentoDTO> resultado = agendamentoService.listarPorProfissionalDTO(idProfissional);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(agendamento.getIdAgendamento(), resultado.get(0).getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional com paginação e retornar DTOs")
    void deveListarAgendamentosPorProfissionalComPaginacaoERetornarDTOs() {
        // Given
        Long idProfissional = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissional(profissional, pageable)).thenReturn(page);
        
        // When
        Page<AgendamentoDTO> resultado = agendamentoService.listarPorProfissionalDTO(idProfissional, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar agendamentos futuros do usuário")
    void deveListarAgendamentosFuturosDoUsuario() {
        // Given
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        agendamento.setDtFim(LocalDateTime.now().plusDays(2));
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimAfterOrderByDtInicioAsc(eq(usuario), any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAgendamentosFuturos(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar agendamentos passados do usuário")
    void deveListarAgendamentosPassadosDoUsuario() {
        // Given
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        // Mantém datas futuras válidas para a entidade, mas simula comportamento de agendamentos passados via mock
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAgendamentosPassados(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar atendimentos futuros do profissional")
    void deveListarAtendimentosFuturosDoProfissional() {
        // Given
        Long idUsuario = 2L; // ID do usuário profissional
        Pageable pageable = PageRequest.of(0, 10);
        agendamento.setDtFim(LocalDateTime.now().plusDays(2));
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissionalAndDtFimAfterOrderByDtInicioAsc(eq(profissional), any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAtendimentosFuturos(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve listar atendimentos passados do profissional")
    void deveListarAtendimentosPassadosDoProfissional() {
        // Given
        Long idUsuario = 2L; // ID do usuário profissional
        Pageable pageable = PageRequest.of(0, 10);
        // Mantém datas futuras válidas para a entidade, mas simula comportamento de atendimentos passados via mock
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissionalAndDtFimBeforeOrderByDtInicioDesc(eq(profissional), any(LocalDateTime.class), eq(pageable)))
            .thenReturn(page);
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAtendimentosPassados(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado para listagem")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoParaListagem() {
        // Given
        Long idUsuario = 999L;
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarPorUsuario(idUsuario);
        });
        
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado para listagem")
    void deveLancarExcecaoQuandoProfissionalNaoEncontradoParaListagem() {
        // Given
        Long idProfissional = 999L;
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarPorProfissional(idProfissional);
        });
        
        assertEquals("Profissional não encontrado", exception.getMessage());
    }
}