package inkspiration.backend.controller.twoFactorAuthController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import inkspiration.backend.controller.TwoFactorAuthController;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.exception.twofactor.*;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayName("TwoFactorAuthController - Testes Completos")
class TwoFactorAuthControllerTest {

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @InjectMocks
    private TwoFactorAuthController twoFactorAuthController;

    private final String AUTH_HEADER = "Bearer token123";
    private final Integer VERIFICATION_CODE = 123456;
    private final String RECOVERY_CODE = "123456";

    private Map<String, String> mockQrData;
    private Map<String, Integer> mockRequestWithCode;
    private Map<String, String> mockRequestWithRecoveryCode;

    @BeforeEach
    void setUp() {
        // Setup mock QR data
        mockQrData = new HashMap<>();
        mockQrData.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...");
        mockQrData.put("secretKey", "JBSWY3DPEHPK3PXP");
        mockQrData.put("issuer", "Inkspiration");
        mockQrData.put("accountName", "teste@email.com");
        mockQrData.put("otpAuthUrl", "otpauth://totp/Inkspiration:teste@email.com?secret=JBSWY3DPEHPK3PXP&issuer=Inkspiration");

        // Setup mock request with verification code
        mockRequestWithCode = new HashMap<>();
        mockRequestWithCode.put("code", VERIFICATION_CODE);

        // Setup mock request with recovery code
        mockRequestWithRecoveryCode = new HashMap<>();
        mockRequestWithRecoveryCode.put("recoveryCode", RECOVERY_CODE);
    }

