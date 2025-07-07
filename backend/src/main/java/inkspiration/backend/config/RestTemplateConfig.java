package inkspiration.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import java.time.Duration;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .requestFactory(() -> {
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                    @Override
                    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                        super.prepareConnection(connection, httpMethod);
                        connection.setInstanceFollowRedirects(true);
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                        connection.setRequestProperty("Accept", "application/json, text/plain, */*");
                        connection.setRequestProperty("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8");
                        connection.setRequestProperty("Connection", "keep-alive");
                    }
                };
                factory.setConnectTimeout(10000);
                factory.setReadTimeout(10000);
                return factory;
            })
            .build();
    }
} 