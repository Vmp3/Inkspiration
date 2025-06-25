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

public class UsuarioCpfTest {
    private Validator validator;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuario = new Usuario();
    }

    @Test
    void cpfNaoPodeSerNulo() {
        usuario.setCpf(null);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O CPF é obrigatório")));
    }

    @Test
    void cpfNaoPodeSerVazio() {
        usuario.setCpf("");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O CPF é obrigatório")));
    }

    @Test
    void cpfDeveSerValido() {
        usuario.setCpf("12345678901"); // CPF inválido
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("CPF inválido")));
    }

    @Test
    void cpfValidoDeveSerAceito() {
        usuario.setCpf("52998224725"); // CPF válido
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }

    @Test
    void cpfComMascaraDeveSerAceito() {
        usuario.setCpf("529.982.247-25"); // CPF válido com máscara
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
    }
} 