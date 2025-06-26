package inkspiration.backend.controller.imagemController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import inkspiration.backend.controller.ImagemController;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.exception.imagem.*;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImagemController - Testes Completos")
class ImagemControllerTest {

    @Mock
    private ImagemService imagemService;

    @InjectMocks
    private ImagemController imagemController;

    private final Long ID_IMAGEM = 1L;
    private final Long ID_PORTFOLIO = 2L;
    private final String IMAGEM_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

    private ImagemDTO mockImagemDTO;
    private List<ImagemDTO> mockImagensLista;

    @BeforeEach
    void setUp() {
        // Setup mock ImagemDTO
        mockImagemDTO = new ImagemDTO();
        mockImagemDTO.setIdImagem(ID_IMAGEM);
        mockImagemDTO.setImagemBase64(IMAGEM_BASE64);
        mockImagemDTO.setIdPortfolio(ID_PORTFOLIO);

        // Setup mock lista de imagens
        ImagemDTO imagem2 = new ImagemDTO();
        imagem2.setIdImagem(2L);
        imagem2.setImagemBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=");
        imagem2.setIdPortfolio(ID_PORTFOLIO);

        mockImagensLista = Arrays.asList(mockImagemDTO, imagem2);
    }

    // Testes para listarPorPortfolio
    @Test
    @DisplayName("Deve listar imagens por portfólio com sucesso")
    void deveListarImagensPorPortfolioComSucesso() {
        // Arrange
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenReturn(mockImagensLista);

        // Act
        ResponseEntity<List<ImagemDTO>> response = imagemController.listarPorPortfolio(ID_PORTFOLIO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getStatusCodeValue());
        
        List<ImagemDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
        assertEquals(ID_IMAGEM, responseBody.get(0).getIdImagem());
        assertEquals(IMAGEM_BASE64, responseBody.get(0).getImagemBase64());
        assertEquals(ID_PORTFOLIO, responseBody.get(0).getIdPortfolio());
        
        verify(imagemService).listarPorPortfolioComValidacao(ID_PORTFOLIO);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao listar imagens de portfólio sem imagens")
    void deveRetornarListaVaziaAoListarImagensPortfolioSemImagens() {
        // Arrange
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<ImagemDTO>> response = imagemController.listarPorPortfolio(ID_PORTFOLIO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<ImagemDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
        
        verify(imagemService).listarPorPortfolioComValidacao(ID_PORTFOLIO);
    }

    @Test
    @DisplayName("Deve propagar exceção ao listar imagens por portfólio")
    void devePropagarExcecaoAoListarImagensPorPortfolio() {
        // Arrange
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenThrow(new ImagemProcessamentoException("Erro ao listar imagens do portfólio"));

        // Act & Assert
        ImagemProcessamentoException exception = assertThrows(ImagemProcessamentoException.class, () -> {
            imagemController.listarPorPortfolio(ID_PORTFOLIO);
        });

        assertEquals("Erro ao listar imagens do portfólio", exception.getMessage());
        verify(imagemService).listarPorPortfolioComValidacao(ID_PORTFOLIO);
    }

    @Test
    @DisplayName("Deve listar imagens com portfólio nulo")
    void deveListarImagensComPortfolioNulo() {
        // Arrange
        Long idPortfolioNulo = null;
        when(imagemService.listarPorPortfolioComValidacao(idPortfolioNulo))
            .thenThrow(new ImagemProcessamentoException("ID do portfólio não pode ser nulo"));

        // Act & Assert
        ImagemProcessamentoException exception = assertThrows(ImagemProcessamentoException.class, () -> {
            imagemController.listarPorPortfolio(idPortfolioNulo);
        });

        assertEquals("ID do portfólio não pode ser nulo", exception.getMessage());
        verify(imagemService).listarPorPortfolioComValidacao(idPortfolioNulo);
    }

    // Testes para buscarPorId
    @Test
    @DisplayName("Deve buscar imagem por ID com sucesso")
    void deveBuscarImagemPorIdComSucesso() {
        // Arrange
        when(imagemService.buscarPorIdComValidacao(ID_IMAGEM))
            .thenReturn(mockImagemDTO);

        // Act
        ResponseEntity<ImagemDTO> response = imagemController.buscarPorId(ID_IMAGEM);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getStatusCodeValue());
        
        ImagemDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(ID_IMAGEM, responseBody.getIdImagem());
        assertEquals(IMAGEM_BASE64, responseBody.getImagemBase64());
        assertEquals(ID_PORTFOLIO, responseBody.getIdPortfolio());
        
        verify(imagemService).buscarPorIdComValidacao(ID_IMAGEM);
    }

    @Test
    @DisplayName("Deve propagar exceção ao buscar imagem inexistente")
    void devePropagarExcecaoAoBuscarImagemInexistente() {
        // Arrange
        Long idInexistente = 999L;
        when(imagemService.buscarPorIdComValidacao(idInexistente))
            .thenThrow(new ImagemNaoEncontradaException("Imagem não encontrada com ID: " + idInexistente));

        // Act & Assert
        ImagemNaoEncontradaException exception = assertThrows(ImagemNaoEncontradaException.class, () -> {
            imagemController.buscarPorId(idInexistente);
        });

        assertEquals("Imagem não encontrada com ID: " + idInexistente, exception.getMessage());
        verify(imagemService).buscarPorIdComValidacao(idInexistente);
    }

    @Test
    @DisplayName("Deve buscar imagem com ID nulo")
    void deveBuscarImagemComIdNulo() {
        // Arrange
        Long idNulo = null;
        when(imagemService.buscarPorIdComValidacao(idNulo))
            .thenThrow(new ImagemNaoEncontradaException("ID da imagem não pode ser nulo"));

        // Act & Assert
        ImagemNaoEncontradaException exception = assertThrows(ImagemNaoEncontradaException.class, () -> {
            imagemController.buscarPorId(idNulo);
        });

        assertEquals("ID da imagem não pode ser nulo", exception.getMessage());
        verify(imagemService).buscarPorIdComValidacao(idNulo);
    }

    // Testes para salvar
    @Test
    @DisplayName("Deve salvar imagem com sucesso")
    void deveSalvarImagemComSucesso() {
        // Arrange
        ImagemDTO imagemParaSalvar = new ImagemDTO();
        imagemParaSalvar.setImagemBase64(IMAGEM_BASE64);
        imagemParaSalvar.setIdPortfolio(ID_PORTFOLIO);

        when(imagemService.salvarComValidacao(imagemParaSalvar))
            .thenReturn(mockImagemDTO);

        // Act
        ResponseEntity<ImagemDTO> response = imagemController.salvar(imagemParaSalvar);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(201, response.getStatusCodeValue());
        
        ImagemDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(ID_IMAGEM, responseBody.getIdImagem());
        assertEquals(IMAGEM_BASE64, responseBody.getImagemBase64());
        assertEquals(ID_PORTFOLIO, responseBody.getIdPortfolio());
        
        verify(imagemService).salvarComValidacao(imagemParaSalvar);
    }

    @Test
    @DisplayName("Deve propagar exceção ao salvar imagem com portfólio inexistente")
    void devePropagarExcecaoAoSalvarImagemComPortfolioInexistente() {
        // Arrange
        ImagemDTO imagemComPortfolioInexistente = new ImagemDTO();
        imagemComPortfolioInexistente.setImagemBase64(IMAGEM_BASE64);
        imagemComPortfolioInexistente.setIdPortfolio(999L);

        when(imagemService.salvarComValidacao(imagemComPortfolioInexistente))
            .thenThrow(new ImagemSalvamentoException("Portfólio não encontrado com ID: 999"));

        // Act & Assert
        ImagemSalvamentoException exception = assertThrows(ImagemSalvamentoException.class, () -> {
            imagemController.salvar(imagemComPortfolioInexistente);
        });

        assertEquals("Portfólio não encontrado com ID: 999", exception.getMessage());
        verify(imagemService).salvarComValidacao(imagemComPortfolioInexistente);
    }

    @Test
    @DisplayName("Deve propagar exceção ao salvar imagem com dados inválidos")
    void devePropagarExcecaoAoSalvarImagemComDadosInvalidos() {
        // Arrange
        ImagemDTO imagemInvalida = new ImagemDTO();
        imagemInvalida.setImagemBase64("imagem_invalida");
        imagemInvalida.setIdPortfolio(ID_PORTFOLIO);

        when(imagemService.salvarComValidacao(imagemInvalida))
            .thenThrow(new ImagemSalvamentoException("Formato de imagem base64 inválido"));

        // Act & Assert
        ImagemSalvamentoException exception = assertThrows(ImagemSalvamentoException.class, () -> {
            imagemController.salvar(imagemInvalida);
        });

        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
        verify(imagemService).salvarComValidacao(imagemInvalida);
    }

    @Test
    @DisplayName("Deve salvar imagem com DTO nulo")
    void deveSalvarImagemComDTONulo() {
        // Arrange
        ImagemDTO dtoNulo = null;
        when(imagemService.salvarComValidacao(dtoNulo))
            .thenThrow(new ImagemSalvamentoException("DTO da imagem não pode ser nulo"));

        // Act & Assert
        ImagemSalvamentoException exception = assertThrows(ImagemSalvamentoException.class, () -> {
            imagemController.salvar(dtoNulo);
        });

        assertEquals("DTO da imagem não pode ser nulo", exception.getMessage());
        verify(imagemService).salvarComValidacao(dtoNulo);
    }

    @Test
    @DisplayName("Deve salvar imagem com imagem base64 nula")
    void deveSalvarImagemComImagemBase64Nula() {
        // Arrange
        ImagemDTO imagemComBase64Nulo = new ImagemDTO();
        imagemComBase64Nulo.setImagemBase64(null);
        imagemComBase64Nulo.setIdPortfolio(ID_PORTFOLIO);

        when(imagemService.salvarComValidacao(imagemComBase64Nulo))
            .thenThrow(new ImagemSalvamentoException("Imagem base64 é obrigatória"));

        // Act & Assert
        ImagemSalvamentoException exception = assertThrows(ImagemSalvamentoException.class, () -> {
            imagemController.salvar(imagemComBase64Nulo);
        });

        assertEquals("Imagem base64 é obrigatória", exception.getMessage());
        verify(imagemService).salvarComValidacao(imagemComBase64Nulo);
    }

    @Test
    @DisplayName("Deve salvar imagem com ID de portfólio nulo")
    void deveSalvarImagemComIdPortfolioNulo() {
        // Arrange
        ImagemDTO imagemComPortfolioNulo = new ImagemDTO();
        imagemComPortfolioNulo.setImagemBase64(IMAGEM_BASE64);
        imagemComPortfolioNulo.setIdPortfolio(null);

        when(imagemService.salvarComValidacao(imagemComPortfolioNulo))
            .thenThrow(new ImagemSalvamentoException("ID do portfólio é obrigatório"));

        // Act & Assert
        ImagemSalvamentoException exception = assertThrows(ImagemSalvamentoException.class, () -> {
            imagemController.salvar(imagemComPortfolioNulo);
        });

        assertEquals("ID do portfólio é obrigatório", exception.getMessage());
        verify(imagemService).salvarComValidacao(imagemComPortfolioNulo);
    }

    // Testes para deletar
    @Test
    @DisplayName("Deve deletar imagem com sucesso")
    void deveDeletarImagemComSucesso() {
        // Arrange
        doNothing().when(imagemService).deletarComValidacao(ID_IMAGEM);

        // Act
        ResponseEntity<Void> response = imagemController.deletar(ID_IMAGEM);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        
        verify(imagemService).deletarComValidacao(ID_IMAGEM);
    }

    @Test
    @DisplayName("Deve propagar exceção ao deletar imagem inexistente")
    void devePropagarExcecaoAoDeletarImagemInexistente() {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new ImagemNaoEncontradaException("Imagem não encontrada com ID: " + idInexistente))
            .when(imagemService).deletarComValidacao(idInexistente);

        // Act & Assert
        ImagemNaoEncontradaException exception = assertThrows(ImagemNaoEncontradaException.class, () -> {
            imagemController.deletar(idInexistente);
        });

        assertEquals("Imagem não encontrada com ID: " + idInexistente, exception.getMessage());
        verify(imagemService).deletarComValidacao(idInexistente);
    }

