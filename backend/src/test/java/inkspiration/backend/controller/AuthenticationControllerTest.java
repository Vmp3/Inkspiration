package inkspiration.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthenticationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.service.UsuarioService;
import inkspiration.backend.security.AuthorizationService;

@WebMvcTest(value = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TwoFactorAuthService twoFactorAuthService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioAutenticarDTO loginDTO;
    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        loginDTO = new UsuarioAutenticarDTO();
        loginDTO.setCpf("12345678901");
        loginDTO.setSenha("password123");
        loginDTO.setRememberMe(false);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Test User");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setSenha("password123");
        usuarioDTO.setDataNascimento("01/01/1990");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf("12345678901");
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setRole("ROLE_USER");
    }

    @Test
    @WithMockUser
    void testLogin_Success_WithoutTwoFactor() throws Exception {
        // Arrange
        User userDetails = new User("12345678901", "password123", 
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(usuarioService.buscarPorCpf("12345678901")).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(1L)).thenReturn(false);
        when(authService.authenticate(any(Authentication.class), anyBoolean())).thenReturn("mock-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.message").value("Login realizado com sucesso"));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioService).buscarPorCpf("12345678901");
        verify(twoFactorAuthService).isTwoFactorEnabled(1L);
        verify(authService).authenticate(any(Authentication.class), eq(false));
    }

    @Test
    @WithMockUser
    void testLogin_DeletedUser() throws Exception {
        // Arrange
        User userDetails = new User("12345678901", "password123", 
            List.of(new SimpleGrantedAuthority("ROLE_DELETED")));
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Usuário inativo ou deletado"));
    }

    @Test
    @WithMockUser
    void testLogin_TwoFactorRequired() throws Exception {
        // Arrange
        User userDetails = new User("12345678901", "password123", 
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(usuarioService.buscarPorCpf("12345678901")).thenReturn(usuario);
        when(twoFactorAuthService.isTwoFactorEnabled(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().is(428))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.requiresTwoFactor").value(true));
    }

    @Test
    @WithMockUser
    void testRegister_Success() throws Exception {
        // Arrange
        when(usuarioService.criar(any(UsuarioDTO.class))).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nome").value("Test User"));

        verify(usuarioService).criar(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser
    void testRefreshToken_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(usuarioService.atualizarTokenUsuario(userId)).thenReturn("new-token");

        // Act & Assert
        mockMvc.perform(post("/auth/refresh-token/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("new-token"));

        verify(usuarioService).atualizarTokenUsuario(userId);
    }

    @Test
    @WithMockUser
    void testReautenticar_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        usuario.setRole("ROLE_ADMIN");
        when(usuarioService.buscarPorId(userId)).thenReturn(usuario);
        when(authService.authenticate(any(Authentication.class))).thenReturn("new-token");

        // Act & Assert
        mockMvc.perform(post("/auth/reauth/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("new-token"));

        verify(usuarioService).buscarPorId(userId);
        verify(authService).authenticate(any(Authentication.class));
    }

    @Test
    @WithMockUser
    void testCheckTwoFactorRequirement_EmptyCpf() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("cpf", "");

        // Act & Assert
        mockMvc.perform(post("/auth/check-2fa")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("CPF é obrigatório"));
    }
} 