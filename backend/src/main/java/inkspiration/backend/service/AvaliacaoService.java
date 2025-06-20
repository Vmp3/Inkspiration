package inkspiration.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.AvaliacaoDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Avaliacao;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.AvaliacaoRepository;
import inkspiration.backend.repository.ProfissionalRepository;

@Service
public class AvaliacaoService {
    
    private final AvaliacaoRepository avaliacaoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    
    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            AgendamentoRepository agendamentoRepository,
            ProfissionalRepository profissionalRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.profissionalRepository = profissionalRepository;
    }
    
    @Transactional
    public Avaliacao criarAvaliacao(Long idAgendamento, String descricao, Integer rating) {
        // Validar se o agendamento existe
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        
        // Validar se já existe uma avaliação para este agendamento
        if (avaliacaoRepository.existsByAgendamento(agendamento)) {
            throw new RuntimeException("Este agendamento já foi avaliado");
        }
        
        // Validar rating (1-5)
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("A avaliação deve ser entre 1 e 5 estrelas");
        }
        
        // Criar a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAgendamento(agendamento);
        avaliacao.setDescricao(descricao);
        avaliacao.setRating(rating);
        
        // Salvar a avaliação
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        
        // Atualizar a nota média do profissional
        atualizarNotaProfissional(agendamento.getProfissional().getIdProfissional());
        
        return avaliacaoSalva;
    }
    
    public Avaliacao buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
    }
    
    public List<Avaliacao> listarPorUsuario(Long idUsuario) {
        return avaliacaoRepository.findByAgendamentoUsuarioIdUsuario(idUsuario);
    }
    
    public Page<Avaliacao> listarPorUsuario(Long idUsuario, Pageable pageable) {
        return avaliacaoRepository.findByAgendamentoUsuarioIdUsuario(idUsuario, pageable);
    }
    
    public List<Avaliacao> listarPorProfissional(Long idProfissional) {
        return avaliacaoRepository.findByAgendamentoProfissionalIdProfissional(idProfissional);
    }
    
    public Page<Avaliacao> listarPorProfissional(Long idProfissional, Pageable pageable) {
        return avaliacaoRepository.findByAgendamentoProfissionalIdProfissional(idProfissional, pageable);
    }
    
    @Transactional
    public Avaliacao atualizarAvaliacao(Long id, String descricao, Integer rating) {
        Avaliacao avaliacao = buscarPorId(id);
        
        // Validar rating (1-5)
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("A avaliação deve ser entre 1 e 5 estrelas");
        }
        
        // Atualizar dados
        avaliacao.setDescricao(descricao);
        avaliacao.setRating(rating);
        
        // Salvar alterações
        Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);
        
        // Atualizar a nota média do profissional
        atualizarNotaProfissional(avaliacao.getAgendamento().getProfissional().getIdProfissional());
        
        return avaliacaoAtualizada;
    }
    
    @Transactional
    public void excluirAvaliacao(Long id) {
        Avaliacao avaliacao = buscarPorId(id);
        Long idProfissional = avaliacao.getAgendamento().getProfissional().getIdProfissional();
        
        avaliacaoRepository.delete(avaliacao);
        
        // Atualizar a nota média do profissional
        atualizarNotaProfissional(idProfissional);
    }
    
    @Transactional
    public void atualizarNotaProfissional(Long idProfissional) {
        // Obter a média das avaliações para o profissional
        Double mediaAvaliacoes = avaliacaoRepository.calculateAverageRatingByProfissional(idProfissional);
        
        // Se não houver avaliações, definir como zero
        if (mediaAvaliacoes == null) {
            mediaAvaliacoes = 0.0;
        }
        
        // Arredondar para uma casa decimal
        BigDecimal notaMedia = BigDecimal.valueOf(mediaAvaliacoes).setScale(1, BigDecimal.ROUND_HALF_UP);
        
        // Atualizar a nota do profissional
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        profissional.setNota(notaMedia);
        profissionalRepository.save(profissional);
    }
    
    // Métodos que retornam DTOs
    public AvaliacaoDTO buscarPorIdDTO(Long id) {
        Avaliacao avaliacao = buscarPorId(id);
        return new AvaliacaoDTO(avaliacao);
    }
    
    public List<AvaliacaoDTO> listarPorUsuarioDTO(Long idUsuario) {
        return listarPorUsuario(idUsuario).stream()
                .map(AvaliacaoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AvaliacaoDTO> listarPorUsuarioDTO(Long idUsuario, Pageable pageable) {
        Page<Avaliacao> avaliacoesPage = listarPorUsuario(idUsuario, pageable);
        List<AvaliacaoDTO> avaliacoesDTO = avaliacoesPage.getContent().stream()
                .map(AvaliacaoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(avaliacoesDTO, pageable, avaliacoesPage.getTotalElements());
    }
    
    public List<AvaliacaoDTO> listarPorProfissionalDTO(Long idProfissional) {
        return listarPorProfissional(idProfissional).stream()
                .map(AvaliacaoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AvaliacaoDTO> listarPorProfissionalDTO(Long idProfissional, Pageable pageable) {
        Page<Avaliacao> avaliacoesPage = listarPorProfissional(idProfissional, pageable);
        List<AvaliacaoDTO> avaliacoesDTO = avaliacoesPage.getContent().stream()
                .map(AvaliacaoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(avaliacoesDTO, pageable, avaliacoesPage.getTotalElements());
    }
    
    public Avaliacao buscarPorAgendamento(Long idAgendamento) {
        return avaliacaoRepository.findByAgendamento_IdAgendamento(idAgendamento).orElse(null);
    }
    
    public java.util.Map<String, Object> obterEstatisticasProfissional(Long idProfissional) {
        // Verificar se o profissional existe
        if (!profissionalRepository.existsById(idProfissional)) {
            throw new RuntimeException("Profissional não encontrado");
        }
        
        // Obter estatísticas
        Long totalAvaliacoes = avaliacaoRepository.countByProfissionalId(idProfissional);
        Double mediaAvaliacoes = avaliacaoRepository.calculateAverageRatingByProfissional(idProfissional);
        Long avaliacoesComComentario = avaliacaoRepository.countByProfissionalIdAndDescricaoNotNull(idProfissional);
        
        // Se não houver avaliações, definir média como zero
        if (mediaAvaliacoes == null) {
            mediaAvaliacoes = 0.0;
        }
        
        var stats = new java.util.HashMap<String, Object>();
        stats.put("totalAvaliacoes", totalAvaliacoes);
        stats.put("mediaAvaliacoes", mediaAvaliacoes);
        stats.put("avaliacoesComComentario", avaliacoesComComentario);
        stats.put("avaliacoesSemComentario", totalAvaliacoes - avaliacoesComComentario);
        
        return stats;
    }
} 