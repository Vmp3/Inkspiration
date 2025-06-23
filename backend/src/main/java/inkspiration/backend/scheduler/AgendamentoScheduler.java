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
        logger.info("AgendamentoScheduler inicializado - Verificação automática a cada 1 minuto");
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 1 minuto (60000 ms)
    @Transactional
    public void atualizarStatusAgendamentosPassados() {
        try {
            LocalDateTime agora = LocalDateTime.now();
            logger.debug("Executando verificação automática de agendamentos às {}", agora);
            
            // Busca agendamentos com status AGENDADO onde dtFim já passou
            List<Agendamento> agendamentosParaAtualizar = agendamentoRepository
                .findByStatusAndDtFimBefore(StatusAgendamento.AGENDADO, agora);

            logger.debug("Encontrados {} agendamentos para verificação", agendamentosParaAtualizar.size());

            if (!agendamentosParaAtualizar.isEmpty()) {
                logger.info("Iniciando atualização automática de {} agendamentos passados às {}", 
                    agendamentosParaAtualizar.size(), agora);
                
                int atualizados = 0;
                // Atualiza cada agendamento diretamente no banco
                for (Agendamento agendamento : agendamentosParaAtualizar) {
                    StatusAgendamento statusAnterior = agendamento.getStatus();
                    agendamento.setStatus(StatusAgendamento.CONCLUIDO);
                    agendamentoRepository.save(agendamento);
                    atualizados++;
                    
                    logger.info("Agendamento ID {} atualizado: {} -> CONCLUIDO (Data/Hora fim: {})", 
                        agendamento.getIdAgendamento(), statusAnterior, agendamento.getDtFim());
                }

                logger.info("Atualização automática concluída com sucesso - {} de {} agendamentos atualizados", 
                    atualizados, agendamentosParaAtualizar.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao atualizar automaticamente o status dos agendamentos: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }
} 