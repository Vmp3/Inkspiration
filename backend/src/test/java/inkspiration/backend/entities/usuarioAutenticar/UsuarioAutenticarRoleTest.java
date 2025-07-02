package inkspiration.backend.entities.usuarioAutenticar;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.enums.UserRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class UsuarioAutenticarRoleTest {
    private Validator validator;
    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usuarioAutenticar = new UsuarioAutenticar();
    }

    @Test
    void roleNaoPodeSerNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioAutenticar.setRole(null);
        });
        assertEquals("Role não pode ser nula", exception.getMessage());
    }

    @Test
    void roleVaziaEInvalida() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioAutenticar.setRole("");
        });
        assertEquals("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED", exception.getMessage());
    }

    @Test
    void roleComRoleValidaDeveSerAceita() {
        usuarioAutenticar.setRole("ROLE_USER");
        assertEquals("ROLE_USER", usuarioAutenticar.getRole());
        
        Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
        assertFalse(violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    void roleSemPrefixoDeveSerNormalizada() {
        usuarioAutenticar.setRole("USER");
        assertEquals("ROLE_USER", usuarioAutenticar.getRole());
    }

    @Test
    void roleMinusculaDeveSerNormalizada() {
        usuarioAutenticar.setRole("user");
        assertEquals("ROLE_USER", usuarioAutenticar.getRole());
    }

    @Test
    void roleComEspacosDeveSerNormalizada() {
        usuarioAutenticar.setRole(" USER ");
        assertEquals("ROLE_USER", usuarioAutenticar.getRole());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "TEST", "ROLE_INVALID", "OTHER"})
    void roleInvalidaDeveLancarExcecao(String invalidRole) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioAutenticar.setRole(invalidRole);
        });
        assertEquals("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED", exception.getMessage());
    }

    @Test
    void todasAsRolesValidasDevemSerAceitas() {
        for (UserRole role : UserRole.values()) {
            usuarioAutenticar.setRole(role.name());
            assertEquals(role.name(), usuarioAutenticar.getRole());
            
            Set<ConstraintViolation<UsuarioAutenticar>> violations = validator.validate(usuarioAutenticar);
            assertFalse(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("role")));
        }
    }

    @Test
    void roleAdminDeveSerAceita() {
        usuarioAutenticar.setRole("ADMIN");
        assertEquals("ROLE_ADMIN", usuarioAutenticar.getRole());
    }

    @Test
    void roleProfDeveSerAceita() {
        usuarioAutenticar.setRole("PROF");
        assertEquals("ROLE_PROF", usuarioAutenticar.getRole());
    }

    @Test
    void roleDeletedDeveSerAceita() {
        usuarioAutenticar.setRole("DELETED");
        assertEquals("ROLE_DELETED", usuarioAutenticar.getRole());
    }
} 