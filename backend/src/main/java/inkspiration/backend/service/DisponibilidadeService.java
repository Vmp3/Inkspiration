package inkspiration.backend.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.DisponibilidadeRepository;
import inkspiration.backend.repository.ProfissionalRepository;

/**
 * Serviço para gerenciar a disponibilidade de trabalho dos profissionais.
 * 
 * Este serviço armazena e verifica os horários regulares de trabalho dos profissionais,
 * ou seja, os períodos em que eles estão disponíveis para atendimento ao público.
 * 
 * A disponibilidade é armazenada como um JSON na forma:
 * {
 *   "Segunda": [
 *     {"inicio": "08:00", "fim": "12:00"},
 *     {"inicio": "13:00", "fim": "18:00"}
 *   ],
 *   "Terça": [
 *     {"inicio": "09:00", "fim": "19:00"}
 *   ],
 *   ...
 * }
 * 
 * Este serviço NÃO verifica conflitos com agendamentos existentes - isso é
 * responsabilidade do AgendamentoService.
 */
@Service
public class DisponibilidadeService {
    
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ObjectMapper objectMapper;
    
    public DisponibilidadeService(
            DisponibilidadeRepository disponibilidadeRepository,
            ProfissionalRepository profissionalRepository,
            AgendamentoRepository agendamentoRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.profissionalRepository = profissionalRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Cadastra ou atualiza a disponibilidade de um profissional.
     * 
     * @param idProfissional ID do profissional
     * @param horarios Mapa de dias da semana para lista de períodos com horários de início e fim
     * @return A entidade de disponibilidade salva
     * @throws JsonProcessingException Se houver erro ao converter o mapa para JSON
     */
    public Disponibilidade cadastrarDisponibilidade(Long idProfissional, Map<String, List<Map<String, String>>> horarios) 
            throws JsonProcessingException {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        // Converter Map para JSON
        String horarioJson = objectMapper.writeValueAsString(horarios);
        
        Disponibilidade disponibilidade = disponibilidadeRepository
                .findByProfissional(profissional)
                .orElse(new Disponibilidade());
                
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento(horarioJson);
        
        return disponibilidadeRepository.save(disponibilidade);
    }
    
    /**
     * Obtém a disponibilidade de um profissional como um mapa.
     * 
     * @param idProfissional ID do profissional
     * @return Mapa de dias da semana para lista de períodos com horários de início e fim
     * @throws JsonProcessingException Se houver erro ao converter o JSON para mapa
     */
    public Map<String, List<Map<String, String>>> obterDisponibilidade(Long idProfissional) 
            throws JsonProcessingException {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
                
        Disponibilidade disponibilidade = disponibilidadeRepository
                .findByProfissional(profissional)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não cadastrada"));
                
        // Converter JSON para Map usando TypeReference
        TypeReference<Map<String, List<Map<String, String>>>> typeRef = 
                new TypeReference<Map<String, List<Map<String, String>>>>() {};
        return objectMapper.readValue(disponibilidade.getHrAtendimento(), typeRef);
    }
    
    /**
     * Verifica se o profissional está trabalhando em determinado horário, 
     * com base nos horários cadastrados na tabela de Disponibilidade.
     * 
     * Esta verificação NÃO considera agendamentos existentes, apenas se o 
     * horário está dentro do período regular de trabalho do profissional.
     * 
     * @param idProfissional ID do profissional
     * @param dataHoraInicio Data e hora de início
     * @param dataHoraFim Data e hora de fim
     * @return true se o profissional estiver trabalhando no horário, false caso contrário
     * @throws JsonProcessingException Se houver erro ao processar a disponibilidade
     */
    public boolean isProfissionalDisponivel(Long idProfissional, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) 
            throws JsonProcessingException {
        // Validar que a data/hora de início é anterior à data/hora de fim
        if (dataHoraInicio.isAfter(dataHoraFim)) {
            return false;
        }
        
        // Extrair o dia da semana e horários
        DayOfWeek diaSemana = dataHoraInicio.getDayOfWeek();
        LocalTime horaInicio = dataHoraInicio.toLocalTime();
        LocalTime horaFim = dataHoraFim.toLocalTime();
        
        // Obter a disponibilidade do profissional
        Map<String, List<Map<String, String>>> disponibilidade = obterDisponibilidade(idProfissional);
        
        // Nome do dia em português
        String nomeDia = obterNomeDiaSemana(diaSemana);
        
        // Verificar se o profissional trabalha nesse dia
        if (!disponibilidade.containsKey(nomeDia)) {
            return false;
        }
        
        // Verificar se o horário está dentro do período de trabalho
        List<Map<String, String>> horariosDia = disponibilidade.get(nomeDia);
        for (Map<String, String> horario : horariosDia) {
            if (horario.containsKey("inicio") && horario.containsKey("fim")) {
                LocalTime inicioTrabalho = LocalTime.parse(horario.get("inicio"));
                LocalTime fimTrabalho = LocalTime.parse(horario.get("fim"));
                
                // Verifica se o horário de início e fim estão dentro do horário de trabalho
                boolean inicioValido = !horaInicio.isBefore(inicioTrabalho) && !horaInicio.isAfter(fimTrabalho);
                boolean fimValido = !horaFim.isBefore(inicioTrabalho) && !horaFim.isAfter(fimTrabalho);
                
                if (inicioValido && fimValido) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Converte o dia da semana do enum Java para o nome em português.
     */
    private String obterNomeDiaSemana(DayOfWeek diaSemana) {
        switch (diaSemana) {
            case MONDAY: return "Segunda";
            case TUESDAY: return "Terça";
            case WEDNESDAY: return "Quarta";
            case THURSDAY: return "Quinta";
            case FRIDAY: return "Sexta";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
    
    /**
     * Busca uma disponibilidade por ID e retorna como DTO.
     * 
     * @param id ID da disponibilidade
     * @return DTO com os dados da disponibilidade
     */
    public DisponibilidadeDTO buscarPorIdDTO(Long id) {
        Disponibilidade disponibilidade = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não encontrada"));
        
        return new DisponibilidadeDTO(disponibilidade);
    }
    
    /**
     * Busca a disponibilidade de um profissional e retorna como DTO.
     * 
     * @param idProfissional ID do profissional
     * @return DTO com os dados da disponibilidade
     */
    public DisponibilidadeDTO buscarPorProfissionalDTO(Long idProfissional) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
                
        Disponibilidade disponibilidade = disponibilidadeRepository
                .findByProfissional(profissional)
                .orElseThrow(() -> new RuntimeException("Disponibilidade não cadastrada"));
                
        return new DisponibilidadeDTO(disponibilidade);
    }
    
    /**
     * Cadastra ou atualiza a disponibilidade de um profissional e retorna como DTO.
     * 
     * @param idProfissional ID do profissional
     * @param horarios Mapa de dias da semana para lista de períodos com horários de início e fim
     * @return DTO com os dados da disponibilidade salva
     * @throws JsonProcessingException Se houver erro ao converter o mapa para JSON
     */
    public DisponibilidadeDTO cadastrarDisponibilidadeDTO(Long idProfissional, Map<String, List<Map<String, String>>> horarios) 
            throws JsonProcessingException {
        Disponibilidade disponibilidade = cadastrarDisponibilidade(idProfissional, horarios);
        return new DisponibilidadeDTO(disponibilidade);
    }
    
    /**
     * Obtém os horários disponíveis de um profissional em uma data específica,
     * considerando a disponibilidade cadastrada, agendamentos existentes e duração do serviço.
     * 
     * @param idProfissional ID do profissional
     * @param data Data para verificar disponibilidade
     * @param tipoServico Tipo de serviço que define a duração
     * @return Lista de horários disponíveis no formato "HH:mm"
     * @throws JsonProcessingException Se houver erro ao processar a disponibilidade
     */
    public List<String> obterHorariosDisponiveis(Long idProfissional, LocalDate data, TipoServico tipoServico) 
            throws JsonProcessingException {
        List<String> horariosDisponiveis = new ArrayList<>();
        
        Map<String, List<Map<String, String>>> disponibilidade = obterDisponibilidade(idProfissional);
        
        DayOfWeek diaSemana = data.getDayOfWeek();
        String nomeDia = obterNomeDiaSemana(diaSemana);
        
        if (!disponibilidade.containsKey(nomeDia)) {
            return horariosDisponiveis;
        }
        
        LocalDateTime inicioDia = data.atTime(0, 0);
        LocalDateTime fimDia = data.atTime(23, 59, 59);
        List<Agendamento> agendamentosExistentes = agendamentoRepository
                .findByProfissionalAndPeriod(idProfissional, inicioDia, fimDia);
        
        int duracaoHoras = tipoServico.getDuracaoHoras();
        
        List<Map<String, String>> horariosDia = disponibilidade.get(nomeDia);
        List<Map<String, String>> periodosConsolidados = new ArrayList<>();
        
        if (!horariosDia.isEmpty()) {
            Map<String, String> periodoAtual = new HashMap<>(horariosDia.get(0));
            
            for (int i = 1; i < horariosDia.size(); i++) {
                Map<String, String> proximoPeriodo = horariosDia.get(i);
                LocalTime fimAtual = LocalTime.parse(periodoAtual.get("fim"));
                LocalTime inicioProximo = LocalTime.parse(proximoPeriodo.get("inicio"));
                
                if (inicioProximo.minusMinutes(1).equals(fimAtual) || inicioProximo.equals(fimAtual)) {
                    periodoAtual.put("fim", proximoPeriodo.get("fim"));
                } else {
                    periodosConsolidados.add(new HashMap<>(periodoAtual));
                    periodoAtual = new HashMap<>(proximoPeriodo);
                }
            }
            periodosConsolidados.add(periodoAtual);
        }
        
        for (Map<String, String> periodo : periodosConsolidados) {
            if (periodo.containsKey("inicio") && periodo.containsKey("fim")) {
                LocalTime inicioTrabalho = LocalTime.parse(periodo.get("inicio"));
                LocalTime fimTrabalho = LocalTime.parse(periodo.get("fim"));
                
                LocalTime horarioLimite = fimTrabalho.minusHours(duracaoHoras);
                
                if (inicioTrabalho.isAfter(horarioLimite)) {
                    continue;
                }
                
                LocalTime horaAtual = inicioTrabalho;
                while (!horaAtual.isAfter(horarioLimite)) {
                    LocalDateTime inicioServico = data.atTime(horaAtual);
                    LocalDateTime fimServico = inicioServico.plusHours(duracaoHoras).minusSeconds(1);
                    
                    boolean temConflito = agendamentosExistentes.stream()
                            .anyMatch(agendamento -> 
                                agendamento.getDtInicio().isBefore(fimServico.plusSeconds(1)) && 
                                agendamento.getDtFim().isAfter(inicioServico.minusSeconds(1)));
                    
                    if (!temConflito) {
                        horariosDisponiveis.add(horaAtual.toString());
                    }
                    
                    horaAtual = horaAtual.plusHours(1);
                    
                    if (horaAtual.isAfter(fimTrabalho)) {
                        break;
                    }
                }
            }
        }
        
        if (data.isEqual(LocalDate.now())) {
            LocalTime horaAtual = LocalTime.now();
            horariosDisponiveis.removeIf(horario -> LocalTime.parse(horario).isBefore(horaAtual));
        }
        
        return horariosDisponiveis;
    }
} 