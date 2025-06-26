package inkspiration.backend.exception.twofactor;

public class TwoFactorStatusException extends RuntimeException {
    
    public TwoFactorStatusException(String message) {
        super(message);
    }
    
    public TwoFactorStatusException(String message, Throwable cause) {
        super(message, cause);
    }
} 