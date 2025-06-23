package inkspiration.backend.exception.disponibilidade;

public class DisponibilidadeAcessoException extends RuntimeException {
    
    public DisponibilidadeAcessoException(String message) {
        super(message);
    }
    
    public DisponibilidadeAcessoException(String message, Throwable cause) {
        super(message, cause);
    }
} 