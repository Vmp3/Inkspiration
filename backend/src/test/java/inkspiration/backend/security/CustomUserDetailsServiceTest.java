package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
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
@DisplayName("CustomUserDetailsService - Testes Unitários")
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioAutenticarRepository repository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Deve carregar usuário por CPF com sucesso")
    void deveCarregarUsuarioPorCPFComSucesso() {
        // Arrange
        String cpf = "12345678900";
        String senha = "senha123";
        String role = "ROLE_USER";

        UsuarioAutenticar usuario = new UsuarioAutenticar();
        usuario.setCpf(cpf);
        usuario.setSenha(senha);
        usuario.setRole(role);

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);

        // Assert
        assertNotNull(userDetails);
        assertEquals(cpf, userDetails.getUsername());
        assertEquals(senha, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não encontrado")
    void deveLancarUsernameNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        // Arrange
        String cpf = "12345678900";
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(cpf)
        );
        assertEquals("Usuário não encontrado com o CPF: " + cpf, exception.getMessage());
    }

    @Test
    @DisplayName("Deve carregar usuário com role personalizada")
    void deveCarregarUsuarioComRolePersonalizada() {
        // Arrange
        String cpf = "12345678900";
        String role = "ROLE_ADMIN";

        UsuarioAutenticar usuario = new UsuarioAutenticar();
        usuario.setCpf(cpf);
        usuario.setSenha("senha123");
        usuario.setRole(role);

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);

        // Assert
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Deve remover prefixo ROLE_ ao criar authorities")
    void deveRemoverPrefixoRoleAoCriarAuthorities() {
        // Arrange
        String cpf = "12345678900";
        String role = "ROLE_PROF";

        UsuarioAutenticar usuario = new UsuarioAutenticar();
        usuario.setCpf(cpf);
        usuario.setSenha("senha123");
        usuario.setRole(role);

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);

        // Assert
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PROF")));
    }
} 