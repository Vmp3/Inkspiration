package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.EnderecoDTO;
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
    private final UsuarioService usuarioService;

    @Autowired
    public ProfissionalService(ProfissionalRepository profissionalRepository, 
                              UsuarioRepository usuarioRepository,
                              EnderecoRepository enderecoRepository,
                              PortifolioService portifolioService,
                              UsuarioService usuarioService) {
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.enderecoRepository = enderecoRepository;
        this.portifolioService = portifolioService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Profissional criar(ProfissionalDTO dto) {
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
        
        // Cria o profissional
        Profissional profissional = new Profissional();
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        
        profissional.setNota(dto.getNota());
        
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
        
        return profissionalRepository.save(profissional);
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
    
    // Métodos auxiliares para conversão e atualização de entidades
    
    private Endereco converterEnderecoDTO(EnderecoDTO dto) {
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
            profissional.getNota()
        );
    }
    
    private EnderecoDTO converterEnderecoParaDto(Endereco endereco) {
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

    // Método auxiliar para buscar endereço por ID
    private Endereco buscarEnderecoPorId(Long idEndereco) {
        return enderecoRepository.findById(idEndereco)
            .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com ID: " + idEndereco));
    }
} 