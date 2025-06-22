package inkspiration.backend.entities.passwordResetCode;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.PasswordResetCode;

@DisplayName("Testes gerais da entidade PasswordResetCode")
public class PasswordResetCodeEntityTest {

    @Test
    @DisplayName("Deve criar código com construtor padrão")
    void deveCriarCodigoComConstrutorPadrao() {
        PasswordResetCode code = new PasswordResetCode();
        
        assertNotNull(code);
        assertNull(code.getId());
        assertNull(code.getCpf());
        assertNull(code.getCode());
        assertNull(code.getCreatedAt());
        assertNull(code.getExpiresAt());
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Deve criar código com construtor completo")
    void deveCriarCodigoComConstrutorCompleto() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(1);
        
        PasswordResetCode code = new PasswordResetCode("12345678901", "ABC123", createdAt, expiresAt);
        
        assertEquals("12345678901", code.getCpf());
        assertEquals("ABC123", code.getCode());
        assertEquals(createdAt, code.getCreatedAt());
        assertEquals(expiresAt, code.getExpiresAt());
        assertFalse(code.isUsed());
        assertNull(code.getId());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        PasswordResetCode code = new PasswordResetCode();
        Long id = 456L;
        
        code.setId(id);
        assertEquals(id, code.getId());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        PasswordResetCode code = new PasswordResetCode();
        
        code.setId(null);
        assertNull(code.getId());
    }

    @Test
    @DisplayName("Deve definir e obter status de usado")
    void deveDefinirEObterStatusUsado() {
        PasswordResetCode code = new PasswordResetCode();
        
        code.setUsed(true);
        assertTrue(code.isUsed());
        
        code.setUsed(false);
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Status padrão de usado deve ser false")
    void statusPadraoUsadoDeveSerFalse() {
        PasswordResetCode code = new PasswordResetCode();
        assertFalse(code.isUsed());
    }

    @Test
    @DisplayName("Deve verificar se código é válido - não usado e não expirado")
    void deveVerificarCodigoValido() {
        PasswordResetCode code = new PasswordResetCode();
        code.setExpiresAt(LocalDateTime.now().plusHours(1));
        code.setUsed(false);
        
        assertTrue(code.isValid());
    }

    @Test
    @DisplayName("Deve verificar se código é inválido - usado")
    void deveVerificarCodigoInvalidoUsado() {
        PasswordResetCode code = new PasswordResetCode();
        code.setExpiresAt(LocalDateTime.now().plusHours(1));
        code.setUsed(true);
        
        assertFalse(code.isValid());
    }

    @Test
    @DisplayName("Construtor completo deve usar setters com validação")
    void construtorCompletoDeveUsarSettersComValidacao() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusHours(1);
        
        // Testa se o construtor valida CPF nulo
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            new PasswordResetCode(null, "ABC123", createdAt, expiresAt);
        });
        assertEquals("O CPF não pode ser nulo", exception1.getMessage());
        
        // Testa se o construtor valida código nulo
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            new PasswordResetCode("12345678901", null, createdAt, expiresAt);
        });
        assertEquals("O código não pode ser nulo ou vazio", exception2.getMessage());
        
        // Testa se o construtor valida data de criação nula
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            new PasswordResetCode("12345678901", "ABC123", null, expiresAt);
        });
        assertEquals("A data de criação não pode ser nula", exception3.getMessage());
        
        // Testa se o construtor valida data de expiração nula
        Exception exception4 = assertThrows(IllegalArgumentException.class, () -> {
            new PasswordResetCode("12345678901", "ABC123", createdAt, null);
        });
        assertEquals("A data de expiração não pode ser nula", exception4.getMessage());
    }
} 