package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.CpfValidator;

public class ValidaCpfTest {

    @Test
    void testCpfValido() {
        assertTrue(CpfValidator.isValid("11144477735"), "CPF deve ser válido");
        assertTrue(CpfValidator.isValid("123.456.789-09"), "CPF formatado deve ser válido");
        assertTrue(CpfValidator.isValid("00000000191"), "CPF deve ser válido");
    }

    @Test
    void testCpfInvalido() {
        assertFalse(CpfValidator.isValid("12345678900"), "CPF deve ser inválido");
        assertFalse(CpfValidator.isValid("1234567890"), "CPF com 10 dígitos deve ser inválido");
        assertFalse(CpfValidator.isValid("123456789012"), "CPF com 12 dígitos deve ser inválido");
        assertFalse(CpfValidator.isValid("abcdefghijk"), "CPF com letras deve ser inválido");
        assertFalse(CpfValidator.isValid("11111111111"), "CPF com todos os dígitos iguais deve ser inválido");
        assertFalse(CpfValidator.isValid("22222222222"), "CPF com todos os dígitos iguais deve ser inválido");
        assertFalse(CpfValidator.isValid("00000000000"), "CPF com todos os zeros deve ser inválido");
    }

    @Test
    void testCpfVazio() {
        assertFalse(CpfValidator.isValid(""), "CPF vazio deve ser inválido");
        assertFalse(CpfValidator.isValid(null), "CPF nulo deve ser inválido");
    }

    @Test
    void testCpfComCaracteresEspeciais() {
        assertTrue(CpfValidator.isValid("111.444.777-35"), "CPF com formatação deve ser válido");
        assertTrue(CpfValidator.isValid("111 444 777 35"), "CPF com espaços deve ser válido");
        assertTrue(CpfValidator.isValid("111-444-777-35"), "CPF com hífens deve ser válido");
    }

    @Test
    void testFormataCpf() {
        assertEquals("111.444.777-35", CpfValidator.format("11144477735"), "CPF deve ser formatado corretamente");
        assertEquals("111.444.777-35", CpfValidator.format("111.444.777-35"), "CPF já formatado deve permanecer igual");
        assertEquals("1234567890", CpfValidator.format("1234567890"), "CPF inválido não deve ser formatado");
    }
} 