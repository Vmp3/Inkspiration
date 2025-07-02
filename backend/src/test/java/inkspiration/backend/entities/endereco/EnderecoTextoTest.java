package inkspiration.backend.entities.endereco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;

@DisplayName("Testes de validação de campos de texto - Endereco")
public class EnderecoTextoTest {

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
    }

    // Testes para Rua
    @Test
    @DisplayName("Deve aceitar rua válida com 3 caracteres")
    void deveAceitarRuaValidaCom3Caracteres() {
        endereco.setRua("Rua");
        assertEquals("Rua", endereco.getRua());
    }

    @Test
    @DisplayName("Deve aceitar rua válida com 200 caracteres")
    void deveAceitarRuaValidaCom200Caracteres() {
        String rua = "R".repeat(200);
        endereco.setRua(rua);
        assertEquals(rua, endereco.getRua());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas da rua")
    void deveRemoverEspacosDasBodasDaRua() {
        endereco.setRua("  Rua das Flores  ");
        assertEquals("Rua das Flores", endereco.getRua());
    }

    @Test
    @DisplayName("Não deve aceitar rua nula")
    void naoDeveAceitarRuaNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setRua(null);
        });
        assertEquals("A rua não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar rua vazia")
    void naoDeveAceitarRuaVazia() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setRua("");
        });
        assertEquals("A rua não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar rua com apenas espaços")
    void naoDeveAceitarRuaComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setRua("   ");
        });
        assertEquals("A rua não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar rua com menos de 3 caracteres")
    void naoDeveAceitarRuaComMenosDe3Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setRua("Ru");
        });
        assertEquals("A rua deve ter entre 3 e 200 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar rua com mais de 200 caracteres")
    void naoDeveAceitarRuaComMaisDe200Caracteres() {
        String rua = "R".repeat(201);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setRua(rua);
        });
        assertEquals("A rua deve ter entre 3 e 200 caracteres", exception.getMessage());
    }

    // Testes para Bairro
    @Test
    @DisplayName("Deve aceitar bairro válido com 3 caracteres")
    void deveAceitarBairroValidoCom3Caracteres() {
        endereco.setBairro("ABC");
        assertEquals("ABC", endereco.getBairro());
    }

    @Test
    @DisplayName("Deve aceitar bairro válido com 100 caracteres")
    void deveAceitarBairroValidoCom100Caracteres() {
        String bairro = "B".repeat(100);
        endereco.setBairro(bairro);
        assertEquals(bairro, endereco.getBairro());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas do bairro")
    void deveRemoverEspacosDosBairro() {
        endereco.setBairro("  Centro  ");
        assertEquals("Centro", endereco.getBairro());
    }

    @Test
    @DisplayName("Não deve aceitar bairro nulo")
    void naoDeveAceitarBairroNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setBairro(null);
        });
        assertEquals("O bairro não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar bairro com menos de 3 caracteres")
    void naoDeveAceitarBairroComMenosDe3Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setBairro("AB");
        });
        assertEquals("O bairro deve ter entre 3 e 100 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar bairro com mais de 100 caracteres")
    void naoDeveAceitarBairroComMaisDe100Caracteres() {
        String bairro = "B".repeat(101);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setBairro(bairro);
        });
        assertEquals("O bairro deve ter entre 3 e 100 caracteres", exception.getMessage());
    }

    // Testes para Cidade
    @Test
    @DisplayName("Deve aceitar cidade válida com 2 caracteres")
    void deveAceitarCidadeValidaCom2Caracteres() {
        endereco.setCidade("SP");
        assertEquals("SP", endereco.getCidade());
    }

    @Test
    @DisplayName("Deve aceitar cidade válida com 100 caracteres")
    void deveAceitarCidadeValidaCom100Caracteres() {
        String cidade = "C".repeat(100);
        endereco.setCidade(cidade);
        assertEquals(cidade, endereco.getCidade());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas da cidade")
    void deveRemoverEspacosDaCidade() {
        endereco.setCidade("  São Paulo  ");
        assertEquals("São Paulo", endereco.getCidade());
    }

    @Test
    @DisplayName("Não deve aceitar cidade nula")
    void naoDeveAceitarCidadeNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCidade(null);
        });
        assertEquals("A cidade não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar cidade com menos de 2 caracteres")
    void naoDeveAceitarCidadeComMenosDe2Caracteres() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCidade("A");
        });
        assertEquals("A cidade deve ter entre 2 e 100 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar cidade com mais de 100 caracteres")
    void naoDeveAceitarCidadeComMaisDe100Caracteres() {
        String cidade = "C".repeat(101);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCidade(cidade);
        });
        assertEquals("A cidade deve ter entre 2 e 100 caracteres", exception.getMessage());
    }

    // Testes para Complemento
    @Test
    @DisplayName("Deve aceitar complemento válido")
    void deveAceitarComplementoValido() {
        String complemento = "Apartamento 123";
        endereco.setComplemento(complemento);
        assertEquals(complemento, endereco.getComplemento());
    }

    @Test
    @DisplayName("Deve aceitar complemento com 100 caracteres")
    void deveAceitarComplementoCom100Caracteres() {
        String complemento = "C".repeat(100);
        endereco.setComplemento(complemento);
        assertEquals(complemento, endereco.getComplemento());
    }

    @Test
    @DisplayName("Deve aceitar complemento nulo")
    void deveAceitarComplementoNulo() {
        endereco.setComplemento(null);
        assertNull(endereco.getComplemento());
    }

    @Test
    @DisplayName("Deve aceitar complemento vazio e converter para null")
    void deveAceitarComplementoVazioEConverterParaNull() {
        endereco.setComplemento("");
        assertNull(endereco.getComplemento());
    }

    @Test
    @DisplayName("Deve aceitar complemento com apenas espaços e converter para null")
    void deveAceitarComplementoComApenasEspacosEConverterParaNull() {
        endereco.setComplemento("   ");
        assertNull(endereco.getComplemento());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas do complemento")
    void deveRemoverEspacosDoComplemento() {
        endereco.setComplemento("  Apto 101  ");
        assertEquals("Apto 101", endereco.getComplemento());
    }

    @Test
    @DisplayName("Não deve aceitar complemento com mais de 100 caracteres")
    void naoDeveAceitarComplementoComMaisDe100Caracteres() {
        String complemento = "C".repeat(101);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setComplemento(complemento);
        });
        assertEquals("O complemento não pode exceder 100 caracteres", exception.getMessage());
    }

    // Testes para Número
    @Test
    @DisplayName("Deve aceitar número válido com 1 caractere")
    void deveAceitarNumeroValidoCom1Caractere() {
        endereco.setNumero("1");
        assertEquals("1", endereco.getNumero());
    }

    @Test
    @DisplayName("Deve aceitar número válido com 10 caracteres")
    void deveAceitarNumeroValidoCom10Caracteres() {
        String numero = "1234567890";
        endereco.setNumero(numero);
        assertEquals(numero, endereco.getNumero());
    }

    @Test
    @DisplayName("Deve aceitar número com letras (ex: 123A)")
    void deveAceitarNumeroComLetras() {
        endereco.setNumero("123A");
        assertEquals("123A", endereco.getNumero());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas do número")
    void deveRemoverEspacosDoNumero() {
        endereco.setNumero("  123  ");
        assertEquals("123", endereco.getNumero());
    }

    @Test
    @DisplayName("Não deve aceitar número nulo")
    void naoDeveAceitarNumeroNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setNumero(null);
        });
        assertEquals("O número não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar número vazio")
    void naoDeveAceitarNumeroVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setNumero("");
        });
        assertEquals("O número não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar número com apenas espaços")
    void naoDeveAceitarNumeroComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setNumero("   ");
        });
        assertEquals("O número não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar número com mais de 10 caracteres")
    void naoDeveAceitarNumeroComMaisDe10Caracteres() {
        String numero = "12345678901";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setNumero(numero);
        });
        assertEquals("O número deve ter entre 1 e 10 caracteres", exception.getMessage());
    }
} 