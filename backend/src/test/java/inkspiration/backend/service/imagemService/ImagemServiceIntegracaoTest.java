package inkspiration.backend.service.imagemService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
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
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.service.ImagemService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de integração - ImagemService")
class ImagemServiceIntegracaoTest {

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
    @DisplayName("Deve executar fluxo completo de criação de imagem")
    void deveExecutarFluxoCompletoDecriacaoImagem() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64(imagemBase64);
        dto.setIdPortfolio(idPortfolio);
        
        Imagem imagemSalva = criarImagem(1L, imagemBase64, portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenReturn(imagemSalva);
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList(imagemSalva));

        
        ImagemDTO imagemCriada = imagemService.salvarComValidacao(dto);
        List<ImagemDTO> imagensDoPortfolio = imagemService.listarPorPortfolioComValidacao(idPortfolio);

        
        assertNotNull(imagemCriada);
        assertEquals(1L, imagemCriada.getIdImagem());
        assertEquals(imagemBase64, imagemCriada.getImagemBase64());
        assertEquals(idPortfolio, imagemCriada.getIdPortfolio());
        
        assertEquals(1, imagensDoPortfolio.size());
        assertEquals(imagemCriada.getIdImagem(), imagensDoPortfolio.get(0).getIdImagem());
    }

    @Test
    @DisplayName("Deve executar fluxo completo de remoção de imagem")
    void deveExecutarFluxoCompletoDeRemocaoImagem() {
        
        Long idImagem = 1L;
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem))
            .thenReturn(Optional.empty());
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList(imagem))
            .thenReturn(Arrays.asList());

        
        List<ImagemDTO> imagensAntes = imagemService.listarPorPortfolioComValidacao(idPortfolio);
        imagemService.deletarComValidacao(idImagem);
        List<ImagemDTO> imagensDepois = imagemService.listarPorPortfolioComValidacao(idPortfolio);

        
        assertEquals(1, imagensAntes.size());
        assertEquals(0, imagensDepois.size());
        
        verify(imagemRepository).delete(imagem);
        
        
        assertThrows(ImagemNaoEncontradaException.class, 
            () -> imagemService.buscarPorIdComValidacao(idImagem));
    }

    @Test
    @DisplayName("Deve validar integridade referencial entre imagem e portfólio")
    void deveValidarIntegridadeReferencialEntreImagemEPortfolio() {
        
        Long idPortfolio = 1L;
        Long idImagemOrfa = 999L;
        
        Portfolio portfolio = criarPortfolio(idPortfolio);
        Imagem imagemValida = criarImagem(1L, "base64data", portfolio);
        
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList(imagemValida));
        when(imagemRepository.findById(1L))
            .thenReturn(Optional.of(imagemValida));
        when(imagemRepository.findById(idImagemOrfa))
            .thenReturn(Optional.empty());

        
        List<ImagemDTO> imagensDoPortfolio = imagemService.listarPorPortfolioComValidacao(idPortfolio);
        ImagemDTO imagemEncontrada = imagemService.buscarPorIdComValidacao(1L);
        
        assertEquals(1, imagensDoPortfolio.size());
        assertEquals(idPortfolio, imagemEncontrada.getIdPortfolio());
        
        
        assertThrows(ImagemNaoEncontradaException.class,
            () -> imagemService.buscarPorIdComValidacao(idImagemOrfa));
    }

    @Test
    @DisplayName("Deve tratar cenário de portfólio sem imagens")
    void deveTratarCenarioDePortfolioSemImagens() {
        
        Long idPortfolioVazio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolioVazio))
            .thenReturn(Arrays.asList());

        
        List<ImagemDTO> imagens = imagemService.listarPorPortfolioComValidacao(idPortfolioVazio);

        
        assertNotNull(imagens);
        assertTrue(imagens.isEmpty());
        verify(imagemRepository).findByPortfolioIdPortfolio(idPortfolioVazio);
    }

    @Test
    @DisplayName("Deve processar diferentes formatos de imagem")
    void deveProcessarDiferentesFormatosDeImagem() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        List<Imagem> imagensDiferentes = Arrays.asList(
            criarImagem(1L, "data:image/png;base64,iVBORw0KGgo=", portfolio),
            criarImagem(2L, "data:image/jpeg;base64,/9j/4AAQSkZ=", portfolio),
            criarImagem(3L, "data:image/gif;base64,R0lGODlhAQA=", portfolio),
            criarImagem(4L, "data:image/webp;base64,UklGRiIA=", portfolio)
        );
        
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(imagensDiferentes);

        
        List<ImagemDTO> resultado = imagemService.listarPorPortfolioComValidacao(idPortfolio);

        
        assertEquals(4, resultado.size());
        
        
        assertTrue(resultado.stream().anyMatch(img -> img.getImagemBase64().contains("png")));
        assertTrue(resultado.stream().anyMatch(img -> img.getImagemBase64().contains("jpeg")));
        assertTrue(resultado.stream().anyMatch(img -> img.getImagemBase64().contains("gif")));
        assertTrue(resultado.stream().anyMatch(img -> img.getImagemBase64().contains("webp")));
    }

    @Test
    @DisplayName("Deve validar transações e rollback em caso de erro")
    void deveValidarTransacoesERollbackEmCasoDeErro() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenThrow(new RuntimeException("Falha na transação"));

        
        assertThrows(ImagemSalvamentoException.class,
            () -> imagemService.salvarComValidacao(dto));
        
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository).save(any(Imagem.class));
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