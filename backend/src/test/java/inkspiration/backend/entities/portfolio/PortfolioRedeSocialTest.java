package inkspiration.backend.entities.portfolio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Portfolio;

@DisplayName("Testes de validação de redes sociais - Portfolio")
public class PortfolioRedeSocialTest {

    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
    }

    // Testes TikTok
    @Test
    @DisplayName("Deve aceitar TikTok válido")
    void deveAceitarTikTokValido() {
        String tiktok = "@usuario123";
        portfolio.setTiktok(tiktok);
        assertEquals(tiktok, portfolio.getTiktok());
    }

    @Test
    @DisplayName("Deve aceitar TikTok sem @")
    void deveAceitarTikTokSemArroba() {
        String tiktok = "usuario123";
        portfolio.setTiktok(tiktok);
        assertEquals(tiktok, portfolio.getTiktok());
    }

    @Test
    @DisplayName("Deve aceitar TikTok com pontos e underscores")
    void deveAceitarTikTokComPontosEUnderscores() {
        String tiktok = "usuario.123_test";
        portfolio.setTiktok(tiktok);
        assertEquals(tiktok, portfolio.getTiktok());
    }

    @Test
    @DisplayName("Deve aceitar TikTok nulo")
    void deveAceitarTikTokNulo() {
        portfolio.setTiktok(null);
        assertNull(portfolio.getTiktok());
    }

    @Test
    @DisplayName("Deve aceitar TikTok vazio")
    void deveAceitarTikTokVazio() {
        portfolio.setTiktok("");
        assertEquals("", portfolio.getTiktok());
    }

    @Test
    @DisplayName("Não deve aceitar TikTok com caracteres especiais")
    void naoDeveAceitarTikTokComCaracteresEspeciais() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setTiktok("usuario@#$");
        });
        assertEquals("TikTok deve conter apenas letras, números, pontos e underscores", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar TikTok muito longo")
    void naoDeveAceitarTikTokMuitoLongo() {
        String tiktok = "a".repeat(51);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setTiktok(tiktok);
        });
        assertEquals("O TikTok não pode exceder 50 caracteres", exception.getMessage());
    }

    // Testes Instagram
    @Test
    @DisplayName("Deve aceitar Instagram válido")
    void deveAceitarInstagramValido() {
        String instagram = "@usuario123";
        portfolio.setInstagram(instagram);
        assertEquals(instagram, portfolio.getInstagram());
    }

    @Test
    @DisplayName("Deve aceitar Instagram sem @")
    void deveAceitarInstagramSemArroba() {
        String instagram = "usuario123";
        portfolio.setInstagram(instagram);
        assertEquals(instagram, portfolio.getInstagram());
    }

    @Test
    @DisplayName("Não deve aceitar Instagram com caracteres especiais")
    void naoDeveAceitarInstagramComCaracteresEspeciais() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setInstagram("usuario@#$");
        });
        assertEquals("Instagram deve conter apenas letras, números, pontos e underscores", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar Instagram muito longo")
    void naoDeveAceitarInstagramMuitoLongo() {
        String instagram = "a".repeat(51);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setInstagram(instagram);
        });
        assertEquals("O Instagram não pode exceder 50 caracteres", exception.getMessage());
    }

    // Testes Twitter
    @Test
    @DisplayName("Deve aceitar Twitter válido")
    void deveAceitarTwitterValido() {
        String twitter = "@usuario123";
        portfolio.setTwitter(twitter);
        assertEquals(twitter, portfolio.getTwitter());
    }

    @Test
    @DisplayName("Deve aceitar Twitter sem @")
    void deveAceitarTwitterSemArroba() {
        String twitter = "usuario123";
        portfolio.setTwitter(twitter);
        assertEquals(twitter, portfolio.getTwitter());
    }

    @Test
    @DisplayName("Não deve aceitar Twitter com caracteres especiais")
    void naoDeveAceitarTwitterComCaracteresEspeciais() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setTwitter("usuario@#$");
        });
        assertEquals("Twitter deve conter apenas letras, números, pontos e underscores", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar Twitter muito longo")
    void naoDeveAceitarTwitterMuitoLongo() {
        String twitter = "a".repeat(51);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setTwitter(twitter);
        });
        assertEquals("O Twitter não pode exceder 50 caracteres", exception.getMessage());
    }

    // Testes Facebook
    @Test
    @DisplayName("Deve aceitar Facebook válido")
    void deveAceitarFacebookValido() {
        String facebook = "Usuario Facebook";
        portfolio.setFacebook(facebook);
        assertEquals(facebook, portfolio.getFacebook());
    }

    @Test
    @DisplayName("Deve aceitar Facebook com caracteres especiais")
    void deveAceitarFacebookComCaracteresEspeciais() {
        String facebook = "Usuario @#$ Facebook";
        portfolio.setFacebook(facebook);
        assertEquals(facebook, portfolio.getFacebook());
    }

    @Test
    @DisplayName("Deve aceitar Facebook nulo")
    void deveAceitarFacebookNulo() {
        portfolio.setFacebook(null);
        assertNull(portfolio.getFacebook());
    }

    @Test
    @DisplayName("Não deve aceitar Facebook muito longo")
    void naoDeveAceitarFacebookMuitoLongo() {
        String facebook = "a".repeat(51);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            portfolio.setFacebook(facebook);
        });
        assertEquals("O Facebook não pode exceder 50 caracteres", exception.getMessage());
    }
} 