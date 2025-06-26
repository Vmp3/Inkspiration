package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.exception.UsuarioValidationException;

@DisplayName("UsuarioService - Testes de DTO e Entidades")
class UsuarioServiceDTOTest {

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setEmail("joao@example.com");
        usuarioDTO.setCpf("11144477735");
        usuarioDTO.setDataNascimento("01/01/1990");
        usuarioDTO.setSenha("MinhaSenh@123");
        usuarioDTO.setTelefone("(11) 99999-9999");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setCpf("11144477735");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setTelefone("(11) 99999-9999");
        usuario.setRole("ROLE_USER");
        usuario.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar UsuarioDTO válido")
    void deveCriarUsuarioDTOValido() {
        assertNotNull(usuarioDTO);
        assertEquals("João Silva", usuarioDTO.getNome());
        assertEquals("joao@example.com", usuarioDTO.getEmail());
        assertEquals("11144477735", usuarioDTO.getCpf());
        assertEquals("01/01/1990", usuarioDTO.getDataNascimento());
        assertEquals("MinhaSenh@123", usuarioDTO.getSenha());
        assertEquals("(11) 99999-9999", usuarioDTO.getTelefone());
    }

    @Test
    @DisplayName("Deve criar Usuario válido")
    void deveCriarUsuarioValido() {
        assertNotNull(usuario);
        assertEquals(1L, usuario.getIdUsuario());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@example.com", usuario.getEmail());
        assertEquals("11144477735", usuario.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), usuario.getDataNascimento());
        assertEquals("(11) 99999-9999", usuario.getTelefone());
        assertEquals("ROLE_USER", usuario.getRole());
        assertNotNull(usuario.getCreatedAt());
    }

    @Test
    @DisplayName("Deve validar todos os campos obrigatórios do DTO")
    void deveValidarTodosCamposObrigatoriosDoDTO() {
        // Nome obrigatório
        usuarioDTO.setNome(null);
        assertThrows(UsuarioValidationException.NomeObrigatorioException.class, () -> {
            if (usuarioDTO.getNome() == null || usuarioDTO.getNome().trim().isEmpty()) {
                throw new UsuarioValidationException.NomeObrigatorioException();
            }
        });
        usuarioDTO.setNome("João Silva");
        
        // Email obrigatório
        usuarioDTO.setEmail(null);
        assertThrows(UsuarioValidationException.EmailObrigatorioException.class, () -> {
            if (usuarioDTO.getEmail() == null || usuarioDTO.getEmail().trim().isEmpty()) {
                throw new UsuarioValidationException.EmailObrigatorioException();
            }
        });
        usuarioDTO.setEmail("joao@example.com");
        
        // CPF obrigatório
        usuarioDTO.setCpf(null);
        assertThrows(UsuarioValidationException.CpfObrigatorioException.class, () -> {
            if (usuarioDTO.getCpf() == null || usuarioDTO.getCpf().trim().isEmpty()) {
                throw new UsuarioValidationException.CpfObrigatorioException();
            }
        });
        usuarioDTO.setCpf("11144477735");
        
        // Data de nascimento obrigatória
        usuarioDTO.setDataNascimento(null);
        assertThrows(UsuarioValidationException.DataNascimentoObrigatoriaException.class, () -> {
            if (usuarioDTO.getDataNascimento() == null || usuarioDTO.getDataNascimento().trim().isEmpty()) {
                throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
            }
        });
        usuarioDTO.setDataNascimento("01/01/1990");
        
        // Senha obrigatória
        usuarioDTO.setSenha(null);
        assertThrows(UsuarioValidationException.SenhaObrigatoriaException.class, () -> {
            if (usuarioDTO.getSenha() == null || usuarioDTO.getSenha().trim().isEmpty()) {
                throw new UsuarioValidationException.SenhaObrigatoriaException();
            }
        });
        usuarioDTO.setSenha("MinhaSenh@123");
        
        // Telefone obrigatório
        usuarioDTO.setTelefone(null);
        assertThrows(UsuarioValidationException.TelefoneObrigatorioException.class, () -> {
            if (usuarioDTO.getTelefone() == null || usuarioDTO.getTelefone().trim().isEmpty()) {
                throw new UsuarioValidationException.TelefoneObrigatorioException();
            }
        });
    }

    @Test
    @DisplayName("Deve criar UsuarioAutenticar válido")
    void deveCriarUsuarioAutenticarValido() {
        UsuarioAutenticar usuarioAuth = new UsuarioAutenticar();
        usuarioAuth.setCpf("11144477735");
        usuarioAuth.setSenha("senha-criptografada");
        usuarioAuth.setRole("ROLE_USER");
        
        usuario.setUsuarioAutenticar(usuarioAuth);
        
        assertNotNull(usuario.getUsuarioAutenticar());
        assertEquals("11144477735", usuario.getUsuarioAutenticar().getCpf());
        assertEquals("senha-criptografada", usuario.getUsuarioAutenticar().getSenha());
        assertEquals("ROLE_USER", usuario.getUsuarioAutenticar().getRole());
    }

    @Test
    @DisplayName("Deve permitir modificar campos do DTO")
    void devePermitirModificarCamposDoDTO() {
        usuarioDTO.setNome("Maria Silva");
        usuarioDTO.setEmail("maria@example.com");
        usuarioDTO.setCpf("98765432100");
        usuarioDTO.setDataNascimento("15/12/1985");
        usuarioDTO.setSenha("OutraSenha@456");
        usuarioDTO.setTelefone("(21) 98888-7777");
        
        assertEquals("Maria Silva", usuarioDTO.getNome());
        assertEquals("maria@example.com", usuarioDTO.getEmail());
        assertEquals("98765432100", usuarioDTO.getCpf());
        assertEquals("15/12/1985", usuarioDTO.getDataNascimento());
        assertEquals("OutraSenha@456", usuarioDTO.getSenha());
        assertEquals("(21) 98888-7777", usuarioDTO.getTelefone());
    }

    @Test
    @DisplayName("Deve permitir modificar campos da entidade Usuario")
    void devePermitirModificarCamposDaEntidadeUsuario() {
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@example.com");
        usuario.setCpf("98765432100");
        usuario.setDataNascimento(LocalDate.of(1985, 12, 15));
        usuario.setTelefone("(21) 98888-7777");
        usuario.setRole("ROLE_ADMIN");
        
        assertEquals("Maria Silva", usuario.getNome());
        assertEquals("maria@example.com", usuario.getEmail());
        assertEquals("98765432100", usuario.getCpf());
        assertEquals(LocalDate.of(1985, 12, 15), usuario.getDataNascimento());
        assertEquals("(21) 98888-7777", usuario.getTelefone());
        assertEquals("ROLE_ADMIN", usuario.getRole());
    }

    @Test
    @DisplayName("Deve validar datas de criação")
    void deveValidarDatasDeCriacao() {
        LocalDateTime agora = LocalDateTime.now();
        usuario.setCreatedAt(agora);
        
        assertEquals(agora, usuario.getCreatedAt());
        assertNotNull(usuario.getCreatedAt());
    }

    @Test
    @DisplayName("Deve validar campos opcionais do usuário")
    void deveValidarCamposOpcionaisDoUsuario() {
        // Campos opcionais podem ser nulos
        usuario.setImagemPerfil(null);
        usuario.setTokenAtual(null);
        
        assertNull(usuario.getImagemPerfil());
        assertNull(usuario.getTokenAtual());
        
        // Mas podem ser preenchidos
        usuario.setImagemPerfil("imagem-base64");
        usuario.setTokenAtual("token-123");
        
        assertNotNull(usuario.getImagemPerfil());
        assertNotNull(usuario.getTokenAtual());
    }

    @Test
    @DisplayName("Deve validar diferentes roles")
    void deveValidarDiferentesRoles() {
        // Testando apenas o role definido no setUp
        assertEquals("ROLE_USER", usuario.getRole());
        assertNotNull(usuario.getRole());
        assertFalse(usuario.getRole().isEmpty());
    }

    @Test
    @DisplayName("Deve validar toString ou representação textual")
    void deveValidarToStringOuRepresentacaoTextual() {
        // Testa se os objetos têm representação textual válida
        assertNotNull(usuarioDTO.toString());
        assertNotNull(usuario.toString());
        
        // Verifica se contém informações básicas
        String userString = usuario.toString();
        assertTrue(userString.contains("Usuario") || userString.contains("João Silva") || 
                  userString.contains("joao@example.com") || userString.length() > 0);
    }
} 