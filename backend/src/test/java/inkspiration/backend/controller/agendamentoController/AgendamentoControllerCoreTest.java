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
@DisplayName("AgendamentoController - Testes de Casos de Sucesso")
class AgendamentoControllerCoreTest {

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
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenReturn(agendamentoDTO);

        // Act
        ResponseEntity<AgendamentoDTO> response = agendamentoController.criarAgendamento(agendamentoRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendamentoDTO.getIdAgendamento(), response.getBody().getIdAgendamento());
        assertEquals(agendamentoDTO.getDescricao(), response.getBody().getDescricao());

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID com sucesso")
    void deveBuscarAgendamentoPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        when(agendamentoService.buscarPorIdComValidacao(id)).thenReturn(agendamentoDTO);

        // Act
        ResponseEntity<AgendamentoDTO> response = agendamentoController.buscarPorId(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendamentoDTO.getIdAgendamento(), response.getBody().getIdAgendamento());

        verify(agendamentoService).buscarPorIdComValidacao(id);
    }

    @Test
    @DisplayName("Deve listar agendamentos por usuário com sucesso")
    void deveListarAgendamentosPorUsuarioComSucesso() {
        // Arrange
        Long idUsuario = 1L;
        List<AgendamentoDTO> agendamentos = Arrays.asList(agendamentoDTO);
        when(agendamentoService.listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class)))
            .thenReturn(agendamentos);

        // Act
        ResponseEntity<List<AgendamentoDTO>> response = agendamentoController.listarPorUsuario(idUsuario, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(agendamentoDTO.getIdAgendamento(), response.getBody().get(0).getIdAgendamento());

        verify(agendamentoService).listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar agendamentos por profissional com sucesso")
    void deveListarAgendamentosPorProfissionalComSucesso() {
        // Arrange
        Long idProfissional = 1L;
        List<AgendamentoDTO> agendamentos = Arrays.asList(agendamentoDTO);
        when(agendamentoService.listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class)))
            .thenReturn(agendamentos);

        // Act
        ResponseEntity<List<AgendamentoDTO>> response = agendamentoController.listarPorProfissional(idProfissional, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(agendamentoService).listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar agendamento com sucesso")
    void deveAtualizarAgendamentoComSucesso() {
        // Arrange
        Long id = 1L;
        when(agendamentoService.atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication))
            .thenReturn(agendamentoDTO);

        // Act
        ResponseEntity<AgendamentoDTO> response = agendamentoController.atualizarAgendamento(id, agendamentoUpdateDTO, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(agendamentoService).atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication);
    }

    @Test
    @DisplayName("Deve excluir agendamento com sucesso")
    void deveExcluirAgendamentoComSucesso() {
        // Arrange
        Long id = 1L;
        doNothing().when(agendamentoService).excluirAgendamentoComValidacao(id);

        // Act
        ResponseEntity<String> response = agendamentoController.excluirAgendamento(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Agendamento excluído com sucesso", response.getBody());

        verify(agendamentoService).excluirAgendamentoComValidacao(id);
    }

    @Test
    @DisplayName("Deve listar meus agendamentos com sucesso")
    void deveListarMeusAgendamentosComSucesso() {
        // Arrange
        Page<AgendamentoDTO> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoDTO));
        when(agendamentoService.listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(agendamentosPage);

        // Act
        ResponseEntity<Page<AgendamentoDTO>> response = agendamentoController.listarMeusAgendamentos(authentication, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(agendamentoService).listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve atualizar status do agendamento com sucesso")
    void deveAtualizarStatusDoAgendamentoComSucesso() {
        // Arrange
        Long id = 1L;
        String status = "CONFIRMADO";
        when(agendamentoService.atualizarStatusAgendamentoComAutenticacao(id, status, authentication))
            .thenReturn(agendamentoDTO);

        // Act
        ResponseEntity<AgendamentoDTO> response = agendamentoController.atualizarStatusAgendamento(id, status, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(agendamentoService).atualizarStatusAgendamentoComAutenticacao(id, status, authentication);
    }

    @Test
    @DisplayName("Deve listar meus agendamentos futuros com sucesso")
    void deveListarMeusAgendamentosFuturosComSucesso() {
        // Arrange
        Page<AgendamentoCompletoDTO> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        when(agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(agendamentosPage);

        // Act
        ResponseEntity<Page<AgendamentoCompletoDTO>> response = agendamentoController.listarMeusAgendamentosFuturos(authentication, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(agendamentoService).listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar meus agendamentos passados com sucesso")
    void deveListarMeusAgendamentosPassadosComSucesso() {
        // Arrange
        Page<AgendamentoCompletoDTO> agendamentosPage = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        when(agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(agendamentosPage);

        // Act
        ResponseEntity<Page<AgendamentoCompletoDTO>> response = agendamentoController.listarMeusAgendamentosPassados(authentication, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(agendamentoService).listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve exportar agendamentos PDF com sucesso")
    void deveExportarAgendamentosPDFComSucesso() {
        // Arrange
        Integer ano = 2024;
        byte[] pdfBytes = "PDF content".getBytes();
        when(agendamentoService.exportarAgendamentosPDFComAutenticacao(ano, authentication))
            .thenReturn(pdfBytes);

        // Act
        ResponseEntity<?> response = agendamentoController.exportarAgendamentosPDF(ano, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(pdfBytes, response.getBody());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("attachment"));

        verify(agendamentoService).exportarAgendamentosPDFComAutenticacao(ano, authentication);
    }

    @Test
    @DisplayName("Deve listar meus atendimentos futuros com sucesso")
    void deveListarMeusAtendimentosFuturosComSucesso() {
        // Arrange
        Page<AgendamentoCompletoDTO> atendimentosPage = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        when(agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(atendimentosPage);

        // Act
        ResponseEntity<Page<AgendamentoCompletoDTO>> response = agendamentoController.listarMeusAtendimentosFuturos(authentication, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(agendamentoService).listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar meus atendimentos passados com sucesso")
    void deveListarMeusAtendimentosPassadosComSucesso() {
        // Arrange
        Page<AgendamentoCompletoDTO> atendimentosPage = new PageImpl<>(Arrays.asList(agendamentoCompletoDTO));
        when(agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenReturn(atendimentosPage);

        // Act
        ResponseEntity<Page<AgendamentoCompletoDTO>> response = agendamentoController.listarMeusAtendimentosPassados(authentication, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        verify(agendamentoService).listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve exportar atendimentos PDF com sucesso")
    void deveExportarAtendimentosPDFComSucesso() {
        // Arrange
        Integer ano = 2024;
        Integer mes = 6;
        byte[] pdfBytes = "PDF content".getBytes();
        when(agendamentoService.exportarAtendimentosPDFComAutenticacao(ano, mes, authentication))
            .thenReturn(pdfBytes);

        // Act
        ResponseEntity<?> response = agendamentoController.exportarAtendimentosPDF(ano, mes, authentication);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(pdfBytes, response.getBody());
        assertNotNull(response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("attachment"));
        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("atendimentos-06-2024.pdf"));

        verify(agendamentoService).exportarAtendimentosPDFComAutenticacao(ano, mes, authentication);
    }

    @Test
    @DisplayName("Deve listar agendamentos com paginação customizada")
    void deveListarAgendamentosComPaginacaoCustomizada() {
        // Arrange
        Long idUsuario = 1L;
        List<AgendamentoDTO> agendamentos = Arrays.asList(agendamentoDTO);
        when(agendamentoService.listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class)))
            .thenReturn(agendamentos);

        // Act
        ResponseEntity<List<AgendamentoDTO>> response = agendamentoController.listarPorUsuario(idUsuario, 2, 5);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agendamentoService).listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class));
    }

    // Métodos auxiliares
    private AgendamentoRequestDTO criarAgendamentoRequestDTO() {
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão no braço direito com detalhes em cores");
        dto.setDtInicio(LocalDateTime.now().plusDays(7));
        dto.setValor(new BigDecimal("300.00"));
        dto.setIdProfissional(1L);
        dto.setIdUsuario(2L);
        return dto;
    }

    private AgendamentoUpdateDTO criarAgendamentoUpdateDTO() {
        AgendamentoUpdateDTO dto = new AgendamentoUpdateDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão atualizada com mais detalhes");
        dto.setDtInicio(LocalDateTime.now().plusDays(8));
        return dto;
    }

    private AgendamentoDTO criarAgendamentoDTO() {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setIdAgendamento(1L);
        dto.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        dto.setDescricao("Tatuagem de dragão no braço direito");
        dto.setDtInicio(LocalDateTime.now().plusDays(7));
        dto.setDtFim(LocalDateTime.now().plusDays(7).plusHours(2));
        dto.setValor(new BigDecimal("300.00"));
        dto.setIdProfissional(1L);
        dto.setIdUsuario(2L);
        dto.setStatus(StatusAgendamento.AGENDADO);
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