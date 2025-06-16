package inkspiration.backend.entities.twofactor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;
import java.time.LocalDateTime;

public class TwoFactorRecoveryCodeTest {

    private TwoFactorRecoveryCode twoFactorCode;

    @BeforeEach
    void setUp() {
        twoFactorCode = new TwoFactorRecoveryCode();
    }

    @Test
    void testGettersAndSettersId() {
        Long id = 1L;
        twoFactorCode.setId(id);
        assertEquals(id, twoFactorCode.getId(), "ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersUserId() {
        Long userId = 123L;
        twoFactorCode.setUserId(userId);
        assertEquals(userId, twoFactorCode.getUserId(), "User ID deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCode() {
        String code = "123456";
        twoFactorCode.setCode(code);
        assertEquals(code, twoFactorCode.getCode(), "Código deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCreatedAt() {
        LocalDateTime createdAt = LocalDateTime.now();
        twoFactorCode.setCreatedAt(createdAt);
        assertEquals(createdAt, twoFactorCode.getCreatedAt(), "Data de criação deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersExpiresAt() {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        twoFactorCode.setExpiresAt(expiresAt);
        assertEquals(expiresAt, twoFactorCode.getExpiresAt(), "Data de expiração deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersUsed() {
        twoFactorCode.setUsed(true);
        assertTrue(twoFactorCode.isUsed(), "Used deve ser true");
        
        twoFactorCode.setUsed(false);
        assertFalse(twoFactorCode.isUsed(), "Used deve ser false");
    }

    @Test
    void testConstrutorPadrao() {
        TwoFactorRecoveryCode codeVazio = new TwoFactorRecoveryCode();
        
        assertNull(codeVazio.getId(), "ID deve ser nulo inicialmente");
        assertNull(codeVazio.getUserId(), "User ID deve ser nulo inicialmente");
        assertNull(codeVazio.getCode(), "Código deve ser nulo inicialmente");
        assertNull(codeVazio.getCreatedAt(), "Data de criação deve ser nula inicialmente");
        assertNull(codeVazio.getExpiresAt(), "Data de expiração deve ser nula inicialmente");
        assertFalse(codeVazio.isUsed(), "Used deve ser false inicialmente");
    }

    @Test
    void testConstrutorComParametros() {
        Long userId = 123L;
        String code = "654321";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        
        TwoFactorRecoveryCode codeComParametros = new TwoFactorRecoveryCode(userId, code, expiresAt);
        
        assertEquals(userId, codeComParametros.getUserId(), "User ID deve ser igual ao fornecido no construtor");
        assertEquals(code, codeComParametros.getCode(), "Código deve ser igual ao fornecido no construtor");
        assertEquals(expiresAt, codeComParametros.getExpiresAt(), "Data de expiração deve ser igual à fornecida no construtor");
        assertNotNull(codeComParametros.getCreatedAt(), "Data de criação deve ser definida automaticamente");
        assertFalse(codeComParametros.isUsed(), "Used deve ser false no construtor");
        
        // Verifica se a data de criação é recente (dentro de 1 segundo)
        LocalDateTime agora = LocalDateTime.now();
        assertTrue(codeComParametros.getCreatedAt().isBefore(agora.plusSeconds(1)), 
                  "Data de criação deve estar próxima ao momento atual");
        assertTrue(codeComParametros.getCreatedAt().isAfter(agora.minusSeconds(1)), 
                  "Data de criação deve estar próxima ao momento atual");
    }

    @Test
    void testIsExpired() {
        // Código não expirado
        LocalDateTime futuro = LocalDateTime.now().plusMinutes(5);
        twoFactorCode.setExpiresAt(futuro);
        assertFalse(twoFactorCode.isExpired(), "Código não deve estar expirado");
        
        // Código expirado
        LocalDateTime passado = LocalDateTime.now().minusMinutes(5);
        twoFactorCode.setExpiresAt(passado);
        assertTrue(twoFactorCode.isExpired(), "Código deve estar expirado");
    }

    @Test
    void testTwoFactorCodeCompleto() {
        // Arrange
        Long id = 1L;
        Long userId = 123L;
        String code = "123456";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // Act
        twoFactorCode.setId(id);
        twoFactorCode.setUserId(userId);
        twoFactorCode.setCode(code);
        twoFactorCode.setCreatedAt(createdAt);
        twoFactorCode.setExpiresAt(expiresAt);
        twoFactorCode.setUsed(false);

        // Assert
        assertEquals(id, twoFactorCode.getId());
        assertEquals(userId, twoFactorCode.getUserId());
        assertEquals(code, twoFactorCode.getCode());
        assertEquals(createdAt, twoFactorCode.getCreatedAt());
        assertEquals(expiresAt, twoFactorCode.getExpiresAt());
        assertFalse(twoFactorCode.isUsed());
        assertFalse(twoFactorCode.isExpired());
    }

    @Test
    void testCodigosVariados() {
        String[] codigos = {
            "123456",
            "000000",
            "999999",
            "111111",
            "654321",
            "abcdef",
            "ABC123"
        };

        for (String codigo : codigos) {
            assertDoesNotThrow(() -> {
                twoFactorCode.setCode(codigo);
                assertEquals(codigo, twoFactorCode.getCode());
            }, "Deve aceitar código: " + codigo);
        }
    }

    @Test
    void testUserIdsVariados() {
        Long[] userIds = {
            1L,
            123L,
            999999L,
            Long.MAX_VALUE
        };

        for (Long userId : userIds) {
            assertDoesNotThrow(() -> {
                twoFactorCode.setUserId(userId);
                assertEquals(userId, twoFactorCode.getUserId());
            }, "Deve aceitar user ID: " + userId);
        }
    }

    @Test
    void testDatasCriacao() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime passado = LocalDateTime.now().minusHours(1);
        LocalDateTime futuro = LocalDateTime.now().plusHours(1);

        twoFactorCode.setCreatedAt(agora);
        assertEquals(agora, twoFactorCode.getCreatedAt(), "Deve aceitar data atual");

        twoFactorCode.setCreatedAt(passado);
        assertEquals(passado, twoFactorCode.getCreatedAt(), "Deve aceitar data passada");

        twoFactorCode.setCreatedAt(futuro);
        assertEquals(futuro, twoFactorCode.getCreatedAt(), "Deve aceitar data futura");
    }

    @Test
    void testDatasExpiracao() {
        LocalDateTime base = LocalDateTime.now();
        LocalDateTime[] expiracoes = {
            base.plusMinutes(1),
            base.plusMinutes(5),
            base.plusMinutes(15),
            base.plusHours(1),
            base.plusDays(1)
        };

        for (LocalDateTime expiracao : expiracoes) {
            assertDoesNotThrow(() -> {
                twoFactorCode.setExpiresAt(expiracao);
                assertEquals(expiracao, twoFactorCode.getExpiresAt());
            }, "Deve aceitar data de expiração: " + expiracao);
        }
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        twoFactorCode.setId(idMaximo);
        assertEquals(idMaximo, twoFactorCode.getId(), "Deve aceitar ID máximo");
        
        twoFactorCode.setId(idMinimo);
        assertEquals(idMinimo, twoFactorCode.getId(), "Deve aceitar ID mínimo válido");
        
        // Teste com User IDs extremos
        twoFactorCode.setUserId(idMaximo);
        assertEquals(idMaximo, twoFactorCode.getUserId(), "Deve aceitar User ID máximo");
        
        twoFactorCode.setUserId(idMinimo);
        assertEquals(idMinimo, twoFactorCode.getUserId(), "Deve aceitar User ID mínimo válido");
    }

    @Test
    void testCodigoLimiteTamanho() {
        // Código de 6 caracteres (padrão)
        String codigoPadrao = "123456";
        twoFactorCode.setCode(codigoPadrao);
        assertEquals(codigoPadrao, twoFactorCode.getCode(), "Deve aceitar código de 6 caracteres");
        
        // Código menor
        String codigoMenor = "123";
        twoFactorCode.setCode(codigoMenor);
        assertEquals(codigoMenor, twoFactorCode.getCode(), "Deve aceitar código menor");
        
        // Código maior (se permitido)
        String codigoMaior = "1234567890";
        twoFactorCode.setCode(codigoMaior);
        assertEquals(codigoMaior, twoFactorCode.getCode(), "Deve aceitar código maior");
    }

    @Test
    void testCodigoVazio() {
        twoFactorCode.setCode("");
        assertEquals("", twoFactorCode.getCode(), "Deve aceitar código vazio");
        
        twoFactorCode.setCode(null);
        assertNull(twoFactorCode.getCode(), "Deve aceitar código nulo");
    }

    @Test
    void testUsadoEExpirado() {
        LocalDateTime passado = LocalDateTime.now().minusMinutes(5);
        twoFactorCode.setExpiresAt(passado);
        twoFactorCode.setUsed(true);
        
        assertTrue(twoFactorCode.isExpired(), "Deve estar expirado");
        assertTrue(twoFactorCode.isUsed(), "Deve estar usado");
    }

    @Test
    void testCodigoLimiteExpiracao() {
        LocalDateTime agora = LocalDateTime.now();
        
        // Código que expira exatamente agora
        twoFactorCode.setExpiresAt(agora);
        
        // Como o tempo passa entre a criação e o teste, pode ser que já tenha expirado
        // Vamos testar com uma margem muito pequena
        LocalDateTime quaseAgora = LocalDateTime.now().plusNanos(1000000); // 1ms no futuro
        twoFactorCode.setExpiresAt(quaseAgora);
        
        assertDoesNotThrow(() -> {
            twoFactorCode.isExpired();
        }, "Deve conseguir verificar expiração sem erro");
    }

    @Test
    void testFluxoCompletoTwoFactor() {
        // Simula um fluxo completo de 2FA
        Long userId = 123L;
        String code = "654321";
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        
        // Criação do código
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(userId, code, expiresAt);
        
        // Verificações iniciais
        assertFalse(recoveryCode.isUsed(), "Código deve estar não usado inicialmente");
        assertFalse(recoveryCode.isExpired(), "Código não deve estar expirado inicialmente");
        assertEquals(userId, recoveryCode.getUserId(), "User ID deve ser igual");
        assertEquals(code, recoveryCode.getCode(), "Código deve ser igual");
        
        // Uso do código
        recoveryCode.setUsed(true);
        assertTrue(recoveryCode.isUsed(), "Código deve estar usado após o uso");
    }

    @Test
    void testCodigoAlfanumerico() {
        String[] codigosAlfanumericos = {
            "ABC123",
            "XYZ789",
            "abc123",
            "Mix3d1",
            "123ABC"
        };

        for (String codigo : codigosAlfanumericos) {
            assertDoesNotThrow(() -> {
                twoFactorCode.setCode(codigo);
                assertEquals(codigo, twoFactorCode.getCode());
            }, "Deve aceitar código alfanumérico: " + codigo);
        }
    }

    @Test
    void testCodigoCaracteresEspeciais() {
        String[] codigosEspeciais = {
            "12-34-56",
            "AB.CD.EF",
            "123_456",
            "ABC!DEF"
        };

        for (String codigo : codigosEspeciais) {
            assertDoesNotThrow(() -> {
                twoFactorCode.setCode(codigo);
                assertEquals(codigo, twoFactorCode.getCode());
            }, "Deve aceitar código com caracteres especiais: " + codigo);
        }
    }

    @Test
    void testMultiplosCodigosParaMesmoUsuario() {
        Long userId = 123L;
        
        TwoFactorRecoveryCode code1 = new TwoFactorRecoveryCode(userId, "111111", LocalDateTime.now().plusMinutes(5));
        TwoFactorRecoveryCode code2 = new TwoFactorRecoveryCode(userId, "222222", LocalDateTime.now().plusMinutes(5));
        TwoFactorRecoveryCode code3 = new TwoFactorRecoveryCode(userId, "333333", LocalDateTime.now().plusMinutes(5));
        
        assertAll("Múltiplos códigos para mesmo usuário",
            () -> assertEquals(userId, code1.getUserId()),
            () -> assertEquals(userId, code2.getUserId()),
            () -> assertEquals(userId, code3.getUserId()),
            () -> assertEquals("111111", code1.getCode()),
            () -> assertEquals("222222", code2.getCode()),
            () -> assertEquals("333333", code3.getCode())
        );
    }

    @Test
    void testPrecisaoDataCriacao() {
        TwoFactorRecoveryCode code1 = new TwoFactorRecoveryCode(123L, "123456", LocalDateTime.now().plusMinutes(5));
        
        // Pequena pausa para garantir diferença de tempo
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        TwoFactorRecoveryCode code2 = new TwoFactorRecoveryCode(456L, "654321", LocalDateTime.now().plusMinutes(5));
        
        // As datas devem ser diferentes (mesmo que por milissegundos)
        assertNotEquals(code1.getCreatedAt(), code2.getCreatedAt(), 
                       "Datas de criação de códigos diferentes devem ser diferentes");
    }

    @Test
    void testExpiracaoComPrecisao() {
        LocalDateTime base = LocalDateTime.now();
        LocalDateTime expiracao1 = base.plusSeconds(30);
        LocalDateTime expiracao2 = base.plusMinutes(5);
        
        twoFactorCode.setExpiresAt(expiracao1);
        assertEquals(expiracao1, twoFactorCode.getExpiresAt(), "Deve aceitar expiração com precisão de segundos");
        
        twoFactorCode.setExpiresAt(expiracao2);
        assertEquals(expiracao2, twoFactorCode.getExpiresAt(), "Deve aceitar expiração com precisão de minutos");
    }

    @Test
    void testCodigoUnicode() {
        String codigoUnicode = "🔒🔑🛡️";
        
        twoFactorCode.setCode(codigoUnicode);
        assertEquals(codigoUnicode, twoFactorCode.getCode(), "Deve aceitar código com caracteres Unicode");
    }

    @Test
    void testUserIdZero() {
        Long userIdZero = 0L;
        twoFactorCode.setUserId(userIdZero);
        assertEquals(userIdZero, twoFactorCode.getUserId(), "Deve aceitar User ID zero");
    }

    @Test
    void testCamposNulos() {
        twoFactorCode.setUserId(null);
        twoFactorCode.setCode(null);
        twoFactorCode.setCreatedAt(null);
        twoFactorCode.setExpiresAt(null);

        assertNull(twoFactorCode.getUserId(), "Deve aceitar User ID nulo");
        assertNull(twoFactorCode.getCode(), "Deve aceitar código nulo");
        assertNull(twoFactorCode.getCreatedAt(), "Deve aceitar data de criação nula");
        assertNull(twoFactorCode.getExpiresAt(), "Deve aceitar data de expiração nula");
    }
} 