package inkspiration.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	public static void main(String[] args) {
		// Configurações SSL para melhor compatibilidade
		System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
		System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
		System.setProperty("sun.security.ssl.allowLegacyHelloMessages", "true");
		
		SpringApplication.run(BackendApplication.class, args);
	}
}
