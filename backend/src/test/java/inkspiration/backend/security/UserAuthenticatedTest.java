package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.enums.UserRole;

@DisplayName("UserAuthenticated - Testes Unitários")
class UserAuthenticatedTest {

    private UsuarioAutenticar usuario;
    private UserAuthenticated userAuthenticated;

    @BeforeEach
    void setUp() {
        usuario = new UsuarioAutenticar();
        usuario.setCpf("12345678900");
        usuario.setSenha("senha123");
        usuario.setRole(UserRole.ROLE_USER.getRole());
        
        userAuthenticated = new UserAuthenticated(usuario);
    }

    @Test
    @DisplayName("Deve retornar authorities corretamente")
    void deveRetornarAuthoritiesCorretamente() {
        var authorities = userAuthenticated.getAuthorities();
        
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        
        GrantedAuthority authority = authorities.iterator().next();
        assertEquals(UserRole.ROLE_USER.getRole(), authority.getAuthority());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando role é nula")
    void deveLancarIllegalArgumentExceptionQuandoRoleNula() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole(null);
        });
        assertEquals("Role não pode ser nula", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando role é inválida")
    void deveLancarIllegalArgumentExceptionQuandoRoleInvalida() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuario.setRole("INVALID_ROLE");
        });
        assertEquals("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar senha corretamente")
    void deveRetornarSenhaCorretamente() {
        assertEquals("senha123", userAuthenticated.getPassword());
    }

    @Test
    @DisplayName("Deve retornar CPF como username")
    void deveRetornarCPFComoUsername() {
        assertEquals("12345678900", userAuthenticated.getUsername());
    }

    @Test
    @DisplayName("Deve retornar true para conta não expirada")
    void deveRetornarTrueParaContaNaoExpirada() {
        assertTrue(userAuthenticated.isAccountNonExpired());
    }

    @Test
    @DisplayName("Deve retornar true para conta não bloqueada")
    void deveRetornarTrueParaContaNaoBloqueada() {
        assertTrue(userAuthenticated.isAccountNonLocked());
    }

    @Test
    @DisplayName("Deve retornar true para credenciais não expiradas")
    void deveRetornarTrueParaCredenciaisNaoExpiradas() {
        assertTrue(userAuthenticated.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Deve retornar true para usuário ativo")
    void deveRetornarTrueParaUsuarioAtivo() {
        assertTrue(userAuthenticated.isEnabled());
    }

    @Test
    @DisplayName("Deve retornar false para usuário deletado")
    void deveRetornarFalseParaUsuarioDeletado() {
        usuario.setRole(UserRole.ROLE_DELETED.getRole());
        userAuthenticated = new UserAuthenticated(usuario);
        
        assertFalse(userAuthenticated.isEnabled());
    }
} 