    @Test
    @DisplayName("Deve propagar exceção de remoção ao deletar imagem")
    void devePropagarExcecaoRemocaoAoDeletarImagem() {
        // Arrange
        doThrow(new ImagemRemocaoException("Erro interno ao deletar imagem"))
            .when(imagemService).deletarComValidacao(ID_IMAGEM);

        // Act & Assert
        ImagemRemocaoException exception = assertThrows(ImagemRemocaoException.class, () -> {
            imagemController.deletar(ID_IMAGEM);
        });

        assertEquals("Erro interno ao deletar imagem", exception.getMessage());
        verify(imagemService).deletarComValidacao(ID_IMAGEM);
    }

    @Test
    @DisplayName("Deve deletar imagem com ID nulo")
    void deveDeletarImagemComIdNulo() {
        // Arrange
        Long idNulo = null;
        doThrow(new ImagemNaoEncontradaException("ID da imagem não pode ser nulo"))
            .when(imagemService).deletarComValidacao(idNulo);

        // Act & Assert
        ImagemNaoEncontradaException exception = assertThrows(ImagemNaoEncontradaException.class, () -> {
            imagemController.deletar(idNulo);
        });

        assertEquals("ID da imagem não pode ser nulo", exception.getMessage());
        verify(imagemService).deletarComValidacao(idNulo);
    }

