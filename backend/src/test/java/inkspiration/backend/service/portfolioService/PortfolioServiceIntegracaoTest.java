package inkspiration.backend.service.portfolioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.exception.portfolio.PortfolioAtualizacaoException;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.portfolio.PortfolioRemocaoException;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.PortfolioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceIntegracaoTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(authorizationService).requireAdmin();
    }

    @Test
    @DisplayName("Deve executar fluxo completo de criação de portfolio")
    void deveExecutarFluxoCompletoDecricaoPortfolio() {
        
        Long idProfissional = 1L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolioSalvo = criarPortfolio(1L);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);
        when(portfolioRepository.findById(1L))
            .thenReturn(Optional.of(portfolioSalvo));

        
        Portfolio portfolioCriado = portfolioService.criar(dto);
        PortfolioDTO portfolioBuscado = portfolioService.buscarPorIdComValidacao(1L);

        
        assertNotNull(portfolioCriado);
        assertEquals(1L, portfolioCriado.getIdPortfolio());
        assertEquals(dto.getDescricao(), portfolioCriado.getDescricao());
        
        assertNotNull(portfolioBuscado);
        assertEquals(1L, portfolioBuscado.getIdPortfolio());
        assertEquals(dto.getDescricao(), portfolioBuscado.getDescricao());
        
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(profissionalRepository).findById(idProfissional);
        verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de atualização de portfolio")
    void deveExecutarFluxoCompletoDeAtualizacaoPortfolio() {
        
        Long idPortfolio = 1L;
        Long idProfissionalAntigo = 1L;
        Long idProfissionalNovo = 2L;
        
        Portfolio portfolioExistente = criarPortfolio(idPortfolio);
        Profissional profissionalAntigo = criarProfissional(idProfissionalAntigo);
        Profissional profissionalNovo = criarProfissional(idProfissionalNovo);
        
        portfolioExistente.setProfissional(profissionalAntigo);
        profissionalAntigo.setPortfolio(portfolioExistente);
        
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setDescricao("Nova Descrição com pelo menos 20 caracteres");
        dto.setIdProfissional(idProfissionalNovo);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolioExistente));
        when(profissionalRepository.save(profissionalAntigo))
            .thenReturn(profissionalAntigo);
        when(profissionalRepository.findById(idProfissionalNovo))
            .thenReturn(Optional.of(profissionalNovo));
        when(profissionalRepository.save(profissionalNovo))
            .thenReturn(profissionalNovo);
        when(portfolioRepository.save(portfolioExistente))
            .thenReturn(portfolioExistente);

        
        PortfolioDTO portfolioAtualizado = portfolioService.atualizarComValidacao(idPortfolio, dto);

        
        assertNotNull(portfolioAtualizado);
        assertEquals(idPortfolio, portfolioAtualizado.getIdPortfolio());
        assertEquals("Nova Descrição com pelo menos 20 caracteres", portfolioAtualizado.getDescricao());
        assertEquals(idProfissionalNovo, portfolioAtualizado.getIdProfissional());
        
        verify(portfolioRepository).findById(idPortfolio);
        verify(profissionalRepository).save(profissionalAntigo);
        verify(profissionalRepository).findById(idProfissionalNovo);
        verify(profissionalRepository).save(profissionalNovo);
        verify(portfolioRepository).save(portfolioExistente);
    }

    @Test
    @DisplayName("Deve executar fluxo completo de remoção de portfolio")
    void deveExecutarFluxoCompletoDeRemocaoPortfolio() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 1L;
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio);
        portfolio.setProfissional(profissional);
        profissional.setPortfolio(portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio))
            .thenReturn(Optional.empty());
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);
        doNothing().when(portfolioRepository).delete(portfolio);

        
        portfolioService.deletarComValidacao(idPortfolio);

        
        verify(portfolioRepository).findById(idPortfolio);
        verify(profissionalRepository).save(profissional);
        verify(portfolioRepository).delete(portfolio);
        assertNull(profissional.getPortfolio());
        
        
        assertThrows(PortfolioNaoEncontradoException.class,
            () -> portfolioService.buscarPorIdComValidacao(idPortfolio));
    }

    @Test
    @DisplayName("Deve validar integridade referencial entre portfolio e profissional")
    void deveValidarIntegridadeReferencialEntrePortfolioEProfissional() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 1L;
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio);
        portfolio.setProfissional(profissional);
        profissional.setPortfolio(portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));

        
        PortfolioDTO portfolioEncontrado = portfolioService.buscarPorIdComValidacao(idPortfolio);

        
        assertNotNull(portfolioEncontrado);
        assertEquals(idPortfolio, portfolioEncontrado.getIdPortfolio());
        assertEquals(idProfissional, portfolioEncontrado.getIdProfissional());
        
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve processar múltiplos portfolios com paginação")
    void deveProcessarMultiplosPortfoliosComPaginacao() {
        
        Pageable pageable = PageRequest.of(0, 2);
        List<Portfolio> portfolios = Arrays.asList(
            criarPortfolio(1L),
            criarPortfolio(2L)
        );
        Page<Portfolio> pagePortfolios = new PageImpl<>(portfolios, pageable, 5);
        
        when(portfolioRepository.findAll(pageable))
            .thenReturn(pagePortfolios);

        
        List<PortfolioDTO> resultado = portfolioService.listarComAutorizacao(pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getIdPortfolio());
        assertEquals(2L, resultado.get(1).getIdPortfolio());
        
        verify(authorizationService).requireAdmin();
        verify(portfolioRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve manter consistência em operações sequenciais")
    void deveManterConsistenciaEmOperacoesSequenciais() {
        
        Long idPortfolio = 1L;
        PortfolioDTO dtoOriginal = criarPortfolioDTO();
        dtoOriginal.setDescricao("Descrição original com pelo menos 20 caracteres");
        
        Portfolio portfolioSalvo = criarPortfolio(idPortfolio);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolioSalvo));
        
        
        PortfolioDTO dtoAtualizado = criarPortfolioDTO();
        dtoAtualizado.setDescricao("Descrição atualizada com pelo menos 20 caracteres");
        
        when(portfolioRepository.save(portfolioSalvo))
            .thenReturn(portfolioSalvo);

        
        Portfolio portfolioCriado = portfolioService.criar(dtoOriginal);
        PortfolioDTO portfolioBuscado = portfolioService.buscarPorIdComValidacao(idPortfolio);
        PortfolioDTO portfolioAtualizado = portfolioService.atualizarComValidacao(idPortfolio, dtoAtualizado);

        
        assertNotNull(portfolioCriado);
        assertNotNull(portfolioBuscado);
        assertNotNull(portfolioAtualizado);
        
        assertEquals(idPortfolio, portfolioCriado.getIdPortfolio());
        assertEquals(idPortfolio, portfolioBuscado.getIdPortfolio());
        assertEquals(idPortfolio, portfolioAtualizado.getIdPortfolio());
        
        verify(portfolioRepository, times(2)).save(any(Portfolio.class));
        verify(portfolioRepository, times(2)).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve tratar cenário de portfolio sem profissional associado")
    void deveTratarCenarioDePortfolioSemProfissionalAssociado() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        portfolio.setProfissional(null);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        doNothing().when(portfolioRepository).delete(portfolio);

        
        PortfolioDTO portfolioBuscado = portfolioService.buscarPorIdComValidacao(idPortfolio);
        portfolioService.deletarComValidacao(idPortfolio);

        
        assertNotNull(portfolioBuscado);
        assertEquals(idPortfolio, portfolioBuscado.getIdPortfolio());
        assertNull(portfolioBuscado.getIdProfissional());
        
        verify(portfolioRepository, times(2)).findById(idPortfolio);
        verify(portfolioRepository).delete(portfolio);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve validar transações e rollback em caso de erro")
    void deveValidarTransacoesERollbackEmCasoDeErro() {
        
        Long idProfissional = 1L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolioSalvo = criarPortfolio(1L);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(profissional))
            .thenThrow(new RuntimeException("Falha na transação"));

        
        assertThrows(RuntimeException.class,
            () -> portfolioService.criar(dto));
        
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(profissionalRepository).findById(idProfissional);
        verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve processar atualizações de diferentes campos")
    void deveProcessarAtualizacoesDeDiferentesCampos() {
        
        Long idPortfolio = 1L;
        Portfolio portfolioExistente = criarPortfolio(idPortfolio);
        
        PortfolioDTO dto = new PortfolioDTO();
        dto.setDescricao("Nova descrição com pelo menos 20 caracteres");
        dto.setExperiencia("Nova experiência");
        dto.setEspecialidade("Nova especialidade");
        dto.setWebsite("https://novosite.com");
        dto.setInstagram("@novousuario");
        dto.setTiktok("@novotiktok");
        dto.setFacebook("Novo Facebook");
        dto.setTwitter("@novotwitter");
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolioExistente));
        when(portfolioRepository.save(portfolioExistente))
            .thenReturn(portfolioExistente);

        
        PortfolioDTO resultado = portfolioService.atualizarComValidacao(idPortfolio, dto);

        
        assertNotNull(resultado);
        assertEquals("Nova descrição com pelo menos 20 caracteres", resultado.getDescricao());
        assertEquals("Nova experiência", resultado.getExperiencia());
        assertEquals("Nova especialidade", resultado.getEspecialidade());
        assertEquals("https://novosite.com", resultado.getWebsite());
        assertEquals("@novousuario", resultado.getInstagram());
        assertEquals("@novotiktok", resultado.getTiktok());
        assertEquals("Novo Facebook", resultado.getFacebook());
        assertEquals("@novotwitter", resultado.getTwitter());
        
        verify(portfolioRepository).findById(idPortfolio);
        verify(portfolioRepository).save(portfolioExistente);
    }

    
    private Portfolio criarPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        portfolio.setDescricao("Descrição detalhada do portfolio com trabalhos em tatuagem realista e aquarela");
        portfolio.setExperiencia("Experiência de 5 anos em tatuagens realistas e aquarela");
        portfolio.setEspecialidade("Especialidade em tatuagem realista e aquarela");
        portfolio.setWebsite("https://example.com");
        portfolio.setInstagram("@usuario_teste");
        portfolio.setTiktok("@usuario_teste");
        portfolio.setFacebook("Usuario Teste");
        portfolio.setTwitter("@usuario_teste");
        return portfolio;
    }

    private PortfolioDTO criarPortfolioDTO() {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setDescricao("Descrição detalhada do portfolio com trabalhos em tatuagem realista e aquarela");
        dto.setExperiencia("Experiência de 5 anos em tatuagens realistas e aquarela");
        dto.setEspecialidade("Especialidade em tatuagem realista e aquarela");
        dto.setWebsite("https://example.com");
        dto.setInstagram("@usuario_teste");
        dto.setTiktok("@usuario_teste");
        dto.setFacebook("Usuario Teste");
        dto.setTwitter("@usuario_teste");
        return dto;
    }

    private Profissional criarProfissional(Long id) {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(id);
        return profissional;
    }
} 