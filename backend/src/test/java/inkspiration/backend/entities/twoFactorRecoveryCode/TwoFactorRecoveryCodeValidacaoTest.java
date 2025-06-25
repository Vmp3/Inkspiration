package inkspiration.backend.entities.twoFactorRecoveryCode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;

@DisplayName("Testes de métodos de validação - TwoFactorRecoveryCode")
public class TwoFactorRecoveryCodeValidacaoTest {

    private TwoFactorRecoveryCode code;

    @BeforeEach
    void setUp() {
        code = new TwoFactorRecoveryCode();
    }

    @Test
    @DisplayName("Deve verificar se o código expirou - código não expirado")
    void deveVerificarCodigoNaoExpirado() {
        code.setExpiresAt(LocalDateTime.now().plusHours(1));
        assertFalse(code.isExpired());
    }

    @Test
    @DisplayName("Deve verificar se o código expirou - código expirado")
    void deveVerificarCodigoExpirado() {
        code.setExpiresAt(LocalDateTime.now().minusHours(1));
        assertTrue(code.isExpired());
    }

    @Test
    @DisplayName("Deve verificar se o código expirou no limite")
    void deveVerificarCodigoExpiradoNoLimite() {
        code.setExpiresAt(LocalDateTime.now().minusSeconds(1));
        assertTrue(code.isExpired());
    }

    @Test
    @DisplayName("Deve verificar se o código é válido - código válido")
    void deveVerificarCodigoValido() {
        code.setExpiresAt(LocalDateTime.now().plusHours(1));
        code.setUsed(false);
        assertTrue(code.isValid());
    }

    @Test
    @DisplayName("Deve verificar se o código é válido - código usado")
    void deveVerificarCodigoUsado() {
        code.setExpiresAt(LocalDateTime.now().plusHours(1));
        code.setUsed(true);
        assertFalse(code.isValid());
    }

    @Test
    @DisplayName("Deve verificar se o código é válido - código expirado")
    void deveVerificarCodigoExpiradoInvalido() {
        code.setExpiresAt(LocalDateTime.now().minusHours(1));
        code.setUsed(false);
        assertFalse(code.isValid());
    }

    @Test
    @DisplayName("Deve verificar se o código é válido - código usado e expirado")
    void deveVerificarCodigoUsadoEExpirado() {
        code.setExpiresAt(LocalDateTime.now().minusHours(1));
        code.setUsed(true);
        assertFalse(code.isValid());
    }

    @Test
    @DisplayName("Deve definir status de usado como true")
    void deveDefinirStatusUsadoTrue() {
        code.setUsed(true);
        assertTrue(code.isUsed());
    }

    @Test
    @DisplayName("Deve definir status de usado como false")
    void deveDefinirStatusUsadoFalse() {
        code.setUsed(false);
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Deve alternar status de usado")
    void deveAlternarStatusUsado() {
        code.setUsed(false);
        assertFalse(code.isUsed());
        
        code.setUsed(true);
        assertTrue(code.isUsed());
        
        code.setUsed(false);
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Status padrão de usado deve ser false")
    void statusPadraoUsadoDeveSerFalse() {
        TwoFactorRecoveryCode newCode = new TwoFactorRecoveryCode();
        assertFalse(newCode.isUsed());
    }
} 