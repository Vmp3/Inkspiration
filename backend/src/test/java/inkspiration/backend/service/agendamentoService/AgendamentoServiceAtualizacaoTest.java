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
import inkspiration.backend.exception.agendamento.AgendamentoNaoAutorizadoException;
import inkspiration.backend.exception.agendamento.CancelamentoNaoPermitidoException;
import inkspiration.backend.dto.AgendamentoCompletoDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import org.springframework.data.domain.Page;

class AgendamentoServiceAtualizacaoTest {

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
        agendamento.setDtInicio(LocalDateTime.now().plusDays(5).withHour(14).withMinute(0));
        agendamento.setDtFim(LocalDateTime.now().plusDays(5).withHour(16).withMinute(0));
        agendamento.setValor(new BigDecimal("150.00"));
        agendamento.setStatus(StatusAgendamento.AGENDADO);
    }

    @Test
    @DisplayName("Deve atualizar agendamento com sucesso")
    void deveAtualizarAgendamentoComSucesso() throws Exception {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String tipoServico = "media";
        String descricao = "Nova descrição";
        LocalDateTime dtInicio = LocalDate.now().plusDays(3).atTime(15, 0);
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList());
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any())).thenReturn(Arrays.asList());
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        
        // When
        Agendamento resultado = agendamentoService.atualizarAgendamento(
            id, idUsuarioLogado, tipoServico, descricao, dtInicio);
        
        // Then
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não autorizado para atualizar")
    void deveLancarExcecaoQuandoUsuarioNaoAutorizadoParaAtualizar() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 999L; // ID diferente do dono do agendamento
        String tipoServico = "media";
        String descricao = "Nova descrição";
        LocalDateTime dtInicio = LocalDate.now().plusDays(3).atTime(15, 0);
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamento(
                id, idUsuarioLogado, tipoServico, descricao, dtInicio);
        });
        
        assertTrue(exception.getMessage().contains("Não autorizado"));
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de serviço inválido na atualização")
    void deveLancarExcecaoParaTipoServicoInvalidoNaAtualizacao() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String tipoServico = "invalido";
        String descricao = "Nova descrição para o agendamento";
        LocalDateTime dtInicio = LocalDate.now().plusDays(3).atTime(15, 0);
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamento(
                id, idUsuarioLogado, tipoServico, descricao, dtInicio);
        });
        
        assertTrue(exception.getMessage().contains("Tipo de serviço inválido"));
    }

    @Test
    @DisplayName("Deve excluir agendamento com sucesso")
    void deveExcluirAgendamentoComSucesso() {
        // Given
        Long id = 1L;
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When
        agendamentoService.excluirAgendamento(id);
        
        // Then
        verify(agendamentoRepository).delete(agendamento);
    }

    @Test
    @DisplayName("Deve atualizar status para cancelado com sucesso")
    void deveAtualizarStatusParaCanceladoComSucesso() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String status = "CANCELADO";
        List<String> roles = Arrays.asList("ROLE_USER");
        
        // Agendamento com mais de 3 dias de antecedência
        agendamento.setDtInicio(LocalDateTime.now().plusDays(5));
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        
        // When
        Agendamento resultado = agendamentoService.atualizarStatusAgendamento(
            id, idUsuarioLogado, status, roles);
        
        // Then
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve permitir profissional atualizar status do seu agendamento")
    void devePermitirProfissionalAtualizarStatusDoSeuAgendamento() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 2L; // ID do profissional
        String status = "CONCLUIDO";
        List<String> roles = Arrays.asList("ROLE_PROF");
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findById(idUsuarioLogado)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissional));
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);
        
        // When
        Agendamento resultado = agendamentoService.atualizarStatusAgendamento(
            id, idUsuarioLogado, status, roles);
        
        // Then
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não autorizado para alterar status")
    void deveLancarExcecaoQuandoUsuarioNaoAutorizadoParaAlterarStatus() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 999L; // ID diferente
        String status = "CANCELADO";
        List<String> roles = Arrays.asList("ROLE_USER");
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarStatusAgendamento(
                id, idUsuarioLogado, status, roles);
        });
        
        assertTrue(exception.getMessage().contains("Não autorizado"));
    }

    @Test
    @DisplayName("Deve lançar exceção para status inválido")
    void deveLancarExcecaoParaStatusInvalido() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String status = "STATUS_INVALIDO";
        List<String> roles = Arrays.asList("ROLE_USER");
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarStatusAgendamento(
                id, idUsuarioLogado, status, roles);
        });
        
        assertTrue(exception.getMessage().contains("Status inválido"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar agendamento já cancelado")
    void deveLancarExcecaoAoTentarCancelarAgendamentoJaCancelado() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String status = "CANCELADO";
        List<String> roles = Arrays.asList("ROLE_USER");
        
        agendamento.setStatus(StatusAgendamento.CANCELADO); // Já cancelado
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarStatusAgendamento(
                id, idUsuarioLogado, status, roles);
        });
        
        assertTrue(exception.getMessage().contains("Somente agendamentos com status 'Agendado' podem ser cancelados"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar com menos de 3 dias de antecedência")
    void deveLancarExcecaoAoTentarCancelarComMenosDe3DiasDeAntecedencia() {
        // Given
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        String status = "CANCELADO";
        List<String> roles = Arrays.asList("ROLE_USER");
        
        // Agendamento com menos de 3 dias de antecedência
        agendamento.setDtInicio(LocalDateTime.now().plusDays(2));
        
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarStatusAgendamento(
                id, idUsuarioLogado, status, roles);
        });
        
        assertTrue(exception.getMessage().contains("O cancelamento só é permitido com no mínimo 3 dias de antecedência"));
    }

    @Test
    @DisplayName("Não deve atualizar status quando agendamento já está concluído")
    void naoDeveAtualizarStatusQuandoAgendamentoJaEstaConcluido() {
        // Given
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        // Mantém datas futuras válidas para a entidade, simula comportamento via mock
        
        // When
        Long idUsuario = 1L;
    when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(agendamento)));
        
        agendamentoService.listarAgendamentosPassados(idUsuario, 
            org.springframework.data.domain.PageRequest.of(0, 10));
        
        // Then
        verify(agendamentoRepository, never()).save(agendamento);
    }

    @Test
    @DisplayName("Não deve atualizar status quando agendamento ainda não passou do horário")
    void naoDeveAtualizarStatusQuandoAgendamentoAindaNaoPassouDoHorario() {
        // Given
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        // Datas já são futuras por padrão no setUp, mantém assim
        
        // When
        Long idUsuario = 1L;
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimAfterOrderByDtInicioAsc(eq(usuario), any(), any()))
            .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(agendamento)));
        
        agendamentoService.listarAgendamentosFuturos(idUsuario, 
            org.springframework.data.domain.PageRequest.of(0, 10));
        
        // Then
        verify(agendamentoRepository, never()).save(agendamento);
    }
}