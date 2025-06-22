package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.TipoServico;

@DisplayName("Testes de validação de tipo de serviço - Agendamento")
public class AgendamentoTipoServicoTest {

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }

    @Test
    @DisplayName("Deve aceitar tipo de serviço TATUAGEM_PEQUENA")
    void deveAceitarTipoServicoTatuagemPequena() {
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        assertEquals(TipoServico.TATUAGEM_PEQUENA, agendamento.getTipoServico());
    }

    @Test
    @DisplayName("Deve aceitar tipo de serviço TATUAGEM_MEDIA")
    void deveAceitarTipoServicoTatuagemMedia() {
        agendamento.setTipoServico(TipoServico.TATUAGEM_MEDIA);
        assertEquals(TipoServico.TATUAGEM_MEDIA, agendamento.getTipoServico());
    }

    @Test
    @DisplayName("Deve aceitar tipo de serviço TATUAGEM_GRANDE")
    void deveAceitarTipoServicoTatuagemGrande() {
        agendamento.setTipoServico(TipoServico.TATUAGEM_GRANDE);
        assertEquals(TipoServico.TATUAGEM_GRANDE, agendamento.getTipoServico());
    }

    @Test
    @DisplayName("Deve aceitar tipo de serviço SESSAO")
    void deveAceitarTipoServicoSessao() {
        agendamento.setTipoServico(TipoServico.SESSAO);
        assertEquals(TipoServico.SESSAO, agendamento.getTipoServico());
    }

    @Test
    @DisplayName("Deve aceitar todos os tipos de serviço válidos")
    void deveAceitarTodosTiposServicoValidos() {
        for (TipoServico tipo : TipoServico.values()) {
            agendamento.setTipoServico(tipo);
            assertEquals(tipo, agendamento.getTipoServico());
        }
    }

    @Test
    @DisplayName("Deve aceitar redefinir tipo de serviço")
    void deveAceitarRedefinirTipoServico() {
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        assertEquals(TipoServico.TATUAGEM_PEQUENA, agendamento.getTipoServico());
        
        agendamento.setTipoServico(TipoServico.TATUAGEM_MEDIA);
        assertEquals(TipoServico.TATUAGEM_MEDIA, agendamento.getTipoServico());
    }

    @Test
    @DisplayName("Não deve aceitar tipo de serviço nulo")
    void naoDeveAceitarTipoServicoNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setTipoServico(null);
        });
        assertEquals("Tipo de serviço não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve manter tipo de serviço após definir outros campos")
    void deveManterTipoServicoAposDefinirOutrosCampos() {
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao("Tatuagem tribal no braço direito");
        
        assertEquals(TipoServico.TATUAGEM_PEQUENA, agendamento.getTipoServico());
        assertEquals("Tatuagem tribal no braço direito", agendamento.getDescricao());
    }
} 