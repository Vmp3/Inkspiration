package inkspiration.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;

@Service
public class AgendamentoService {
    
    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeService disponibilidadeService;
    
    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            ProfissionalRepository profissionalRepository,
            UsuarioRepository usuarioRepository,
            DisponibilidadeService disponibilidadeService) {
        this.agendamentoRepository = agendamentoRepository;
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadeService = disponibilidadeService;
    }
    
    private LocalDateTime ajustarHorarioInicio(LocalDateTime dtInicio) {
        return dtInicio.withMinute(0).withSecond(0).withNano(0);
    }
    
    private LocalDateTime calcularHorarioFim(LocalDateTime dtInicioAjustado, TipoServico tipoServico) {
        return dtInicioAjustado
                .plusHours(tipoServico.getDuracaoHoras())
                .minusSeconds(1) 
                .withNano(0);
    }
    
    @Transactional
    public Agendamento criarAgendamento(Long idUsuario, Long idProfissional, String tipoServicoStr, 
            String descricao, LocalDateTime dtInicio) throws Exception {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        if (usuario.getIdUsuario().equals(profissional.getUsuario().getIdUsuario())) {
            throw new RuntimeException("Não é possível agendar consigo mesmo");
        }
        
        LocalDateTime agora = LocalDateTime.now();
        if (dtInicio.isBefore(agora)) {
            throw new RuntimeException("Não é possível agendar para datas e horários que já passaram");
        }
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de serviço inválido. Opções válidas: " +
                    "pequena, media, grande, sessao");
        }
        
        LocalDateTime dtInicioAjustado = ajustarHorarioInicio(dtInicio);
        
        LocalDateTime dtFim = calcularHorarioFim(dtInicioAjustado, tipoServico);
        
        try {
            boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (!estaNoHorarioDeTrabalho) {
                throw new RuntimeException("O profissional não está trabalhando nesse horário. " +
                        "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                        " (" + tipoServico.getDuracaoHoras() + " horas)");
            }
            
            boolean existeConflito = agendamentoRepository.existsConflitingSchedule(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (existeConflito) {
                throw new RuntimeException("O profissional já possui outro agendamento nesse horário. " +
                        "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                        " (" + tipoServico.getDuracaoHoras() + " horas)");
            }
            
            Agendamento agendamento = new Agendamento();
            agendamento.setUsuario(usuario);
            agendamento.setProfissional(profissional);
            agendamento.setTipoServico(tipoServico);
            agendamento.setDescricao(descricao);
            agendamento.setDtInicio(dtInicioAjustado);
            agendamento.setDtFim(dtFim);
            
            return agendamentoRepository.save(agendamento);
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar disponibilidade do profissional", e);
        }
    }
    
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }
    
    public List<Agendamento> listarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return agendamentoRepository.findByUsuario(usuario);
    }
    
    public Page<Agendamento> listarPorUsuario(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return agendamentoRepository.findByUsuario(usuario, pageable);
    }
    
    public List<Agendamento> listarPorProfissional(Long idProfissional) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        return agendamentoRepository.findByProfissional(profissional);
    }
    
    public Page<Agendamento> listarPorProfissional(Long idProfissional, Pageable pageable) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        return agendamentoRepository.findByProfissional(profissional, pageable);
    }
    
    public List<Agendamento> listarPorProfissionalEPeriodo(Long idProfissional, 
            LocalDateTime inicio, LocalDateTime fim) {
        
        return agendamentoRepository.findByProfissionalAndPeriod(idProfissional, inicio, fim);
    }
    
    @Transactional
    public Agendamento atualizarAgendamento(Long id, String tipoServicoStr, String descricao,
            LocalDateTime dtInicio) throws Exception {
        
        Agendamento agendamento = buscarPorId(id);
        
        // Validar se a data não é passada
        LocalDateTime agora = LocalDateTime.now();
        if (dtInicio.isBefore(agora)) {
            throw new RuntimeException("Não é possível agendar para datas e horários que já passaram");
        }
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de serviço inválido. Opções válidas: " +
                    "pequena, media, grande, sessao");
        }
        
        LocalDateTime dtInicioAjustado = ajustarHorarioInicio(dtInicio);
        
        LocalDateTime dtFim = calcularHorarioFim(dtInicioAjustado, tipoServico);
        
        if (!agendamento.getDtInicio().equals(dtInicioAjustado) || !agendamento.getDtFim().equals(dtFim)) {
            try {
                boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                        agendamento.getProfissional().getIdProfissional(), dtInicioAjustado, dtFim);
                
                if (!estaNoHorarioDeTrabalho) {
                    throw new RuntimeException("O profissional não está trabalhando nesse horário. " +
                            "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                            " (" + tipoServico.getDuracaoHoras() + " horas)");
                }
                
                List<Agendamento> agendamentosConflitantes = agendamentoRepository
                        .findByProfissionalAndPeriod(
                                agendamento.getProfissional().getIdProfissional(), 
                                dtInicioAjustado.minusHours(1), 
                                dtFim.plusHours(1))
                        .stream()
                        .filter(a -> !a.getIdAgendamento().equals(id)) 
                        .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicioAjustado)))
                        .collect(Collectors.toList());
                
                if (!agendamentosConflitantes.isEmpty()) {
                    throw new RuntimeException("O profissional já possui outro agendamento nesse horário. " +
                            "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                            " (" + tipoServico.getDuracaoHoras() + " horas)");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao processar disponibilidade do profissional", e);
            }
        }
        
        agendamento.setTipoServico(tipoServico);
        agendamento.setDescricao(descricao);
        agendamento.setDtInicio(dtInicioAjustado);
        agendamento.setDtFim(dtFim);
        
        return agendamentoRepository.save(agendamento);
    }
    
    @Transactional
    public void excluirAgendamento(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamentoRepository.delete(agendamento);
    }
    
    public AgendamentoDTO buscarPorIdDTO(Long id) {
        Agendamento agendamento = buscarPorId(id);
        return new AgendamentoDTO(agendamento);
    }
    
    public List<AgendamentoDTO> listarPorUsuarioDTO(Long idUsuario) {
        return listarPorUsuario(idUsuario).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AgendamentoDTO> listarPorUsuarioDTO(Long idUsuario, Pageable pageable) {
        Page<Agendamento> agendamentosPage = listarPorUsuario(idUsuario, pageable);
        List<AgendamentoDTO> agendamentosDTO = agendamentosPage.getContent().stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }
    
    public List<AgendamentoDTO> listarPorProfissionalDTO(Long idProfissional) {
        return listarPorProfissional(idProfissional).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AgendamentoDTO> listarPorProfissionalDTO(Long idProfissional, Pageable pageable) {
        Page<Agendamento> agendamentosPage = listarPorProfissional(idProfissional, pageable);
        List<AgendamentoDTO> agendamentosDTO = agendamentosPage.getContent().stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }
    
    public List<AgendamentoDTO> listarPorProfissionalEPeriodoDTO(Long idProfissional, 
            LocalDateTime inicio, LocalDateTime fim) {
        return listarPorProfissionalEPeriodo(idProfissional, inicio, fim).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
} 