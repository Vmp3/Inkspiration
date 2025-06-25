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
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.PortfolioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceCoreTest {

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
    @DisplayName("Deve listar todos os portfólios com paginação")
    void deveListarTodosPortfoliosComPaginacao() {
        
        Pageable pageable = PageRequest.of(0, 10);
        List<Portfolio> portfolios = Arrays.asList(
            criarPortfolio(1L, "Portfolio 1"),
            criarPortfolio(2L, "Portfolio 2"),
            criarPortfolio(3L, "Portfolio 3")
        );
        Page<Portfolio> pagePortfolios = new PageImpl<>(portfolios, pageable, portfolios.size());
        
        when(portfolioRepository.findAll(pageable))
            .thenReturn(pagePortfolios);

        
        Page<Portfolio> resultado = portfolioService.listarTodos(pageable);

        
        assertNotNull(resultado);
        assertEquals(3, resultado.getContent().size());
        assertEquals(3, resultado.getTotalElements());
        verify(portfolioRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve criar portfólio sem profissional associado")
    void deveCriarPortfolioSemProfissionalAssociado() {
        
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(null);
        
        Portfolio portfolioSalvo = criarPortfolio(1L, dto.getDescricao());
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);

        
        Portfolio resultado = portfolioService.criar(dto);

        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPortfolio());
        assertEquals(dto.getDescricao(), resultado.getDescricao());
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(profissionalRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve criar portfólio com profissional associado")
    void deveCriarPortfolioComProfissionalAssociado() {
        
        Long idProfissional = 1L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolioSalvo = criarPortfolio(1L, dto.getDescricao());
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        Portfolio resultado = portfolioService.criar(dto);

        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPortfolio());
        assertEquals(dto.getDescricao(), resultado.getDescricao());
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(profissionalRepository).findById(idProfissional);
        verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar portfólio com profissional inexistente")
    void deveLancarExcecaoAoCriarPortfolioComProfissionalInexistente() {
        
        Long idProfissional = 999L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(criarPortfolio(1L, dto.getDescricao()));
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.empty());

        
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> portfolioService.criar(dto)
        );

        
        assertTrue(exception.getMessage().contains("Profissional não encontrado com ID: " + idProfissional));
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve buscar portfólio por ID com sucesso")
    void deveBuscarPortfolioPorIdComSucesso() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio, "Meu Portfolio");
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));

        
        Portfolio resultado = portfolioService.buscarPorId(idPortfolio);

        
        assertNotNull(resultado);
        assertEquals(idPortfolio, resultado.getIdPortfolio());
        assertEquals("Meu Portfolio", resultado.getDescricao());
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar portfólio inexistente")
    void deveLancarExcecaoAoBuscarPortfolioInexistente() {
        
        Long idPortfolio = 999L;
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.buscarPorId(idPortfolio)
        );

        
        assertTrue(exception.getMessage().contains("Portfolio não encontrado com ID: " + idPortfolio));
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve atualizar portfólio sem alterar profissional")
    void deveAtualizarPortfolioSemAlterarProfissional() {
        
        Long idPortfolio = 1L;
        Portfolio portfolioExistente = criarPortfolio(idPortfolio, "Descricao Antiga");
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setDescricao("Nova Descricao");
        dto.setIdProfissional(null);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolioExistente));
        when(portfolioRepository.save(portfolioExistente))
            .thenReturn(portfolioExistente);

        
        Portfolio resultado = portfolioService.atualizar(idPortfolio, dto);

        
        assertNotNull(resultado);
        assertEquals("Nova Descricao", resultado.getDescricao());
        verify(portfolioRepository).findById(idPortfolio);
        verify(portfolioRepository).save(portfolioExistente);
    }

    @Test
    @DisplayName("Deve deletar portfólio sem profissional associado")
    void deveDeletarPortfolioSemProfissionalAssociado() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio, "Portfolio para deletar");
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        doNothing().when(portfolioRepository).delete(portfolio);

        
        portfolioService.deletar(idPortfolio);

        
        verify(portfolioRepository).findById(idPortfolio);
        verify(portfolioRepository).delete(portfolio);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar portfólio com profissional associado")
    void deveDeletarPortfolioComProfissionalAssociado() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio, "Portfolio para deletar");
        portfolio.setProfissional(profissional);
        profissional.setPortfolio(portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);
        doNothing().when(portfolioRepository).delete(portfolio);

        
        portfolioService.deletar(idPortfolio);

        
        verify(portfolioRepository).findById(idPortfolio);
        verify(profissionalRepository).save(profissional);
        verify(portfolioRepository).delete(portfolio);
        assertNull(profissional.getPortfolio());
    }

    @Test
    @DisplayName("Deve converter entidade para DTO corretamente")
    void deveConverterEntidadeParaDTOCorretamente() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 2L;
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio, "Descrição do Portfolio");
        portfolio.setProfissional(profissional);

        
        PortfolioDTO resultado = portfolioService.converterParaDto(portfolio);

        
        assertNotNull(resultado);
        assertEquals(idPortfolio, resultado.getIdPortfolio());
        assertEquals(idProfissional, resultado.getIdProfissional());
        assertEquals("Descrição do Portfolio", resultado.getDescricao());
    }

    @Test
    @DisplayName("Deve retornar null ao converter portfolio null")
    void deveRetornarNullAoConverterPortfolioNull() {
        
        PortfolioDTO resultado = portfolioService.converterParaDto(null);

        
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve converter portfolio sem profissional para DTO")
    void deveConverterPortfolioSemProfissionalParaDTO() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio, "Portfolio sem profissional");
        portfolio.setProfissional(null);

        
        PortfolioDTO resultado = portfolioService.converterParaDto(portfolio);

        
        assertNotNull(resultado);
        assertEquals(idPortfolio, resultado.getIdPortfolio());
        assertNull(resultado.getIdProfissional());
        assertEquals("Portfolio sem profissional", resultado.getDescricao());
    }

    @Test
    @DisplayName("Deve listar portfólios com autorização de admin")
    void deveListarPortfoliosComAutorizacaoAdmin() {
        
        Pageable pageable = PageRequest.of(0, 5);
        List<Portfolio> portfolios = Arrays.asList(
            criarPortfolio(1L, "Portfolio 1"),
            criarPortfolio(2L, "Portfolio 2")
        );
        Page<Portfolio> pagePortfolios = new PageImpl<>(portfolios, pageable, portfolios.size());
        
        when(portfolioRepository.findAll(pageable))
            .thenReturn(pagePortfolios);

        
        List<PortfolioDTO> resultado = portfolioService.listarComAutorizacao(pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(authorizationService).requireAdmin();
        verify(portfolioRepository).findAll(pageable);
    }

    
    private Portfolio criarPortfolio(Long id, String descricao) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        portfolio.setDescricao(descricao);
        portfolio.setExperiencia("Experiência teste");
        portfolio.setEspecialidade("Especialidade teste");
        portfolio.setWebsite("https://example.com");
        portfolio.setInstagram("@usuario_teste");
        portfolio.setTiktok("@usuario_teste");
        portfolio.setFacebook("Usuario Teste");
        portfolio.setTwitter("@usuario_teste");
        return portfolio;
    }

    private PortfolioDTO criarPortfolioDTO() {
        PortfolioDTO dto = new PortfolioDTO();
        dto.setDescricao("Descrição de portfolio para teste com pelo menos 20 caracteres");
        dto.setExperiencia("Experiência de teste");
        dto.setEspecialidade("Especialidade de teste");
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