package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Usuario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UsuarioDataNascimentoTest {
    private Validator validator;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuario = new Usuario();
    }

    @Test
    void dataNascimentoNaoPodeSerNula() {
        usuario.setDataNascimento(null);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("A data de nascimento é obrigatória")));
    }

    @Test
    void dataNascimentoNaoPodeSerFutura() {
        usuario.setDataNascimento(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("A data de nascimento deve ser no passado")));
    }

    @Test
    void dataNascimentoValidaDeveSerAceita() {
        usuario.setDataNascimento(LocalDate.now().minusYears(20));
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("dataNascimento")));
    }

    @Test
    void dataNascimentoHojeNaoDeveSerAceita() {
        usuario.setDataNascimento(LocalDate.now());
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("A data de nascimento deve ser no passado")));
    }

    @Test
    void dataNascimentoPassadaDeveSerAceita() {
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("dataNascimento")));
    }
} 