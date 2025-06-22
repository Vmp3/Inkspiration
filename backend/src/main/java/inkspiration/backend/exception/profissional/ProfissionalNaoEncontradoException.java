package inkspiration.backend.exception.profissional;

public class ProfissionalNaoEncontradoException extends RuntimeException {
    
    public ProfissionalNaoEncontradoException(String message) {
        super(message);
    }
    
    public ProfissionalNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
} 