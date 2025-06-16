package inkspiration.backend.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.DateValidator;

public class DateValidatorTest {

    @Test
    void testDatasValidas() {
        String[] datasValidas = {
            "25/12/2022",
            "01/01/2000",
            "15/06/1990",
            "29/02/2020", // ano bissexto
            "31/12/1999"
        };

        for (String data : datasValidas) {
            assertTrue(DateValidator.isValid(data), "Data válida deveria ser aceita: " + data);
        }
    }

    @Test
    void testDatasInvalidas() {
        String[] datasInvalidas = {
            null,
            "",
            "   ",
            "01/13/2023", // mês inválido
            "32/12/2023", // dia inválido
            "30/02/2023", // fevereiro com 30 dias
            "00/01/2023", // dia zero
            "01/00/2023", // mês zero
            "abc/def/ghi", // formato inválido
            "2023-12-25", // formato incorreto
            "25-12-2023", // formato DD-MM-YYYY com hífen
            "12/25/2023", // formato MM/DD/YYYY incorreto
            "2023", // apenas ano
            "12/2023", // apenas mês e ano
            "invalid-date"
        };

        for (String data : datasInvalidas) {
            assertFalse(DateValidator.isValid(data), "Data inválida deveria ser rejeitada: " + data);
        }
    }

    @Test
    void testDataNula() {
        assertFalse(DateValidator.isValid(null), "Data nula deve ser considerada inválida");
    }

    @Test
    void testDataVazia() {
        assertFalse(DateValidator.isValid(""), "Data vazia deve ser considerada inválida");
        assertFalse(DateValidator.isValid("   "), "Data apenas com espaços deve ser considerada inválida");
    }

    @Test
    void testAnosBissextos() {
        String[] anosBissextos = {
            "29/02/2000", // divisível por 400
            "29/02/2004", // divisível por 4
            "29/02/2008",
            "29/02/2012",
            "29/02/2016",
            "29/02/2020"
        };

        for (String data : anosBissextos) {
            assertTrue(DateValidator.isValid(data), "Data de ano bissexto deveria ser válida: " + data);
        }
    }

    @Test
    void testAnosNaoBissextos() {
        String[] datasInvalidas = {
            "29/02/2001", // não é bissexto
            "29/02/2003",
            "29/02/2005", 
            "29/02/2021",
            "29/02/2023",
            "29/02/1900" // divisível por 100 mas não por 400
        };

        for (String data : datasInvalidas) {
            assertFalse(DateValidator.isValid(data), "Data de 29 de fevereiro em ano não bissexto deveria ser inválida: " + data);
        }
    }

    @Test
    void testFormatosData() {
        // Testa se apenas o formato DD/MM/YYYY é aceito
        String[] formatosValidos = {
            "01/01/2023",
            "31/12/2023"
        };

        String[] formatosInvalidos = {
            "01-01-2023",
            "2023/01/01",
            "01.01.2023",
            "2023.01.01",
            "Jan 1, 2023",
            "1 Jan 2023",
            "2023-01-01"
        };

        for (String data : formatosValidos) {
            assertTrue(DateValidator.isValid(data), "Formato válido deveria ser aceito: " + data);
        }

        for (String data : formatosInvalidos) {
            assertFalse(DateValidator.isValid(data), "Formato inválido deveria ser rejeitado: " + data);
        }
    }

    @Test
    void testDiasDosMeses() {
        // Janeiro, Março, Maio, Julho, Agosto, Outubro, Dezembro têm 31 dias
        String[] meses31Dias = {"01", "03", "05", "07", "08", "10", "12"};
        for (String mes : meses31Dias) {
            assertTrue(DateValidator.isValid("31/" + mes + "/2022"), 
                      "Dia 31 deveria ser válido para o mês " + mes);
        }

        // Abril, Junho, Setembro, Novembro têm 30 dias
        String[] meses30Dias = {"04", "06", "09", "11"};
        for (String mes : meses30Dias) {
            assertTrue(DateValidator.isValid("30/" + mes + "/2022"), 
                      "Dia 30 deveria ser válido para o mês " + mes);
            assertFalse(DateValidator.isValid("31/" + mes + "/2022"), 
                       "Dia 31 deveria ser inválido para o mês " + mes);
        }

        // Fevereiro tem 28 dias em anos não bissextos
        assertTrue(DateValidator.isValid("28/02/2022"), "28 de fevereiro deveria ser válido em ano não bissexto");
        assertFalse(DateValidator.isValid("29/02/2022"), "29 de fevereiro deveria ser inválido em ano não bissexto");
    }

