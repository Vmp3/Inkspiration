package inkspiration.backend.entities.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;

@DisplayName("Testes gerais da entidade Portfolio")
public class PortfolioEntityTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
    }

    @Test
    @DisplayName("Deve criar portfolio com construtor padrão")
    void deveCriarPortfolioComConstrutorPadrao() {
        assertNotNull(portfolio);
        assertNull(portfolio.getIdPortfolio());
        assertNull(portfolio.getDescricao());
        assertNull(portfolio.getExperiencia());
        assertNull(portfolio.getEspecialidade());
        assertNull(portfolio.getWebsite());
        assertNull(portfolio.getTiktok());
        assertNull(portfolio.getInstagram());
        assertNull(portfolio.getFacebook());
        assertNull(portfolio.getTwitter());
        assertNull(portfolio.getProfissional());
        assertNotNull(portfolio.getImagens());
        assertTrue(portfolio.getImagens().isEmpty());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        Long id = 123L;
        portfolio.setIdPortfolio(id);
        assertEquals(id, portfolio.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        portfolio.setIdPortfolio(null);
        assertNull(portfolio.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve definir e obter profissional")
    void deveDefinirEObterProfissional() {
        Profissional profissional = new Profissional();
        portfolio.setProfissional(profissional);
        assertEquals(profissional, portfolio.getProfissional());
    }

    @Test
    @DisplayName("Deve aceitar profissional nulo")
    void deveAceitarProfissionalNulo() {
        portfolio.setProfissional(null);
        assertNull(portfolio.getProfissional());
    }

    @Test
    @DisplayName("Deve definir e obter lista de imagens")
    void deveDefinirEObterListaImagens() {
        List<Imagem> imagens = new ArrayList<>();
        Imagem imagem1 = new Imagem();
        Imagem imagem2 = new Imagem();
        imagens.add(imagem1);
        imagens.add(imagem2);
        
        portfolio.setImagens(imagens);
        assertEquals(imagens, portfolio.getImagens());
        assertEquals(2, portfolio.getImagens().size());
    }

    @Test
    @DisplayName("Deve adicionar imagem")
    void deveAdicionarImagem() {
        Imagem imagem = new Imagem();
        portfolio.adicionarImagem(imagem);
        
        assertEquals(1, portfolio.getImagens().size());
        assertTrue(portfolio.getImagens().contains(imagem));
        assertEquals(portfolio, imagem.getPortfolio());
    }

    @Test
    @DisplayName("Deve remover imagem")
    void deveRemoverImagem() {
        Imagem imagem = new Imagem();
        portfolio.adicionarImagem(imagem);
        assertEquals(1, portfolio.getImagens().size());
        
        portfolio.removerImagem(imagem);
        assertEquals(0, portfolio.getImagens().size());
        assertFalse(portfolio.getImagens().contains(imagem));
        assertNull(imagem.getPortfolio());
    }

    @Test
    @DisplayName("Deve adicionar múltiplas imagens")
    void deveAdicionarMultiplasImagens() {
        Imagem imagem1 = new Imagem();
        Imagem imagem2 = new Imagem();
        Imagem imagem3 = new Imagem();
        
        portfolio.adicionarImagem(imagem1);
        portfolio.adicionarImagem(imagem2);
        portfolio.adicionarImagem(imagem3);
        
        assertEquals(3, portfolio.getImagens().size());
        assertTrue(portfolio.getImagens().contains(imagem1));
        assertTrue(portfolio.getImagens().contains(imagem2));
        assertTrue(portfolio.getImagens().contains(imagem3));
    }

    @Test
    @DisplayName("Deve remover imagem específica de múltiplas")
    void deveRemoverImagemEspecificaDeMultiplas() {
        Imagem imagem1 = new Imagem();
        Imagem imagem2 = new Imagem();
        Imagem imagem3 = new Imagem();
        
        portfolio.adicionarImagem(imagem1);
        portfolio.adicionarImagem(imagem2);
        portfolio.adicionarImagem(imagem3);
        
        portfolio.removerImagem(imagem2);
        
        assertEquals(2, portfolio.getImagens().size());
        assertTrue(portfolio.getImagens().contains(imagem1));
        assertFalse(portfolio.getImagens().contains(imagem2));
        assertTrue(portfolio.getImagens().contains(imagem3));
    }
} 