package inkspiration.backend.exception.portfolio;

public class PortfolioRemocaoException extends RuntimeException {
    
    public PortfolioRemocaoException(String message) {
        super(message);
    }
    
    public PortfolioRemocaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 