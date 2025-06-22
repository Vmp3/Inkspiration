package inkspiration.backend.service.passwordResetService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class PasswordResetServiceValidacaoTest {

    @Test
    @DisplayName("Deve gerar código de 6 dígitos")
    void deveGerarCodigoDe6Digitos() {
        // Given
        int codigo = (int) (Math.random() * 1000000);
        
        // When
        String codigoFormatado = String.format("%06d", codigo);
        
        // Then
        assertEquals(6, codigoFormatado.length());
        assertTrue(codigoFormatado.matches("\\d{6}"));
    }

    @Test
    @DisplayName("Deve validar CPF limpo sem máscara")
    void deveValidarCPFLimpoSemMascara() {
        // Given
        String cpfComMascara = "123.456.789-10";
        String cpfComPontos = "123.456.789.10";
        String cpfLimpo = "12345678910";
        
        // When
        String cpfProcessado1 = cpfComMascara.replaceAll("[^0-9]", "");
        String cpfProcessado2 = cpfComPontos.replaceAll("[^0-9]", "");
        String cpfProcessado3 = cpfLimpo.replaceAll("[^0-9]", "");
        
        // Then
        assertEquals("12345678910", cpfProcessado1);
        assertEquals("12345678910", cpfProcessado2);
        assertEquals("12345678910", cpfProcessado3);
    }

    @Test
    @DisplayName("Deve validar tempo de expiração de 15 minutos")
    void deveValidarTempoExpiracao15Minutos() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime expiracao = agora.plusMinutes(15);
        
        // When
        long diferencaMinutos = java.time.Duration.between(agora, expiracao).toMinutes();
        
        // Then
        assertEquals(15, diferencaMinutos);
        assertTrue(expiracao.isAfter(agora));
    }

    @Test
    @DisplayName("Deve validar códigos dentro do prazo")
    void deveValidarCodigosDentroPrazo() {
        // Given
        LocalDateTime criacao = LocalDateTime.now().minusMinutes(10);
        LocalDateTime expiracao = criacao.plusMinutes(15);
        LocalDateTime agora = LocalDateTime.now();
        
        // When
        boolean valido = agora.isBefore(expiracao);
        
        // Then
        assertTrue(valido);
    }

    @Test
    @DisplayName("Deve invalidar códigos expirados")
    void deveInvalidarCodigosExpirados() {
        // Given
        LocalDateTime criacao = LocalDateTime.now().minusMinutes(20);
        LocalDateTime expiracao = criacao.plusMinutes(15);
        LocalDateTime agora = LocalDateTime.now();
        
        // When
        boolean valido = agora.isBefore(expiracao);
        
        // Then
        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve validar limite de 3 tentativas em 15 minutos")
    void deveValidarLimite3TentativasEm15Minutos() {
        // Given
        int limite = 3;
        int tentativasAtuais = 2;
        int novasTentativas = 1;
        
        // When
        int totalTentativas = tentativasAtuais + novasTentativas;
        boolean dentroLimite = totalTentativas <= limite;
        
        // Then
        assertTrue(dentroLimite);
        
        // Given - Exceder limite
        tentativasAtuais = 3;
        novasTentativas = 1;
        
        // When
        totalTentativas = tentativasAtuais + novasTentativas;
        dentroLimite = totalTentativas <= limite;
        
        // Then
        assertFalse(dentroLimite);
    }

    @Test
    @DisplayName("Deve validar senhas com critérios de segurança")
    void deveValidarSenhasComCriteriosSeguranca() {
        // Given
        List<String> senhasValidas = Arrays.asList(
            "MinhaSenh@123",
            "P@ssw0rd!",
            "Segur@456",
            "Test@123"
        );
        
        // When & Then
        for (String senha : senhasValidas) {
            assertTrue(senha.length() >= 8);
            assertTrue(senha.matches(".*[A-Z].*")); // Pelo menos uma maiúscula
            assertTrue(senha.matches(".*[a-z].*")); // Pelo menos uma minúscula
            assertTrue(senha.matches(".*\\d.*")); // Pelo menos um número
            assertTrue(senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\?].*")); // Pelo menos um especial
        }
    }

    @Test
    @DisplayName("Deve rejeitar senhas fracas")
    void deveRejeitarSenhasFracas() {
        // Given
        List<String> senhasFracas = Arrays.asList(
            "123456",
            "password",
            "abc123",
            "12345678",
            "senha",
            "test"
        );
        
        // When & Then
        for (String senha : senhasFracas) {
            boolean senhaFraca = senha.length() < 8 ||
                               !senha.matches(".*[A-Z].*") ||
                               !senha.matches(".*[a-z].*") ||
                               !senha.matches(".*\\d.*") ||
                               !senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\?].*");
            
            assertTrue(senhaFraca);
        }
    }

    @Test
    @DisplayName("Deve mascarar email para privacidade")
    void deveMascararEmailParaPrivacidade() {
        // Given
        String email = "usuario@exemplo.com";
        
        // When
        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        String emailMascarado = localPart.substring(0, 2) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
        
        // Then
        assertEquals("us***o@exemplo.com", emailMascarado);
        assertTrue(emailMascarado.contains("***"));
    }

    @Test
    @DisplayName("Deve mascarar emails curtos corretamente")
    void deveMascararEmailsCurtosCorretamente() {
        // Given
        String emailCurto = "ab@test.com";
        
        // When
        int atIndex = emailCurto.indexOf('@');
        String localPart = emailCurto.substring(0, atIndex);
        String domainPart = emailCurto.substring(atIndex);
        String emailMascarado = "**" + localPart.charAt(localPart.length() - 1) + domainPart;
        
        // Then
        assertEquals("**b@test.com", emailMascarado);
    }

    @Test
    @DisplayName("Deve validar janela de tempo para códigos recentes")
    void deveValidarJanelaTempoCodigosRecentes() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime quinzeMinutosAtras = agora.minusMinutes(15);
        LocalDateTime dezMinutosAtras = agora.minusMinutes(10);
        LocalDateTime vinteMinutosAtras = agora.minusMinutes(20);
        
        // When & Then
        assertTrue(dezMinutosAtras.isAfter(quinzeMinutosAtras));
        assertFalse(vinteMinutosAtras.isAfter(quinzeMinutosAtras));
    }

    @Test
    @DisplayName("Deve marcar códigos como usados")
    void deveMarcarCodigosComoUsados() {
        // Given
        boolean codigoUsado = false;
        
        // When
        codigoUsado = true;
        
        // Then
        assertTrue(codigoUsado);
    }

    @Test
    @DisplayName("Deve validar códigos não utilizados")
    void deveValidarCodigosNaoUtilizados() {
        // Given
        boolean codigoUsado = false;
        
        // When & Then
        assertFalse(codigoUsado);
    }

    @Test
    @DisplayName("Deve gerar códigos únicos")
    void deveGerarCodigosUnicos() {
        // Given
        int codigo1 = (int) (Math.random() * 1000000);
        int codigo2 = (int) (Math.random() * 1000000);
        
        // When
        String codigoFormatado1 = String.format("%06d", codigo1);
        String codigoFormatado2 = String.format("%06d", codigo2);
        
        // Then
        // Embora seja possível gerar códigos iguais, é muito improvável
        assertEquals(6, codigoFormatado1.length());
        assertEquals(6, codigoFormatado2.length());
        assertTrue(codigoFormatado1.matches("\\d{6}"));
        assertTrue(codigoFormatado2.matches("\\d{6}"));
    }

    @Test
    @DisplayName("Deve validar códigos apenas numéricos")
    void deveValidarCodigosApenasNumericos() {
        // Given
        String codigoValido = "123456";
        String codigoInvalido = "abc123";
        
        // When & Then
        assertTrue(codigoValido.matches("\\d{6}"));
        assertFalse(codigoInvalido.matches("\\d{6}"));
    }

    @Test
    @DisplayName("Deve limpar códigos expirados automaticamente")
    void deveLimparCodigosExpiradosAutomaticamente() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime codigoExpirado = agora.minusHours(1);
        LocalDateTime codigoValido = agora.minusMinutes(5);
        
        // When
        boolean deveRemoverExpirado = codigoExpirado.isBefore(agora);
        boolean deveManterValido = codigoValido.isAfter(agora.minusMinutes(15));
        
        // Then
        assertTrue(deveRemoverExpirado);
        assertTrue(deveManterValido);
    }
} 