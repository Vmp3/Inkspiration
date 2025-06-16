package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.repository.UsuarioAutenticarRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioAutenticarRepository repository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf("12345678901");
        usuarioAutenticar.setSenha("hashedPassword");
        usuarioAutenticar.setRole("ROLE_USER");
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        String cpf = "12345678901";
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertEquals(cpf, userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String cpf = "12345678901";
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> customUserDetailsService.loadUserByUsername(cpf));
        
        assertEquals("Usuário não encontrado com o CPF: " + cpf, exception.getMessage());
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_AdminRole() {
        // Arrange
        String cpf = "12345678901";
        usuarioAutenticar.setRole("ROLE_ADMIN");
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertEquals(cpf, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_WithRolePrefix() {
        // Arrange
        String cpf = "12345678901";
        usuarioAutenticar.setRole("ROLE_PROFESSIONAL");
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertEquals(cpf, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PROFESSIONAL")));
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_EmptyPassword() {
        // Arrange
        String cpf = "12345678901";
        usuarioAutenticar.setSenha("");
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertEquals("", userDetails.getPassword());
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_NullCpf() {
        // Arrange
        String cpf = null;
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, 
            () -> customUserDetailsService.loadUserByUsername(cpf));
        
        assertEquals("Usuário não encontrado com o CPF: " + cpf, exception.getMessage());
        verify(repository).findByCpf(cpf);
    }

    @Test
    void testLoadUserByUsername_EmptyRole() {
        // Arrange
        String cpf = "12345678901";
        usuarioAutenticar.setRole("");
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_")));
        verify(repository).findByCpf(cpf);
    }
} 