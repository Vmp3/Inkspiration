package inkspiration.backend.exception.endereco;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CepInvalidoException extends EnderecoValidacaoException {
    
    public CepInvalidoException(String message) {
        super(message);
    }
    
    public CepInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
} 