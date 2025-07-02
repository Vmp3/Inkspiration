package inkspiration.backend.service.portfolioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.PortfolioService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceMockTest {

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
    @DisplayName("Deve chamar repositório correto ao listar portfolios")
    void deveChamarRepositorioCorretoAoListarPortfolios() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(portfolioRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        portfolioService.listarTodos(pageable);

        
        verify(portfolioRepository, times(1)).findAll(pageable);
        verifyNoMoreInteractions(portfolioRepository);
    }

    @Test
    @DisplayName("Deve capturar parâmetros corretos ao buscar por ID")
    void deveCapturaParametrosCorretosAoBuscarPorId() {
        
        Long idPortfolio = 123L;
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(criarPortfolio(idPortfolio)));
        
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        
        portfolioService.buscarPorId(idPortfolio);

        
        verify(portfolioRepository).findById(idCaptor.capture());
        assertEquals(idPortfolio, idCaptor.getValue());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao criar portfolio com profissional")
    void deveVerificarOrdemChamadasAoCriarPortfolioComProfissional() {
        
        Long idProfissional = 1L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        Portfolio portfolioSalvo = criarPortfolio(1L);
        Profissional profissional = criarProfissional(idProfissional);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolioSalvo);
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        portfolioService.criar(dto);

        
        InOrder inOrder = inOrder(portfolioRepository, profissionalRepository);
        inOrder.verify(portfolioRepository).save(any(Portfolio.class));
        inOrder.verify(profissionalRepository).findById(idProfissional);
        inOrder.verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve capturar entidade correta ao salvar portfolio")
    void deveCapturaEntidadeCorretaAoSalvarPortfolio() {
        
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setDescricao("Descrição específica para captura");
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(criarPortfolio(1L));
        
        ArgumentCaptor<Portfolio> portfolioCaptor = ArgumentCaptor.forClass(Portfolio.class);

        
        portfolioService.criar(dto);

        
        verify(portfolioRepository).save(portfolioCaptor.capture());
        
        Portfolio portfolioCapturado = portfolioCaptor.getValue();
        assertEquals("Descrição específica para captura", portfolioCapturado.getDescricao());
        assertEquals(dto.getExperiencia(), portfolioCapturado.getExperiencia());
        assertEquals(dto.getEspecialidade(), portfolioCapturado.getEspecialidade());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao deletar portfolio com profissional")
    void deveVerificarOrdemChamadasAoDeletarPortfolioComProfissional() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 1L;
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio);
        portfolio.setProfissional(profissional);
        profissional.setPortfolio(portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        portfolioService.deletar(idPortfolio);

        
        InOrder inOrder = inOrder(portfolioRepository, profissionalRepository);
        inOrder.verify(portfolioRepository).findById(idPortfolio);
        inOrder.verify(profissionalRepository).save(profissional);
        inOrder.verify(portfolioRepository).delete(portfolio);
    }

    @Test
    @DisplayName("Deve capturar profissional correto ao desassociar")
    void deveCapturaProfiissionalCorretoAoDesassociar() {
        
        Long idPortfolio = 1L;
        Long idProfissional = 1L;
        
        Profissional profissional = criarProfissional(idProfissional);
        Portfolio portfolio = criarPortfolio(idPortfolio);
        portfolio.setProfissional(profissional);
        profissional.setPortfolio(portfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        
        ArgumentCaptor<Profissional> profissionalCaptor = ArgumentCaptor.forClass(Profissional.class);

        
        portfolioService.deletar(idPortfolio);

        
        verify(profissionalRepository).save(profissionalCaptor.capture());
        
        Profissional profissionalCapturado = profissionalCaptor.getValue();
        assertEquals(idProfissional, profissionalCapturado.getIdProfissional());
        assertNull(profissionalCapturado.getPortfolio());
    }

    @Test
    @DisplayName("Deve configurar mock para múltiplas chamadas")
    void deveConfigurarMockParaMultiplasChamadas() {
        
        Long idPortfolio = 1L;
        Portfolio primeiroPortfolio = criarPortfolio(idPortfolio);
        primeiroPortfolio.setDescricao("Primeira busca");
        
        Portfolio segundoPortfolio = criarPortfolio(idPortfolio);
        segundoPortfolio.setDescricao("Segunda busca");
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(primeiroPortfolio))
            .thenReturn(Optional.of(segundoPortfolio));

        
        Portfolio primeiroResultado = portfolioService.buscarPorId(idPortfolio);
        Portfolio segundoResultado = portfolioService.buscarPorId(idPortfolio);

        
        assertEquals("Primeira busca", primeiroResultado.getDescricao());
        assertEquals("Segunda busca", segundoResultado.getDescricao());
        verify(portfolioRepository, times(2)).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve resetar mock entre operações")
    void deveResetarMockEntreOperacoes() {
        
        Long idPortfolio = 1L;
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(criarPortfolio(idPortfolio)));

        
        portfolioService.buscarPorId(idPortfolio);
        
        
        reset(portfolioRepository);
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(criarPortfolio(idPortfolio)));
        
        
        portfolioService.buscarPorId(idPortfolio);

        
        verify(portfolioRepository, times(1)).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve verificar que não há interações desnecessárias")
    void deveVerificarQueNaoHaInteracoesDesnecessarias() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(portfolioRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        portfolioService.listarTodos(pageable);

        
        verify(portfolioRepository, only()).findAll(pageable);
        verifyNoInteractions(profissionalRepository);
        verifyNoInteractions(authorizationService);
    }

    @Test
    @DisplayName("Deve usar argumentMatchers corretamente")
    void deveUsarArgumentMatchersCorretamente() {
        
        when(portfolioRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarPortfolio(1L)));
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(criarPortfolio(1L));
        
        PortfolioDTO dto = criarPortfolioDTO();

        
        portfolioService.buscarPorId(1L);
        portfolioService.criar(dto);

        
        verify(portfolioRepository).findById(anyLong());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Deve configurar stub para retornar exception")
    void deveConfigurarStubParaRetornarException() {
        
        Long idPortfolio = 1L;
        when(portfolioRepository.findById(idPortfolio))
            .thenThrow(new RuntimeException("Erro simulado"));

        
        assertThrows(RuntimeException.class, 
            () -> portfolioService.buscarPorId(idPortfolio));
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve verificar invocação de métodos com timeout")
    void deveVerificarInvocacaoMetodosComTimeout() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(portfolioRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        portfolioService.listarTodos(pageable);

        
        verify(portfolioRepository, timeout(1000)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve capturar múltiplos argumentos em sequência")
    void deveCapturaMultiplosArgumentosEmSequencia() {
        
        when(portfolioRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarPortfolio(1L)));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        
        portfolioService.buscarPorId(1L);
        portfolioService.buscarPorId(2L);
        portfolioService.buscarPorId(3L);

        
        verify(portfolioRepository, times(3)).findById(captor.capture());
        
        List<Long> idsCapturados = captor.getAllValues();
        assertEquals(Arrays.asList(1L, 2L, 3L), idsCapturados);
    }

    @Test
    @DisplayName("Deve verificar autorização antes de listar portfolios")
    void deveVerificarAutorizacaoAntesDeListarPortfolios() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(portfolioRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        portfolioService.listarComAutorizacao(pageable);

        
        InOrder inOrder = inOrder(authorizationService, portfolioRepository);
        inOrder.verify(authorizationService).requireAdmin();
        inOrder.verify(portfolioRepository).findAll(pageable);
    }

    
    private Portfolio criarPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        portfolio.setDescricao("Descrição de portfolio para teste com pelo menos 20 caracteres");
        portfolio.setExperiencia("Experiência de teste");
        portfolio.setEspecialidade("Especialidade de teste");
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