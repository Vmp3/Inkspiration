package inkspiration.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UsuarioException {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UsuarioNaoEncontradoException extends RuntimeException {
        public UsuarioNaoEncontradoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailJaExisteException extends RuntimeException {
        public EmailJaExisteException(String message) {
            super(message);
        }
    }
    
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class CpfJaExisteException extends RuntimeException {
        public CpfJaExisteException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class PermissaoNegadaException extends RuntimeException {
        public PermissaoNegadaException(String message) {
            super(message);
        }
    }
    
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UsuarioInativoException extends RuntimeException {
        public UsuarioInativoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class AutenticacaoFalhouException extends RuntimeException {
        public AutenticacaoFalhouException(String message) {
            super(message);
        }
    }

    public static class SenhaInvalidaException extends RuntimeException {
        public SenhaInvalidaException(String message) {
            super(message);
        }
    }
} 