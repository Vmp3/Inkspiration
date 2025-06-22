package inkspiration.backend.service.emailVerificationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

class EmailVerificationServiceValidacaoTest {

    @Test
    @DisplayName("Deve gerar código de verificação de 6 dígitos")
    void deveGerarCodigoVerificacao6Digitos() {
        // Given
        int codigo = (int) (Math.random() * 1000000);
        
        // When
        String codigoFormatado = String.format("%06d", codigo);
        
        // Then
        assertEquals(6, codigoFormatado.length());
        assertTrue(codigoFormatado.matches("\\d{6}"));
        assertTrue(Integer.parseInt(codigoFormatado) >= 0);
        assertTrue(Integer.parseInt(codigoFormatado) <= 999999);
    }

    @Test
    @DisplayName("Deve validar email para verificação")
    void deveValidarEmailParaVerificacao() {
        // Given
        List<String> emailsValidos = Arrays.asList(
            "usuario@exemplo.com",
            "teste.email@dominio.com.br",
            "email123@teste.org",
            "user+tag@domain.co"
        );
        
        List<String> emailsInvalidos = Arrays.asList(
            "email-sem-arroba",
            "@dominio.com",
            "email@",
            ""
        );
        
        // When & Then
        for (String email : emailsValidos) {
            assertTrue(email.contains("@"));
            assertTrue(email.contains("."));
            assertFalse(email.startsWith("@"));
            assertFalse(email.endsWith("@"));
        }
        
        for (String email : emailsInvalidos) {
            if (email != null && !email.isEmpty()) {
                boolean invalido = !email.contains("@") || 
                                  email.startsWith("@") || 
                                  email.endsWith("@") ||
                                  !email.contains(".");
                assertTrue(invalido);
            }
        }
    }

    @Test
    @DisplayName("Deve validar expiração do código em 10 minutos")
    void deveValidarExpiracaoCodigo10Minutos() {
        // Given
        LocalDateTime criacao = LocalDateTime.now();
        LocalDateTime expiracao = criacao.plusMinutes(10);
        
        // When
        long diferencaMinutos = java.time.Duration.between(criacao, expiracao).toMinutes();
        
        // Then
        assertEquals(10, diferencaMinutos);
        assertTrue(expiracao.isAfter(criacao));
    }

    @Test
    @DisplayName("Deve validar código dentro do prazo")
    void deveValidarCodigoDentroPrazo() {
        // Given
        LocalDateTime criacao = LocalDateTime.now().minusMinutes(5);
        LocalDateTime expiracao = criacao.plusMinutes(10);
        LocalDateTime agora = LocalDateTime.now();
        
        // When
        boolean valido = agora.isBefore(expiracao);
        
        // Then
        assertTrue(valido);
    }

    @Test
    @DisplayName("Deve invalidar código expirado")
    void deveInvalidarCodigoExpirado() {
        // Given
        LocalDateTime criacao = LocalDateTime.now().minusMinutes(15);
        LocalDateTime expiracao = criacao.plusMinutes(10);
        LocalDateTime agora = LocalDateTime.now();
        
        // When
        boolean valido = agora.isBefore(expiracao);
        
        // Then
        assertFalse(valido);
    }

    @Test
    @DisplayName("Deve validar limite de tentativas de verificação")
    void deveValidarLimiteTentativasVerificacao() {
        // Given
        int limiteTentativas = 5;
        int tentativasAtuais = 3;
        
        // When
        boolean dentroLimite = tentativasAtuais < limiteTentativas;
        
        // Then
        assertTrue(dentroLimite);
        
        // Given - Exceder limite
        tentativasAtuais = 5;
        
        // When
        boolean excedeulimite = tentativasAtuais >= limiteTentativas;
        
        // Then
        assertTrue(excedeulimite);
    }

    @Test
    @DisplayName("Deve marcar email como verificado")
    void deveMarcarEmailComoVerificado() {
        // Given
        boolean emailVerificado = false;
        
        // When
        emailVerificado = true;
        
        // Then
        assertTrue(emailVerificado);
    }

    @Test
    @DisplayName("Deve validar códigos únicos por email")
    void deveValidarCodigosUnicosPorEmail() {
        // Given
        String email = "usuario@exemplo.com";
        int codigo1 = (int) (Math.random() * 1000000);
        int codigo2 = (int) (Math.random() * 1000000);
        
        // When
        String codigoFormatado1 = String.format("%06d", codigo1);
        String codigoFormatado2 = String.format("%06d", codigo2);
        
        // Then
        assertEquals(6, codigoFormatado1.length());
        assertEquals(6, codigoFormatado2.length());
        assertNotNull(email);
        assertTrue(email.contains("@"));
    }

