package inkspiration.backend.exception.profissional;

public class EnderecoNaoEncontradoException extends RuntimeException {
    
    public EnderecoNaoEncontradoException(String message) {
        super(message);
    }
    
    public EnderecoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
} 