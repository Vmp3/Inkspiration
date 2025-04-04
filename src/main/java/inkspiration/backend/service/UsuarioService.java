package inkspiration.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioAutenticarRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UsuarioAutenticarRepository autenticarRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final TokenRevogadoRepository tokenRevogadoRepository;

    @Autowired
    public UsuarioService(UsuarioRepository repository, 
                         UsuarioAutenticarRepository autenticarRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService,
                         HttpServletRequest request,
                         TokenRevogadoRepository tokenRevogadoRepository) {
        this.repository = repository;
        this.autenticarRepository = autenticarRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.request = request;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
    }

    @Transactional
    public Usuario criar(UsuarioDTO dto) {
        validarCamposObrigatorios(dto);
        
        if (repository.existsByEmail(dto.getEmail())) {
            throw new UsuarioException.EmailJaExisteException("Email já cadastrado");
        }
        
        if (repository.existsByCpf(dto.getCpf())) {
            throw new UsuarioException.CpfJaExisteException("CPF já cadastrado");
        }
    
        Usuario usuario = new Usuario();
        preencherUsuario(usuario, dto);
        
        usuario.setRole(determinarRole(dto.getRole()));
    
        // Cria o objeto UsuarioAutenticar
        UsuarioAutenticar usuarioAuth = new UsuarioAutenticar();
        usuarioAuth.setCpf(dto.getCpf());
        usuarioAuth.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioAuth.setRole(usuario.getRole());
        
        // Configura o endereço se fornecido
        if (dto.getEndereco() != null) {
            usuario.setEndereco(dto.getEndereco());
        }
        
        // Associa o usuarioAutenticar ao usuário
        usuario.setUsuarioAutenticar(usuarioAuth);
        
        // Salva o usuário com suas associações
        usuario = repository.save(usuario);
        
        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public Page<Usuario> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<UsuarioResponseDTO> listarTodosResponse(Pageable pageable) {
        Page<Usuario> usuarios = repository.findAll(pageable);
        return usuarios.getContent().stream()
                .map(usuario -> new UsuarioResponseDTO(
                    usuario.getIdUsuario(), 
                    usuario.getNome(), 
                    usuario.getCpf(),
                    usuario.getEmail(), 
                    usuario.getDataNascimento() != null ? usuario.getDataNascimento().toString() : null,
                    usuario.getTelefone(),
                    usuario.getImagemPerfil(),
                    usuario.getEndereco(),
                    usuario.getRole()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioDTO dto) {
        validarCamposObrigatorios(dto);
        Usuario usuarioExistente = buscarPorId(id);
        
        boolean precisaRevogarToken = false;
        
        // Verifica se está tentando alterar a role
        if (dto.getRole() != null && !dto.getRole().equals(usuarioExistente.getRole())) {
            // Obtém o usuário autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin) {
                throw new UsuarioException.PermissaoNegadaException("Apenas administradores podem alterar roles");
            }
            precisaRevogarToken = true;
        }
        
        // Verifica se o email está sendo alterado
        if (!usuarioExistente.getEmail().equals(dto.getEmail())) {
            if (repository.existsByEmail(dto.getEmail())) {
                throw new UsuarioException.EmailJaExisteException("Email já cadastrado");
            }
            precisaRevogarToken = true;
        }
        
        // Verifica se o CPF está sendo alterado
        if (!usuarioExistente.getCpf().equals(dto.getCpf())) {
            if (repository.existsByCpf(dto.getCpf())) {
                throw new UsuarioException.CpfJaExisteException("CPF já cadastrado");
            }
            precisaRevogarToken = true;
        }

        preencherUsuario(usuarioExistente, dto);
        
        // Se for admin, permite alterar a role
        if (dto.getRole() != null) {
            usuarioExistente.setRole(determinarRole(dto.getRole()));
        }
        
        // Atualiza o endereço se fornecido
        if (dto.getEndereco() != null) {
            if (usuarioExistente.getEndereco() == null) {
                usuarioExistente.setEndereco(new Endereco());
            }
            atualizarEndereco(usuarioExistente.getEndereco(), dto.getEndereco());
        }
        
        // Atualiza o usuarioAutenticar
        if (usuarioExistente.getUsuarioAutenticar() == null) {
            UsuarioAutenticar usuarioAuth = new UsuarioAutenticar();
            usuarioAuth.setCpf(dto.getCpf());
            usuarioAuth.setRole(usuarioExistente.getRole());
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
                usuarioAuth.setSenha(passwordEncoder.encode(dto.getSenha()));
            }
            usuarioExistente.setUsuarioAutenticar(usuarioAuth);
        } else {
            UsuarioAutenticar usuarioAuth = usuarioExistente.getUsuarioAutenticar();
            usuarioAuth.setCpf(dto.getCpf());
            usuarioAuth.setRole(usuarioExistente.getRole());
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
                usuarioAuth.setSenha(passwordEncoder.encode(dto.getSenha()));
            }
        }
        
        usuarioExistente = repository.save(usuarioExistente);
        
        // Revoga o token atual e gera um novo
        if (precisaRevogarToken) {
            String novoToken = jwtService.generateToken(SecurityContextHolder.getContext().getAuthentication());
            atualizarToken(usuarioExistente, novoToken);
        }
        
        return usuarioExistente;
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setRole("ROLE_DELETED");

        // Revoga o token atual
        if (usuario.getTokenAtual() != null) {
            TokenRevogado tokenRevogado = new TokenRevogado(usuario.getTokenAtual());
            tokenRevogadoRepository.save(tokenRevogado);
            usuario.setTokenAtual(null);
        }

        if (usuario.getUsuarioAutenticar() != null) {
            usuario.getUsuarioAutenticar().setRole("ROLE_DELETED");
        }
        
        repository.save(usuario);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        
        // Revoga o token atual
        if (usuario.getTokenAtual() != null) {
            TokenRevogado tokenRevogado = new TokenRevogado(usuario.getTokenAtual());
            tokenRevogadoRepository.save(tokenRevogado);
        }

        repository.delete(usuario);
    }

    private void validarCamposObrigatorios(UsuarioDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new UsuarioValidationException.NomeObrigatorioException();
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new UsuarioValidationException.EmailObrigatorioException();
        }
        if (dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
            throw new UsuarioValidationException.CpfObrigatorioException();
        }
        if (dto.getDataNascimento() == null) {
            throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
        }
        if (dto.getSenha() == null || dto.getSenha().trim().isEmpty()) {
            throw new UsuarioValidationException.SenhaObrigatoriaException();
        }
    }

    private void preencherUsuario(Usuario usuario, UsuarioDTO dto) {
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setEmail(dto.getEmail());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setTelefone(dto.getTelefone());
        usuario.setImagemPerfil(dto.getImagemPerfil());
    }

    private String determinarRole(String role) {
        if (role != null) {
            if (role.equalsIgnoreCase("admin")) {
                return "ROLE_ADMIN";
            } else if (role.equalsIgnoreCase("deleted")) {
                return "ROLE_DELETED";
            }
        }
        return "ROLE_USER";
    }

    private void atualizarEndereco(Endereco enderecoAtual, Endereco novoEndereco) {
        enderecoAtual.setCep(novoEndereco.getCep());
        enderecoAtual.setRua(novoEndereco.getRua());
        enderecoAtual.setBairro(novoEndereco.getBairro());
        enderecoAtual.setComplemento(novoEndereco.getComplemento());
        enderecoAtual.setCidade(novoEndereco.getCidade());
        enderecoAtual.setEstado(novoEndereco.getEstado());
        enderecoAtual.setLatitude(novoEndereco.getLatitude());
        enderecoAtual.setLongitude(novoEndereco.getLongitude());
        enderecoAtual.setNumero(novoEndereco.getNumero());
    }

    public boolean existsByRole(String role) {
        return repository.existsByRole(role);
    }

    private void atualizarToken(Usuario usuario, String novoToken) {
        String tokenAntigo = usuario.getTokenAtual();
        if (tokenAntigo != null) {
            TokenRevogado tokenRevogado = new TokenRevogado(tokenAntigo);
            tokenRevogadoRepository.save(tokenRevogado);
        }
        usuario.setTokenAtual(novoToken);
        repository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
    }
    
    public Usuario buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf)
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public void salvar(Usuario usuario) {
        repository.save(usuario);
    }
}