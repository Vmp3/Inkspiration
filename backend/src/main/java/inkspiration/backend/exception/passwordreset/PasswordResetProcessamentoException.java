package inkspiration.backend.exception.passwordreset;

public class PasswordResetProcessamentoException extends RuntimeException {
    
    public PasswordResetProcessamentoException(String message) {
        super(message);
    }
    
    public PasswordResetProcessamentoException(String message, Throwable cause) {
        super(message, cause);
    }
} 