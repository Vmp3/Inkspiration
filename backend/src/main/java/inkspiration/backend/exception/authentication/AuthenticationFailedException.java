package inkspiration.backend.exception.authentication;

public class AuthenticationFailedException extends RuntimeException {
    
    private String errorCode;
    
    public AuthenticationFailedException(String message) {
        super(message);
    }
    
    public AuthenticationFailedException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public AuthenticationFailedException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
} 