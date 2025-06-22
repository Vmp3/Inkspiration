package inkspiration.backend.exception.emailverification;

public class EmailVerificationReenvioException extends RuntimeException {
    
    public EmailVerificationReenvioException(String message) {
        super(message);
    }
    
    public EmailVerificationReenvioException(String message, Throwable cause) {
        super(message, cause);
    }
} 