package inkspiration.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.service.UsuarioService;

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
        usuario.setRole("ROLE_USER");

        userDetails = User.builder()
            .username("12345678900")
            .password("senha123")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .build();

        authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("Deve realizar login com sucesso sem 2FA")
    void deveRealizarLoginComSucessoSem2FA() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf())).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario())).thenReturn(false);
        when(jwtService.generateToken(any(Authentication.class), anyBoolean())).thenReturn("token");

        // Act
        String token = authenticationService.login(loginDTO);

        // Assert
        assertNotNull(token);
        assertEquals("token", token);
        verify(usuarioService).salvar(usuario);
    }

    @Test
    @DisplayName("Deve realizar login com sucesso com 2FA")
    void deveRealizarLoginComSucessoCom2FA() {
        // Arrange
        loginDTO.setTwoFactorCode(123456);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf())).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario())).thenReturn(true);
        when(twoFactorAuthService.validateCode(eq(usuario.getIdUsuario()), eq(123456))).thenReturn(true);
        when(jwtService.generateToken(any(Authentication.class), anyBoolean())).thenReturn("token");

        // Act
        String token = authenticationService.login(loginDTO);

        // Assert
        assertNotNull(token);
        assertEquals("token", token);
        verify(usuarioService).salvar(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void deveLancarExcecaoQuandoCredenciaisInvalidas() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class)))
            .thenThrow(new AuthenticationFailedException("CPF ou senha incorretos"));

        // Act & Assert
        assertThrows(AuthenticationFailedException.class, () -> authenticationService.login(loginDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário está inativo")
    void deveLancarExcecaoQuandoUsuarioInativo() {
        // Arrange
        UserDetails userDetailsInativo = User.builder()
            .username("12345678900")
            .password("senha123")
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_DELETED")))
            .build();

        Authentication authInativo = new UsernamePasswordAuthenticationToken(
            userDetailsInativo, null, userDetailsInativo.getAuthorities());

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authInativo);

        // Act & Assert
        assertThrows(UserInactiveException.class, () -> authenticationService.login(loginDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando 2FA é requerido mas não fornecido")
    void deveLancarExcecaoQuando2FARequeridoMasNaoFornecido() {
        // Arrange
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf())).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario())).thenReturn(true);

        // Act & Assert
        assertThrows(TwoFactorRequiredException.class, () -> authenticationService.login(loginDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando código 2FA é inválido")
    void deveLancarExcecaoQuandoCodigo2FAInvalido() {
        // Arrange
        loginDTO.setTwoFactorCode(123456);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(usuarioService.buscarPorCpf(loginDTO.getCpf())).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario())).thenReturn(true);
        when(twoFactorAuthService.validateCode(eq(usuario.getIdUsuario()), eq(123456))).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTwoFactorCodeException.class, () -> authenticationService.login(loginDTO));
    }

    @Test
    @DisplayName("Deve reautenticar usuário com sucesso")
    void deveReautenticarUsuarioComSucesso() {
        // Arrange
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("novoToken");

        // Act
        String novoToken = authenticationService.reautenticar(1L);

        // Assert
        assertNotNull(novoToken);
        assertEquals("novoToken", novoToken);
        verify(usuarioService).salvar(usuario);
    }

    @Test
    @DisplayName("Deve verificar requisito 2FA corretamente")
    void deveVerificarRequisito2FACorretamente() {
        // Arrange
        when(usuarioService.buscarPorCpf("12345678900")).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario())).thenReturn(true);

        // Act
        boolean requires2FA = authenticationService.checkTwoFactorRequirement("12345678900");

        // Assert
        assertTrue(requires2FA);
    }

    @Test
    @DisplayName("Deve revogar token corretamente")
    void deveRevogarTokenCorretamente() {
        // Arrange
        String token = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9";
        ArgumentCaptor<TokenRevogado> tokenRevogadoCaptor = ArgumentCaptor.forClass(TokenRevogado.class);

        // Act
        authenticationService.revogarToken(token);

        // Assert
        verify(tokenRevogadoRepository).save(tokenRevogadoCaptor.capture());
        TokenRevogado tokenRevogadoSalvo = tokenRevogadoCaptor.getValue();
        assertEquals(token, tokenRevogadoSalvo.getToken());
    }

    @Test
    @DisplayName("Deve verificar se token está revogado")
    void deveVerificarSeTokenEstaRevogado() {
        // Arrange
        String token = "token";
        when(tokenRevogadoRepository.existsByToken(token)).thenReturn(true);

        // Act
        boolean isRevoked = authenticationService.isTokenRevoked(token);

        // Assert
        assertTrue(isRevoked);
        verify(tokenRevogadoRepository).existsByToken(token);
    }
} 