package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.UserRole;

class UsuarioRoleEntityTest {
    
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
    }

    @Test
    void testSetRoleComRoleValida() {
        usuario.setRole("ROLE_USER");
        assertEquals("ROLE_USER", usuario.getRole());
    }

    @Test
    void testSetRoleSemPrefixo() {
        usuario.setRole("USER");
        assertEquals("ROLE_USER", usuario.getRole());
    }

    @Test
    void testSetRoleMinuscula() {
        usuario.setRole("user");
        assertEquals("ROLE_USER", usuario.getRole());
    }

    @Test
    void testSetRoleComEspacos() {
        usuario.setRole(" USER ");
        assertEquals("ROLE_USER", usuario.getRole());
    }

    @Test
    void testSetRoleNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole(null);
        });
        assertEquals("Role não pode ser nula", exception.getMessage());
    }

    @Test
    void testSetRoleVazia() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole("");
        });
        assertTrue(exception.getMessage().contains("Role inválida"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "TEST", "ROLE_INVALID", "OTHER"})
    void testSetRoleInvalida(String invalidRole) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole(invalidRole);
        });
        assertEquals("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED", exception.getMessage());
    }

    @Test
    void testSetRoleTodasAsRolesValidas() {
        // Testa todas as roles válidas do enum
        for (UserRole role : UserRole.values()) {
            usuario.setRole(role.name());
            assertEquals(role.name(), usuario.getRole());
        }
    }
} 