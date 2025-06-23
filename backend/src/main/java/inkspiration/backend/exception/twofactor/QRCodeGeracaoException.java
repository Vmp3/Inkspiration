package inkspiration.backend.exception.twofactor;

public class QRCodeGeracaoException extends RuntimeException {
    
    public QRCodeGeracaoException(String message) {
        super(message);
    }
    
    public QRCodeGeracaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 