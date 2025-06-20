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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.EnderecoDTO;
import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.EnderecoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PortifolioService portifolioService;
    private final DisponibilidadeService disponibilidadeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ProfissionalService(ProfissionalRepository profissionalRepository, 
                              UsuarioRepository usuarioRepository,
                              EnderecoRepository enderecoRepository,
                              PortifolioService portifolioService,
                              UsuarioService usuarioService,
                              DisponibilidadeService disponibilidadeService) {
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.portifolioService = portifolioService;
        this.disponibilidadeService = disponibilidadeService;
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
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + dto.getIdEndereco()));
        
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
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + dto.getIdEndereco()));
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
     * Se o profissional já existir, será atualizado em vez de criado.
     * 
     * @param dto O DTO contendo todas as informações para criar o profissional e suas entidades relacionadas
     * @return O profissional criado ou atualizado
     * @throws JsonProcessingException Caso ocorra um erro no processamento do JSON de disponibilidade
     */
    @Transactional
    public Profissional criarProfissionalCompleto(ProfissionalCriacaoDTO dto) throws JsonProcessingException {
        // 1. Verificar se já existe um profissional para este usuário
        Profissional profissional;
        boolean isUpdate = false;
        
        try {
            profissional = buscarPorUsuario(dto.getIdUsuario());
            isUpdate = true;
        } catch (ResourceNotFoundException e) {
            // Profissional não existe, criar um novo
            ProfissionalDTO profissionalDTO = new ProfissionalDTO();
            profissionalDTO.setIdUsuario(dto.getIdUsuario());
            profissionalDTO.setIdEndereco(dto.getIdEndereco());
            profissionalDTO.setNota(new BigDecimal("0.0")); // Nota inicial sempre zero
            // Define os tipos de serviço via processamento
            
            profissional = criar(profissionalDTO);
            
            // Processar preços dos serviços na criação usando o método do service
            if (dto.getPrecosServicos() != null && !dto.getPrecosServicos().isEmpty()) {
                processarTiposServicoComPrecos(profissional, dto.getTiposServico(), dto.getPrecosServicos());
                // Salvar preços no JSON antes de persistir
                salvarTiposServicoPrecos(profissional);
                profissional = profissionalRepository.save(profissional);
            }
        }
        
        if (isUpdate) {
            // Processar tipos de serviço com preços usando o método do service
            processarTiposServicoComPrecos(profissional, dto.getTiposServico(), dto.getPrecosServicos());
            
            // Salvar preços no JSON antes de persistir
            salvarTiposServicoPrecos(profissional);
            
            profissional = profissionalRepository.save(profissional);
            
            profissionalRepository.flush();
            profissional = profissionalRepository.findById(profissional.getIdProfissional()).orElseThrow();
        }
        
        // 2. Criar ou atualizar o portifólio
        PortifolioDTO portifolioDTO = new PortifolioDTO();
        portifolioDTO.setIdProfissional(profissional.getIdProfissional());
        portifolioDTO.setDescricao(dto.getDescricao());
        portifolioDTO.setEspecialidade(dto.getEspecialidade());
        portifolioDTO.setExperiencia(dto.getExperiencia());
        portifolioDTO.setWebsite(dto.getWebsite());
        portifolioDTO.setTiktok(dto.getTiktok());
        portifolioDTO.setInstagram(dto.getInstagram());
        portifolioDTO.setFacebook(dto.getFacebook());
        portifolioDTO.setTwitter(dto.getTwitter());
        
        if (isUpdate && profissional.getPortifolio() != null) {
            portifolioDTO.setIdPortifolio(profissional.getPortifolio().getIdPortifolio());
            portifolioService.atualizar(profissional.getPortifolio().getIdPortifolio(), portifolioDTO);
        } else {
            portifolioService.criar(portifolioDTO);
        }
        
        if (dto.getDisponibilidades() != null && !dto.getDisponibilidades().isEmpty()) {
            Map<String, List<Map<String, String>>> horarios = new HashMap<>();
            
            dto.getDisponibilidades().forEach(dispDTO -> {
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
            
            disponibilidadeService.cadastrarDisponibilidade(profissional.getIdProfissional(), horarios);
        }
        
        return profissional;
    }

    public Profissional buscarPorId(Long id) {
        Profissional profissional = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));
        
        // Carregar preços do JSON para o Map transiente
        carregarTiposServicoPrecos(profissional);
        
        return profissional;
    }
    
    public Profissional buscarPorUsuario(Long idUsuario) {
        Profissional profissional = profissionalRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
        
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
                    if (profissional.getPortifolio() != null && profissional.getPortifolio().getEspecialidade() != null) {
                        especialidades = profissional.getPortifolio().getEspecialidade().toLowerCase();
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
    
    
    public Endereco converterEnderecoDTO(EnderecoDTO dto) {
        Endereco endereco = new Endereco();
        atualizarEndereco(endereco, dto);
        return endereco;
    }
    
    private void atualizarEndereco(Endereco endereco, EnderecoDTO dto) {
        endereco.setCep(dto.getCep());
        endereco.setRua(dto.getRua());
        endereco.setBairro(dto.getBairro());
        endereco.setComplemento(dto.getComplemento());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setLatitude(dto.getLatitude());
        endereco.setLongitude(dto.getLongitude());
        endereco.setNumero(dto.getNumero());
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
    
    public EnderecoDTO converterEnderecoParaDto(Endereco endereco) {
        return new EnderecoDTO(
            endereco.getIdEndereco(),
            endereco.getCep(),
            endereco.getRua(),
            endereco.getBairro(),
            endereco.getComplemento(),
            endereco.getCidade(),
            endereco.getEstado(),
            endereco.getLatitude(),
            endereco.getLongitude(),
            endereco.getNumero()
        );
    }

        public Endereco buscarEnderecoPorId(Long idEndereco) {
        return enderecoRepository.findById(idEndereco)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + idEndereco));
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
} 