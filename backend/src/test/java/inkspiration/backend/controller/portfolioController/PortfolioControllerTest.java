package inkspiration.backend.controller.portfolioController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import inkspiration.backend.controller.PortfolioController;
import inkspiration.backend.service.PortfolioService;
import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.exception.portfolio.*;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioController - Testes Completos")
class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private PortfolioDTO mockPortfolioDTO;
    private List<PortfolioDTO> mockPortfolioList;

    @BeforeEach
    void setUp() {
        // Setup mock portfolio DTO
        mockPortfolioDTO = new PortfolioDTO(
            1L, // idPortfolio
            1L, // idProfissional
            "Biografia profissional detalhada", // descricao
            "5 anos de experiência", // experiencia
            "Tatuagem realista", // especialidade
            "https://website.com", // website
            "@tiktok", // tiktok
            "@instagram", // instagram
            "facebook", // facebook
            "@twitter" // twitter
        );

        // Setup mock portfolio list
        mockPortfolioList = Arrays.asList(mockPortfolioDTO);
    }

    // Testes para listar()
    @Test
    @DisplayName("Deve listar portfolios com sucesso")
    void deveListarPortfoliosComSucesso() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        when(portfolioService.listarComAutorizacao(pageable))
            .thenReturn(mockPortfolioList);

        // Act
        ResponseEntity<List<PortfolioDTO>> response = portfolioController.listar(page, size);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPortfolioList, response.getBody());
        verify(portfolioService).listarComAutorizacao(pageable);
    }

    @Test
    @DisplayName("Deve listar portfolios com paginação personalizada")
    void deveListarPortfoliosComPaginacaoPersonalizada() {
        // Arrange
        int page = 2;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);
        when(portfolioService.listarComAutorizacao(pageable))
            .thenReturn(mockPortfolioList);

        // Act
        ResponseEntity<List<PortfolioDTO>> response = portfolioController.listar(page, size);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPortfolioList, response.getBody());
        verify(portfolioService).listarComAutorizacao(pageable);
    }

    // Testes para buscarPorId()
    @Test
    @DisplayName("Deve buscar portfolio por ID com sucesso")
    void deveBuscarPortfolioPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        when(portfolioService.buscarPorIdComValidacao(id))
            .thenReturn(mockPortfolioDTO);

        // Act
        ResponseEntity<PortfolioDTO> response = portfolioController.buscarPorId(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPortfolioDTO, response.getBody());
        verify(portfolioService).buscarPorIdComValidacao(id);
    }

    @Test
    @DisplayName("Deve propagar exceção ao buscar portfolio inexistente")
    void deveLancarExcecaoAoBuscarPortfolioInexistente() {
        // Arrange
        Long id = 999L;
        when(portfolioService.buscarPorIdComValidacao(id))
            .thenThrow(new PortfolioNaoEncontradoException("Portfolio não encontrado"));

        // Act & Assert
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioController.buscarPorId(id)
        );

        assertEquals("Portfolio não encontrado", exception.getMessage());
        verify(portfolioService).buscarPorIdComValidacao(id);
    }

    // Testes para atualizar()
    @Test
    @DisplayName("Deve atualizar portfolio com sucesso")
    void deveAtualizarPortfolioComSucesso() {
        // Arrange
        Long id = 1L;
        when(portfolioService.atualizarComValidacao(eq(id), any(PortfolioDTO.class)))
            .thenReturn(mockPortfolioDTO);

        // Act
        ResponseEntity<PortfolioDTO> response = portfolioController.atualizar(id, mockPortfolioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPortfolioDTO, response.getBody());
        verify(portfolioService).atualizarComValidacao(id, mockPortfolioDTO);
    }

    @Test
    @DisplayName("Deve propagar exceção ao atualizar portfolio inexistente")
    void deveLancarExcecaoAoAtualizarPortfolioInexistente() {
        // Arrange
        Long id = 999L;
        when(portfolioService.atualizarComValidacao(eq(id), any(PortfolioDTO.class)))
            .thenThrow(new PortfolioNaoEncontradoException("Portfolio não encontrado"));

        // Act & Assert
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioController.atualizar(id, mockPortfolioDTO)
        );

        assertEquals("Portfolio não encontrado", exception.getMessage());
        verify(portfolioService).atualizarComValidacao(id, mockPortfolioDTO);
    }

    @Test
    @DisplayName("Deve propagar exceção ao falhar atualização de portfolio")
    void deveLancarExcecaoAoFalharAtualizacaoPortfolio() {
        // Arrange
        Long id = 1L;
        when(portfolioService.atualizarComValidacao(eq(id), any(PortfolioDTO.class)))
            .thenThrow(new PortfolioAtualizacaoException("Erro ao atualizar portfolio"));

        // Act & Assert
        PortfolioAtualizacaoException exception = assertThrows(
            PortfolioAtualizacaoException.class,
            () -> portfolioController.atualizar(id, mockPortfolioDTO)
        );

        assertEquals("Erro ao atualizar portfolio", exception.getMessage());
        verify(portfolioService).atualizarComValidacao(id, mockPortfolioDTO);
    }

    // Testes para deletar()
    @Test
    @DisplayName("Deve deletar portfolio com sucesso")
    void deveDeletarPortfolioComSucesso() {
        // Arrange
        Long id = 1L;
        doNothing().when(portfolioService).deletarComValidacao(id);

        // Act
        ResponseEntity<Void> response = portfolioController.deletar(id);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(portfolioService).deletarComValidacao(id);
    }

    @Test
    @DisplayName("Deve propagar exceção ao deletar portfolio inexistente")
    void deveLancarExcecaoAoDeletarPortfolioInexistente() {
        // Arrange
        Long id = 999L;
        doThrow(new PortfolioNaoEncontradoException("Portfolio não encontrado"))
            .when(portfolioService).deletarComValidacao(id);

        // Act & Assert
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioController.deletar(id)
        );

        assertEquals("Portfolio não encontrado", exception.getMessage());
        verify(portfolioService).deletarComValidacao(id);
    }

    @Test
    @DisplayName("Deve propagar exceção ao falhar deleção de portfolio")
    void deveLancarExcecaoAoFalharDelecaoPortfolio() {
        // Arrange
        Long id = 1L;
        doThrow(new PortfolioRemocaoException("Erro ao deletar portfolio"))
            .when(portfolioService).deletarComValidacao(id);

        // Act & Assert
        PortfolioRemocaoException exception = assertThrows(
            PortfolioRemocaoException.class,
            () -> portfolioController.deletar(id)
        );

        assertEquals("Erro ao deletar portfolio", exception.getMessage());
        verify(portfolioService).deletarComValidacao(id);
    }
} 