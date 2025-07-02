package inkspiration.backend.entities.avaliacao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Avaliacao;

@DisplayName("Avaliacao - Testes Unitários")
class AvaliacaoTest {

    private Avaliacao avaliacao;
    private Agendamento agendamento;
    private final Long ID_AVALIACAO = 1L;
    private final String DESCRICAO = "Excelente atendimento, profissional muito atencioso e competente";
    private final Integer RATING = 5;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
        agendamento.setIdAgendamento(1L);

        avaliacao = new Avaliacao();
    }

    @Test
    @DisplayName("Deve criar avaliação com construtor vazio")
    void deveCriarAvaliacaoComConstrutorVazio() {
        // Assert
        assertNotNull(avaliacao);
    }

    @Test
    @DisplayName("Deve criar avaliação com construtor completo")
    void deveCriarAvaliacaoComConstrutorCompleto() {
        // Act
        avaliacao = new Avaliacao(DESCRICAO, RATING, agendamento);

        // Assert
        assertNotNull(avaliacao);
        assertEquals(DESCRICAO, avaliacao.getDescricao());
        assertEquals(RATING, avaliacao.getRating());
        assertEquals(agendamento, avaliacao.getAgendamento());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        // Act
        avaliacao.setIdAvaliacao(ID_AVALIACAO);

        // Assert
        assertEquals(ID_AVALIACAO, avaliacao.getIdAvaliacao());
    }

    @Test
    @DisplayName("Deve definir e obter descrição")
    void deveDefinirEObterDescricao() {
        // Act
        avaliacao.setDescricao(DESCRICAO);

        // Assert
        assertEquals(DESCRICAO, avaliacao.getDescricao());
    }

    @Test
    @DisplayName("Deve definir e obter rating")
    void deveDefinirEObterRating() {
        // Act
        avaliacao.setRating(RATING);

        // Assert
        assertEquals(RATING, avaliacao.getRating());
    }

    @Test
    @DisplayName("Deve definir e obter agendamento")
    void deveDefinirEObterAgendamento() {
        // Act
        avaliacao.setAgendamento(agendamento);

        // Assert
        assertEquals(agendamento, avaliacao.getAgendamento());
    }

    @Test
    @DisplayName("Deve retornar true para avaliações com mesmo ID")
    void deveRetornarTrueParaAvaliacoesComMesmoId() {
        // Arrange
        Avaliacao avaliacao1 = new Avaliacao();
        avaliacao1.setIdAvaliacao(ID_AVALIACAO);
        
        Avaliacao avaliacao2 = new Avaliacao();
        avaliacao2.setIdAvaliacao(ID_AVALIACAO);

        // Act & Assert
        assertEquals(avaliacao1, avaliacao2);
        assertEquals(avaliacao1.hashCode(), avaliacao2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false para avaliações com IDs diferentes")
    void deveRetornarFalseParaAvaliacoesComIdsDiferentes() {
        // Arrange
        Avaliacao avaliacao1 = new Avaliacao();
        avaliacao1.setIdAvaliacao(1L);
        
        Avaliacao avaliacao2 = new Avaliacao();
        avaliacao2.setIdAvaliacao(2L);

        // Act & Assert
        assertNotEquals(avaliacao1, avaliacao2);
        assertNotEquals(avaliacao1.hashCode(), avaliacao2.hashCode());
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com null")
    void deveRetornarFalseAoCompararComNull() {
        // Assert
        assertFalse(avaliacao.equals(null));
    }

    @Test
    @DisplayName("Deve retornar false ao comparar com objeto de outra classe")
    void deveRetornarFalseAoCompararComObjetoDeOutraClasse() {
        // Assert
        assertFalse(avaliacao.equals(new Object()));
    }

    @Test
    @DisplayName("Deve retornar true ao comparar objeto com ele mesmo")
    void deveRetornarTrueAoCompararObjetoComEleMesmo() {
        // Assert
        assertTrue(avaliacao.equals(avaliacao));
    }

    @Test
    @DisplayName("Deve retornar false ao comparar avaliações sem ID")
    void deveRetornarFalseAoCompararAvaliacoesSemId() {
        // Arrange
        Avaliacao avaliacao1 = new Avaliacao();
        Avaliacao avaliacao2 = new Avaliacao();

        // Act & Assert
        assertNotEquals(avaliacao1, avaliacao2);
    }

    @Test
    @DisplayName("Deve retornar 0 para hashCode quando ID é null")
    void deveRetornarZeroParaHashCodeQuandoIdENull() {
        // Arrange
        Avaliacao avaliacaoSemId = new Avaliacao();

        // Act & Assert
        assertEquals(0, avaliacaoSemId.hashCode());
    }
} 