package inkspiration.backend.entities.twoFactorRecoveryCode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.TwoFactorRecoveryCode;

@DisplayName("Testes gerais da entidade TwoFactorRecoveryCode")
public class TwoFactorRecoveryCodeEntityTest {

    @Test
    @DisplayName("Deve criar código com construtor padrão")
    void deveCriarCodigoComConstrutorPadrao() {
        TwoFactorRecoveryCode code = new TwoFactorRecoveryCode();
        
        assertNotNull(code);
        assertNull(code.getId());
        assertNull(code.getUserId());
        assertNull(code.getCode());
        assertNull(code.getCreatedAt());
        assertNull(code.getExpiresAt());
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Deve criar código de recuperação com construtor completo")
    void deveCriarCodigoComConstrutorCompleto() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        TwoFactorRecoveryCode codigo = new TwoFactorRecoveryCode(1L, "ABC123", expiresAt);
        
        assertEquals(1L, codigo.getUserId());
        assertEquals("ABC123", codigo.getCode());
        assertNotNull(codigo.getCreatedAt());
        assertEquals(expiresAt, codigo.getExpiresAt());
        assertFalse(codigo.isUsed());
        assertNull(codigo.getId());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        TwoFactorRecoveryCode code = new TwoFactorRecoveryCode();
        Long id = 456L;
        
        code.setId(id);
        assertEquals(id, code.getId());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        TwoFactorRecoveryCode code = new TwoFactorRecoveryCode();
        
        code.setId(null);
        assertNull(code.getId());
    }

    @Test
    @DisplayName("Construtor completo deve definir createdAt automaticamente")
    void construtorCompletoDeveDefinirCreatedAtAutomaticamente() {
        LocalDateTime antes = LocalDateTime.now();
        TwoFactorRecoveryCode codigo = new TwoFactorRecoveryCode(1L, "ABC123", LocalDateTime.now().plusHours(1));
        LocalDateTime depois = LocalDateTime.now();
        
        assertNotNull(codigo.getCreatedAt());
        assertTrue(codigo.getCreatedAt().isAfter(antes) || codigo.getCreatedAt().isEqual(antes));
        assertTrue(codigo.getCreatedAt().isBefore(depois) || codigo.getCreatedAt().isEqual(depois));
    }

    @Test
    @DisplayName("Construtor completo deve usar setters com validação")
    void construtorCompletoDeveUsarSettersComValidacao() {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
        
        // Testa se o construtor valida userId nulo
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new TwoFactorRecoveryCode(null, "ABC123", expiresAt);
        });
        assertEquals("O ID do usuário não pode ser nulo", exception1.getMessage());
        
        // Testa se o construtor valida código nulo
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new TwoFactorRecoveryCode(1L, null, expiresAt);
        });
        assertEquals("O código não pode ser nulo ou vazio", exception2.getMessage());
        
        // Testa se o construtor valida data de expiração nula
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            new TwoFactorRecoveryCode(1L, "ABC123", null);
        });
        assertEquals("A data de expiração não pode ser nula", exception3.getMessage());
    }
} 