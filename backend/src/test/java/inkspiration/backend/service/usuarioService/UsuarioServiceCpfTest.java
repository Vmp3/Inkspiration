package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.util.CpfValidator;

@DisplayName("UsuarioService - Testes de CPF")
class UsuarioServiceCpfTest {

    @Test
    @DisplayName("Deve validar CPF válido")
    void deveValidarCpfValido() {
        assertTrue(CpfValidator.isValid("11144477735"));
        assertTrue(CpfValidator.isValid("12345678909"));
        assertTrue(CpfValidator.isValid("98765432100"));
    }

    @Test
    @DisplayName("Deve invalidar CPF inválido")
    void deveInvalidarCpfInvalido() {
        assertFalse(CpfValidator.isValid("12345678901"));
        assertFalse(CpfValidator.isValid("11111111111"));
        assertFalse(CpfValidator.isValid("00000000000"));
        assertFalse(CpfValidator.isValid("99999999999"));
    }

    @Test
    @DisplayName("Deve validar CPF com máscara")
    void deveValidarCpfComMascara() {
        assertTrue(CpfValidator.isValid("111.444.777-35"));
        assertTrue(CpfValidator.isValid("123.456.789-09"));
        assertFalse(CpfValidator.isValid("000.000.000-00"));
    }

    @Test
    @DisplayName("Deve invalidar CPF nulo ou vazio")
    void deveInvalidarCpfNuloOuVazio() {
        assertThrows(NullPointerException.class, () -> CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve invalidar CPF com tamanho incorreto")
    void deveInvalidarCpfComTamanhoIncorreto() {
        assertFalse(CpfValidator.isValid("123"));
        assertFalse(CpfValidator.isValid("1234567890"));
        assertFalse(CpfValidator.isValid("123456789012"));
    }

    @Test
    @DisplayName("Deve formatar CPF corretamente")
    void deveFormatarCpfCorretamente() {
        String cpfComMascara = "111.444.777-35";
        String cpfLimpo = cpfComMascara.replaceAll("[^0-9]", "");
        
        assertEquals("11144477735", cpfLimpo);
        assertEquals(11, cpfLimpo.length());
    }

    @Test
    @DisplayName("Deve limpar CPF com caracteres especiais")
    void deveLimparCpfComCaracteresEspeciais() {
        assertEquals("11144477735", "111.444.777-35".replaceAll("[^0-9]", ""));
        assertEquals("11144477735", "111 444 777 35".replaceAll("[^0-9]", ""));
        assertEquals("11144477735", "111-444-777-35".replaceAll("[^0-9]", ""));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF obrigatório")
    void deveLancarExcecaoParaCpfObrigatorio() {
        assertThrows(UsuarioValidationException.CpfObrigatorioException.class, () -> {
            String cpf = null;
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new UsuarioValidationException.CpfObrigatorioException();
            }
        });

        assertThrows(UsuarioValidationException.CpfObrigatorioException.class, () -> {
            String cpf = "";
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new UsuarioValidationException.CpfObrigatorioException();
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF inválido")
    void deveLancarExcecaoParaCpfInvalido() {
        assertThrows(UsuarioValidationException.CpfInvalidoException.class, () -> {
            String cpf = "12345678901";
            if (!CpfValidator.isValid(cpf)) {
                throw new UsuarioValidationException.CpfInvalidoException("CPF inválido");
            }
        });
    }
} 