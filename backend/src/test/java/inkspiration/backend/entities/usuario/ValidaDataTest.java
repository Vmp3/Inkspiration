package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.DateValidator;

public class ValidaDataTest {

    @Test
    void testDataValida() {
        assertTrue(DateValidator.isValid("01/01/1990"), "Data válida deve ser aceita");
        assertTrue(DateValidator.isValid("15/06/1985"), "Data válida deve ser aceita");
        assertTrue(DateValidator.isValid("29/02/2020"), "Data de ano bissexto deve ser válida");
        assertTrue(DateValidator.isValid("31/12/1999"), "Último dia do ano deve ser válido");
        assertTrue(DateValidator.isValid("01/01/2000"), "Primeiro dia do ano deve ser válido");
    }

    @Test
    void testDataInvalida() {
        assertFalse(DateValidator.isValid("32/01/1990"), "Dia 32 deve ser inválido");
        assertFalse(DateValidator.isValid("01/13/1990"), "Mês 13 deve ser inválido");
        assertFalse(DateValidator.isValid("29/02/1990"), "29 de fevereiro em ano não bissexto deve ser inválido");
        assertFalse(DateValidator.isValid("31/04/1990"), "31 de abril deve ser inválido");
        assertFalse(DateValidator.isValid("31/06/1990"), "31 de junho deve ser inválido");
        assertFalse(DateValidator.isValid("31/09/1990"), "31 de setembro deve ser inválido");
        assertFalse(DateValidator.isValid("31/11/1990"), "31 de novembro deve ser inválido");
        assertFalse(DateValidator.isValid("30/02/1990"), "30 de fevereiro deve ser inválido");
    }

    @Test
    void testDataFutura() {
        // Datas futuras devem ser inválidas segundo a implementação
        String dataFutura = String.format("01/01/%d", LocalDate.now().getYear() + 1);
        assertFalse(DateValidator.isValid(dataFutura), "Data futura deve ser inválida");
    }

    @Test
    void testDataVazia() {
        assertFalse(DateValidator.isValid(""), "Data vazia deve ser inválida");
        assertFalse(DateValidator.isValid("   "), "Data apenas com espaços deve ser inválida");
        assertFalse(DateValidator.isValid(null), "Data nula deve ser inválida");
    }

    @Test
    void testFormatoInvalido() {
        assertFalse(DateValidator.isValid("1/1/1990"), "Formato sem zeros à esquerda deve ser inválido");
        assertFalse(DateValidator.isValid("01-01-1990"), "Formato com hífen deve ser inválido");
        assertFalse(DateValidator.isValid("01.01.1990"), "Formato com ponto deve ser inválido");
        assertFalse(DateValidator.isValid("1990/01/01"), "Formato americano deve ser inválido");
        assertFalse(DateValidator.isValid("01/1/90"), "Ano com 2 dígitos deve ser inválido");
        assertFalse(DateValidator.isValid("1/01/1990"), "Dia sem zero à esquerda deve ser inválido");
        assertFalse(DateValidator.isValid("01/1/1990"), "Mês sem zero à esquerda deve ser inválido");
    }

    @Test
    void testAnosBissextos() {
        assertTrue(DateValidator.isValid("29/02/2000"), "Ano 2000 é bissexto");
        assertTrue(DateValidator.isValid("29/02/2004"), "Ano 2004 é bissexto");
        assertTrue(DateValidator.isValid("29/02/2008"), "Ano 2008 é bissexto");
        assertFalse(DateValidator.isValid("29/02/1900"), "Ano 1900 não é bissexto");
        assertFalse(DateValidator.isValid("29/02/2001"), "Ano 2001 não é bissexto");
        assertFalse(DateValidator.isValid("29/02/2002"), "Ano 2002 não é bissexto");
        assertFalse(DateValidator.isValid("29/02/2003"), "Ano 2003 não é bissexto");
    }

    @Test
    void testFevereiro() {
        assertTrue(DateValidator.isValid("28/02/1990"), "28 de fevereiro deve ser válido");
        assertFalse(DateValidator.isValid("29/02/1990"), "29 de fevereiro em ano não bissexto deve ser inválido");
        assertTrue(DateValidator.isValid("29/02/2020"), "29 de fevereiro em ano bissexto deve ser válido");
        assertFalse(DateValidator.isValid("30/02/2020"), "30 de fevereiro mesmo em ano bissexto deve ser inválido");
    }

    @Test
    void testMesesCom30Dias() {
        String[] mesesCom30Dias = {"04", "06", "09", "11"};
        for (String mes : mesesCom30Dias) {
            assertTrue(DateValidator.isValid("30/" + mes + "/1990"), "Dia 30 do mês " + mes + " deve ser válido");
            assertFalse(DateValidator.isValid("31/" + mes + "/1990"), "Dia 31 do mês " + mes + " deve ser inválido");
        }
    }

    @Test
    void testMesesCom31Dias() {
        String[] mesesCom31Dias = {"01", "03", "05", "07", "08", "10", "12"};
        for (String mes : mesesCom31Dias) {
            assertTrue(DateValidator.isValid("31/" + mes + "/1990"), "Dia 31 do mês " + mes + " deve ser válido");
        }
    }

    @Test
    void testParseDate() {
        LocalDate data = DateValidator.parseDate("15/06/1990");
        assertEquals(LocalDate.of(1990, 6, 15), data, "Data parseada deve corresponder à esperada");
    }

    @Test
    void testParseDateInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.parseDate("32/01/1990");
        }, "Parse de data inválida deve lançar exceção");

        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.parseDate("abc");
        }, "Parse de string inválida deve lançar exceção");

        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.parseDate(null);
        }, "Parse de data nula deve lançar exceção");
    }

    @Test
    void testDatasLimite() {
        assertTrue(DateValidator.isValid("01/01/1900"), "Data muito antiga deve ser válida");
        
        // Teste com data de ontem (sempre no passado)
        LocalDate ontem = LocalDate.now().minusDays(1);
        String dataOntem = String.format("%02d/%02d/%d", 
            ontem.getDayOfMonth(), 
            ontem.getMonthValue(), 
            ontem.getYear());
        assertTrue(DateValidator.isValid(dataOntem), "Data de ontem deve ser válida");
    }

    @Test
    void testCaracteresInvalidos() {
        assertFalse(DateValidator.isValid("ab/cd/efgh"), "Data com letras deve ser inválida");
        assertFalse(DateValidator.isValid("01/01/199a"), "Ano com letra deve ser inválido");
        assertFalse(DateValidator.isValid("0a/01/1990"), "Dia com letra deve ser inválido");
        assertFalse(DateValidator.isValid("01/0b/1990"), "Mês com letra deve ser inválido");
    }
} 