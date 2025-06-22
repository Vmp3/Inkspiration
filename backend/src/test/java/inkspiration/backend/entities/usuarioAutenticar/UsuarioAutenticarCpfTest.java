package inkspiration.backend.entities.usuarioAutenticar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.UsuarioAutenticar;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UsuarioAutenticarCpfTest {
    private Validator validator;
    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuarioAutenticar = new UsuarioAutenticar();
    }

    @Test
    void cpfNaoPodeSerNulo() {
        usuarioAutenticar.setCpf(null);
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O CPF é obrigatório")));
    }

    @Test
    void cpfNaoPodeSerVazio() {
        usuarioAutenticar.setCpf("");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O CPF é obrigatório")));
    }

    @Test
    void cpfDeveTermOnzeDigitos() {
        usuarioAutenticar.setCpf("12345");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("CPF deve ter 11 dígitos")));
    }

    @Test
    void cpfComMaisDeOnzeDigitosEInvalido() {
        usuarioAutenticar.setCpf("123456789012");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("CPF deve ter 11 dígitos")));
    }

    @Test
    void cpfComLetrasEInvalido() {
        usuarioAutenticar.setCpf("1234567890a");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("CPF deve ter 11 dígitos")));
    }

    @Test
    void cpfValidoDeveSerAceito() {
        usuarioAutenticar.setCpf("12345678901");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    void cpfComMascaraDeveSerLimpo() {
        usuarioAutenticar.setCpf("123.456.789-01");
        assertEquals("12345678901", usuarioAutenticar.getCpf());
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }
} 