package inkspiration.backend.controller.agendamentoController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import inkspiration.backend.controller.AgendamentoController;
import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.service.AgendamentoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoController - Testes de Integração")
class AgendamentoControllerIntegracaoTest {

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AgendamentoController agendamentoController;

    private AgendamentoRequestDTO agendamentoRequestDTO;
    private AgendamentoUpdateDTO agendamentoUpdateDTO;
    private AgendamentoDTO agendamentoDTO;
    private AgendamentoCompletoDTO agendamentoCompletoDTO;

    @BeforeEach
    void setUp() {
        agendamentoRequestDTO = criarAgendamentoRequestDTO();
        agendamentoUpdateDTO = criarAgendamentoUpdateDTO();
        agendamentoDTO = criarAgendamentoDTO();
        agendamentoCompletoDTO = criarAgendamentoCompletoDTO();
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de criação e busca de agendamento")
    void deveRealizarFluxoCompletoDecriacaoEBuscaDeAgendamento() {
        // Arrange
        Long idAgendamento = 1L;
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenReturn(agendamentoDTO);
        when(agendamentoService.buscarPorIdComValidacao(idAgendamento))
            .thenReturn(agendamentoDTO);

        // Act - Criar agendamento
        ResponseEntity<AgendamentoDTO> criacaoResponse = agendamentoController.criarAgendamento(agendamentoRequestDTO);
        
        // Act - Buscar agendamento criado
        ResponseEntity<AgendamentoDTO> buscaResponse = agendamentoController.buscarPorId(idAgendamento);

        // Assert
        assertEquals(HttpStatus.CREATED, criacaoResponse.getStatusCode());
        assertEquals(HttpStatus.OK, buscaResponse.getStatusCode());
        assertEquals(agendamentoDTO.getIdAgendamento(), criacaoResponse.getBody().getIdAgendamento());
        assertEquals(agendamentoDTO.getIdAgendamento(), buscaResponse.getBody().getIdAgendamento());

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
        verify(agendamentoService).buscarPorIdComValidacao(idAgendamento);
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de criação, atualização e busca")
    void deveRealizarFluxoCompletoDecriacaoAtualizacaoEBusca() {
        // Arrange
        Long idAgendamento = 1L;
        AgendamentoDTO agendamentoAtualizado = criarAgendamentoDTOAtualizado();
        
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenReturn(agendamentoDTO);
        when(agendamentoService.atualizarAgendamentoComAutenticacao(idAgendamento, agendamentoUpdateDTO, authentication))
            .thenReturn(agendamentoAtualizado);
        when(agendamentoService.buscarPorIdComValidacao(idAgendamento))
            .thenReturn(agendamentoAtualizado);

        // Act - Criar agendamento
        ResponseEntity<AgendamentoDTO> criacaoResponse = agendamentoController.criarAgendamento(agendamentoRequestDTO);
        
        // Act - Atualizar agendamento
        ResponseEntity<AgendamentoDTO> atualizacaoResponse = agendamentoController.atualizarAgendamento(idAgendamento, agendamentoUpdateDTO, authentication);
        
        // Act - Buscar agendamento atualizado
        ResponseEntity<AgendamentoDTO> buscaResponse = agendamentoController.buscarPorId(idAgendamento);

        // Assert
        assertEquals(HttpStatus.CREATED, criacaoResponse.getStatusCode());
        assertEquals(HttpStatus.OK, atualizacaoResponse.getStatusCode());
        assertEquals(HttpStatus.OK, buscaResponse.getStatusCode());
        assertEquals("Tatuagem de dragão atualizada com mais detalhes e sombreamento realista", atualizacaoResponse.getBody().getDescricao());
        assertEquals("Tatuagem de dragão atualizada com mais detalhes e sombreamento realista", buscaResponse.getBody().getDescricao());

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
        verify(agendamentoService).atualizarAgendamentoComAutenticacao(idAgendamento, agendamentoUpdateDTO, authentication);
        verify(agendamentoService).buscarPorIdComValidacao(idAgendamento);
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de listagem por usuário e profissional")
    void deveRealizarFluxoCompletoDeListagemPorUsuarioEProfissional() {
        // Arrange
        Long idUsuario = 1L;
        Long idProfissional = 2L;
        List<AgendamentoDTO> agendamentosUsuario = Arrays.asList(agendamentoDTO);
        List<AgendamentoDTO> agendamentosProfissional = Arrays.asList(agendamentoDTO);

        when(agendamentoService.listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class)))
            .thenReturn(agendamentosUsuario);
        when(agendamentoService.listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class)))
            .thenReturn(agendamentosProfissional);

