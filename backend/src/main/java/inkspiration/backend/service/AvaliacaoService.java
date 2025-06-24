package inkspiration.backend.service;

import inkspiration.backend.dto.AvaliacaoDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Avaliacao;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.exception.agendamento.AgendamentoNaoEncontradoException;
import inkspiration.backend.exception.avaliacao.AvaliacaoJaExisteException;
import inkspiration.backend.exception.avaliacao.AvaliacaoNaoEncontradaException;
import inkspiration.backend.exception.avaliacao.AvaliacaoNaoPermitidaException;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.AvaliacaoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ProfissionalRepository profissionalRepository;

    @Autowired
    private AuthorizationService authorizationService;

    @Transactional
    public AvaliacaoDTO criarAvaliacao(AvaliacaoDTO avaliacaoDTO) {
        // Buscar o agendamento
        Agendamento agendamento = agendamentoRepository.findById(avaliacaoDTO.getIdAgendamento())
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        // Verificar se o usuário é o cliente do agendamento
        Usuario usuarioAtual = authorizationService.getCurrentUser();
        if (!agendamento.getUsuario().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {
            throw new AvaliacaoNaoPermitidaException("Você não tem permissão para avaliar este agendamento");
        }

        // Verificar se o agendamento está concluído
        if (!StatusAgendamento.CONCLUIDO.equals(agendamento.getStatus())) {
            throw new AvaliacaoNaoPermitidaException("Só é possível avaliar agendamentos concluídos");
        }

        // Verificar se já existe uma avaliação para este agendamento
        if (avaliacaoRepository.existsByAgendamentoId(agendamento.getIdAgendamento())) {
            throw new AvaliacaoJaExisteException("Já existe uma avaliação para este agendamento");
        }

        // Criar a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setDescricao(avaliacaoDTO.getDescricao());
        avaliacao.setRating(avaliacaoDTO.getRating());
        avaliacao.setAgendamento(agendamento);

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        // Atualizar a nota média do profissional
        atualizarNotaProfissional(agendamento.getProfissional().getIdProfissional());

        return convertToDTO(avaliacaoSalva);
    }

    /**
     * Atualiza a nota média do profissional baseado em todas as suas avaliações
     */
    private void atualizarNotaProfissional(Long idProfissional) {
        BigDecimal mediaAvaliacoes = avaliacaoRepository.calcularMediaAvaliacoesPorProfissional(idProfissional);
        
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado para atualização da nota"));
        
        profissional.setNota(mediaAvaliacoes);
        profissionalRepository.save(profissional);
    }

    @Transactional(readOnly = true)
    public Optional<AvaliacaoDTO> buscarAvaliacaoPorAgendamento(Long idAgendamento) {
        // Buscar o agendamento
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        // Verificar se o usuário tem permissão para ver a avaliação
        Usuario usuarioAtual = authorizationService.getCurrentUser();
        if (!agendamento.getUsuario().getIdUsuario().equals(usuarioAtual.getIdUsuario()) &&
            !agendamento.getProfissional().getUsuario().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {
            throw new AvaliacaoNaoPermitidaException("Você não tem permissão para ver esta avaliação");
        }

        return avaliacaoRepository.findByAgendamentoId(idAgendamento)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public boolean podeAvaliar(Long idAgendamento) {
        // Buscar o agendamento
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        // Verificar se o usuário é o cliente do agendamento
        Usuario usuarioAtual = authorizationService.getCurrentUser();
        if (!agendamento.getUsuario().getIdUsuario().equals(usuarioAtual.getIdUsuario())) {
            return false;
        }

        // Verificar se o agendamento está concluído
        if (!StatusAgendamento.CONCLUIDO.equals(agendamento.getStatus())) {
            return false;
        }

        // Verificar se já existe uma avaliação
        return !avaliacaoRepository.existsByAgendamentoId(idAgendamento);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoDTO> buscarAvaliacoesPorProfissional(Long idProfissional, Pageable pageable) {
        Page<Avaliacao> avaliacoes = avaliacaoRepository.findByProfissionalId(idProfissional, pageable);
        return avaliacoes.map(this::convertToPublicDTO);
    }

    @Transactional(readOnly = true)
    public Long contarAvaliacoesPorProfissional(Long idProfissional) {
        return avaliacaoRepository.countByProfissionalId(idProfissional);
    }

    private AvaliacaoDTO convertToDTO(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
                avaliacao.getIdAvaliacao(),
                avaliacao.getDescricao(),
                avaliacao.getRating(),
                avaliacao.getAgendamento().getIdAgendamento()
        );
    }
    
    private AvaliacaoDTO convertToPublicDTO(Avaliacao avaliacao) {
        AvaliacaoDTO dto = new AvaliacaoDTO(
                avaliacao.getIdAvaliacao(),
                avaliacao.getDescricao(),
                avaliacao.getRating(),
                avaliacao.getAgendamento().getIdAgendamento()
        );
        
        // Adicionar informações do cliente que fez a avaliação (apenas nome)
        if (avaliacao.getAgendamento() != null && avaliacao.getAgendamento().getUsuario() != null) {
            dto.setNomeCliente(avaliacao.getAgendamento().getUsuario().getNome());
            dto.setImagemCliente(avaliacao.getAgendamento().getUsuario().getImagemPerfil());
        }
        
        // Adicionar tipo de serviço
        if (avaliacao.getAgendamento() != null) {
            dto.setTipoServico(avaliacao.getAgendamento().getTipoServico().name());
        }
        
        return dto;
    }
} 