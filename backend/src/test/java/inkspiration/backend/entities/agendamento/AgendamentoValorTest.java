package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;

@DisplayName("Testes de validação de valor - Agendamento")
public class AgendamentoValorTest {

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }

    @Test
    @DisplayName("Deve aceitar valor nulo")
    void deveAceitarValorNulo() {
        agendamento.setValor(null);
        assertNull(agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor válido pequeno")
    void deveAceitarValorValidoPequeno() {
        BigDecimal valor = new BigDecimal("50.00");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor válido médio")
    void deveAceitarValorValidoMedio() {
        BigDecimal valor = new BigDecimal("250.50");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor válido alto")
    void deveAceitarValorValidoAlto() {
        BigDecimal valor = new BigDecimal("1500.99");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor máximo permitido")
    void deveAceitarValorMaximoPermitido() {
        BigDecimal valor = new BigDecimal("999999.99");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor mínimo permitido")
    void deveAceitarValorMinimoPermitido() {
        BigDecimal valor = new BigDecimal("0.01");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor com muitas casas decimais")
    void deveAceitarValorComMuitasCasasDecimais() {
        BigDecimal valor = new BigDecimal("123.456789");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor inteiro")
    void deveAceitarValorInteiro() {
        BigDecimal valor = new BigDecimal("100");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Não deve aceitar valor zero")
    void naoDeveAceitarValorZero() {
        BigDecimal valor = BigDecimal.ZERO;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setValor(valor);
        });
        assertEquals("Valor deve ser maior que zero quando fornecido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar valor negativo")
    void naoDeveAceitarValorNegativo() {
        BigDecimal valor = new BigDecimal("-50.00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setValor(valor);
        });
        assertEquals("Valor deve ser maior que zero quando fornecido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar valor maior que o máximo permitido")
    void naoDeveAceitarValorMaiorQueMaximoPermitido() {
        BigDecimal valor = new BigDecimal("1000000.00");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setValor(valor);
        });
        assertEquals("Valor não pode exceder R$ 999.999,99", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar valor muito maior que o máximo")
    void naoDeveAceitarValorMuitoMaiorQueMaximo() {
        BigDecimal valor = new BigDecimal("9999999.99");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setValor(valor);
        });
        assertEquals("Valor não pode exceder R$ 999.999,99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar redefinição de valor")
    void deveAceitarRedefinicaoDeValor() {
        BigDecimal valor1 = new BigDecimal("100.00");
        BigDecimal valor2 = new BigDecimal("200.00");
        
        agendamento.setValor(valor1);
        assertEquals(valor1, agendamento.getValor());
        
        agendamento.setValor(valor2);
        assertEquals(valor2, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar alterar valor para nulo")
    void deveAceitarAlterarValorParaNulo() {
        BigDecimal valor = new BigDecimal("100.00");
        
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
        
        agendamento.setValor(null);
        assertNull(agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor com precisão exata de 2 casas decimais")
    void deveAceitarValorComPrecisaoExataDe2CasasDecimais() {
        BigDecimal valor = new BigDecimal("123.45");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor típico de tatuagem")
    void deveAceitarValorTipicoTatuagem() {
        BigDecimal valor = new BigDecimal("350.00");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }

    @Test
    @DisplayName("Deve aceitar valor típico de piercing")
    void deveAceitarValorTipicoPiercing() {
        BigDecimal valor = new BigDecimal("80.00");
        agendamento.setValor(valor);
        assertEquals(valor, agendamento.getValor());
    }
} 