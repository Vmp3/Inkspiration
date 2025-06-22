package inkspiration.backend.exception.passwordreset;

public class PasswordResetValidacaoException extends RuntimeException {
    
    public PasswordResetValidacaoException(String message) {
        super(message);
    }
    
    public PasswordResetValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 