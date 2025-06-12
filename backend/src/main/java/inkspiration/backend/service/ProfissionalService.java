package inkspiration.backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.EnderecoDTO;
import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
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
        
        profissional.setTiposServico(dto.getTiposServico());
        
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
        
        if (dto.getTiposServico() != null && !dto.getTiposServico().isEmpty()) {
            profissional.setTiposServico(dto.getTiposServico());
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
            profissionalDTO.setTiposServico(dto.getTiposServico()); // Define os tipos de serviço
            
            profissional = criar(profissionalDTO);
        }
        
        if (isUpdate && dto.getTiposServico() != null && !dto.getTiposServico().isEmpty()) {
            profissional.setTiposServico(dto.getTiposServico());
            profissionalRepository.save(profissional);
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
        
        if (dto.getEstilosTatuagem() != null && !dto.getEstilosTatuagem().isEmpty()) {
            // TODO: Adicionar estilos de tatuagem ao portifólio
        }
        
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
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + id));
    }
    
    public Profissional buscarPorUsuario(Long idUsuario) {
        return profissionalRepository.findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
    }
    
    public boolean existePerfil(Long idUsuario) {
        return profissionalRepository.existsByUsuario_IdUsuario(idUsuario);
    }

    public Page<Profissional> listar(Pageable pageable) {
        return profissionalRepository.findAll(pageable);
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
} 