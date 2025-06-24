package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PublicUrls - Testes Unitários")
class PublicUrlsTest {

    @Test
    @DisplayName("Deve conter URL de autenticação")
    void deveConterUrlAutenticacao() {
        assertTrue(containsUrl("/auth/**"), "Deve conter URL base de autenticação");
        assertTrue(containsUrl("/auth/register/**"), "Deve conter URL de registro");
    }

    @Test
    @DisplayName("Deve conter URL do console H2")
    void deveConterUrlConsoleH2() {
        assertTrue(containsUrl("/h2-console/**"), "Deve conter URL do console H2");
    }

    @Test
    @DisplayName("Deve conter URL de profissionais")
    void deveConterUrlProfissionais() {
        assertTrue(containsUrl("/profissional/**"), "Deve conter URL de profissionais");
    }

    @Test
    @DisplayName("Deve conter URL de portfólios")
    void deveConterUrlPortfolios() {
        assertTrue(containsUrl("/portfolios/**"), "Deve conter URL de portfólios");
    }

    @Test
    @DisplayName("Deve conter URL de usuários")
    void deveConterUrlUsuarios() {
        assertTrue(containsUrl("/usuarios/**"), "Deve conter URL de usuários");
    }

    @Test
    @DisplayName("Deve ter quantidade correta de URLs")
    void deveTerQuantidadeCorretaDeUrls() {
        assertEquals(6, PublicUrls.URLS.length, "Deve ter exatamente 6 URLs públicas");
    }

    @Test
    @DisplayName("Não deve conter URLs nulas")
    void naoDeveConterUrlsNulas() {
        for (String url : PublicUrls.URLS) {
            assertNotNull(url, "URLs não devem ser nulas");
        }
    }

    @Test
    @DisplayName("Não deve conter URLs vazias")
    void naoDeveConterUrlsVazias() {
        for (String url : PublicUrls.URLS) {
            assertFalse(url.trim().isEmpty(), "URLs não devem ser vazias");
        }
    }

    @Test
    @DisplayName("Todas URLs devem começar com /")
    void todasUrlsDevemComecarComBarra() {
        for (String url : PublicUrls.URLS) {
            assertTrue(url.startsWith("/"), "URLs devem começar com /");
        }
    }

    @Test
    @DisplayName("Todas URLs públicas devem terminar com /**")
    void todasUrlsDevemTerminarComWildcard() {
        for (String url : PublicUrls.URLS) {
            assertTrue(url.endsWith("/**"), "URLs públicas devem terminar com /**");
        }
    }

    private boolean containsUrl(String url) {
        for (String publicUrl : PublicUrls.URLS) {
            if (publicUrl.equals(url)) {
                return true;
            }
        }
        return false;
    }
} 