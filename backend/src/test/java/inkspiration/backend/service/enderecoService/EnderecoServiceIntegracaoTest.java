package inkspiration.backend.service.enderecoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.exception.endereco.CepInvalidoException;
import inkspiration.backend.exception.endereco.CidadeInvalidaException;
import inkspiration.backend.exception.endereco.EstadoInvalidoException;
import inkspiration.backend.exception.endereco.EnderecoValidacaoException;
import inkspiration.backend.service.EnderecoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de integração - EnderecoService")
class EnderecoServiceIntegracaoTest {

    @Mock
    private RestTemplate restTemplate;

    private EnderecoService enderecoService;

    @BeforeEach
    void setUp() {
        enderecoService = new EnderecoService(restTemplate);
    }

    @Test
    @DisplayName("Deve validar endereço completo com sucesso")
    void deveValidarEnderecoCompletoComSucesso() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        String respostaViaCep = criarRespostaViaCepValida();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
        
        verify(restTemplate).getForObject(eq("https://viacep.com.br/ws/88137074/json/"), eq(String.class));
    }

    @Test
    @DisplayName("Deve validar endereço com bairro correspondente")
    void deveValidarEnderecoComBairroCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setBairro("Pedra Branca");
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
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
    }

    @Test
    @DisplayName("Deve validar endereço com logradouro correspondente")
    void deveValidarEnderecoComLogradouroCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setRua("Rua da Universidade");
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
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
    }

    @Test
    @DisplayName("Deve lançar exceção para endereço nulo")
    void deveLancarExcecaoParaEnderecoNulo() {
        // Act & Assert
        EnderecoValidacaoException exception = assertThrows(
            EnderecoValidacaoException.class,
            () -> enderecoService.validarEndereco(null)
        );
        
        assertEquals("Endereço não pode ser nulo", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP nulo")
    void deveLancarExcecaoParaCepNulo() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep(null)
        );
        assertEquals("O CEP não pode ser nulo ou vazio", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP vazio")
    void deveLancarExcecaoParaCepVazio() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("   ")
        );
        assertEquals("O CEP não pode ser nulo ou vazio", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP com formato inválido")
    void deveLancarExcecaoParaCepComFormatoInvalido() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("1234567") // 7 dígitos
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar exceção para estado nulo")
    void deveLancarExcecaoParaEstadoNulo() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setEstado(null)
        );
        assertEquals("O estado não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para estado não correspondente")
    void deveLancarExcecaoParaEstadoNaoCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setEstado("RJ"); // CEP é de SP
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        EstadoInvalidoException exception = assertThrows(
            EstadoInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Estado 'RJ' não corresponde ao CEP informado. Estado esperado: SC", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para cidade nula")
    void deveLancarExcecaoParaCidadeNula() {
        // Arrange
        Endereco endereco = criarEnderecoValido();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCidade(null)
        );
        assertEquals("A cidade não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para cidade não correspondente")
    void deveLancarExcecaoParaCidadeNaoCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setCidade("Rio de Janeiro"); // CEP é de Palhoça
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        CidadeInvalidaException exception = assertThrows(
            CidadeInvalidaException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Cidade 'Rio de Janeiro' não corresponde ao CEP informado. Cidade esperada: Palhoça", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para bairro não correspondente")
    void deveLancarExcecaoParaBairroNaoCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setBairro("Centro"); // ViaCEP retorna "Pedra Branca"
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
                "logradouro": "Rua da Universidade",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        EnderecoValidacaoException exception = assertThrows(
            EnderecoValidacaoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Bairro 'Centro' não corresponde ao CEP informado. Bairro esperado: Pedra Branca", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para logradouro não correspondente")
    void deveLancarExcecaoParaLogradouroNaoCorrespondente() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setRua("Rua Augusta"); // ViaCEP retorna "Rua da Universidade"
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
                "logradouro": "Rua da Universidade",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        EnderecoValidacaoException exception = assertThrows(
            EnderecoValidacaoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Logradouro 'Rua Augusta' não corresponde ao CEP informado. Logradouro esperado: Rua da Universidade", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP não encontrado na API")
    void deveLancarExcecaoParaCepNaoEncontradoNaAPI() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
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
    }

    @Test
    @DisplayName("Deve lançar exceção para erro de conexão com API")
    void deveLancarExcecaoParaErroConexaoComAPI() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Erro de conexão"));

        // Act & Assert
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para resposta nula da API")
    void deveLancarExcecaoParaRespostaNulaDaAPI() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(null);

        // Act & Assert
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve validar endereço com comparação case-insensitive")
    void deveValidarEnderecoComComparacaoCaseInsensitive() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setEstado("sc"); // minúsculo
        endereco.setCidade("palhoça"); // minúsculo
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
    }

    @Test
    @DisplayName("Deve validar endereço com trimming de espaços")
    void deveValidarEnderecoComTrimmingDeEspacos() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setEstado("  SC  ");
        endereco.setCidade("  Palhoça  ");
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
    }

    @Test
    @DisplayName("Deve validar endereço com bairro vazio no ViaCEP")
    void deveValidarEnderecoComBairroVazioNoViaCEP() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setBairro("Centro");
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
                "logradouro": "Rua da Universidade",
                "bairro": "",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert - Não deve lançar exceção quando bairro está vazio no ViaCEP
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
    }

    @Test
    @DisplayName("Deve validar endereço com logradouro vazio no ViaCEP")
    void deveValidarEnderecoComLogradouroVazioNoViaCEP() {
        // Arrange
        Endereco endereco = criarEnderecoValido();
        endereco.setRua("Rua da Universidade");
        
        String respostaViaCep = """
            {
                "cep": "88137-074",
                "logradouro": "",
                "bairro": "Pedra Branca",
                "localidade": "Palhoça",
                "uf": "SC"
            }
            """;
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        // Act & Assert - Não deve lançar exceção quando logradouro está vazio no ViaCEP
        assertDoesNotThrow(() -> enderecoService.validarEndereco(endereco));
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