package inkspiration.backend.exception.emailverification;

public class EmailVerificationValidacaoException extends RuntimeException {
    
    public EmailVerificationValidacaoException(String message) {
        super(message);
    }
    
    public EmailVerificationValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 