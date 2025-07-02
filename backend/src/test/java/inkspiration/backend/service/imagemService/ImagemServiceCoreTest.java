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
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.service.ImagemService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes core - ImagemService")
class ImagemServiceCoreTest {

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
    @DisplayName("Deve listar imagens por portfólio com sucesso")
    void deveListarImagensPorPortfolioComSucesso() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        List<Imagem> imagens = Arrays.asList(
            criarImagem(1L, "base64data1", portfolio),
            criarImagem(2L, "base64data2", portfolio),
            criarImagem(3L, "base64data3", portfolio)
        );
        
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(imagens);

        
        List<ImagemDTO> resultado = imagemService.listarPorPortfolio(idPortfolio);

        
        assertEquals(3, resultado.size());
        
        for (int i = 0; i < imagens.size(); i++) {
            ImagemDTO dto = resultado.get(i);
            Imagem imagem = imagens.get(i);
            
            assertEquals(imagem.getIdImagem(), dto.getIdImagem());
            assertEquals(imagem.getImagemBase64(), dto.getImagemBase64());
            assertEquals(imagem.getPortfolio().getIdPortfolio(), dto.getIdPortfolio());
        }
        
        verify(imagemRepository).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há imagens no portfólio")
    void deveRetornarListaVaziaQuandoNaoHaImagensNoPortfolio() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());

        
        List<ImagemDTO> resultado = imagemService.listarPorPortfolio(idPortfolio);

        
        assertTrue(resultado.isEmpty());
        verify(imagemRepository).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve buscar imagem por ID com sucesso")
    void deveBuscarImagemPorIdComSucesso() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));

        
        ImagemDTO resultado = imagemService.buscarPorId(idImagem);

        
        assertNotNull(resultado);
        assertEquals(imagem.getIdImagem(), resultado.getIdImagem());
        assertEquals(imagem.getImagemBase64(), resultado.getImagemBase64());
        assertEquals(imagem.getPortfolio().getIdPortfolio(), resultado.getIdPortfolio());
        
        verify(imagemRepository).findById(idImagem);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar imagem inexistente")
    void deveLancarExcecaoAoBuscarImagemInexistente() {
        
        Long idImagem = 999L;
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.empty());

        
        ImagemNaoEncontradaException exception = assertThrows(
            ImagemNaoEncontradaException.class,
            () -> imagemService.buscarPorId(idImagem)
        );
        
        assertEquals("Imagem não encontrada com ID: " + idImagem, exception.getMessage());
        verify(imagemRepository).findById(idImagem);
    }

    @Test
    @DisplayName("Deve salvar imagem com sucesso")
    void deveSalvarImagemComSucesso() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        Imagem imagemSalva = criarImagem(1L, dto.getImagemBase64(), portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(imagemRepository.save(any(Imagem.class)))
            .thenReturn(imagemSalva);

        
        ImagemDTO resultado = imagemService.salvar(dto);

        
        assertNotNull(resultado);
        assertEquals(imagemSalva.getIdImagem(), resultado.getIdImagem());
        assertEquals(imagemSalva.getImagemBase64(), resultado.getImagemBase64());
        assertEquals(imagemSalva.getPortfolio().getIdPortfolio(), resultado.getIdPortfolio());
        
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar imagem com portfólio inexistente")
    void deveLancarExcecaoAoSalvarImagemComPortfolioInexistente() {
        
        Long idPortfolio = 999L;
        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> imagemService.salvar(dto)
        );
        
        assertEquals("Portifólio não encontrado com ID: " + idPortfolio, exception.getMessage());
        verify(portfolioRepository).findById(idPortfolio);
        verify(imagemRepository, never()).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve deletar imagem com sucesso")
    void deveDeletarImagemComSucesso() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));

        
        imagemService.deletar(idImagem);

        
        verify(imagemRepository).findById(idImagem);
        verify(imagemRepository).delete(imagem);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar imagem inexistente")
    void deveLancarExcecaoAoDeletarImagemInexistente() {
        
        Long idImagem = 999L;
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.empty());

        
        ImagemNaoEncontradaException exception = assertThrows(
            ImagemNaoEncontradaException.class,
            () -> imagemService.deletar(idImagem)
        );
        
        assertEquals("Imagem não encontrada com ID: " + idImagem, exception.getMessage());
        verify(imagemRepository).findById(idImagem);
        verify(imagemRepository, never()).delete(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve converter entidade para DTO corretamente")
    void deveConverterEntidadeParaDTOCorretamente() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        Imagem imagemOriginal = criarImagem(1L, "base64data", portfolio);
        
        when(imagemRepository.findById(1L))
            .thenReturn(Optional.of(imagemOriginal));

        
        ImagemDTO resultado = imagemService.buscarPorId(1L);

        
        assertEquals(imagemOriginal.getIdImagem(), resultado.getIdImagem());
        assertEquals(imagemOriginal.getImagemBase64(), resultado.getImagemBase64());
        assertEquals(imagemOriginal.getPortfolio().getIdPortfolio(), resultado.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve processar múltiplas imagens do mesmo portfólio")
    void deveProcessarMultiplasImagensDoMesmoPortfolio() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        List<Imagem> imagens = Arrays.asList(
            criarImagem(1L, "data:image/png;base64,iVBOR1=", portfolio),
            criarImagem(2L, "data:image/jpeg;base64,/9j/4AA=", portfolio),
            criarImagem(3L, "data:image/gif;base64,R0lGOD=", portfolio)
        );
        
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(imagens);

        
        List<ImagemDTO> resultado = imagemService.listarPorPortfolio(idPortfolio);

        
        assertEquals(3, resultado.size());
        
        
        for (ImagemDTO dto : resultado) {
            assertEquals(idPortfolio, dto.getIdPortfolio());
        }
        
        
        assertTrue(resultado.get(0).getImagemBase64().contains("png"));
        assertTrue(resultado.get(1).getImagemBase64().contains("jpeg"));
        assertTrue(resultado.get(2).getImagemBase64().contains("gif"));
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