package inkspiration.backend.exception.imagem;

public class ImagemNaoEncontradaException extends RuntimeException {
    
    public ImagemNaoEncontradaException(String message) {
        super(message);
    }
    
    public ImagemNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
} 