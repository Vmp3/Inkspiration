package inkspiration.backend.exception.endereco;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EnderecoValidacaoException extends RuntimeException {
    
    public EnderecoValidacaoException(String message) {
        super(message);
    }
    
    public EnderecoValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
} 