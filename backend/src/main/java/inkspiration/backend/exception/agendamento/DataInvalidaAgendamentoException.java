package inkspiration.backend.exception.agendamento;

public class DataInvalidaAgendamentoException extends RuntimeException {
    
    public DataInvalidaAgendamentoException(String message) {
        super(message);
    }
    
    public DataInvalidaAgendamentoException(String message, Throwable cause) {
        super(message, cause);
    }
} 