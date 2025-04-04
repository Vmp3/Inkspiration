package inkspiration.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UsuarioValidationException {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class NomeObrigatorioException extends RuntimeException {
        public NomeObrigatorioException() {
            super("Nome é obrigatório");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class EmailObrigatorioException extends RuntimeException {
        public EmailObrigatorioException() {
            super("Email é obrigatório");
        }
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CpfObrigatorioException extends RuntimeException {
        public CpfObrigatorioException() {
            super("CPF é obrigatório");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DataNascimentoObrigatoriaException extends RuntimeException {
        public DataNascimentoObrigatoriaException() {
            super("Data de nascimento é obrigatória");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class SenhaObrigatoriaException extends RuntimeException {
        public SenhaObrigatoriaException() {
            super("Senha é obrigatória");
        }
    }
} 