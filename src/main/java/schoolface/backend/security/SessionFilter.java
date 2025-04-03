package inkspiration.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class SessionFilter {

    @Autowired
    private JwtDecoder jwtDecoder;

    public boolean isValidSession(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String userId = jwt.getSubject(); 
            return userId != null;
        } catch (Exception e) {
            return false;
        }
    }
} 