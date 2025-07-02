package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorizationService - Testes Unitários")
class AuthorizationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Deve identificar usuário admin corretamente")
    void deveIdentificarUsuarioAdminCorretamente() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean isAdmin = authorizationService.isCurrentUserAdmin();

        // Assert
        assertTrue(isAdmin);
    }

    @Test
    @DisplayName("Deve identificar usuário não admin corretamente")
    void deveIdentificarUsuarioNaoAdminCorretamente() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean isAdmin = authorizationService.isCurrentUserAdmin();

        // Assert
        assertFalse(isAdmin);
    }

    @Test
    @DisplayName("Deve retornar ID do usuário atual do token")
    void deveRetornarIdUsuarioAtualDoToken() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        Long userId = authorizationService.getCurrentUserId();

        // Assert
        assertEquals(123L, userId);
    }

    @Test
    @DisplayName("Deve retornar CPF do usuário atual")
    void deveRetornarCpfUsuarioAtual() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("12345678900");
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        String cpf = authorizationService.getCurrentUserCpf();

        // Assert
        assertEquals("12345678900", cpf);
    }

    @Test
    @DisplayName("Deve permitir acesso quando usuário é admin")
    void devePermitirAcessoQuandoUsuarioEAdmin() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean canAccess = authorizationService.canAccessUser(456L);

        // Assert
        assertTrue(canAccess);
    }

    @Test
    @DisplayName("Deve permitir acesso quando usuário acessa próprios dados")
    void devePermitirAcessoQuandoUsuarioAcessaPropriosDados() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean canAccess = authorizationService.canAccessUser(123L);

        // Assert
        assertTrue(canAccess);
    }

    @Test
    @DisplayName("Deve negar acesso quando usuário tenta acessar dados de outro usuário")
    void deveNegarAcessoQuandoUsuarioTentaAcessarDadosDeOutroUsuario() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean canAccess = authorizationService.canAccessUser(456L);

        // Assert
        assertFalse(canAccess);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não admin tenta operação administrativa")
    void deveLancarExcecaoQuandoUsuarioNaoAdminTentaOperacaoAdministrativa() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> authorizationService.requireAdmin());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário tenta acessar dados de outro usuário")
    void deveLancarExcecaoQuandoUsuarioTentaAcessarDadosDeOutroUsuarioRequire() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> authorizationService.requireUserAccessOrAdmin(456L));
    }

    @Test
    @DisplayName("Deve retornar usuário atual corretamente")
    void deveRetornarUsuarioAtualCorretamente() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("12345678900");
        when(securityContext.getAuthentication()).thenReturn(auth);

        Usuario usuario = new Usuario();
        usuario.setCpf("12345678900");
        when(usuarioRepository.findByCpf("12345678900")).thenReturn(Optional.of(usuario));

        // Act
        Usuario currentUser = authorizationService.getCurrentUser();

        // Assert
        assertNotNull(currentUser);
        assertEquals("12345678900", currentUser.getCpf());
    }

    @Test
    @DisplayName("Deve validar propriedade do token corretamente")
    void deveValidarPropriedadeTokenCorretamente() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act
        boolean isValid = authorizationService.validateTokenOwnership(123L);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve verificar permissão de edição de perfil corretamente")
    void deveVerificarPermissaoEdicaoPerfilCorretamente() {
        // Arrange
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        when(auth.getToken()).thenReturn(jwt);
        when(jwt.getClaim("userId")).thenReturn(123L);
        when(auth.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        // Act & Assert
        assertTrue(authorizationService.canEditProfile(123L)); // Próprio perfil
        assertFalse(authorizationService.canEditProfile(456L)); // Outro perfil
    }
} 