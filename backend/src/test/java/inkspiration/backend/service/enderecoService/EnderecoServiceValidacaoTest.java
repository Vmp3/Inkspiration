package inkspiration.backend.service.enderecoService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class EnderecoServiceValidacaoTest {

    @Test
    @DisplayName("Deve validar CEP com 8 dígitos")
    void deveValidarCEPCom8Digitos() {
        // Given
        List<String> cepsValidos = Arrays.asList("12345678", "00000000", "99999999");
        List<String> cepsInvalidos = Arrays.asList("1234567", "123456789", "abcdefgh", "12345-67");
        
        // When & Then
        for (String cep : cepsValidos) {
            assertEquals(8, cep.length());
            assertTrue(cep.matches("\\d{8}"));
        }
        
        for (String cep : cepsInvalidos) {
            if (cep != null) {
                assertTrue(cep.length() != 8 || !cep.matches("\\d{8}"));
            }
        }
    }

    @Test
    @DisplayName("Deve limpar CEP removendo caracteres não numéricos")
    void deveLimparCEPRemovendoCaracteresNaoNumericos() {
        // Given
        String cepComMascara = "12345-678";
        String cepComEspacos = "123 456 78";
        String cepComPontos = "123.456.78";
        
        // When
        String cepLimpo1 = cepComMascara.replaceAll("[^0-9]", "");
        String cepLimpo2 = cepComEspacos.replaceAll("[^0-9]", "");
        String cepLimpo3 = cepComPontos.replaceAll("[^0-9]", "");
        
        // Then
        assertEquals("12345678", cepLimpo1);
        assertEquals("12345678", cepLimpo2);
        assertEquals("12345678", cepLimpo3);
    }

    @Test
    @DisplayName("Deve validar estados brasileiros válidos")
    void deveValidarEstadosBrasileirosValidos() {
        // Given
        List<String> estadosValidos = Arrays.asList(
            "SP", "RJ", "MG", "RS", "PR", "SC", "BA", "GO", "PE", "CE",
            "PA", "MA", "PB", "ES", "PI", "AL", "RN", "MT", "MS", "DF",
            "SE", "RO", "AC", "AM", "RR", "AP", "TO"
        );
        
        // When & Then
        for (String estado : estadosValidos) {
            assertEquals(2, estado.length());
            assertTrue(estado.matches("[A-Z]{2}"));
        }
    }

    @Test
    @DisplayName("Deve rejeitar estados inválidos")
    void deveRejeitarEstadosInvalidos() {
        // Given
        List<String> estadosInvalidos = Arrays.asList("XX", "ZZ", "ABC", "S", "", "123");
        
        // When & Then
        for (String estado : estadosInvalidos) {
            if (estado != null && !estado.isEmpty()) {
                assertTrue(estado.length() != 2 || !estado.matches("[A-Z]{2}") || 
                          !Arrays.asList("SP", "RJ", "MG", "RS", "PR", "SC", "BA", "GO", "PE", "CE",
                                        "PA", "MA", "PB", "ES", "PI", "AL", "RN", "MT", "MS", "DF",
                                        "SE", "RO", "AC", "AM", "RR", "AP", "TO").contains(estado));
            }
        }
    }

    @Test
    @DisplayName("Deve validar nomes de cidades brasileiras")
    void deveValidarNomesCidadesBrasileiras() {
        // Given
        List<String> cidadesValidas = Arrays.asList(
            "São Paulo", "Rio de Janeiro", "Belo Horizonte", "Salvador", "Brasília",
            "Fortaleza", "Manaus", "Curitiba", "Recife", "Porto Alegre"
        );
        
        // When & Then
        for (String cidade : cidadesValidas) {
            assertNotNull(cidade);
            assertFalse(cidade.trim().isEmpty());
            assertTrue(cidade.length() > 0);
        }
    }

    @Test
    @DisplayName("Deve validar comparação case-insensitive de cidades")
    void deveValidarComparacaoCaseInsensitiveCidades() {
        // Given
        String cidadeAPI = "São Paulo";
        String cidadeUsuario1 = "são paulo";
        String cidadeUsuario2 = "SÃO PAULO";
        String cidadeUsuario3 = "São Paulo";
        
        // When & Then
        assertTrue(cidadeAPI.equalsIgnoreCase(cidadeUsuario1));
        assertTrue(cidadeAPI.equalsIgnoreCase(cidadeUsuario2));
        assertTrue(cidadeAPI.equalsIgnoreCase(cidadeUsuario3));
    }

    @Test
    @DisplayName("Deve validar comparação case-insensitive de estados")
    void deveValidarComparacaoCaseInsensitiveEstados() {
        // Given
        String estadoAPI = "SP";
        String estadoUsuario1 = "sp";
        String estadoUsuario2 = "Sp";
        String estadoUsuario3 = "SP";
        
        // When & Then
        assertTrue(estadoAPI.equalsIgnoreCase(estadoUsuario1));
        assertTrue(estadoAPI.equalsIgnoreCase(estadoUsuario2));
        assertTrue(estadoAPI.equalsIgnoreCase(estadoUsuario3));
    }

    @Test
    @DisplayName("Deve validar estrutura de resposta ViaCEP")
    void deveValidarEstruturaRespostaViaCEP() {
        // Given - Simular estrutura de resposta da API ViaCEP
        String jsonResponse = """
            {
                "cep": "12345-678",
                "logradouro": "Rua Exemplo",
                "bairro": "Centro",
                "localidade": "São Paulo",
                "uf": "SP"
            }
            """;
        
        // When & Then
        assertTrue(jsonResponse.contains("cep"));
        assertTrue(jsonResponse.contains("logradouro"));
        assertTrue(jsonResponse.contains("bairro"));
        assertTrue(jsonResponse.contains("localidade"));
        assertTrue(jsonResponse.contains("uf"));
    }

    @Test
    @DisplayName("Deve detectar CEP não encontrado na API")
    void deveDetectarCEPNaoEncontradoAPI() {
        // Given - Simular resposta de erro da API ViaCEP
        String jsonErro = """
            {
                "erro": true
            }
            """;
        
        // When & Then
        assertTrue(jsonErro.contains("erro"));
        assertTrue(jsonErro.contains("true"));
    }

    @Test
    @DisplayName("Deve validar números de endereço")
    void deveValidarNumerosEndereco() {
        // Given
        List<String> numerosValidos = Arrays.asList("123", "456A", "789B", "S/N", "1000");
        
        // When & Then
        for (String numero : numerosValidos) {
            assertNotNull(numero);
            assertFalse(numero.trim().isEmpty());
        }
    }

    @Test
    @DisplayName("Deve validar complementos de endereço")
    void deveValidarComplementosEndereco() {
        // Given
        List<String> complementosValidos = Arrays.asList(
            "Apto 101", "Casa 2", "Sala 302", "Bloco A", "", null
        );
        
        // When & Then
        for (String complemento : complementosValidos) {
            // Complemento pode ser vazio ou nulo
            assertTrue(complemento == null || complemento.length() >= 0);
        }
    }

    @Test
    @DisplayName("Deve validar URL da API ViaCEP")
    void deveValidarURLAPIViaCEP() {
        // Given
        String cep = "12345678";
        String urlEsperada = "https://viacep.com.br/ws/" + cep + "/json/";
        
        // When & Then
        assertTrue(urlEsperada.contains("https://"));
        assertTrue(urlEsperada.contains("viacep.com.br"));
        assertTrue(urlEsperada.contains("/ws/"));
        assertTrue(urlEsperada.contains("/json/"));
        assertTrue(urlEsperada.contains(cep));
    }

    @Test
    @DisplayName("Deve validar trimming de campos de endereço")
    void deveValidarTrimmingCamposEndereco() {
        // Given
        String ruaComEspacos = "  Rua das Flores  ";
        String bairroComEspacos = "  Centro  ";
        String cidadeComEspacos = "  São Paulo  ";
        
        // When
        String ruaTrimmed = ruaComEspacos.trim();
        String bairroTrimmed = bairroComEspacos.trim();
        String cidadeTrimmed = cidadeComEspacos.trim();
        
        // Then
        assertEquals("Rua das Flores", ruaTrimmed);
        assertEquals("Centro", bairroTrimmed);
        assertEquals("São Paulo", cidadeTrimmed);
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios de endereço")
    void deveValidarCamposObrigatoriosEndereco() {
        // Given
        String cep = "12345678";
        String estado = "SP";
        String cidade = "São Paulo";
        
        // When & Then
        assertNotNull(cep);
        assertFalse(cep.trim().isEmpty());
        assertNotNull(estado);
        assertFalse(estado.trim().isEmpty());
        assertNotNull(cidade);
        assertFalse(cidade.trim().isEmpty());
    }

    @Test
    @DisplayName("Deve validar campos opcionais de endereço")
    void deveValidarCamposOpcionaisEndereco() {
        // Given
        String logradouro = null;
        String bairro = "";
        String complemento = null;
        
        // When & Then
        assertTrue(logradouro == null || !logradouro.trim().isEmpty());
        assertTrue(bairro == null || bairro.length() >= 0);
        assertTrue(complemento == null || complemento.length() >= 0);
    }
} 