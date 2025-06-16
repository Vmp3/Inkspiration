package inkspiration.backend.security;

import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.repository.TokenRevogadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthenticationService")
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

    @BeforeEach
    void setUp() {
        // Setup básico se necessário
    }

    @Test
    @DisplayName("Deve autenticar usuário e retornar token")
    void deveAutenticarUsuarioERetornarToken() {
        // Given
        when(jwtService.generateToken(authentication)).thenReturn(token);

        // When
        String resultado = authenticationService.authenticate(authentication);

        // Then
        assertNotNull(resultado);
        assertEquals(token, resultado);
        verify(jwtService, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve autenticar usuário com remember me")
    void deveAutenticarUsuarioComRememberMe() {
        // Given
        Boolean rememberMe = true;
        when(jwtService.generateToken(authentication, rememberMe)).thenReturn(token);

        // When
        String resultado = authenticationService.authenticate(authentication, rememberMe);

        // Then
        assertNotNull(resultado);
        assertEquals(token, resultado);
        verify(jwtService, times(1)).generateToken(authentication, rememberMe);
    }

    @Test
    @DisplayName("Deve autenticar usuário sem remember me")
    void deveAutenticarUsuarioSemRememberMe() {
        // Given
        Boolean rememberMe = false;
        when(jwtService.generateToken(authentication, rememberMe)).thenReturn(token);

        // When
        String resultado = authenticationService.authenticate(authentication, rememberMe);

        // Then
        assertNotNull(resultado);
        assertEquals(token, resultado);
        verify(jwtService, times(1)).generateToken(authentication, rememberMe);
    }

    @Test
    @DisplayName("Deve autenticar usuário com remember me null")
    void deveAutenticarUsuarioComRememberMeNull() {
        // Given
        Boolean rememberMe = null;
        when(jwtService.generateToken(authentication, rememberMe)).thenReturn(token);

        // When
        String resultado = authenticationService.authenticate(authentication, rememberMe);

        // Then
        assertNotNull(resultado);
        assertEquals(token, resultado);
        verify(jwtService, times(1)).generateToken(authentication, rememberMe);
    }

    @Test
    @DisplayName("Deve verificar se token está revogado")
    void deveVerificarSeTokenEstaRevogado() {
        // Given
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(true);

        // When
        boolean resultado = authenticationService.isTokenRevoked(token);

        // Then
        assertTrue(resultado);
        verify(tokenRevogadoRepository, times(1)).existsByToken(token);
    }

    @Test
    @DisplayName("Deve verificar se token não está revogado")
    void deveVerificarSeTokenNaoEstaRevogado() {
        // Given
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(false);

        // When
        boolean resultado = authenticationService.isTokenRevoked(token);

        // Then
        assertFalse(resultado);
        verify(tokenRevogadoRepository, times(1)).existsByToken(token);
    }

    @Test
    @DisplayName("Deve revogar token com sucesso")
    void deveRevogarTokenComSucesso() {
        // Given
        TokenRevogado tokenRevogado = new TokenRevogado(token);
        when(tokenRevogadoRepository.save(any(TokenRevogado.class))).thenReturn(tokenRevogado);

        // When
        assertDoesNotThrow(() -> authenticationService.revogarToken(token));

        // Then
        verify(tokenRevogadoRepository, times(1)).save(any(TokenRevogado.class));
    }

    @Test
    @DisplayName("Não deve revogar token nulo")
    void naoDeveRevogarTokenNulo() {
        // When
        assertDoesNotThrow(() -> authenticationService.revogarToken(null));

        // Then
        verify(tokenRevogadoRepository, never()).save(any(TokenRevogado.class));
    }

    @Test
    @DisplayName("Não deve revogar token vazio")
    void naoDeveRevogarTokenVazio() {
        // When
        assertDoesNotThrow(() -> authenticationService.revogarToken(""));

        // Then
        verify(tokenRevogadoRepository, never()).save(any(TokenRevogado.class));
    }

    @Test
    @DisplayName("Deve revogar token com espaços")
    void deveRevogarTokenComEspacos() {
        // Given
        String tokenComEspacos = "   ";
        TokenRevogado tokenRevogado = new TokenRevogado(tokenComEspacos);
        when(tokenRevogadoRepository.save(any(TokenRevogado.class))).thenReturn(tokenRevogado);

        // When
        assertDoesNotThrow(() -> authenticationService.revogarToken(tokenComEspacos));

        // Then
        verify(tokenRevogadoRepository, times(1)).save(any(TokenRevogado.class));
    }

    @Test
    @DisplayName("Deve tratar erro ao salvar token revogado")
    void deveTratarErroAoSalvarTokenRevogado() {
        // Given
        when(tokenRevogadoRepository.save(any(TokenRevogado.class)))
            .thenThrow(new RuntimeException("Erro de banco"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationService.revogarToken(token));
        
        verify(tokenRevogadoRepository, times(1)).save(any(TokenRevogado.class));
    }

    @Test
    @DisplayName("Deve tratar erro ao verificar token revogado")
    void deveTratarErroAoVerificarTokenRevogado() {
        // Given
        when(tokenRevogadoRepository.existsByToken(token))
            .thenThrow(new RuntimeException("Erro de banco"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationService.isTokenRevoked(token));
        
        verify(tokenRevogadoRepository, times(1)).existsByToken(token);
    }

    @Test
    @DisplayName("Deve tratar erro na geração de token")
    void deveTratarErroNaGeracaoToken() {
        // Given
        when(jwtService.generateToken(authentication))
            .thenThrow(new RuntimeException("Erro na geração"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(authentication));
        
        verify(jwtService, times(1)).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve tratar erro na geração de token com remember me")
    void deveTratarErroNaGeracaoTokenComRememberMe() {
        // Given
        Boolean rememberMe = true;
        when(jwtService.generateToken(authentication, rememberMe))
            .thenThrow(new RuntimeException("Erro na geração"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(authentication, rememberMe));
        
        verify(jwtService, times(1)).generateToken(authentication, rememberMe);
    }
} 