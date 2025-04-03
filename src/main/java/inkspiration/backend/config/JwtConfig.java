package inkspiration.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private long expirySeconds = 36000L; // 10 horas

    public long getExpirySeconds() {
        return expirySeconds;
    }

    public void setExpirySeconds(long expirySeconds) {
        this.expirySeconds = expirySeconds;
    }
} 