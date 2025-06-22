package inkspiration.backend.exception.endereco;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EstadoInvalidoException extends EnderecoValidacaoException {
    
    public EstadoInvalidoException(String message) {
        super(message);
    }
    
    public EstadoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}