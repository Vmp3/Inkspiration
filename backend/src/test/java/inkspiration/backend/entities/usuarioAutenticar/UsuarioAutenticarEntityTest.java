package inkspiration.backend.entities.usuarioAutenticar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.UsuarioAutenticar;

public class UsuarioAutenticarEntityTest {

    @Test
    void construtorPadraoDeveFuncionar() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        assertNotNull(usuarioAutenticar);
        assertNull(usuarioAutenticar.getIdUsuarioAutenticar());
        assertNull(usuarioAutenticar.getCpf());
        assertNull(usuarioAutenticar.getSenha());
        assertNull(usuarioAutenticar.getRole());
    }

    @Test
    void construtorComParametrosDeveFuncionar() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar(1L, "123.456.789-01", "senhaHasheada", "USER");
        
        assertEquals(1L, usuarioAutenticar.getIdUsuarioAutenticar());
        assertEquals("12345678901", usuarioAutenticar.getCpf()); // CPF deve ser limpo
        assertEquals("senhaHasheada", usuarioAutenticar.getSenha());
        assertEquals("ROLE_USER", usuarioAutenticar.getRole()); // Role deve ser normalizada
    }

    @Test
    void construtorComDTODeveFuncionar() {
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(2L);
        dto.setCpf("987.654.321-00");
        dto.setSenha("outraSenhaHasheada");
        dto.setRole("ADMIN");
        
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar(dto);
        
        assertEquals(2L, usuarioAutenticar.getIdUsuarioAutenticar());
        assertEquals("98765432100", usuarioAutenticar.getCpf()); // CPF deve ser limpo
        assertEquals("outraSenhaHasheada", usuarioAutenticar.getSenha());
        assertEquals("ROLE_ADMIN", usuarioAutenticar.getRole()); // Role deve ser normalizada
    }

    @Test
    void gettersESettersDevemFuncionar() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        
        usuarioAutenticar.setIdUsuarioAutenticar(3L);
        assertEquals(3L, usuarioAutenticar.getIdUsuarioAutenticar());
        
        usuarioAutenticar.setCpf("111.222.333-44");
        assertEquals("11122233344", usuarioAutenticar.getCpf());
        
        usuarioAutenticar.setSenha("novaSenha");
        assertEquals("novaSenha", usuarioAutenticar.getSenha());
        
        usuarioAutenticar.setRole("PROF");
        assertEquals("ROLE_PROF", usuarioAutenticar.getRole());
    }

    @Test
    void cpfComApenasNumerosDeveSerMantido() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf("12345678901");
        assertEquals("12345678901", usuarioAutenticar.getCpf());
    }

    @Test
    void cpfComMascaraComplexaDeveSerLimpo() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf("123.456.789-01 ");
        assertEquals("12345678901", usuarioAutenticar.getCpf());
    }

    @Test
    void roleComPrefixoJaExistenteDeveSerMantida() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setRole("ROLE_DELETED");
        assertEquals("ROLE_DELETED", usuarioAutenticar.getRole());
    }

    @Test
    void roleMinusculaComEspacosDeveSerNormalizada() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setRole(" prof ");
        assertEquals("ROLE_PROF", usuarioAutenticar.getRole());
    }

    @Test
    void construtorComCpfNuloDeveFuncionar() {
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar(1L, null, "senha", "USER");
        assertNull(usuarioAutenticar.getCpf());
    }

    @Test
    void construtorComRoleInvalidaDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UsuarioAutenticar(1L, "12345678901", "senha", "ROLE_INVALID");
        });
    }
} 