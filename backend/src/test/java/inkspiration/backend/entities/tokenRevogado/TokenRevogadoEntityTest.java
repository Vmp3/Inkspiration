package inkspiration.backend.entities.tokenRevogado;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TokenRevogado;

@DisplayName("Testes gerais da entidade TokenRevogado")
public class TokenRevogadoEntityTest {

    @Test
    @DisplayName("Deve criar token revogado com construtor padrão")
    void deveCriarTokenComConstrutorPadrao() {
        TokenRevogado tokenRevogado = new TokenRevogado();
        
        assertNotNull(tokenRevogado);
        assertNull(tokenRevogado.getToken());
        assertNull(tokenRevogado.getDataRevogacao());
        assertNull(tokenRevogado.getId());
    }

    @Test
    @DisplayName("Deve criar token revogado com construtor completo")
    void deveCriarTokenComConstrutorCompleto() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        TokenRevogado tokenRev = new TokenRevogado(token);
        
        assertEquals(token, tokenRev.getToken());
        assertNotNull(tokenRev.getDataRevogacao());
        assertNull(tokenRev.getId());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        TokenRevogado tokenRevogado = new TokenRevogado();
        Long id = 123L;
        
        tokenRevogado.setId(id);
        assertEquals(id, tokenRevogado.getId());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        TokenRevogado tokenRevogado = new TokenRevogado();
        
        tokenRevogado.setId(null);
        assertNull(tokenRevogado.getId());
    }
} 