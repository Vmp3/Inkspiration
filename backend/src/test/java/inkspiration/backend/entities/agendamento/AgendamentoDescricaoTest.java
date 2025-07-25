package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;

@DisplayName("Testes de validação de descrição - Agendamento")
public class AgendamentoDescricaoTest {

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }

    @Test
    @DisplayName("Deve aceitar descrição válida")
    void deveAceitarDescricaoValida() {
        String descricao = "Tatuagem tribal costas";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição válida com 500 caracteres")
    void deveAceitarDescricaoValidaCom500Caracteres() {
        String descricao = "D".repeat(500);
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição complexa")
    void deveAceitarDescricaoComplexa() {
        String descricao = "Tatuagem em estilo realista de um leão na parte superior do braço esquerdo, com sombreamento detalhado e dimensões aproximadas de 15x20cm.";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição com caracteres especiais")
    void deveAceitarDescricaoComCaracteresEspeciais() {
        String descricao = "Piercing: brinco argola 8mm + limpeza pós-procedimento (R$ 150,00) com acompanhamento";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição com acentos")
    void deveAceitarDescricaoComAcentos() {
        String descricao = "Sessão de remoção a laser para tatuagem localizada no tornozelo direito com anestesia";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas da descrição")
    void deveRemoverEspacosDosBoardasDaDescricao() {
        String descricaoComEspacos = "  Tatuagem tribal nas costas com sombreamento em preto  ";
        String descricaoEsperada = "Tatuagem tribal nas costas com sombreamento em preto";
        agendamento.setDescricao(descricaoComEspacos);
        assertEquals(descricaoEsperada, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Não deve aceitar descrição nula")
    void naoDeveAceitarDescricaoNula() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDescricao(null);
        });
        assertEquals("Descrição não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar descrição vazia")
    void naoDeveAceitarDescricaoVazia() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDescricao("");
        });
        assertEquals("Descrição não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar descrição com apenas espaços")
    void naoDeveAceitarDescricaoComApenasEspacos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDescricao("   ");
        });
        assertEquals("Descrição não pode ser nula ou vazia", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar descrição com mais de 500 caracteres")
    void naoDeveAceitarDescricaoComMaisDe500Caracteres() {
        String descricao = "D".repeat(501);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDescricao(descricao);
        });
        assertEquals("A descrição não pode exceder 500 caracteres", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar descrição com quebras de linha")
    void deveAceitarDescricaoComQuebrasLinha() {
        String descricao = "Sessão de tatuagem completa:\n- Desenho personalizado\n- Local: braço direito\n- Duração: 3h";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição com números")
    void deveAceitarDescricaoComNumeros() {
        String descricao = "Retoque da tatuagem feita em 2023, sessão 2 de 3 previstas para finalização";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar descrição com emojis")
    void deveAceitarDescricaoComEmojis() {
        String descricao = "Tatuagem de borboleta 🦋 nas costas, estilo delicado e colorido com sombreamento 🎨";
        agendamento.setDescricao(descricao);
        assertEquals(descricao, agendamento.getDescricao());
    }
} 