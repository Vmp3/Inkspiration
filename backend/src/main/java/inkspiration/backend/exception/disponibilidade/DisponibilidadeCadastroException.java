package inkspiration.backend.exception.disponibilidade;

public class DisponibilidadeCadastroException extends RuntimeException {
    
    public DisponibilidadeCadastroException(String message) {
        super(message);
    }
    
    public DisponibilidadeCadastroException(String message, Throwable cause) {
        super(message, cause);
    }
} 