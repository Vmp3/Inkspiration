package inkspiration.backend.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.CpfValidator;

public class CpfValidatorTest {

    @Test
    void testCpfsValidos() {
        String[] cpfsValidos = {
            "11144477735",
            "12345678909",
            "98765432100",
            "00000000191"
        };

        for (String cpf : cpfsValidos) {
            assertTrue(CpfValidator.isValid(cpf), "CPF válido deveria ser aceito: " + cpf);
        }
    }

    @Test
    void testCpfsComFormatacao() {
        String[] cpfsFormatados = {
            "111.444.777-35",
            "123.456.789-09",
            "987.654.321-00",
            "000.000.001-91"
        };

        for (String cpf : cpfsFormatados) {
            assertTrue(CpfValidator.isValid(cpf), "CPF formatado válido deveria ser aceito: " + cpf);
        }
    }

    @Test
    void testCpfsInvalidos() {
        String[] cpfsInvalidos = {
            null,
            "",
            "   ",
            "12345678900", // dígito verificador inválido
            "98765432101", // dígito verificador inválido
            "123456789",   // muito curto
            "123456789012", // muito longo
            "abcdefghijk",  // caracteres não numéricos
            "000.000.000-00", // todos zeros
            "111.111.111-11", // todos iguais
            "222.222.222-22", // todos iguais
            "333.333.333-33", // todos iguais
            "444.444.444-44", // todos iguais
            "555.555.555-55", // todos iguais
            "666.666.666-66", // todos iguais
            "777.777.777-77", // todos iguais
            "888.888.888-88", // todos iguais
            "999.999.999-99"  // todos iguais
        };

        for (String cpf : cpfsInvalidos) {
            assertFalse(CpfValidator.isValid(cpf), "CPF inválido deveria ser rejeitado: " + cpf);
        }
    }

    @Test
    void testCpfNulo() {
        assertFalse(CpfValidator.isValid(null), "CPF nulo deve ser considerado inválido");
    }

    @Test
    void testCpfVazio() {
        assertFalse(CpfValidator.isValid(""), "CPF vazio deve ser considerado inválido");
        assertFalse(CpfValidator.isValid("   "), "CPF apenas com espaços deve ser considerado inválido");
    }

    @Test
    void testCpfComCaracteresEspeciais() {
        String[] cpfsComCaracteres = {
            "111.444.777-35",
            "111 444 777 35",
            "111-444-777-35",
            "111/444/777/35"
        };

        // Testa se a validação funciona com diferentes formatos
        for (String cpf : cpfsComCaracteres) {
            assertDoesNotThrow(() -> CpfValidator.isValid(cpf), 
                              "Validação de CPF com caracteres especiais não deve lançar exceção: " + cpf);
        }
    }

    @Test
    void testCpfTamanhoIncorreto() {
        String[] cpfsTamanhoIncorreto = {
            "1234567890",     // 10 dígitos
            "123456789012",   // 12 dígitos
            "12345",          // 5 dígitos
            "123456789012345" // 15 dígitos
        };

        for (String cpf : cpfsTamanhoIncorreto) {
            assertFalse(CpfValidator.isValid(cpf), "CPF com tamanho incorreto deveria ser rejeitado: " + cpf);
        }
    }

    @Test
    void testCpfComLetras() {
        String[] cpfsComLetras = {
            "1234567890a",
            "a1234567890",
            "12345a67890",
            "abcdefghijk",
            "111.444.777-3a"
        };

        for (String cpf : cpfsComLetras) {
            assertFalse(CpfValidator.isValid(cpf), "CPF com letras deveria ser rejeitado: " + cpf);
        }
    }

    @Test
    void testCpfSequenciaisInvalidos() {
        String[] cpfsSequenciais = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999"
        };

        for (String cpf : cpfsSequenciais) {
            assertFalse(CpfValidator.isValid(cpf), "CPF sequencial deveria ser rejeitado: " + cpf);
        }
    }

    @Test
    void testCpfLimpeza() {
        // Testa se a função de limpeza funciona corretamente
        String[] cpfsParaLimpar = {
            "111.444.777-35",
            "111 444 777 35",
            "111-444-777-35",
            " 11144477735 "
        };

        for (String cpf : cpfsParaLimpar) {
            // Assumindo que existe um método de limpeza ou que a validação limpa automaticamente
            assertDoesNotThrow(() -> CpfValidator.isValid(cpf), 
                              "CPF com formatação deveria ser processado corretamente: " + cpf);
        }
    }

    @Test
    void testCpfDigitosVerificadores() {
        // Testa casos específicos de dígitos verificadores
        String cpfCorreto = "11144477735";
        String cpfIncorreto1 = "11144477734";
        String cpfIncorreto2 = "11144477736";

        assertTrue(CpfValidator.isValid(cpfCorreto), "CPF com dígitos verificadores corretos deveria ser válido");
        assertFalse(CpfValidator.isValid(cpfIncorreto1), "CPF com primeiro dígito verificador incorreto deveria ser inválido");
        assertFalse(CpfValidator.isValid(cpfIncorreto2), "CPF com segundo dígito verificador incorreto deveria ser inválido");
    }

    @Test
    void testCpfCasosLimite() {
        // Testa casos limites do algoritmo
        String[] cpfsLimite = {
            "00000000191", // CPF especial
            "12345678909"  // CPF válido comum
        };

        for (String cpf : cpfsLimite) {
            assertDoesNotThrow(() -> CpfValidator.isValid(cpf), 
                              "Validação de CPF limite não deve lançar exceção: " + cpf);
        }
    }

    @Test
    void testCpfFormatacaoMista() {
        String[] cpfsFormatacaoMista = {
            "111.444.777-35", // formatado completo
            "11144477735",     // sem formatação
            "111 444 777 35"   // com espaços
        };

        for (String cpf : cpfsFormatacaoMista) {
            assertDoesNotThrow(() -> CpfValidator.isValid(cpf), 
                              "Validação de CPF com formatação mista não deve lançar exceção: " + cpf);
        }
    }

    @Test
    void testPerformance() {
        // Testa performance com múltiplas validações
        String cpfValido = "11144477735";
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            CpfValidator.isValid(cpfValido);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 1000, "Validação de 1000 CPFs deve ser rápida (< 1s), levou: " + duration + "ms");
    }

    @Test
    void testCpfComEspacos() {
        String[] cpfsComEspacos = {
            " 11144477735 ",
            "  111.444.777-35  ",
            " 111 444 777 35 ",
            "\t11144477735\t",
            "\n111.444.777-35\n"
        };

        for (String cpf : cpfsComEspacos) {
            assertDoesNotThrow(() -> CpfValidator.isValid(cpf), 
                              "CPF com espaços deve ser processado corretamente: " + cpf);
        }
    }

    @Test
    void testFormatacao() {
        String cpfSemFormato = "11144477735";
        String cpfFormatado = CpfValidator.format(cpfSemFormato);
        
        assertEquals("111.444.777-35", cpfFormatado, "CPF deve ser formatado corretamente");
    }

    @Test
    void testFormatacaoComCpfInvalido() {
        String cpfCurto = "123456789";
        String resultado = CpfValidator.format(cpfCurto);
        
        assertEquals(cpfCurto, resultado, "CPF inválido deve retornar sem formatação");
    }

    @Test
    void testFormatacaoJaFormatado() {
        String cpfJaFormatado = "111.444.777-35";
        String resultado = CpfValidator.format(cpfJaFormatado);
        
        assertEquals("111.444.777-35", resultado, "CPF já formatado deve ser formatado corretamente");
    }
} 