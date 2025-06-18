package inkspiration.backend.exception;

public class DisponibilidadeException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public DisponibilidadeException(String message) {
        super(message);
    }
    
    public DisponibilidadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class HorarioInvalidoException extends DisponibilidadeException {
        private static final long serialVersionUID = 1L;
        
        public HorarioInvalidoException(String message) {
            super(message);
        }
        
        public HorarioInvalidoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class EspecialidadeVaziaException extends DisponibilidadeException {
        private static final long serialVersionUID = 1L;
        
        public EspecialidadeVaziaException() {
            super("É necessário selecionar pelo menos uma especialidade");
        }
    }

    public static class TipoServicoVazioException extends DisponibilidadeException {
        private static final long serialVersionUID = 1L;
        
        public TipoServicoVazioException() {
            super("É necessário selecionar pelo menos um tipo de serviço");
        }
    }
} 