package inkspiration.backend.controller.profissionalController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.ProfissionalController;
import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.service.ProfissionalService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalController - Testes de Integração")
class ProfissionalControllerIntegracaoTest {

    @Mock
    private ProfissionalService profissionalService;

    @InjectMocks
    private ProfissionalController profissionalController;

    private ProfissionalDTO profissionalDTO;
    private ProfissionalCriacaoDTO profissionalCriacaoDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        profissionalDTO = criarProfissionalDTO();
        profissionalCriacaoDTO = criarProfissionalCriacaoDTO();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de busca pública com listagem")
    void deveExecutarFluxoCompletoDeBuscaPublicaComListagem() {
        // Arrange
        List<ProfissionalDTO> profissionaisPublicos = Arrays.asList(profissionalDTO);
        List<ProfissionalDTO> profissionaisPrivados = Arrays.asList(profissionalDTO);
        
        when(profissionalService.listarPublico(any(Pageable.class))).thenReturn(profissionaisPublicos);
        when(profissionalService.listarComAutorizacao(any(Pageable.class))).thenReturn(profissionaisPrivados);

        // Act
        ResponseEntity<List<ProfissionalDTO>> responsePublico = profissionalController.listarPublico();
        ResponseEntity<List<ProfissionalDTO>> responsePrivado = profissionalController.listar(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, responsePublico.getStatusCode());
        assertEquals(HttpStatus.OK, responsePrivado.getStatusCode());
        assertNotNull(responsePublico.getBody());
        assertNotNull(responsePrivado.getBody());
        assertEquals(1, responsePublico.getBody().size());
        assertEquals(1, responsePrivado.getBody().size());
        
        verify(profissionalService).listarPublico(any(Pageable.class));
        verify(profissionalService).listarComAutorizacao(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve executar fluxo completo de busca por filtros")
    void deveExecutarFluxoCompletoDeBuscaPorFiltros() {
        // Arrange
        Map<String, Object> profissionalMap = new HashMap<>();
        profissionalMap.put("profissional", profissionalDTO);
        profissionalMap.put("endereco", new HashMap<String, Object>());
        profissionalMap.put("portfolio", new HashMap<String, Object>());
        
        List<Map<String, Object>> content = Arrays.asList(profissionalMap);
        Pageable pageableComSize9 = PageRequest.of(0, 9);
        Page<Map<String, Object>> page = new PageImpl<>(content, pageableComSize9, 1);
        
        when(profissionalService.listarCompletoComFiltros(
            any(Pageable.class), anyString(), anyString(), anyDouble(), any(String[].class), anyString()))
            .thenReturn(page);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.listarCompleto(
            0, 9, "João Silva", "São Paulo", 4.0, new String[]{"TATUAGEM_PEQUENA"}, "melhorAvaliacao");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue(responseBody.containsKey("content"));
        assertTrue(responseBody.containsKey("totalElements"));
        assertTrue(responseBody.containsKey("totalPages"));
        assertTrue(responseBody.containsKey("currentPage"));
        assertTrue(responseBody.containsKey("size"));
        assertTrue(responseBody.containsKey("hasNext"));
        assertTrue(responseBody.containsKey("hasPrevious"));
        
        assertEquals(1L, responseBody.get("totalElements"));
        assertEquals(1, responseBody.get("totalPages"));
        assertEquals(0, responseBody.get("currentPage"));
        assertEquals(9, responseBody.get("size"));
        assertEquals(false, responseBody.get("hasNext"));
        assertEquals(false, responseBody.get("hasPrevious"));
        
        verify(profissionalService).listarCompletoComFiltros(
            any(Pageable.class), eq("João Silva"), eq("São Paulo"), 
            eq(4.0), any(String[].class), eq("melhorAvaliacao"));
    }

    @Test
    @DisplayName("Deve executar fluxo completo de busca por ID e profissional completo")
    void deveExecutarFluxoCompletoDeBuscaPorIdEProfissionalCompleto() {
        // Arrange
        Long id = 1L;
        Map<String, Object> profissionalCompleto = new HashMap<>();
        profissionalCompleto.put("profissional", profissionalDTO);
        profissionalCompleto.put("portfolio", new HashMap<String, Object>());
        profissionalCompleto.put("imagens", Arrays.asList());
        profissionalCompleto.put("disponibilidades", new HashMap<String, Object>());
        
        when(profissionalService.buscarPorId(id)).thenReturn(null);
        when(profissionalService.converterParaDto(any())).thenReturn(profissionalDTO);
        when(profissionalService.buscarCompletoComValidacao(id)).thenReturn(profissionalCompleto);

        // Act
        ResponseEntity<ProfissionalDTO> responseBasico = profissionalController.buscarPorId(id);
        ResponseEntity<Map<String, Object>> responseCompleto = profissionalController.buscarCompletoPorid(id);

        // Assert
        assertEquals(HttpStatus.OK, responseBasico.getStatusCode());
        assertEquals(HttpStatus.OK, responseCompleto.getStatusCode());
        assertNotNull(responseBasico.getBody());
        assertNotNull(responseCompleto.getBody());
        assertEquals(profissionalDTO, responseBasico.getBody());
        assertEquals(profissionalCompleto, responseCompleto.getBody());
        
        verify(profissionalService).buscarPorId(id);
        verify(profissionalService).converterParaDto(any());
        verify(profissionalService).buscarCompletoComValidacao(id);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de busca por usuário")
    void deveExecutarFluxoCompletoDeBuscaPorUsuario() {
        // Arrange
        Long idUsuario = 1L;
        
        ProfissionalService.ProfissionalCompletoData data = mock(ProfissionalService.ProfissionalCompletoData.class);
        when(data.getProfissional()).thenReturn(profissionalDTO);
        when(data.getPortfolio()).thenReturn(null);
        when(data.getImagens()).thenReturn(new ArrayList<ImagemDTO>());
        when(data.getDisponibilidades()).thenReturn(new HashMap<String, List<Map<String, String>>>());
        when(data.getTiposServico()).thenReturn(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        when(data.getPrecosServicos()).thenReturn(new HashMap<String, BigDecimal>());
        when(data.getTiposServicoPrecos()).thenReturn(new HashMap<String, BigDecimal>());
        
        when(profissionalService.buscarPorUsuarioComAutorizacao(idUsuario)).thenReturn(profissionalDTO);
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario)).thenReturn(data);
        when(profissionalService.verificarPerfilComAutorizacao(idUsuario)).thenReturn(true);

        // Act
        ResponseEntity<ProfissionalDTO> responseBasico = profissionalController.buscarPorUsuario(idUsuario);
        ResponseEntity<Map<String, Object>> responseCompleto = profissionalController.buscarProfissionalCompleto(idUsuario);
        ResponseEntity<Boolean> responseVerificacao = profissionalController.verificarPerfil(idUsuario);

        // Assert
        assertEquals(HttpStatus.OK, responseBasico.getStatusCode());
        assertEquals(HttpStatus.OK, responseCompleto.getStatusCode());
        assertEquals(HttpStatus.OK, responseVerificacao.getStatusCode());
        
        assertNotNull(responseBasico.getBody());
        assertNotNull(responseCompleto.getBody());
        assertNotNull(responseVerificacao.getBody());
        
        assertEquals(profissionalDTO, responseBasico.getBody());
        assertTrue(responseVerificacao.getBody());
        
        Map<String, Object> responseCompletoBody = responseCompleto.getBody();
        assertTrue(responseCompletoBody.containsKey("profissional"));
        assertTrue(responseCompletoBody.containsKey("portfolio"));
        assertTrue(responseCompletoBody.containsKey("imagens"));
        assertTrue(responseCompletoBody.containsKey("disponibilidades"));
        
        verify(profissionalService).buscarPorUsuarioComAutorizacao(idUsuario);
        verify(profissionalService).buscarProfissionalCompletoComAutorizacao(idUsuario);
        verify(profissionalService).verificarPerfilComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de listagem de tipos de serviço")
    void deveExecutarFluxoCompletoDeListagemDeTiposDeServico() {
        // Arrange
        List<Map<String, Object>> tiposServicoGeral = Arrays.asList(
            criarTipoServicoMap("TATUAGEM_PEQUENA", "pequena", 2),
            criarTipoServicoMap("TATUAGEM_MEDIA", "media", 4)
        );
        
        Long idProfissional = 1L;
        List<Map<String, Object>> tiposServicoProfissional = Arrays.asList(
            criarTipoServicoComPrecoMap("TATUAGEM_PEQUENA", 2, new BigDecimal("150.00"))
        );
        
        when(profissionalService.listarTiposServico()).thenReturn(tiposServicoGeral);
        when(profissionalService.listarTiposServicoPorProfissionalComValidacao(idProfissional))
            .thenReturn(tiposServicoProfissional);

        // Act
        ResponseEntity<List<Map<String, Object>>> responseGeral = profissionalController.listarTiposServico();
        ResponseEntity<List<Map<String, Object>>> responseProfissional = 
            profissionalController.listarTiposServicoPorProfissional(idProfissional);

        // Assert
        assertEquals(HttpStatus.OK, responseGeral.getStatusCode());
        assertEquals(HttpStatus.OK, responseProfissional.getStatusCode());
        
        assertNotNull(responseGeral.getBody());
        assertNotNull(responseProfissional.getBody());
        
        assertEquals(2, responseGeral.getBody().size());
        assertEquals(1, responseProfissional.getBody().size());
        
        verify(profissionalService).listarTiposServico();
        verify(profissionalService).listarTiposServicoPorProfissionalComValidacao(idProfissional);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de criação e atualização")
    void deveExecutarFluxoCompletoDeCriacaoEAtualizacao() {
        // Arrange
        Long idUsuario = profissionalCriacaoDTO.getIdUsuario();
        Long idProfissional = 1L;
        
        ProfissionalService.ProfissionalCompletoData data = mock(ProfissionalService.ProfissionalCompletoData.class);
        when(data.getProfissional()).thenReturn(profissionalDTO);
        when(data.getPortfolio()).thenReturn(null);
        when(data.getImagens()).thenReturn(new ArrayList<ImagemDTO>());
        when(data.getDisponibilidades()).thenReturn(new HashMap<String, List<Map<String, String>>>());
        when(data.getTiposServico()).thenReturn(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        when(data.getPrecosServicos()).thenReturn(new HashMap<String, BigDecimal>());
        when(data.getTiposServicoPrecos()).thenReturn(new HashMap<String, BigDecimal>());
        
        when(profissionalService.criarProfissionalCompletoComValidacao(profissionalCriacaoDTO))
            .thenReturn(profissionalDTO);
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario)).thenReturn(data);
        when(profissionalService.atualizarComAutorizacao(idProfissional, profissionalDTO))
            .thenReturn(profissionalDTO);
        when(profissionalService.atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO))
            .thenReturn(profissionalDTO);

        // Act
        ResponseEntity<Map<String, Object>> responseCriacao = 
            profissionalController.criarProfissionalCompleto(profissionalCriacaoDTO);
        ResponseEntity<ProfissionalDTO> responseAtualizacaoBasica = 
            profissionalController.atualizar(idProfissional, profissionalDTO);
        ResponseEntity<ProfissionalDTO> responseAtualizacaoCompleta = 
            profissionalController.atualizarProfissionalCompleto(idUsuario, profissionalCriacaoDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, responseCriacao.getStatusCode());
        assertEquals(HttpStatus.OK, responseAtualizacaoBasica.getStatusCode());
        assertEquals(HttpStatus.OK, responseAtualizacaoCompleta.getStatusCode());
        
        assertNotNull(responseCriacao.getBody());
        assertNotNull(responseAtualizacaoBasica.getBody());
        assertNotNull(responseAtualizacaoCompleta.getBody());
        
        assertTrue(responseCriacao.getBody().containsKey("profissional"));
        assertEquals(profissionalDTO, responseAtualizacaoBasica.getBody());
        assertEquals(profissionalDTO, responseAtualizacaoCompleta.getBody());
        
        verify(profissionalService).criarProfissionalCompletoComValidacao(profissionalCriacaoDTO);
        verify(profissionalService).buscarProfissionalCompletoComAutorizacao(idUsuario);
        verify(profissionalService).atualizarComAutorizacao(idProfissional, profissionalDTO);
        verify(profissionalService).atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de atualização com imagens e deleção")
    void deveExecutarFluxoCompletoDeAtualizacaoComImagensEDelecao() {
        // Arrange
        Long idUsuario = 1L;
        Long idProfissional = 1L;
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("profissional", profissionalCriacaoDTO);
        requestData.put("imagens", Arrays.asList("base64image1", "base64image2"));
        requestData.put("imagensRemover", Arrays.asList(1L, 2L));
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profissional", profissionalDTO);
        responseData.put("imagens", Arrays.asList());
        
        when(profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData))
            .thenReturn(responseData);
        doNothing().when(profissionalService).deletarComAutorizacao(idProfissional);

        // Act
        ResponseEntity<Map<String, Object>> responseAtualizacao = 
            profissionalController.atualizarProfissionalCompletoComImagens(idUsuario, requestData);
        ResponseEntity<Void> responseDelecao = profissionalController.deletar(idProfissional);

        // Assert
        assertEquals(HttpStatus.OK, responseAtualizacao.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, responseDelecao.getStatusCode());
        
        assertNotNull(responseAtualizacao.getBody());
        assertEquals(responseData, responseAtualizacao.getBody());
        
        verify(profissionalService).atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);
        verify(profissionalService).deletarComAutorizacao(idProfissional);
    }

    @Test
    @DisplayName("Deve manter consistência entre diferentes operações de busca")
    void deveManterConsistenciaEntreDiferentesOperacoesDeBusca() {
        // Arrange
        Long idProfissional = 1L;
        Long idUsuario = 1L;
        
        Map<String, Object> profissionalCompleto = new HashMap<>();
        profissionalCompleto.put("profissional", profissionalDTO);
        
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(null);
        when(profissionalService.converterParaDto(any())).thenReturn(profissionalDTO);
        when(profissionalService.buscarCompletoComValidacao(idProfissional)).thenReturn(profissionalCompleto);
        when(profissionalService.buscarPorUsuarioComAutorizacao(idUsuario)).thenReturn(profissionalDTO);

        // Act
        ResponseEntity<ProfissionalDTO> response1 = profissionalController.buscarPorId(idProfissional);
        ResponseEntity<Map<String, Object>> response2 = profissionalController.buscarCompletoPorid(idProfissional);
        ResponseEntity<ProfissionalDTO> response3 = profissionalController.buscarPorUsuario(idUsuario);

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        
        // Verificar consistência dos dados
        ProfissionalDTO profissional1 = response1.getBody();
        Map<String, Object> completoResponse = response2.getBody();
        ProfissionalDTO profissional3 = response3.getBody();
        
        assertNotNull(profissional1);
        assertNotNull(completoResponse);
        assertNotNull(profissional3);
        
        assertEquals(profissional1.getIdProfissional(), profissional3.getIdProfissional());
        assertEquals(profissional1.getIdUsuario(), profissional3.getIdUsuario());
        
        verify(profissionalService).buscarPorId(idProfissional);
        verify(profissionalService).converterParaDto(any());
        verify(profissionalService).buscarCompletoComValidacao(idProfissional);
        verify(profissionalService).buscarPorUsuarioComAutorizacao(idUsuario);
    }

    // Métodos auxiliares
    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdProfissional(1L);
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("4.5"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }

    private ProfissionalCriacaoDTO criarProfissionalCriacaoDTO() {
        ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        dto.setDescricao("Descrição detalhada do profissional com mais de 20 caracteres para atender à validação");
        return dto;
    }

    private Map<String, Object> criarTipoServicoMap(String nome, String descricao, int duracaoHoras) {
        Map<String, Object> tipoMap = new HashMap<>();
        tipoMap.put("nome", nome);
        tipoMap.put("descricao", descricao);
        tipoMap.put("duracaoHoras", duracaoHoras);
        return tipoMap;
    }

    private Map<String, Object> criarTipoServicoComPrecoMap(String tipo, int duracaoHoras, BigDecimal preco) {
        Map<String, Object> tipoMap = new HashMap<>();
        tipoMap.put("tipo", tipo);
        tipoMap.put("duracaoHoras", duracaoHoras);
        tipoMap.put("preco", preco);
        tipoMap.put("exemplo", "Tatuagem pequena - 2 horas");
        return tipoMap;
    }
} 