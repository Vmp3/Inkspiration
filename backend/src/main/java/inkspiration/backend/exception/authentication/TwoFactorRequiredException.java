package inkspiration.backend.exception.authentication;

public class TwoFactorRequiredException extends RuntimeException {
    
    private boolean requiresTwoFactor = true;
    
    public TwoFactorRequiredException(String message) {
        super(message);
    }
    
    public TwoFactorRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public boolean isRequiresTwoFactor() {
        return requiresTwoFactor;
    }
    
    public void setRequiresTwoFactor(boolean requiresTwoFactor) {
        this.requiresTwoFactor = requiresTwoFactor;
    }
} 