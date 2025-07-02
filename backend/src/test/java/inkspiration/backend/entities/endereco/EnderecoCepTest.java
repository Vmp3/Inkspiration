package inkspiration.backend.entities.endereco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;

@DisplayName("Testes de validação de CEP - Endereco")
public class EnderecoCepTest {

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
    }

    @Test
    @DisplayName("Deve aceitar CEP válido com 8 dígitos")
    void deveAceitarCepValidoCom8Digitos() {
        String cep = "01234567";
        endereco.setCep(cep);
        assertEquals(cep, endereco.getCep());
    }

    @Test
    @DisplayName("Deve limpar formatação do CEP")
    void deveLimparFormatacaoDoCep() {
        endereco.setCep("01234-567");
        assertEquals("01234567", endereco.getCep());
    }

    @Test
    @DisplayName("Deve remover espaços do CEP")
    void deveRemoverEspacosDoCep() {
        endereco.setCep("01 234 567");
        assertEquals("01234567", endereco.getCep());
    }

    @Test
    @DisplayName("Deve remover pontos do CEP")
    void deveRemoverPontosDoCep() {
        endereco.setCep("01.234.567");
        assertEquals("01234567", endereco.getCep());
    }

    @Test
    @DisplayName("Deve remover caracteres especiais do CEP")
    void deveRemoverCaracteresEspeciaisDoCep() {
        endereco.setCep("01@23#45$67");
        assertEquals("01234567", endereco.getCep());
    }

    @Test
    @DisplayName("Não deve aceitar CEP nulo")
    void naoDeveAceitarCepNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep(null);
        });
        assertEquals("O CEP não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP vazio")
    void naoDeveAceitarCepVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("");
        });
        assertEquals("O CEP não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP com apenas espaços")
    void naoDeveAceitarCepComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("   ");
        });
        assertEquals("O CEP não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP com menos de 8 dígitos")
    void naoDeveAceitarCepComMenosDe8Digitos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("0123456");
        });
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP com mais de 8 dígitos")
    void naoDeveAceitarCepComMaisDe8Digitos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("012345678");
        });
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP apenas com letras")
    void naoDeveAceitarCepApenasComLetras() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("abcdefgh");
        });
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CEP misturado com letras e números")
    void naoDeveAceitarCepMisturadoComLetrasENumeros() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setCep("01abc567");
        });
        assertEquals("CEP deve ter exatamente 8 dígitos", exception.getMessage());
    }
} 