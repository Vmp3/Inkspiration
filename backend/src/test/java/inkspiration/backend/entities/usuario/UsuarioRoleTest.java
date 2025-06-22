package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import inkspiration.backend.enums.UserRole;

class UsuarioRoleTest {

    @Test
    void testGetDescricao() {
        assertEquals("Administrador", UserRole.ROLE_ADMIN.getDescricao());
        assertEquals("Usuário", UserRole.ROLE_USER.getDescricao());
        assertEquals("Profissional", UserRole.ROLE_PROF.getDescricao());
        assertEquals("Usuário Deletado", UserRole.ROLE_DELETED.getDescricao());
    }

    @Test
    void testGetRole() {
        assertEquals("ROLE_ADMIN", UserRole.ROLE_ADMIN.getRole());
        assertEquals("ROLE_USER", UserRole.ROLE_USER.getRole());
        assertEquals("ROLE_PROF", UserRole.ROLE_PROF.getRole());
        assertEquals("ROLE_DELETED", UserRole.ROLE_DELETED.getRole());
    }

    @Test
    void testFromStringComPrefixoRole() {
        assertEquals(UserRole.ROLE_ADMIN, UserRole.fromString("ROLE_ADMIN"));
        assertEquals(UserRole.ROLE_USER, UserRole.fromString("ROLE_USER"));
        assertEquals(UserRole.ROLE_PROF, UserRole.fromString("ROLE_PROF"));
        assertEquals(UserRole.ROLE_DELETED, UserRole.fromString("ROLE_DELETED"));
    }

    @Test
    void testFromStringSemPrefixoRole() {
        assertEquals(UserRole.ROLE_ADMIN, UserRole.fromString("ADMIN"));
        assertEquals(UserRole.ROLE_USER, UserRole.fromString("USER"));
        assertEquals(UserRole.ROLE_PROF, UserRole.fromString("PROF"));
        assertEquals(UserRole.ROLE_DELETED, UserRole.fromString("DELETED"));
    }

    @Test
    void testFromStringComCaseInsensitive() {
        assertEquals(UserRole.ROLE_ADMIN, UserRole.fromString("admin"));
        assertEquals(UserRole.ROLE_USER, UserRole.fromString("user"));
        assertEquals(UserRole.ROLE_PROF, UserRole.fromString("prof"));
        assertEquals(UserRole.ROLE_DELETED, UserRole.fromString("deleted"));
    }

    @Test
    void testFromStringComEspacos() {
        assertEquals(UserRole.ROLE_ADMIN, UserRole.fromString(" ADMIN "));
        assertEquals(UserRole.ROLE_USER, UserRole.fromString(" USER "));
        assertEquals(UserRole.ROLE_PROF, UserRole.fromString(" PROF "));
        assertEquals(UserRole.ROLE_DELETED, UserRole.fromString(" DELETED "));
    }

    @Test
    void testFromStringNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            UserRole.fromString(null);
        });
        assertEquals("Role não pode ser nula", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "TEST", "ROLE_INVALID", "OTHER"})
    void testFromStringInvalido(String invalidRole) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            UserRole.fromString(invalidRole);
        });
        assertEquals("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED", exception.getMessage());
    }
} 