package inkspiration.backend.exception.agendamento;

public class AgendamentoNaoAutorizadoException extends RuntimeException {
    
    public AgendamentoNaoAutorizadoException(String message) {
        super(message);
    }
    
    public AgendamentoNaoAutorizadoException(String message, Throwable cause) {
        super(message, cause);
    }
} 