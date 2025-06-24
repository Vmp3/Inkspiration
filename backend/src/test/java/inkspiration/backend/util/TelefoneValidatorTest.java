package inkspiration.backend.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TelefoneValidator - Testes Completos")
class TelefoneValidatorTest {

    // Testes para isValid()
    @Test
    @DisplayName("Deve validar telefone fixo com formato completo")
    void deveValidarTelefoneFixoComFormatoCompleto() {
        assertTrue(TelefoneValidator.isValid("(11) 2345-6789"));
        assertTrue(TelefoneValidator.isValid("(21) 3456-7890"));
    }

    @Test
    @DisplayName("Deve validar celular com formato completo")
    void deveValidarCelularComFormatoCompleto() {
        assertTrue(TelefoneValidator.isValid("(11) 91234-5678"));
        assertTrue(TelefoneValidator.isValid("(21) 98765-4321"));
    }

    @Test
    @DisplayName("Deve validar telefone fixo sem formatação")
    void deveValidarTelefoneFixoSemFormatacao() {
        assertTrue(TelefoneValidator.isValid("1123456789"));
        assertTrue(TelefoneValidator.isValid("2134567890"));
    }

    @Test
    @DisplayName("Deve validar celular sem formatação")
    void deveValidarCelularSemFormatacao() {
        assertTrue(TelefoneValidator.isValid("11912345678"));
        assertTrue(TelefoneValidator.isValid("21987654321"));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar telefones inválidos")
    @ValueSource(strings = {
        "(00) 1234-5678",  // DDD inválido
        "(11) 0234-5678",  // Número fixo inválido
        "(11) 90234-5678", // Número celular inválido
        "(11) 1234-567",   // Número incompleto
        "(11) 1234-56789", // Número longo demais
        "123456789",       // Número sem DDD
        "abc12345678",     // Caracteres inválidos
        "(11)1234-5678"    // Espaçamento incorreto
    })
    void deveRejeitarTelefonesInvalidos(String telefone) {
        assertFalse(TelefoneValidator.isValid(telefone));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar telefones nulos ou vazios")
    @NullAndEmptySource
    void deveRejeitarTelefonesNulosOuVazios(String telefone) {
        assertFalse(TelefoneValidator.isValid(telefone));
    }

    // Testes para isCelular()
    @Test
    @DisplayName("Deve identificar celular com formato completo")
    void deveIdentificarCelularComFormatoCompleto() {
        assertTrue(TelefoneValidator.isCelular("(11) 91234-5678"));
        assertTrue(TelefoneValidator.isCelular("(21) 98765-4321"));
    }

    @Test
    @DisplayName("Deve identificar celular sem formatação")
    void deveIdentificarCelularSemFormatacao() {
        assertTrue(TelefoneValidator.isCelular("11912345678"));
        assertTrue(TelefoneValidator.isCelular("21987654321"));
    }

    @Test
    @DisplayName("Deve rejeitar telefone fixo como celular")
    void deveRejeitarTelefoneFixoComoCelular() {
        assertFalse(TelefoneValidator.isCelular("(11) 2345-6789"));
        assertFalse(TelefoneValidator.isCelular("1123456789"));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar celulares inválidos")
    @ValueSource(strings = {
        "(11) 90234-5678", // 0 após o 9
        "(00) 91234-5678", // DDD inválido
        "(11) 9123-5678",  // Número incompleto
        "91234-5678",      // Sem DDD
        "abc91234567"      // Caracteres inválidos
    })
    void deveRejeitarCelularesInvalidos(String telefone) {
        assertFalse(TelefoneValidator.isCelular(telefone));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar celulares nulos ou vazios")
    @NullAndEmptySource
    void deveRejeitarCelularesNulosOuVazios(String telefone) {
        assertFalse(TelefoneValidator.isCelular(telefone));
    }

    // Testes para format()
    @Test
    @DisplayName("Deve formatar celular corretamente")
    void deveFormatarCelularCorretamente() {
        assertEquals("(11) 91234-5678", TelefoneValidator.format("11912345678"));
        assertEquals("(11) 91234-5678", TelefoneValidator.format("(11)912345678"));
        assertEquals("(11) 91234-5678", TelefoneValidator.format("(11) 91234-5678"));
    }

    @Test
    @DisplayName("Deve formatar telefone fixo corretamente")
    void deveFormatarTelefoneFixoCorretamente() {
        assertEquals("(11) 2345-6789", TelefoneValidator.format("1123456789"));
        assertEquals("(11) 2345-6789", TelefoneValidator.format("(11)23456789"));
        assertEquals("(11) 2345-6789", TelefoneValidator.format("(11) 2345-6789"));
    }

    @Test
    @DisplayName("Deve retornar null para telefone null")
    void deveRetornarNullParaTelefoneNull() {
        assertNull(TelefoneValidator.format(null));
    }

    @Test
    @DisplayName("Deve retornar original para telefone inválido")
    void deveRetornarOriginalParaTelefoneInvalido() {
        String telefoneInvalido = "123";
        assertEquals(telefoneInvalido, TelefoneValidator.format(telefoneInvalido));
    }

    // Testes para getFormatExample()
    @Test
    @DisplayName("Deve retornar exemplo de formato correto")
    void deveRetornarExemploDeFormatoCorreto() {
        String exemplo = TelefoneValidator.getFormatExample();
        assertNotNull(exemplo);
        assertTrue(exemplo.contains("(11) 91234-5678"));
        assertTrue(exemplo.contains("11912345678"));
        assertTrue(exemplo.contains("(11) 1234-5678"));
        assertTrue(exemplo.contains("1112345678"));
    }

    // Testes para getValidationMessage()
    @Test
    @DisplayName("Deve retornar mensagem para telefone nulo")
    void deveRetornarMensagemParaTelefoneNulo() {
        assertEquals("Telefone é obrigatório", TelefoneValidator.getValidationMessage(null));
    }

    @Test
    @DisplayName("Deve retornar mensagem para telefone vazio")
    void deveRetornarMensagemParaTelefoneVazio() {
        assertEquals("Telefone é obrigatório", TelefoneValidator.getValidationMessage(""));
        assertEquals("Telefone é obrigatório", TelefoneValidator.getValidationMessage("  "));
    }

    @Test
    @DisplayName("Deve retornar mensagem para telefone inválido")
    void deveRetornarMensagemParaTelefoneInvalido() {
        assertEquals("Telefone inválido. Use o formato (99) 99999-9999.", 
            TelefoneValidator.getValidationMessage("123"));
    }

    @Test
    @DisplayName("Deve retornar null para telefone válido")
    void deveRetornarNullParaTelefoneValido() {
        assertNull(TelefoneValidator.getValidationMessage("(11) 91234-5678"));
        assertNull(TelefoneValidator.getValidationMessage("11912345678"));
        assertNull(TelefoneValidator.getValidationMessage("(11) 2345-6789"));
        assertNull(TelefoneValidator.getValidationMessage("1123456789"));
    }
} 