package inkspiration.backend.entities.imagem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portfolio;

@DisplayName("Testes gerais da entidade Imagem")
public class ImagemEntityTest {

    @Test
    @DisplayName("Deve criar imagem com construtor padrão")
    void deveCriarImagemComConstrutorPadrao() {
        Imagem imagem = new Imagem();
        
        assertNotNull(imagem);
        assertNull(imagem.getIdImagem());
        assertNull(imagem.getImagemBase64());
        assertNull(imagem.getPortfolio());
    }

    @Test
    @DisplayName("Deve criar imagem com construtor completo")
    void deveCriarImagemComConstrutorCompleto() {
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        Portfolio portfolio = new Portfolio();
        
        Imagem imagem = new Imagem(imagemBase64, portfolio);
        
        assertEquals(imagemBase64, imagem.getImagemBase64());
        assertEquals(portfolio, imagem.getPortfolio());
        assertNull(imagem.getIdImagem());
    }

    @Test
    @DisplayName("Deve definir e obter ID da imagem")
    void deveDefinirEObterIdDaImagem() {
        Imagem imagem = new Imagem();
        Long id = 123L;
        
        imagem.setIdImagem(id);
        assertEquals(id, imagem.getIdImagem());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        Imagem imagem = new Imagem();
        
        imagem.setIdImagem(null);
        assertNull(imagem.getIdImagem());
    }

    @Test
    @DisplayName("Deve definir e obter portfolio")
    void deveDefinirEObterPortfolio() {
        Imagem imagem = new Imagem();
        Portfolio portfolio = new Portfolio();
        
        imagem.setPortfolio(portfolio);
        assertEquals(portfolio, imagem.getPortfolio());
    }

    @Test
    @DisplayName("Deve aceitar portfolio nulo")
    void deveAceitarPortfolioNulo() {
        Imagem imagem = new Imagem();
        
        imagem.setPortfolio(null);
        assertNull(imagem.getPortfolio());
    }

    @Test
    @DisplayName("Construtor completo deve aceitar imagem nula")
    void construtorCompletoDeveAceitarImagemNula() {
        Portfolio portfolio = new Portfolio();
        
        Imagem imagem = new Imagem(null, portfolio);
        
        assertNull(imagem.getImagemBase64());
        assertEquals(portfolio, imagem.getPortfolio());
    }

    @Test
    @DisplayName("Construtor completo deve validar formato quando fornecido")
    void construtorCompletoDeveValidarFormatoQuandoFornecido() {
        Portfolio portfolio = new Portfolio();
        
        // Testa se o construtor valida formato inválido quando imagem é fornecida
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Imagem("imagem_invalida", portfolio);
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Construtor completo deve aceitar portfolio nulo")
    void construtorCompletoDeveAceitarPortfolioNulo() {
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        
        Imagem imagem = new Imagem(imagemBase64, null);
        
        assertEquals(imagemBase64, imagem.getImagemBase64());
        assertNull(imagem.getPortfolio());
    }

    @Test
    @DisplayName("Deve aceitar imagem vazia no construtor e converter para null")
    void deveAceitarImagemVaziaNoConstrutorEConverterParaNull() {
        Portfolio portfolio = new Portfolio();
        
        Imagem imagem = new Imagem("", portfolio);
        
        assertNull(imagem.getImagemBase64());
        assertEquals(portfolio, imagem.getPortfolio());
    }
} 