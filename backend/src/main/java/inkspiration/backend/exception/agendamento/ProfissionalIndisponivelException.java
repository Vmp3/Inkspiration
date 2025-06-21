package inkspiration.backend.exception.agendamento;

public class ProfissionalIndisponivelException extends RuntimeException {
    
    public ProfissionalIndisponivelException(String message) {
        super(message);
    }
    
    public ProfissionalIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
} 