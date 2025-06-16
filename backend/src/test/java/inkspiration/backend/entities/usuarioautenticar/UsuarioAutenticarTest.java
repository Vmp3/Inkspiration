package inkspiration.backend.entities.usuarioautenticar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.util.Hashing;

public class UsuarioAutenticarTest {

    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        usuarioAutenticar = new UsuarioAutenticar();
    }

    @Test
    void testGettersAndSettersIdUsuarioAutenticar() {
        Long id = 1L;
        usuarioAutenticar.setIdUsuarioAutenticar(id);
        assertEquals(id, usuarioAutenticar.getIdUsuarioAutenticar(), "ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCpf() {
        String cpf = "12345678901";
        usuarioAutenticar.setCpf(cpf);
        assertEquals(cpf, usuarioAutenticar.getCpf(), "CPF deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersSenha() {
        String senha = "senha123";
        usuarioAutenticar.setSenha(senha);
        assertEquals(senha, usuarioAutenticar.getSenha(), "Senha deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersRole() {
        String role = "USER";
        usuarioAutenticar.setRole(role);
        assertEquals(role, usuarioAutenticar.getRole(), "Role deve ser igual ao definido");
    }

    @Test
    void testConstrutorPadrao() {
        UsuarioAutenticar usuarioVazio = new UsuarioAutenticar();
        
        assertNull(usuarioVazio.getIdUsuarioAutenticar(), "ID deve ser nulo inicialmente");
        assertNull(usuarioVazio.getCpf(), "CPF deve ser nulo inicialmente");
        assertNull(usuarioVazio.getSenha(), "Senha deve ser nula inicialmente");
        assertNull(usuarioVazio.getRole(), "Role deve ser nulo inicialmente");
    }

    @Test
    void testConstrutorComParametros() {
        Long id = 1L;
        String cpf = "12345678901";
        String senha = "senha123";
        String role = "USER";
        
        UsuarioAutenticar usuarioComParametros = new UsuarioAutenticar(id, cpf, senha, role);
        
        assertEquals(id, usuarioComParametros.getIdUsuarioAutenticar(), "ID deve ser igual ao fornecido no construtor");
        assertEquals(cpf, usuarioComParametros.getCpf(), "CPF deve ser igual ao fornecido no construtor");
        assertTrue(Hashing.matches(senha, usuarioComParametros.getSenha()), "Senha deve ser hasheada corretamente");
        assertEquals(role, usuarioComParametros.getRole(), "Role deve ser igual ao fornecido no construtor");
    }

    @Test
    void testConstrutorComDTO() {
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(1L);
        dto.setCpf("12345678901");
        dto.setSenha("senha123");
        dto.setRole("USER");
        
        UsuarioAutenticar usuarioComDTO = new UsuarioAutenticar(dto);
        
        assertEquals(dto.getIdUsuarioAutenticar(), usuarioComDTO.getIdUsuarioAutenticar(), "ID deve ser igual ao do DTO");
        assertEquals(dto.getCpf(), usuarioComDTO.getCpf(), "CPF deve ser igual ao do DTO");
        assertTrue(Hashing.matches(dto.getSenha(), usuarioComDTO.getSenha()), "Senha deve ser hasheada corretamente");
        assertEquals(dto.getRole(), usuarioComDTO.getRole(), "Role deve ser igual ao do DTO");
    }

    @Test
    void testCpfFormatacao() {
        String cpfComFormatacao = "123.456.789-01";
        usuarioAutenticar.setCpf(cpfComFormatacao);
        assertEquals("12345678901", usuarioAutenticar.getCpf(), "CPF deve ser formatado removendo pontos e hífens");
    }

    @Test
    void testCpfJaFormatado() {
        String cpfSemFormatacao = "12345678901";
        usuarioAutenticar.setCpf(cpfSemFormatacao);
        assertEquals("12345678901", usuarioAutenticar.getCpf(), "CPF já formatado deve permanecer igual");
    }

    @Test
    void testCpfNulo() {
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf(null), "CPF nulo deve lançar exceção");
    }

    @Test
    void testCpfVazio() {
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf(""), "CPF vazio deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf("   "), "CPF apenas com espaços deve lançar exceção");
    }

    @Test
    void testConstrutorComCpfNulo() {
        assertThrows(IllegalArgumentException.class, () -> new UsuarioAutenticar(1L, null, "senha", "USER"), "Construtor com CPF nulo deve lançar exceção");
    }

    @Test
    void testConstrutorComCpfVazio() {
        assertThrows(IllegalArgumentException.class, () -> new UsuarioAutenticar(1L, "", "senha", "USER"), "Construtor com CPF vazio deve lançar exceção");
    }

    @Test
    void testConstrutorDTOComCpfNulo() {
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(1L);
        dto.setCpf(null);
        dto.setSenha("senha123");
        dto.setRole("USER");
        
        assertThrows(IllegalArgumentException.class, () -> new UsuarioAutenticar(dto), "Construtor DTO com CPF nulo deve lançar exceção");
    }

    @Test
    void testConstrutorDTOComCpfVazio() {
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(1L);
        dto.setCpf("");
        dto.setSenha("senha123");
        dto.setRole("USER");
        
        assertThrows(IllegalArgumentException.class, () -> new UsuarioAutenticar(dto), "Construtor DTO com CPF vazio deve lançar exceção");
    }

    @Test
    void testHashingSenhaConstrutorParametros() {
        String senhaOriginal = "minhasenha123";
        UsuarioAutenticar usuario = new UsuarioAutenticar(1L, "12345678901", senhaOriginal, "USER");
        
        assertNotEquals(senhaOriginal, usuario.getSenha(), "Senha deve ser hasheada, não em texto plano");
        assertTrue(Hashing.matches(senhaOriginal, usuario.getSenha()), "Hash da senha deve ser verificável");
    }

    @Test
    void testHashingSenhaConstrutorDTO() {
        String senhaOriginal = "senhadto456";
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(1L);
        dto.setCpf("12345678901");
        dto.setSenha(senhaOriginal);
        dto.setRole("ADMIN");
        
        UsuarioAutenticar usuario = new UsuarioAutenticar(dto);
        
        assertNotEquals(senhaOriginal, usuario.getSenha(), "Senha deve ser hasheada, não em texto plano");
        assertTrue(Hashing.matches(senhaOriginal, usuario.getSenha()), "Hash da senha deve ser verificável");
    }

    @Test
    void testRolesVariadas() {
        String[] roles = {"USER", "ADMIN", "MODERATOR", "GUEST"};
        
        for (String role : roles) {
            usuarioAutenticar.setRole(role);
            assertEquals(role, usuarioAutenticar.getRole(), "Role deve ser igual ao definido: " + role);
        }
    }

    @Test
    void testSenhasDiferentes() {
        String senha1 = "senha123";
        String senha2 = "senha456";
        
        UsuarioAutenticar usuario1 = new UsuarioAutenticar(1L, "12345678901", senha1, "USER");
        UsuarioAutenticar usuario2 = new UsuarioAutenticar(2L, "10987654321", senha2, "USER");
        
        assertNotEquals(usuario1.getSenha(), usuario2.getSenha(), "Hashes de senhas diferentes devem ser diferentes");
        assertTrue(Hashing.matches(senha1, usuario1.getSenha()), "Primeira senha deve ser verificável");
        assertTrue(Hashing.matches(senha2, usuario2.getSenha()), "Segunda senha deve ser verificável");
        assertFalse(Hashing.matches(senha1, usuario2.getSenha()), "Primeira senha não deve coincidir com hash da segunda");
        assertFalse(Hashing.matches(senha2, usuario1.getSenha()), "Segunda senha não deve coincidir com hash da primeira");
    }

    @Test
    void testSenhaVaziaNoSetter() {
        // Testando que o setter não faz validação (apenas os construtores fazem)
        assertDoesNotThrow(() -> usuarioAutenticar.setSenha(""), "Setter deve aceitar senha vazia");
        assertDoesNotThrow(() -> usuarioAutenticar.setSenha(null), "Setter deve aceitar senha nula");
    }

    @Test
    void testRoleVaziaENull() {
        assertDoesNotThrow(() -> usuarioAutenticar.setRole(""), "Role pode ser vazia");
        assertDoesNotThrow(() -> usuarioAutenticar.setRole(null), "Role pode ser nula");
    }
} 