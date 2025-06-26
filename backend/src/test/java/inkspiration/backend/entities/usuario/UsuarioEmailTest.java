package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UsuarioEmailTest {
    private Validator validator;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuario = new Usuario();
    }

    @Test
    void emailNaoPodeSerNulo() {
        usuario.setEmail(null);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O email é obrigatório")));
    }

    @Test
    void emailNaoPodeSerVazio() {
        usuario.setEmail("");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O email é obrigatório")));
    }

    @Test
    void emailDeveSerValido() {
        usuario.setEmail("email_invalido");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Email inválido")));
    }

    @Test
    void emailValidoDeveSerAceito() {
        usuario.setEmail("usuario@exemplo.com");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void emailComSubdominioDeveSerAceito() {
        usuario.setEmail("usuario@sub.exemplo.com");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void emailComCaracteresEspeciaisDeveSerAceito() {
        usuario.setEmail("usuario.nome+tag@exemplo.com");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
} 