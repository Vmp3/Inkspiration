package inkspiration.backend.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.util.CpfValidator;
import inkspiration.backend.util.DateValidator;
import inkspiration.backend.util.EmailValidator;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.exception.usuario.UserAccessDeniedException;
import inkspiration.backend.exception.usuario.TokenValidationException;
import inkspiration.backend.exception.usuario.InvalidProfileImageException;
import inkspiration.backend.dto.UsuarioSeguroDTO;
import inkspiration.backend.security.AuthorizationService;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import inkspiration.backend.util.PasswordValidator;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ProfissionalRepository profissionalRepository;
    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final AuthorizationService authorizationService;
    private final EnderecoService enderecoService;

    @Autowired
    public UsuarioService(UsuarioRepository repository, 
                         ProfissionalRepository profissionalRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService,
                         HttpServletRequest request,
                         TokenRevogadoRepository tokenRevogadoRepository,
                         AuthorizationService authorizationService,
                         EnderecoService enderecoService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.profissionalRepository = profissionalRepository;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
        this.authorizationService = authorizationService;
        this.enderecoService = enderecoService;
    }

    @Transactional
    public Usuario criar(UsuarioDTO dto) {
        validarCamposObrigatorios(dto);
        
        String cpfLimpo = dto.getCpf().replaceAll("[^0-9]", "");
        
        if (repository.existsByEmail(dto.getEmail())) {
            throw new UsuarioException.EmailJaExisteException("Email já cadastrado");
        }
        
        if (repository.existsByCpf(cpfLimpo)) {
            throw new UsuarioException.CpfJaExisteException("CPF já cadastrado");
        }
    
        Usuario usuario = new Usuario();
        preencherUsuario(usuario, dto);
        
        usuario.setRole(determinarRole(dto.getRole()));
    
        // Cria o objeto UsuarioAutenticar
        UsuarioAutenticar usuarioAuth = new UsuarioAutenticar();
        usuarioAuth.setCpf(cpfLimpo);
        usuarioAuth.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioAuth.setRole(usuario.getRole());
        
        // Configura o endereço se fornecido
        if (dto.getEndereco() != null) {
            // Validar endereço usando ViaCEP
            enderecoService.validarEndereco(dto.getEndereco());
            usuario.setEndereco(dto.getEndereco());
        }
        
        // Associa o usuarioAutenticar ao usuário
        usuario.setUsuarioAutenticar(usuarioAuth);
        
        usuario.setCreatedAt(java.time.LocalDateTime.now());
        
        // Salva o usuário com suas associações
        usuario = repository.save(usuario);
        
        return usuario;
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    public List<UsuarioResponseDTO> listarTodosResponse(Pageable pageable) {
        Page<Usuario> usuarios = repository.findAll(pageable);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return usuarios.getContent().stream()
                .map(usuario -> new UsuarioResponseDTO(
                    usuario.getIdUsuario(), 
                    usuario.getNome(), 
                    usuario.getCpf(),
                    usuario.getEmail(), 
                    usuario.getDataNascimento() != null ? usuario.getDataNascimento().format(formatter) : null,
                    usuario.getTelefone(),
                    usuario.getImagemPerfil(),
                    usuario.getEndereco(),
                    usuario.getRole()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioDTO dto) {
        validarCamposObrigatoriosParaEdicao(dto);
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
        }
        
        // Verifica se o CPF está sendo alterado
        String cpfFormatado = dto.getCpf().replaceAll("[^0-9]", "");
        if (!usuarioExistente.getCpf().equals(cpfFormatado)) {
            if (repository.existsByCpf(cpfFormatado)) {
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
            // Validar endereço usando ViaCEP
            enderecoService.validarEndereco(dto.getEndereco());
            
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
            if (dto.getSenha() != null && !dto.getSenha().isEmpty() && 
                !"SENHA_NAO_ALTERADA".equals(dto.getSenha()) && !dto.isManterSenhaAtual()) {
                
                // Validar nova senha
                if (!PasswordValidator.isValid(dto.getSenha())) {
                    throw new UsuarioValidationException.SenhaInvalidaException(PasswordValidator.getPasswordRequirements());
                }
                
                usuarioAuth.setSenha(passwordEncoder.encode(dto.getSenha()));
            }
            usuarioExistente.setUsuarioAutenticar(usuarioAuth);
        } else {
            UsuarioAutenticar usuarioAuth = usuarioExistente.getUsuarioAutenticar();
            usuarioAuth.setCpf(dto.getCpf());
            usuarioAuth.setRole(usuarioExistente.getRole());
            
            // Só atualiza a senha se não for o valor especial e se não tiver a flag manterSenhaAtual
            if (dto.getSenha() != null && !dto.getSenha().isEmpty() && 
                !"SENHA_NAO_ALTERADA".equals(dto.getSenha()) && !dto.isManterSenhaAtual()) {
                
                // Validar nova senha
                if (!PasswordValidator.isValid(dto.getSenha())) {
                    throw new UsuarioValidationException.SenhaInvalidaException(PasswordValidator.getPasswordRequirements());
                }
                
                // Se tiver senhaAtual, verificar se ela corresponde à senha atual
                if (dto.getSenhaAtual() != null && !dto.getSenhaAtual().isEmpty()) {
                    if (!passwordEncoder.matches(dto.getSenhaAtual(), usuarioAuth.getSenha())) {
                        throw new UsuarioException.SenhaInvalidaException("Senha atual incorreta");
                    }
                }
                
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
    public void reativar(Long id) {
        Usuario usuario = buscarPorId(id);
        
        if (!"ROLE_DELETED".equals(usuario.getRole())) {
            throw new UsuarioException.UsuarioNaoEncontradoException("Usuário não está desativado");
        }
        
        String roleOriginal = determinarRoleOriginal(usuario);
        usuario.setRole(roleOriginal);

        if (usuario.getUsuarioAutenticar() != null) {
            usuario.getUsuarioAutenticar().setRole(roleOriginal);
        }
        
        repository.save(usuario);
    }

    private String determinarRoleOriginal(Usuario usuario) {
        // Verifica se o usuário tem um registro de profissional associado
        // Se tiver, era um profissional (ROLE_PROF)
        // Caso contrário, era um usuário comum (ROLE_USER)
        
        boolean isProfissional = profissionalRepository.existsByUsuario_IdUsuario(usuario.getIdUsuario());
        
        if (isProfissional) {
            return "ROLE_PROF";
        } else {
            return "ROLE_USER";
        }
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

    private void validarCamposObrigatoriosParaEdicao(UsuarioDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new UsuarioValidationException.NomeObrigatorioException();
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new UsuarioValidationException.EmailObrigatorioException();
        }
        if (!EmailValidator.isValid(dto.getEmail())) {
            throw new UsuarioValidationException.EmailInvalidoException("Email inválido");
        }
        if (dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
            throw new UsuarioValidationException.CpfObrigatorioException();
        }
        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new UsuarioValidationException.CpfInvalidoException("CPF inválido");
        }
        if (dto.getDataNascimento() == null || dto.getDataNascimento().trim().isEmpty()) {
            throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
        }
        if (!DateValidator.isValid(dto.getDataNascimento())) {
            throw new UsuarioValidationException.DataInvalidaException("Data de nascimento inválida. Use o formato DD/MM/YYYY");
        }
        if (!DateValidator.hasMinimumAge(dto.getDataNascimento(), 18)) {
            throw new UsuarioValidationException.IdadeMinimaException(18);
        }
        
        // Para edição, só valida a senha se ela estiver sendo alterada
        if (dto.getSenha() != null && !dto.getSenha().isEmpty() && 
            !"SENHA_NAO_ALTERADA".equals(dto.getSenha()) && !dto.isManterSenhaAtual()) {
            if (!PasswordValidator.isValid(dto.getSenha())) {
                throw new UsuarioValidationException.SenhaInvalidaException(PasswordValidator.getPasswordRequirements());
            }
        }
    }

    private void validarCamposObrigatorios(UsuarioDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new UsuarioValidationException.NomeObrigatorioException();
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new UsuarioValidationException.EmailObrigatorioException();
        }
        if (!EmailValidator.isValid(dto.getEmail())) {
            throw new UsuarioValidationException.EmailInvalidoException("Email inválido");
        }
        if (dto.getCpf() == null || dto.getCpf().trim().isEmpty()) {
            throw new UsuarioValidationException.CpfObrigatorioException();
        }
        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new UsuarioValidationException.CpfInvalidoException("CPF inválido");
        }
        if (dto.getDataNascimento() == null || dto.getDataNascimento().trim().isEmpty()) {
            throw new UsuarioValidationException.DataNascimentoObrigatoriaException();
        }
        if (!DateValidator.isValid(dto.getDataNascimento())) {
            throw new UsuarioValidationException.DataInvalidaException("Data de nascimento inválida. Use o formato DD/MM/YYYY");
        }
        if (!DateValidator.hasMinimumAge(dto.getDataNascimento(), 18)) {
            throw new UsuarioValidationException.IdadeMinimaException(18);
        }
        if (dto.getSenha() == null || dto.getSenha().trim().isEmpty()) {
            throw new UsuarioValidationException.SenhaObrigatoriaException();
        }
        if (!PasswordValidator.isValid(dto.getSenha())) {
            throw new UsuarioValidationException.SenhaInvalidaException(PasswordValidator.getPasswordRequirements());
        }
    }

    private void preencherUsuario(Usuario usuario, UsuarioDTO dto) {
        String cpfLimpo = dto.getCpf().replaceAll("[^0-9]", "");
        usuario.setNome(dto.getNome());
        usuario.setCpf(cpfLimpo);
        usuario.setEmail(dto.getEmail());
        usuario.setDataNascimento(DateValidator.parseDate(dto.getDataNascimento()));
        usuario.setTelefone(dto.getTelefone());
        
        // Só atualiza a imagem de perfil se ela for fornecida no DTO
        if (dto.getImagemPerfil() != null) {
            usuario.setImagemPerfil(dto.getImagemPerfil());
        }
        // Se for null, mantém a imagem existente
    }

    private String determinarRole(String role) {
        if (role != null) {
            if (role.equalsIgnoreCase("admin")) {
                return "ROLE_ADMIN";
            } else if (role.equalsIgnoreCase("deleted")) {
                return "ROLE_DELETED";
            } else if (role.equalsIgnoreCase("prof")) {
                return "ROLE_PROF";
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

    private void atualizarToken(Usuario usuario, String novoToken) {
        if (usuario.getTokenAtual() != null) {
            TokenRevogado tokenRevogado = new TokenRevogado(usuario.getTokenAtual());
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

    public Usuario buscarPorEmailOptional(String email) {
        return repository.findByEmail(email).orElse(null);
    }
    
    public Usuario buscarPorCpfOptional(String cpf) {
        return repository.findByCpf(cpf).orElse(null);
    }

    public void salvar(Usuario usuario) {
        repository.save(usuario);
    }
    
    @Transactional
    public void atualizarFotoPerfil(Long id, String imagemBase64) {
        Usuario usuario = buscarPorId(id);
        usuario.setImagemPerfil(imagemBase64);
        repository.save(usuario);
    }

    public List<UsuarioResponseDTO> listarTodosComAutorizacao(Pageable pageable) {
        authorizationService.requireAdmin();
        return listarTodosResponse(pageable);
    }

    public UsuarioSeguroDTO buscarPorIdComAutorizacao(Long id) {
        authorizationService.requireUserAccessOrAdmin(id);
        Usuario usuario = buscarPorId(id);
        return UsuarioSeguroDTO.fromUsuario(usuario);
    }

    public UsuarioResponseDTO buscarDetalhesComAutorizacao(Long id) {
        authorizationService.requireUserAccessOrAdmin(id);
        Usuario usuario = buscarPorId(id);
        
        String dataNascimentoStr = null;
        if (usuario.getDataNascimento() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimentoStr = usuario.getDataNascimento().format(formatter);
        }
        
        return new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getNome(),
            usuario.getCpf(),
            usuario.getEmail(),
            dataNascimentoStr,
            usuario.getTelefone(),
            usuario.getImagemPerfil(),
            usuario.getEndereco(),
            usuario.getRole()
        );
    }

    public UsuarioSeguroDTO atualizarComAutorizacao(Long id, UsuarioDTO dto) {
        authorizationService.requireUserAccessOrAdmin(id);
        Usuario usuario = atualizar(id, dto);
        return UsuarioSeguroDTO.fromUsuario(usuario);
    }

    public void inativarComAutorizacao(Long id) {
        authorizationService.requireAdmin();
        inativar(id);
    }

    public void deletarComAutorizacao(Long id) {
        authorizationService.requireAdmin();
        deletar(id);
    }

    public void atualizarFotoPerfilComAutorizacao(Long id, String imagemBase64) {
        authorizationService.requireUserAccessOrAdmin(id);
        
        if (imagemBase64 == null || imagemBase64.isEmpty()) {
            throw new InvalidProfileImageException("Imagem não fornecida");
        }
        
        atualizarFotoPerfil(id, imagemBase64);
    }

    public boolean validateTokenComplete(Long id, String token) {
        if (token == null || token.isEmpty()) {
            throw new TokenValidationException("Token não fornecido");
        }
        
        Usuario usuario = buscarPorId(id);
        String tokenAtual = usuario.getTokenAtual();
        
        if (tokenAtual == null) {
            throw new TokenValidationException("Usuário não possui token ativo");
        }
        
        return token.equals(tokenAtual);
    }
}