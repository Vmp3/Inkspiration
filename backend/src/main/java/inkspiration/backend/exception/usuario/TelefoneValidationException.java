package inkspiration.backend.exception.usuario;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TelefoneValidationException extends RuntimeException {
    
    public TelefoneValidationException() {
        super("Telefone inválido. Use o formato (99) 99999-9999.");
    }
    
    public TelefoneValidationException(String message) {
        super(message);
    }
    
    public TelefoneValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 