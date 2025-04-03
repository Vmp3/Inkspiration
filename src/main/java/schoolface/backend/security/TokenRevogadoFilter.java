package inkspiration.backend.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Component
public class TokenRevogadoFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private static final String ERRO_TOKEN_REVOGADO = "Erro: O token expirou ou alguma informação crítica do usuário foi alterada, realize o login novamente.";

    public TokenRevogadoFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtService.isTokenRevogado(token)) {
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"mensagem\": \"" + ERRO_TOKEN_REVOGADO + "\"}");
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 