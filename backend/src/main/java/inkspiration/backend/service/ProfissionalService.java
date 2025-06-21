package inkspiration.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.profissional.DadosCompletosProfissionalException;
import inkspiration.backend.exception.profissional.EnderecoNaoEncontradoException;
import inkspiration.backend.exception.profissional.DisponibilidadeProcessamentoException;
import inkspiration.backend.exception.profissional.ProfissionalAcessoNegadoException;
import inkspiration.backend.exception.profissional.ProfissionalJaExisteException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.exception.profissional.TipoServicoInvalidoProfissionalException;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.service.ImagemService;
import java.util.Arrays;
import java.util.Collections;
import inkspiration.backend.repository.EnderecoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.service.EnderecoService;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PortfolioService portfolioService;
    private final DisponibilidadeService disponibilidadeService;
    private final AuthorizationService authorizationService;
    private final ImagemService imagemService;
    private final EnderecoService enderecoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ProfissionalService(ProfissionalRepository profissionalRepository, 
                              UsuarioRepository usuarioRepository,
                              EnderecoRepository enderecoRepository,
                              PortfolioService portfolioService,
                              UsuarioService usuarioService,
                              DisponibilidadeService disponibilidadeService,
                              AuthorizationService authorizationService,
                              ImagemService imagemService,
                              EnderecoService enderecoService) {
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.portfolioService = portfolioService;
        this.disponibilidadeService = disponibilidadeService;
        this.authorizationService = authorizationService;
        this.imagemService = imagemService;
        this.enderecoService = enderecoService;
    }

    /**
     * Carrega os preços dos serviços do JSON armazenado no banco para o Map transiente da entidade
     */
    private void carregarTiposServicoPrecos(Profissional profissional) {
        Map<String, BigDecimal> tiposServicoPrecos = new HashMap<>();
        if (profissional.getTiposServicoStr() != null && !profissional.getTiposServicoStr().isEmpty()) {
            try {
                TypeReference<Map<String, BigDecimal>> typeRef = new TypeReference<Map<String, BigDecimal>>() {};
                tiposServicoPrecos = objectMapper.readValue(profissional.getTiposServicoStr(), typeRef);
                System.out.println("LOG Service: Carregando tipos de serviço com preços: " + tiposServicoPrecos);
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao carregar tipos de serviço com preços: " + e.getMessage());
                tiposServicoPrecos = new HashMap<>();
            }
        }
        profissional.setTiposServicoPrecos(tiposServicoPrecos);
    }

    /**
     * Salva os preços dos serviços do Map transiente para o JSON no banco
     */
    private void salvarTiposServicoPrecos(Profissional profissional) {
        if (profissional.getTiposServicoPrecos() != null && !profissional.getTiposServicoPrecos().isEmpty()) {
            try {
                String json = objectMapper.writeValueAsString(profissional.getTiposServicoPrecos());
                profissional.setTiposServicoStr(json);
                System.out.println("LOG Service: Salvando tipos de serviço com preços: " + profissional.getTiposServicoPrecos());
            } catch (JsonProcessingException e) {
                System.err.println("Erro ao salvar tipos de serviço com preços: " + e.getMessage());
                profissional.setTiposServicoStr("");
            }
        } else {
            profissional.setTiposServicoStr("");
        }
    }

    @Transactional
    private Profissional criar(ProfissionalDTO dto) {
        // Verifica se o usuário existe
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
        
        // Verifica se já existe um profissional para este usuário
        if (profissionalRepository.existsByUsuario(usuario)) {
            throw new IllegalStateException("Já existe um perfil profissional para este usuário");
        }
        
        // Verifica se o endereço existe
        Endereco endereco = enderecoRepository.findById(dto.getIdEndereco())
            .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado com ID: " + dto.getIdEndereco()));
        
        // Validar endereço usando ViaCEP
        enderecoService.validarEndereco(endereco);
        
        // Atualiza o papel (role) do usuário para ROLE_PROF
        usuario.setRole("ROLE_PROF");
        if (usuario.getUsuarioAutenticar() != null) {
            usuario.getUsuarioAutenticar().setRole("ROLE_PROF");
        }
        usuarioRepository.save(usuario);
        
        // Cria o profissional
        Profissional profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        
        // Define nota inicial como 0 para novos profissionais
        if (dto.getNota() == null) {
            profissional.setNota(new BigDecimal("0.0"));
        } else {
            profissional.setNota(dto.getNota());
        }
        
        // Processar tipos de serviço com preços no service - primeira criação sem preços ainda
        processarTiposServicoComPrecos(profissional, dto.getTiposServico(), null);
        
        // Salvar preços no JSON antes de persistir
        salvarTiposServicoPrecos(profissional);
        
        return profissionalRepository.save(profissional);
    }

    @Transactional
    public Profissional atualizar(Long id, ProfissionalDTO dto) {
        Profissional profissional = buscarPorId(id);
        
        // Atualiza o endereço se o ID for fornecido
        if (dto.getIdEndereco() != null) {
            Endereco endereco = enderecoRepository.findById(dto.getIdEndereco())
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado com ID: " + dto.getIdEndereco()));
            
            // Validar endereço usando ViaCEP
            enderecoService.validarEndereco(endereco);
            
            profissional.setEndereco(endereco);
        }
        
        // Atualiza a nota se fornecida
        if (dto.getNota() != null) {
            profissional.setNota(dto.getNota());
        }
        
        if (dto.getTiposServico() != null) {
            // Criar Map com preços padrão (se não fornecidos)
            Map<String, BigDecimal> tiposComPrecos = new HashMap<>();
            for (TipoServico tipo : dto.getTiposServico()) {
                tiposComPrecos.put(tipo.name(), BigDecimal.ZERO);
            }
                    profissional.setTiposServicoPrecos(tiposComPrecos);
        
        // Salvar no JSON para persistência
        salvarTiposServicoPrecos(profissional);
        }
        
        return profissionalRepository.save(profissional);
    }
    
    /**
     * Cria um profissional completo, com portifólio e disponibilidade em uma única transação.
     * Se qualquer parte do processo falhar, toda a transação é revertida.
     * Este método é exclusivo para CRIAÇÃO de novos profissionais.
     * 
     * @param dto O DTO contendo todas as informações para criar o profissional e suas entidades relacionadas
     * @return O profissional criado
     * @throws JsonProcessingException Caso ocorra um erro no processamento do JSON de disponibilidade
     * @throws ProfissionalJaExisteException Caso já exista um profissional para este usuário
     */
    @Transactional
    public Profissional criarProfissionalCompleto(ProfissionalCriacaoDTO dto) throws JsonProcessingException {
        // Verificar se já existe um profissional para este usuário
        if (profissionalRepository.existsByUsuario_IdUsuario(dto.getIdUsuario())) {
            throw new ProfissionalJaExisteException("Já existe um profissional cadastrado para este usuário");
        }
        
        // 1. Criar o profissional
        ProfissionalDTO profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setIdUsuario(dto.getIdUsuario());
        profissionalDTO.setIdEndereco(dto.getIdEndereco());
        profissionalDTO.setNota(new BigDecimal("0.0")); // Nota inicial sempre zero
        
        Profissional profissional = criar(profissionalDTO);
        
        // Processar preços dos serviços na criação
        if (dto.getPrecosServicos() != null && !dto.getPrecosServicos().isEmpty()) {
            processarTiposServicoComPrecos(profissional, dto.getTiposServico(), dto.getPrecosServicos());
            salvarTiposServicoPrecos(profissional);
            profissional = profissionalRepository.save(profissional);
        }
        
        // 2. Criar o portifólio
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setIdProfissional(profissional.getIdProfissional());
        portfolioDTO.setDescricao(dto.getDescricao());
        portfolioDTO.setEspecialidade(dto.getEspecialidade());
        portfolioDTO.setExperiencia(dto.getExperiencia());
        portfolioDTO.setWebsite(dto.getWebsite());
        portfolioDTO.setTiktok(dto.getTiktok());
        portfolioDTO.setInstagram(dto.getInstagram());
        portfolioDTO.setFacebook(dto.getFacebook());
        portfolioDTO.setTwitter(dto.getTwitter());
        
        portfolioService.criar(portfolioDTO);
        
        // Recarregar o profissional para obter a referência atualizada do portfólio
        profissional = profissionalRepository.findById(profissional.getIdProfissional()).orElseThrow();
        
        // 3. Criar disponibilidades se fornecidas
        if (dto.getDisponibilidades() != null && !dto.getDisponibilidades().isEmpty()) {
            criarDisponibilidades(profissional.getIdProfissional(), dto.getDisponibilidades());
        }
        
        return profissional;
    }

    /**
     * Atualiza um profissional completo, incluindo portifólio e disponibilidade em uma única transação.
     * Se qualquer parte do processo falhar, toda a transação é revertida.
     * Este método é exclusivo para ATUALIZAÇÃO de profissionais existentes.
     * 
     * @param dto O DTO contendo todas as informações para atualizar o profissional e suas entidades relacionadas
     * @return O profissional atualizado
     * @throws JsonProcessingException Caso ocorra um erro no processamento do JSON de disponibilidade
     * @throws ProfissionalNaoEncontradoException Caso o profissional não exista
     */
    @Transactional
    public Profissional atualizarProfissionalCompleto(ProfissionalCriacaoDTO dto) throws JsonProcessingException {
        // 1. Buscar o profissional existente
        Profissional profissional = buscarPorUsuario(dto.getIdUsuario());
        
        // 2. Atualizar tipos de serviço com preços
        if (dto.getPrecosServicos() != null && !dto.getPrecosServicos().isEmpty()) {
            processarTiposServicoComPrecos(profissional, dto.getTiposServico(), dto.getPrecosServicos());
            salvarTiposServicoPrecos(profissional);
        }
        
        // Atualizar endereço se fornecido
        if (dto.getIdEndereco() != null) {
            profissional.getUsuario().getEndereco().setIdEndereco(dto.getIdEndereco());
        }
        
        profissional = profissionalRepository.save(profissional);
        profissionalRepository.flush();
        profissional = profissionalRepository.findById(profissional.getIdProfissional()).orElseThrow();
        
        // 3. Atualizar o portifólio
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setIdProfissional(profissional.getIdProfissional());
        portfolioDTO.setDescricao(dto.getDescricao());
        portfolioDTO.setEspecialidade(dto.getEspecialidade());
        portfolioDTO.setExperiencia(dto.getExperiencia());
        portfolioDTO.setWebsite(dto.getWebsite());
        portfolioDTO.setTiktok(dto.getTiktok());
        portfolioDTO.setInstagram(dto.getInstagram());
        portfolioDTO.setFacebook(dto.getFacebook());
        portfolioDTO.setTwitter(dto.getTwitter());
        
        if (profissional.getPortfolio() != null) {
            portfolioDTO.setIdPortfolio(profissional.getPortfolio().getIdPortfolio());
            portfolioService.atualizar(profissional.getPortfolio().getIdPortfolio(), portfolioDTO);
        } else {
            portfolioService.criar(portfolioDTO);
        }
        
        // 4. Atualizar disponibilidades se fornecidas
        if (dto.getDisponibilidades() != null && !dto.getDisponibilidades().isEmpty()) {
            criarDisponibilidades(profissional.getIdProfissional(), dto.getDisponibilidades());
        }
        
        return profissional;
    }

    /**
     * Método auxiliar para criar disponibilidades a partir de uma lista de DTOs
     */
    private void criarDisponibilidades(Long idProfissional, List<DisponibilidadeDTO> disponibilidades) throws JsonProcessingException {
        Map<String, List<Map<String, String>>> horarios = new HashMap<>();
        
        disponibilidades.forEach(dispDTO -> {
            String[] partes = dispDTO.getHrAtendimento().split("-");
            if (partes.length == 3) {
                String diaSemana = partes[0];
                String inicio = partes[1];
                String fim = partes[2];
                
                Map<String, String> periodo = new HashMap<>();
                periodo.put("inicio", inicio);
                periodo.put("fim", fim);
                
                horarios.computeIfAbsent(diaSemana, k -> new ArrayList<>()).add(periodo);
            }
        });
        
        disponibilidadeService.cadastrarDisponibilidade(idProfissional, horarios);
    }

    public Profissional buscarPorId(Long id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ProfissionalNaoEncontradoException("Profissional não encontrado com ID: " + id));
        
        // Carregar preços do JSON para o Map transiente
        carregarTiposServicoPrecos(profissional);
        
        return profissional;
    }
    
    public Profissional buscarPorUsuario(Long idUsuario) {
        Profissional profissional = profissionalRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new ProfissionalNaoEncontradoException("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
        
        // Carregar preços do JSON para o Map transiente
        carregarTiposServicoPrecos(profissional);
        
        return profissional;
    }
    
    public boolean existePerfil(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
        return profissionalRepository.existsByUsuario(usuario);
    }

    public Page<Profissional> listar(Pageable pageable) {
        return profissionalRepository.findAll(pageable);
    }

    public Page<Profissional> listarComFiltros(Pageable pageable, String searchTerm, String locationTerm, 
                                             double minRating, String[] selectedSpecialties, String sortBy) {
        // Buscar TODOS os profissionais primeiro (sem paginação)
        List<Profissional> allProfissionais = profissionalRepository.findAll();
        
        // Carregar preços para todos os profissionais
        allProfissionais.forEach(this::carregarTiposServicoPrecos);
        
        // Aplicar filtros manualmente
        List<Profissional> filteredProfissionais = allProfissionais.stream()
            .filter(profissional -> {
                // Filtro por nome (searchTerm)
                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    String nome = profissional.getUsuario() != null ? profissional.getUsuario().getNome() : "";
                    if (!nome.toLowerCase().contains(searchTerm.toLowerCase())) {
                        return false;
                    }
                }
                
                // Filtro por localização (locationTerm)
                if (locationTerm != null && !locationTerm.trim().isEmpty()) {
                    String location = "";
                    if (profissional.getEndereco() != null) {
                        location = profissional.getEndereco().getCidade() + ", " + profissional.getEndereco().getEstado();
                    }
                    if (!location.toLowerCase().contains(locationTerm.toLowerCase())) {
                        return false;
                    }
                }
                
                // Filtro por avaliação mínima
                if (minRating > 0) {
                    double rating = profissional.getNota() != null ? profissional.getNota().doubleValue() : 0.0;
                    if (rating < minRating) {
                        return false;
                    }
                }
                
                // Filtro por especialidades
                if (selectedSpecialties != null && selectedSpecialties.length > 0) {
                    String especialidades = "";
                    if (profissional.getPortfolio() != null && profissional.getPortfolio().getEspecialidade() != null) {
                        especialidades = profissional.getPortfolio().getEspecialidade().toLowerCase();
                    }
                    
                    boolean hasSpecialty = false;
                    for (String specialty : selectedSpecialties) {
                        if (especialidades.contains(specialty.toLowerCase())) {
                            hasSpecialty = true;
                            break;
                        }
                    }
                    if (!hasSpecialty) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        // Aplicar ordenação
        if ("melhorAvaliacao".equals(sortBy)) {
            filteredProfissionais.sort((a, b) -> {
                double ratingA = a.getNota() != null ? a.getNota().doubleValue() : 0.0;
                double ratingB = b.getNota() != null ? b.getNota().doubleValue() : 0.0;
                return Double.compare(ratingB, ratingA);
            });
        } else if ("maisRecente".equals(sortBy)) {
            filteredProfissionais.sort((a, b) -> 
                Long.compare(b.getIdProfissional(), a.getIdProfissional()));
        } else if ("maisAntigo".equals(sortBy)) {
            filteredProfissionais.sort((a, b) -> 
                Long.compare(a.getIdProfissional(), b.getIdProfissional()));
        } else {
            // Ordenação por relevância (padrão)
            filteredProfissionais.sort((a, b) -> {
                double ratingA = a.getNota() != null ? a.getNota().doubleValue() : 0.0;
                double ratingB = b.getNota() != null ? b.getNota().doubleValue() : 0.0;
                int ratingComparison = Double.compare(ratingB, ratingA);
                if (ratingComparison != 0) {
                    return ratingComparison;
                }
                // Em caso de empate, ordenar por nome
                String nameA = a.getUsuario() != null ? a.getUsuario().getNome() : "";
                String nameB = b.getUsuario() != null ? b.getUsuario().getNome() : "";
                return nameA.compareToIgnoreCase(nameB);
            });
        }
        
        // Implementar paginação manual nos resultados filtrados
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredProfissionais.size());
        
        // Verificar se o índice start está dentro dos limites
        if (start >= filteredProfissionais.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, filteredProfissionais.size());
        }
        
        List<Profissional> pagedProfissionais = filteredProfissionais.subList(start, end);
        
        return new PageImpl<>(pagedProfissionais, pageable, filteredProfissionais.size());
    }

    @Transactional
    public void deletar(Long id) {
        Profissional profissional = buscarPorId(id);
        profissionalRepository.delete(profissional);
    }
    
    public ProfissionalDTO converterParaDto(Profissional profissional) {
        if (profissional == null) return null;
        
        Long idEndereco = null;
        if (profissional.getEndereco() != null) {
            idEndereco = profissional.getEndereco().getIdEndereco();
        }
        
        return new ProfissionalDTO(
            profissional.getIdProfissional(),
            profissional.getUsuario().getIdUsuario(),
            idEndereco,
            profissional.getNota(),
            profissional.getTiposServico()
        );
    }

    public Endereco buscarEnderecoPorId(Long idEndereco) {
        return enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço não encontrado com ID: " + idEndereco));
    }
    
    /**
     * Processa tipos de serviço com preços - toda lógica fica no service
     */
    private void processarTiposServicoComPrecos(Profissional profissional, List<TipoServico> tiposServico, Map<String, BigDecimal> precosServicos) {
        System.out.println("LOG Service: Processando tipos de serviço: " + tiposServico);
        System.out.println("LOG Service: Processando preços: " + precosServicos);
        
        Map<String, BigDecimal> tiposComPrecos = new HashMap<>();
        
        if (tiposServico != null && !tiposServico.isEmpty()) {
            for (TipoServico tipo : tiposServico) {
                BigDecimal preco = BigDecimal.ZERO;
                
                // Se há preços informados, usa eles
                if (precosServicos != null && precosServicos.containsKey(tipo.name())) {
                    preco = precosServicos.get(tipo.name());
                }
                
                tiposComPrecos.put(tipo.name(), preco);
                System.out.println("LOG Service: Tipo " + tipo.name() + " com preço " + preco);
            }
        }
        
        profissional.setTiposServicoPrecos(tiposComPrecos);
    }

    // Novos métodos movidos do controller
    public List<ProfissionalDTO> listarComAutorizacao(Pageable pageable) {
        authorizationService.requireAdmin();
        
        Page<Profissional> profissionais = listar(pageable);
        
        return profissionais.getContent().stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public List<ProfissionalDTO> listarPublico(Pageable pageable) {
        Page<Profissional> profissionais = listar(pageable);
        
        return profissionais.getContent().stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public Page<Map<String, Object>> listarCompletoComFiltros(Pageable pageable, String searchTerm, String locationTerm,
                                                        double minRating, String[] selectedSpecialties, String sortBy) {
        Page<Profissional> profissionais = listarComFiltros(pageable, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy);
        
        List<Map<String, Object>> profissionaisCompletos = profissionais.getContent().stream()
                .map(this::montarProfissionalCompleto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(profissionaisCompletos, pageable, profissionais.getTotalElements());
    }

    public Map<String, Object> buscarCompletoComValidacao(Long id) {
        Profissional profissional = buscarPorId(id);
        return montarProfissionalCompleto(profissional);
    }

    public ProfissionalDTO buscarPorUsuarioComAutorizacao(Long idUsuario) {
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        Profissional profissional = buscarPorUsuario(idUsuario);
        return converterParaDto(profissional);
    }

    public class ProfissionalCompletoData {
        private ProfissionalDTO profissional;
        private PortfolioDTO portfolio;
        private List<ImagemDTO> imagens;
        private Map<String, List<Map<String, String>>> disponibilidades;
        private List<TipoServico> tiposServico;
        private Map<String, BigDecimal> precosServicos;
        private Map<String, BigDecimal> tiposServicoPrecos;
        
        // Getters e setters
        public ProfissionalDTO getProfissional() { return profissional; }
        public void setProfissional(ProfissionalDTO profissional) { this.profissional = profissional; }
        
        public PortfolioDTO getPortfolio() { return portfolio; }
        public void setPortfolio(PortfolioDTO portfolio) { this.portfolio = portfolio; }
        
        public List<ImagemDTO> getImagens() { return imagens; }
        public void setImagens(List<ImagemDTO> imagens) { this.imagens = imagens; }
        
        public Map<String, List<Map<String, String>>> getDisponibilidades() { return disponibilidades; }
        public void setDisponibilidades(Map<String, List<Map<String, String>>> disponibilidades) { this.disponibilidades = disponibilidades; }
        
        public List<TipoServico> getTiposServico() { return tiposServico; }
        public void setTiposServico(List<TipoServico> tiposServico) { this.tiposServico = tiposServico; }
        
        public Map<String, BigDecimal> getPrecosServicos() { return precosServicos; }
        public void setPrecosServicos(Map<String, BigDecimal> precosServicos) { this.precosServicos = precosServicos; }
        
        public Map<String, BigDecimal> getTiposServicoPrecos() { return tiposServicoPrecos; }
        public void setTiposServicoPrecos(Map<String, BigDecimal> tiposServicoPrecos) { this.tiposServicoPrecos = tiposServicoPrecos; }
    }

    public ProfissionalCompletoData buscarProfissionalCompletoComAutorizacao(Long idUsuario) {
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        Profissional profissional = buscarPorUsuario(idUsuario);
        ProfissionalDTO profissionalDto = converterParaDto(profissional);
        
        PortfolioDTO portfolioDto = null;
        if (profissional.getPortfolio() != null) {
            portfolioDto = portfolioService.converterParaDto(profissional.getPortfolio());
        }
        
        List<ImagemDTO> imagens = Collections.emptyList();
        if (profissional.getPortfolio() != null) {
            imagens = imagemService.listarPorPortfolio(profissional.getPortfolio().getIdPortfolio());
        }
        
        Map<String, List<Map<String, String>>> disponibilidades = Collections.emptyMap();
        try {
            disponibilidades = disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional());
        } catch (Exception e) {
            System.out.println("Nenhuma disponibilidade encontrada para o profissional: " + e.getMessage());
        }
        
        ProfissionalCompletoData data = new ProfissionalCompletoData();
        data.setProfissional(profissionalDto);
        data.setPortfolio(portfolioDto);
        data.setImagens(imagens);
        data.setDisponibilidades(disponibilidades);
        data.setTiposServico(profissional.getTiposServico());
        data.setPrecosServicos(profissional.getPrecosServicos());
        data.setTiposServicoPrecos(profissional.getTiposServicoPrecos());
        
        return data;
    }

    public Boolean verificarPerfilComAutorizacao(Long idUsuario) {
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        return existePerfil(idUsuario);
    }

    public List<Map<String, Object>> listarTiposServico() {
        return Arrays.stream(TipoServico.values())
            .map(tipo -> {
                Map<String, Object> tipoMap = new HashMap<>();
                tipoMap.put("nome", tipo.name());
                tipoMap.put("descricao", tipo.getDescricao());
                tipoMap.put("duracaoHoras", tipo.getDuracaoHoras());
                return tipoMap;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> listarTiposServicoPorProfissionalComValidacao(Long idProfissional) {
        Profissional profissional = buscarPorId(idProfissional);
        Map<String, BigDecimal> precosServicos = profissional.getPrecosServicos();
        
        return profissional.getTiposServico().stream()
            .map(tipo -> {
                Map<String, Object> tipoMap = new HashMap<>();
                tipoMap.put("tipo", tipo.name());
                tipoMap.put("duracaoHoras", tipo.getDuracaoHoras());
                tipoMap.put("exemplo", tipo.name().startsWith("TATUAGEM_") ? 
                    "Tatuagem " + tipo.name().replace("TATUAGEM_", "").toLowerCase() + " - " + tipo.getDuracaoHoras() + " horas" :
                    "Sessão completa - " + tipo.getDuracaoHoras() + " horas");
                
                BigDecimal preco = precosServicos.getOrDefault(tipo.name(), BigDecimal.ZERO);
                tipoMap.put("preco", preco);
                
                return tipoMap;
            })
            .collect(Collectors.toList());
    }

    public ProfissionalDTO criarProfissionalCompletoComValidacao(ProfissionalCriacaoDTO dto) {
        try {
            System.out.println("LOG: Recebendo DTO com tipos de serviço: " + dto.getTiposServico());
            System.out.println("LOG: Recebendo DTO com preços: " + dto.getPrecosServicos());
            
            Profissional profissional = criarProfissionalCompleto(dto);
            return converterParaDto(profissional);
        } catch (JsonProcessingException e) {
            throw new DisponibilidadeProcessamentoException("Erro ao processar disponibilidades: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ProfissionalDTO atualizarComAutorizacao(Long id, ProfissionalDTO dto) {
        Profissional profissionalExistente = buscarPorId(id);
        Long idUsuario = profissionalExistente.getUsuario().getIdUsuario();
        
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        Profissional profissionalAtualizado = atualizar(id, dto);
        return converterParaDto(profissionalAtualizado);
    }

    public ProfissionalDTO atualizarProfissionalCompletoComAutorizacao(Long idUsuario, ProfissionalCriacaoDTO dto) {
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        dto.setIdUsuario(idUsuario);
        
        try {
            Profissional profissionalAtualizado = atualizarProfissionalCompleto(dto);
            return converterParaDto(profissionalAtualizado);
        } catch (Exception e) {
            throw new DadosCompletosProfissionalException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> atualizarProfissionalCompletoComImagensComAutorizacao(Long idUsuario, Map<String, Object> requestData) {
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        try {
            Map<String, Object> portfolioData = (Map<String, Object>) requestData.get("portfolio");
            List<Map<String, Object>> imagensData = (List<Map<String, Object>>) requestData.get("imagens");
            Map<String, List<Map<String, String>>> disponibilidadesData = (Map<String, List<Map<String, String>>>) requestData.get("disponibilidades");
            List<String> tiposServicoStr = (List<String>) requestData.get("tiposServico");
            Map<String, Object> precosServicosData = (Map<String, Object>) requestData.get("precosServicos");
            
            System.out.println("LOG Controller: Recebendo preços na atualização: " + precosServicosData);
            
            Profissional profissional = buscarPorUsuario(idUsuario);
            
            ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
            dto.setIdUsuario(idUsuario);
            dto.setIdEndereco(profissional.getEndereco().getIdEndereco());
            
            if (tiposServicoStr != null) {
                try {
                    List<TipoServico> tiposServico = tiposServicoStr.stream()
                            .map(TipoServico::valueOf)
                            .collect(Collectors.toList());
                    dto.setTiposServico(tiposServico);
                } catch (IllegalArgumentException e) {
                    throw new TipoServicoInvalidoProfissionalException("Tipo de serviço inválido: " + e.getMessage());
                }
            }
            
            if (precosServicosData != null && !precosServicosData.isEmpty()) {
                Map<String, BigDecimal> precosFormatados = new HashMap<>();
                precosServicosData.forEach((tipo, preco) -> {
                    try {
                        BigDecimal precoDecimal;
                        if (preco instanceof Number) {
                            precoDecimal = BigDecimal.valueOf(((Number) preco).doubleValue());
                        } else if (preco instanceof String) {
                            precoDecimal = new BigDecimal(preco.toString());
                        } else {
                            precoDecimal = new BigDecimal(preco.toString());
                        }
                        precosFormatados.put(tipo, precoDecimal);
                        System.out.println("LOG Controller: Preço processado para " + tipo + ": " + precoDecimal);
                    } catch (Exception e) {
                        System.err.println("Erro ao processar preço para " + tipo + ": " + e.getMessage());
                    }
                });
                dto.setPrecosServicos(precosFormatados);
            }
            
            if (portfolioData != null) {
                dto.setDescricao((String) portfolioData.get("descricao"));
                dto.setEspecialidade((String) portfolioData.get("especialidade"));
                dto.setExperiencia((String) portfolioData.get("experiencia"));
                dto.setInstagram((String) portfolioData.get("instagram"));
                dto.setTiktok((String) portfolioData.get("tiktok"));
                dto.setFacebook((String) portfolioData.get("facebook"));
                dto.setTwitter((String) portfolioData.get("twitter"));
                dto.setWebsite((String) portfolioData.get("website"));
            }
            
            profissional = atualizarProfissionalCompleto(dto);
            
            if (imagensData != null && profissional.getPortfolio() != null) {
                Long portfolioId = profissional.getPortfolio().getIdPortfolio();
                
                List<ImagemDTO> imagensAtuais = imagemService.listarPorPortfolio(portfolioId);
                for (ImagemDTO imagem : imagensAtuais) {
                    imagemService.deletar(imagem.getIdImagem());
                }
                
                for (Map<String, Object> imagemData : imagensData) {
                    if (imagemData.containsKey("imagemBase64")) {
                        ImagemDTO imagemDto = new ImagemDTO();
                        imagemDto.setImagemBase64((String) imagemData.get("imagemBase64"));
                        imagemDto.setIdPortfolio(portfolioId);
                        imagemService.salvar(imagemDto);
                    }
                }
            }
            
            if (disponibilidadesData != null) {
                disponibilidadeService.cadastrarDisponibilidade(profissional.getIdProfissional(), disponibilidadesData);
            }
            
            profissional = buscarPorId(profissional.getIdProfissional());
            return montarProfissionalCompleto(profissional);
            
        } catch (Exception e) {
            throw new DadosCompletosProfissionalException("Erro ao atualizar dados completos: " + e.getMessage());
        }
    }

    public void deletarComAutorizacao(Long id) {
        Profissional profissional = buscarPorId(id);
        Long idUsuario = profissional.getUsuario().getIdUsuario();
        
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        deletar(id);
    }

    private Map<String, Object> montarProfissionalCompleto(Profissional profissional) {
        Map<String, Object> profissionalCompleto = new HashMap<>();
        
        ProfissionalDTO profissionalDto = converterParaDto(profissional);
        profissionalCompleto.put("profissional", profissionalDto);
        
        Map<String, Object> usuarioInfo = new HashMap<>();
        if (profissional.getUsuario() != null) {
            usuarioInfo.put("idUsuario", profissional.getUsuario().getIdUsuario());
            usuarioInfo.put("nome", profissional.getUsuario().getNome());
            usuarioInfo.put("email", profissional.getUsuario().getEmail());
            usuarioInfo.put("telefone", profissional.getUsuario().getTelefone());
            usuarioInfo.put("imagemPerfil", profissional.getUsuario().getImagemPerfil());
        }
        profissionalCompleto.put("usuario", usuarioInfo);
        
        profissionalCompleto.put("tiposServico", profissional.getTiposServico());
        profissionalCompleto.put("precosServicos", profissional.getPrecosServicos());
        profissionalCompleto.put("tiposServicoPrecos", profissional.getTiposServicoPrecos());
        
        Map<String, Object> enderecoInfo = new HashMap<>();
        if (profissional.getEndereco() != null) {
            enderecoInfo.put("idEndereco", profissional.getEndereco().getIdEndereco());
            enderecoInfo.put("cep", profissional.getEndereco().getCep());
            enderecoInfo.put("rua", profissional.getEndereco().getRua());
            enderecoInfo.put("bairro", profissional.getEndereco().getBairro());
            enderecoInfo.put("cidade", profissional.getEndereco().getCidade());
            enderecoInfo.put("estado", profissional.getEndereco().getEstado());
            enderecoInfo.put("numero", profissional.getEndereco().getNumero());
            enderecoInfo.put("complemento", profissional.getEndereco().getComplemento());
            enderecoInfo.put("latitude", profissional.getEndereco().getLatitude());
            enderecoInfo.put("longitude", profissional.getEndereco().getLongitude());
        }
        profissionalCompleto.put("endereco", enderecoInfo);
        
        PortfolioDTO portfolioDto = null;
        if (profissional.getPortfolio() != null) {
            portfolioDto = portfolioService.converterParaDto(profissional.getPortfolio());
        }
        profissionalCompleto.put("portfolio", portfolioDto);
        
        List<ImagemDTO> imagens = Collections.emptyList();
        if (profissional.getPortfolio() != null) {
            imagens = imagemService.listarPorPortfolio(profissional.getPortfolio().getIdPortfolio());
        }
        profissionalCompleto.put("imagens", imagens);
        
        Map<String, List<Map<String, String>>> disponibilidades = Collections.emptyMap();
        try {
            disponibilidades = disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional());
        } catch (Exception e) {
            System.out.println("Nenhuma disponibilidade encontrada para o profissional: " + e.getMessage());
        }
        profissionalCompleto.put("disponibilidades", disponibilidades);
        
        return profissionalCompleto;
    }

    private void validarCamposObrigatorios(ProfissionalDTO dto) {
        // ... existing code ...
    }
} 