package inkspiration.backend.service.authenticationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.authentication.AuthenticationFailedException;
import inkspiration.backend.exception.authentication.InvalidTwoFactorCodeException;
import inkspiration.backend.exception.authentication.TwoFactorRequiredException;
import inkspiration.backend.exception.authentication.UserInactiveException;
import inkspiration.backend.exception.authentication.UserNotFoundException;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.security.AuthenticationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.service.UsuarioService;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService - Testes Unitários")
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private UsuarioAutenticarDTO loginDTO;
    private Usuario usuario;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        loginDTO = new UsuarioAutenticarDTO();
        loginDTO.setCpf("12345678900");
        loginDTO.setSenha("senha123");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf("12345678900");

        userDetails = new User(
            "12345678900",
            "senha123",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }

    @Test
    @DisplayName("Deve realizar login com sucesso quando 2FA não está habilitado")
    void deveRealizarLoginComSucessoQuando2FANaoHabilitado() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario()))
            .thenReturn(false);
        doReturn("token").when(jwtService).generateToken(any(Authentication.class), anyBoolean());

        // Act
        String token = authenticationService.login(loginDTO);

        // Assert
        assertNotNull(token);
        assertEquals("token", token);
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(usuarioService).buscarPorCpf(loginDTO.getCpf());
        verify(twoFactorAuthService).isTwoFactorEnabled(usuario.getIdUsuario());
        verify(jwtService).generateToken(any(Authentication.class), anyBoolean());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso quando 2FA está habilitado e código é válido")
    void deveRealizarLoginComSucessoQuando2FAHabilitadoECodigoValido() {
        // Arrange
        loginDTO.setTwoFactorCode(123456);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario()))
            .thenReturn(true);
        when(twoFactorAuthService.validateCode(usuario.getIdUsuario(), loginDTO.getTwoFactorCode()))
            .thenReturn(true);
        doReturn("token").when(jwtService).generateToken(any(Authentication.class), anyBoolean());

        // Act
        String token = authenticationService.login(loginDTO);

        // Assert
        assertNotNull(token);
        assertEquals("token", token);
        verify(twoFactorAuthService).validateCode(usuario.getIdUsuario(), loginDTO.getTwoFactorCode());
    }

    @Test
    @DisplayName("Deve lançar TwoFactorRequiredException quando 2FA habilitado e código não fornecido")
    void deveLancarTwoFactorRequiredExceptionQuando2FAHabilitadoECodigoNaoFornecido() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario()))
            .thenReturn(true);

        // Act & Assert
        TwoFactorRequiredException exception = assertThrows(
            TwoFactorRequiredException.class,
            () -> authenticationService.login(loginDTO)
        );
        assertEquals("Código de autenticação de dois fatores é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar InvalidTwoFactorCodeException quando código 2FA é inválido")
    void deveLancarInvalidTwoFactorCodeExceptionQuandoCodigo2FAInvalido() {
        // Arrange
        loginDTO.setTwoFactorCode(123456);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario()))
            .thenReturn(true);
        when(twoFactorAuthService.validateCode(usuario.getIdUsuario(), loginDTO.getTwoFactorCode()))
            .thenReturn(false);

        // Act & Assert
        InvalidTwoFactorCodeException exception = assertThrows(
            InvalidTwoFactorCodeException.class,
            () -> authenticationService.login(loginDTO)
        );
        assertEquals("Código de autenticação de dois fatores inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar AuthenticationFailedException quando credenciais são inválidas")
    void deveLancarAuthenticationFailedExceptionQuandoCredenciaisInvalidas() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
            AuthenticationFailedException.class,
            () -> authenticationService.login(loginDTO)
        );
        assertEquals("CPF ou senha incorretos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException quando usuário não é encontrado")
    void deveLancarUserNotFoundExceptionQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(null);

        // Act & Assert
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> authenticationService.login(loginDTO)
        );
        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar UserInactiveException quando usuário está inativo")
    void deveLancarUserInactiveExceptionQuandoUsuarioInativo() {
        // Arrange
        UserDetails inactiveUserDetails = new User(
            "12345678900",
            "senha123",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_DELETED"))
        );
        Authentication inactiveAuth = new UsernamePasswordAuthenticationToken(
            inactiveUserDetails,
            null,
            inactiveUserDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenReturn(inactiveAuth);

        // Act & Assert
        UserInactiveException exception = assertThrows(
            UserInactiveException.class,
            () -> authenticationService.login(loginDTO)
        );
        assertEquals("Usuário inativo ou deletado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar requisito de 2FA com sucesso")
    void deveVerificarRequisito2FAComSucesso() {
        // Arrange
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario()))
            .thenReturn(true);

        // Act
        boolean requiresTwoFactor = authenticationService.checkTwoFactorRequirement(loginDTO.getCpf());

        // Assert
        assertTrue(requiresTwoFactor);
        verify(usuarioService).buscarPorCpf(loginDTO.getCpf());
        verify(twoFactorAuthService).isTwoFactorEnabled(usuario.getIdUsuario());
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não existe ao verificar 2FA")
    void deveRetornarFalseQuandoUsuarioNaoExisteAoVerificar2FA() {
        // Arrange
        when(usuarioService.buscarPorCpf(loginDTO.getCpf()))
            .thenReturn(null);

        // Act
        boolean requiresTwoFactor = authenticationService.checkTwoFactorRequirement(loginDTO.getCpf());

        // Assert
        assertFalse(requiresTwoFactor);
        verify(usuarioService).buscarPorCpf(loginDTO.getCpf());
        verifyNoInteractions(twoFactorAuthService);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando CPF é nulo ao verificar 2FA")
    void deveLancarIllegalArgumentExceptionQuandoCPFNuloAoVerificar2FA() {
        // Arrange
        String cpfNulo = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authenticationService.checkTwoFactorRequirement(cpfNulo)
        );
        assertEquals("CPF não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve gerar token de autenticação com sucesso")
    void deveGerarTokenAutenticacaoComSucesso() {
        // Arrange
        when(jwtService.generateToken(authentication))
            .thenReturn("token");

        // Act
        String token = authenticationService.authenticate(authentication);

        // Assert
        assertEquals("token", token);
        verify(jwtService).generateToken(authentication);
    }

    @Test
    @DisplayName("Deve gerar token de autenticação com rememberMe")
    void deveGerarTokenAutenticacaoComRememberMe() {
        // Arrange
        when(jwtService.generateToken(authentication, true))
            .thenReturn("token_longo");

        // Act
        String token = authenticationService.authenticate(authentication, true);

        // Assert
        assertEquals("token_longo", token);
        verify(jwtService).generateToken(authentication, true);
    }

    @Test
    @DisplayName("Deve verificar se token está revogado")
    void deveVerificarSeTokenEstaRevogado() {
        // Arrange
        String token = "token";
        when(tokenRevogadoRepository.existsByToken(token))
            .thenReturn(true);

        // Act
        boolean isRevoked = authenticationService.isTokenRevoked(token);

        // Assert
        assertTrue(isRevoked);
        verify(tokenRevogadoRepository).existsByToken(token);
    }
} 