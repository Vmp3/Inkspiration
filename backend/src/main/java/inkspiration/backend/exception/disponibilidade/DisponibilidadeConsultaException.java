package inkspiration.backend.exception.disponibilidade;

public class DisponibilidadeConsultaException extends RuntimeException {
    
    public DisponibilidadeConsultaException(String message) {
        super(message);
    }
    
    public DisponibilidadeConsultaException(String message, Throwable cause) {
        super(message, cause);
    }
} 