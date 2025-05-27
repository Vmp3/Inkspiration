package inkspiration.backend.security;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import inkspiration.backend.config.JwtConfig;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioRepository;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder jwtDecoder;
    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final JwtConfig jwtConfig;
    private final UsuarioRepository usuarioRepository;
    
    @Value("${api.security.token.expiration:720}")
    private int expiration;

    @Autowired
    public JwtService(JwtEncoder encoder, JwtDecoder jwtDecoder, TokenRevogadoRepository tokenRevogadoRepository, JwtConfig jwtConfig, UsuarioRepository usuarioRepository) {
        this.encoder = encoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
        this.jwtConfig = jwtConfig;
        this.usuarioRepository = usuarioRepository;
    }

    public String generateToken(Authentication authentication) {
        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        long expiry = jwtConfig.getExpirySeconds();
        
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
                
        // Tenta obter o ID do usuário através do CPF no subject
        String cpf = authentication.getName();
        Long userId = null;
        
        try {
            // Busca o usuário pelo CPF
            Usuario usuario = usuarioRepository.findByCpf(cpf).orElse(null);
            if (usuario != null) {
                userId = usuario.getIdUsuario();
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar ID do usuário para o token: " + e.getMessage());
        }
            
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("inkspiration")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope);
        
        // Adiciona o ID do usuário se disponível
        if (userId != null) {
            claimsBuilder.claim("userId", userId);
        }
        
        return encoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    public Long getUserIdFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("userId");
    }

    public boolean isTokenRevogado(String token) {
        return tokenRevogadoRepository.existsByToken(token);
    }
}