package inkspiration.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.repository.AgendamentoRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AgendamentoScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AgendamentoScheduler.class);
    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoScheduler(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Scheduled(fixedRate = 100000)
    @Transactional
    public void atualizarStatusAgendamentosPassados() {
        try {
            LocalDateTime agora = LocalDateTime.now();
            
            List<Agendamento> agendamentosParaAtualizar = agendamentoRepository
                .findByStatusAndDtFimBefore(StatusAgendamento.AGENDADO, agora);

            if (!agendamentosParaAtualizar.isEmpty()) {
                logger.info("Iniciando atualização de {} agendamentos passados", agendamentosParaAtualizar.size());
                
                for (Agendamento agendamento : agendamentosParaAtualizar) {
                    agendamento.setStatus(StatusAgendamento.CONCLUIDO);
                    agendamentoRepository.save(agendamento);
                    logger.debug("Agendamento {} atualizado para CONCLUIDO", agendamento.getIdAgendamento());
                }

                logger.info("Atualização de agendamentos concluída com sucesso");
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar status dos agendamentos: {}", e.getMessage(), e);
        }
    }
} 