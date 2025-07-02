package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionFilter - Testes Unitários")
class SessionFilterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private SessionFilter sessionFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";

    @Test
    @DisplayName("Deve retornar true para token válido")
    void deveRetornarTrueParaTokenValido() {
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn("123");

        assertTrue(sessionFilter.isValidSession(VALID_TOKEN));
    }

    @Test
    @DisplayName("Deve retornar false para token inválido")
    void deveRetornarFalseParaTokenInvalido() {
        when(jwtDecoder.decode(INVALID_TOKEN)).thenThrow(JwtException.class);

        assertFalse(sessionFilter.isValidSession(INVALID_TOKEN));
    }

    @Test
    @DisplayName("Deve retornar false para token nulo")
    void deveRetornarFalseParaTokenNulo() {
        assertFalse(sessionFilter.isValidSession(null));
    }

    @Test
    @DisplayName("Deve retornar false quando subject é nulo")
    void deveRetornarFalseQuandoSubjectNulo() {
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getSubject()).thenReturn(null);

        assertFalse(sessionFilter.isValidSession(VALID_TOKEN));
    }
} 