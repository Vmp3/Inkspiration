package inkspiration.backend.entities.profissional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;

@DisplayName("Testes de validação de usuário - Profissional")
public class ProfissionalUsuarioTest {

    private Profissional profissional;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
    }

    @Test
    @DisplayName("Deve definir usuário válido")
    void deveDefinirUsuarioValido() {
        profissional.setUsuario(usuario);
        assertEquals(usuario, profissional.getUsuario());
    }

    @Test
    @DisplayName("Não deve aceitar usuário nulo")
    void naoDeveAceitarUsuarioNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            profissional.setUsuario(null);
        });
        assertEquals("O usuário não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve permitir alterar usuário")
    void devePermitirAlterarUsuario() {
        profissional.setUsuario(usuario);
        assertEquals(usuario, profissional.getUsuario());
        
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome("Maria Santos");
        novoUsuario.setCpf("98765432109");
        novoUsuario.setEmail("maria@email.com");
        
        profissional.setUsuario(novoUsuario);
        assertEquals(novoUsuario, profissional.getUsuario());
    }
} 