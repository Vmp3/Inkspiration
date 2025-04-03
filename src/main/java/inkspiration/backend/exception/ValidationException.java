package inkspiration.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class EmailObrigatorioException extends ValidationException {
        public EmailObrigatorioException() {
            super("Email é obrigatório");
        }
    }

    public static class SenhaObrigatoriaException extends ValidationException {
        public SenhaObrigatoriaException() {
            super("Senha é obrigatória");
        }
    }

    public static class PerfilObrigatorioException extends ValidationException {
        public PerfilObrigatorioException() {
            super("Perfil é obrigatório");
        }
    }
}