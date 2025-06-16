package inkspiration.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.EmailVerificationRequest;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.EmailVerificationService;
import inkspiration.backend.service.UsuarioService;
import inkspiration.backend.security.AuthorizationService;

@WebMvcTest(value = EmailVerificationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class EmailVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioDTO usuarioDTO;
    private EmailVerificationRequest verificationRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Test User");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setSenha("password123");
        usuarioDTO.setDataNascimento("01/01/1990");

        verificationRequest = new EmailVerificationRequest();
        verificationRequest.setEmail("test@example.com");
        verificationRequest.setCode("123456");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setCpf("12345678901");
    }

    @Test
    @WithMockUser
    void testRequestEmailVerification_Success() throws Exception {
        // Arrange
        doNothing().when(emailVerificationService).requestEmailVerification(any(UsuarioDTO.class));

        // Act & Assert
        mockMvc.perform(post("/auth/request-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Email de verificação enviado com sucesso"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(emailVerificationService).requestEmailVerification(any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser
    void testRequestEmailVerification_Error() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Email já existe")).when(emailVerificationService)
            .requestEmailVerification(any(UsuarioDTO.class));

        // Act & Assert
        mockMvc.perform(post("/auth/request-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email já existe"));
    }

    @Test
    @WithMockUser
    void testVerifyEmail_Success() throws Exception {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUser("test@example.com", "123456"))
            .thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Conta criada com sucesso!"))
                .andExpect(jsonPath("$.usuario.idUsuario").value(1L))
                .andExpect(jsonPath("$.usuario.nome").value("Test User"));

        verify(emailVerificationService).verifyEmailAndCreateUser("test@example.com", "123456");
    }

    @Test
    @WithMockUser
    void testVerifyEmail_InvalidCode() throws Exception {
        // Arrange
        when(emailVerificationService.verifyEmailAndCreateUser("test@example.com", "123456"))
            .thenThrow(new RuntimeException("Código de verificação inválido"));

        // Act & Assert
        mockMvc.perform(post("/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Código de verificação inválido"));
    }

    @Test
    @WithMockUser
    void testResendVerificationCode_Success() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("email", "test@example.com");
        doNothing().when(emailVerificationService).resendVerificationCode("test@example.com");

        // Act & Assert
        mockMvc.perform(post("/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Código de verificação reenviado com sucesso"));

        verify(emailVerificationService).resendVerificationCode("test@example.com");
    }

    @Test
    @WithMockUser
    void testResendVerificationCode_EmptyEmail() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("email", "");

        // Act & Assert
        mockMvc.perform(post("/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email é obrigatório"));
    }

    @Test
    @WithMockUser
    void testResendVerificationCode_Error() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("email", "test@example.com");
        doThrow(new RuntimeException("Email não encontrado")).when(emailVerificationService)
            .resendVerificationCode("test@example.com");

        // Act & Assert
        mockMvc.perform(post("/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email não encontrado"));
    }

    @Test
    @WithMockUser
    void testResendVerificationCode_MissingEmail() throws Exception {
        // Arrange
        Map<String, String> request = Map.of("other", "value");

        // Act & Assert
        mockMvc.perform(post("/auth/resend-verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email é obrigatório"));
    }
} 