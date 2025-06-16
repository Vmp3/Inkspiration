package inkspiration.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.EmailService;

@WebMvcTest(value = TwoFactorAuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class TwoFactorAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TwoFactorAuthService twoFactorAuthService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader = "Bearer test-token";
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        // Mock JWT service para retornar valores válidos
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(userId);
    }

    @Test
    @WithMockUser
    void testGenerateQRCode_Success() throws Exception {
        // Arrange
        Map<String, String> qrData = Map.of(
            "qrCode", "data:image/png;base64,test",
            "secretKey", "TEST123456",
            "issuer", "Inkspiration",
            "accountName", "user@test.com",
            "otpAuthUrl", "otpauth://totp/..."
        );
        when(twoFactorAuthService.generateQRCodeAndSecret(userId)).thenReturn(qrData);

        // Act & Assert
        mockMvc.perform(post("/two-factor/generate-qr")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.qrCode").value("data:image/png;base64,test"))
                .andExpect(jsonPath("$.secretKey").value("TEST123456"));

        verify(twoFactorAuthService).generateQRCodeAndSecret(userId);
    }

    @Test
    @WithMockUser
    void testGenerateQRCode_Error() throws Exception {
        // Arrange
        when(twoFactorAuthService.generateQRCodeAndSecret(userId))
            .thenThrow(new RuntimeException("QR generation failed"));

        // Act & Assert
        mockMvc.perform(post("/two-factor/generate-qr")
                .header("Authorization", authHeader))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Erro ao gerar QR Code: QR generation failed"));
    }

    @Test
    @WithMockUser
    void testEnableTwoFactor_Success() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.enableTwoFactor(userId, 123456)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/two-factor/enable")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Autenticação de dois fatores ativada com sucesso"));

        verify(twoFactorAuthService).enableTwoFactor(userId, 123456);
    }

    @Test
    @WithMockUser
    void testEnableTwoFactor_InvalidCode() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.enableTwoFactor(userId, 123456)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/two-factor/enable")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Código de verificação inválido"));
    }

    @Test
    @WithMockUser
    void testEnableTwoFactor_MissingCode() throws Exception {
        // Arrange - enviando JSON vazio para simular campo obrigatório faltante
        Map<String, Object> request = Map.of();

        // Act & Assert
        mockMvc.perform(post("/two-factor/enable")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Código de verificação é obrigatório"));
    }

    @Test
    @WithMockUser
    void testDisableTwoFactor_Success() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.disableTwoFactor(userId, 123456)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/two-factor/disable")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Autenticação de dois fatores desativada com sucesso"));

        verify(twoFactorAuthService).disableTwoFactor(userId, 123456);
    }

    @Test
    @WithMockUser
    void testDisableTwoFactor_InvalidCode() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.disableTwoFactor(userId, 123456)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/two-factor/disable")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Código de verificação inválido"));
    }

    @Test
    @WithMockUser
    void testGetTwoFactorStatus_Enabled() throws Exception {
        // Arrange
        when(twoFactorAuthService.isTwoFactorEnabled(userId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/two-factor/status")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.enabled").value(true));

        verify(twoFactorAuthService).isTwoFactorEnabled(userId);
    }

    @Test
    @WithMockUser
    void testGetTwoFactorStatus_Disabled() throws Exception {
        // Arrange
        when(twoFactorAuthService.isTwoFactorEnabled(userId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/two-factor/status")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    @WithMockUser
    void testValidateCode_Success() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.validateCode(userId, 123456)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/two-factor/validate")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.valid").value(true));

        verify(twoFactorAuthService).validateCode(userId, 123456);
    }

    @Test
    @WithMockUser
    void testValidateCode_Invalid() throws Exception {
        // Arrange
        Map<String, Integer> request = Map.of("code", 123456);
        when(twoFactorAuthService.validateCode(userId, 123456)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/two-factor/validate")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @WithMockUser
    void testSendRecoveryCode_Success() throws Exception {
        // Arrange
        when(twoFactorAuthService.sendRecoveryCodeByEmail(userId)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/two-factor/send-recovery-code")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(twoFactorAuthService).sendRecoveryCodeByEmail(userId);
    }

    @Test
    @WithMockUser
    void testError_WithInvalidToken() throws Exception {
        // Arrange
        when(jwtService.getUserIdFromToken("invalid-token"))
            .thenThrow(new RuntimeException("Invalid token"));

        // Act & Assert
        mockMvc.perform(post("/two-factor/generate-qr")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
} 