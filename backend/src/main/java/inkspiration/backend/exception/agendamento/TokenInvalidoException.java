package inkspiration.backend.exception.agendamento;

public class TokenInvalidoException extends RuntimeException {
    
    public TokenInvalidoException(String message) {
        super(message);
    }
    
    public TokenInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
} 