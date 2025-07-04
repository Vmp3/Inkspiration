package inkspiration.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.exception.endereco.CepInvalidoException;
import inkspiration.backend.exception.endereco.EstadoInvalidoException;
import inkspiration.backend.exception.endereco.CidadeInvalidaException;
import inkspiration.backend.exception.endereco.EnderecoValidacaoException;

@Service
public class EnderecoService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public EnderecoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Valida um endereço completo usando a API ViaCEP
     * @param endereco O endereço a ser validado
     * @throws EnderecoValidacaoException se o endereço for inválido
     */
    public void validarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new EnderecoValidacaoException("Endereço não pode ser nulo");
        }
        
        // Validar CEP primeiro
        if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
            throw new CepInvalidoException("CEP é obrigatório");
        }
        
        String cepLimpo = endereco.getCep().replaceAll("[^0-9]", "");
        if (cepLimpo.length() != 8) {
            throw new CepInvalidoException("CEP deve conter exatamente 8 dígitos");
        }
        
        // Buscar dados do CEP na API ViaCEP
        DadosViaCep dadosViaCep = buscarDadosCep(cepLimpo);
        
        // Validar estado
        if (endereco.getEstado() == null || endereco.getEstado().trim().isEmpty()) {
            throw new EstadoInvalidoException("Estado é obrigatório");
        }
        
        if (!dadosViaCep.getUf().equalsIgnoreCase(endereco.getEstado().trim())) {
            throw new EstadoInvalidoException(
                String.format("Estado '%s' não corresponde ao CEP informado. Estado esperado: %s", 
                endereco.getEstado(), dadosViaCep.getUf())
            );
        }
        
        // Validar cidade
        if (endereco.getCidade() == null || endereco.getCidade().trim().isEmpty()) {
            throw new CidadeInvalidaException("Cidade é obrigatória");
        }
        
        if (!dadosViaCep.getLocalidade().equalsIgnoreCase(endereco.getCidade().trim())) {
            throw new CidadeInvalidaException(
                String.format("Cidade '%s' não corresponde ao CEP informado. Cidade esperada: %s", 
                endereco.getCidade(), dadosViaCep.getLocalidade())
            );
        }
        
        // Validar bairro se fornecido no ViaCEP e no endereço
        if (dadosViaCep.getBairro() != null && !dadosViaCep.getBairro().trim().isEmpty() &&
            endereco.getBairro() != null && !endereco.getBairro().trim().isEmpty()) {
            if (!dadosViaCep.getBairro().equalsIgnoreCase(endereco.getBairro().trim())) {
                throw new EnderecoValidacaoException(
                    String.format("Bairro '%s' não corresponde ao CEP informado. Bairro esperado: %s", 
                    endereco.getBairro(), dadosViaCep.getBairro())
                );
            }
        }
        
        // Validar rua se fornecida no ViaCEP e no endereço
        if (dadosViaCep.getLogradouro() != null && !dadosViaCep.getLogradouro().trim().isEmpty() &&
            endereco.getRua() != null && !endereco.getRua().trim().isEmpty()) {
            if (!dadosViaCep.getLogradouro().equalsIgnoreCase(endereco.getRua().trim())) {
                throw new EnderecoValidacaoException(
                    String.format("Logradouro '%s' não corresponde ao CEP informado. Logradouro esperado: %s", 
                    endereco.getRua(), dadosViaCep.getLogradouro())
                );
            }
        }
    }
    
    /**
     * Busca dados de um CEP na API ViaCEP
     * @param cep CEP limpo (apenas números)
     * @return Dados do CEP
     * @throws CepInvalidoException se o CEP não for encontrado ou for inválido
     */
    private DadosViaCep buscarDadosCep(String cep) {
        try {
            String url = "https://viacep.com.br/ws/" + cep + "/json/";
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                throw new CepInvalidoException("Erro ao consultar CEP na API ViaCEP");
            }
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Verificar se o CEP foi encontrado
            if (jsonNode.has("erro") && jsonNode.get("erro").asBoolean()) {
                throw new CepInvalidoException("CEP não encontrado");
            }
            
            // Extrair dados necessários
            DadosViaCep dados = new DadosViaCep();
            dados.setLogradouro(jsonNode.get("logradouro").asText());
            dados.setBairro(jsonNode.get("bairro").asText());
            dados.setLocalidade(jsonNode.get("localidade").asText());
            dados.setUf(jsonNode.get("uf").asText());
            
            return dados;
            
        } catch (RestClientException e) {
            throw new CepInvalidoException("Erro de conexão ao consultar CEP: " + e.getMessage());
        } catch (Exception e) {
            throw new CepInvalidoException("Erro ao processar resposta da API ViaCEP: " + e.getMessage());
        }
    }
    
    /**
     * Classe interna para representar os dados retornados pela API ViaCEP
     */
    private static class DadosViaCep {
        private String logradouro;
        private String bairro;
        private String localidade;
        private String uf;
        
        // Getters e Setters
        public String getLogradouro() { return logradouro; }
        public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
        
        public String getBairro() { return bairro; }
        public void setBairro(String bairro) { this.bairro = bairro; }
        
        public String getLocalidade() { return localidade; }
        public void setLocalidade(String localidade) { this.localidade = localidade; }
        
        public String getUf() { return uf; }
        public void setUf(String uf) { this.uf = uf; }
    }
}