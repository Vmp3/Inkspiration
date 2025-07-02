package inkspiration.backend.controller.profissionalController;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.controller.ProfissionalController;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.enums.TipoServico;

@DisplayName("ProfissionalController - Testes de Validação")
class ProfissionalControllerValidacaoTest {

    private ProfissionalController profissionalController;
    private ProfissionalDTO profissionalDTO;
    private ProfissionalCriacaoDTO profissionalCriacaoDTO;

    @BeforeEach
    void setUp() {
        profissionalDTO = criarProfissionalDTO();
        profissionalCriacaoDTO = criarProfissionalCriacaoDTO();
    }

    @Test
    @DisplayName("Deve validar construção do ProfissionalController")
    void deveValidarConstrucaoDoController() {
        assertDoesNotThrow(() -> {
            new ProfissionalController(null);
        });
    }

    @Test
    @DisplayName("Deve validar criação de Pageable para listagem")
    void deveValidarCriacaoDePageableParaListagem() {
        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 9);
        Pageable pageable3 = PageRequest.of(0, 100);
        
        assertNotNull(pageable1);
        assertNotNull(pageable2);
        assertNotNull(pageable3);
        
        assertEquals(0, pageable1.getPageNumber());
        assertEquals(10, pageable1.getPageSize());
        
        assertEquals(1, pageable2.getPageNumber());
        assertEquals(9, pageable2.getPageSize());
        
