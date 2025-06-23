package inkspiration.backend.exception.usuario;

public class TokenValidationException extends RuntimeException {
    
    private String newToken;
    
    public TokenValidationException(String message) {
        super(message);
    }
    
    public TokenValidationException(String message, String newToken) {
        super(message);
        this.newToken = newToken;
    }
    
    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getNewToken() {
        return newToken;
    }
    
    public void setNewToken(String newToken) {
        this.newToken = newToken;
    }
} 