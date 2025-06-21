package inkspiration.backend.exception.emailverification;

public class EmailVerificationCriacaoUsuarioException extends RuntimeException {
    
    public EmailVerificationCriacaoUsuarioException(String message) {
        super(message);
    }
    
    public EmailVerificationCriacaoUsuarioException(String message, Throwable cause) {
        super(message, cause);
    }
} 