    // Testes adicionais para casos edge e cobertura 100%
    @Test
    @DisplayName("Deve listar uma única imagem do portfólio")
    void deveListarUnicaImagemDoPortfolio() {
        // Arrange
        List<ImagemDTO> listaComUmaImagem = Arrays.asList(mockImagemDTO);
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenReturn(listaComUmaImagem);

        // Act
        ResponseEntity<List<ImagemDTO>> response = imagemController.listarPorPortfolio(ID_PORTFOLIO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<ImagemDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals(mockImagemDTO.getIdImagem(), responseBody.get(0).getIdImagem());
        
        verify(imagemService).listarPorPortfolioComValidacao(ID_PORTFOLIO);
    }

    @Test
    @DisplayName("Deve retornar resposta com diferentes tipos de exceções no service")
    void deveRetornarRespostaComDiferentesTiposExcecoesNoService() {
        // Arrange & Act & Assert para ImagemProcessamentoException
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenThrow(new ImagemProcessamentoException("Erro de processamento"));

        ImagemProcessamentoException processingException = assertThrows(ImagemProcessamentoException.class, () -> {
            imagemController.listarPorPortfolio(ID_PORTFOLIO);
        });
        assertEquals("Erro de processamento", processingException.getMessage());

        // Reset mock
        reset(imagemService);

        // Arrange & Act & Assert para RuntimeException genérica
        when(imagemService.buscarPorIdComValidacao(ID_IMAGEM))
            .thenThrow(new RuntimeException("Erro genérico"));

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            imagemController.buscarPorId(ID_IMAGEM);
        });
        assertEquals("Erro genérico", runtimeException.getMessage());
    }

    @Test
    @DisplayName("Deve validar fluxo completo de operações CRUD")
    void deveValidarFluxoCompletoOperacoesCRUD() {
        // Arrange
        ImagemDTO novaImagem = new ImagemDTO();
        novaImagem.setImagemBase64(IMAGEM_BASE64);
        novaImagem.setIdPortfolio(ID_PORTFOLIO);

        ImagemDTO imagemSalva = new ImagemDTO();
        imagemSalva.setIdImagem(ID_IMAGEM);
        imagemSalva.setImagemBase64(IMAGEM_BASE64);
        imagemSalva.setIdPortfolio(ID_PORTFOLIO);

        // Mock para salvar
        when(imagemService.salvarComValidacao(novaImagem)).thenReturn(imagemSalva);
        
        // Mock para buscar
        when(imagemService.buscarPorIdComValidacao(ID_IMAGEM)).thenReturn(imagemSalva);
        
        // Mock para listar
        when(imagemService.listarPorPortfolioComValidacao(ID_PORTFOLIO))
            .thenReturn(Arrays.asList(imagemSalva));
        
        // Mock para deletar
        doNothing().when(imagemService).deletarComValidacao(ID_IMAGEM);

        // Act & Assert - CREATE
        ResponseEntity<ImagemDTO> createResponse = imagemController.salvar(novaImagem);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        // Act & Assert - READ
        ResponseEntity<ImagemDTO> readResponse = imagemController.buscarPorId(ID_IMAGEM);
        assertEquals(HttpStatus.OK, readResponse.getStatusCode());
        assertEquals(ID_IMAGEM, readResponse.getBody().getIdImagem());

        // Act & Assert - LIST
        ResponseEntity<List<ImagemDTO>> listResponse = imagemController.listarPorPortfolio(ID_PORTFOLIO);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertEquals(1, listResponse.getBody().size());

        // Act & Assert - DELETE
        ResponseEntity<Void> deleteResponse = imagemController.deletar(ID_IMAGEM);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify all operations
        verify(imagemService).salvarComValidacao(novaImagem);
        verify(imagemService).buscarPorIdComValidacao(ID_IMAGEM);
        verify(imagemService).listarPorPortfolioComValidacao(ID_PORTFOLIO);
        verify(imagemService).deletarComValidacao(ID_IMAGEM);
    }

    @Test
    @DisplayName("Deve garantir que todos os métodos HTTP estão sendo testados")
    void deveGarantirQueTodosMetodosHTTPEstaoSendoTestados() {
        // Este teste assegura que todos os endpoints HTTP são cobertos
        
        // GET /portfolio/{id} - testado em listarPorPortfolio
        when(imagemService.listarPorPortfolioComValidacao(anyLong()))
            .thenReturn(mockImagensLista);
        
        ResponseEntity<List<ImagemDTO>> getListResponse = imagemController.listarPorPortfolio(ID_PORTFOLIO);
        assertEquals(HttpStatus.OK, getListResponse.getStatusCode());

        // GET /{id} - testado em buscarPorId
        when(imagemService.buscarPorIdComValidacao(anyLong()))
            .thenReturn(mockImagemDTO);
        
        ResponseEntity<ImagemDTO> getResponse = imagemController.buscarPorId(ID_IMAGEM);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        // POST / - testado em salvar
        when(imagemService.salvarComValidacao(any(ImagemDTO.class)))
            .thenReturn(mockImagemDTO);
        
        ResponseEntity<ImagemDTO> postResponse = imagemController.salvar(mockImagemDTO);
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());

        // DELETE /{id} - testado em deletar
        doNothing().when(imagemService).deletarComValidacao(anyLong());
        
        ResponseEntity<Void> deleteResponse = imagemController.deletar(ID_IMAGEM);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
} 