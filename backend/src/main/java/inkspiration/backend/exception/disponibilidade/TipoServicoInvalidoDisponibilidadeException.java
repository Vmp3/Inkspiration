package inkspiration.backend.exception.disponibilidade;

public class TipoServicoInvalidoDisponibilidadeException extends RuntimeException {
    
    public TipoServicoInvalidoDisponibilidadeException(String message) {
        super(message);
    }
    
    public TipoServicoInvalidoDisponibilidadeException(String message, Throwable cause) {
        super(message, cause);
    }
} 