    @Test
    void testDatasPassadas() {
        // O DateValidator só aceita datas passadas
        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDate hoje = LocalDate.now();
        LocalDate amanha = LocalDate.now().plusDays(1);
        
        String ontemStr = String.format("%02d/%02d/%d", ontem.getDayOfMonth(), ontem.getMonthValue(), ontem.getYear());
        String hojeStr = String.format("%02d/%02d/%d", hoje.getDayOfMonth(), hoje.getMonthValue(), hoje.getYear());
        String amanhaStr = String.format("%02d/%02d/%d", amanha.getDayOfMonth(), amanha.getMonthValue(), amanha.getYear());
        
        assertTrue(DateValidator.isValid(ontemStr), "Data passada deveria ser válida");
        assertFalse(DateValidator.isValid(hojeStr), "Data de hoje deveria ser inválida");
        assertFalse(DateValidator.isValid(amanhaStr), "Data futura deveria ser inválida");
    }

    @Test
    void testValidacaoComLocalDate() {
        // Testa o método parseDate
        String dataValida = "25/12/2020";
        LocalDate data = DateValidator.parseDate(dataValida);
        
        assertEquals(25, data.getDayOfMonth());
        assertEquals(12, data.getMonthValue());
        assertEquals(2020, data.getYear());
    }

    @Test
    void testParseDataInvalida() {
        String dataInvalida = "32/12/2023";
        
        assertThrows(IllegalArgumentException.class, () -> {
            DateValidator.parseDate(dataInvalida);
        }, "Parse de data inválida deve lançar IllegalArgumentException");
    }

    @Test
    void testPerformance() {
        // Testa performance com múltiplas validações
        String dataValida = "25/12/2020";
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            DateValidator.isValid(dataValida);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 1000, "Validação de 1000 datas deve ser rápida (< 1s), levou: " + duration + "ms");
    }

    @Test
    void testDataComEspacos() {
        String[] datasComEspacos = {
            " 25/12/2020 ",
            "  01/01/2020  ",
            "\t15/06/2020\t",
            "\n10/03/2020\n"
        };

        for (String data : datasComEspacos) {
            assertDoesNotThrow(() -> DateValidator.isValid(data), 
                              "Data com espaços deve ser processada corretamente: " + data);
        }
    }

    @Test
    void testValidacaoTiposData() {
        // Testa diferentes métodos de validação se existirem
        String dataString = "25/12/2020";
        
        // Testa se não lança exceção para conversões
        assertDoesNotThrow(() -> DateValidator.parseDate(dataString), 
                          "String de data válida deve ser parseável para LocalDate");
    }

    @Test
    void testCasosEspeciais() {
        // Testa casos especiais de datas
        String[] casosEspeciais = {
            "29/02/2000", // ano bissexto centenário
            "28/02/1900", // ano não bissexto centenário
            "29/02/2004", // ano bissexto comum
            "28/02/2022"  // fevereiro em ano comum
        };

        for (String data : casosEspeciais) {
            assertDoesNotThrow(() -> DateValidator.isValid(data), 
                              "Caso especial de data não deve causar exceção: " + data);
        }
    }

    @Test
    void testFormatacaoInconsistente() {
        String[] formatosInconsistentes = {
            "1/1/2020",     // sem zero à esquerda
            "01/1/2020",    // parcialmente formatado
            "1/01/2020",    // parcialmente formatado
            "01/01/20",     // ano com 2 dígitos
            "001/01/2020",  // dia com 3 dígitos
            "01/001/2020"   // mês com 3 dígitos
        };

        for (String data : formatosInconsistentes) {
            assertDoesNotThrow(() -> DateValidator.isValid(data), 
                              "Formatação inconsistente não deve causar exceção: " + data);
        }
    }

    @Test
    void testDataHistorica() {
        String[] datasHistoricas = {
            "22/04/1500", // Descobrimento do Brasil
            "07/09/1822", // Independência do Brasil
            "15/11/1889", // Proclamação da República
            "08/05/1945", // Fim da Segunda Guerra
            "01/01/2000"  // Virada do milênio
        };

        for (String data : datasHistoricas) {
            assertTrue(DateValidator.isValid(data), 
                      "Data histórica deveria ser válida: " + data);
        }
    }
} 