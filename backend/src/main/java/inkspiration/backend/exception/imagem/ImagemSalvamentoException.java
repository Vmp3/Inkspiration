package inkspiration.backend.exception.imagem;

public class ImagemSalvamentoException extends RuntimeException {
    
    public ImagemSalvamentoException(String message) {
        super(message);
    }
    
    public ImagemSalvamentoException(String message, Throwable cause) {
        super(message, cause);
    }
} 