package inkspiration.backend.exception.imagem;

public class ImagemRemocaoException extends RuntimeException {
    
    public ImagemRemocaoException(String message) {
        super(message);
    }
    
    public ImagemRemocaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 