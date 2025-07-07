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

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.exception.endereco.CepInvalidoException;
import inkspiration.backend.exception.endereco.CidadeInvalidaException;
import inkspiration.backend.exception.endereco.EstadoInvalidoException;
import inkspiration.backend.exception.endereco.EnderecoValidacaoException;
import inkspiration.backend.service.EnderecoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de exceções - EnderecoService")
class EnderecoServiceExcecaoTest {

    @Mock
    private RestTemplate restTemplate;

    private EnderecoService enderecoService;

    @BeforeEach
    void setUp() {
        enderecoService = new EnderecoService(restTemplate);
    }

    
    @Test
    @DisplayName("Deve lançar CepInvalidoException para CEP com menos de 8 dígitos")
    void deveLancarCepInvalidoExceptionParaCepComMenosDe8Digitos() {
        
        Endereco endereco = criarEnderecoBase();

        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("1234567") 
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para CEP com mais de 8 dígitos")
    void deveLancarCepInvalidoExceptionParaCepComMaisDe8Digitos() {
        
        Endereco endereco = criarEnderecoBase();

        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("123456789") 
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para CEP com caracteres não numéricos")
    void deveLancarCepInvalidoExceptionParaCepComCaracteresNaoNumericos() {
        
        Endereco endereco = criarEnderecoBase();

        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCep("abcd1234") 
        );
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para timeout na API")
    void deveLancarCepInvalidoExceptionParaTimeoutNaAPI() {
        
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("Read timeout"));

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para JSON malformado")
    void deveLancarCepInvalidoExceptionParaJSONMalformado() {
        
        Endereco endereco = criarEnderecoValido();
        String jsonInvalido = "{ cep: 12345678, localidade: São Paulo }"; 
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(jsonInvalido);

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para resposta sem campos obrigatórios")
    void deveLancarCepInvalidoExceptionParaRespostaSemCamposObrigatorios() {
        
        Endereco endereco = criarEnderecoValido();
        String jsonIncompleto = """
            {
                "cep": "01310-100"
            }
            """; 
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(jsonIncompleto);

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    
    @Test
    @DisplayName("Deve lançar EstadoInvalidoException para estado vazio")
    void deveLancarEstadoInvalidoExceptionParaEstadoVazio() {
        
        Endereco endereco = criarEnderecoValido();

        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setEstado("   ") 
        );
        assertEquals("O estado não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar EstadoInvalidoException para estado divergente case-sensitive")
    void deveLancarEstadoInvalidoExceptionParaEstadoDivergenteCaseSensitive() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setEstado("RJ"); 
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        
        EstadoInvalidoException exception = assertThrows(
            EstadoInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Estado 'RJ' não corresponde ao CEP informado. Estado esperado: SC", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar EstadoInvalidoException para estado inexistente")
    void deveLancarEstadoInvalidoExceptionParaEstadoInexistente() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setEstado("XX"); 
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        
        EstadoInvalidoException exception = assertThrows(
            EstadoInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Estado 'XX' não corresponde ao CEP informado. Estado esperado: SC", exception.getMessage());
    }

    
    @Test
    @DisplayName("Deve lançar CidadeInvalidaException para cidade vazia")
    void deveLancarCidadeInvalidaExceptionParaCidadeVazia() {
        
        Endereco endereco = criarEnderecoValido();

        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> endereco.setCidade("   ") 
        );
        assertEquals("A cidade não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CidadeInvalidaException para cidade divergente")
    void deveLancarCidadeInvalidaExceptionParaCidadeDivergente() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setCidade("Rio de Janeiro"); 
        
        String respostaViaCep = criarRespostaViaCepValida();
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        
        CidadeInvalidaException exception = assertThrows(
            CidadeInvalidaException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Cidade 'Rio de Janeiro' não corresponde ao CEP informado. Cidade esperada: Palhoça", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CidadeInvalidaException para cidade com acentos diferentes")
    void deveLancarCidadeInvalidaExceptionParaCidadeComAcentosDiferentes() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setCidade("Palhoca"); 
        
        String respostaViaCep = criarRespostaViaCepValida(); 
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn(respostaViaCep);

        
        CidadeInvalidaException exception = assertThrows(
            CidadeInvalidaException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Cidade 'Palhoca' não corresponde ao CEP informado. Cidade esperada: Palhoça", exception.getMessage());
    }

    
    @Test
    @DisplayName("Deve lançar EnderecoValidacaoException para bairro divergente")
    void deveLancarEnderecoValidacaoExceptionParaBairroDivergente() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setBairro("Centro"); 
        
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

        
        EnderecoValidacaoException exception = assertThrows(
            EnderecoValidacaoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Bairro 'Centro' não corresponde ao CEP informado. Bairro esperado: Pedra Branca", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar EnderecoValidacaoException para logradouro divergente")
    void deveLancarEnderecoValidacaoExceptionParaLogradouroDivergente() {
        
        Endereco endereco = criarEnderecoValido();
        endereco.setRua("Rua Augusta"); 
        
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

        
        EnderecoValidacaoException exception = assertThrows(
            EnderecoValidacaoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Logradouro 'Rua Augusta' não corresponde ao CEP informado. Logradouro esperado: Rua da Universidade", exception.getMessage());
    }

    
    @Test
    @DisplayName("Deve lançar CepInvalidoException para erro de DNS")
    void deveLancarCepInvalidoExceptionParaErroDNS() {
        
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("UnknownHostException: viacep.com.br"));

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para erro HTTP 500")
    void deveLancarCepInvalidoExceptionParaErroHTTP500() {
        
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenThrow(new RestClientException("500 Internal Server Error"));

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar CepInvalidoException para resposta vazia")
    void deveLancarCepInvalidoExceptionParaRespostaVazia() {
        
        Endereco endereco = criarEnderecoValido();
        
        when(restTemplate.getForObject(anyString(), eq(String.class)))
            .thenReturn("");

        
        CepInvalidoException exception = assertThrows(
            CepInvalidoException.class,
            () -> enderecoService.validarEndereco(endereco)
        );
        
        assertEquals("Erro ao consultar CEP na API do ViaCEP, tente novamente", exception.getMessage());
    }

    
    private Endereco criarEnderecoBase() {
        Endereco endereco = new Endereco();
        endereco.setRua("Rua da Universidade");
        endereco.setBairro("Pedra Branca");
        endereco.setCidade("Palhoça");
        endereco.setEstado("SC");
        endereco.setNumero("123");
        return endereco;
    }

    private Endereco criarEnderecoValido() {
        Endereco endereco = criarEnderecoBase();
        endereco.setCep("88137074");
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