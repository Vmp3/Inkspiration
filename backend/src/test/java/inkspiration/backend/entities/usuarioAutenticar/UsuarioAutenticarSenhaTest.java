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

public class UsuarioAutenticarSenhaTest {
    private Validator validator;
    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuarioAutenticar = new UsuarioAutenticar();
    }

    @Test
    void senhaNaoPodeSerNula() {
        usuarioAutenticar.setSenha(null);
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("A senha é obrigatória")));
    }

    @Test
    void senhaNaoPodeSerVazia() {
        usuarioAutenticar.setSenha("");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("A senha é obrigatória")));
    }

    @Test
    void senhaValidaDeveSerAceita() {
        usuarioAutenticar.setSenha("senhaHasheada123");
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    void senhaDeveSerArmazenadaComoRecebida() {
        String senhaHasheada = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        usuarioAutenticar.setSenha(senhaHasheada);
        assertEquals(senhaHasheada, usuarioAutenticar.getSenha());
    }

    @Test
    void senhaComEspacosDeveSerAceita() {
        String senhaComEspacos = "senha com espacos";
        usuarioAutenticar.setSenha(senhaComEspacos);
        assertEquals(senhaComEspacos, usuarioAutenticar.getSenha());
        
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }

    @Test
    void senhaComCaracteresEspeciaisDeveSerAceita() {
        String senhaComCaracteresEspeciais = "senha!@#$%^&*()";
        usuarioAutenticar.setSenha(senhaComCaracteresEspeciais);
        assertEquals(senhaComCaracteresEspeciais, usuarioAutenticar.getSenha());
        
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("senha")));
    }
} 