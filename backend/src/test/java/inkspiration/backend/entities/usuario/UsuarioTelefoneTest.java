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

public class UsuarioTelefoneTest {
    private Validator validator;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuario = new Usuario();
    }

    @Test
    void telefoneNaoPodeSerNulo() {
        usuario.setTelefone(null);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O telefone é obrigatório")));
    }

    @Test
    void telefoneNaoPodeSerVazio() {
        usuario.setTelefone("");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O telefone é obrigatório")));
    }

    @Test
    void telefoneDeveSerValido() {
        usuario.setTelefone("123456789"); // Formato inválido
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Telefone inválido. Use o formato (99) 99999-9999")));
    }

    @Test
    void telefoneValidoDeveSerAceito() {
        usuario.setTelefone("(11) 99999-9999");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    void telefoneSemMascaraDeveSerAceito() {
        usuario.setTelefone("11999999999");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    void telefoneComEspacosDeveSerAceito() {
        usuario.setTelefone("11 99999 9999");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }

    @Test
    void telefoneFixoDeveSerAceito() {
        usuario.setTelefone("(11) 3333-3333");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("telefone")));
    }
} 