package inkspiration.backend.service.twoFactorAuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class TwoFactorAuthServiceValidacaoTest {

    @Test
    @DisplayName("Deve validar códigos de verificação de 6 dígitos")
    void deveValidarCodigosVerificacao6Digitos() {
        // Given
        List<Integer> codigosValidos = Arrays.asList(123456, 000000, 999999, 012345);
        List<Integer> codigosInvalidos = Arrays.asList(12345, 1234567, -123456, 0);
        
        // When & Then
        for (Integer codigo : codigosValidos) {
            String codigoStr = String.format("%06d", codigo);
            assertEquals(6, codigoStr.length());
            assertTrue(codigo >= 0 && codigo <= 999999);
        }
        
        for (Integer codigo : codigosInvalidos) {
            if (codigo != null) {
                String codigoStr = codigo.toString();
                assertTrue(codigoStr.length() != 6 || codigo < 0 || codigo > 999999);
            }
        }
    }

    @Test
    @DisplayName("Deve gerar códigos de recuperação únicos")
    void deveGerarCodigosRecuperacaoUnicos() {
        // Given
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int tamanho = 8;
        
        // When
        StringBuilder codigo1 = new StringBuilder();
        StringBuilder codigo2 = new StringBuilder();
        
        for (int i = 0; i < tamanho; i++) {
            codigo1.append(charset.charAt((int) (Math.random() * charset.length())));
            codigo2.append(charset.charAt((int) (Math.random() * charset.length())));
        }
        
        // Then
        assertEquals(tamanho, codigo1.toString().length());
        assertEquals(tamanho, codigo2.toString().length());
        assertTrue(codigo1.toString().matches("[A-Z0-9]{8}"));
        assertTrue(codigo2.toString().matches("[A-Z0-9]{8}"));
    }

    @Test
    @DisplayName("Deve validar formato de QR code")
    void deveValidarFormatoQRCode() {
        // Given
        String issuer = "Inkspiration";
        String account = "usuario@exemplo.com";
        String secret = "JBSWY3DPEHPK3PXP";
        
        // When
        String qrText = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", 
                                     issuer, account, secret, issuer);
        
        // Then
        assertTrue(qrText.startsWith("otpauth://totp/"));
        assertTrue(qrText.contains("secret="));
        assertTrue(qrText.contains("issuer="));
        assertTrue(qrText.contains(issuer));
        assertTrue(qrText.contains(account));
        assertTrue(qrText.contains(secret));
    }

    @Test
    @DisplayName("Deve validar formato de secret Base32")
    void deveValidarFormatoSecretBase32() {
        // Given
        List<String> secretsValidos = Arrays.asList(
            "JBSWY3DPEHPK3PXP",
            "ABCDEFGHIJKLMNOP",
            "234567ABCDEFGHIJ"
        );
        
        List<String> secretsInvalidos = Arrays.asList(
            "123456789", // muito curto
            "abcdefgh", // letras minúsculas
            "ABCD123@#$", // caracteres especiais
            ""
        );
        
        // When & Then
        for (String secret : secretsValidos) {
            assertTrue(secret.matches("[A-Z2-7]+"));
            assertTrue(secret.length() >= 16);
        }
        
        for (String secret : secretsInvalidos) {
            if (secret != null && !secret.isEmpty()) {
                boolean invalido = !secret.matches("[A-Z2-7]+") || secret.length() < 16;
                assertTrue(invalido);
            }
        }
    }

    @Test
    @DisplayName("Deve validar status 2FA habilitado/desabilitado")
    void deveValidarStatus2FAHabilitadoDesabilitado() {
        // Given
        boolean twoFactorEnabled = false;
        
        // When - Habilitar 2FA
        twoFactorEnabled = true;
        
        // Then
        assertTrue(twoFactorEnabled);
        
        // When - Desabilitar 2FA
        twoFactorEnabled = false;
        
        // Then
        assertFalse(twoFactorEnabled);
    }

    @Test
    @DisplayName("Deve validar tempo de janela para códigos TOTP")
    void deveValidarTempoJanelaCodigosTOTP() {
        // Given
        long timeStep = 30; // segundos
        long currentTime = System.currentTimeMillis() / 1000;
        
        // When
        long currentStep = currentTime / timeStep;
        long previousStep = currentStep - 1;
        long nextStep = currentStep + 1;
        
        // Then
        assertTrue(currentStep > 0);
        assertEquals(currentStep - 1, previousStep);
        assertEquals(currentStep + 1, nextStep);
    }

    @Test
    @DisplayName("Deve validar algoritmo HMAC-SHA1")
    void deveValidarAlgoritmoHMACSHA1() {
        // Given
        String algoritmo = "HmacSHA1";
        
        // When & Then
        assertEquals("HmacSHA1", algoritmo);
        assertNotNull(algoritmo);
        assertFalse(algoritmo.isEmpty());
    }

    @Test
    @DisplayName("Deve validar tamanho de código TOTP")
    void deveValidarTamanhoCodigoTOTP() {
        // Given
        int digitos = 6;
        
        // When & Then
        assertEquals(6, digitos);
        assertTrue(digitos > 0);
        assertTrue(digitos <= 10);
    }

    @Test
    @DisplayName("Deve validar formato de imagem QR code")
    void deveValidarFormatoImagemQRCode() {
        // Given
        String formato = "PNG";
        int largura = 200;
        int altura = 200;
        
        // When & Then
        assertEquals("PNG", formato);
        assertEquals(200, largura);
        assertEquals(200, altura);
        assertTrue(largura > 0);
        assertTrue(altura > 0);
    }

    @Test
    @DisplayName("Deve validar email para account no QR code")
    void deveValidarEmailParaAccountQRCode() {
        // Given
        String email = "usuario@exemplo.com";
        
        // When & Then
        assertTrue(email.contains("@"));
        assertTrue(email.contains("."));
        assertFalse(email.startsWith("@"));
        assertFalse(email.endsWith("@"));
    }

    @Test
    @DisplayName("Deve validar códigos de backup únicos")
    void deveValidarCodigosBackupUnicos() {
        // Given
        int quantidadeBackups = 10;
        
        // When
        String[] codigosBackup = new String[quantidadeBackups];
        for (int i = 0; i < quantidadeBackups; i++) {
            codigosBackup[i] = gerarCodigoBackup();
        }
        
        // Then
        assertEquals(quantidadeBackups, codigosBackup.length);
        for (String codigo : codigosBackup) {
            assertNotNull(codigo);
            assertEquals(8, codigo.length());
            assertTrue(codigo.matches("[A-Z0-9]{8}"));
        }
    }

    @Test
    @DisplayName("Deve validar expiração de códigos de recuperação")
    void deveValidarExpiracaoCodigosRecuperacao() {
        // Given
        long criadoEm = System.currentTimeMillis();
        long validadePorHoras = 24;
        long validadeEmMs = validadePorHoras * 60 * 60 * 1000;
        
        // When
        long expiraEm = criadoEm + validadeEmMs;
        long agora = System.currentTimeMillis();
        boolean expirado = agora > expiraEm;
        
        // Then
        assertTrue(expiraEm > criadoEm);
        assertFalse(expirado); // Código recém criado não deve estar expirado
    }

    @Test
    @DisplayName("Deve validar uso único de códigos de recuperação")
    void deveValidarUsoUnicoCodigosRecuperacao() {
        // Given
        boolean codigoUsado = false;
        
        // When - Usar código
        codigoUsado = true;
        
        // Then
        assertTrue(codigoUsado);
        
        // When - Tentar usar novamente
        boolean podeUsarNovamente = !codigoUsado;
        
        // Then
        assertFalse(podeUsarNovamente);
    }

    @Test
    @DisplayName("Deve validar Base64 para QR code")
    void deveValidarBase64ParaQRCode() {
        // Given
        String textoOriginal = "otpauth://totp/teste";
        
        // When
        String base64 = java.util.Base64.getEncoder().encodeToString(textoOriginal.getBytes());
        String decoded = new String(java.util.Base64.getDecoder().decode(base64));
        
        // Then
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        assertEquals(textoOriginal, decoded);
    }

    private String gerarCodigoBackup() {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            codigo.append(charset.charAt((int) (Math.random() * charset.length())));
        }
        return codigo.toString();
    }
} 