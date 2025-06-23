package inkspiration.backend.exception.portfolio;

public class PortfolioAtualizacaoException extends RuntimeException {
    
    public PortfolioAtualizacaoException(String message) {
        super(message);
    }
    
    public PortfolioAtualizacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 