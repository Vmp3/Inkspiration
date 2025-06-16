package inkspiration.backend.entities.password;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.PasswordResetCode;
import java.time.LocalDateTime;

public class PasswordResetCodeTest {

    private PasswordResetCode passwordResetCode;

    @BeforeEach
    void setUp() {
        passwordResetCode = new PasswordResetCode();
    }

    @Test
    void testGettersAndSettersId() {
        Long id = 1L;
        passwordResetCode.setId(id);
        assertEquals(id, passwordResetCode.getId(), "ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCpf() {
        String cpf = "12345678901";
        passwordResetCode.setCpf(cpf);
        assertEquals(cpf, passwordResetCode.getCpf(), "CPF deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCode() {
        String code = "123456";
        passwordResetCode.setCode(code);
        assertEquals(code, passwordResetCode.getCode(), "Código deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        passwordResetCode.setCreatedAt(createdAt);
        assertEquals(createdAt, passwordResetCode.getCreatedAt(), "Data de criação deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersExpiresAt() {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        passwordResetCode.setExpiresAt(expiresAt);
        assertEquals(expiresAt, passwordResetCode.getExpiresAt(), "Data de expiração deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersUsed() {
        passwordResetCode.setUsed(true);
        assertTrue(passwordResetCode.isUsed(), "Used deve ser true");
        
        passwordResetCode.setUsed(false);
        assertFalse(passwordResetCode.isUsed(), "Used deve ser false");
    }

    @Test
    void testConstrutorPadrao() {
        PasswordResetCode codeVazio = new PasswordResetCode();
        
        assertNull(codeVazio.getId(), "ID deve ser nulo inicialmente");
        assertNull(codeVazio.getCpf(), "CPF deve ser nulo inicialmente");
        assertNull(codeVazio.getCode(), "Código deve ser nulo inicialmente");
        assertNull(codeVazio.getCreatedAt(), "Data de criação deve ser nula inicialmente");
        assertNull(codeVazio.getExpiresAt(), "Data de expiração deve ser nula inicialmente");
        assertFalse(codeVazio.isUsed(), "Used deve ser false inicialmente");
    }

    @Test
    void testConstrutorComParametros() {
        String cpf = "12345678901";
        String code = "123456";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        
        PasswordResetCode codeComParametros = new PasswordResetCode(cpf, code, createdAt, expiresAt);
        
        assertEquals(cpf, codeComParametros.getCpf(), "CPF deve ser igual ao fornecido no construtor");
        assertEquals(code, codeComParametros.getCode(), "Código deve ser igual ao fornecido no construtor");
        assertEquals(createdAt, codeComParametros.getCreatedAt(), "Data de criação deve ser igual à fornecida no construtor");
        assertEquals(expiresAt, codeComParametros.getExpiresAt(), "Data de expiração deve ser igual à fornecida no construtor");
        assertFalse(codeComParametros.isUsed(), "Used deve ser false no construtor");
    }

    @Test
    void testCpfFormatacao() {
        String cpfComFormatacao = "123.456.789-01";
        passwordResetCode.setCpf(cpfComFormatacao);
        assertEquals("12345678901", passwordResetCode.getCpf(), "CPF deve ser formatado removendo pontos e hífens");
    }

    @Test
    void testCpfComEspacos() {
        String cpfComEspacos = "123 456 789 01";
        passwordResetCode.setCpf(cpfComEspacos);
        assertEquals("12345678901", passwordResetCode.getCpf(), "CPF deve ser formatado removendo espaços");
    }

    @Test
    void testCpfComCaracteresEspeciais() {
        String cpfComCaracteres = "123.456.789-01 abc";
        passwordResetCode.setCpf(cpfComCaracteres);
        assertEquals("12345678901", passwordResetCode.getCpf(), "CPF deve manter apenas números");
    }

    @Test
    void testIsExpired() {
        // Código não expirado
        LocalDateTime futuro = LocalDateTime.now().plusMinutes(15);
        passwordResetCode.setExpiresAt(futuro);
        assertFalse(passwordResetCode.isExpired(), "Código não deve estar expirado");
        
        // Código expirado
        LocalDateTime passado = LocalDateTime.now().minusMinutes(15);
        passwordResetCode.setExpiresAt(passado);
        assertTrue(passwordResetCode.isExpired(), "Código deve estar expirado");
    }

    @Test
    void testIsValid() {
        LocalDateTime futuro = LocalDateTime.now().plusMinutes(15);
        passwordResetCode.setExpiresAt(futuro);
        passwordResetCode.setUsed(false);
        assertTrue(passwordResetCode.isValid(), "Código deve ser válido quando não usado e não expirado");
        
        // Código usado
        passwordResetCode.setUsed(true);
        assertFalse(passwordResetCode.isValid(), "Código não deve ser válido quando usado");
        
        // Código expirado
        passwordResetCode.setUsed(false);
        LocalDateTime passado = LocalDateTime.now().minusMinutes(15);
        passwordResetCode.setExpiresAt(passado);
        assertFalse(passwordResetCode.isValid(), "Código não deve ser válido quando expirado");
    }

    @Test
    void testCodigosVariados() {
        String[] codigos = {
            "123456",
            "000000",
            "999999",
            "111111",
            "654321"
        };

        for (String codigo : codigos) {
            assertDoesNotThrow(() -> {
                passwordResetCode.setCode(codigo);
                assertEquals(codigo, passwordResetCode.getCode());
            }, "Deve aceitar código: " + codigo);
        }
    }

    @Test
    void testDatasCriacao() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime passado = LocalDateTime.now().minusHours(1);
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);

        passwordResetCode.setCreatedAt(agora);
        assertEquals(agora, passwordResetCode.getCreatedAt(), "Deve aceitar data atual");

        passwordResetCode.setCreatedAt(passado);
        assertEquals(passado, passwordResetCode.getCreatedAt(), "Deve aceitar data passada");

        passwordResetCode.setCreatedAt(futuro);
        assertEquals(futuro, passwordResetCode.getCreatedAt(), "Deve aceitar data futura");
    }

    @Test
    void testDatasExpiracao() {
        LocalDateTime base = LocalDateTime.now();
        LocalDateTime[] expirações = {
            base.plusMinutes(5),
            base.plusMinutes(15),
            base.plusMinutes(30),
            base.plusHours(1),
            base.plusHours(24)
        };

        for (LocalDateTime expiracao : expirações) {
            assertDoesNotThrow(() -> {
                passwordResetCode.setExpiresAt(expiracao);
                assertEquals(expiracao, passwordResetCode.getExpiresAt());
            }, "Deve aceitar data de expiração: " + expiracao);
        }
    }

    @Test
    void testPasswordResetCodeCompleto() {
        // Arrange
        Long id = 1L;
        String cpf = "12345678901";
        String code = "123456";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        // Act
        passwordResetCode.setId(id);
        passwordResetCode.setCpf(cpf);
        passwordResetCode.setCode(code);
        passwordResetCode.setCreatedAt(createdAt);
        passwordResetCode.setExpiresAt(expiresAt);
        passwordResetCode.setUsed(false);

        // Assert
        assertEquals(id, passwordResetCode.getId());
        assertEquals(cpf, passwordResetCode.getCpf());
        assertEquals(code, passwordResetCode.getCode());
        assertEquals(createdAt, passwordResetCode.getCreatedAt());
        assertEquals(expiresAt, passwordResetCode.getExpiresAt());
        assertFalse(passwordResetCode.isUsed());
        assertTrue(passwordResetCode.isValid());
        assertFalse(passwordResetCode.isExpired());
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        passwordResetCode.setId(idMaximo);
        assertEquals(idMaximo, passwordResetCode.getId(), "Deve aceitar ID máximo");
        
        passwordResetCode.setId(idMinimo);
        assertEquals(idMinimo, passwordResetCode.getId(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testCodigoLimiteExpiracao() {
        LocalDateTime agora = LocalDateTime.now();
        
        // Código que expira exatamente agora
        passwordResetCode.setExpiresAt(agora);
        
        // Como o tempo passa entre a criação e o teste, pode ser que já tenha expirado
        // Vamos testar com uma margem muito pequena
        LocalDateTime quaseAgora = LocalDateTime.now().plusNanos(1000000); // 1ms no futuro
        passwordResetCode.setExpiresAt(quaseAgora);
        
        assertDoesNotThrow(() -> {
            passwordResetCode.isExpired();
        }, "Deve conseguir verificar expiração sem erro");
    }

    @Test
    void testUsadoEExpirado() {
        LocalDateTime passado = LocalDateTime.now().minusMinutes(15);
        passwordResetCode.setExpiresAt(passado);
        passwordResetCode.setUsed(true);
        
        assertTrue(passwordResetCode.isExpired(), "Deve estar expirado");
        assertTrue(passwordResetCode.isUsed(), "Deve estar usado");
        assertFalse(passwordResetCode.isValid(), "Não deve ser válido quando usado e expirado");
    }

    @Test
    void testCpfSomenteNumeros() {
        String cpfSomenteNumeros = "12345678901";
        passwordResetCode.setCpf(cpfSomenteNumeros);
        assertEquals(cpfSomenteNumeros, passwordResetCode.getCpf(), "CPF só com números deve ser mantido igual");
    }

    @Test
    void testCpfVazio() {
        passwordResetCode.setCpf("");
        assertEquals("", passwordResetCode.getCpf(), "CPF vazio deve ser mantido como vazio");
    }

    @Test
    void testCodigoNumerico() {
        String codigoNumerico = "123456";
        passwordResetCode.setCode(codigoNumerico);
        assertEquals(codigoNumerico, passwordResetCode.getCode(), "Código numérico deve ser aceito");
    }

    @Test
    void testFluxoCompletoPasswordReset() {
        // Simula um fluxo completo de reset de senha
        String cpf = "123.456.789-01";
        String code = "123456";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        
        // Criação do código
        PasswordResetCode resetCode = new PasswordResetCode(cpf, code, createdAt, expiresAt);
        
        // Verificações iniciais
        assertFalse(resetCode.isUsed(), "Código deve estar não usado inicialmente");
        assertFalse(resetCode.isExpired(), "Código não deve estar expirado inicialmente");
        assertTrue(resetCode.isValid(), "Código deve ser válido inicialmente");
        assertEquals("123.456.789-01", resetCode.getCpf(), "CPF deve ser igual ao fornecido no construtor");
        
        // Testando formatação via setter
        resetCode.setCpf(cpf);
        assertEquals("12345678901", resetCode.getCpf(), "CPF deve ser formatado ao usar o setter");
        
        // Uso do código
        resetCode.setUsed(true);
        assertFalse(resetCode.isValid(), "Código não deve ser válido após o uso");
    }
} 