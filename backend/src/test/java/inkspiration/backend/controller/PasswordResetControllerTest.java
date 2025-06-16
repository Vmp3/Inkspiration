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

import inkspiration.backend.dto.ForgotPasswordDTO;
import inkspiration.backend.dto.ResetPasswordDTO;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.PasswordResetService;
import inkspiration.backend.security.AuthorizationService;

@WebMvcTest(value = PasswordResetController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ForgotPasswordDTO forgotPasswordDTO;
    private ResetPasswordDTO resetPasswordDTO;

    @BeforeEach
    void setUp() {
        forgotPasswordDTO = new ForgotPasswordDTO();
        forgotPasswordDTO.setCpf("12345678901");

        resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setCpf("12345678901");
        resetPasswordDTO.setCode("123456");
        resetPasswordDTO.setNewPassword("newPassword123");
    }

    @Test
    @WithMockUser
    void testForgotPassword_Success() throws Exception {
        // Arrange
        when(passwordResetService.generatePasswordResetCode("12345678901"))
            .thenReturn("Código enviado com sucesso");

        // Act & Assert
        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Código enviado com sucesso"));

        verify(passwordResetService).generatePasswordResetCode("12345678901");
    }

    @Test
    @WithMockUser
    void testForgotPassword_RuntimeException() throws Exception {
        // Arrange
        when(passwordResetService.generatePasswordResetCode("12345678901"))
            .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado"));
    }



    @Test
    @WithMockUser
    void testResetPassword_Success() throws Exception {
        // Arrange
        doNothing().when(passwordResetService).resetPassword("12345678901", "123456", "newPassword123");

        // Act & Assert
        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso"));

        verify(passwordResetService).resetPassword("12345678901", "123456", "newPassword123");
    }

    @Test
    @WithMockUser
    void testResetPassword_RuntimeException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Código inválido")).when(passwordResetService)
            .resetPassword("12345678901", "123456", "newPassword123");

        // Act & Assert
        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Código inválido"));
    }


} 