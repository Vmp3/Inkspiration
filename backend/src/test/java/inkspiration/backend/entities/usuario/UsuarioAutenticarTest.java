package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void testConstrutorVazio() {
        UsuarioAutenticar usuario = new UsuarioAutenticar();
        
        assertNull(usuario.getIdUsuarioAutenticar(), "ID deve ser nulo inicialmente");
        assertNull(usuario.getCpf(), "CPF deve ser nulo inicialmente");
        assertNull(usuario.getSenha(), "Senha deve ser nula inicialmente");
        assertNull(usuario.getRole(), "Role deve ser nulo inicialmente");
    }

    @Test
    void testConstrutorCompleto() {
        Long id = 1L;
        String cpf = "111.444.777-35";
        String senha = "minhasenha123";
        String role = "USER";

        UsuarioAutenticar usuario = new UsuarioAutenticar(id, cpf, senha, role);

        assertEquals(id, usuario.getIdUsuarioAutenticar(), "ID deve ser igual ao definido");
        assertEquals("11144477735", usuario.getCpf(), "CPF deve remover formatação");
        assertNotEquals(senha, usuario.getSenha(), "Senha deve ser hasheada");
        assertTrue(Hashing.matches(senha, usuario.getSenha()), "Hash da senha deve corresponder à senha original");
        assertEquals(role, usuario.getRole(), "Role deve ser igual ao definido");
    }

    @Test
    void testConstrutorComDTO() {
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(1L);
        dto.setCpf("111.444.777-35");
        dto.setSenha("minhasenha123");
        dto.setRole("ADMIN");

        UsuarioAutenticar usuario = new UsuarioAutenticar(dto);

        assertEquals(dto.getIdUsuarioAutenticar(), usuario.getIdUsuarioAutenticar(), "ID deve ser igual ao do DTO");
        assertEquals("11144477735", usuario.getCpf(), "CPF deve remover formatação");
        assertNotEquals(dto.getSenha(), usuario.getSenha(), "Senha deve ser hasheada");
        assertTrue(Hashing.matches(dto.getSenha(), usuario.getSenha()), "Hash da senha deve corresponder à senha do DTO");
        assertEquals(dto.getRole(), usuario.getRole(), "Role deve ser igual ao do DTO");
    }

    @Test
    void testGettersAndSettersIdUsuarioAutenticar() {
        Long id = 1L;
        usuarioAutenticar.setIdUsuarioAutenticar(id);
        assertEquals(id, usuarioAutenticar.getIdUsuarioAutenticar(), "ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCpf() {
        String cpf = "123.456.789-09";
        usuarioAutenticar.setCpf(cpf);
        assertEquals("12345678909", usuarioAutenticar.getCpf(), "CPF deve remover formatação");
    }

    @Test
    void testCpfRemoveFormatacao() {
        usuarioAutenticar.setCpf("111.444.777-35");
        assertEquals("11144477735", usuarioAutenticar.getCpf(), "CPF deve remover pontos e hífen");

        usuarioAutenticar.setCpf("111 444 777 35");
        assertEquals("11144477735", usuarioAutenticar.getCpf(), "CPF deve remover espaços");

        usuarioAutenticar.setCpf("111-444-777-35");
        assertEquals("11144477735", usuarioAutenticar.getCpf(), "CPF deve remover hífens");
    }

    @Test
    void testCpfComCaracteresEspeciais() {
        usuarioAutenticar.setCpf("abc111def444ghi777jkl35");
        assertEquals("11144477735", usuarioAutenticar.getCpf(), "CPF deve manter apenas números");
    }

    @Test
    void testGettersAndSettersSenha() {
        String senha = "minhasenha123";
        usuarioAutenticar.setSenha(senha);
        assertEquals(senha, usuarioAutenticar.getSenha(), "Senha deve ser igual à definida (setter direto não faz hash)");
    }

    @Test
    void testGettersAndSettersRole() {
        String role = "ADMIN";
        usuarioAutenticar.setRole(role);
        assertEquals(role, usuarioAutenticar.getRole(), "Role deve ser igual ao definido");
    }

    @Test
    void testUsuarioComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String cpf = "111.444.777-35";
        String senha = "senhaSegura123";
        String role = "USER";

        // Act
        usuarioAutenticar.setIdUsuarioAutenticar(id);
        usuarioAutenticar.setCpf(cpf);
        usuarioAutenticar.setSenha(senha);
        usuarioAutenticar.setRole(role);

        // Assert
        assertEquals(id, usuarioAutenticar.getIdUsuarioAutenticar());
        assertEquals("11144477735", usuarioAutenticar.getCpf());
        assertEquals(senha, usuarioAutenticar.getSenha());
        assertEquals(role, usuarioAutenticar.getRole());
    }

    @Test
    void testValoresNulos() {
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf(null), "CPF nulo deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf(""), "CPF vazio deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuarioAutenticar.setCpf("   "), "CPF apenas com espaços deve lançar exceção");
        
        assertDoesNotThrow(() -> {
            usuarioAutenticar.setSenha(null);
            usuarioAutenticar.setRole(null);
        }, "Senha e role podem aceitar valores nulos");
    }



    @Test
    void testSenhaVazia() {
        String senhaVazia = "";
        usuarioAutenticar.setSenha(senhaVazia);
        assertEquals(senhaVazia, usuarioAutenticar.getSenha(), "Senha vazia deve ser aceita");
    }

    @Test
    void testRoleVazia() {
        String roleVazia = "";
        usuarioAutenticar.setRole(roleVazia);
        assertEquals(roleVazia, usuarioAutenticar.getRole(), "Role vazia deve ser aceita");
    }

    @Test
    void testHashSenhaNosConstrutores() {
        String senhaOriginal = "senha123";
        
        // Teste construtor completo
        UsuarioAutenticar usuario1 = new UsuarioAutenticar(1L, "11144477735", senhaOriginal, "USER");
        assertNotEquals(senhaOriginal, usuario1.getSenha(), "Senha deve ser hasheada no construtor completo");
        assertTrue(Hashing.matches(senhaOriginal, usuario1.getSenha()), "Hash deve corresponder à senha original");

        // Teste construtor com DTO
        UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
        dto.setIdUsuarioAutenticar(2L);
        dto.setCpf("11144477735");
        dto.setSenha(senhaOriginal);
        dto.setRole("ADMIN");

        UsuarioAutenticar usuario2 = new UsuarioAutenticar(dto);
        assertNotEquals(senhaOriginal, usuario2.getSenha(), "Senha deve ser hasheada no construtor com DTO");
        assertTrue(Hashing.matches(senhaOriginal, usuario2.getSenha()), "Hash deve corresponder à senha original");
    }

    @Test
    void testDiferentesRoles() {
        String[] roles = {"USER", "ADMIN", "MODERATOR", "GUEST"};
        
        for (String role : roles) {
            usuarioAutenticar.setRole(role);
            assertEquals(role, usuarioAutenticar.getRole(), "Role " + role + " deve ser aceita");
        }
    }

    @Test
    void testValoresLimite() {
        // Teste com strings muito grandes
        String textoGrande = "a".repeat(2000);
        
        assertDoesNotThrow(() -> {
            usuarioAutenticar.setCpf(textoGrande);
            usuarioAutenticar.setSenha(textoGrande);
            usuarioAutenticar.setRole(textoGrande);
        }, "Deve aceitar strings grandes sem lançar exceção");
    }
} 