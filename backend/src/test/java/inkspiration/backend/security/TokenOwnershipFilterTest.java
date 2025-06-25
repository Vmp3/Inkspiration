package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenOwnershipFilter - Testes Unitários")
class TokenOwnershipFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private TokenOwnershipFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve permitir acesso a endpoints GET públicos")
    void devePermitirAcessoEndpointsGetPublicos() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/auth/login");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve permitir acesso quando token pertence ao usuário")
    void devePermitirAcessoQuandoTokenPertenceAoUsuario() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/usuario/123/atualizar");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve permitir acesso quando usuário é admin")
    void devePermitirAcessoQuandoUsuarioEAdmin() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/usuario/123/atualizar");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(456L);
        when(jwtAuth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve negar acesso quando token não pertence ao usuário")
    void deveNegarAcessoQuandoTokenNaoPertenceAoUsuario() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/usuario/123/atualizar");

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(456L);
        when(jwtAuth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        writer.flush();
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve permitir acesso quando URL não contém ID de usuário")
    void devePermitirAcessoQuandoUrlNaoContemIdUsuario() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/outros/recursos");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve permitir acesso a endpoints públicos específicos")
    void devePermitirAcessoEndpointsPublicosEspecificos() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("GET");
        String[] publicEndpoints = {
            "/auth/login",
            "/profissional/publico",
            "/profissional/123",
            "/profissional/123/imagens",
            "/portfolio/123",
            "/disponibilidades/profissional/123",
            "/disponibilidades/profissional/123/verificar"
        };

        for (String endpoint : publicEndpoints) {
            // Reset para cada iteração
            reset(request, filterChain);
            when(request.getRequestURI()).thenReturn(endpoint);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    @DisplayName("Deve validar diferentes padrões de URL com ID de usuário")
    void deveValidarDiferentesPadroesUrlComIdUsuario() throws ServletException, IOException {
        // Arrange
        when(request.getMethod()).thenReturn("PUT");
        String[] urlPatterns = {
            "/usuario/123",
            "/usuario/123/foto-perfil",
            "/usuario/detalhes/123",
            "/usuario/atualizar/123",
            "/usuario/123/validate-token",
            "/profissional/usuario/123",
            "/profissional/verificar/123"
        };

        JwtAuthenticationToken jwtAuth = mock(JwtAuthenticationToken.class);
        when(jwtAuth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(securityContext.getAuthentication()).thenReturn(jwtAuth);

        for (String url : urlPatterns) {
            // Reset para cada iteração
            reset(request, filterChain);
            when(request.getRequestURI()).thenReturn(url);

            // Act
            filter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
        }
    }
} 