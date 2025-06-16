package inkspiration.backend.entities.token;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TokenRevogado;
import java.time.LocalDateTime;

public class TokenRevogadoTest {

    private TokenRevogado tokenRevogado;

    @BeforeEach
    void setUp() {
        tokenRevogado = new TokenRevogado();
    }

    @Test
    void testGettersAndSettersId() {
        Long id = 1L;
        tokenRevogado.setId(id);
        assertEquals(id, tokenRevogado.getId(), "ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        tokenRevogado.setToken(token);
        assertEquals(token, tokenRevogado.getToken(), "Token deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersDataRevogacao() {
        LocalDateTime dataRevogacao = LocalDateTime.now();
        tokenRevogado.setDataRevogacao(dataRevogacao);
        assertEquals(dataRevogacao, tokenRevogado.getDataRevogacao(), "Data de revogação deve ser igual à definida");
    }

    @Test
    void testConstrutorPadrao() {
        TokenRevogado tokenVazio = new TokenRevogado();
        
        assertNull(tokenVazio.getId(), "ID deve ser nulo inicialmente");
        assertNull(tokenVazio.getToken(), "Token deve ser nulo inicialmente");
        assertNull(tokenVazio.getDataRevogacao(), "Data de revogação deve ser nula inicialmente");
    }

    @Test
    void testConstrutorComToken() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        TokenRevogado tokenComParametro = new TokenRevogado(token);
        
        assertEquals(token, tokenComParametro.getToken(), "Token deve ser igual ao fornecido no construtor");
        assertNotNull(tokenComParametro.getDataRevogacao(), "Data de revogação deve ser definida automaticamente");
        
        // Verifica se a data de revogação é recente (dentro de 1 segundo)
        LocalDateTime agora = LocalDateTime.now();
        assertTrue(tokenComParametro.getDataRevogacao().isBefore(agora.plusSeconds(1)), 
                  "Data de revogação deve estar próxima ao momento atual");
        assertTrue(tokenComParametro.getDataRevogacao().isAfter(agora.minusSeconds(1)), 
                  "Data de revogação deve estar próxima ao momento atual");
    }

    @Test
    void testTokenRevogadoCompleto() {
        // Arrange
        Long id = 1L;
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        LocalDateTime dataRevogacao = LocalDateTime.now();

        // Act
        tokenRevogado.setId(id);
        tokenRevogado.setToken(token);
        tokenRevogado.setDataRevogacao(dataRevogacao);

        // Assert
        assertEquals(id, tokenRevogado.getId());
        assertEquals(token, tokenRevogado.getToken());
        assertEquals(dataRevogacao, tokenRevogado.getDataRevogacao());
    }

    @Test
    void testTokensVariados() {
        String[] tokens = {
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.EkN-DOsnsuRjRO6BxXemmJDm3HbxrbRzXglbN2S4sOkopdU4IsDxTI8jO19W_A4K8ZPJijNLis4EZsHeY559a4DFOd50_OqgHs3-KvBdInBfm5GG8IK4REw5C2Mn9nFOJ5hbZ1-5kLqqHRpDgD6o5y6w_C_nRlHUGe3kgZJCgC4",
            "simple-token-123",
            "a".repeat(500), // Token longo
            "token-com-caracteres-especiais-@#$%"
        };

        for (String token : tokens) {
            assertDoesNotThrow(() -> {
                tokenRevogado.setToken(token);
                assertEquals(token, tokenRevogado.getToken());
            }, "Deve aceitar token: " + token.substring(0, Math.min(50, token.length())) + "...");
        }
    }

    @Test
    void testTokenLongo() {
        String tokenLongo = "a".repeat(1000);
        
        assertDoesNotThrow(() -> {
            tokenRevogado.setToken(tokenLongo);
        }, "Deve aceitar token longo sem lançar exceção");
        
        assertEquals(tokenLongo, tokenRevogado.getToken(), "Deve armazenar token longo corretamente");
    }

    @Test
    void testTokenVazio() {
        tokenRevogado.setToken("");
        assertEquals("", tokenRevogado.getToken(), "Deve aceitar token vazio");
        
        tokenRevogado.setToken(null);
        assertNull(tokenRevogado.getToken(), "Deve aceitar token nulo");
    }

    @Test
    void testDataRevogacaoVariadas() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime[] datas = {
            agora,
            agora.minusMinutes(1),
            agora.minusHours(1),
            agora.minusDays(1),
            agora.minusMonths(1),
            agora.plusMinutes(1) // Data futura (caso o sistema permita)
        };

        for (LocalDateTime data : datas) {
            assertDoesNotThrow(() -> {
                tokenRevogado.setDataRevogacao(data);
                assertEquals(data, tokenRevogado.getDataRevogacao());
            }, "Deve aceitar data de revogação: " + data);
        }
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        tokenRevogado.setId(idMaximo);
        assertEquals(idMaximo, tokenRevogado.getId(), "Deve aceitar ID máximo");
        
        tokenRevogado.setId(idMinimo);
        assertEquals(idMinimo, tokenRevogado.getId(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testTokenComCaracteresEspeciais() {
        String tokenComEspeciais = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkrDo29vIGRhIFPDoW8gUGF1bG8iLCJpYXQiOjE1MTYyMzkwMjJ9.abc123-_+=";
        
        tokenRevogado.setToken(tokenComEspeciais);
        assertEquals(tokenComEspeciais, tokenRevogado.getToken(), "Deve aceitar token com caracteres especiais");
    }

    @Test
    void testTokenJWTCompleto() {
        // Token JWT válido com header, payload e signature
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                         "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ." +
                         "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        tokenRevogado.setToken(jwtToken);
        assertEquals(jwtToken, tokenRevogado.getToken(), "Deve aceitar token JWT completo");
        
        // Verifica se o token tem as 3 partes separadas por ponto
        String[] partes = tokenRevogado.getToken().split("\\.");
        assertEquals(3, partes.length, "Token JWT deve ter 3 partes separadas por ponto");
    }

    @Test
    void testDataRevogacaoNula() {
        tokenRevogado.setDataRevogacao(null);
        assertNull(tokenRevogado.getDataRevogacao(), "Deve aceitar data de revogação nula");
    }

    @Test
    void testFluxoCompletoRevogacao() {
        // Simula um fluxo completo de revogação de token
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        // Criação do token revogado
        TokenRevogado tokenRevogadoFluxo = new TokenRevogado(token);
        
        // Verificações
        assertEquals(token, tokenRevogadoFluxo.getToken(), "Token deve ser armazenado corretamente");
        assertNotNull(tokenRevogadoFluxo.getDataRevogacao(), "Data de revogação deve ser definida automaticamente");
        
        // Verifica se a data está dentro de um intervalo razoável (últimos 5 segundos)
        LocalDateTime agora = LocalDateTime.now();
        assertTrue(tokenRevogadoFluxo.getDataRevogacao().isAfter(agora.minusSeconds(5)), 
                  "Data de revogação deve ser recente");
    }

    @Test
    void testTokenComEspacos() {
        String tokenComEspacos = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        
        tokenRevogado.setToken(tokenComEspacos);
        assertEquals(tokenComEspacos, tokenRevogado.getToken(), "Deve aceitar token com espaços (Bearer token)");
    }

    @Test
    void testTokensMultiplos() {
        // Testando múltiplos tokens revogados
        TokenRevogado token1 = new TokenRevogado("token1");
        TokenRevogado token2 = new TokenRevogado("token2");
        TokenRevogado token3 = new TokenRevogado("token3");
        
        assertAll("Múltiplos tokens revogados",
            () -> assertEquals("token1", token1.getToken()),
            () -> assertEquals("token2", token2.getToken()),
            () -> assertEquals("token3", token3.getToken()),
            () -> assertNotNull(token1.getDataRevogacao()),
            () -> assertNotNull(token2.getDataRevogacao()),
            () -> assertNotNull(token3.getDataRevogacao())
        );
    }

    @Test
    void testPrecisaoDataRevogacao() {
        TokenRevogado token1 = new TokenRevogado("token1");
        
        // Pequena pausa para garantir diferença de tempo
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        TokenRevogado token2 = new TokenRevogado("token2");
        
        // As datas devem ser diferentes (mesmo que por milissegundos)
        assertNotEquals(token1.getDataRevogacao(), token2.getDataRevogacao(), 
                       "Datas de revogação de tokens diferentes devem ser diferentes");
    }

    @Test
    void testTokenUnicode() {
        String tokenUnicode = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLwn5iAIiwibmFtZSI6IkrDo29vIPCfkYQiLCJpYXQiOjE1MTYyMzkwMjJ9.signature";
        
        tokenRevogado.setToken(tokenUnicode);
        assertEquals(tokenUnicode, tokenRevogado.getToken(), "Deve aceitar token com caracteres Unicode");
    }
} 