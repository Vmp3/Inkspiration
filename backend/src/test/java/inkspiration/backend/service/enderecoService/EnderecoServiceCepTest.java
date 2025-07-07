package inkspiration.backend.service.enderecoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.exception.endereco.CepInvalidoException;
import inkspiration.backend.service.EnderecoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de CEP - EnderecoService")
class EnderecoServiceCepTest {

    @Mock
    private RestTemplate restTemplate;

    private EnderecoService enderecoService;

    @BeforeEach
    void setUp() {
        enderecoService = new EnderecoService(restTemplate);
    }

    @Test
    @DisplayName("Deve aceitar CEP com formato padrão (8 dígitos)")
    void deveAceitarCepComFormatoPadrao() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("88137074");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve limpar CEP com máscara (formato 12345-678)")
    void deveLimparCepComMascara() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("88137-074");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve limpar CEP com espaços")
    void deveLimparCepComEspacos() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep(" 881 370 74 ");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve limpar CEP com pontos")
    void deveLimparCepComPontos() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("881.370.74");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve limpar CEP com múltiplos caracteres especiais")
    void deveLimparCepComMultiplosCaracteresEspeciais() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("8#8@1$3%7&0*7(4)");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/88137074/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve rejeitar CEP com 7 dígitos")
    void deveRejeitarCepCom7Digitos() {
        // Arrange
        Endereco endereco = criarEnderecoBase();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("0131010") // 7 dígitos
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve rejeitar CEP com 9 dígitos")
    void deveRejeitarCepCom9Digitos() {
        // Arrange
        Endereco endereco = criarEnderecoBase();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("013101001") // 9 dígitos
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve rejeitar CEP com letras")
    void deveRejeitarCepComLetras() {
        // Arrange
        Endereco endereco = criarEnderecoBase();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("0131010A") // contém letra
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve rejeitar CEP totalmente alfabético")
    void deveRejeitarCepTotalmenteAlfabetico() {
        // Arrange
        Endereco endereco = criarEnderecoBase();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("ABCDEFGH") // só letras
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve rejeitar CEP vazio após limpeza")
    void deveRejeitarCepVazioAposLimpeza() {
        // Arrange
        Endereco endereco = criarEnderecoBase();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("!@#$%^&*") // só caracteres especiais
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve aceitar CEP com zeros à esquerda")
    void deveAceitarCepComZerosAEsquerda() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("00123456");
        
        String respostaViaCep = """
            {
                "cep": "00123-456",
                "logradouro": "Rua da Universidade",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/00123456/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve aceitar CEP com todos os dígitos iguais")
    void deveAceitarCepComTodosDigitosIguais() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("11111111");
        
        String respostaViaCep = """
            {
                "cep": "11111-111",
                "logradouro": "Rua da Universidade",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/11111111/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve processar CEP com resposta de erro da API")
    void deveProcessarCepComRespostaDeErroDaAPI() {
        // Arrange
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("99999999"); // CEP que não existe
        
        String respostaErro = """
            {
                "erro": true
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaErro);

        // Act & Assert
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
        
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(String.class));
        assertEquals("https://viacep.com.br/ws/99999999/json/", urlCaptor.getValue());
    }

    @Test
    @DisplayName("Deve validar diferentes CEPs válidos do Brasil")
    void deveValidarDiferentesCepsValidosDoBrasil() {
        // Arrange
        String[] cepsValidos = {
            "88137074", // Palhoça - SC
            "20040020", // Rio de Janeiro - RJ  
            "30112000", // Belo Horizonte - MG
            "40070110", // Salvador - BA
            "70040010"  // Brasília - DF
        };
        
        for (String cep : cepsValidos) {
            Endereco endereco = criarEnderecoBase();
            endereco.setCep(cep);
            
            String respostaViaCep = String.format("""
                {
                    "cep": "%s",
                    "logradouro": "Rua da Universidade",
                    "bairro": "Pedra Branca",
                    "localidade": "Palhoça",
                    "uf": "SC"
                }
                """, cep.substring(0, 5) + "-" + cep.substring(5));
            
            when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(respostaViaCep);

            // Act & Assert
            assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco),
                "CEP " + cep + " deveria ser válido");
            
            reset(restTemplate); // Reset para próxima iteração
        }
    }

    // Métodos auxiliares
    private Endereco criarEnderecoBase() {
        Endereco endereco = new Endereco();
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