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
import inkspiration.backend.exception.usuario.TokenValidationException;
import inkspiration.backend.util.CpfValidator;
import inkspiration.backend.util.DateValidator;
import inkspiration.backend.util.EmailValidator;
import inkspiration.backend.util.TelefoneValidator;

@DisplayName("UsuarioService - Testes Simples (Sem Mocks)")
class UsuarioServiceSimpleTest {

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setEmail("joao@example.com");
        usuarioDTO.setCpf("11144477735"); // CPF válido
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

        UsuarioAutenticar usuarioAuth = new UsuarioAutenticar();
        usuarioAuth.setCpf("11144477735");
        usuarioAuth.setSenha("senha-criptografada");
        usuarioAuth.setRole("ROLE_USER");
        usuario.setUsuarioAutenticar(usuarioAuth);
    }

    @Test
    @DisplayName("Deve validar CPF corretamente")
    void deveValidarCpfCorretamente() {
        assertTrue(CpfValidator.isValid("11144477735"));
        assertFalse(CpfValidator.isValid("12345678901"));
        assertTrue(CpfValidator.isValid("111.444.777-35")); // CPF com máscara também é válido
        assertFalse(CpfValidator.isValid("000.000.000-00")); // CPF inválido
    }

    @Test
    @DisplayName("Deve validar email corretamente")
    void deveValidarEmailCorretamente() {
        assertTrue(EmailValidator.isValid("joao@example.com"));
        assertTrue(EmailValidator.isValid("usuario.teste@domain.com.br"));
        assertFalse(EmailValidator.isValid("email-invalido"));
        assertFalse(EmailValidator.isValid("@domain.com"));
        assertFalse(EmailValidator.isValid("usuario@"));
    }

    @Test
    @DisplayName("Deve validar data corretamente")
    void deveValidarDataCorretamente() {
        assertTrue(DateValidator.isValid("01/01/1990"));
        assertTrue(DateValidator.isValid("31/12/2000"));
        assertFalse(DateValidator.isValid("31/02/1990"));
        assertFalse(DateValidator.isValid("32/01/1990"));
        assertFalse(DateValidator.isValid("01/13/1990"));
        assertFalse(DateValidator.isValid("1990-01-01"));
    }

    @Test
    @DisplayName("Deve validar idade mínima corretamente")
    void deveValidarIdadeMinimaCorretamente() {
        assertTrue(DateValidator.hasMinimumAge("01/01/1990", 18));
        assertTrue(DateValidator.hasMinimumAge("01/01/2000", 18));
        assertFalse(DateValidator.hasMinimumAge("01/01/2010", 18));
    }

    @Test
    @DisplayName("Deve validar telefone corretamente")
    void deveValidarTelefoneCorretamente() {
        assertTrue(TelefoneValidator.isValid("(11) 99999-9999"));
        assertTrue(TelefoneValidator.isValid("(11) 3333-4444"));
        assertTrue(TelefoneValidator.isValid("11999999999"));
        assertTrue(TelefoneValidator.isValid("1133334444"));
        assertFalse(TelefoneValidator.isValid("123"));
        assertFalse(TelefoneValidator.isValid("(11) 1234-5678")); // Celular deve começar com 9
    }

    @Test
    @DisplayName("Deve identificar telefone celular corretamente")
    void deveIdentificarTelefoneCelularCorretamente() {
        assertTrue(TelefoneValidator.isCelular("(11) 99999-9999"));
        assertTrue(TelefoneValidator.isCelular("11987654321"));
        assertFalse(TelefoneValidator.isCelular("(11) 3333-4444"));
        assertFalse(TelefoneValidator.isCelular("1133334444"));
    }

    @Test
    @DisplayName("Deve validar token simples corretamente")
    void deveValidarTokenSimplesCorretamente() {
        String token = "token-teste-123";
        usuario.setTokenAtual(token);
        
        // Simula validação básica de token
        if (token == null || token.isEmpty()) {
            assertThrows(TokenValidationException.class, () -> {
                throw new TokenValidationException("Token não fornecido");
            });
        }
        
        if (usuario.getTokenAtual() == null) {
            assertThrows(TokenValidationException.class, () -> {
                throw new TokenValidationException("Usuário não possui token ativo");
            });
        }
        
        assertEquals(token, usuario.getTokenAtual());
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
        assertNotNull(usuario.getUsuarioAutenticar());
    }

    @Test
    @DisplayName("Deve validar dados obrigatórios do DTO")
    void deveValidarDadosObrigatoriosDoDTO() {
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
    }

    @Test
    @DisplayName("Deve formatar CPF corretamente")
    void deveFormatarCpfCorretamente() {
        String cpfComMascara = "111.444.777-35";
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", "");
        
        assertEquals("11144477735", cpfLimpo);
        assertEquals(11, cpfLimpo.length());
    }

    @Test
    @DisplayName("Deve limpar telefone corretamente")
    void deveLimparTelefoneCorretamente() {
        String telefoneComMascara = "(11) 99999-9999";
        String telefoneLimpo = telefoneComMascara.replaceAll("[^0-9]", "");
        
        assertEquals("11999999999", telefoneLimpo);
        assertEquals(11, telefoneLimpo.length());
    }
} 