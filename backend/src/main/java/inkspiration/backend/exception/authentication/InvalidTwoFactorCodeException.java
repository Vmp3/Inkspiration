package inkspiration.backend.exception.authentication;

public class InvalidTwoFactorCodeException extends RuntimeException {
    
    private boolean requiresTwoFactor = true;
    
    public InvalidTwoFactorCodeException(String message) {
        super(message);
    }
    
    public InvalidTwoFactorCodeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public boolean isRequiresTwoFactor() {
        return requiresTwoFactor;
    }
    
    public void setRequiresTwoFactor(boolean requiresTwoFactor) {
        this.requiresTwoFactor = requiresTwoFactor;
    }
} 