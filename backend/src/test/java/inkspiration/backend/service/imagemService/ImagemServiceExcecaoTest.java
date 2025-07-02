package inkspiration.backend.service.imagemService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.exception.imagem.ImagemNaoEncontradaException;
import inkspiration.backend.exception.imagem.ImagemProcessamentoException;
import inkspiration.backend.exception.imagem.ImagemRemocaoException;
import inkspiration.backend.exception.imagem.ImagemSalvamentoException;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.service.ImagemService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de exceções - ImagemService")
class ImagemServiceExcecaoTest {

    @Mock
    private ImagemRepository imagemRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    private ImagemService imagemService;

    @BeforeEach
    void setUp() {
        imagemService = new ImagemService(imagemRepository, portfolioRepository);
    }

    @Test
    @DisplayName("Deve lançar ImagemProcessamentoException ao listar imagens com erro de banco")
    void deveLancarImagemProcessamentoExceptionAoListarImagensComErroBanco() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenThrow(new RuntimeException("Erro de conexão com banco"));

        
        ImagemProcessamentoException exception = assertThrows(
            ImagemProcessamentoException.class,
            () -> imagemService.listarPorPortfolioComValidacao(idPortfolio)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao listar imagens do portfólio"));
        assertTrue(exception.getMessage().contains("Erro de conexão com banco"));
        verify(imagemRepository).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve lançar ImagemNaoEncontradaException ao buscar por ID inexistente")
    void deveLancarImagemNaoEncontradaExceptionAoBuscarPorIdInexistente() {
        
        Long idImagem = 999L;
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.empty());

        
        ImagemNaoEncontradaException exception = assertThrows(
            ImagemNaoEncontradaException.class,
            () -> imagemService.buscarPorIdComValidacao(idImagem)
        );
        
        assertEquals("Imagem não encontrada com ID: " + idImagem, exception.getMessage());
        verify(imagemRepository).findById(idImagem);
    }

    @Test
    @DisplayName("Deve lançar ImagemSalvamentoException quando portfólio não existe")
    void deveLancarImagemSalvamentoExceptionQuandoPortfolioNaoExiste() {
        
        Long idPortfolio = 999L;
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        ImagemSalvamentoException exception = assertThrows(
            ImagemSalvamentoException.class,
            () -> imagemService.salvarComValidacao(dto)
        );
        
        assertEquals("Portfólio não encontrado com ID: " + idPortfolio, exception.getMessage());
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository, never()).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve lançar ImagemSalvamentoException para erro genérico ao salvar")
    void deveLancarImagemSalvamentoExceptionParaErroGenericoAoSalvar() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenThrow(new RuntimeException("Erro ao salvar no banco"));

        
        ImagemSalvamentoException exception = assertThrows(
            ImagemSalvamentoException.class,
            () -> imagemService.salvarComValidacao(dto)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao salvar imagem"));
        assertTrue(exception.getMessage().contains("Erro ao salvar no banco"));
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve relançar ImagemNaoEncontradaException ao deletar imagem inexistente")
    void deveRelancarImagemNaoEncontradaExceptionAoDeletarImagemInexistente() {
        
        Long idImagem = 999L;
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.empty());

        
        ImagemNaoEncontradaException exception = assertThrows(
            ImagemNaoEncontradaException.class,
            () -> imagemService.deletarComValidacao(idImagem)
        );
        
        assertEquals("Imagem não encontrada com ID: " + idImagem, exception.getMessage());
        verify(imagemRepository).findById(idImagem);
        verify(imagemRepository, never()).delete(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve lançar ImagemRemocaoException para erro genérico ao deletar")
    void deveLancarImagemRemocaoExceptionParaErroGenericoAoDeletar() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));
        doThrow(new RuntimeException("Erro ao deletar do banco"))
            .when(imagemRepository).delete(imagem);

        
        ImagemRemocaoException exception = assertThrows(
            ImagemRemocaoException.class,
            () -> imagemService.deletarComValidacao(idImagem)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao deletar imagem"));
        assertTrue(exception.getMessage().contains("Erro ao deletar do banco"));
        verify(imagemRepository).findById(idImagem);
        verify(imagemRepository).delete(imagem);
    }

    @Test
    @DisplayName("Deve capturar e tratar exceção de timeout no banco")
    void deveCapturaETratatExcecaoDeTimeoutNoBanco() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenThrow(new RuntimeException("Connection timeout"));

        
        ImagemProcessamentoException exception = assertThrows(
            ImagemProcessamentoException.class,
            () -> imagemService.listarPorPortfolioComValidacao(idPortfolio)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao listar imagens do portfólio"));
        assertTrue(exception.getMessage().contains("Connection timeout"));
    }

    @Test
    @DisplayName("Deve capturar e tratar exceção de violação de constraint")
    void deveCapturaETratatExcecaoDeViolacaoConstraint() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenThrow(new RuntimeException("Constraint violation"));

        
        ImagemSalvamentoException exception = assertThrows(
            ImagemSalvamentoException.class,
            () -> imagemService.salvarComValidacao(dto)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao salvar imagem"));
        assertTrue(exception.getMessage().contains("Constraint violation"));
    }

    @Test
    @DisplayName("Deve capturar e tratar exceção de lock de banco")
    void deveCapturaETratatExcecaoDeLockBanco() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));
        doThrow(new RuntimeException("Deadlock detected"))
            .when(imagemRepository).delete(imagem);

        
        ImagemRemocaoException exception = assertThrows(
            ImagemRemocaoException.class,
            () -> imagemService.deletarComValidacao(idImagem)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao deletar imagem"));
        assertTrue(exception.getMessage().contains("Deadlock detected"));
    }

    @Test
    @DisplayName("Deve manter integridade transacional em caso de erro")
    void deveManterIntegridadeTransacionalEmCasoDeErro() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenThrow(new RuntimeException("Transação falhou"));

        
        ImagemSalvamentoException exception = assertThrows(
            ImagemSalvamentoException.class,
            () -> imagemService.salvarComValidacao(dto)
        );
        
        assertNotNull(exception);
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve tratar exceção de acesso negado ao banco")
    void deveTratarExcecaoDeAcessoNegadoAoBanco() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenThrow(new RuntimeException("Access denied"));

        
        ImagemProcessamentoException exception = assertThrows(
            ImagemProcessamentoException.class,
            () -> imagemService.listarPorPortfolioComValidacao(idPortfolio)
        );
        
        assertTrue(exception.getMessage().contains("Erro ao listar imagens do portfólio"));
        assertTrue(exception.getMessage().contains("Access denied"));
    }

    
    private Portfolio criarPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        return portfolio;
    }

    private Imagem criarImagem(Long id, String base64, Portfolio portfolio) {
        Imagem imagem = new Imagem();
        imagem.setIdImagem(id);
        
        String base64Valido = base64.startsWith("data:image/") ? 
            base64 : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        imagem.setImagemBase64(base64Valido);
        imagem.setPortfolio(portfolio);
        return imagem;
    }
} 