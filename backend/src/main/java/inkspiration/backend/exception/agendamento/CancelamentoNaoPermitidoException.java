package inkspiration.backend.exception.agendamento;

public class CancelamentoNaoPermitidoException extends RuntimeException {
    
    public CancelamentoNaoPermitidoException(String message) {
        super(message);
    }
    
    public CancelamentoNaoPermitidoException(String message, Throwable cause) {
        super(message, cause);
    }
} 