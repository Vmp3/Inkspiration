package inkspiration.backend.security;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(2) // Executar após o TokenRevogadoFilter
public class TokenResponseFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public TokenResponseFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Verificar se o token é válido
                if (!jwtService.isTokenRevogado(token)) {
                    // Extrair o ID do usuário do token
                    Long userId = jwtService.getUserIdFromToken(token);
                    
                    if (userId != null) {
                        // Buscar o usuário pelo ID
                        usuarioRepository.findById(userId).ifPresent(usuario -> {
                            // Verificar se o token atual do usuário é diferente do token na requisição
                            String tokenAtual = usuario.getTokenAtual();
                            if (tokenAtual != null && !tokenAtual.equals(token)) {
                                // Adicionar o token atualizado no header da resposta
                                response.setHeader("New-Auth-Token", tokenAtual);
                                response.setHeader("Access-Control-Expose-Headers", "New-Auth-Token");
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // Apenas logar o erro, não interromper a requisição
                System.err.println("Erro ao processar token para resposta: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 