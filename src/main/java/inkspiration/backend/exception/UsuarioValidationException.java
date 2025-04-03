package inkspiration.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioValidationException extends RuntimeException {
    
    public static class NomeObrigatorioException extends UsuarioValidationException {
        public NomeObrigatorioException() {
            super("Nome é obrigatório");
        }
    }

    public static class EmailObrigatorioException extends UsuarioValidationException {
        public EmailObrigatorioException() {
            super("Email é obrigatório");
        }
    }

    public static class EmailInvalidoException extends UsuarioValidationException {
        public EmailInvalidoException() {
            super("Email inválido");
        }
    }

    public static class DataNascimentoObrigatoriaException extends UsuarioValidationException {
        public DataNascimentoObrigatoriaException() {
            super("Data de nascimento é obrigatória");
        }
    }

    public static class DataNascimentoInvalidaException extends UsuarioValidationException {
        public DataNascimentoInvalidaException() {
            super("Data de nascimento deve ser no passado");
        }
    }

    public static class SenhaObrigatoriaException extends UsuarioValidationException {
        public SenhaObrigatoriaException() {
            super("Senha é obrigatória");
        }
    }

    public UsuarioValidationException(String message) {
        super(message);
    }

    public UsuarioValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 