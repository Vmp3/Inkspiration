package inkspiration.backend.service.twoFactorAuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.TwoFactorRecoveryCode;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.repository.TwoFactorRecoveryCodeRepository;
import inkspiration.backend.service.EmailService;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.security.JwtService;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TwoFactorAuthService - Testes Core")
class TwoFactorAuthServiceCoreTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TwoFactorRecoveryCodeRepository twoFactorRecoveryCodeRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    // Instância real do GoogleAuthenticator para gerar códigos válidos
    private GoogleAuthenticator googleAuth = new GoogleAuthenticator();

    private Usuario usuario;
    private final Long USER_ID = 1L;
    private final String EMAIL = "teste@email.com";
    private final String NOME = "Usuário Teste";
    private final String SECRET_KEY = "JBSWY3DPEHPK3PXP";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(USER_ID);
        usuario.setEmail(EMAIL);
        usuario.setNome(NOME);
        usuario.setTwoFactorEnabled(false);
        usuario.setTwoFactorSecret(null);
    }

    // Método auxiliar para gerar código válido
    private int getValidCode(String secretKey) {
        return googleAuth.getTotpPassword(secretKey);
    }

    @Test
    @DisplayName("Deve gerar QR code e secret key com sucesso")
    void deveGerarQRCodeESecretKeyComSucesso() throws Exception {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        Map<String, String> resultado = twoFactorAuthService.generateQRCodeAndSecret(USER_ID);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("qrCode"));
        assertTrue(resultado.containsKey("secretKey"));
        assertTrue(resultado.containsKey("issuer"));
        assertTrue(resultado.containsKey("accountName"));
        assertTrue(resultado.containsKey("otpAuthUrl"));
        
        assertEquals("Inkspiration", resultado.get("issuer"));
        assertEquals(EMAIL, resultado.get("accountName"));
        assertTrue(resultado.get("qrCode").startsWith("data:image/png;base64,"));
        assertTrue(resultado.get("otpAuthUrl").startsWith("otpauth://totp/"));
        
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar QR code para usuário inexistente")
    void deveLancarExcecaoAoGerarQRCodeParaUsuarioInexistente() {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> twoFactorAuthService.generateQRCodeAndSecret(USER_ID)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve gerar QR code legacy com sucesso")
    void deveGerarQRCodeLegacyComSucesso() throws Exception {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        String qrCode = twoFactorAuthService.generateQRCode(USER_ID);

        // Assert
        assertNotNull(qrCode);
        assertTrue(qrCode.startsWith("data:image/png;base64,"));
        
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve ativar 2FA com código válido")
    void deveAtivar2FAComCodigoValido() {
        // Arrange
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao ativar 2FA com código inválido")
    void deveFalharAoAtivar2FAComCodigoInvalido() {
        // Arrange
        usuario.setTwoFactorSecret(SECRET_KEY);
        int invalidCode = 999999; // Código claramente inválido
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, invalidCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao ativar 2FA para usuário inexistente")
    void deveFalharAoAtivar2FAParaUsuarioInexistente() {
        // Arrange
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        // Act
        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve falhar ao ativar 2FA sem secret key")
    void deveFalharAoAtivar2FASemSecretKey() {
        // Arrange
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve desativar 2FA com código válido")
    void deveDesativar2FAComCodigoValido() {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        boolean resultado = twoFactorAuthService.disableTwoFactor(USER_ID, validCode);

        // Assert
        assertTrue(resultado);
        assertFalse(usuario.getTwoFactorEnabled());
        assertNull(usuario.getTwoFactorSecret());
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao desativar 2FA com código inválido")
    void deveFalharAoDesativar2FAComCodigoInvalido() {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int invalidCode = 999999; // Código claramente inválido
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.disableTwoFactor(USER_ID, invalidCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao desativar 2FA quando não está habilitado")
    void deveFalharAoDesativar2FAQuandoNaoEstaHabilitado() {
        // Arrange
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.disableTwoFactor(USER_ID, validCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve validar código com sucesso")
    void deveValidarCodigoComSucesso() {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.validateCode(USER_ID, validCode);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve falhar ao validar código inválido")
    void deveFalharAoValidarCodigoInvalido() {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int invalidCode = 999999; // Código claramente inválido
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.validateCode(USER_ID, invalidCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve retornar false para validação quando 2FA não está habilitado")
    void deveRetornarFalseParaValidacaoQuando2FANaoEstaHabilitado() {
        // Arrange
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.validateCode(USER_ID, validCode);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve verificar se 2FA está habilitado")
    void deveVerificarSe2FAEstaHabilitado() {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(USER_ID);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve retornar false quando 2FA não está habilitado")
    void deveRetornarFalseQuando2FANaoEstaHabilitado() {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(USER_ID);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve retornar false para usuário inexistente ao verificar status 2FA")
    void deveRetornarFalseParaUsuarioInexistenteAoVerificarStatus2FA() {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        // Act
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(USER_ID);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve enviar código de recuperação por email com sucesso")
    void deveEnviarCodigoRecuperacaoPorEmailComSucesso() throws Exception {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(new TwoFactorRecoveryCode());
        doNothing().when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        // Act
        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).findById(USER_ID);
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
        verify(twoFactorRecoveryCodeRepository).save(any(TwoFactorRecoveryCode.class));
        verify(emailService).sendTwoFactorRecoveryCode(eq(EMAIL), eq(NOME), anyString());
    }

    @Test
    @DisplayName("Deve falhar ao enviar código de recuperação quando 2FA não está habilitado")
    void deveFalharAoEnviarCodigoRecuperacaoQuando2FANaoEstaHabilitado() {
        // Arrange
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository).findById(USER_ID);
        verify(twoFactorRecoveryCodeRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Deve remover código quando falha ao enviar email")
    void deveRemoverCodigoQuandoFalhaAoEnviarEmail() throws Exception {
        // Arrange
        usuario.setTwoFactorEnabled(true);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        doThrow(new RuntimeException("Erro ao enviar email"))
            .when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        // Act
        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        // Assert
        assertFalse(resultado);
        // Verify que foi chamado deleteByUserId, save e delete
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
        verify(twoFactorRecoveryCodeRepository).save(any(TwoFactorRecoveryCode.class));
        verify(twoFactorRecoveryCodeRepository).delete(any(TwoFactorRecoveryCode.class));
    }

    @Test
    @DisplayName("Deve validar código de recuperação com sucesso")
    void deveValidarCodigoRecuperacaoComSucesso() {
        // Arrange
        String codigo = "123456";
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, codigo, LocalDateTime.now().plusMinutes(10));
        
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, codigo))
            .thenReturn(Optional.of(recoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(recoveryCode);

        // Act
        boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, codigo);

        // Assert
        assertTrue(resultado);
        assertTrue(recoveryCode.isUsed());
        verify(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        verify(twoFactorRecoveryCodeRepository).findByUserIdAndCodeAndUsedFalse(USER_ID, codigo);
        verify(twoFactorRecoveryCodeRepository).save(recoveryCode);
    }

    @Test
    @DisplayName("Deve falhar ao validar código de recuperação inexistente")
    void deveFalharAoValidarCodigoRecuperacaoInexistente() {
        // Arrange
        String codigo = "123456";
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, codigo))
            .thenReturn(Optional.empty());

        // Act
        boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, codigo);

        // Assert
        assertFalse(resultado);
        verify(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        verify(twoFactorRecoveryCodeRepository).findByUserIdAndCodeAndUsedFalse(USER_ID, codigo);
    }

    @Test
    @DisplayName("Deve falhar ao validar código de recuperação expirado")
    void deveFalharAoValidarCodigoRecuperacaoExpirado() {
        // Arrange
        String codigo = "123456";
        LocalDateTime pastTime = LocalDateTime.now().plusMinutes(10); // Definir data futura primeiro
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, codigo, pastTime);
        
        // Depois forçar expiração através de mock
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            // Mock LocalDateTime.now() para retornar uma data muito no futuro, tornando o código expirado
            mockedLocalDateTime.when(LocalDateTime::now)
                .thenReturn(pastTime.plusHours(1));
            
            when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, codigo))
                .thenReturn(Optional.of(recoveryCode));

            // Act
            boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, codigo);

            // Assert
            assertFalse(resultado);
        }
    }

    @Test
    @DisplayName("Deve desativar 2FA com código de recuperação válido")
    void deveDesativar2FAComCodigoRecuperacaoValido() {
        // Arrange
        String codigo = "123456";
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, codigo, LocalDateTime.now().plusMinutes(10));
        
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, codigo))
            .thenReturn(Optional.of(recoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(recoveryCode);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        // Act
        boolean resultado = twoFactorAuthService.disableTwoFactorWithRecoveryCode(USER_ID, codigo);

        // Assert
        assertTrue(resultado);
        assertFalse(usuario.getTwoFactorEnabled());
        assertNull(usuario.getTwoFactorSecret());
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao desativar 2FA com código de recuperação inválido")
    void deveFalharAoDesativar2FAComCodigoRecuperacaoInvalido() {
        // Arrange
        String codigo = "123456";
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, codigo))
            .thenReturn(Optional.empty());

        // Act
        boolean resultado = twoFactorAuthService.disableTwoFactorWithRecoveryCode(USER_ID, codigo);

        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any());
    }
} 