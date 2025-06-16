package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;

public class EnderecoTest {

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
    }

    @Test
    void testConstrutorVazio() {
        Endereco novoEndereco = new Endereco();
        
        assertNull(novoEndereco.getIdEndereco(), "ID deve ser nulo inicialmente");
        assertNull(novoEndereco.getCep(), "CEP deve ser nulo inicialmente");
        assertNull(novoEndereco.getRua(), "Rua deve ser nula inicialmente");
        assertNull(novoEndereco.getBairro(), "Bairro deve ser nulo inicialmente");
        assertNull(novoEndereco.getComplemento(), "Complemento deve ser nulo inicialmente");
        assertNull(novoEndereco.getCidade(), "Cidade deve ser nula inicialmente");
        assertNull(novoEndereco.getEstado(), "Estado deve ser nulo inicialmente");
        assertNull(novoEndereco.getLatitude(), "Latitude deve ser nula inicialmente");
        assertNull(novoEndereco.getLongitude(), "Longitude deve ser nula inicialmente");
        assertNull(novoEndereco.getNumero(), "Número deve ser nulo inicialmente");
    }

    @Test
    void testGettersAndSettersIdEndereco() {
        Long id = 1L;
        endereco.setIdEndereco(id);
        assertEquals(id, endereco.getIdEndereco(), "ID do endereço deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCep() {
        String cep = "01234-567";
        endereco.setCep(cep);
        assertEquals(cep, endereco.getCep(), "CEP deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersRua() {
        String rua = "Rua das Flores";
        endereco.setRua(rua);
        assertEquals(rua, endereco.getRua(), "Rua deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersBairro() {
        String bairro = "Centro";
        endereco.setBairro(bairro);
        assertEquals(bairro, endereco.getBairro(), "Bairro deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersComplemento() {
        String complemento = "Apartamento 101";
        endereco.setComplemento(complemento);
        assertEquals(complemento, endereco.getComplemento(), "Complemento deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersCidade() {
        String cidade = "São Paulo";
        endereco.setCidade(cidade);
        assertEquals(cidade, endereco.getCidade(), "Cidade deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersEstado() {
        String estado = "SP";
        endereco.setEstado(estado);
        assertEquals(estado, endereco.getEstado(), "Estado deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersLatitude() {
        Double latitude = -23.550520;
        endereco.setLatitude(latitude);
        assertEquals(latitude, endereco.getLatitude(), "Latitude deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersLongitude() {
        Double longitude = -46.633308;
        endereco.setLongitude(longitude);
        assertEquals(longitude, endereco.getLongitude(), "Longitude deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersNumero() {
        String numero = "123";
        endereco.setNumero(numero);
        assertEquals(numero, endereco.getNumero(), "Número deve ser igual ao definido");
    }

    @Test
    void testEnderecoCompleto() {
        // Arrange
        Long id = 1L;
        String cep = "01234-567";
        String rua = "Rua das Flores";
        String bairro = "Centro";
        String complemento = "Apartamento 101";
        String cidade = "São Paulo";
        String estado = "SP";
        Double latitude = -23.550520;
        Double longitude = -46.633308;
        String numero = "123";

        // Act
        endereco.setIdEndereco(id);
        endereco.setCep(cep);
        endereco.setRua(rua);
        endereco.setBairro(bairro);
        endereco.setComplemento(complemento);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setLatitude(latitude);
        endereco.setLongitude(longitude);
        endereco.setNumero(numero);

        // Assert
        assertEquals(id, endereco.getIdEndereco());
        assertEquals(cep, endereco.getCep());
        assertEquals(rua, endereco.getRua());
        assertEquals(bairro, endereco.getBairro());
        assertEquals(complemento, endereco.getComplemento());
        assertEquals(cidade, endereco.getCidade());
        assertEquals(estado, endereco.getEstado());
        assertEquals(latitude, endereco.getLatitude());
        assertEquals(longitude, endereco.getLongitude());
        assertEquals(numero, endereco.getNumero());
    }

    @Test
    void testValoresNulos() {
        assertDoesNotThrow(() -> {
            endereco.setCep(null);
            endereco.setRua(null);
            endereco.setBairro(null);
            endereco.setComplemento(null);
            endereco.setCidade(null);
            endereco.setEstado(null);
            endereco.setLatitude(null);
            endereco.setLongitude(null);
            endereco.setNumero(null);
        }, "Deve aceitar valores nulos sem lançar exceção");
    }

    @Test
    void testCepFormatado() {
        String[] cepsValidos = {"12345-678", "01234-567", "87654-321"};
        
        for (String cep : cepsValidos) {
            endereco.setCep(cep);
            assertEquals(cep, endereco.getCep(), "CEP formatado deve ser aceito");
        }
    }

    @Test
    void testCepSemFormatacao() {
        String cepSemFormatacao = "12345678";
        endereco.setCep(cepSemFormatacao);
        assertEquals(cepSemFormatacao, endereco.getCep(), "CEP sem formatação deve ser aceito");
    }

    @Test
    void testCoordenadas() {
        // Coordenadas válidas (São Paulo)
        endereco.setLatitude(-23.550520);
        endereco.setLongitude(-46.633308);
        
        assertEquals(-23.550520, endereco.getLatitude(), 0.000001, "Latitude deve ser precisa");
        assertEquals(-46.633308, endereco.getLongitude(), 0.000001, "Longitude deve ser precisa");
    }

    @Test
    void testCoordenadasExtremas() {
        // Coordenadas nos limites do planeta
        endereco.setLatitude(90.0); // Polo Norte
        endereco.setLongitude(180.0); // Linha de data internacional
        
        assertEquals(90.0, endereco.getLatitude(), "Latitude máxima deve ser aceita");
        assertEquals(180.0, endereco.getLongitude(), "Longitude máxima deve ser aceita");
        
        endereco.setLatitude(-90.0); // Polo Sul
        endereco.setLongitude(-180.0); // Linha de data internacional
        
        assertEquals(-90.0, endereco.getLatitude(), "Latitude mínima deve ser aceita");
        assertEquals(-180.0, endereco.getLongitude(), "Longitude mínima deve ser aceita");
    }

    @Test
    void testCoordenadasZero() {
        endereco.setLatitude(0.0);
        endereco.setLongitude(0.0);
        
        assertEquals(0.0, endereco.getLatitude(), "Latitude zero deve ser aceita");
        assertEquals(0.0, endereco.getLongitude(), "Longitude zero deve ser aceita");
    }

    @Test
    void testEstadosSiglas() {
        String[] estadosValidos = {"SP", "RJ", "MG", "PR", "SC", "RS", "BA", "GO", "DF"};
        
        for (String estado : estadosValidos) {
            endereco.setEstado(estado);
            assertEquals(estado, endereco.getEstado(), "Estado " + estado + " deve ser aceito");
        }
    }

    @Test
    void testNumeroString() {
        String[] numerosValidos = {"123", "456A", "S/N", "KM 15", "Lote 10"};
        
        for (String numero : numerosValidos) {
            endereco.setNumero(numero);
            assertEquals(numero, endereco.getNumero(), "Número " + numero + " deve ser aceito");
        }
    }

    @Test
    void testComplementoVazio() {
        endereco.setComplemento("");
        assertEquals("", endereco.getComplemento(), "Complemento vazio deve ser aceito");
    }

    @Test
    void testRuaComCaracteresEspeciais() {
        String ruaEspecial = "Rua José da Silva, 123 - Bloco A";
        endereco.setRua(ruaEspecial);
        assertEquals(ruaEspecial, endereco.getRua(), "Rua com caracteres especiais deve ser aceita");
    }

    @Test
    void testCidadeComAcentos() {
        String cidadeComAcento = "São José dos Campos";
        endereco.setCidade(cidadeComAcento);
        assertEquals(cidadeComAcento, endereco.getCidade(), "Cidade com acentos deve ser aceita");
    }

    @Test
    void testBairroComEspacos() {
        String bairroComEspacos = "Vila Nova Conceição";
        endereco.setBairro(bairroComEspacos);
        assertEquals(bairroComEspacos, endereco.getBairro(), "Bairro com espaços deve ser aceito");
    }

    @Test
    void testValoresLimite() {
        // Teste com strings muito grandes
        String textoGrande = "a".repeat(1000);
        
        assertDoesNotThrow(() -> {
            endereco.setCep(textoGrande);
            endereco.setRua(textoGrande);
            endereco.setBairro(textoGrande);
            endereco.setComplemento(textoGrande);
            endereco.setCidade(textoGrande);
            endereco.setEstado(textoGrande);
            endereco.setNumero(textoGrande);
        }, "Deve aceitar strings grandes sem lançar exceção");
    }
} 