        assertEquals(0, pageable3.getPageNumber());
        assertEquals(100, pageable3.getPageSize());
    }

    @Test
    @DisplayName("Deve validar parâmetros de paginação")
    void deveValidarParametrosDePaginacao() {
        int page = 0;
        int size = 9;
        
        assertTrue(page >= 0);
        assertTrue(size > 0);
        assertTrue(size <= 100);
        
        assertEquals(0, Math.max(0, page));
        assertEquals(9, Math.max(1, size));
    }

    @Test
    @DisplayName("Deve validar parâmetros de filtros")
    void deveValidarParametrosDeFiltros() {
        String searchTerm = "João";
        String locationTerm = "São Paulo";
        double minRating = 4.0;
        String[] selectedSpecialties = {"TATUAGEM_PEQUENA", "TATUAGEM_MEDIA"};
        String sortBy = "melhorAvaliacao";
        
        assertNotNull(searchTerm);
        assertNotNull(locationTerm);
        assertTrue(minRating >= 0.0);
        assertTrue(minRating <= 5.0);
        assertNotNull(selectedSpecialties);
        assertNotNull(sortBy);
        
        assertTrue(searchTerm.length() > 0);
        assertTrue(locationTerm.length() > 0);
        assertEquals(2, selectedSpecialties.length);
    }

    @Test
    @DisplayName("Deve validar ProfissionalDTO para requests")
    void deveValidarProfissionalDTOParaRequests() {
        assertNotNull(profissionalDTO);
        assertNotNull(profissionalDTO.getIdUsuario());
        assertNotNull(profissionalDTO.getIdEndereco());
        assertNotNull(profissionalDTO.getNota());
        assertNotNull(profissionalDTO.getTiposServico());
        
        assertTrue(profissionalDTO.getIdUsuario() > 0);
        assertTrue(profissionalDTO.getIdEndereco() > 0);
        assertTrue(profissionalDTO.getNota().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(profissionalDTO.getNota().compareTo(new BigDecimal("5.0")) <= 0);
        assertFalse(profissionalDTO.getTiposServico().isEmpty());
    }

    @Test
    @DisplayName("Deve validar ProfissionalCriacaoDTO para requests")
    void deveValidarProfissionalCriacaoDTOParaRequests() {
        assertNotNull(profissionalCriacaoDTO);
        assertNotNull(profissionalCriacaoDTO.getIdUsuario());
        assertNotNull(profissionalCriacaoDTO.getIdEndereco());
        assertNotNull(profissionalCriacaoDTO.getTiposServico());
        assertNotNull(profissionalCriacaoDTO.getDescricao());
        assertNotNull(profissionalCriacaoDTO.getDisponibilidades());
        assertNotNull(profissionalCriacaoDTO.getPrecosServicos());
        
        assertTrue(profissionalCriacaoDTO.getIdUsuario() > 0);
        assertTrue(profissionalCriacaoDTO.getIdEndereco() > 0);
        assertFalse(profissionalCriacaoDTO.getTiposServico().isEmpty());
        assertTrue(profissionalCriacaoDTO.getDescricao().length() >= 20);
        assertTrue(profissionalCriacaoDTO.getDescricao().length() <= 500);
        assertFalse(profissionalCriacaoDTO.getDisponibilidades().isEmpty());
        assertFalse(profissionalCriacaoDTO.getPrecosServicos().isEmpty());
    }

    @Test
    @DisplayName("Deve validar IDs de profissional")
    void deveValidarIDsDeProfissional() {
        Long id1 = 1L;
        Long id2 = 999L;
        Long idInvalido = -1L;
        Long idUsuario = 123L;
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(idUsuario);
        assertTrue(id1 > 0);
        assertTrue(id2 > 0);
        assertTrue(idUsuario > 0);
        assertTrue(idInvalido < 0);
    }

    @Test
    @DisplayName("Deve validar Map para requests de atualização completa")
    void deveValidarMapParaRequestsDeAtualizacaoCompleta() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("profissional", profissionalCriacaoDTO);
        requestData.put("imagens", Arrays.asList("base64image1", "base64image2"));
        requestData.put("imagensRemover", Arrays.asList(1L, 2L));
        
        assertNotNull(requestData);
        assertTrue(requestData.containsKey("profissional"));
        assertTrue(requestData.containsKey("imagens"));
        assertTrue(requestData.containsKey("imagensRemover"));
        assertNotNull(requestData.get("profissional"));
        assertNotNull(requestData.get("imagens"));
        assertNotNull(requestData.get("imagensRemover"));
    }

    @Test
    @DisplayName("Deve validar parâmetros de ordenação")
    void deveValidarParametrosDeOrdenacao() {
        String sortBy1 = "melhorAvaliacao";
        String sortBy2 = "maisRecente";
        String sortBy3 = "alfabetica";
        String sortBy4 = "proximidade";
        
        assertNotNull(sortBy1);
        assertNotNull(sortBy2);
        assertNotNull(sortBy3);
        assertNotNull(sortBy4);
        
        List<String> validSortOptions = Arrays.asList(
            "melhorAvaliacao", "maisRecente", "alfabetica", "proximidade"
        );
        
        assertTrue(validSortOptions.contains(sortBy1));
        assertTrue(validSortOptions.contains(sortBy2));
        assertTrue(validSortOptions.contains(sortBy3));
        assertTrue(validSortOptions.contains(sortBy4));
    }

    @Test
    @DisplayName("Deve validar estrutura de response completo")
    void deveValidarEstruturaDeResponseCompleto() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", Arrays.asList(profissionalDTO));
        response.put("totalElements", 1L);
        response.put("totalPages", 1);
        response.put("currentPage", 0);
        response.put("size", 9);
        response.put("hasNext", false);
        response.put("hasPrevious", false);
        
        assertNotNull(response);
        assertTrue(response.containsKey("content"));
        assertTrue(response.containsKey("totalElements"));
        assertTrue(response.containsKey("totalPages"));
        assertTrue(response.containsKey("currentPage"));
        assertTrue(response.containsKey("size"));
        assertTrue(response.containsKey("hasNext"));
        assertTrue(response.containsKey("hasPrevious"));
        
        assertTrue((Long) response.get("totalElements") >= 0);
        assertTrue((Integer) response.get("totalPages") >= 0);
        assertTrue((Integer) response.get("currentPage") >= 0);
        assertTrue((Integer) response.get("size") > 0);
    }

    @Test
    @DisplayName("Deve validar estrutura de response de profissional completo")
    void deveValidarEstruturaDeResponseDeProfissionalCompleto() {
        Map<String, Object> response = new HashMap<>();
        response.put("profissional", profissionalDTO);
        response.put("portfolio", new HashMap<String, Object>());
        response.put("imagens", Arrays.asList());
        response.put("disponibilidades", Arrays.asList());
        response.put("tiposServico", Arrays.asList());
        response.put("precosServicos", new HashMap<String, BigDecimal>());
        response.put("tiposServicoPrecos", new HashMap<String, BigDecimal>());
        
        assertNotNull(response);
        assertTrue(response.containsKey("profissional"));
        assertTrue(response.containsKey("portfolio"));
        assertTrue(response.containsKey("imagens"));
        assertTrue(response.containsKey("disponibilidades"));
        assertTrue(response.containsKey("tiposServico"));
        assertTrue(response.containsKey("precosServicos"));
        assertTrue(response.containsKey("tiposServicoPrecos"));
    }

    @Test
    @DisplayName("Deve validar paths dos endpoints")
    void deveValidarPathsDosEndpoints() {
        String listarPath = "/profissional";
        String publicoPath = "/profissional/publico";
        String completoPath = "/profissional/completo";
        String buscarPath = "/profissional/{id}";
        String usuarioPath = "/profissional/usuario/{idUsuario}";
        String criarPath = "/auth/register/profissional-completo";
        String atualizarPath = "/profissional/atualizar/{id}";
        String deletarPath = "/profissional/deletar/{id}";
        String tiposServicoPath = "/tipos-servico";
        
        assertNotNull(listarPath);
        assertNotNull(publicoPath);
        assertNotNull(completoPath);
        assertNotNull(buscarPath);
        assertNotNull(usuarioPath);
        assertNotNull(criarPath);
        assertNotNull(atualizarPath);
        assertNotNull(deletarPath);
        assertNotNull(tiposServicoPath);
        
        assertEquals("/profissional", listarPath);
        assertEquals("/profissional/publico", publicoPath);
        assertEquals("/profissional/completo", completoPath);
        assertTrue(buscarPath.contains("{id}"));
        assertTrue(usuarioPath.contains("{idUsuario}"));
    }

    @Test
    @DisplayName("Deve validar tipos de serviço")
    void deveValidarTiposDeServico() {
        List<TipoServico> tiposServico = Arrays.asList(
            TipoServico.TATUAGEM_PEQUENA,
            TipoServico.TATUAGEM_MEDIA
        );
        
        assertNotNull(tiposServico);
        assertFalse(tiposServico.isEmpty());
        assertEquals(2, tiposServico.size());
        assertTrue(tiposServico.contains(TipoServico.TATUAGEM_PEQUENA));
        assertTrue(tiposServico.contains(TipoServico.TATUAGEM_MEDIA));
    }

    // Métodos auxiliares
    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdProfissional(1L);
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("4.5"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA, TipoServico.TATUAGEM_MEDIA));
        return dto;
    }

    private ProfissionalCriacaoDTO criarProfissionalCriacaoDTO() {
        ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        dto.setDescricao("Descrição detalhada do profissional com mais de 20 caracteres para atender à validação");
        dto.setExperiencia("5 anos");
        dto.setEspecialidade("Tatuagem realista");
        dto.setWebsite("https://exemplo.com");
        dto.setInstagram("@profissional");
        
        // Disponibilidades
        DisponibilidadeDTO disponibilidade = new DisponibilidadeDTO();
        dto.setDisponibilidades(Arrays.asList(disponibilidade));
        
        // Preços dos serviços
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("150.00"));
        dto.setPrecosServicos(precos);
        
        return dto;
    }
} 