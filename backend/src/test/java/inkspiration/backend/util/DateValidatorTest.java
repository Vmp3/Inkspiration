package inkspiration.backend.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateValidator - Testes Completos")
class DateValidatorTest {

    // Testes para isValid()
    @Test
    @DisplayName("Deve validar data com formato dd/MM/yyyy")
    void deveValidarDataComFormatoDDMMYYYY() {
        assertTrue(DateValidator.isValid("25/12/2023"));
        assertTrue(DateValidator.isValid("01/01/2024"));
    }

    @ParameterizedTest
    @DisplayName("Deve rejeitar datas inválidas")
    @ValueSource(strings = {
        "30/02/2023",  // Dia inválido para fevereiro
        "31/04/2023",  // Dia inválido para abril
        "31/06/2023",  // Dia inválido para junho
        "31/09/2023",  // Dia inválido para setembro
        "31/11/2023",  // Dia inválido para novembro
        "32/01/2023",  // Dia inválido
        "00/01/2023",  // Dia zero
        "15/13/2023",  // Mês inválido
        "15/00/2023",  // Mês zero
        "29/02/2023",  // 29 de fevereiro em ano não bissexto
        "abc/12/2023", // Formato inválido
        "25-12-2023",  // Formato inválido
        "2023/12/25"   // Formato inválido
    })
    void deveRejeitarDatasInvalidas(String data) {
        assertFalse(DateValidator.isValid(data));
    }

    @Test
    @DisplayName("Deve validar 29 de fevereiro em ano bissexto")
    void deveValidar29DeFevereiroEmAnoBissexto() {
        assertTrue(DateValidator.isValid("29/02/2024")); // 2024 é bissexto
        assertTrue(DateValidator.isValid("29/02/2020")); // 2020 é bissexto
    }

    @Test
    @DisplayName("Deve rejeitar datas nulas ou vazias")
    void deveRejeitarDatasNulasOuVazias() {
        assertFalse(DateValidator.isValid(null));
        assertFalse(DateValidator.isValid(""));
        assertFalse(DateValidator.isValid("   "));
    }

    // Testes para parseDate()
    @Test
    @DisplayName("Deve converter string para LocalDate")
    void deveConverterStringParaLocalDate() {
        assertEquals(LocalDate.of(2023, 12, 25), DateValidator.parseDate("25/12/2023"));
        assertEquals(LocalDate.of(2024, 1, 1), DateValidator.parseDate("01/01/2024"));
    }

    @Test
    @DisplayName("Deve lançar exceção para data inválida na conversão")
    void deveLancarExcecaoParaDataInvalidaNaConversao() {
        assertThrows(IllegalArgumentException.class, () -> DateValidator.parseDate("30/02/2023"));
        assertThrows(IllegalArgumentException.class, () -> DateValidator.parseDate("31/04/2023"));
    }

    @Test
    @DisplayName("Deve lançar exceção para data nula ou vazia na conversão")
    void deveLancarExcecaoParaDataNulaOuVaziaNaConversao() {
        assertThrows(IllegalArgumentException.class, () -> DateValidator.parseDate(null));
        assertThrows(IllegalArgumentException.class, () -> DateValidator.parseDate(""));
        assertThrows(IllegalArgumentException.class, () -> DateValidator.parseDate("   "));
    }

    // Testes para hasMinimumAge()
    @ParameterizedTest
    @DisplayName("Deve validar idade mínima com diferentes valores")
    @CsvSource({
        "01/01/2000, 18",  // Pessoa com mais de 18 anos
        "31/12/2005, 18",  // Pessoa exatamente com 18 anos
        "01/01/1990, 30",  // Pessoa com mais de 30 anos
        "31/12/1992, 30"   // Pessoa exatamente com 30 anos
    })
    void deveValidarIdadeMinimaComDiferentesValores(String dataNascimento, int idadeMinima) {
        assertTrue(DateValidator.hasMinimumAge(dataNascimento, idadeMinima));
    }

    @Test
    @DisplayName("Deve retornar false para data inválida na validação de idade")
    void deveRetornarFalseParaDataInvalidaNaValidacaoDeIdade() {
        assertFalse(DateValidator.hasMinimumAge("31/02/2000", 18));
        assertFalse(DateValidator.hasMinimumAge("00/01/2000", 18));
        assertFalse(DateValidator.hasMinimumAge("15/13/2000", 18));
    }

    @Test
    @DisplayName("Deve retornar false para idade mínima negativa")
    void deveRetornarFalseParaIdadeMinimaNegativa() {
        assertFalse(DateValidator.hasMinimumAge("01/01/2000", -1));
        assertFalse(DateValidator.hasMinimumAge("01/01/2000", -18));
    }

    @Test
    @DisplayName("Deve retornar false para data nula ou vazia na validação de idade")
    void deveRetornarFalseParaDataNulaOuVaziaNaValidacaoDeIdade() {
        assertFalse(DateValidator.hasMinimumAge(null, 18));
        assertFalse(DateValidator.hasMinimumAge("", 18));
        assertFalse(DateValidator.hasMinimumAge("   ", 18));
    }
} 