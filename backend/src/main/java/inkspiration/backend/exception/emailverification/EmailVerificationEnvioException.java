package inkspiration.backend.exception.emailverification;

public class EmailVerificationEnvioException extends RuntimeException {
    
    public EmailVerificationEnvioException(String message) {
        super(message);
    }
    
    public EmailVerificationEnvioException(String message, Throwable cause) {
        super(message, cause);
    }
} 