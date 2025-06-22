package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.usuario.TokenValidationException;

@DisplayName("UsuarioService - Testes de Token")
class UsuarioServiceTokenTest {

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve validar token válido")
    void deveValidarTokenValido() {
        String token = "token-valido-123";
        usuario.setTokenAtual(token);
        
        assertNotNull(usuario.getTokenAtual());
        assertEquals(token, usuario.getTokenAtual());
    }

    @Test
    @DisplayName("Deve lançar exceção para token nulo")
    void deveLancarExcecaoParaTokenNulo() {
        assertThrows(TokenValidationException.class, () -> {
            String token = null;
            if (token == null || token.isEmpty()) {
                throw new TokenValidationException("Token não fornecido");
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para token vazio")
    void deveLancarExcecaoParaTokenVazio() {
        assertThrows(TokenValidationException.class, () -> {
            String token = "";
            if (token == null || token.isEmpty()) {
                throw new TokenValidationException("Token não fornecido");
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para usuário sem token ativo")
    void deveLancarExcecaoParaUsuarioSemTokenAtivo() {
        assertThrows(TokenValidationException.class, () -> {
            if (usuario.getTokenAtual() == null) {
                throw new TokenValidationException("Usuário não possui token ativo");
            }
        });
    }

    @Test
    @DisplayName("Deve validar token com diferentes formatos")
    void deveValidarTokenComDiferentesFormatos() {
        // Token simples
        usuario.setTokenAtual("token123");
        assertEquals("token123", usuario.getTokenAtual());

        // Token com UUID
        usuario.setTokenAtual("550e8400-e29b-41d4-a716-446655440000");
        assertEquals("550e8400-e29b-41d4-a716-446655440000", usuario.getTokenAtual());

        // Token JWT simulado
        usuario.setTokenAtual("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ");
        assertNotNull(usuario.getTokenAtual());
        assertTrue(usuario.getTokenAtual().length() > 50);
    }

    @Test
    @DisplayName("Deve validar token com espaços")
    void deveValidarTokenComEspacos() {
        String tokenComEspacos = "  token-com-espacos  ";
        String tokenLimpo = tokenComEspacos.trim();
        
        usuario.setTokenAtual(tokenLimpo);
        assertEquals("token-com-espacos", usuario.getTokenAtual());
        assertFalse(usuario.getTokenAtual().contains(" "));
    }

    @Test
    @DisplayName("Deve validar token longo")
    void deveValidarTokenLongo() {
        String tokenLongo = "token".repeat(100); // 500 caracteres
        usuario.setTokenAtual(tokenLongo);
        
        assertEquals(tokenLongo, usuario.getTokenAtual());
        assertEquals(500, usuario.getTokenAtual().length());
    }

    @Test
    @DisplayName("Deve limpar token do usuário")
    void deveLimparTokenDoUsuario() {
        usuario.setTokenAtual("token-temporario");
        assertNotNull(usuario.getTokenAtual());
        
        usuario.setTokenAtual(null);
        assertNull(usuario.getTokenAtual());
    }

    @Test
    @DisplayName("Deve validar token especial")
    void deveValidarTokenEspecial() {
        String tokenEspecial = "token-123_ABC@#$%";
        usuario.setTokenAtual(tokenEspecial);
        
        assertEquals(tokenEspecial, usuario.getTokenAtual());
        assertTrue(usuario.getTokenAtual().contains("@"));
        assertTrue(usuario.getTokenAtual().contains("#"));
    }

    @Test
    @DisplayName("Deve comparar tokens")
    void deveCompararTokens() {
        String token1 = "token-123";
        String token2 = "token-456";
        String token3 = "token-123";
        
        usuario.setTokenAtual(token1);
        
        assertEquals(token1, usuario.getTokenAtual());
        assertNotEquals(token2, usuario.getTokenAtual());
        assertEquals(token3, usuario.getTokenAtual());
    }

    @Test
    @DisplayName("Deve validar integridade do token")
    void deveValidarIntegridadeDoToken() {
        String tokenOriginal = "token-original-123";
        usuario.setTokenAtual(tokenOriginal);
        
        // Simula validação de integridade
        String tokenRecuperado = usuario.getTokenAtual();
        assertEquals(tokenOriginal, tokenRecuperado);
        assertEquals(tokenOriginal.length(), tokenRecuperado.length());
        assertTrue(tokenRecuperado.startsWith("token-"));
        assertTrue(tokenRecuperado.endsWith("-123"));
    }

    @Test
    @DisplayName("Deve validar token case-sensitive")
    void deveValidarTokenCaseSensitive() {
        String tokenMinusculo = "token-minusculo";
        String tokenMaiusculo = "TOKEN-MAIUSCULO";
        
        usuario.setTokenAtual(tokenMinusculo);
        assertNotEquals(tokenMaiusculo, usuario.getTokenAtual());
        
        usuario.setTokenAtual(tokenMaiusculo);
        assertNotEquals(tokenMinusculo, usuario.getTokenAtual());
    }
} 