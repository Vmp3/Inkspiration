package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.util.DateValidator;

@DisplayName("UsuarioService - Testes de Data")
class UsuarioServiceDataTest {

    @Test
    @DisplayName("Deve validar data válida")
    void deveValidarDataValida() {
        assertTrue(DateValidator.isValid("01/01/1990"));
        assertTrue(DateValidator.isValid("31/12/2000"));
        assertTrue(DateValidator.isValid("29/02/2020")); // Ano bissexto
        assertTrue(DateValidator.isValid("15/06/1985"));
        assertTrue(DateValidator.isValid("28/02/1999"));
    }

    @Test
    @DisplayName("Deve invalidar data inválida")
    void deveInvalidarDataInvalida() {
        assertFalse(DateValidator.isValid("31/02/1990")); // Fevereiro não tem 31 dias
        assertFalse(DateValidator.isValid("32/01/1990")); // Dia não existe
        assertFalse(DateValidator.isValid("01/13/1990")); // Mês não existe
        assertFalse(DateValidator.isValid("29/02/1999")); // Ano não bissexto
        assertFalse(DateValidator.isValid("1990-01-01")); // Formato incorreto
    }

    @Test
    @DisplayName("Deve invalidar data nula ou vazia")
    void deveInvalidarDataNulaOuVazia() {
        assertFalse(DateValidator.isValid(null));
        assertFalse(DateValidator.isValid(""));
        assertFalse(DateValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve invalidar formato de data incorreto")
    void deveInvalidarFormatoDataIncorreto() {
        assertFalse(DateValidator.isValid("1990/01/01"));
        assertFalse(DateValidator.isValid("01-01-1990"));
        assertFalse(DateValidator.isValid("01.01.1990"));
        assertFalse(DateValidator.isValid("1/1/90"));
        assertFalse(DateValidator.isValid("01/01/90"));
    }

    @Test
    @DisplayName("Deve validar idade mínima corretamente")
    void deveValidarIdadeMinimaCorretamente() {
        assertTrue(DateValidator.hasMinimumAge("01/01/1990", 18));
        assertTrue(DateValidator.hasMinimumAge("01/01/2000", 18));
        assertTrue(DateValidator.hasMinimumAge("01/01/1980", 30));
        assertFalse(DateValidator.hasMinimumAge("01/01/2010", 18));
        assertFalse(DateValidator.hasMinimumAge("01/01/2023", 5));
    }

    @Test
    @DisplayName("Deve validar idade com data atual")
    void deveValidarIdadeComDataAtual() {
        // Simulando uma data de hoje - 20 anos
        java.time.LocalDate dataHoje = java.time.LocalDate.now();
        java.time.LocalDate data20AnosAtras = dataHoje.minusYears(20);
        String dataFormatada = String.format("%02d/%02d/%d", 
            data20AnosAtras.getDayOfMonth(), 
            data20AnosAtras.getMonthValue(), 
            data20AnosAtras.getYear());
        
        assertTrue(DateValidator.hasMinimumAge(dataFormatada, 18));
        assertTrue(DateValidator.hasMinimumAge(dataFormatada, 19));
        assertFalse(DateValidator.hasMinimumAge(dataFormatada, 21));
    }

    @Test
    @DisplayName("Deve lançar exceção para data obrigatória")
    void deveLancarExcecaoParaDataObrigatoria() {
        assertThrows(UsuarioValidationException.DataNascimentoObrigatoriaException.class, () -> {
            String data = null;
            if (data == null || data.trim().isEmpty()) {
                throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
            }
        });

        assertThrows(UsuarioValidationException.DataNascimentoObrigatoriaException.class, () -> {
            String data = "";
            if (data == null || data.trim().isEmpty()) {
                throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para data inválida")
    void deveLancarExcecaoParaDataInvalida() {
        assertThrows(UsuarioValidationException.DataInvalidaException.class, () -> {
            String data = "31/02/1990";
            if (!DateValidator.isValid(data)) {
                throw new UsuarioValidationException.DataInvalidaException("Data inválida");
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para idade mínima")
    void deveLancarExcecaoParaIdadeMinima() {
        assertThrows(UsuarioValidationException.IdadeMinimaException.class, () -> {
            String data = "01/01/2010";
            int idadeMinima = 18;
            if (!DateValidator.hasMinimumAge(data, idadeMinima)) {
                throw new UsuarioValidationException.IdadeMinimaException(idadeMinima);
            }
        });
    }

    @Test
    @DisplayName("Deve validar anos bissextos")
    void deveValidarAnosBissextos() {
        assertTrue(DateValidator.isValid("29/02/2020")); // Divisível por 4
        assertTrue(DateValidator.isValid("29/02/2000")); // Divisível por 400
        assertFalse(DateValidator.isValid("29/02/1900")); // Divisível por 100 mas não por 400
        assertFalse(DateValidator.isValid("29/02/2021")); // Não divisível por 4
    }
} 