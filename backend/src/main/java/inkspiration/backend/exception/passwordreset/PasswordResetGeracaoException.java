package inkspiration.backend.exception.passwordreset;

public class PasswordResetGeracaoException extends RuntimeException {
    
    public PasswordResetGeracaoException(String message) {
        super(message);
    }
    
    public PasswordResetGeracaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 