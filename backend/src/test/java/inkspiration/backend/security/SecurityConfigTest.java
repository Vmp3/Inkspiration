package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.web.SecurityFilterChain;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig - Testes Unitários")
class SecurityConfigTest {

    @Mock
    private RSAPublicKey publicKey;

    @Mock
    private RSAPrivateKey privateKey;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private TokenOwnershipFilter tokenOwnershipFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(securityConfig, "key", publicKey);
        ReflectionTestUtils.setField(securityConfig, "priv", privateKey);
    }

    @Test
    @DisplayName("Deve criar UserDetailsService corretamente")
    void deveCriarUserDetailsServiceCorretamente() {
        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService(customUserDetailsService);

        // Assert
        assertNotNull(userDetailsService);
        assertEquals(customUserDetailsService, userDetailsService);
    }

    @Test
    @DisplayName("Deve criar AuthenticationManager corretamente")
    void deveCriarAuthenticationManagerCorretamente() {
        // Arrange
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Act
        AuthenticationManager authManager = securityConfig.authManager(userDetailsService, passwordEncoder);

        // Assert
        assertNotNull(authManager);
        assertTrue(authManager instanceof ProviderManager);
        
        ProviderManager providerManager = (ProviderManager) authManager;
        assertEquals(1, providerManager.getProviders().size());
        assertTrue(providerManager.getProviders().get(0) instanceof DaoAuthenticationProvider);
    }

    @Test
    @DisplayName("Deve criar JwtDecoder corretamente")
    void deveCriarJwtDecoderCorretamente() {
        // Act
        JwtDecoder jwtDecoder = securityConfig.jwtDecoder();

        // Assert
        assertNotNull(jwtDecoder);
        assertTrue(jwtDecoder instanceof NimbusJwtDecoder);
    }
    @Test
    @DisplayName("Deve criar PasswordEncoder corretamente")
    void deveCriarPasswordEncoderCorretamente() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    @DisplayName("Deve criar JwtAuthenticationConverter corretamente")
    void deveCriarJwtAuthenticationConverterCorretamente() {
        // Act
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        // Assert
        assertNotNull(converter);
    }

    @Test
    @DisplayName("Deve criar CorsFilter corretamente")
    void deveCriarCorsFilterCorretamente() {
        // Act
        CorsFilter corsFilter = securityConfig.corsFilter();

        // Assert
        assertNotNull(corsFilter);
        
        UrlBasedCorsConfigurationSource source = (UrlBasedCorsConfigurationSource) ReflectionTestUtils.getField(corsFilter, "configSource");
        assertNotNull(source);
        
        CorsConfiguration config = source.getCorsConfigurations().get("/**");
        assertNotNull(config);
        assertTrue(config.getAllowedMethods().contains("*"));
        assertTrue(config.getAllowedHeaders().contains("*"));
        assertTrue(config.getAllowCredentials());
        assertTrue(config.getExposedHeaders().contains("Authorization"));
    }

} 