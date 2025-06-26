package inkspiration.backend.exception.endereco;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CidadeInvalidaException extends EnderecoValidacaoException {
    
    public CidadeInvalidaException(String message) {
        super(message);
    }
    
    public CidadeInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}