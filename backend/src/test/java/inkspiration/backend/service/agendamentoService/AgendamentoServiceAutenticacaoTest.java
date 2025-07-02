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
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

class AgendamentoServiceAutenticacaoTest {

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
    private Authentication authentication;

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
        
        // Setup authentication mock
        setupAuthenticationMock();
    }

    private void setupAuthenticationMock() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(1L);
        
        authentication = new JwtAuthenticationToken(jwt);
    }

    @Test
    @DisplayName("Deve criar agendamento com validação de request DTO")
    void deveCriarAgendamentoComValidacaoDeRequestDTO() throws Exception {
        // Given
        AgendamentoRequestDTO request = new AgendamentoRequestDTO();
        request.setIdUsuario(1L);
        request.setIdProfissional(1L);
        request.setTipoServico("pequena");
        request.setDescricao("Descrição de teste para agendamento");
        request.setDtInicio(LocalDate.now().plusDays(2).atTime(14, 0));
        request.setValor(new BigDecimal("150.00"));
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.existsConflitingSchedule(any(), any(), any())).thenReturn(false);
        when(agendamentoRepository.save(any())).thenReturn(agendamento);
        
        // When
        AgendamentoDTO resultado = agendamentoService.criarAgendamentoComValidacao(request);
        
        // Then
        assertNotNull(resultado);
        assertEquals(agendamento.getIdAgendamento(), resultado.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID com validação")
    void deveBuscarAgendamentoPorIdComValidacao() {
        // Given
        Long id = 1L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When
        AgendamentoDTO resultado = agendamentoService.buscarPorIdComValidacao(id);
        
        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário com validação e paginação")
    void deveListarAgendamentosPorUsuarioComValidacaoEPaginacao() {
        // Given
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        
        // When
        List<AgendamentoDTO> resultado = agendamentoService.listarPorUsuarioComValidacao(idUsuario, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional com validação e paginação")
    void deveListarAgendamentosPorProfissionalComValidacaoEPaginacao() {
        // Given
        Long idProfissional = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissional(profissional, pageable)).thenReturn(page);
        
        // When
        List<AgendamentoDTO> resultado = agendamentoService.listarPorProfissionalComValidacao(idProfissional, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve atualizar agendamento com autenticação")
    void deveAtualizarAgendamentoComAutenticacao() throws Exception {
        // Given
        Long id = 1L;
        AgendamentoUpdateDTO request = new AgendamentoUpdateDTO();
        request.setTipoServico("media");
        request.setDescricao("Nova descrição para o agendamento");
        request.setDtInicio(LocalDate.now().plusDays(3).atTime(15, 0));
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any())).thenReturn(Arrays.asList());
        when(agendamentoRepository.save(any())).thenReturn(agendamento);
        
        // When
        AgendamentoDTO resultado = agendamentoService.atualizarAgendamentoComAutenticacao(id, request, authentication);
        
        // Then
        assertNotNull(resultado);
        assertEquals(agendamento.getIdAgendamento(), resultado.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve excluir agendamento com validação")
    void deveExcluirAgendamentoComValidacao() {
        // Given
        Long id = 1L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        assertDoesNotThrow(() -> {
            agendamentoService.excluirAgendamentoComValidacao(id);
        });
        
        verify(agendamentoRepository).delete(agendamento);
    }

    @Test
    @DisplayName("Deve listar meus agendamentos com autenticação")
    void deveListarMeusAgendamentosComAutenticacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        
        // When
        Page<AgendamentoDTO> resultado = agendamentoService.listarMeusAgendamentosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve atualizar status do agendamento com autenticação")
    void deveAtualizarStatusDoAgendamentoComAutenticacao() {
        // Given
        Long id = 1L;
        String status = "CANCELADO";
        agendamento.setDtInicio(LocalDateTime.now().plusDays(5)); // Mais de 3 dias
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any())).thenReturn(agendamento);
        
        // When
        AgendamentoDTO resultado = agendamentoService.atualizarStatusAgendamentoComAutenticacao(id, status, authentication);
        
        // Then
        assertNotNull(resultado);
        assertEquals(agendamento.getIdAgendamento(), resultado.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve listar meus agendamentos futuros com autenticação")
    void deveListarMeusAgendamentosFuturosComAutenticacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        agendamento.setDtFim(LocalDateTime.now().plusDays(2));
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimAfterOrderByDtInicioAsc(eq(usuario), any(), eq(pageable)))
            .thenReturn(page);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve listar meus agendamentos passados com autenticação")
    void deveListarMeusAgendamentosPassadosComAutenticacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        // Mantém datas futuras válidas para a entidade, simula comportamento de agendamentos passados via mock
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(), eq(pageable)))
            .thenReturn(page);
        when(agendamentoRepository.save(any())).thenReturn(agendamento);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve listar meus atendimentos futuros com autenticação")
    void deveListarMeusAtendimentosFuturosComAutenticacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        agendamento.setDtFim(LocalDateTime.now().plusDays(2));
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findByUsuario(usuario)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissionalAndDtFimAfterOrderByDtInicioAsc(eq(profissional), any(), eq(pageable)))
            .thenReturn(page);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve listar meus atendimentos passados com autenticação")
    void deveListarMeusAtendimentosPassadosComAutenticacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        // Mantém datas futuras válidas para a entidade, simula comportamento de atendimentos passados via mock
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.findByUsuario(usuario)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.findByProfissionalAndDtFimBeforeOrderByDtInicioDesc(eq(profissional), any(), eq(pageable)))
            .thenReturn(page);
        when(agendamentoRepository.save(any())).thenReturn(agendamento);
        
        // When
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token inválido")
    void deveLancarExcecaoQuandoTokenInvalido() {
        // Given
        Authentication authInvalido = mock(Authentication.class);
        when(authInvalido.getPrincipal()).thenReturn("string_invalida");
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAgendamentosComAutenticacao(authInvalido, PageRequest.of(0, 10));
        });
    }

    @Test
    @DisplayName("Deve extrair userId do token JWT corretamente")
    void deveExtrairUserIdDoTokenJWTCorretamente() {
        // Given - token configurado no setUp
        Pageable pageable = PageRequest.of(0, 10);
        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        Page<Agendamento> page = new PageImpl<>(agendamentos, pageable, 1);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuario(usuario, pageable)).thenReturn(page);
        
        // When
        Page<AgendamentoDTO> resultado = agendamentoService.listarMeusAgendamentosComAutenticacao(authentication, pageable);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(usuarioRepository).findById(1L);
    }
}