package inkspiration.backend.service.agendamentoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.exception.agendamento.TokenInvalidoException;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.service.AgendamentoService;
import inkspiration.backend.service.DisponibilidadeService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Testes de Cobertura Total")
class AgendamentoServiceCoberturaTotalTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DisponibilidadeService disponibilidadeService;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario;
    private Profissional profissional;
    private Agendamento agendamento;
    private Endereco endereco;

    @BeforeEach
    void setUp() {
        usuario = criarUsuario();
        endereco = criarEndereco();
        profissional = criarProfissional();
        agendamento = criarAgendamento();
    }

    // Método utilitário para definir datas passadas usando reflection
    private void setDataPassadaComReflection(Agendamento agendamento, LocalDateTime dtInicio, LocalDateTime dtFim) throws Exception {
        Field dtInicioField = Agendamento.class.getDeclaredField("dtInicio");
        dtInicioField.setAccessible(true);
        dtInicioField.set(agendamento, dtInicio);
        
        Field dtFimField = Agendamento.class.getDeclaredField("dtFim");
        dtFimField.setAccessible(true);
        dtFimField.set(agendamento, dtFim);
    }

    @Test
    @DisplayName("Deve testar lambda de filtro no atualizarAgendamento - horário conflitante")
    void deveTestarLambdaFiltroHorarioConflitanteAtualizarAgendamento() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        LocalDateTime novoInicio = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        // Agendamento conflitante com status AGENDADO
        Agendamento agendamentoConflitante = criarAgendamento();
        agendamentoConflitante.setIdAgendamento(2L);
        agendamentoConflitante.setStatus(StatusAgendamento.AGENDADO);
        agendamentoConflitante.setDtInicio(novoInicio.plusMinutes(30));
        agendamentoConflitante.setDtFim(novoInicio.plusHours(2));

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario))
            .thenReturn(Arrays.asList(agendamento, agendamentoConflitante));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", novoInicio);
        });

        assertTrue(exception.getMessage().contains("Você já possui outro agendamento nesse horário"));
        
        // Verifica que os lambdas foram executados
        verify(agendamentoRepository).findByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve testar lambda de filtro no atualizarAgendamento - sem conflito com agendamento cancelado")
    void deveTestarLambdaSemConflitoAgendamentoCancelado() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        LocalDateTime novoInicio = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        // Agendamento cancelado - não deve gerar conflito
        Agendamento agendamentoCancelado = criarAgendamento();
        agendamentoCancelado.setIdAgendamento(2L);
        agendamentoCancelado.setStatus(StatusAgendamento.CANCELADO);
        agendamentoCancelado.setDtInicio(novoInicio.plusMinutes(30));
        agendamentoCancelado.setDtFim(novoInicio.plusHours(2));

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario))
            .thenReturn(Arrays.asList(agendamento, agendamentoCancelado));
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any()))
            .thenReturn(Arrays.asList());
        when(agendamentoRepository.save(any())).thenReturn(agendamento);

        // Act
        Agendamento resultado = agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", novoInicio);

        // Assert
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve testar lambda de filtro de agendamentos conflitantes do profissional")
    void deveTestarLambdaFiltroAgendamentosConflitantesProfissional() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        LocalDateTime novoInicio = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        // Agendamento conflitante do profissional
        Agendamento agendamentoConflitanteProfissional = criarAgendamento();
        agendamentoConflitanteProfissional.setIdAgendamento(3L);
        agendamentoConflitanteProfissional.setDtInicio(novoInicio.plusMinutes(30));
        agendamentoConflitanteProfissional.setDtFim(novoInicio.plusHours(2));

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(agendamento));
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any()))
            .thenReturn(Arrays.asList(agendamento, agendamentoConflitanteProfissional));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", novoInicio);
        });

        assertTrue(exception.getMessage().contains("O profissional já possui outro agendamento nesse horário"));
    }

    @Test
    @DisplayName("Deve testar extrairUserIdDoToken com autenticação inválida")
    void deveTestarExtrairUserIdComAutenticacaoInvalida() {
        // Arrange
        Authentication authInvalida = mock(Authentication.class);

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoService.atualizarStatusAgendamentoComAutenticacao(1L, "CONCLUIDO", authInvalida);
        });
    }

    @Test
    @DisplayName("Deve testar extrairUserIdDoToken com userId nulo")
    void deveTestarExtrairUserIdComUserIdNulo() {
        // Arrange
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(null);

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoService.atualizarStatusAgendamentoComAutenticacao(1L, "CONCLUIDO", jwtAuthenticationToken);
        });
    }

    @Test
    @DisplayName("Deve testar atualizarStatusAgendamentoComAutenticacao com scope nulo")
    void deveTestarAtualizarStatusComScopeNulo() throws Exception {
        // Arrange
        Long id = 1L;
        String status = "CONCLUIDO";
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(jwt.getClaimAsString("scope")).thenReturn(null);
        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any())).thenReturn(agendamento);

        // Act
        AgendamentoDTO resultado = agendamentoService.atualizarStatusAgendamentoComAutenticacao(id, status, jwtAuthenticationToken);

        // Assert
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve testar lambda de mapeamento em listarAgendamentosPassados")
    void deveTestarLambdaMapeamentoAgendamentosPassados() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        // Criar agendamento que será elegível para atualização de status
        Agendamento agendamentoPassado = new Agendamento();
        agendamentoPassado.setIdAgendamento(1L);
        agendamentoPassado.setUsuario(usuario);
        agendamentoPassado.setProfissional(profissional);
        agendamentoPassado.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamentoPassado.setDescricao("Tatuagem de dragão");
        agendamentoPassado.setStatus(StatusAgendamento.AGENDADO);
        agendamentoPassado.setValor(new BigDecimal("300.00"));
        
        // Usar reflection para definir datas passadas
        LocalDateTime dtInicioPassado = LocalDateTime.now().minusDays(1);
        LocalDateTime dtFimPassado = LocalDateTime.now().minusHours(1);
        setDataPassadaComReflection(agendamentoPassado, dtInicioPassado, dtFimPassado);
        
        Page<Agendamento> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoPassado));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(), eq(pageable)))
            .thenReturn(agendamentosPage);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoPassado);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAgendamentosPassados(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(agendamentoRepository).save(any()); // Verifica que atualizarStatusSeNecessario foi chamado
    }

    @Test
    @DisplayName("Deve testar lambda de mapeamento em listarAtendimentosFuturos")
    void deveTestarLambdaMapeamentoAtendimentosFuturos() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        Page<Agendamento> atendimentosPage = new PageImpl<>(Arrays.asList(agendamento));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalAndDtFimAfterOrderByDtInicioAsc(eq(profissionalLogado), any(), eq(pageable)))
            .thenReturn(atendimentosPage);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAtendimentosFuturos(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve testar lambda de mapeamento em listarAtendimentosPassados")
    void deveTestarLambdaMapeamentoAtendimentosPassados() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        // Criar atendimento que será elegível para atualização de status
        Agendamento atendimentoPassado = new Agendamento();
        atendimentoPassado.setIdAgendamento(1L);
        atendimentoPassado.setUsuario(usuario);
        atendimentoPassado.setProfissional(profissionalLogado);
        atendimentoPassado.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        atendimentoPassado.setDescricao("Tatuagem de dragão");
        atendimentoPassado.setStatus(StatusAgendamento.AGENDADO);
        atendimentoPassado.setValor(new BigDecimal("300.00"));
        
        // Usar reflection para definir datas passadas
        LocalDateTime dtInicioPassado = LocalDateTime.now().minusDays(1);
        LocalDateTime dtFimPassado = LocalDateTime.now().minusHours(1);
        setDataPassadaComReflection(atendimentoPassado, dtInicioPassado, dtFimPassado);
        
        Page<Agendamento> atendimentosPage = new PageImpl<>(Arrays.asList(atendimentoPassado));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalAndDtFimBeforeOrderByDtInicioDesc(eq(profissionalLogado), any(), eq(pageable)))
            .thenReturn(atendimentosPage);
        when(agendamentoRepository.save(any())).thenReturn(atendimentoPassado);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAtendimentosPassados(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(agendamentoRepository).save(any()); // Verifica que atualizarStatusSeNecessario foi chamado
    }

    @Test
    @DisplayName("Deve testar lambda de filtro de agendamentos conflitantes sem exclusão do próprio ID")
    void deveTestarLambdaFiltroSemExclusaoProprioId() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        LocalDateTime novoInicio = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        // Agendamento conflitante do profissional que NÃO tem o mesmo ID
        Agendamento agendamentoConflitanteProfissional = criarAgendamento();
        agendamentoConflitanteProfissional.setIdAgendamento(3L); // ID diferente
        agendamentoConflitanteProfissional.setDtInicio(novoInicio.plusMinutes(30));
        agendamentoConflitanteProfissional.setDtFim(novoInicio.plusHours(2));

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(agendamento));
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any()))
            .thenReturn(Arrays.asList(agendamento, agendamentoConflitanteProfissional));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", novoInicio);
        });

        assertTrue(exception.getMessage().contains("O profissional já possui outro agendamento nesse horário"));
    }

    @Test
    @DisplayName("Deve testar lambda de filtro sem conflito quando mesmo ID")
    void deveTestarLambdaFiltroSemConflitoMesmoId() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        LocalDateTime novoInicio = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(agendamento));
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any()))
            .thenReturn(Arrays.asList(agendamento)); // Apenas o próprio agendamento
        when(agendamentoRepository.save(any())).thenReturn(agendamento);

        // Act
        Agendamento resultado = agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", novoInicio);

        // Assert
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve testar atualizarAgendamento sem mudança de horário")
    void deveTestarAtualizarAgendamentoSemMudancaHorario() throws Exception {
        // Arrange
        Long id = 1L;
        Long idUsuarioLogado = 1L;
        // Usar exatamente o mesmo horário que já está no agendamento
        LocalDateTime mesmoInicio = agendamento.getDtInicio();

        when(agendamentoRepository.findById(id)).thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.findByUsuario(usuario)).thenReturn(Arrays.asList(agendamento));
        when(disponibilidadeService.isProfissionalDisponivel(any(), any(), any())).thenReturn(true);
        when(agendamentoRepository.findByProfissionalAndPeriod(any(), any(), any()))
            .thenReturn(Arrays.asList(agendamento));
        when(agendamentoRepository.save(any())).thenReturn(agendamento);

        // Act
        Agendamento resultado = agendamentoService.atualizarAgendamento(id, idUsuarioLogado, "pequena", "Nova descrição", mesmoInicio);

        // Assert
        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve testar lambda de filtro para atendimentos futuros")
    void deveTestarLambdaFiltroAtendimentosFuturos() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        Page<Agendamento> atendimentosPage = new PageImpl<>(Arrays.asList(agendamento));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalAndDtFimAfterOrderByDtInicioAsc(eq(profissionalLogado), any(), eq(pageable)))
            .thenReturn(atendimentosPage);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAtendimentosFuturos(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
    }

    @Test
    @DisplayName("Deve testar caso de exceção RuntimeException no catch de PDF")
    void deveTestarExcecaoRuntimeExceptionCatchPDF() throws Exception {
        // Arrange
        Integer ano = 2024;
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        // Mockar para lançar uma RuntimeException qualquer (não relacionada com agendamentos vazios)
        when(agendamentoRepository.findByUsuarioIdAndStatusAndAno(1L, StatusAgendamento.CONCLUIDO, ano))
            .thenThrow(new RuntimeException("Erro de conexão com banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.exportarAgendamentosPDFComAutenticacao(ano, jwtAuthenticationToken);
        });

        assertTrue(exception.getMessage().contains("Erro ao gerar PDF"));
    }

    @Test
    @DisplayName("Deve testar caso de exceção RuntimeException no catch de PDF para atendimentos")
    void deveTestarExcecaoRuntimeExceptionCatchPDFAtendimentos() throws Exception {
        // Arrange
        Integer ano = 2024;
        Integer mes = 6;
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(2L);
        when(usuarioRepository.findById(2L)).thenThrow(new RuntimeException("Erro de conexão com banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.exportarAtendimentosPDFComAutenticacao(ano, mes, jwtAuthenticationToken);
        });

        assertTrue(exception.getMessage().contains("Erro ao gerar PDF"));
    }

    @Test
    @DisplayName("Deve testar atualizarStatusSeNecessario quando status já é CONCLUIDO")
    void deveTestarAtualizarStatusSeNecessarioJaConcluido() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Agendamento agendamentoConcluido = criarAgendamento();
        agendamentoConcluido.setStatus(StatusAgendamento.CONCLUIDO);
        // Usar datas futuras para evitar validação de data passada
        agendamentoConcluido.setDtInicio(LocalDateTime.now().plusDays(1));
        agendamentoConcluido.setDtFim(LocalDateTime.now().plusDays(1).plusHours(2));
        
        Page<Agendamento> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoConcluido));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(), eq(pageable)))
            .thenReturn(agendamentosPage);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAgendamentosPassados(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        // Não deve salvar porque o status já é CONCLUIDO
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve testar atualizarStatusSeNecessario quando agendamento ainda não terminou")
    void deveTestarAtualizarStatusSeNecessarioAindaNaoTerminou() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        Agendamento agendamentoFuturo = criarAgendamento();
        agendamentoFuturo.setStatus(StatusAgendamento.AGENDADO);
        // Usar datas futuras para evitar validação de data passada
        agendamentoFuturo.setDtInicio(LocalDateTime.now().plusDays(1));
        agendamentoFuturo.setDtFim(LocalDateTime.now().plusDays(1).plusHours(2)); // Ainda não terminou
        
        Page<Agendamento> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoFuturo));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(eq(usuario), any(), eq(pageable)))
            .thenReturn(agendamentosPage);

        // Act
        Page<AgendamentoCompletoDTO> resultado = agendamentoService.listarAgendamentosPassados(idUsuario, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        // Não deve salvar porque ainda não terminou
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarPorUsuarioComValidacao")
    void deveTestarCatchListarPorUsuarioComValidacao() {
        // Arrange
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        when(usuarioRepository.findById(idUsuario)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarPorUsuarioComValidacao(idUsuario, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarPorProfissionalComValidacao")
    void deveTestarCatchListarPorProfissionalComValidacao() {
        // Arrange
        Long idProfissional = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        
        when(profissionalRepository.findById(idProfissional)).thenThrow(new RuntimeException("Profissional não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarPorProfissionalComValidacao(idProfissional, pageable);
        });

        assertEquals("Profissional não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em excluirAgendamentoComValidacao")
    void deveTestarCatchExcluirAgendamentoComValidacao() {
        // Arrange
        Long id = 1L;
        
        when(agendamentoRepository.findById(id)).thenThrow(new RuntimeException("Agendamento não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.excluirAgendamentoComValidacao(id);
        });

        assertEquals("Agendamento não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarMeusAgendamentosComAutenticacao")
    void deveTestarCatchListarMeusAgendamentosComAutenticacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAgendamentosComAutenticacao(jwtAuthenticationToken, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarMeusAgendamentosFuturosComAutenticacao")
    void deveTestarCatchListarMeusAgendamentosFuturosComAutenticacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(jwtAuthenticationToken, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarMeusAgendamentosPassadosComAutenticacao")
    void deveTestarCatchListarMeusAgendamentosPassadosComAutenticacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(jwtAuthenticationToken, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarMeusAtendimentosFuturosComAutenticacao")
    void deveTestarCatchListarMeusAtendimentosFuturosComAutenticacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(jwtAuthenticationToken, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em listarMeusAtendimentosPassadosComAutenticacao")
    void deveTestarCatchListarMeusAtendimentosPassadosComAutenticacao() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(jwtAuthenticationToken, pageable);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar caso de exceção catch em buscarPorIdComValidacao")
    void deveTestarCatchBuscarPorIdComValidacao() {
        // Arrange
        Long id = 1L;
        
        when(agendamentoRepository.findById(id)).thenThrow(new RuntimeException("Erro de acesso ao banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.buscarPorIdComValidacao(id);
        });

        assertEquals("Agendamento não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve testar catch em atualizarAgendamentoComAutenticacao")
    void deveTestarCatchAtualizarAgendamentoComAutenticacao() throws Exception {
        // Arrange
        Long id = 1L;
        AgendamentoUpdateDTO request = new AgendamentoUpdateDTO();
        request.setTipoServico("pequena");
        request.setDescricao("Nova descrição");
        request.setDtInicio(LocalDateTime.now().plusDays(1));
        
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(1L);
        when(agendamentoRepository.findById(id)).thenThrow(new RuntimeException("Erro de acesso"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.atualizarAgendamentoComAutenticacao(id, request, jwtAuthenticationToken);
        });

        assertEquals("Erro de acesso", exception.getMessage());
    }

    @Test
    @DisplayName("Deve gerar PDF de agendamentos com sucesso")
    void deveGerarPDFAgendamentosComSucesso() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Integer ano = 2024;
        
        List<Agendamento> agendamentos = Arrays.asList(
            criarAgendamentoParaPDF(1L, "Cliente 1", "Tatuagem pequena"),
            criarAgendamentoParaPDF(2L, "Cliente 2", "Tatuagem média")
        );

        when(agendamentoRepository.findByUsuarioIdAndStatusAndAno(idUsuario, StatusAgendamento.CONCLUIDO, ano))
            .thenReturn(agendamentos);

        // Act
        byte[] resultado = agendamentoService.gerarPDFAgendamentos(idUsuario, ano);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        verify(agendamentoRepository).findByUsuarioIdAndStatusAndAno(idUsuario, StatusAgendamento.CONCLUIDO, ano);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há agendamentos para PDF")
    void deveLancarExcecaoQuandoNaoHaAgendamentosParaPDF() {
        // Arrange
        Long idUsuario = 1L;
        Integer ano = 2024;

        when(agendamentoRepository.findByUsuarioIdAndStatusAndAno(idUsuario, StatusAgendamento.CONCLUIDO, ano))
            .thenReturn(Arrays.asList());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            agendamentoService.gerarPDFAgendamentos(idUsuario, ano);
        });

        assertTrue(exception.getMessage().contains("Nenhum agendamento concluído encontrado para o ano " + ano));
    }

    @Test
    @DisplayName("Deve gerar PDF de atendimentos com sucesso")
    void deveGerarPDFAtendimentosComSucesso() throws Exception {
        // Arrange
        Long idUsuario = 2L;
        Integer ano = 2024;
        Integer mes = 6;
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        List<Agendamento> atendimentos = Arrays.asList(
            criarAgendamentoParaPDF(1L, "Cliente 1", "Tatuagem pequena"),
            criarAgendamentoParaPDF(2L, "Cliente 2", "Tatuagem média")
        );

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndAnoMes(
            profissionalLogado.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes))
            .thenReturn(atendimentos);

        // Act
        byte[] resultado = agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
        verify(usuarioRepository).findById(idUsuario);
        verify(profissionalRepository).findByUsuario(usuarioProfissional);
        verify(agendamentoRepository).findByProfissionalIdAndStatusAndAnoMes(
            profissionalLogado.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado para PDF atendimentos")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoParaPDFAtendimentos() {
        // Arrange
        Long idUsuario = 1L;
        Integer ano = 2024;
        Integer mes = 6;

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado para PDF atendimentos")
    void deveLancarExcecaoQuandoProfissionalNaoEncontradoParaPDFAtendimentos() {
        // Arrange
        Long idUsuario = 2L;
        Integer ano = 2024;
        Integer mes = 6;
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);
        });

        assertEquals("Profissional não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há atendimentos para PDF")
    void deveLancarExcecaoQuandoNaoHaAtendimentosParaPDF() {
        // Arrange
        Long idUsuario = 2L;
        Integer ano = 2024;
        Integer mes = 6;
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndAnoMes(
            profissionalLogado.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes))
            .thenReturn(Arrays.asList());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);
        });

        assertTrue(exception.getMessage().contains("Nenhum atendimento concluído encontrado para 06/2024"));
    }

    @Test
    @DisplayName("Deve testar geração de PDF com agendamentos com valores nulos")
    void deveTestarGeracaoPDFComAgendamentosComValoresNulos() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Integer ano = 2024;
        
        Agendamento agendamentoComDadosNulos = new Agendamento();
        agendamentoComDadosNulos.setIdAgendamento(1L);
        agendamentoComDadosNulos.setUsuario(usuario);
        agendamentoComDadosNulos.setProfissional(profissional);
        agendamentoComDadosNulos.setStatus(StatusAgendamento.CONCLUIDO);
        // Deixar outros campos nulos para testar as validações
        
        List<Agendamento> agendamentos = Arrays.asList(agendamentoComDadosNulos);

        when(agendamentoRepository.findByUsuarioIdAndStatusAndAno(idUsuario, StatusAgendamento.CONCLUIDO, ano))
            .thenReturn(agendamentos);

        // Act
        byte[] resultado = agendamentoService.gerarPDFAgendamentos(idUsuario, ano);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    @DisplayName("Deve testar geração de PDF atendimentos com dados nulos")
    void deveTestarGeracaoPDFAtendimentosComDadosNulos() throws Exception {
        // Arrange
        Long idUsuario = 2L;
        Integer ano = 2024;
        Integer mes = 6;
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        Agendamento atendimentoComDadosNulos = new Agendamento();
        atendimentoComDadosNulos.setIdAgendamento(1L);
        atendimentoComDadosNulos.setUsuario(usuario);
        atendimentoComDadosNulos.setProfissional(profissionalLogado);
        atendimentoComDadosNulos.setStatus(StatusAgendamento.CONCLUIDO);
        // Deixar outros campos nulos para testar as validações
        
        List<Agendamento> atendimentos = Arrays.asList(atendimentoComDadosNulos);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndAnoMes(
            profissionalLogado.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes))
            .thenReturn(atendimentos);

        // Act
        byte[] resultado = agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    @Test
    @DisplayName("Deve testar geração de PDF com mês maior que 12")
    void deveTestarGeracaoPDFComMesMaiorQue12() throws Exception {
        // Arrange
        Long idUsuario = 2L;
        Integer ano = 2024;
        Integer mes = 15; // Mês inválido para testar o case do array de meses
        
        Usuario usuarioProfissional = criarUsuario();
        usuarioProfissional.setIdUsuario(2L);
        
        Profissional profissionalLogado = criarProfissional();
        profissionalLogado.setIdProfissional(2L);
        profissionalLogado.setUsuario(usuarioProfissional);
        
        List<Agendamento> atendimentos = Arrays.asList(criarAgendamentoParaPDF(1L, "Cliente 1", "Tatuagem pequena"));

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioProfissional));
        when(profissionalRepository.findByUsuario(usuarioProfissional)).thenReturn(Optional.of(profissionalLogado));
        when(agendamentoRepository.findByProfissionalIdAndStatusAndAnoMes(
            profissionalLogado.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes))
            .thenReturn(atendimentos);

        // Act
        byte[] resultado = agendamentoService.gerarPDFAtendimentos(idUsuario, ano, mes);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.length > 0);
    }

    // Método auxiliar para criar agendamentos para testes de PDF
    private Agendamento criarAgendamentoParaPDF(Long id, String nomeCliente, String descricao) {
        Usuario cliente = new Usuario();
        cliente.setIdUsuario(id);
        cliente.setNome(nomeCliente);
        cliente.setEmail(nomeCliente.toLowerCase().replace(" ", "") + "@test.com");
        cliente.setCpf("12345678901");

        Agendamento agendamento = new Agendamento();
        agendamento.setIdAgendamento(id);
        agendamento.setUsuario(cliente);
        agendamento.setProfissional(profissional);
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao(descricao);
        agendamento.setDtInicio(LocalDateTime.now().plusDays(50));
        agendamento.setDtFim(LocalDateTime.now().plusDays(50).plusHours(2));
        agendamento.setValor(new BigDecimal("250.00"));
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        return agendamento;
    }

    // Métodos auxiliares
    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setCpf("12345678901");
        return usuario;
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01234-567");
        return endereco;
    }

    private Profissional criarProfissional() {
        Usuario usuarioProfissional = new Usuario();
        usuarioProfissional.setIdUsuario(2L);
        usuarioProfissional.setNome("Maria Profissional");
        usuarioProfissional.setEmail("maria@test.com");
        usuarioProfissional.setCpf("98765432109");

        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuarioProfissional);
        profissional.setEndereco(endereco);
        return profissional;
    }

    private Agendamento criarAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setIdAgendamento(1L);
        agendamento.setUsuario(usuario);
        agendamento.setProfissional(profissional);
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao("Tatuagem de dragão");
        agendamento.setDtInicio(LocalDateTime.now().plusDays(1));
        agendamento.setDtFim(LocalDateTime.now().plusDays(1).plusHours(2));
        agendamento.setValor(new BigDecimal("300.00"));
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        return agendamento;
    }
} 