package inkspiration.backend.exception.twofactor;

public class TwoFactorAtivacaoException extends RuntimeException {
    
    public TwoFactorAtivacaoException(String message) {
        super(message);
    }
    
    public TwoFactorAtivacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 