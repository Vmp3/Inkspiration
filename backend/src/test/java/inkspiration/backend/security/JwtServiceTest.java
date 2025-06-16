package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import inkspiration.backend.config.JwtConfig;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private JwtEncoder encoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtService jwtService;

    private Usuario usuario;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf("12345678901");

        jwt = mock(Jwt.class);
        
        // Configura os valores padrão
        ReflectionTestUtils.setField(jwtService, "expiration", 720);
        ReflectionTestUtils.setField(jwtService, "defaultTokenExpirationMinutes", 720L);
        ReflectionTestUtils.setField(jwtService, "rememberMeTokenExpirationMinutes", 43200L);
    }

    @Test
    void testGenerateToken_Success() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(usuarioRepository).findByCpf(cpf);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGenerateToken_WithRememberMe_Success() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token-remember-me";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication, true);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(usuarioRepository).findByCpf(cpf);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGenerateToken_UserNotFound() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token-no-user";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(usuarioRepository).findByCpf(cpf);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGenerateToken_DatabaseException() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token-exception";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenThrow(new RuntimeException("Database error"));

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(usuarioRepository).findByCpf(cpf);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGetUserIdFromToken_Success() {
        // Arrange
        String token = "valid-jwt-token";
        Long expectedUserId = 1L;

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(expectedUserId);

        // Act
        Long userId = jwtService.getUserIdFromToken(token);

        // Assert
        assertEquals(expectedUserId, userId);
        verify(jwtDecoder).decode(token);
    }

    @Test
    void testGetUserIdFromToken_NullUserId() {
        // Arrange
        String token = "valid-jwt-token-no-userid";

        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(null);

        // Act
        Long userId = jwtService.getUserIdFromToken(token);

        // Assert
        assertNull(userId);
        verify(jwtDecoder).decode(token);
    }

    @Test
    void testIsTokenRevogado_TokenRevoked() {
        // Arrange
        String token = "revoked-token";
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(true);

        // Act
        boolean isRevoked = jwtService.isTokenRevogado(token);

        // Assert
        assertTrue(isRevoked);
        verify(tokenRevogadoRepository).existsByToken(token);
    }

    @Test
    void testIsTokenRevogado_TokenNotRevoked() {
        // Arrange
        String token = "valid-token";
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(false);

        // Act
        boolean isRevoked = jwtService.isTokenRevogado(token);

        // Assert
        assertFalse(isRevoked);
        verify(tokenRevogadoRepository).existsByToken(token);
    }

    @Test
    void testGenerateToken_MultipleAuthorities() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token-multi-auth";
        Collection<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testGenerateToken_RememberMeFalse() {
        // Arrange
        String cpf = "12345678901";
        String expectedToken = "mocked-jwt-token-no-remember";
        Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        when(authentication.getName()).thenReturn(cpf);
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getTokenValue()).thenReturn(expectedToken);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        // Act
        String token = jwtService.generateToken(authentication, false);

        // Assert
        assertNotNull(token);
        assertEquals(expectedToken, token);
        verify(encoder).encode(any(JwtEncoderParameters.class));
    }
} 