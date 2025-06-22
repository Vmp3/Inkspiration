package inkspiration.backend.exception.agendamento;

public class TipoServicoInvalidoException extends RuntimeException {
    
    public TipoServicoInvalidoException(String message) {
        super(message);
    }
    
    public TipoServicoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
} 