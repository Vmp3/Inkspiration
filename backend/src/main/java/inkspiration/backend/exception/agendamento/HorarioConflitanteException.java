package inkspiration.backend.exception.agendamento;

public class HorarioConflitanteException extends RuntimeException {
    
    public HorarioConflitanteException(String message) {
        super(message);
    }
    
    public HorarioConflitanteException(String message, Throwable cause) {
        super(message, cause);
    }
} 