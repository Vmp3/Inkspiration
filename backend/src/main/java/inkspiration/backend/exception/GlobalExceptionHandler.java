package inkspiration.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        UsuarioException.UsuarioNaoEncontradoException.class,
        UsuarioException.EmailJaExisteException.class,
        UsuarioException.CpfJaExisteException.class,
        UsuarioException.PermissaoNegadaException.class,
        UsuarioException.UsuarioInativoException.class,
        UsuarioException.AutenticacaoFalhouException.class
    })
    public ResponseEntity<Map<String, String>> handleUsuarioExceptions(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof UsuarioException.UsuarioNaoEncontradoException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof UsuarioException.EmailJaExisteException || 
                  ex instanceof UsuarioException.CpfJaExisteException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof UsuarioException.PermissaoNegadaException ||
                  ex instanceof UsuarioException.UsuarioInativoException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UsuarioException.AutenticacaoFalhouException) {
            status = HttpStatus.UNAUTHORIZED;
        }
        
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler({
        UsuarioValidationException.NomeObrigatorioException.class,
        UsuarioValidationException.EmailObrigatorioException.class,
        UsuarioValidationException.EmailInvalidoException.class,
        UsuarioValidationException.CpfObrigatorioException.class,
        UsuarioValidationException.CpfInvalidoException.class,
        UsuarioValidationException.DataNascimentoObrigatoriaException.class,
        UsuarioValidationException.DataInvalidaException.class,
        UsuarioValidationException.SenhaObrigatoriaException.class
    })
    public ResponseEntity<Map<String, String>> handleUsuarioValidationExceptions(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Erro de validação");
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Acesso negado: você não tem permissão para realizar esta operação");
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        
        if (ex instanceof BadCredentialsException) {
            errors.put("error", "Credenciais inválidas");
        } else if (ex.getMessage().contains("User is disabled")) {
            errors.put("error", "Usuário está inativo ou foi excluído do sistema");
        } else {
            errors.put("error", "Erro de autenticação: " + ex.getMessage());
        }
        
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Erro interno do servidor: " + ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DisponibilidadeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleHorarioInvalidoException(DisponibilidadeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Erro de validação de horário");
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}