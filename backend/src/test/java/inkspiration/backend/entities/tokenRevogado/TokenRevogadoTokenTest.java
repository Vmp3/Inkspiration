package inkspiration.backend.entities.tokenRevogado;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TokenRevogado;

@DisplayName("Testes de validação de token - TokenRevogado")
public class TokenRevogadoTokenTest {

    private TokenRevogado tokenRevogado;

    @BeforeEach
    void setUp() {
        tokenRevogado = new TokenRevogado();
    }

    @Test
    @DisplayName("Deve definir token válido")
    void deveDefinirTokenValido() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        tokenRevogado.setToken(token);
        assertEquals(token, tokenRevogado.getToken());
    }

    @Test
    @DisplayName("Deve remover espaços do token")
    void deveRemoverEspacosDoToken() {
        String token = " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 ";
        tokenRevogado.setToken(token);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", tokenRevogado.getToken());
    }

    @Test
    @DisplayName("Não deve aceitar token nulo")
    void naoDeveAceitarTokenNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setToken(null);
        });
        assertEquals("O token não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar token vazio")
    void naoDeveAceitarTokenVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setToken("");
        });
        assertEquals("O token não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar token com apenas espaços")
    void naoDeveAceitarTokenComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setToken("   ");
        });
        assertEquals("O token não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar token muito curto")
    void naoDeveAceitarTokenMuitoCurto() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setToken("123456789"); // 9 caracteres
        });
        assertEquals("O token deve ter pelo menos 10 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar token com exatamente 10 caracteres")
    void deveAceitarTokenCom10Caracteres() {
        String token = "1234567890"; // 10 caracteres
        tokenRevogado.setToken(token);
        assertEquals(token, tokenRevogado.getToken());
    }

    @Test
    @DisplayName("Não deve aceitar token muito longo")
    void naoDeveAceitarTokenMuitoLongo() {
        String tokenLongo = "a".repeat(1001); // 1001 caracteres
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenRevogado.setToken(tokenLongo);
        });
        assertEquals("O token não pode exceder 1000 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar token com exatamente 1000 caracteres")
    void deveAceitarTokenCom1000Caracteres() {
        String token = "a".repeat(1000); // 1000 caracteres
        tokenRevogado.setToken(token);
        assertEquals(token, tokenRevogado.getToken());
    }
} 