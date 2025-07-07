package inkspiration.backend.service.portfolioService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.PortfolioService;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceDeleteByUserIdTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    @DisplayName("Deve buscar portfolio por ID do usuário com sucesso")
    void deveBuscarPortfolioPorIdUsuarioComSucesso() {
        // Arrange
        Long idUsuario = 1L;
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome("João");
        
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio do João");
        portfolio.setProfissional(profissional);
        
        when(portfolioRepository.findByUsuarioId(idUsuario)).thenReturn(Optional.of(portfolio));
        
        // Act
        Portfolio resultado = portfolioService.buscarPorUsuarioId(idUsuario);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(portfolio.getIdPortfolio(), resultado.getIdPortfolio());
        assertEquals(portfolio.getDescricao(), resultado.getDescricao());
        verify(portfolioRepository).findByUsuarioId(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando portfolio não é encontrado por ID do usuário")
    void deveLancarExcecaoQuandoPortfolioNaoEncontradoPorIdUsuario() {
        // Arrange
        Long idUsuario = 999L;
        when(portfolioRepository.findByUsuarioId(idUsuario)).thenReturn(Optional.empty());
        
        // Act & Assert
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.buscarPorUsuarioId(idUsuario)
        );
        
        assertEquals("Portfolio não encontrado para o usuário com ID: " + idUsuario, exception.getMessage());
        verify(portfolioRepository).findByUsuarioId(idUsuario);
    }

    @Test
    @DisplayName("Deve deletar portfolio por ID do usuário com sucesso")
    void deveDeletarPortfolioPorIdUsuarioComSucesso() {
        // Arrange
        Long idUsuario = 1L;
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome("João");
        
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio do João");
        portfolio.setProfissional(profissional);
        
        when(portfolioRepository.findByUsuarioId(idUsuario)).thenReturn(Optional.of(portfolio));
        
        // Act
        portfolioService.deletarPorUsuarioId(idUsuario);
        
        // Assert
        verify(portfolioRepository).findByUsuarioId(idUsuario);
        verify(profissionalRepository).save(profissional);
        verify(portfolioRepository).delete(portfolio);
        
        // Verifica se as associações foram desfeitas
        assertNull(profissional.getPortfolio());
        assertNull(portfolio.getProfissional());
    }

    @Test
    @DisplayName("Deve deletar portfolio com validação por ID do usuário")
    void deveDeletarPortfolioComValidacaoPorIdUsuario() {
        // Arrange
        Long idUsuario = 1L;
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome("João");
        
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio do João");
        portfolio.setProfissional(profissional);
        
        when(portfolioRepository.findByUsuarioId(idUsuario)).thenReturn(Optional.of(portfolio));
        
        // Act
        assertDoesNotThrow(() -> portfolioService.deletarPorUsuarioIdComValidacao(idUsuario));
        
        // Assert
        verify(portfolioRepository).findByUsuarioId(idUsuario);
        verify(profissionalRepository).save(profissional);
        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    @DisplayName("Deve propagar exceção quando portfolio não encontrado na validação")
    void devePropagrarExcecaoQuandoPortfolioNaoEncontradoNaValidacao() {
        // Arrange
        Long idUsuario = 999L;
        when(portfolioRepository.findByUsuarioId(idUsuario)).thenReturn(Optional.empty());
        
        // Act & Assert
        PortfolioNaoEncontradoException exception = assertThrows(
            PortfolioNaoEncontradoException.class,
            () -> portfolioService.deletarPorUsuarioIdComValidacao(idUsuario)
        );
        
        assertEquals("Portfolio não encontrado para o usuário com ID: " + idUsuario, exception.getMessage());
        verify(portfolioRepository).findByUsuarioId(idUsuario);
        verify(portfolioRepository, never()).delete(any());
        verify(profissionalRepository, never()).save(any());
    }
} 