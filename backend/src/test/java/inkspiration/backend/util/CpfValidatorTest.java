package inkspiration.backend.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CpfValidator - Testes Completos")
class CpfValidatorTest {

    // Testes para isValid()
    @Test
    @DisplayName("Deve validar CPF com formato completo")
    void deveValidarCPFComFormatoCompleto() {
        assertTrue(CpfValidator.isValid("111.444.777-35"));
        assertTrue(CpfValidator.isValid("123.456.789-09"));
    }

    @Test
    @DisplayName("Deve validar CPF sem formatação")
    void deveValidarCPFSemFormatacao() {
        assertTrue(CpfValidator.isValid("11144477735"));
        assertTrue(CpfValidator.isValid("12345678909"));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar CPFs com dígitos iguais")
    @ValueSource(strings = {
        "000.000.000-00",
        "111.111.111-11",
        "222.222.222-22",
        "333.333.333-33",
        "444.444.444-44",
        "555.555.555-55",
        "666.666.666-66",
        "777.777.777-77",
        "888.888.888-88",
        "999.999.999-99"
    })
    void deveRejeitarCPFsComDigitosIguais(String cpf) {
        assertFalse(CpfValidator.isValid(cpf));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar CPFs inválidos")
    @ValueSource(strings = {
        "111.444.777-36", // Dígito verificador inválido
        "123.456.789-00", // Dígito verificador inválido
        "123.456.789",    // Incompleto
        "123456789012",   // Longo demais
        "abc.def.ghi-jk", // Caracteres inválidos
        "12345",          // Muito curto
        "1234567890"      // Faltando um dígito
    })
    void deveRejeitarCPFsInvalidos(String cpf) {
        assertFalse(CpfValidator.isValid(cpf));
    }

    @Test
    @DisplayName("Deve rejeitar CPFs nulos ou vazios")
    void deveRejeitarCPFsNulosOuVazios() {
        assertFalse(CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("   "));
    }

    // Testes para format()
    @Test
    @DisplayName("Deve formatar CPF corretamente")
    void deveFormatarCPFCorretamente() {
        assertEquals("111.444.777-35", CpfValidator.format("11144477735"));
        assertEquals("123.456.789-09", CpfValidator.format("12345678909"));
    }

    @Test
    @DisplayName("Deve manter formatação existente")
    void deveManterFormatacaoExistente() {
        assertEquals("111.444.777-35", CpfValidator.format("111.444.777-35"));
        assertEquals("123.456.789-09", CpfValidator.format("123.456.789-09"));
    }

    @Test
    @DisplayName("Deve retornar CPF original quando inválido")
    void deveRetornarCPFOriginalQuandoInvalido() {
        String cpfInvalido = "123456";
        assertEquals(cpfInvalido, CpfValidator.format(cpfInvalido));
    }

    @Test
    @DisplayName("Deve formatar CPF com outros caracteres")
    void deveFormatarCPFComOutrosCaracteres() {
        assertEquals("111.444.777-35", CpfValidator.format("111-444-777/35"));
        assertEquals("111.444.777-35", CpfValidator.format("111 444 777 35"));
    }

    // Testes para getValidationMessage()
    @Test
    @DisplayName("Deve retornar mensagem para CPF nulo ou vazio")
    void deveRetornarMensagemParaCPFNuloOuVazio() {
        assertEquals("CPF é obrigatório", CpfValidator.getValidationMessage(null));
        assertEquals("CPF é obrigatório", CpfValidator.getValidationMessage(""));
        assertEquals("CPF é obrigatório", CpfValidator.getValidationMessage("   "));
    }

    @Test
    @DisplayName("Deve retornar mensagem para CPF inválido")
    void deveRetornarMensagemParaCPFInvalido() {
        assertEquals("CPF inválido", CpfValidator.getValidationMessage("123.456.789-00"));
        assertEquals("CPF inválido", CpfValidator.getValidationMessage("000.000.000-00"));
    }

    @Test
    @DisplayName("Deve retornar null para CPF válido")
    void deveRetornarNullParaCPFValido() {
        assertNull(CpfValidator.getValidationMessage("111.444.777-35"));
        assertNull(CpfValidator.getValidationMessage("123.456.789-09"));
    }
} 