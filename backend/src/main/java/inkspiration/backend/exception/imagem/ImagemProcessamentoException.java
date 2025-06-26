package inkspiration.backend.exception.imagem;

public class ImagemProcessamentoException extends RuntimeException {
    
    public ImagemProcessamentoException(String message) {
        super(message);
    }
    
    public ImagemProcessamentoException(String message, Throwable cause) {
        super(message, cause);
    }
} 