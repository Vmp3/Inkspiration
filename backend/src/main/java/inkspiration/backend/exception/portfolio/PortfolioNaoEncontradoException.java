package inkspiration.backend.exception.portfolio;

public class PortfolioNaoEncontradoException extends RuntimeException {
    
    public PortfolioNaoEncontradoException(String message) {
        super(message);
    }
    
    public PortfolioNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
} 