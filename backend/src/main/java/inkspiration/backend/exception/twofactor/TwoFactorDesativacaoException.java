package inkspiration.backend.exception.twofactor;

public class TwoFactorDesativacaoException extends RuntimeException {
    
    public TwoFactorDesativacaoException(String message) {
        super(message);
    }
    
    public TwoFactorDesativacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 