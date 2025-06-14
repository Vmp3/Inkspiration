package inkspiration.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.repository.TokenRevogadoRepository;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final TokenRevogadoRepository tokenRevogadoRepository;

    public AuthenticationService(JwtService jwtService, TokenRevogadoRepository tokenRevogadoRepository) {
        this.jwtService = jwtService;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
    }

    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
    
    public String authenticate(Authentication authentication, Boolean rememberMe) {
        return jwtService.generateToken(authentication, rememberMe);
    }
    
    public boolean isTokenRevoked(String token) {
        return tokenRevogadoRepository.existsByToken(token);
    }

    public void revogarToken(String token) {
        if (token != null && !token.isEmpty()) {
            TokenRevogado tokenRevogado = new TokenRevogado(token);
            tokenRevogadoRepository.save(tokenRevogado);
        }
    }
}