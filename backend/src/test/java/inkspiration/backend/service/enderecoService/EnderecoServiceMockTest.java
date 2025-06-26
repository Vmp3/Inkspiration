package inkspiration.backend.service.enderecoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.service.EnderecoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de mock - EnderecoService")
class EnderecoServiceMockTest {

    @Mock
    private RestTemplate restTemplate;

    private EnderecoService enderecoService;

    @BeforeEach
    void setUp() {
        enderecoService = new EnderecoService(restTemplate);
    }

    @Test
    @DisplayName("Deve fazer chamada correta para API ViaCEP com CEP limpo")
    void deveFazerChamadaCorretaParaAPIViaCEPComCepLimpo() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setCep("88137-074"); // CEP com máscara
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act
        enderecoService.validarEndereco(endereco);

        // Assert
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        
        String urlChamada = urlCaptor.getValue();
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlChamada);
    }

    @Test
    @DisplayName("Deve fazer exatamente uma chamada para API por validação")
    void deveFazerExatamenteUmaChamadaParaAPIPorValidacao() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act
        enderecoService.validarEndereco(endereco);

        // Assert
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    @DisplayName("Deve construir URL correta para diferentes CEPs")
    void deveConstruirURLCorretaParaDiferentesCEPs() {
        // Arrange
        String[] ceps = {"12345678", "87654321", "11111111"};
        String respostaViaCep = criarRespostaViaCepValida();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert
        for (String cep : ceps) {
            Endereco endereco = criarEnderecoValido();
            endereco.setCep(cep);
            
            enderecoService.validarEndereco(endereco);
        }
        
        verify(restTemplate, times(3)).getForObject(urlCaptor.capture(), eq(String.class));
        
        // Verifica as URLs capturadas
        List<String> urlsChamadas = urlCaptor.getAllValues();
        for (int i = 0; i < ceps.length; i++) {
            String urlEsperada = "https://viacep.com.br/ws/" + ceps[i] + "/json/";
            assertEquals(urlEsperada, urlsChamadas.get(i));
        }
    }

    @Test
    @DisplayName("Deve usar sempre String.class como tipo de retorno")
    void deveUsarSempreStringClassComoTipoDeRetorno() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act
        enderecoService.validarEndereco(endereco);

        // Assert
        verify(restTemplate).getForObject(anyString(), eq(String.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(Object.class));
        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

    @Test
    @DisplayName("Deve processar resposta JSON com campos nulos")
    void deveProcessarRespostaJSONComCamposNulos() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert - Deve lançar exceção quando bairro é nulo no endereço
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setBairro(null) // Bairro nulo no endereço
        );
        assertEquals("O bairro não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve processar resposta JSON com campos vazios")
    void deveProcessarRespostaJSONComCamposVazios() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert - Deve lançar exceção quando bairro é vazio no endereço
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setBairro("") // Bairro vazio no endereço
        );
        assertEquals("O bairro não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar múltiplos endereços em sequência")
    void deveValidarMultiplosEnderecosEmSequencia() {
        // Arrange
        Endereco endereco1 = criarEnderecoValido();
        endereco1.setCep("88137074");
        
        Endereco endereco2 = criarEnderecoValido();
        endereco2.setCep("04567890");
        endereco2.setCidade("Palhoça");
        endereco2.setBairro("Vila Madalena");
        endereco2.setRua("Rua Exemplo");
        
        String resposta1 = criarRespostaViaCepValida();
        String resposta2 = """
            {
                "cep": "04567-890",
                "logradouro": "Rua Exemplo",
                "bairro": "Vila Madalena",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(eq("https://viacep.com.br/ws/88137074/json/"), eq(String.class)))
            .thenReturn(resposta1);
        when(restTemplate.getForObject(eq("https://viacep.com.br/ws/04567890/json/"), eq(String.class)))
            .thenReturn(resposta2);

        // Act
        enderecoService.validarEndereco(endereco1);
        enderecoService.validarEndereco(endereco2);

        // Assert
        verify(restTemplate, times(2)).getForObject(anyString(), eq(String.class));
        verify(restTemplate).getForObject("https://viacep.com.br/ws/88137074/json/", String.class);
        verify(restTemplate).getForObject("https://viacep.com.br/ws/04567890/json/", String.class);
    }

    @Test
    @DisplayName("Deve resetar mock entre chamadas")
    void deveResetarMockEntreChamadas() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        String respostaViaCep = criarRespostaViaCepValida();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act - Primeira chamada
        enderecoService.validarEndereco(endereco);
        
        // Reset do mock
        reset(restTemplate);
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);
        
        // Act - Segunda chamada
        enderecoService.validarEndereco(endereco);

        // Assert - Apenas a segunda chamada deve ser contabilizada
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    @DisplayName("Deve capturar argumentos de chamada corretamente")
    void deveCapturaArgumentosDeChamadaCorretamente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setCep("12345678");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Class<String>> classCaptor = ArgumentCaptor.forClass(Class.class);

        // Act
        enderecoService.validarEndereco(endereco);

        // Assert
        verify(restTemplate).getForObject(urlCaptor.capture(), classCaptor.capture());
        
        assertEquals("https://viacep.com.br/ws/12345678/json/", urlCaptor.getValue());
        assertEquals(String.class, classCaptor.getValue());
    }

    @Test
    @DisplayName("Deve verificar que não há interações desnecessárias com mock")
    void deveVerificarQueNaoHaInteracoesDesnecessariasComMock() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> endereco.setCep(null)); // CEP inválido - não deve chamar API
        
        // Verifica que o RestTemplate não foi chamado
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve configurar mock com múltiplas respostas")
    void deveConfigurarMockComMultiplasRespostas() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(criarRespostaViaCepValida())
            .thenReturn("""
                {
                    "cep": "88137-074",
                    "logradouro": "Outra Rua",
                    "bairro": "Outro Bairro",
                    "localidade": "Palhoça",
                    "uf": "SC"
                }
                """)
            .thenThrow(new RuntimeException("Terceira chamada falha"));

        // Act & Assert
        // Primeira chamada - sucesso
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        // Segunda chamada - sucesso com dados diferentes
        endereco.setBairro("Outro Bairro");
        endereco.setRua("Outra Rua");
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        // Terceira chamada - falha
        assertThrows(RuntimeException.class, () -> enderecoService.validarEndereco(endereco));
        
        verify(restTemplate, times(3)).getForObject(anyString(), eq(String.class));
    }

    // Métodos auxiliares
    private Endereco criarEnderecoValido() {
        Endereco endereco = new Endereco();
        endereco.setCep("88137074");
        endereco.setRua("Rua da Universidade");
        endereco.setBairro("Pedra Branca");
        endereco.setCidade("Palhoça");
        endereco.setEstado("SC");
        endereco.setNumero("123");
        return endereco;
    }

    private String criarRespostaViaCepValida() {
        return """
            {
                "cep": "88137-074",
                "logradouro": "Rua da Universidade",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
    }
} 