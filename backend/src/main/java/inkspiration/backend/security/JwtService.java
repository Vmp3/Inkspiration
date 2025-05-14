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
import inkspiration.backend.repository.TokenRevogadoRepository;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder jwtDecoder;
    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final JwtConfig jwtConfig;
    
    @Value("${api.security.token.expiration:720}")
    private int expiration;

    @Autowired
    public JwtService(JwtEncoder encoder, JwtDecoder jwtDecoder, TokenRevogadoRepository tokenRevogadoRepository, JwtConfig jwtConfig) {
        this.encoder = encoder;
        this.jwtDecoder = jwtDecoder;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Authentication authentication) {
        Instant now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
        long expiry = jwtConfig.getExpirySeconds();
        
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
                
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("inkspiration")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public Long getUserIdFromToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaim("userId");
    }

    public boolean isTokenRevogado(String token) {
        return tokenRevogadoRepository.existsByToken(token);
    }
}