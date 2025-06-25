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
import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.service.ProfissionalService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalController - Testes Core")
class ProfissionalControllerCoreTest {

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
    @DisplayName("Deve listar profissionais com paginação")
    void deveListarProfissionaisComPaginacao() {
        // Arrange
        List<ProfissionalDTO> profissionais = Arrays.asList(profissionalDTO);
        when(profissionalService.listarComAutorizacao(any(Pageable.class))).thenReturn(profissionais);

        // Act
        ResponseEntity<List<ProfissionalDTO>> response = profissionalController.listar(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(profissionalService).listarComAutorizacao(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar profissionais públicos")
    void deveListarProfissionaisPublicos() {
        // Arrange
        List<ProfissionalDTO> profissionais = Arrays.asList(profissionalDTO);
        when(profissionalService.listarPublico(any(Pageable.class))).thenReturn(profissionais);

        // Act
        ResponseEntity<List<ProfissionalDTO>> response = profissionalController.listarPublico();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(profissionalService).listarPublico(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar profissionais completos com filtros")
    void deveListarProfissionaisCompletosComFiltros() {
        // Arrange
        Map<String, Object> profissionalMap = new HashMap<>();
        profissionalMap.put("profissional", profissionalDTO);
        List<Map<String, Object>> content = Arrays.asList(profissionalMap);
        Page<Map<String, Object>> page = new PageImpl<>(content, pageable, 1);
        
        when(profissionalService.listarCompletoComFiltros(
            any(Pageable.class), anyString(), anyString(), anyDouble(), any(String[].class), anyString()))
            .thenReturn(page);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.listarCompleto(
            0, 9, "João", "São Paulo", 4.0, new String[]{"TATUAGEM_PEQUENA"}, "melhorAvaliacao");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("content"));
        assertTrue(response.getBody().containsKey("totalElements"));
        verify(profissionalService).listarCompletoComFiltros(
            any(Pageable.class), anyString(), anyString(), anyDouble(), any(String[].class), anyString());
    }

    @Test
    @DisplayName("Deve buscar profissional completo por ID")
    void deveBuscarProfissionalCompletoPorId() {
        // Arrange
        Long id = 1L;
        Map<String, Object> profissionalCompleto = new HashMap<>();
        profissionalCompleto.put("profissional", profissionalDTO);
        
        when(profissionalService.buscarCompletoComValidacao(id)).thenReturn(profissionalCompleto);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.buscarCompletoPorid(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profissionalCompleto, response.getBody());
        verify(profissionalService).buscarCompletoComValidacao(id);
    }

    @Test
    @DisplayName("Deve buscar profissional por ID")
    void deveBuscarProfissionalPorId() {
        // Arrange
        Long id = 1L;
        when(profissionalService.buscarPorId(id)).thenReturn(null);
        when(profissionalService.converterParaDto(any())).thenReturn(profissionalDTO);

        // Act
        ResponseEntity<ProfissionalDTO> response = profissionalController.buscarPorId(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(profissionalService).buscarPorId(id);
        verify(profissionalService).converterParaDto(any());
    }

    @Test
    @DisplayName("Deve buscar profissional por usuário")
    void deveBuscarProfissionalPorUsuario() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.buscarPorUsuarioComAutorizacao(idUsuario)).thenReturn(profissionalDTO);

        // Act
        ResponseEntity<ProfissionalDTO> response = profissionalController.buscarPorUsuario(idUsuario);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profissionalDTO, response.getBody());
        verify(profissionalService).buscarPorUsuarioComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve buscar profissional completo por usuário")
    void deveBuscarProfissionalCompletoPorUsuario() {
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
        
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario)).thenReturn(data);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.buscarProfissionalCompleto(idUsuario);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("profissional"));
        verify(profissionalService).buscarProfissionalCompletoComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve verificar perfil do profissional")
    void deveVerificarPerfilDoProfissional() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.verificarPerfilComAutorizacao(idUsuario)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = profissionalController.verificarPerfil(idUsuario);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
        verify(profissionalService).verificarPerfilComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve listar tipos de serviço")
    void deveListarTiposDeServico() {
        // Arrange
        List<Map<String, Object>> tiposServico = Arrays.asList(new HashMap<>());
        when(profissionalService.listarTiposServico()).thenReturn(tiposServico);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = profissionalController.listarTiposServico();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(profissionalService).listarTiposServico();
    }

    @Test
    @DisplayName("Deve listar tipos de serviço por profissional")
    void deveListarTiposDeServicoPorProfissional() {
        // Arrange
        Long idProfissional = 1L;
        List<Map<String, Object>> tiposServico = Arrays.asList(new HashMap<>());
        when(profissionalService.listarTiposServicoPorProfissionalComValidacao(idProfissional)).thenReturn(tiposServico);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = profissionalController.listarTiposServicoPorProfissional(idProfissional);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(profissionalService).listarTiposServicoPorProfissionalComValidacao(idProfissional);
    }

    @Test
    @DisplayName("Deve criar profissional completo")
    void deveCriarProfissionalCompleto() {
        // Arrange
        ProfissionalService.ProfissionalCompletoData data = mock(ProfissionalService.ProfissionalCompletoData.class);
        when(data.getProfissional()).thenReturn(profissionalDTO);
        when(data.getPortfolio()).thenReturn(null);
        when(data.getImagens()).thenReturn(new ArrayList<ImagemDTO>());
        when(data.getDisponibilidades()).thenReturn(new HashMap<String, List<Map<String, String>>>());
        when(data.getTiposServico()).thenReturn(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        when(data.getPrecosServicos()).thenReturn(new HashMap<String, BigDecimal>());
        when(data.getTiposServicoPrecos()).thenReturn(new HashMap<String, BigDecimal>());
        
        when(profissionalService.criarProfissionalCompletoComValidacao(profissionalCriacaoDTO)).thenReturn(profissionalDTO);
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(profissionalCriacaoDTO.getIdUsuario())).thenReturn(data);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.criarProfissionalCompleto(profissionalCriacaoDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("profissional"));
        verify(profissionalService).criarProfissionalCompletoComValidacao(profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve atualizar profissional")
    void deveAtualizarProfissional() {
        // Arrange
        Long id = 1L;
        when(profissionalService.atualizarComAutorizacao(id, profissionalDTO)).thenReturn(profissionalDTO);

        // Act
        ResponseEntity<ProfissionalDTO> response = profissionalController.atualizar(id, profissionalDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profissionalDTO, response.getBody());
        verify(profissionalService).atualizarComAutorizacao(id, profissionalDTO);
    }

    @Test
    @DisplayName("Deve atualizar profissional completo")
    void deveAtualizarProfissionalCompleto() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO)).thenReturn(profissionalDTO);

        // Act
        ResponseEntity<ProfissionalDTO> response = profissionalController.atualizarProfissionalCompleto(idUsuario, profissionalCriacaoDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(profissionalDTO, response.getBody());
        verify(profissionalService).atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve atualizar profissional completo com imagens")
    void deveAtualizarProfissionalCompletoComImagens() {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profissional", profissionalDTO);
        
        when(profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData)).thenReturn(responseData);

        // Act
        ResponseEntity<Map<String, Object>> response = profissionalController.atualizarProfissionalCompletoComImagens(idUsuario, requestData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseData, response.getBody());
        verify(profissionalService).atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);
    }

    @Test
    @DisplayName("Deve deletar profissional")
    void deveDeletarProfissional() {
        // Arrange
        Long id = 1L;
        doNothing().when(profissionalService).deletarComAutorizacao(id);

        // Act
        ResponseEntity<Void> response = profissionalController.deletar(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(profissionalService).deletarComAutorizacao(id);
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
} 