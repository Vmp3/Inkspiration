package inkspiration.backend.security;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
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
class AuthorizationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private AuthorizationService authorizationService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf("12345678901");
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
    }

    @Test
    void testIsCurrentUserAdmin_UserIsAdmin() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act
            boolean isAdmin = authorizationService.isCurrentUserAdmin();

            // Assert
            assertTrue(isAdmin);
        }
    }

    @Test
    void testIsCurrentUserAdmin_UserIsNotAdmin() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

            // Act
            boolean isAdmin = authorizationService.isCurrentUserAdmin();

            // Assert
            assertFalse(isAdmin);
        }
    }

    @Test
    void testIsCurrentUserAdmin_NoAuthentication() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act
            boolean isAdmin = authorizationService.isCurrentUserAdmin();

            // Assert
            assertFalse(isAdmin);
        }
    }

    @Test
    void testGetCurrentUserId_Success() {
        // Arrange
        Long expectedUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(expectedUserId);

            // Act
            Long userId = authorizationService.getCurrentUserId();

            // Assert
            assertEquals(expectedUserId, userId);
        }
    }

    @Test
    void testGetCurrentUserId_NoAuthentication() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act
            Long userId = authorizationService.getCurrentUserId();

            // Assert
            assertNull(userId);
        }
    }

    @Test
    void testGetCurrentUserId_NotJwtAuthentication() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            // Act
            Long userId = authorizationService.getCurrentUserId();

            // Assert
            assertNull(userId);
        }
    }

    @Test
    void testGetCurrentUserCpf_Success() {
        // Arrange
        String expectedCpf = "12345678901";
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(expectedCpf);

            // Act
            String cpf = authorizationService.getCurrentUserCpf();

            // Assert
            assertEquals(expectedCpf, cpf);
        }
    }

    @Test
    void testGetCurrentUserCpf_NoAuthentication() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act
            String cpf = authorizationService.getCurrentUserCpf();

            // Assert
            assertNull(cpf);
        }
    }

    @Test
    void testCanAccessUser_AdminCanAccessAnyUser() {
        // Arrange
        Long targetUserId = 2L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act
            boolean canAccess = authorizationService.canAccessUser(targetUserId);

            // Assert
            assertTrue(canAccess);
        }
    }

    @Test
    void testCanAccessUser_UserCanAccessOwnData() {
        // Arrange
        Long targetUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(jwtAuthenticationToken).getAuthorities();
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(targetUserId);

            // Act
            boolean canAccess = authorizationService.canAccessUser(targetUserId);

            // Assert
            assertTrue(canAccess);
        }
    }

    @Test
    void testCanAccessUser_UserCannotAccessOtherUserData() {
        // Arrange
        Long targetUserId = 2L;
        Long currentUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(jwtAuthenticationToken).getAuthorities();
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(currentUserId);

            // Act
            boolean canAccess = authorizationService.canAccessUser(targetUserId);

            // Assert
            assertFalse(canAccess);
        }
    }

    @Test
    void testCanAccessProfessional_AdminCanAccess() {
        // Arrange
        Long professionalUserId = 2L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act
            boolean canAccess = authorizationService.canAccessProfessional(professionalUserId);

            // Assert
            assertTrue(canAccess);
        }
    }

    @Test
    void testCanAccessProfessional_ProfessionalCanAccessOwnData() {
        // Arrange
        Long professionalUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_PROF")))
                .when(jwtAuthenticationToken).getAuthorities();
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(professionalUserId);

            // Act
            boolean canAccess = authorizationService.canAccessProfessional(professionalUserId);

            // Assert
            assertTrue(canAccess);
        }
    }

    @Test
    void testRequireAdmin_UserIsAdmin_Success() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act & Assert
            assertDoesNotThrow(() -> authorizationService.requireAdmin());
        }
    }

    @Test
    void testRequireAdmin_UserIsNotAdmin_ThrowsException() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication).getAuthorities();

            // Act & Assert
            assertThrows(UsuarioException.PermissaoNegadaException.class, () -> authorizationService.requireAdmin());
        }
    }

    @Test
    void testRequireUserAccessOrAdmin_AdminCanAccess() {
        // Arrange
        Long targetUserId = 2L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act & Assert
            assertDoesNotThrow(() -> authorizationService.requireUserAccessOrAdmin(targetUserId));
        }
    }

    @Test
    void testRequireUserAccessOrAdmin_UserCannotAccess_ThrowsException() {
        // Arrange
        Long targetUserId = 2L;
        Long currentUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(jwtAuthenticationToken).getAuthorities();
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(currentUserId);

            // Act & Assert
            assertThrows(UsuarioException.PermissaoNegadaException.class, 
                () -> authorizationService.requireUserAccessOrAdmin(targetUserId));
        }
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        String cpf = "12345678901";
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(cpf);
            when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

            // Act
            Usuario result = authorizationService.getCurrentUser();

            // Assert
            assertNotNull(result);
            assertEquals(usuario.getCpf(), result.getCpf());
            verify(usuarioRepository).findByCpf(cpf);
        }
    }

    @Test
    void testGetCurrentUser_NoAuthentication_ThrowsException() {
        // Arrange
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            // Act & Assert
            assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, 
                () -> authorizationService.getCurrentUser());
        }
    }

    @Test
    void testGetCurrentUser_UserNotFound_ThrowsException() {
        // Arrange
        String cpf = "12345678901";
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(cpf);
            when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, 
                () -> authorizationService.getCurrentUser());
        }
    }

    @Test
    void testValidateTokenOwnership_ValidOwner() {
        // Arrange
        Long expectedUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(expectedUserId);

            // Act
            boolean result = authorizationService.validateTokenOwnership(expectedUserId);

            // Assert
            assertTrue(result);
        }
    }

    @Test
    void testValidateTokenOwnership_InvalidOwner() {
        // Arrange
        Long tokenUserId = 1L;
        Long requestUserId = 2L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(tokenUserId);

            // Act
            boolean result = authorizationService.validateTokenOwnership(requestUserId);

            // Assert
            assertFalse(result);
        }
    }

    @Test
    void testCanEditProfile_AdminCanEdit() {
        // Arrange
        Long targetUserId = 2L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

            // Act
            boolean canEdit = authorizationService.canEditProfile(targetUserId);

            // Assert
            assertTrue(canEdit);
        }
    }

    @Test
    void testCanEditProfile_UserCanEditOwnProfile() {
        // Arrange
        Long targetUserId = 1L;
        try (MockedStatic<SecurityContextHolder> mockSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
            
            doReturn(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(jwtAuthenticationToken).getAuthorities();
            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
            when(jwt.getClaim("userId")).thenReturn(targetUserId);

            // Act
            boolean canEdit = authorizationService.canEditProfile(targetUserId);

            // Assert
            assertTrue(canEdit);
        }
    }
} 