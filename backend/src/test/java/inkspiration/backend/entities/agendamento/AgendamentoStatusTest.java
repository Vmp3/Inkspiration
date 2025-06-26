package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.StatusAgendamento;

@DisplayName("Testes de validação de status - Agendamento")
public class AgendamentoStatusTest {

    private Agendamento agendamento;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
    }

    @Test
    @DisplayName("Deve ter status padrão AGENDADO")
    void deveTerStatusPadraoAgendado() {
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar status AGENDADO")
    void deveAceitarStatusAgendado() {
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar status CONCLUIDO")
    void deveAceitarStatusConcluido2() {
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar status CANCELADO")
    void deveAceitarStatusCancelado2() {
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar status CONCLUIDO")
    void deveAceitarStatusConcluido() {
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar status CANCELADO")
    void deveAceitarStatusCancelado() {
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar todos os status válidos")
    void deveAceitarTodosStatusValidos() {
        for (StatusAgendamento status : StatusAgendamento.values()) {
            agendamento.setStatus(status);
            assertEquals(status, agendamento.getStatus());
        }
    }

    @Test
    @DisplayName("Deve aceitar mudança de status")
    void deveAceitarMudancaStatus() {
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
        
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
        
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Não deve aceitar status nulo")
    void naoDeveAceitarStatusNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setStatus(null);
        });
        assertEquals("Status não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve manter status após definir outros campos")
    void deveManterStatusAposDefinirOutrosCampos() {
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamento.setDescricao("Tatuagem tribal no braço direito");
        
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
        assertEquals("Tatuagem tribal no braço direito", agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar status de cancelamento")
    void deveAceitarStatusCancelamento() {
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve aceitar fluxo completo de status")
    void deveAceitarFluxoCompletoStatus() {
        // Fluxo normal: AGENDADO -> CONCLUIDO
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
        
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
        
        // Fluxo de cancelamento: AGENDADO -> CANCELADO
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
        
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
    }
} 