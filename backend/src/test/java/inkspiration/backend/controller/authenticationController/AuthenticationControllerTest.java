package inkspiration.backend.controller.authenticationController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.AuthenticationController;
import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.authentication.AuthenticationFailedException;
import inkspiration.backend.exception.authentication.InvalidTwoFactorCodeException;
import inkspiration.backend.exception.authentication.TwoFactorRequiredException;
import inkspiration.backend.exception.authentication.UserInactiveException;
import inkspiration.backend.exception.authentication.UserNotFoundException;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.security.AuthenticationService;
import inkspiration.backend.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationController - Testes Completos")
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private UsuarioAutenticarDTO usuarioAutenticarDTO;
    private UsuarioDTO usuarioDTO;
    private Usuario usuario;
    private Map<String, String> checkTwoFactorRequest;

    @BeforeEach
    void setUp() {
        setupUsuarioAutenticarDTO();
        setupUsuarioDTO();
        setupUsuario();
        setupCheckTwoFactorRequest();
    }

    private void setupUsuarioAutenticarDTO() {
        usuarioAutenticarDTO = new UsuarioAutenticarDTO();
        usuarioAutenticarDTO.setCpf("12345678901");
        usuarioAutenticarDTO.setSenha("minhasenha123");
        usuarioAutenticarDTO.setRole(UserRole.ROLE_USER.name());
        usuarioAutenticarDTO.setRememberMe(false);
    }

    private void setupUsuarioDTO() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("joao@email.com");
        usuarioDTO.setDataNascimento("15/05/1990");
        usuarioDTO.setTelefone("11987654321");
        usuarioDTO.setSenha("minhasenha123");
        usuarioDTO.setRole(UserRole.ROLE_USER.name());
    }

    private void setupUsuario() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
    }

    private void setupCheckTwoFactorRequest() {
        checkTwoFactorRequest = new HashMap<>();
        checkTwoFactorRequest.put("cpf", "12345678901");
    }

    // =================== TESTES DE LOGIN ===================

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() {
        // Arrange
        String token = "jwt.token.example";
        when(authService.login(usuarioAutenticarDTO)).thenReturn(token);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.login(usuarioAutenticarDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(token, responseBody.get("token"));
        assertEquals("Login realizado com sucesso", responseBody.get("message"));

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve realizar login com 2FA")
    void deveRealizarLoginCom2FA() {
        // Arrange
        usuarioAutenticarDTO.setTwoFactorCode(123456);
        String token = "jwt.token.example.2fa";
        when(authService.login(usuarioAutenticarDTO)).thenReturn(token);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.login(usuarioAutenticarDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(token, responseBody.get("token"));

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve realizar login com remember me")
    void deveRealizarLoginComRememberMe() {
        // Arrange
        usuarioAutenticarDTO.setRememberMe(true);
        String token = "jwt.token.extended";
        when(authService.login(usuarioAutenticarDTO)).thenReturn(token);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.login(usuarioAutenticarDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals(token, responseBody.get("token"));

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void deveLancarExcecaoQuandoCredenciaisSaoInvalidas() {
        // Arrange
        when(authService.login(usuarioAutenticarDTO))
                .thenThrow(new AuthenticationFailedException("Credenciais inválidas"));

        // Act & Assert
        assertThrows(AuthenticationFailedException.class, () -> {
            authenticationController.login(usuarioAutenticarDTO);
        });

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não é encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(authService.login(usuarioAutenticarDTO))
                .thenThrow(new UserNotFoundException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationController.login(usuarioAutenticarDTO);
        });

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário está inativo")
    void deveLancarExcecaoQuandoUsuarioEstaInativo() {
        // Arrange
        when(authService.login(usuarioAutenticarDTO))
                .thenThrow(new UserInactiveException("Usuário inativo"));

        // Act & Assert
        assertThrows(UserInactiveException.class, () -> {
            authenticationController.login(usuarioAutenticarDTO);
        });

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando 2FA é requerido")
    void deveLancarExcecaoQuando2FAERequerido() {
        // Arrange
        when(authService.login(usuarioAutenticarDTO))
                .thenThrow(new TwoFactorRequiredException("2FA requerido"));

        // Act & Assert
        assertThrows(TwoFactorRequiredException.class, () -> {
            authenticationController.login(usuarioAutenticarDTO);
        });

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando código 2FA é inválido")
    void deveLancarExcecaoQuandoCodigo2FAEInvalido() {
        // Arrange
        usuarioAutenticarDTO.setTwoFactorCode(999999);
        when(authService.login(usuarioAutenticarDTO))
                .thenThrow(new InvalidTwoFactorCodeException("Código 2FA inválido"));

        // Act & Assert
        assertThrows(InvalidTwoFactorCodeException.class, () -> {
            authenticationController.login(usuarioAutenticarDTO);
        });

        verify(authService).login(usuarioAutenticarDTO);
    }

    // =================== TESTES DE REGISTRO ===================

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        // Arrange
        when(usuarioService.criar(usuarioDTO)).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = authenticationController.register(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuario.getIdUsuario(), response.getBody().getIdUsuario());
        assertEquals(usuario.getNome(), response.getBody().getNome());
        assertEquals(usuario.getEmail(), response.getBody().getEmail());

        verify(usuarioService).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve registrar usuário profissional")
    void deveRegistrarUsuarioProfissional() {
        // Arrange
        usuarioDTO.setRole(UserRole.ROLE_PROF.name());
        usuario.setNome("Maria Tattoo");
        when(usuarioService.criar(usuarioDTO)).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = authenticationController.register(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuario.getNome(), response.getBody().getNome());

        verify(usuarioService).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar usuário com dados inválidos")
    void deveLancarExcecaoAoRegistrarUsuarioComDadosInvalidos() {
        // Arrange
        when(usuarioService.criar(usuarioDTO))
                .thenThrow(new RuntimeException("Dados do usuário inválidos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationController.register(usuarioDTO);
        });

        verify(usuarioService).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar usuário com email já existente")
    void deveLancarExcecaoAoRegistrarUsuarioComEmailJaExistente() {
        // Arrange
        when(usuarioService.criar(usuarioDTO))
                .thenThrow(new UsuarioException.EmailJaExisteException("Email já cadastrado"));

        // Act & Assert
        assertThrows(UsuarioException.EmailJaExisteException.class, () -> {
            authenticationController.register(usuarioDTO);
        });

        verify(usuarioService).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar usuário com CPF já existente")
    void deveLancarExcecaoAoRegistrarUsuarioComCPFJaExistente() {
        // Arrange
        when(usuarioService.criar(usuarioDTO))
                .thenThrow(new UsuarioException.CpfJaExisteException("CPF já cadastrado"));

        // Act & Assert
        assertThrows(UsuarioException.CpfJaExisteException.class, () -> {
            authenticationController.register(usuarioDTO);
        });

        verify(usuarioService).criar(usuarioDTO);
    }

    // =================== TESTES DE REAUTENTICAÇÃO ===================

    @Test
    @DisplayName("Deve reautenticar usuário com sucesso")
    void deveReautenticarUsuarioComSucesso() {
        // Arrange
        Long userId = 1L;
        String newToken = "jwt.new.token.example";
        when(authService.reautenticar(userId)).thenReturn(newToken);

        // Act
        ResponseEntity<String> response = authenticationController.reautenticar(userId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newToken, response.getBody());

        verify(authService).reautenticar(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reautenticar usuário inexistente")
    void deveLancarExcecaoAoReautenticarUsuarioInexistente() {
        // Arrange
        Long userId = 999L;
        when(authService.reautenticar(userId))
                .thenThrow(new UserNotFoundException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationController.reautenticar(userId);
        });

        verify(authService).reautenticar(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reautenticar usuário inativo")
    void deveLancarExcecaoAoReautenticarUsuarioInativo() {
        // Arrange
        Long userId = 1L;
        when(authService.reautenticar(userId))
                .thenThrow(new UserInactiveException("Usuário inativo"));

        // Act & Assert
        assertThrows(UserInactiveException.class, () -> {
            authenticationController.reautenticar(userId);
        });

        verify(authService).reautenticar(userId);
    }

    @Test
    @DisplayName("Deve reautenticar com diferentes tipos de usuário")
    void deveReautenticarComDiferentesTiposDeUsuario() {
        // Arrange
        Long professionalUserId = 2L;
        String professionalToken = "jwt.professional.token";
        when(authService.reautenticar(professionalUserId)).thenReturn(professionalToken);

        // Act
        ResponseEntity<String> response = authenticationController.reautenticar(professionalUserId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(professionalToken, response.getBody());

        verify(authService).reautenticar(professionalUserId);
    }

    // =================== TESTES DE VERIFICAÇÃO 2FA ===================

    @Test
    @DisplayName("Deve verificar requisito de 2FA com sucesso - requer 2FA")
    void deveVerificarRequisito2FAComSucessoRequer2FA() {
        // Arrange
        String cpf = "12345678901";
        when(authService.checkTwoFactorRequirement(cpf)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.checkTwoFactorRequirement(checkTwoFactorRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertTrue((Boolean) responseBody.get("requiresTwoFactor"));

        verify(authService).checkTwoFactorRequirement(cpf);
    }

    @Test
    @DisplayName("Deve verificar requisito de 2FA com sucesso - não requer 2FA")
    void deveVerificarRequisito2FAComSucessoNaoRequer2FA() {
        // Arrange
        String cpf = "12345678901";
        when(authService.checkTwoFactorRequirement(cpf)).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.checkTwoFactorRequirement(checkTwoFactorRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertFalse((Boolean) responseBody.get("requiresTwoFactor"));

        verify(authService).checkTwoFactorRequirement(cpf);
    }

    @Test
    @DisplayName("Deve verificar 2FA com CPF diferente")
    void deveVerificar2FAComCPFDiferente() {
        // Arrange
        String cpfDiferente = "98765432100";
        Map<String, String> requestDiferente = new HashMap<>();
        requestDiferente.put("cpf", cpfDiferente);
        
        when(authService.checkTwoFactorRequirement(cpfDiferente)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.checkTwoFactorRequirement(requestDiferente);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("requiresTwoFactor"));

        verify(authService).checkTwoFactorRequirement(cpfDiferente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar 2FA com usuário não encontrado")
    void deveLancarExcecaoAoVerificar2FAComUsuarioNaoEncontrado() {
        // Arrange
        String cpf = "12345678901";
        when(authService.checkTwoFactorRequirement(cpf))
                .thenThrow(new UserNotFoundException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationController.checkTwoFactorRequirement(checkTwoFactorRequest);
        });

        verify(authService).checkTwoFactorRequirement(cpf);
    }

    // =================== TESTES DE CENÁRIOS DE BORDA ===================

    @Test
    @DisplayName("Deve lidar com request de verificação 2FA com CPF nulo")
    void deveLidarComRequestVerificacao2FAComCPFNulo() {
        // Arrange
        Map<String, String> requestComCpfNulo = new HashMap<>();
        requestComCpfNulo.put("cpf", null);
        
        // O service deve lançar exceção quando CPF é null
        when(authService.checkTwoFactorRequirement(null))
                .thenThrow(new UserNotFoundException("CPF não pode ser nulo"));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationController.checkTwoFactorRequirement(requestComCpfNulo);
        });

        verify(authService).checkTwoFactorRequirement(null);
    }

    @Test
    @DisplayName("Deve lidar com request de verificação 2FA com CPF vazio")
    void deveLidarComRequestVerificacao2FAComCPFVazio() {
        // Arrange
        Map<String, String> requestComCpfVazio = new HashMap<>();
        requestComCpfVazio.put("cpf", "");
        
        // O service pode retornar false para CPF vazio ou lançar exceção
        when(authService.checkTwoFactorRequirement(""))
                .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.checkTwoFactorRequirement(requestComCpfVazio);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertFalse((Boolean) responseBody.get("requiresTwoFactor"));

        verify(authService).checkTwoFactorRequirement("");
    }

    @Test
    @DisplayName("Deve processar login com campos opcionais nulos")
    void deveProcessarLoginComCamposOpcionaisNulos() {
        // Arrange
        usuarioAutenticarDTO.setTwoFactorCode(null);
        usuarioAutenticarDTO.setRememberMe(null);
        String token = "jwt.token.simple";
        when(authService.login(usuarioAutenticarDTO)).thenReturn(token);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.login(usuarioAutenticarDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, response.getBody().get("token"));

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve processar registro com campos opcionais nulos")
    void deveProcessarRegistroComCamposOpcionaisNulos() {
        // Arrange
        usuarioDTO.setTelefone(null);
        usuarioDTO.setImagemPerfil(null);
        when(usuarioService.criar(usuarioDTO)).thenReturn(usuario);

        // Act
        ResponseEntity<Usuario> response = authenticationController.register(usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(usuarioService).criar(usuarioDTO);
    }

    @Test
    @DisplayName("Deve verificar estrutura completa da resposta de login")
    void deveVerificarEstruturaCompletaDaRespostaDeLogin() {
        // Arrange
        String token = "jwt.token.complete";
        when(authService.login(usuarioAutenticarDTO)).thenReturn(token);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.login(usuarioAutenticarDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(3, responseBody.size()); // success, token, message
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("token"));
        assertTrue(responseBody.containsKey("message"));
        
        assertEquals(true, responseBody.get("success"));
        assertEquals(token, responseBody.get("token"));
        assertEquals("Login realizado com sucesso", responseBody.get("message"));

        verify(authService).login(usuarioAutenticarDTO);
    }

    @Test
    @DisplayName("Deve verificar estrutura completa da resposta de verificação 2FA")
    void deveVerificarEstruturaCompletaDaRespostaDeVerificacao2FA() {
        // Arrange
        String cpf = "12345678901";
        when(authService.checkTwoFactorRequirement(cpf)).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                authenticationController.checkTwoFactorRequirement(checkTwoFactorRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(2, responseBody.size()); // success, requiresTwoFactor
        assertTrue(responseBody.containsKey("success"));
        assertTrue(responseBody.containsKey("requiresTwoFactor"));
        
        assertEquals(true, responseBody.get("success"));
        assertEquals(true, responseBody.get("requiresTwoFactor"));

        verify(authService).checkTwoFactorRequirement(cpf);
    }
}