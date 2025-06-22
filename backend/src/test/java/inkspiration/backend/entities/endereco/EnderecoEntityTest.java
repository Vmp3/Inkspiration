package inkspiration.backend.entities.endereco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;

@DisplayName("Testes gerais da entidade Endereco")
public class EnderecoEntityTest {

    @Test
    @DisplayName("Deve criar endereço com construtor padrão")
    void deveCriarEnderecoComConstrutorPadrao() {
        Endereco endereco = new Endereco();
        
        assertNotNull(endereco);
        assertNull(endereco.getIdEndereco());
        assertNull(endereco.getCep());
        assertNull(endereco.getRua());
        assertNull(endereco.getBairro());
        assertNull(endereco.getComplemento());
        assertNull(endereco.getCidade());
        assertNull(endereco.getEstado());
        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
        assertNull(endereco.getNumero());
    }

    @Test
    @DisplayName("Deve definir e obter ID do endereço")
    void deveDefinirEObterIdDoEndereco() {
        Endereco endereco = new Endereco();
        Long id = 789L;
        
        endereco.setIdEndereco(id);
        assertEquals(id, endereco.getIdEndereco());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        Endereco endereco = new Endereco();
        
        endereco.setIdEndereco(null);
        assertNull(endereco.getIdEndereco());
    }

    @Test
    @DisplayName("Deve criar endereço completo válido")
    void deveCriarEnderecoCompletoValido() {
        Endereco endereco = new Endereco();
        
        endereco.setCep("01234567");
        endereco.setRua("Rua das Flores");
        endereco.setBairro("Centro");
        endereco.setComplemento("Apto 101");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setLatitude(-23.5505);
        endereco.setLongitude(-46.6333);
        endereco.setNumero("123");
        
        assertEquals("01234567", endereco.getCep());
        assertEquals("Rua das Flores", endereco.getRua());
        assertEquals("Centro", endereco.getBairro());
        assertEquals("Apto 101", endereco.getComplemento());
        assertEquals("São Paulo", endereco.getCidade());
        assertEquals("SP", endereco.getEstado());
        assertEquals(-23.5505, endereco.getLatitude());
        assertEquals(-46.6333, endereco.getLongitude());
        assertEquals("123", endereco.getNumero());
    }

    @Test
    @DisplayName("Deve criar endereço mínimo válido sem complemento e coordenadas")
    void deveCriarEnderecoMinimoValidoSemComplementoECoordenadas() {
        Endereco endereco = new Endereco();
        
        endereco.setCep("01234567");
        endereco.setRua("Rua das Flores");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setNumero("123");
        
        assertEquals("01234567", endereco.getCep());
        assertEquals("Rua das Flores", endereco.getRua());
        assertEquals("Centro", endereco.getBairro());
        assertNull(endereco.getComplemento());
        assertEquals("São Paulo", endereco.getCidade());
        assertEquals("SP", endereco.getEstado());
        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
        assertEquals("123", endereco.getNumero());
    }

    @Test
    @DisplayName("Campos obrigatórios não devem aceitar valores inválidos")
    void camposObrigatoriosNaoDevemAceitarValoresInvalidos() {
        Endereco endereco = new Endereco();
        
        // Testa CEP
        assertThrows(IllegalArgumentException.class, () -> endereco.setCep(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setCep(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setCep("123"));
        
        // Testa Rua
        assertThrows(IllegalArgumentException.class, () -> endereco.setRua(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setRua(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setRua("AB"));
        
        // Testa Bairro
        assertThrows(IllegalArgumentException.class, () -> endereco.setBairro(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setBairro(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setBairro("AB"));
        
        // Testa Cidade
        assertThrows(IllegalArgumentException.class, () -> endereco.setCidade(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setCidade(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setCidade("A"));
        
        // Testa Estado
        assertThrows(IllegalArgumentException.class, () -> endereco.setEstado(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setEstado(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setEstado("S"));
        
        // Testa Número
        assertThrows(IllegalArgumentException.class, () -> endereco.setNumero(null));
        assertThrows(IllegalArgumentException.class, () -> endereco.setNumero(""));
        assertThrows(IllegalArgumentException.class, () -> endereco.setNumero("12345678901"));
    }

    @Test
    @DisplayName("Campos opcionais devem aceitar valores nulos")
    void camposOpcionaisDevemAceitarValoresNulos() {
        Endereco endereco = new Endereco();
        
        // Complemento é opcional
        endereco.setComplemento(null);
        assertNull(endereco.getComplemento());
        
        // Coordenadas são opcionais
        endereco.setLatitude(null);
        endereco.setLongitude(null);
        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve validar limites de coordenadas corretamente")
    void deveValidarLimitesDeCoordendasCorretamente() {
        Endereco endereco = new Endereco();
        
        // Latitude - limites válidos
        endereco.setLatitude(-90.0);
        assertEquals(-90.0, endereco.getLatitude());
        
        endereco.setLatitude(90.0);
        assertEquals(90.0, endereco.getLatitude());
        
        // Longitude - limites válidos
        endereco.setLongitude(-180.0);
        assertEquals(-180.0, endereco.getLongitude());
        
        endereco.setLongitude(180.0);
        assertEquals(180.0, endereco.getLongitude());
        
        // Limites inválidos
        assertThrows(IllegalArgumentException.class, () -> endereco.setLatitude(90.1));
        assertThrows(IllegalArgumentException.class, () -> endereco.setLatitude(-90.1));
        assertThrows(IllegalArgumentException.class, () -> endereco.setLongitude(180.1));
        assertThrows(IllegalArgumentException.class, () -> endereco.setLongitude(-180.1));
    }
} 