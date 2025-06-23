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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.service.ImagemService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de mock - ImagemService")
class ImagemServiceMockTest {

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
    @DisplayName("Deve chamar repositório correto ao listar imagens por portfólio")
    void deveChamarRepositorioCorretoAoListarImagensPorPortfolio() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());

        
        imagemService.listarPorPortfolio(idPortfolio);

        
        verify(imagemRepository, times(1)).findByPortfolioIdPortfolio(idPortfolio);
        verifyNoMoreInteractions(imagemRepository);
        verifyNoInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("Deve capturar parâmetros corretos ao buscar por ID")
    void deveCapturaParametrosCorretosAoBuscarPorId() {
        
        Long idImagem = 123L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        
        imagemService.buscarPorId(idImagem);

        
        verify(imagemRepository).findById(idCaptor.capture());
        assertEquals(idImagem, idCaptor.getValue());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao salvar imagem")
    void deveVerificarOrdemChamadasAoSalvarImagem() {
        
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

        
        imagemService.salvar(dto);

        
        var inOrder = inOrder(portfolioRepository, imagemRepository);
        inOrder.verify(portfolioRepository).findById(idPortfolio);
        inOrder.verify(imagemRepository).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve capturar entidade correta ao salvar")
    void deveCapturaEntidadeCorretaAoSalvar() {
        
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

        ArgumentCaptor<Imagem> imagemCaptor = ArgumentCaptor.forClass(Imagem.class);

        
        imagemService.salvar(dto);

        
        verify(imagemRepository).save(imagemCaptor.capture());
        
        Imagem imagemCapturada = imagemCaptor.getValue();
        assertEquals(imagemBase64, imagemCapturada.getImagemBase64());
        assertEquals(portfolio, imagemCapturada.getPortfolio());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao deletar")
    void deveVerificarOrdemChamadasAoDeletar() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));

        
        imagemService.deletar(idImagem);

        
        var inOrder = inOrder(imagemRepository);
        inOrder.verify(imagemRepository).findById(idImagem);
        inOrder.verify(imagemRepository).delete(imagem);
    }

    @Test
    @DisplayName("Deve capturar entidade correta ao deletar")
    void deveCapturaEntidadeCorretaAoDeletar() {
        
        Long idImagem = 1L;
        Portfolio portfolio = criarPortfolio(1L);
        Imagem imagem = criarImagem(idImagem, "base64data", portfolio);
        
        when(imagemRepository.findById(idImagem))
            .thenReturn(Optional.of(imagem));

        ArgumentCaptor<Imagem> imagemCaptor = ArgumentCaptor.forClass(Imagem.class);

        
        imagemService.deletar(idImagem);

        
        verify(imagemRepository).delete(imagemCaptor.capture());
        assertEquals(imagem, imagemCaptor.getValue());
    }

    @Test
    @DisplayName("Deve configurar mock para múltiplas chamadas")
    void deveConfigurarMockParaMultiplasChamadas() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        List<Imagem> primeiraLista = Arrays.asList(
            criarImagem(1L, "base64data1", portfolio)
        );
        
        List<Imagem> segundaLista = Arrays.asList(
            criarImagem(1L, "base64data1", portfolio),
            criarImagem(2L, "base64data2", portfolio)
        );
        
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(primeiraLista)
            .thenReturn(segundaLista);

        
        List<ImagemDTO> primeiroResultado = imagemService.listarPorPortfolio(idPortfolio);
        List<ImagemDTO> segundoResultado = imagemService.listarPorPortfolio(idPortfolio);

        
        assertEquals(1, primeiroResultado.size());
        assertEquals(2, segundoResultado.size());
        verify(imagemRepository, times(2)).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve resetar mock entre testes")
    void deveResetarMockEntreTestes() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());

        
        imagemService.listarPorPortfolio(idPortfolio);
        
        
        reset(imagemRepository);
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());
        
        
        imagemService.listarPorPortfolio(idPortfolio);

        
        verify(imagemRepository, times(1)).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve verificar que não há interações desnecessárias")
    void deveVerificarQueNaoHaInteracoesDesnecessarias() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());

        
        imagemService.listarPorPortfolio(idPortfolio);

        
        verify(imagemRepository, only()).findByPortfolioIdPortfolio(idPortfolio);
        verifyNoInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("Deve usar argumentMatchers corretamente")
    void deveUsarArgumentMatchersCorretamente() {
        
        when(imagemRepository.findByPortfolioIdPortfolio(anyLong()))
            .thenReturn(Arrays.asList());
        when(portfolioRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarPortfolio(1L)));
        when(imagemRepository.save(any(Imagem.class)))
            .thenReturn(criarImagem(1L, "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=", criarPortfolio(1L)));

        ImagemDTO dto = new ImagemDTO();
        dto.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        dto.setIdPortfolio(1L);

        
        imagemService.listarPorPortfolio(1L);
        imagemService.salvar(dto);

        
        verify(imagemRepository).findByPortfolioIdPortfolio(anyLong());
        verify(portfolioRepository).findById(anyLong());
        verify(imagemRepository).save(any(Imagem.class));
    }

    @Test
    @DisplayName("Deve configurar stub para retornar exception")
    void deveConfigurarStubParaRetornarException() {
        
        Long idImagem = 1L;
        when(imagemRepository.findById(idImagem))
            .thenThrow(new RuntimeException("Erro simulado"));

        
        assertThrows(RuntimeException.class, () -> imagemService.buscarPorId(idImagem));
        verify(imagemRepository).findById(idImagem);
    }

    @Test
    @DisplayName("Deve verificar invocação de métodos com timeout")
    void deveVerificarInvocacaoMetodosComTimeout() {
        
        Long idPortfolio = 1L;
        when(imagemRepository.findByPortfolioIdPortfolio(idPortfolio))
            .thenReturn(Arrays.asList());

        
        imagemService.listarPorPortfolio(idPortfolio);

        
        verify(imagemRepository, timeout(1000)).findByPortfolioIdPortfolio(idPortfolio);
    }

    @Test
    @DisplayName("Deve capturar múltiplos argumentos em sequência")
    void deveCapturaMultiplosArgumentosEmSequencia() {
        
        when(imagemRepository.findByPortfolioIdPortfolio(anyLong()))
            .thenReturn(Arrays.asList());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        
        imagemService.listarPorPortfolio(1L);
        imagemService.listarPorPortfolio(2L);
        imagemService.listarPorPortfolio(3L);

        
        verify(imagemRepository, times(3)).findByPortfolioIdPortfolio(captor.capture());
        
        List<Long> valoresCapturados = captor.getAllValues();
        assertEquals(Arrays.asList(1L, 2L, 3L), valoresCapturados);
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