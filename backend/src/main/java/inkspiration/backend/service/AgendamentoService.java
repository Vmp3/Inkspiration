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
    
    @Transactional
    public Agendamento criarAgendamento(Long idUsuario, Long idProfissional, String tipoServicoStr, 
            String descricao, LocalDateTime dtInicio, LocalDateTime dtFim) throws Exception {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de serviço inválido. Opções válidas: " +
                    "tatuagem pequena, tatuagem media, tatuagem grande, sessão");
        }
        
        try {
            boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                    idProfissional, dtInicio, dtFim);
            
            if (!estaNoHorarioDeTrabalho) {
                throw new RuntimeException("O profissional não está trabalhando nesse horário");
            }
            
            boolean existeConflito = agendamentoRepository.existsConflitingSchedule(
                    idProfissional, dtInicio, dtFim);
            
            if (existeConflito) {
                throw new RuntimeException("O profissional já possui outro agendamento nesse horário");
            }
            
            Agendamento agendamento = new Agendamento();
            agendamento.setUsuario(usuario);
            agendamento.setProfissional(profissional);
            agendamento.setTipoServico(tipoServico);
            agendamento.setDescricao(descricao);
            agendamento.setDtInicio(dtInicio);
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
            LocalDateTime dtInicio, LocalDateTime dtFim) throws Exception {
        
        Agendamento agendamento = buscarPorId(id);
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de serviço inválido. Opções válidas: " +
                    "tatuagem pequena, tatuagem media, tatuagem grande, sessão");
        }
        
        if (!agendamento.getDtInicio().equals(dtInicio) || !agendamento.getDtFim().equals(dtFim)) {
            try {
                boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                        agendamento.getProfissional().getIdProfissional(), dtInicio, dtFim);
                
                if (!estaNoHorarioDeTrabalho) {
                    throw new RuntimeException("O profissional não está trabalhando nesse horário");
                }
                
                List<Agendamento> agendamentosConflitantes = agendamentoRepository
                        .findByProfissionalAndPeriod(
                                agendamento.getProfissional().getIdProfissional(), 
                                dtInicio.minusHours(1), 
                                dtFim.plusHours(1))
                        .stream()
                        .filter(a -> !a.getIdAgendamento().equals(id)) 
                        .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicio)))
                        .collect(Collectors.toList());
                
                if (!agendamentosConflitantes.isEmpty()) {
                    throw new RuntimeException("O profissional já possui outro agendamento nesse horário");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao processar disponibilidade do profissional", e);
            }
        }
        
        agendamento.setTipoServico(tipoServico);
        agendamento.setDescricao(descricao);
        agendamento.setDtInicio(dtInicio);
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