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

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.exception.portfolio.PortfolioAtualizacaoException;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.portfolio.PortfolioRemocaoException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.PortfolioService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceExcecaoTest {

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
    @DisplayName("Deve lançar PortfolioNaoEncontradoException ao buscar portfolio inexistente")
    void deveLancarPortfolioNaoEncontradoExceptionAoBuscarPortfolioInexistente() {
        
        Long idPortfolio = 999L;
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.buscarPorIdComValidacao(idPortfolio)
        );

        
        assertTrue(exception.getMessage().contains("Portfolio não encontrado com ID: " + idPortfolio));
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve lançar ProfissionalNaoEncontradoException ao criar portfolio com profissional inexistente")
    void deveLancarProfissionalNaoEncontradoExceptionAoCriarPortfolioComProfissionalInexistente() {
        
        Long idProfissional = 999L;
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idProfissional);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(criarPortfolio(1L));
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
    @DisplayName("Deve lançar PortfolioAtualizacaoException ao falhar na atualização")
    void deveLancarPortfolioAtualizacaoExceptionAoFalharNaAtualizacao() {
        
        Long idPortfolio = 1L;
        PortfolioDTO dto = criarPortfolioDTO();
        
        when(portfolioRepository.findById(idPortfolio))
            .thenThrow(new RuntimeException("Erro de banco de dados"));

        
        PortfolioAtualizacaoException exception = assertThrows(
            PortfolioAtualizacaoException.class,
            () -> portfolioService.atualizarComValidacao(idPortfolio, dto)
        );

        
        assertNotNull(exception.getMessage());
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve lançar PortfolioRemocaoException ao falhar na remoção")
    void deveLancarPortfolioRemocaoExceptionAoFalharNaRemocao() {
        
        Long idPortfolio = 1L;
        
        when(portfolioRepository.findById(idPortfolio))
            .thenThrow(new RuntimeException("Erro ao acessar banco"));

        
        PortfolioRemocaoException exception = assertThrows(
            PortfolioRemocaoException.class,
            () -> portfolioService.deletarComValidacao(idPortfolio)
        );

        
        assertNotNull(exception.getMessage());
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve tratar erro de transação ao criar portfolio")
    void deveTratarErroTransacaoAoCriarPortfolio() {
        
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(1L);
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(criarPortfolio(1L));
        when(profissionalRepository.findById(1L))
            .thenReturn(Optional.of(criarProfissional(1L)));
        when(profissionalRepository.save(any(Profissional.class)))
            .thenThrow(new RuntimeException("Falha na transação"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> portfolioService.criar(dto)
        );

        
        assertEquals("Falha na transação", exception.getMessage());
        verify(profissionalRepository).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve tratar erro de constraint violation ao salvar portfolio")
    void deveTratarErroConstraintViolationAoSalvarPortfolio() {
        
        PortfolioDTO dto = criarPortfolioDTO();
        
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenThrow(new RuntimeException("Constraint violation"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> portfolioService.criar(dto)
        );

        
        assertEquals("Constraint violation", exception.getMessage());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Deve tratar erro de timeout ao buscar portfolio")
    void deveTratarErroTimeoutAoBuscarPortfolio() {
        
        Long idPortfolio = 1L;
        when(portfolioRepository.findById(idPortfolio))
            .thenThrow(new RuntimeException("Connection timeout"));

        
        PortfolioAtualizacaoException exception = assertThrows(
            PortfolioAtualizacaoException.class,
            () -> portfolioService.atualizarComValidacao(idPortfolio, criarPortfolioDTO())
        );

        
        assertNotNull(exception.getMessage());
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve manter integridade ao falhar operação com profissional")
    void deveManterIntegridadeAoFalharOperacaoComProfissional() {
        
        Long idPortfolio = 1L;
        Long idProfissionalExistente = 1L;
        Long idNovoProfissional = 2L;
        
        Portfolio portfolio = criarPortfolio(idPortfolio);
        Profissional profissionalExistente = criarProfissional(idProfissionalExistente);
        portfolio.setProfissional(profissionalExistente);
        profissionalExistente.setPortfolio(portfolio);
        
        PortfolioDTO dto = criarPortfolioDTO();
        dto.setIdProfissional(idNovoProfissional);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        when(profissionalRepository.save(profissionalExistente))
            .thenReturn(profissionalExistente);
        when(profissionalRepository.findById(idNovoProfissional))
            .thenThrow(new RuntimeException("Falha ao buscar novo profissional"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> portfolioService.atualizar(idPortfolio, dto)
        );

        
        assertEquals("Falha ao buscar novo profissional", exception.getMessage());
        verify(profissionalRepository).save(profissionalExistente);
        verify(profissionalRepository).findById(idNovoProfissional);
    }

    @Test
    @DisplayName("Deve capturar exceção de acesso negado")
    void deveCapturaExcecaoAcessoNegado() {
        
        Pageable pageable = PageRequest.of(0, 10);
        doThrow(new RuntimeException("Acesso negado"))
            .when(authorizationService).requireAdmin();

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> portfolioService.listarComAutorizacao(pageable)
        );

        
        assertEquals("Acesso negado", exception.getMessage());
        verify(authorizationService).requireAdmin();
        verify(portfolioRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve tratar erro de deadlock no banco")
    void deveTratarErroDeadlockNoBanco() {
        
        Long idPortfolio = 1L;
        Portfolio portfolio = criarPortfolio(idPortfolio);
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.of(portfolio));
        doThrow(new RuntimeException("Deadlock detected"))
            .when(portfolioRepository).delete(portfolio);

        
        PortfolioRemocaoException exception = assertThrows(
            PortfolioRemocaoException.class,
            () -> portfolioService.deletarComValidacao(idPortfolio)
        );

        
        assertNotNull(exception.getMessage());
        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    @DisplayName("Deve relançar PortfolioNaoEncontradoException na atualização")
    void deveRelancarPortfolioNaoEncontradoExceptionNaAtualizacao() {
        
        Long idPortfolio = 999L;
        PortfolioDTO dto = criarPortfolioDTO();
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.atualizarComValidacao(idPortfolio, dto)
        );

        
        assertTrue(exception.getMessage().contains("Portfolio não encontrado com ID: " + idPortfolio));
        verify(portfolioRepository).findById(idPortfolio);
    }

    @Test
    @DisplayName("Deve relançar PortfolioNaoEncontradoException na remoção")
    void deveRelancarPortfolioNaoEncontradoExceptionNaRemocao() {
        
        Long idPortfolio = 999L;
        
        when(portfolioRepository.findById(idPortfolio))
            .thenReturn(Optional.empty());

        
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.deletarComValidacao(idPortfolio)
        );

        
        assertTrue(exception.getMessage().contains("Portfolio não encontrado com ID: " + idPortfolio));
        verify(portfolioRepository).findById(idPortfolio);
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