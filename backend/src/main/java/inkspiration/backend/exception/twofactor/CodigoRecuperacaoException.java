package inkspiration.backend.exception.twofactor;

public class CodigoRecuperacaoException extends RuntimeException {
    
    public CodigoRecuperacaoException(String message) {
        super(message);
    }
    
    public CodigoRecuperacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 