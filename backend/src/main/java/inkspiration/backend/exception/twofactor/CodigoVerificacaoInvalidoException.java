package inkspiration.backend.exception.twofactor;

public class CodigoVerificacaoInvalidoException extends RuntimeException {
    
    public CodigoVerificacaoInvalidoException(String message) {
        super(message);
    }
    
    public CodigoVerificacaoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
} 