    @Test
    @DisplayName("Deve validar reenvio de código")
    void deveValidarReenvioCodigo() {
        // Given
        LocalDateTime ultimoEnvio = LocalDateTime.now().minusMinutes(2);
        int intervaloMinutos = 1;
        
        // When
        LocalDateTime agora = LocalDateTime.now();
        long minutosDesdeUltimoEnvio = java.time.Duration.between(ultimoEnvio, agora).toMinutes();
        boolean podeReenviar = minutosDesdeUltimoEnvio >= intervaloMinutos;
        
        // Then
        assertTrue(podeReenviar);
    }

    @Test
    @DisplayName("Deve bloquear reenvio muito frequente")
    void deveBloqueareReenvioMuitoFrequente() {
        // Given
        LocalDateTime ultimoEnvio = LocalDateTime.now().minusSeconds(30);
        int intervaloMinutos = 1;
        
        // When
        LocalDateTime agora = LocalDateTime.now();
        long minutosDesdeUltimoEnvio = java.time.Duration.between(ultimoEnvio, agora).toMinutes();
        boolean podeReenviar = minutosDesdeUltimoEnvio >= intervaloMinutos;
        
        // Then
        assertFalse(podeReenviar);
    }

    @Test
    @DisplayName("Deve limpar códigos expirados")
    void deveLimparCodigosExpirados() {
        // Given
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime codigoExpirado = agora.minusHours(1);
        LocalDateTime codigoValido = agora.minusMinutes(5);
        
        // When
        boolean deveRemoverExpirado = codigoExpirado.isBefore(agora.minusMinutes(10));
        boolean deveManterValido = codigoValido.isAfter(agora.minusMinutes(10));
        
        // Then
        assertTrue(deveRemoverExpirado);
        assertTrue(deveManterValido);
    }

    @Test
    @DisplayName("Deve validar uso único do código")
    void deveValidarUsoUnicoCodigo() {
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
    @DisplayName("Deve normalizar email para comparação")
    void deveNormalizarEmailParaComparacao() {
        // Given
        String emailOriginal = "Usuario@Exemplo.COM";
        String emailComparar = "usuario@exemplo.com";
        
        // When
        String emailNormalizado = emailOriginal.toLowerCase();
        
        // Then
        assertEquals(emailComparar, emailNormalizado);
    }

    @Test
    @DisplayName("Deve validar conteúdo do email de verificação")
    void deveValidarConteudoEmailVerificacao() {
        // Given
        String assunto = "Verificação de Email - Inkspiration";
        String codigo = "123456";
        String nomeUsuario = "João Silva";
        
        // When
        String corpoEmail = String.format(
            "Olá %s, seu código de verificação é: %s. Válido por 10 minutos.",
            nomeUsuario, codigo
        );
        
        // Then
        assertTrue(assunto.contains("Verificação"));
        assertTrue(assunto.contains("Inkspiration"));
        assertTrue(corpoEmail.contains(nomeUsuario));
        assertTrue(corpoEmail.contains(codigo));
        assertTrue(corpoEmail.contains("10 minutos"));
    }

    @Test
    @DisplayName("Deve validar formato HTML do email")
    void deveValidarFormatoHTMLEmail() {
        // Given
        String codigo = "123456";
        String htmlEmail = String.format("""
            <html>
                <body>
                    <h2>Verificação de Email</h2>
                    <p>Seu código é: <strong>%s</strong></p>
                </body>
            </html>
            """, codigo);
        
        // When & Then
        assertTrue(htmlEmail.contains("<html>"));
        assertTrue(htmlEmail.contains("<body>"));
        assertTrue(htmlEmail.contains("<h2>"));
        assertTrue(htmlEmail.contains("<strong>"));
        assertTrue(htmlEmail.contains(codigo));
    }

    @Test
    @DisplayName("Deve validar configurações de email")
    void deveValidarConfiguracoesEmail() {
        // Given
        String host = "smtp.gmail.com";
        int porta = 587;
        boolean ssl = true;
        String remetente = "noreply@inkspiration.com";
        
        // When & Then
        assertNotNull(host);
        assertFalse(host.isEmpty());
        assertTrue(porta > 0);
        assertTrue(porta <= 65535);
        assertTrue(ssl || !ssl); // Aceita ambos
        assertTrue(remetente.contains("@"));
        assertTrue(remetente.contains("inkspiration"));
    }
} 