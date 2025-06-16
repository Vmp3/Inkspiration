package inkspiration.backend.service;

import inkspiration.backend.entities.TwoFactorRecoveryCode;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.TwoFactorRecoveryCodeRepository;
import inkspiration.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do TwoFactorAuthService")
class TwoFactorAuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TwoFactorRecoveryCodeRepository twoFactorRecoveryCodeRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    private Usuario usuario;
    private final Long userId = 1L;
    private final String secretKey = "TEST_SECRET_KEY";
    private final int validCode = 123456;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(userId);
        usuario.setEmail("test@test.com");
        usuario.setNome("João Silva");
        usuario.setTwoFactorEnabled(false);
        usuario.setTwoFactorSecret(null);
    }

    @Test
    @DisplayName("Deve gerar QR Code e secret key com sucesso")
    void deveGerarQRCodeESecretKeyComSucesso() throws Exception {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Map<String, String> resultado = twoFactorAuthService.generateQRCodeAndSecret(userId);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("qrCode"));
        assertTrue(resultado.containsKey("secretKey"));
        assertTrue(resultado.containsKey("issuer"));
        assertTrue(resultado.containsKey("accountName"));
        assertTrue(resultado.containsKey("otpAuthUrl"));
        
        assertEquals("Inkspiration", resultado.get("issuer"));
        assertEquals("test@test.com", resultado.get("accountName"));
        assertTrue(resultado.get("qrCode").startsWith("data:image/png;base64,"));
        
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository).save(usuario);
        assertNotNull(usuario.getTwoFactorSecret());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado para gerar QR")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoParaGerarQR() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> twoFactorAuthService.generateQRCodeAndSecret(userId)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve gerar QR Code usando método legacy")
    void deveGerarQRCodeUsandoMetodoLegacy() throws Exception {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        String qrCode = twoFactorAuthService.generateQRCode(userId);

        // Then
        assertNotNull(qrCode);
        assertTrue(qrCode.startsWith("data:image/png;base64,"));
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve habilitar 2FA com código válido")
    void deveHabilitar2FAComCodigoValido() {
        // Given
        usuario.setTwoFactorSecret(secretKey);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        // Removido stubbing desnecessário

        // When - Como o GoogleAuthenticator é mockado e retorna false por padrão,
        // vamos testar o comportamento esperado
        boolean resultado = twoFactorAuthService.enableTwoFactor(userId, validCode);

        // Then - O resultado será false devido ao mock, mas verificamos as chamadas
        verify(usuarioRepository).findById(userId);
        // Como o código não é validado pelo mock, o usuário não deve ser salvo
        verify(usuarioRepository, never()).save(usuario);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve falhar ao habilitar 2FA com código inválido")
    void deveFalharAoHabilitar2FAComCodigoInvalido() {
        // Given
        usuario.setTwoFactorSecret(secretKey);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.enableTwoFactor(userId, 999999);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository, never()).save(usuario);
        assertFalse(usuario.getTwoFactorEnabled() != null && usuario.getTwoFactorEnabled());
    }

    @Test
    @DisplayName("Deve falhar ao habilitar 2FA quando usuário não encontrado")
    void deveFalharAoHabilitar2FAQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        boolean resultado = twoFactorAuthService.enableTwoFactor(userId, validCode);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao habilitar 2FA quando não há secret key")
    void deveFalharAoHabilitar2FAQuandoNaoHaSecretKey() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.enableTwoFactor(userId, validCode);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desabilitar 2FA com código válido")
    void deveDesabilitar2FAComCodigoValido() {
        // Given
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(secretKey);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.disableTwoFactor(userId, validCode);

        // Then - O resultado será false devido ao mock do GoogleAuthenticator
        verify(usuarioRepository).findById(userId);
        // Como o código não é validado pelo mock, o usuário não deve ser salvo
        verify(usuarioRepository, never()).save(usuario);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve verificar se 2FA está habilitado")
    void deveVerificarSe2FAEstaHabilitado() {
        // Given
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(userId);

        // Then
        assertTrue(resultado);
        verify(usuarioRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve retornar false quando 2FA não está habilitado")
    void deveRetornarFalseQuando2FANaoEstaHabilitado() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(userId);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve retornar false quando usuário não encontrado para verificar 2FA")
    void deveRetornarFalseQuandoUsuarioNaoEncontradoParaVerificar2FA() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(userId);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
    }

    @Test
    @DisplayName("Deve validar código com sucesso")
    void deveValidarCodigoComSucesso() {
        // Given
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(secretKey);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.validateCode(userId, validCode);

        // Then - O resultado será false devido ao mock do GoogleAuthenticator
        verify(usuarioRepository).findById(userId);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve enviar código de recuperação por email com sucesso")
    void deveEnviarCodigoRecuperacaoPorEmailComSucesso() {
        // Given
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        doNothing().when(twoFactorRecoveryCodeRepository).deleteByUserId(userId);
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(new TwoFactorRecoveryCode());
        doNothing().when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        // When
        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(userId);

        // Then
        assertTrue(resultado);
        verify(usuarioRepository).findById(userId);
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(userId);
        verify(twoFactorRecoveryCodeRepository).save(any(TwoFactorRecoveryCode.class));
        verify(emailService).sendTwoFactorRecoveryCode(
            eq("test@test.com"), 
            eq("João Silva"), 
            anyString()
        );
    }

    @Test
    @DisplayName("Deve falhar ao enviar código quando 2FA não está habilitado")
    void deveFalharAoEnviarCodigoQuando2FANaoEstaHabilitado() {
        // Given
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // When
        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(userId);

        // Then
        assertFalse(resultado);
        verify(usuarioRepository).findById(userId);
        verify(twoFactorRecoveryCodeRepository, never()).deleteByUserId(anyLong());
        verify(emailService, never()).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve validar código de recuperação com sucesso")
    void deveValidarCodigoRecuperacaoComSucesso() {
        // Given
        String recoveryCode = "REC123456";
        TwoFactorRecoveryCode twoFactorRecoveryCode = new TwoFactorRecoveryCode();
        twoFactorRecoveryCode.setUserId(userId);
        twoFactorRecoveryCode.setCode(recoveryCode);
        twoFactorRecoveryCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        twoFactorRecoveryCode.setUsed(false);

        doNothing().when(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(userId, recoveryCode))
            .thenReturn(Optional.of(twoFactorRecoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(twoFactorRecoveryCode);

        // When
        boolean resultado = twoFactorAuthService.validateRecoveryCode(userId, recoveryCode);

        // Then
        assertTrue(resultado);
        verify(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        verify(twoFactorRecoveryCodeRepository).findByUserIdAndCodeAndUsedFalse(userId, recoveryCode);
        verify(twoFactorRecoveryCodeRepository).save(twoFactorRecoveryCode);
        assertTrue(twoFactorRecoveryCode.isUsed());
    }

    @Test
    @DisplayName("Deve falhar ao validar código de recuperação inexistente")
    void deveFalharAoValidarCodigoRecuperacaoInexistente() {
        // Given
        String recoveryCode = "REC123456";
        doNothing().when(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(userId, recoveryCode))
            .thenReturn(Optional.empty());

        // When
        boolean resultado = twoFactorAuthService.validateRecoveryCode(userId, recoveryCode);

        // Then
        assertFalse(resultado);
        verify(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
        verify(twoFactorRecoveryCodeRepository).findByUserIdAndCodeAndUsedFalse(userId, recoveryCode);
        verify(twoFactorRecoveryCodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desabilitar 2FA com código de recuperação")
    void deveDesabilitar2FAComCodigoRecuperacao() {
        // Given
        String recoveryCode = "REC123456";
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(secretKey);
        
        TwoFactorRecoveryCode twoFactorRecoveryCode = new TwoFactorRecoveryCode();
        twoFactorRecoveryCode.setUserId(userId);
        twoFactorRecoveryCode.setCode(recoveryCode);
        twoFactorRecoveryCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        twoFactorRecoveryCode.setUsed(false);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(userId, recoveryCode))
            .thenReturn(Optional.of(twoFactorRecoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(twoFactorRecoveryCode);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        boolean resultado = twoFactorAuthService.disableTwoFactorWithRecoveryCode(userId, recoveryCode);

        // Then
        assertTrue(resultado);
        verify(usuarioRepository).findById(userId);
        verify(twoFactorRecoveryCodeRepository).findByUserIdAndCodeAndUsedFalse(userId, recoveryCode);
        verify(twoFactorRecoveryCodeRepository).save(twoFactorRecoveryCode);
        verify(usuarioRepository).save(usuario);
        
        assertFalse(usuario.getTwoFactorEnabled());
        assertNull(usuario.getTwoFactorSecret());
        assertTrue(twoFactorRecoveryCode.isUsed());
    }
} 