    // Testes para generateQRCode
    @Test
    @DisplayName("Deve gerar QR code com sucesso")
    void deveGerarQRCodeComSucesso() {
        // Arrange
        when(twoFactorAuthService.gerarQRCodeComValidacao(AUTH_HEADER))
            .thenReturn(mockQrData);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.generateQRCode(AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...", responseBody.get("qrCode"));
        assertEquals("JBSWY3DPEHPK3PXP", responseBody.get("secretKey"));
        assertEquals("Inkspiration", responseBody.get("issuer"));
        assertEquals("teste@email.com", responseBody.get("accountName"));
        assertEquals("otpauth://totp/Inkspiration:teste@email.com?secret=JBSWY3DPEHPK3PXP&issuer=Inkspiration", responseBody.get("otpAuthUrl"));
        assertEquals("QR Code gerado com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).gerarQRCodeComValidacao(AUTH_HEADER);
    }

    @Test
    @DisplayName("Deve propagar exceção ao gerar QR code")
    void devePropagarExcecaoAoGerarQRCode() {
        // Arrange
        when(twoFactorAuthService.gerarQRCodeComValidacao(AUTH_HEADER))
            .thenThrow(new QRCodeGeracaoException("Erro ao gerar QR code"));

        // Act & Assert
        QRCodeGeracaoException exception = assertThrows(QRCodeGeracaoException.class, () -> {
            twoFactorAuthController.generateQRCode(AUTH_HEADER);
        });

        assertEquals("Erro ao gerar QR code", exception.getMessage());
        verify(twoFactorAuthService).gerarQRCodeComValidacao(AUTH_HEADER);
    }

    // Testes para enableTwoFactor
    @Test
    @DisplayName("Deve ativar 2FA com sucesso")
    void deveAtivar2FAComSucesso() {
        // Arrange
        when(twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.enableTwoFactor(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores ativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve retornar false ao falhar ativação do 2FA")
    void deveRetornarFalseAoFalharAtivacao2FA() {
        // Arrange
        when(twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.enableTwoFactor(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores ativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve propagar exceção ao ativar 2FA")
    void devePropagarExcecaoAoAtivar2FA() {
        // Arrange
        when(twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenThrow(new CodigoVerificacaoInvalidoException("Código inválido"));

        // Act & Assert
        CodigoVerificacaoInvalidoException exception = assertThrows(CodigoVerificacaoInvalidoException.class, () -> {
            twoFactorAuthController.enableTwoFactor(AUTH_HEADER, mockRequestWithCode);
        });

        assertEquals("Código inválido", exception.getMessage());
        verify(twoFactorAuthService).ativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    // Testes para disableTwoFactor
    @Test
    @DisplayName("Deve desativar 2FA com sucesso")
    void deveDesativar2FAComSucesso() {
        // Arrange
        when(twoFactorAuthService.desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.disableTwoFactor(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores desativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve retornar false ao falhar desativação do 2FA")
    void deveRetornarFalseAoFalharDesativacao2FA() {
        // Arrange
        when(twoFactorAuthService.desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.disableTwoFactor(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores desativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve propagar exceção ao desativar 2FA")
    void devePropagarExcecaoAoDesativar2FA() {
        // Arrange
        when(twoFactorAuthService.desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenThrow(new TwoFactorDesativacaoException("Erro ao desativar"));

        // Act & Assert
        TwoFactorDesativacaoException exception = assertThrows(TwoFactorDesativacaoException.class, () -> {
            twoFactorAuthController.disableTwoFactor(AUTH_HEADER, mockRequestWithCode);
        });

        assertEquals("Erro ao desativar", exception.getMessage());
        verify(twoFactorAuthService).desativarTwoFactorComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    // Testes para getTwoFactorStatus
    @Test
    @DisplayName("Deve obter status 2FA habilitado")
    void deveObterStatus2FAHabilitado() {
        // Arrange
        when(twoFactorAuthService.obterStatusTwoFactorComValidacao(AUTH_HEADER))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.getTwoFactorStatus(AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(true, responseBody.get("enabled"));
        
        verify(twoFactorAuthService).obterStatusTwoFactorComValidacao(AUTH_HEADER);
    }

    @Test
    @DisplayName("Deve obter status 2FA desabilitado")
    void deveObterStatus2FADesabilitado() {
        // Arrange
        when(twoFactorAuthService.obterStatusTwoFactorComValidacao(AUTH_HEADER))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.getTwoFactorStatus(AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(false, responseBody.get("enabled"));
        
        verify(twoFactorAuthService).obterStatusTwoFactorComValidacao(AUTH_HEADER);
    }

    @Test
    @DisplayName("Deve propagar exceção ao obter status 2FA")
    void devePropagarExcecaoAoObterStatus2FA() {
        // Arrange
        when(twoFactorAuthService.obterStatusTwoFactorComValidacao(AUTH_HEADER))
            .thenThrow(new TwoFactorStatusException("Erro ao obter status"));

        // Act & Assert
        TwoFactorStatusException exception = assertThrows(TwoFactorStatusException.class, () -> {
            twoFactorAuthController.getTwoFactorStatus(AUTH_HEADER);
        });

        assertEquals("Erro ao obter status", exception.getMessage());
        verify(twoFactorAuthService).obterStatusTwoFactorComValidacao(AUTH_HEADER);
    }

    // Testes para validateCode
    @Test
    @DisplayName("Deve validar código com sucesso")
    void deveValidarCodigoComSucesso() {
        // Arrange
        when(twoFactorAuthService.validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.validateCode(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(true, responseBody.get("valid"));
        
        verify(twoFactorAuthService).validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve retornar código inválido")
    void deveRetornarCodigoInvalido() {
        // Arrange
        when(twoFactorAuthService.validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.validateCode(AUTH_HEADER, mockRequestWithCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals(false, responseBody.get("valid"));
        
        verify(twoFactorAuthService).validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    @Test
    @DisplayName("Deve propagar exceção ao validar código")
    void devePropagarExcecaoAoValidarCodigo() {
        // Arrange
        when(twoFactorAuthService.validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE))
            .thenThrow(new CodigoVerificacaoInvalidoException("Erro na validação"));

        // Act & Assert
        CodigoVerificacaoInvalidoException exception = assertThrows(CodigoVerificacaoInvalidoException.class, () -> {
            twoFactorAuthController.validateCode(AUTH_HEADER, mockRequestWithCode);
        });

        assertEquals("Erro na validação", exception.getMessage());
        verify(twoFactorAuthService).validarCodigoComValidacao(AUTH_HEADER, VERIFICATION_CODE);
    }

    // Testes para sendRecoveryCode
    @Test
    @DisplayName("Deve enviar código de recuperação com sucesso")
    void deveEnviarCodigoRecuperacaoComSucesso() {
        // Arrange
        when(twoFactorAuthService.enviarCodigoRecuperacaoComValidacao(AUTH_HEADER))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.sendRecoveryCode(AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Código de recuperação enviado para seu email", responseBody.get("message"));
        
        verify(twoFactorAuthService).enviarCodigoRecuperacaoComValidacao(AUTH_HEADER);
    }

    @Test
    @DisplayName("Deve retornar false ao falhar envio de código de recuperação")
    void deveRetornarFalseAoFalharEnvioCodigoRecuperacao() {
        // Arrange
        when(twoFactorAuthService.enviarCodigoRecuperacaoComValidacao(AUTH_HEADER))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.sendRecoveryCode(AUTH_HEADER);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Código de recuperação enviado para seu email", responseBody.get("message"));
        
        verify(twoFactorAuthService).enviarCodigoRecuperacaoComValidacao(AUTH_HEADER);
    }

    // Testes para disableWithRecoveryCode
    @Test
    @DisplayName("Deve desativar 2FA com código de recuperação com sucesso")
    void deveDesativar2FAComCodigoRecuperacaoComSucesso() {
        // Arrange
        when(twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE))
            .thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.disableWithRecoveryCode(AUTH_HEADER, mockRequestWithRecoveryCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores desativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE);
    }

    @Test
    @DisplayName("Deve retornar false ao falhar desativação com código de recuperação")
    void deveRetornarFalseAoFalharDesativacaoComCodigoRecuperacao() {
        // Arrange
        when(twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE))
            .thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = twoFactorAuthController.disableWithRecoveryCode(AUTH_HEADER, mockRequestWithRecoveryCode);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("success"));
        assertEquals("Autenticação de dois fatores desativada com sucesso", responseBody.get("message"));
        
        verify(twoFactorAuthService).desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE);
    }

    @Test
    @DisplayName("Deve propagar exceção ao desativar com código de recuperação")
    void devePropagarExcecaoAoDesativarComCodigoRecuperacao() {
        // Arrange
        when(twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE))
            .thenThrow(new CodigoRecuperacaoException("Código de recuperação inválido"));

        // Act & Assert
        CodigoRecuperacaoException exception = assertThrows(CodigoRecuperacaoException.class, () -> {
            twoFactorAuthController.disableWithRecoveryCode(AUTH_HEADER, mockRequestWithRecoveryCode);
        });

        assertEquals("Código de recuperação inválido", exception.getMessage());
        verify(twoFactorAuthService).desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE);
    }

    // Testes para casos edge - valores nulos nos maps de request
    @Test
    @DisplayName("Deve lidar com código nulo na ativação")
    void deveLidarComCodigoNuloNaAtivacao() {
        // Arrange
        Map<String, Integer> requestWithNullCode = new HashMap<>();
        requestWithNullCode.put("code", null);
        
        when(twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, null))
            .thenThrow(new CodigoVerificacaoInvalidoException("Código é obrigatório"));

        // Act & Assert
        CodigoVerificacaoInvalidoException exception = assertThrows(CodigoVerificacaoInvalidoException.class, () -> {
            twoFactorAuthController.enableTwoFactor(AUTH_HEADER, requestWithNullCode);
        });

        assertEquals("Código é obrigatório", exception.getMessage());
        verify(twoFactorAuthService).ativarTwoFactorComValidacao(AUTH_HEADER, null);
    }

    @Test
    @DisplayName("Deve lidar com código nulo na desativação")
    void deveLidarComCodigoNuloNaDesativacao() {
        // Arrange
        Map<String, Integer> requestWithNullCode = new HashMap<>();
        requestWithNullCode.put("code", null);
        
        when(twoFactorAuthService.desativarTwoFactorComValidacao(AUTH_HEADER, null))
            .thenThrow(new CodigoVerificacaoInvalidoException("Código é obrigatório"));

        // Act & Assert
        CodigoVerificacaoInvalidoException exception = assertThrows(CodigoVerificacaoInvalidoException.class, () -> {
            twoFactorAuthController.disableTwoFactor(AUTH_HEADER, requestWithNullCode);
        });

        assertEquals("Código é obrigatório", exception.getMessage());
        verify(twoFactorAuthService).desativarTwoFactorComValidacao(AUTH_HEADER, null);
    }

    @Test
    @DisplayName("Deve lidar com código nulo na validação")
    void deveLidarComCodigoNuloNaValidacao() {
        // Arrange
        Map<String, Integer> requestWithNullCode = new HashMap<>();
        requestWithNullCode.put("code", null);
        
        when(twoFactorAuthService.validarCodigoComValidacao(AUTH_HEADER, null))
            .thenThrow(new CodigoVerificacaoInvalidoException("Código é obrigatório"));

        // Act & Assert
        CodigoVerificacaoInvalidoException exception = assertThrows(CodigoVerificacaoInvalidoException.class, () -> {
            twoFactorAuthController.validateCode(AUTH_HEADER, requestWithNullCode);
        });

        assertEquals("Código é obrigatório", exception.getMessage());
        verify(twoFactorAuthService).validarCodigoComValidacao(AUTH_HEADER, null);
    }

    @Test
    @DisplayName("Deve lidar com código de recuperação nulo")
    void deveLidarComCodigoRecuperacaoNulo() {
        // Arrange
        Map<String, String> requestWithNullRecoveryCode = new HashMap<>();
        requestWithNullRecoveryCode.put("recoveryCode", null);
        
        when(twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, null))
            .thenThrow(new CodigoRecuperacaoException("Código de recuperação é obrigatório"));

        // Act & Assert
        CodigoRecuperacaoException exception = assertThrows(CodigoRecuperacaoException.class, () -> {
            twoFactorAuthController.disableWithRecoveryCode(AUTH_HEADER, requestWithNullRecoveryCode);
        });

        assertEquals("Código de recuperação é obrigatório", exception.getMessage());
        verify(twoFactorAuthService).desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, null);
    }
} 