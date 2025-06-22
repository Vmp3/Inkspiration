package inkspiration.backend.exception.portfolio;

public class PortfolioCriacaoException extends RuntimeException {
    
    public PortfolioCriacaoException(String message) {
        super(message);
    }
    
    public PortfolioCriacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 