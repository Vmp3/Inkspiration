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
import inkspiration.backend.exception.twofactor.*;

import com.warrenstrange.googleauth.GoogleAuthenticator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("TwoFactorAuthService - Testes Completos")
class TwoFactorAuthServiceTest {

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
    private final String AUTH_HEADER = "Bearer token123";
    private final String TOKEN = "token123";
    private final String RECOVERY_CODE = "123456";

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

    // Testes para generateQRCodeAndSecret
    @Test
    @DisplayName("Deve gerar QR code e secret key com sucesso")
    void deveGerarQRCodeESecretKeyComSucesso() throws Exception {
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        Map<String, String> resultado = twoFactorAuthService.generateQRCodeAndSecret(USER_ID);

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
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> twoFactorAuthService.generateQRCodeAndSecret(USER_ID)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    // Testes para generateQRCode (método legacy)
    @Test
    @DisplayName("Deve gerar QR code legacy com sucesso")
    void deveGerarQRCodeLegacyComSucesso() throws Exception {
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        String qrCode = twoFactorAuthService.generateQRCode(USER_ID);

        assertNotNull(qrCode);
        assertTrue(qrCode.startsWith("data:image/png;base64,"));
    }

    // Testes para enableTwoFactor
    @Test
    @DisplayName("Deve ativar 2FA com código válido")
    void deveAtivar2FAComCodigoValido() {
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        assertTrue(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve falhar ao ativar 2FA para usuário inexistente")
    void deveFalharAoAtivar2FAParaUsuarioInexistente() {
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve falhar ao ativar 2FA sem secret key")
    void deveFalharAoAtivar2FASemSecretKey() {
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.enableTwoFactor(USER_ID, validCode);

        assertFalse(resultado);
    }

    // Testes para disableTwoFactor
    @Test
    @DisplayName("Deve desativar 2FA com código válido")
    void deveDesativar2FAComCodigoValido() {
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.disableTwoFactor(USER_ID, validCode);

        assertTrue(resultado);
        assertFalse(usuario.getTwoFactorEnabled());
        assertNull(usuario.getTwoFactorSecret());
    }

    @Test
    @DisplayName("Deve falhar ao desativar 2FA quando não está habilitado")
    void deveFalharAoDesativar2FAQuandoNaoEstaHabilitado() {
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.disableTwoFactor(USER_ID, validCode);

        assertFalse(resultado);
    }

    // Testes para validateCode
    @Test
    @DisplayName("Deve validar código com sucesso quando 2FA está habilitado")
    void deveValidarCodigoComSucessoQuando2FAEstaHabilitado() {
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.validateCode(USER_ID, validCode);

        assertTrue(resultado);
        verify(usuarioRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Deve retornar false quando 2FA não está habilitado")
    void deveRetornarFalseQuando2FANaoEstaHabilitado() {
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.validateCode(USER_ID, validCode);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar false para usuário inexistente")
    void deveRetornarFalseParaUsuarioInexistente() {
        int validCode = getValidCode(SECRET_KEY);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.empty());

        boolean resultado = twoFactorAuthService.validateCode(USER_ID, validCode);

        assertFalse(resultado);
    }

    // Testes para isTwoFactorEnabled
    @Test
    @DisplayName("Deve retornar true quando 2FA está habilitado")
    void deveRetornarTrueQuando2FAEstaHabilitado() {
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(USER_ID);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve retornar false quando 2FA não está habilitado")
    void deveRetornarFalseQuando2FANaoEstaHabilitadoStatus() {
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.isTwoFactorEnabled(USER_ID);

        assertFalse(resultado);
    }

    // Testes para sendRecoveryCodeByEmail
    @Test
    @DisplayName("Deve enviar código de recuperação por email com sucesso")
    void deveEnviarCodigoRecuperacaoPorEmailComSucesso() throws Exception {
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(new TwoFactorRecoveryCode());
        doNothing().when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        assertTrue(resultado);
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
        verify(emailService).sendTwoFactorRecoveryCode(eq(EMAIL), eq(NOME), anyString());
    }

    @Test
    @DisplayName("Deve falhar ao enviar código quando 2FA não está habilitado")
    void deveFalharAoEnviarCodigoQuando2FANaoEstaHabilitado() {
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve remover código quando falha ao enviar email")
    void deveRemoverCodigoQuandoFalhaAoEnviarEmail() throws Exception {
        usuario.setTwoFactorEnabled(true);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        doThrow(new RuntimeException("Erro ao enviar email"))
            .when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        boolean resultado = twoFactorAuthService.sendRecoveryCodeByEmail(USER_ID);

        assertFalse(resultado);
        // Verify que foi chamado deleteByUserId, save e delete
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
        verify(twoFactorRecoveryCodeRepository).save(any(TwoFactorRecoveryCode.class));
        verify(twoFactorRecoveryCodeRepository).delete(any(TwoFactorRecoveryCode.class));
    }

    // Testes para validateRecoveryCode
    @Test
    @DisplayName("Deve validar código de recuperação com sucesso")
    void deveValidarCodigoRecuperacaoComSucesso() {
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, RECOVERY_CODE, futureTime);
        
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, RECOVERY_CODE))
            .thenReturn(Optional.of(recoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(recoveryCode);

        boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, RECOVERY_CODE);

        assertTrue(resultado);
        assertTrue(recoveryCode.isUsed());
        verify(twoFactorRecoveryCodeRepository).deleteExpiredCodes(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve falhar ao validar código de recuperação inexistente")
    void deveFalharAoValidarCodigoRecuperacaoInexistente() {
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, RECOVERY_CODE))
            .thenReturn(Optional.empty());

        boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, RECOVERY_CODE);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve falhar ao validar código de recuperação expirado")
    void deveFalharAoValidarCodigoRecuperacaoExpirado() {
        LocalDateTime pastTime = LocalDateTime.now().plusMinutes(10); // Definir data futura primeiro
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, RECOVERY_CODE, pastTime);
        
        // Depois forçar expiração através de mock
        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            // Mock LocalDateTime.now() para retornar uma data muito no futuro, tornando o código expirado
            mockedLocalDateTime.when(LocalDateTime::now)
                .thenReturn(pastTime.plusHours(1));
            
            when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, RECOVERY_CODE))
                .thenReturn(Optional.of(recoveryCode));

            boolean resultado = twoFactorAuthService.validateRecoveryCode(USER_ID, RECOVERY_CODE);

            assertFalse(resultado);
        }
    }

    // Testes para disableTwoFactorWithRecoveryCode
    @Test
    @DisplayName("Deve desativar 2FA com código de recuperação válido")
    void deveDesativar2FAComCodigoRecuperacaoValido() {
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, RECOVERY_CODE, futureTime);
        
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, RECOVERY_CODE))
            .thenReturn(Optional.of(recoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(recoveryCode);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.disableTwoFactorWithRecoveryCode(USER_ID, RECOVERY_CODE);

        assertTrue(resultado);
        assertFalse(usuario.getTwoFactorEnabled());
        assertNull(usuario.getTwoFactorSecret());
        verify(twoFactorRecoveryCodeRepository).deleteByUserId(USER_ID);
    }

    // Testes para métodos com validação de JWT
    @Test
    @DisplayName("Deve gerar QR code com validação JWT")
    void deveGerarQRCodeComValidacaoJWT() throws Exception {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        Map<String, String> resultado = twoFactorAuthService.gerarQRCodeComValidacao(AUTH_HEADER);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey("qrCode"));
        verify(jwtService).getUserIdFromToken(TOKEN);
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar QR code com token inválido")
    void deveLancarExcecaoAoGerarQRCodeComTokenInvalido() {
        when(jwtService.getUserIdFromToken(TOKEN))
            .thenThrow(new RuntimeException("Token inválido"));

        assertThrows(QRCodeGeracaoException.class, 
            () -> twoFactorAuthService.gerarQRCodeComValidacao(AUTH_HEADER));
    }

    @Test
    @DisplayName("Deve ativar 2FA com validação JWT")
    void deveAtivar2FAComValidacaoJWT() {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, validCode);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao ativar 2FA com código nulo")
    void deveLancarExcecaoAoAtivar2FAComCodigoNulo() {
        assertThrows(CodigoVerificacaoInvalidoException.class,
            () -> twoFactorAuthService.ativarTwoFactorComValidacao(AUTH_HEADER, null));
    }

    @Test
    @DisplayName("Deve desativar 2FA com validação JWT")
    void deveDesativar2FAComValidacaoJWT() {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.desativarTwoFactorComValidacao(AUTH_HEADER, validCode);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve obter status 2FA com validação JWT")
    void deveObterStatus2FAComValidacaoJWT() {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.obterStatusTwoFactorComValidacao(AUTH_HEADER);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar código com validação JWT")
    void deveValidarCodigoComValidacaoJWT() {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        int validCode = getValidCode(SECRET_KEY);
        
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));

        boolean resultado = twoFactorAuthService.validarCodigoComValidacao(AUTH_HEADER, validCode);

        assertTrue(resultado);
        verify(jwtService).getUserIdFromToken(TOKEN);
    }

    @Test
    @DisplayName("Deve enviar código de recuperação com validação JWT")
    void deveEnviarCodigoRecuperacaoComValidacaoJWT() throws Exception {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorEnabled(true);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(new TwoFactorRecoveryCode());
        doNothing().when(emailService).sendTwoFactorRecoveryCode(anyString(), anyString(), anyString());

        boolean resultado = twoFactorAuthService.enviarCodigoRecuperacaoComValidacao(AUTH_HEADER);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve desativar 2FA com código de recuperação e validação JWT")
    void deveDesativar2FAComCodigoRecuperacaoEValidacaoJWT() {
        when(jwtService.getUserIdFromToken(TOKEN)).thenReturn(USER_ID);
        usuario.setTwoFactorEnabled(true);
        usuario.setTwoFactorSecret(SECRET_KEY);
        
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(USER_ID, RECOVERY_CODE, futureTime);
        
        when(twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(USER_ID, RECOVERY_CODE))
            .thenReturn(Optional.of(recoveryCode));
        when(twoFactorRecoveryCodeRepository.save(any(TwoFactorRecoveryCode.class)))
            .thenReturn(recoveryCode);
        when(usuarioRepository.findById(USER_ID))
            .thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);

        boolean resultado = twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, RECOVERY_CODE);

        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve lançar exceção com código de recuperação nulo")
    void deveLancarExcecaoComCodigoRecuperacaoNulo() {
        assertThrows(CodigoRecuperacaoException.class,
            () -> twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, null));
    }

    @Test
    @DisplayName("Deve lançar exceção com código de recuperação vazio")
    void deveLancarExcecaoComCodigoRecuperacaoVazio() {
        assertThrows(CodigoRecuperacaoException.class,
            () -> twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(AUTH_HEADER, "   "));
    }
}
