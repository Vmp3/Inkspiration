package inkspiration.backend.entities.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Portfolio;

@DisplayName("Testes de validação de website - Portfolio")
public class PortfolioWebsiteTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
    }

    @Test
    @DisplayName("Deve aceitar website válido com http")
    void deveAceitarWebsiteValidoComHttp() {
        String website = "http://example.com";
        portfolio.setWebsite(website);
        assertEquals(website, portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website válido com https")
    void deveAceitarWebsiteValidoComHttps() {
        String website = "https://example.com";
        portfolio.setWebsite(website);
        assertEquals(website, portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website com subdomínio")
    void deveAceitarWebsiteComSubdominio() {
        String website = "https://portfolio.example.com";
        portfolio.setWebsite(website);
        assertEquals(website, portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website com caminho")
    void deveAceitarWebsiteComCaminho() {
        String website = "https://example.com/portfolio";
        portfolio.setWebsite(website);
        assertEquals(website, portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website nulo")
    void deveAceitarWebsiteNulo() {
        portfolio.setWebsite(null);
        assertNull(portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website vazio")
    void deveAceitarWebsiteVazio() {
        portfolio.setWebsite("");
        assertEquals("", portfolio.getWebsite());
    }

    @Test
    @DisplayName("Deve aceitar website com apenas espaços")
    void deveAceitarWebsiteComApenasEspacos() {
        portfolio.setWebsite("   ");
        assertEquals("   ", portfolio.getWebsite());
    }

    @Test
    @DisplayName("Não deve aceitar website sem protocolo")
    void naoDeveAceitarWebsiteSemProtocolo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setWebsite("example.com");
        });
        assertEquals("O website deve começar com http:// ou https://", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar website com protocolo inválido")
    void naoDeveAceitarWebsiteComProtocoloInvalido() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setWebsite("ftp://example.com");
        });
        assertEquals("O website deve começar com http:// ou https://", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar website muito longo")
    void naoDeveAceitarWebsiteMuitoLongo() {
        String website = "https://" + "a".repeat(300) + ".com";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setWebsite(website);
        });
        assertEquals("O website não pode exceder 255 caracteres", exception.getMessage());
    }
} 