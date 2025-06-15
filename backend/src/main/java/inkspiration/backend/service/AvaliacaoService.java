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
    
    // Métodos de busca
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

    // Operações CRUD básicas
    @Transactional
    public Avaliacao criarAvaliacao(Long idAgendamento, String descricao, Integer rating) {
        Agendamento agendamento = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        
        if (avaliacaoRepository.existsByAgendamento(agendamento)) {
            throw new RuntimeException("Este agendamento já foi avaliado");
        }
        
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("A avaliação deve ser entre 1 e 5 estrelas");
        }
        
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAgendamento(agendamento);
        avaliacao.setDescricao(descricao);
        avaliacao.setRating(rating);
        
        return avaliacaoRepository.save(avaliacao);
    }
    
    @Transactional
    public Avaliacao atualizarAvaliacao(Long id, String descricao, Integer rating) {
        Avaliacao avaliacao = buscarPorId(id);
        
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("A avaliação deve ser entre 1 e 5 estrelas");
        }
        
        avaliacao.setDescricao(descricao);
        avaliacao.setRating(rating);
        
        return avaliacaoRepository.save(avaliacao);
    }
    
    @Transactional
    public void excluirAvaliacao(Long id) {
        Avaliacao avaliacao = buscarPorId(id);
        avaliacaoRepository.delete(avaliacao);
    }
}