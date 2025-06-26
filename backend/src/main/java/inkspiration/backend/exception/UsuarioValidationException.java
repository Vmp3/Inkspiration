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
    public static class EmailInvalidoException extends RuntimeException {
        public EmailInvalidoException(String message) {
            super(message);
        }
    }
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CpfObrigatorioException extends RuntimeException {
        public CpfObrigatorioException() {
            super("CPF é obrigatório");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CpfInvalidoException extends RuntimeException {
        public CpfInvalidoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DataNascimentoObrigatoriaException extends RuntimeException {
        public DataNascimentoObrigatoriaException() {
            super("Data de nascimento é obrigatória");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class DataInvalidaException extends RuntimeException {
        public DataInvalidaException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class SenhaObrigatoriaException extends RuntimeException {
        public SenhaObrigatoriaException() {
            super("Senha é obrigatória");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class SenhaInvalidaException extends RuntimeException {
        public SenhaInvalidaException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class IdadeMinimaException extends RuntimeException {
        public IdadeMinimaException(int idadeMinima) {
            super("Você deve ter pelo menos " + idadeMinima + " anos para se cadastrar");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class EnderecoObrigatorioException extends RuntimeException {
        public EnderecoObrigatorioException() {
            super("Endereço é obrigatório");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TelefoneObrigatorioException extends RuntimeException {
        public TelefoneObrigatorioException() {
            super("Telefone é obrigatório");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TelefoneInvalidoException extends RuntimeException {
        public TelefoneInvalidoException() {
            super("Telefone inválido. Use o formato (99) 99999-9999.");
        }
        
        public TelefoneInvalidoException(String message) {
            super(message);
        }
    }
} 