        // Act
        ResponseEntity<List<AgendamentoDTO>> responseUsuario = agendamentoController.listarPorUsuario(idUsuario, 0, 10);
        ResponseEntity<List<AgendamentoDTO>> responseProfissional = agendamentoController.listarPorProfissional(idProfissional, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, responseUsuario.getStatusCode());
        assertEquals(HttpStatus.OK, responseProfissional.getStatusCode());
        assertEquals(1, responseUsuario.getBody().size());
        assertEquals(1, responseProfissional.getBody().size());

        verify(agendamentoService).listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class));
        verify(agendamentoService).listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de gerenciamento de status")
    void deveRealizarFluxoCompletoDeGerenciamentoDeStatus() {
        // Arrange
        Long idAgendamento = 1L;
        String novoStatus = "CONCLUIDO";
        AgendamentoDTO agendamentoConfirmado = criarAgendamentoDTOComStatus(StatusAgendamento.CONCLUIDO);

        when(agendamentoService.buscarPorIdComValidacao(idAgendamento))
            .thenReturn(agendamentoDTO);
        when(agendamentoService.atualizarStatusAgendamentoComAutenticacao(idAgendamento, novoStatus, authentication))
            .thenReturn(agendamentoConfirmado);

        // Act - Buscar agendamento inicial
        ResponseEntity<AgendamentoDTO> buscaInicialResponse = agendamentoController.buscarPorId(idAgendamento);
        
        // Act - Atualizar status
        ResponseEntity<AgendamentoDTO> atualizacaoStatusResponse = agendamentoController.atualizarStatusAgendamento(idAgendamento, novoStatus, authentication);

        // Assert
        assertEquals(HttpStatus.OK, buscaInicialResponse.getStatusCode());
        assertEquals(HttpStatus.OK, atualizacaoStatusResponse.getStatusCode());
        assertEquals(StatusAgendamento.AGENDADO, buscaInicialResponse.getBody().getStatus());
        assertEquals(StatusAgendamento.CONCLUIDO, atualizacaoStatusResponse.getBody().getStatus());

        verify(agendamentoService).buscarPorIdComValidacao(idAgendamento);
        verify(agendamentoService).atualizarStatusAgendamentoComAutenticacao(idAgendamento, novoStatus, authentication);
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de listagem personalizada")
    void deveRealizarFluxoCompletoDeListagemPersonalizada() {
        // Arrange
        Page<AgendamentoDTO> meusAgendamentos = new PageImpl<>(Arrays.asList(agendamentoDTO));
        Page<AgendamentoCompletoDTO> agendamentosFuturos = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        Page<AgendamentoCompletoDTO> agendamentosPassados = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));

        when(agendamentoService.listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(meusAgendamentos);
        when(agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(agendamentosFuturos);
        when(agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(agendamentosPassados);

        // Act
        ResponseEntity<Page<AgendamentoDTO>> responseMeus = agendamentoController.listarMeusAgendamentos(authentication, 0, 10);
        ResponseEntity<Page<AgendamentoCompletoDTO>> responseFuturos = agendamentoController.listarMeusAgendamentosFuturos(authentication, 0, 10);
        ResponseEntity<Page<AgendamentoCompletoDTO>> responsePassados = agendamentoController.listarMeusAgendamentosPassados(authentication, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, responseMeus.getStatusCode());
        assertEquals(HttpStatus.OK, responseFuturos.getStatusCode());
        assertEquals(HttpStatus.OK, responsePassados.getStatusCode());
        assertEquals(1, responseMeus.getBody().getContent().size());
        assertEquals(1, responseFuturos.getBody().getContent().size());
        assertEquals(1, responsePassados.getBody().getContent().size());

        verify(agendamentoService).listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class));
        verify(agendamentoService).listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
        verify(agendamentoService).listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de atendimentos para profissional")
    void deveRealizarFluxoCompletoDeAtendimentosParaProfissional() {
        // Arrange
        Page<AgendamentoCompletoDTO> atendimentosFuturos = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        Page<AgendamentoCompletoDTO> atendimentosPassados = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));

        when(agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(atendimentosFuturos);
        when(agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(atendimentosPassados);

        // Act
        ResponseEntity<Page<AgendamentoCompletoDTO>> responseFuturos = agendamentoController.listarMeusAtendimentosFuturos(authentication, 0, 10);
        ResponseEntity<Page<AgendamentoCompletoDTO>> responsePassados = agendamentoController.listarMeusAtendimentosPassados(authentication, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, responseFuturos.getStatusCode());
        assertEquals(HttpStatus.OK, responsePassados.getStatusCode());
        assertEquals(1, responseFuturos.getBody().getContent().size());
        assertEquals(1, responsePassados.getBody().getContent().size());

        verify(agendamentoService).listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
        verify(agendamentoService).listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de exportação de relatórios")
    void deveRealizarFluxoCompletoDeExportacaoDeRelatorios() {
        // Arrange
        Integer ano = 2024;
        Integer mes = 6;
        byte[] pdfAgendamentos = "PDF agendamentos".getBytes();
        byte[] pdfAtendimentos = "PDF atendimentos".getBytes();

        when(agendamentoService.exportarAgendamentosPDFComAutenticacao(ano, authentication))
            .thenReturn(pdfAgendamentos);
        when(agendamentoService.exportarAtendimentosPDFComAutenticacao(ano, mes, authentication))
            .thenReturn(pdfAtendimentos);

        // Act
        ResponseEntity<?> responseAgendamentos = agendamentoController.exportarAgendamentosPDF(ano, authentication);
        ResponseEntity<?> responseAtendimentos = agendamentoController.exportarAtendimentosPDF(ano, mes, authentication);

        // Assert
        assertEquals(HttpStatus.OK, responseAgendamentos.getStatusCode());
        assertEquals(HttpStatus.OK, responseAtendimentos.getStatusCode());
        assertEquals(pdfAgendamentos, responseAgendamentos.getBody());
        assertEquals(pdfAtendimentos, responseAtendimentos.getBody());
        assertTrue(responseAgendamentos.getHeaders().getFirst("Content-Disposition").contains("agendamentos-2024.pdf"));
        assertTrue(responseAtendimentos.getHeaders().getFirst("Content-Disposition").contains("atendimentos-06-2024.pdf"));

        verify(agendamentoService).exportarAgendamentosPDFComAutenticacao(ano, authentication);
        verify(agendamentoService).exportarAtendimentosPDFComAutenticacao(ano, mes, authentication);
    }

    @Test
    @DisplayName("Deve realizar fluxo completo de criação e exclusão")
    void deveRealizarFluxoCompletoDecriacaoEExclusao() {
        // Arrange
        Long idAgendamento = 1L;
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenReturn(agendamentoDTO);
        doNothing().when(agendamentoService).excluirAgendamentoComValidacao(idAgendamento);

        // Act - Criar agendamento
        ResponseEntity<AgendamentoDTO> criacaoResponse = agendamentoController.criarAgendamento(agendamentoRequestDTO);
        
        // Act - Excluir agendamento
        ResponseEntity<String> exclusaoResponse = agendamentoController.excluirAgendamento(idAgendamento);

        // Assert
        assertEquals(HttpStatus.CREATED, criacaoResponse.getStatusCode());
        assertEquals(HttpStatus.OK, exclusaoResponse.getStatusCode());
        assertEquals("Agendamento excluído com sucesso", exclusaoResponse.getBody());

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
        verify(agendamentoService).excluirAgendamentoComValidacao(idAgendamento);
    }

    @Test
    @DisplayName("Deve testar diferentes tamanhos de paginação")
    void deveTestarDiferentesTamanhosDePaginacao() {
        // Arrange
        Long idUsuario = 1L;
        List<AgendamentoDTO> agendamentos = Arrays.asList(agendamentoDTO);
        when(agendamentoService.listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class)))
            .thenReturn(agendamentos);

        // Act - Diferentes configurações de paginação
        ResponseEntity<List<AgendamentoDTO>> response1 = agendamentoController.listarPorUsuario(idUsuario, 0, 5);
        ResponseEntity<List<AgendamentoDTO>> response2 = agendamentoController.listarPorUsuario(idUsuario, 1, 20);
        ResponseEntity<List<AgendamentoDTO>> response3 = agendamentoController.listarPorUsuario(idUsuario, 2, 50);

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        verify(agendamentoService, times(3)).listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve testar sequência completa de mudanças de status")
    void deveTestarSequenciaCompletaDeMudancasDeStatus() {
        // Arrange
        Long idAgendamento = 1L;
        AgendamentoDTO agendamentoCancelado = criarAgendamentoDTOComStatus(StatusAgendamento.CANCELADO);
        AgendamentoDTO agendamentoConcluido = criarAgendamentoDTOComStatus(StatusAgendamento.CONCLUIDO);

        when(agendamentoService.atualizarStatusAgendamentoComAutenticacao(idAgendamento, "CANCELADO", authentication))
            .thenReturn(agendamentoCancelado);
        when(agendamentoService.atualizarStatusAgendamentoComAutenticacao(idAgendamento, "CONCLUIDO", authentication))
            .thenReturn(agendamentoConcluido);

        // Act - Sequência de mudanças de status
        ResponseEntity<AgendamentoDTO> responseCancelado = agendamentoController.atualizarStatusAgendamento(idAgendamento, "CANCELADO", authentication);
        ResponseEntity<AgendamentoDTO> responseConcluido = agendamentoController.atualizarStatusAgendamento(idAgendamento, "CONCLUIDO", authentication);

        // Assert
        assertEquals(HttpStatus.OK, responseCancelado.getStatusCode());
        assertEquals(HttpStatus.OK, responseConcluido.getStatusCode());
        assertEquals(StatusAgendamento.CANCELADO, responseCancelado.getBody().getStatus());
        assertEquals(StatusAgendamento.CONCLUIDO, responseConcluido.getBody().getStatus());

        verify(agendamentoService).atualizarStatusAgendamentoComAutenticacao(idAgendamento, "CANCELADO", authentication);
        verify(agendamentoService).atualizarStatusAgendamentoComAutenticacao(idAgendamento, "CONCLUIDO", authentication);
    }

    // Métodos auxiliares
    private AgendamentoRequestDTO criarAgendamentoRequestDTO() {
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão no braço direito com detalhes em cores e sombreamento");
        dto.setDtInicio(LocalDateTime.now().plusDays(7));
        dto.setValor(new BigDecimal("300.00"));
        dto.setIdProfissional(1L);
        dto.setIdUsuario(2L);
        return dto;
    }

    private AgendamentoUpdateDTO criarAgendamentoUpdateDTO() {
        AgendamentoUpdateDTO dto = new AgendamentoUpdateDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão atualizada com mais detalhes e sombreamento realista");
        dto.setDtInicio(LocalDateTime.now().plusDays(8));
        return dto;
    }

    private AgendamentoDTO criarAgendamentoDTO() {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setIdAgendamento(1L);
        dto.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        dto.setDescricao("Tatuagem de dragão no braço direito com sombreamento em preto e cinza");
        dto.setDtInicio(LocalDateTime.now().plusDays(7));
        dto.setDtFim(LocalDateTime.now().plusDays(7).plusHours(2));
        dto.setValor(new BigDecimal("300.00"));
        dto.setIdProfissional(1L);
        dto.setIdUsuario(2L);
        dto.setStatus(StatusAgendamento.AGENDADO);
        return dto;
    }

    private AgendamentoDTO criarAgendamentoDTOAtualizado() {
        AgendamentoDTO dto = criarAgendamentoDTO();
        dto.setDescricao("Tatuagem de dragão atualizada com mais detalhes e sombreamento realista");
        return dto;
    }

    private AgendamentoDTO criarAgendamentoDTOComStatus(StatusAgendamento status) {
        AgendamentoDTO dto = criarAgendamentoDTO();
        dto.setStatus(status);
        return dto;
    }

    private AgendamentoCompletoDTO criarAgendamentoCompletoDTO() {
        // Como AgendamentoCompletoDTO não tem construtor padrão, vamos usar mock com strictness lenient
        AgendamentoCompletoDTO dto = mock(AgendamentoCompletoDTO.class, withSettings().lenient());
        when(dto.getIdAgendamento()).thenReturn(1L);
        when(dto.getTipoServico()).thenReturn(TipoServico.TATUAGEM_PEQUENA);
        when(dto.getDescricao()).thenReturn("Tatuagem de dragão no braço direito");
        when(dto.getDtInicio()).thenReturn(LocalDateTime.now().plusDays(7));
        when(dto.getDtFim()).thenReturn(LocalDateTime.now().plusDays(7).plusHours(2));
        when(dto.getValor()).thenReturn(new BigDecimal("300.00"));
        when(dto.getIdProfissional()).thenReturn(1L);
        when(dto.getNomeProfissional()).thenReturn("João Tatuador");
        when(dto.getIdUsuario()).thenReturn(2L);
        when(dto.getNomeUsuario()).thenReturn("Maria Cliente");
        when(dto.getStatus()).thenReturn(StatusAgendamento.AGENDADO);
        when(dto.getRua()).thenReturn("Rua das Flores");
        when(dto.getNumero()).thenReturn("123");
        when(dto.getBairro()).thenReturn("Centro");
        when(dto.getCidade()).thenReturn("São Paulo");
        when(dto.getEstado()).thenReturn("SP");
        when(dto.getCep()).thenReturn("01234-567");
        return dto;
    }
} 