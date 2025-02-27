package schoolface.backend.exception;

public class UsuarioException extends RuntimeException {
    
    public static class EmailJaExisteException extends UsuarioException {
        public EmailJaExisteException(String message) {
            super(message);
        }
    }

    public static class UsuarioNaoEncontradoException extends UsuarioException {
        public UsuarioNaoEncontradoException(String message) {
            super(message);
        }
    }

    public static class UsuarioInativoException extends UsuarioException {
        public UsuarioInativoException(String message) {
            super(message);
        }
    }

    public static class AutenticacaoFalhouException extends UsuarioException {
        public AutenticacaoFalhouException(String message) {
            super(message);
        }
    }

    protected UsuarioException(String message) {
        super(message);
    }

    public UsuarioException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class PermissaoNegadaException extends RuntimeException {
        public PermissaoNegadaException(String message) {
            super(message);
        }
    }
} 