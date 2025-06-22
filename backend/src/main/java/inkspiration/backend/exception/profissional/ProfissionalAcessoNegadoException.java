package inkspiration.backend.exception.profissional;

public class ProfissionalAcessoNegadoException extends RuntimeException {
    
    public ProfissionalAcessoNegadoException(String message) {
        super(message);
    }
    
    public ProfissionalAcessoNegadoException(String message, Throwable cause) {
        super(message, cause);
    }
} 