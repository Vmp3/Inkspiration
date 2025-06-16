package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.UsuarioAutenticar;

public class UsuarioTest {

    private Usuario usuario;
    private Endereco endereco;
    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        endereco = new Endereco();
        usuarioAutenticar = new UsuarioAutenticar();
    }

    @Test
    void testGettersAndSettersIdUsuario() {
        Long id = 1L;
        usuario.setIdUsuario(id);
        assertEquals(id, usuario.getIdUsuario(), "ID do usuário deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersNome() {
        String nome = "João Silva";
        usuario.setNome(nome);
        assertEquals(nome, usuario.getNome(), "Nome deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCpf() {
        String cpf = "123.456.789-09";
        usuario.setCpf(cpf);
        assertEquals("12345678909", usuario.getCpf(), "CPF deve remover formatação");
    }

    @Test
    void testCpfRemoveFormatacao() {
        usuario.setCpf("111.444.777-35");
        assertEquals("11144477735", usuario.getCpf(), "CPF deve remover pontos e hífen");
        
        usuario.setCpf("111 444 777 35");
        assertEquals("11144477735", usuario.getCpf(), "CPF deve remover espaços");
        
        usuario.setCpf("111-444-777-35");
        assertEquals("11144477735", usuario.getCpf(), "CPF deve remover hífens");
    }

    @Test
    void testCpfComCaracteresEspeciais() {
        usuario.setCpf("abc123def456ghi789jkl");
        assertEquals("123456789", usuario.getCpf(), "CPF deve manter apenas números");
    }

    @Test
    void testGettersAndSettersEmail() {
        String email = "joao@email.com";
        usuario.setEmail(email);
        assertEquals(email, usuario.getEmail(), "Email deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersDataNascimento() {
        LocalDate data = LocalDate.of(1990, 1, 1);
        usuario.setDataNascimento(data);
        assertEquals(data, usuario.getDataNascimento(), "Data de nascimento deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersTelefone() {
        String telefone = "(11) 99999-9999";
        usuario.setTelefone(telefone);
        assertEquals(telefone, usuario.getTelefone(), "Telefone deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersImagemPerfil() {
        String imagem = "base64imagedata";
        usuario.setImagemPerfil(imagem);
        assertEquals(imagem, usuario.getImagemPerfil(), "Imagem de perfil deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersTokenAtual() {
        String token = "token123";
        usuario.setTokenAtual(token);
        assertEquals(token, usuario.getTokenAtual(), "Token atual deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersTwoFactorEnabled() {
        usuario.setTwoFactorEnabled(true);
        assertTrue(usuario.getTwoFactorEnabled(), "Two Factor deve estar habilitado");
        
        usuario.setTwoFactorEnabled(false);
        assertFalse(usuario.getTwoFactorEnabled(), "Two Factor deve estar desabilitado");
    }

    @Test
    void testTwoFactorEnabledDefaultValue() {
        Usuario novoUsuario = new Usuario();
        assertFalse(novoUsuario.getTwoFactorEnabled(), "Two Factor deve estar desabilitado por padrão");
    }

    @Test
    void testGettersAndSettersTwoFactorSecret() {
        String secret = "secret123";
        usuario.setTwoFactorSecret(secret);
        assertEquals(secret, usuario.getTwoFactorSecret(), "Two Factor Secret deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCreatedAt() {
        LocalDateTime agora = LocalDateTime.now();
        usuario.setCreatedAt(agora);
        assertEquals(agora, usuario.getCreatedAt(), "Created At deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersEndereco() {
        usuario.setEndereco(endereco);
        assertEquals(endereco, usuario.getEndereco(), "Endereço deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersUsuarioAutenticar() {
        usuario.setUsuarioAutenticar(usuarioAutenticar);
        assertEquals(usuarioAutenticar, usuario.getUsuarioAutenticar(), "UsuarioAutenticar deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersRole() {
        String role = "USER";
        usuario.setRole(role);
        assertEquals(role, usuario.getRole(), "Role deve ser igual ao definido");
    }

    @Test
    void testUsuarioComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String nome = "João Silva";
        String cpf = "111.444.777-35";
        String email = "joao@email.com";
        LocalDate dataNascimento = LocalDate.of(1990, 1, 1);
        String telefone = "(11) 99999-9999";
        String imagemPerfil = "base64imagedata";
        String tokenAtual = "token123";
        Boolean twoFactorEnabled = true;
        String twoFactorSecret = "secret123";
        LocalDateTime createdAt = LocalDateTime.now();
        String role = "USER";

        // Act
        usuario.setIdUsuario(id);
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setDataNascimento(dataNascimento);
        usuario.setTelefone(telefone);
        usuario.setImagemPerfil(imagemPerfil);
        usuario.setTokenAtual(tokenAtual);
        usuario.setTwoFactorEnabled(twoFactorEnabled);
        usuario.setTwoFactorSecret(twoFactorSecret);
        usuario.setCreatedAt(createdAt);
        usuario.setEndereco(endereco);
        usuario.setUsuarioAutenticar(usuarioAutenticar);
        usuario.setRole(role);

        // Assert
        assertEquals(id, usuario.getIdUsuario());
        assertEquals(nome, usuario.getNome());
        assertEquals("11144477735", usuario.getCpf());
        assertEquals(email, usuario.getEmail());
        assertEquals(dataNascimento, usuario.getDataNascimento());
        assertEquals(telefone, usuario.getTelefone());
        assertEquals(imagemPerfil, usuario.getImagemPerfil());
        assertEquals(tokenAtual, usuario.getTokenAtual());
        assertEquals(twoFactorEnabled, usuario.getTwoFactorEnabled());
        assertEquals(twoFactorSecret, usuario.getTwoFactorSecret());
        assertEquals(createdAt, usuario.getCreatedAt());
        assertEquals(endereco, usuario.getEndereco());
        assertEquals(usuarioAutenticar, usuario.getUsuarioAutenticar());
        assertEquals(role, usuario.getRole());
    }

    @Test
    void testUsuarioVazio() {
        Usuario usuarioVazio = new Usuario();
        
        assertNull(usuarioVazio.getIdUsuario(), "ID deve ser nulo inicialmente");
        assertNull(usuarioVazio.getNome(), "Nome deve ser nulo inicialmente");
        assertNull(usuarioVazio.getCpf(), "CPF deve ser nulo inicialmente");
        assertNull(usuarioVazio.getEmail(), "Email deve ser nulo inicialmente");
        assertNull(usuarioVazio.getDataNascimento(), "Data de nascimento deve ser nula inicialmente");
        assertNull(usuarioVazio.getTelefone(), "Telefone deve ser nulo inicialmente");
        assertNull(usuarioVazio.getImagemPerfil(), "Imagem de perfil deve ser nula inicialmente");
        assertNull(usuarioVazio.getTokenAtual(), "Token atual deve ser nulo inicialmente");
        assertFalse(usuarioVazio.getTwoFactorEnabled(), "Two Factor deve estar desabilitado inicialmente");
        assertNull(usuarioVazio.getTwoFactorSecret(), "Two Factor Secret deve ser nulo inicialmente");
        assertNull(usuarioVazio.getCreatedAt(), "Created At deve ser nulo inicialmente");
        assertNull(usuarioVazio.getEndereco(), "Endereço deve ser nulo inicialmente");
        assertNull(usuarioVazio.getUsuarioAutenticar(), "UsuarioAutenticar deve ser nulo inicialmente");
        assertNull(usuarioVazio.getRole(), "Role deve ser nulo inicialmente");
    }

    @Test
    void testCpfNulo() {
        assertThrows(IllegalArgumentException.class, () -> usuario.setCpf(null), "CPF nulo deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuario.setCpf(""), "CPF vazio deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuario.setCpf("   "), "CPF apenas com espaços deve lançar exceção");
    }

    @Test
    void testEmailNulo() {
        assertThrows(IllegalArgumentException.class, () -> usuario.setEmail(null), "Email nulo deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuario.setEmail(""), "Email vazio deve lançar exceção");
        assertThrows(IllegalArgumentException.class, () -> usuario.setEmail("   "), "Email apenas com espaços deve lançar exceção");
    }

    @Test
    void testEnderecoNulo() {
        assertThrows(IllegalArgumentException.class, () -> usuario.setEndereco(null), "Endereço nulo deve lançar exceção");
    }

    @Test
    void testValoresLimite() {
        // Teste com strings muito grandes
        String textoGrande = "a".repeat(2000);
        
        assertDoesNotThrow(() -> {
            usuario.setNome(textoGrande);
            usuario.setEmail(textoGrande);
            usuario.setTelefone(textoGrande);
            usuario.setImagemPerfil(textoGrande);
            usuario.setTokenAtual(textoGrande);
            usuario.setTwoFactorSecret(textoGrande);
            usuario.setRole(textoGrande);
        }, "Deve aceitar strings grandes sem lançar exceção");
    }
} 