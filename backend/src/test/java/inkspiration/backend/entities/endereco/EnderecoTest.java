package inkspiration.backend.entities.endereco;

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
        String rua = "Rua das Flores, 123";
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
        String complemento = "Apt 101, Bloco A";
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
        Double latitude = -23.5505;
        endereco.setLatitude(latitude);
        assertEquals(latitude, endereco.getLatitude(), "Latitude deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersLongitude() {
        Double longitude = -46.6333;
        endereco.setLongitude(longitude);
        assertEquals(longitude, endereco.getLongitude(), "Longitude deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersNumero() {
        String numero = "123A";
        endereco.setNumero(numero);
        assertEquals(numero, endereco.getNumero(), "Número deve ser igual ao definido");
    }

    @Test
    void testConstrutorPadrao() {
        Endereco enderecoVazio = new Endereco();
        
        assertNull(enderecoVazio.getIdEndereco(), "ID deve ser nulo inicialmente");
        assertNull(enderecoVazio.getCep(), "CEP deve ser nulo inicialmente");
        assertNull(enderecoVazio.getRua(), "Rua deve ser nula inicialmente");
        assertNull(enderecoVazio.getBairro(), "Bairro deve ser nulo inicialmente");
        assertNull(enderecoVazio.getComplemento(), "Complemento deve ser nulo inicialmente");
        assertNull(enderecoVazio.getCidade(), "Cidade deve ser nula inicialmente");
        assertNull(enderecoVazio.getEstado(), "Estado deve ser nulo inicialmente");
        assertNull(enderecoVazio.getLatitude(), "Latitude deve ser nula inicialmente");
        assertNull(enderecoVazio.getLongitude(), "Longitude deve ser nula inicialmente");
        assertNull(enderecoVazio.getNumero(), "Número deve ser nulo inicialmente");
    }

    @Test
    void testEnderecoComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String cep = "01234-567";
        String rua = "Rua das Flores";
        String bairro = "Centro";
        String complemento = "Apt 101";
        String cidade = "São Paulo";
        String estado = "SP";
        Double latitude = -23.5505;
        Double longitude = -46.6333;
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
    void testCepFormatos() {
        String[] formatosCep = {
            "01234-567",
            "01234567",
            "12345-678",
            "98765-432"
        };

        for (String cep : formatosCep) {
            assertDoesNotThrow(() -> {
                endereco.setCep(cep);
                assertEquals(cep, endereco.getCep());
            }, "Deve aceitar formato de CEP: " + cep);
        }
    }

    @Test
    void testCoordenadasExtremas() {
        // Coordenadas extremas válidas
        Double latitudeMinima = -90.0;
        Double latitudeMaxima = 90.0;
        Double longitudeMinima = -180.0;
        Double longitudeMaxima = 180.0;

        endereco.setLatitude(latitudeMinima);
        assertEquals(latitudeMinima, endereco.getLatitude(), "Deve aceitar latitude mínima");

        endereco.setLatitude(latitudeMaxima);
        assertEquals(latitudeMaxima, endereco.getLatitude(), "Deve aceitar latitude máxima");

        endereco.setLongitude(longitudeMinima);
        assertEquals(longitudeMinima, endereco.getLongitude(), "Deve aceitar longitude mínima");

        endereco.setLongitude(longitudeMaxima);
        assertEquals(longitudeMaxima, endereco.getLongitude(), "Deve aceitar longitude máxima");
    }

    @Test
    void testCoordenadasPrecisas() {
        Double latitudePrecisa = -23.550520123456789;
        Double longitudePrecisa = -46.633308987654321;

        endereco.setLatitude(latitudePrecisa);
        endereco.setLongitude(longitudePrecisa);

        assertEquals(latitudePrecisa, endereco.getLatitude(), "Deve manter precisão da latitude");
        assertEquals(longitudePrecisa, endereco.getLongitude(), "Deve manter precisão da longitude");
    }

    @Test
    void testNumerosVariados() {
        String[] numerosVariados = {
            "123",
            "123A",
            "123-A",
            "S/N",
            "Lote 15",
            "Quadra 10, Lote 5",
            "KM 25",
            "123/125"
        };

        for (String numero : numerosVariados) {
            assertDoesNotThrow(() -> {
                endereco.setNumero(numero);
                assertEquals(numero, endereco.getNumero());
            }, "Deve aceitar formato de número: " + numero);
        }
    }

    @Test
    void testComplementosVariados() {
        String[] complementos = {
            "Apt 101",
            "Bloco A, Apt 202",
            "Casa dos Fundos",
            "Sobreloja",
            "Andar 15, Sala 1501",
            "Torre Norte",
            "Próximo ao mercado",
            "Casa azul com portão branco"
        };

        for (String complemento : complementos) {
            assertDoesNotThrow(() -> {
                endereco.setComplemento(complemento);
                assertEquals(complemento, endereco.getComplemento());
            }, "Deve aceitar complemento: " + complemento);
        }
    }

    @Test
    void testEstadosSiglas() {
        String[] estados = {
            "SP", "RJ", "MG", "RS", "PR", "SC", "BA", "GO", "MS", "MT",
            "ES", "PE", "CE", "PA", "MA", "PB", "RN", "AL", "SE", "PI",
            "AC", "AP", "AM", "RO", "RR", "TO", "DF"
        };

        for (String estado : estados) {
            assertDoesNotThrow(() -> {
                endereco.setEstado(estado);
                assertEquals(estado, endereco.getEstado());
            }, "Deve aceitar estado: " + estado);
        }
    }

    @Test
    void testCamposVazios() {
        endereco.setCep("");
        endereco.setRua("");
        endereco.setBairro("");
        endereco.setComplemento("");
        endereco.setCidade("");
        endereco.setEstado("");
        endereco.setNumero("");

        assertEquals("", endereco.getCep(), "Deve aceitar CEP vazio");
        assertEquals("", endereco.getRua(), "Deve aceitar rua vazia");
        assertEquals("", endereco.getBairro(), "Deve aceitar bairro vazio");
        assertEquals("", endereco.getComplemento(), "Deve aceitar complemento vazio");
        assertEquals("", endereco.getCidade(), "Deve aceitar cidade vazia");
        assertEquals("", endereco.getEstado(), "Deve aceitar estado vazio");
        assertEquals("", endereco.getNumero(), "Deve aceitar número vazio");
    }

    @Test
    void testCamposNulos() {
        endereco.setCep(null);
        endereco.setRua(null);
        endereco.setBairro(null);
        endereco.setComplemento(null);
        endereco.setCidade(null);
        endereco.setEstado(null);
        endereco.setLatitude(null);
        endereco.setLongitude(null);
        endereco.setNumero(null);

        assertNull(endereco.getCep(), "Deve aceitar CEP nulo");
        assertNull(endereco.getRua(), "Deve aceitar rua nula");
        assertNull(endereco.getBairro(), "Deve aceitar bairro nulo");
        assertNull(endereco.getComplemento(), "Deve aceitar complemento nulo");
        assertNull(endereco.getCidade(), "Deve aceitar cidade nula");
        assertNull(endereco.getEstado(), "Deve aceitar estado nulo");
        assertNull(endereco.getLatitude(), "Deve aceitar latitude nula");
        assertNull(endereco.getLongitude(), "Deve aceitar longitude nula");
        assertNull(endereco.getNumero(), "Deve aceitar número nulo");
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        endereco.setIdEndereco(idMaximo);
        assertEquals(idMaximo, endereco.getIdEndereco(), "Deve aceitar ID máximo");
        
        endereco.setIdEndereco(idMinimo);
        assertEquals(idMinimo, endereco.getIdEndereco(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testCoordenadasZero() {
        endereco.setLatitude(0.0);
        endereco.setLongitude(0.0);

        assertEquals(0.0, endereco.getLatitude(), "Deve aceitar latitude zero");
        assertEquals(0.0, endereco.getLongitude(), "Deve aceitar longitude zero");
    }

    @Test
    void testCaracteresEspeciais() {
        endereco.setRua("Rua José da Conceição, nº 123");
        endereco.setBairro("São José dos Campos");
        endereco.setCidade("São Paulo");
        endereco.setComplemento("Apto. 101 - Bloco \"A\"");

        assertEquals("Rua José da Conceição, nº 123", endereco.getRua(), "Deve aceitar caracteres especiais na rua");
        assertEquals("São José dos Campos", endereco.getBairro(), "Deve aceitar acentos no bairro");
        assertEquals("São Paulo", endereco.getCidade(), "Deve aceitar acentos na cidade");
        assertEquals("Apto. 101 - Bloco \"A\"", endereco.getComplemento(), "Deve aceitar aspas no complemento");
    }

    @Test
    void testEnderecoBrasileiro() {
        endereco.setCep("01310-100");
        endereco.setRua("Avenida Paulista");
        endereco.setNumero("1578");
        endereco.setBairro("Bela Vista");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setLatitude(-23.561684);
        endereco.setLongitude(-46.655981);

        assertAll("Endereço brasileiro completo",
            () -> assertEquals("01310-100", endereco.getCep()),
            () -> assertEquals("Avenida Paulista", endereco.getRua()),
            () -> assertEquals("1578", endereco.getNumero()),
            () -> assertEquals("Bela Vista", endereco.getBairro()),
            () -> assertEquals("São Paulo", endereco.getCidade()),
            () -> assertEquals("SP", endereco.getEstado()),
            () -> assertEquals(-23.561684, endereco.getLatitude()),
            () -> assertEquals(-46.655981, endereco.getLongitude())
        );
    }
} 