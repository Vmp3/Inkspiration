package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import inkspiration.backend.config.JwtConfig;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService - Testes Unitários")
class JwtServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "defaultTokenExpirationMinutes", 720L);
        ReflectionTestUtils.setField(jwtService, "rememberMeTokenExpirationMinutes", 43200L);
    }

    @Test
    @DisplayName("Deve gerar token com configurações padrão")
    void deveGerarTokenComConfiguracoesPadrao() {
        // Arrange
        String cpf = "12345678900";
        Long userId = 123L;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(userId);
        usuario.setCpf(cpf);

        when(authentication.getName()).thenReturn(cpf);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
            .thenReturn(mock(org.springframework.security.oauth2.jwt.Jwt.class));

        // Act
        jwtService.generateToken(authentication);

        // Assert
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        assertEquals(cpf, claims.getSubject());
        assertEquals("ROLE_USER", claims.getClaim("scope"));
        assertEquals(userId, claims.getClaim("userId"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
    }

    @Test
    @DisplayName("Deve gerar token com 'lembrar de mim'")
    void deveGerarTokenComLembrarDeMim() {
        // Arrange
        String cpf = "12345678900";
        when(authentication.getName()).thenReturn(cpf);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
            .thenReturn(mock(org.springframework.security.oauth2.jwt.Jwt.class));

        // Act
        jwtService.generateToken(authentication, true);

        // Assert
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();
        
        // Verifica se a expiração é aproximadamente 30 dias (43200 minutos)
        long durationSeconds = expiresAt.getEpochSecond() - issuedAt.getEpochSecond();
        assertEquals(43200 * 60, durationSeconds);
    }

    @Test
    @DisplayName("Deve obter ID do usuário do token")
    void deveObterIdUsuarioDoToken() {
        // Arrange
        String token = "token";
        Long userId = 123L;
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("userId")).thenReturn(userId);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        // Act
        Long result = jwtService.getUserIdFromToken(token);

        // Assert
        assertEquals(userId, result);
        verify(jwtDecoder).decode(token);
    }

    @Test
    @DisplayName("Deve verificar se token está revogado")
    void deveVerificarSeTokenEstaRevogado() {
        // Arrange
        String token = "token";
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(true);

        // Act & Assert
        assertTrue(jwtService.isTokenRevogado(token));
        verify(tokenRevogadoRepository).existsByToken(token);
    }

    @Test
    @DisplayName("Deve gerar token mesmo quando usuário não é encontrado")
    void deveGerarTokenMesmoQuandoUsuarioNaoEncontrado() {
        // Arrange
        String cpf = "12345678900";
        when(authentication.getName()).thenReturn(cpf);
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
            .thenReturn(mock(org.springframework.security.oauth2.jwt.Jwt.class));

        // Act
        jwtService.generateToken(authentication);

        // Assert
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        assertNull(claims.getClaim("userId"));
    }

    @Test
    @DisplayName("Deve gerar token com múltiplas roles")
    void deveGerarTokenComMultiplasRoles() {
        // Arrange
        String cpf = "12345678900";
        when(authentication.getName()).thenReturn(cpf);
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        doReturn(authorities).when(authentication).getAuthorities();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
            .thenReturn(mock(org.springframework.security.oauth2.jwt.Jwt.class));

        // Act
        jwtService.generateToken(authentication);

        // Assert
        ArgumentCaptor<JwtEncoderParameters> parametersCaptor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(parametersCaptor.capture());

        JwtClaimsSet claims = parametersCaptor.getValue().getClaims();
        assertEquals("ROLE_USER ROLE_ADMIN", claims.getClaim("scope"));
    }
} 