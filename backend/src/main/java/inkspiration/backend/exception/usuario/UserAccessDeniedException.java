package inkspiration.backend.exception.usuario;

public class UserAccessDeniedException extends RuntimeException {
    
    public UserAccessDeniedException(String message) {
        super(message);
    }
    
    public UserAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
} 