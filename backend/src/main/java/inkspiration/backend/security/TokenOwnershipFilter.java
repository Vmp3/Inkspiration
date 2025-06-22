package inkspiration.backend.security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenOwnershipFilter extends OncePerRequestFilter {


    // Padrões de URLs que contêm IDs de usuário que precisam ser validados
    private static final Pattern[] USER_ID_PATTERNS = {
        Pattern.compile("/usuario/(\\d+)(?:/.*)?"),
        Pattern.compile("/usuario/detalhes/(\\d+)"),
        Pattern.compile("/usuario/atualizar/(\\d+)"),
        Pattern.compile("/usuario/(\\d+)/foto-perfil"),
        Pattern.compile("/usuario/(\\d+)/validate-token"),
        Pattern.compile("/profissional/usuario/(\\d+)(?:/.*)?"),
        Pattern.compile("/profissional/verificar/(\\d+)")
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Só valida para métodos que não sejam GET públicos
        if ("GET".equals(method) && isPublicEndpoint(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Verifica se a URL contém um ID de usuário que precisa ser validado
        Long userIdFromUrl = extractUserIdFromUrl(requestURI);
        
        if (userIdFromUrl != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
                Jwt jwt = jwtAuth.getToken();
                Long userIdFromToken = jwt.getClaim("userId");
                
                // Se o token não contém userId ou não corresponde ao ID da URL
                if (userIdFromToken == null || !userIdFromToken.equals(userIdFromUrl)) {
                    // Verifica se é admin
                    boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    
                    if (!isAdmin) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Token não pertence ao usuário especificado\"}");
                        return;
                    }
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private Long extractUserIdFromUrl(String requestURI) {
        for (Pattern pattern : USER_ID_PATTERNS) {
            Matcher matcher = pattern.matcher(requestURI);
            if (matcher.matches()) {
                try {
                    return Long.parseLong(matcher.group(1));
                } catch (NumberFormatException e) {
                    // Ignora se não conseguir converter para Long
                }
            }
        }
        return null;
    }

    private boolean isPublicEndpoint(String requestURI) {
        return requestURI.startsWith("/auth/") ||
               requestURI.equals("/profissional/publico") ||
               requestURI.matches("/profissional/\\d+") ||
               requestURI.matches("/profissional/\\d+/imagens") ||
               requestURI.matches("/portfolio/\\d+") ||
               requestURI.matches("/disponibilidades/profissional/\\d+") ||
               requestURI.matches("/disponibilidades/profissional/\\d+/verificar");
    }
} 