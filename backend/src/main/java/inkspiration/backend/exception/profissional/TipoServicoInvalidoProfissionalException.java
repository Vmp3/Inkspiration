package inkspiration.backend.exception.profissional;

public class TipoServicoInvalidoProfissionalException extends RuntimeException {
    
    public TipoServicoInvalidoProfissionalException(String message) {
        super(message);
    }
    
    public TipoServicoInvalidoProfissionalException(String message, Throwable cause) {
        super(message, cause);
    }
} 