package inkspiration.backend.exception.portfolio;

public class PortfolioAcessoNegadoException extends RuntimeException {
    
    public PortfolioAcessoNegadoException(String message) {
        super(message);
    }
    
    public PortfolioAcessoNegadoException(String message, Throwable cause) {
        super(message, cause);
    }
} 