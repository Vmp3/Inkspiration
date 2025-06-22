package inkspiration.backend.exception.agendamento;

public class AutoAgendamentoException extends RuntimeException {
    
    public AutoAgendamentoException(String message) {
        super(message);
    }
    
    public AutoAgendamentoException(String message, Throwable cause) {
        super(message, cause);
    }
} 