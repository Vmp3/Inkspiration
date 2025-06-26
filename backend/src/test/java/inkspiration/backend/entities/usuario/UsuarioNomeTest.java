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

public class UsuarioNomeTest {
    private Validator validator;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuario = new Usuario();
    }

    @Test
    void nomeNaoPodeSerNulo() {
        usuario.setNome(null);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O nome é obrigatório")));
    }

    @Test
    void nomeNaoPodeSerVazio() {
        usuario.setNome("");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O nome é obrigatório")));
    }

    @Test
    void nomeDeveTerNoMinimo3Caracteres() {
        usuario.setNome("Jo");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O nome deve ter entre 3 e 100 caracteres")));
    }

    @Test
    void nomeNaoPodeTerMaisDe100Caracteres() {
        String nomeGrande = "a".repeat(101);
        usuario.setNome(nomeGrande);
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O nome deve ter entre 3 e 100 caracteres")));
    }

    @Test
    void nomeDeveConterApenasLetrasEEspacos() {
        usuario.setNome("João123");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("O nome deve conter apenas letras e espaços")));
    }

    @Test
    void nomeValidoDeveSerAceito() {
        usuario.setNome("João da Silva");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void nomeComAcentosDeveSerAceito() {
        usuario.setNome("José María");
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }
} 