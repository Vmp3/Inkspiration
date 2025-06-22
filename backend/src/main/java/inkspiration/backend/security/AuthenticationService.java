package inkspiration.backend.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.authentication.AuthenticationFailedException;
import inkspiration.backend.exception.authentication.InvalidTwoFactorCodeException;
import inkspiration.backend.exception.authentication.TwoFactorRequiredException;
import inkspiration.backend.exception.authentication.UserInactiveException;
import inkspiration.backend.exception.authentication.UserNotFoundException;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.service.UsuarioService;

@Service
public class AuthenticationService {

    private final JwtService jwtService;
    private final TokenRevogadoRepository tokenRevogadoRepository;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthenticationService(JwtService jwtService, 
                               TokenRevogadoRepository tokenRevogadoRepository,
                               AuthenticationManager authenticationManager,
                               UsuarioService usuarioService,
                               TwoFactorAuthService twoFactorAuthService) {
        this.jwtService = jwtService;
        this.tokenRevogadoRepository = tokenRevogadoRepository;
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    public String login(UsuarioAutenticarDTO loginDTO) {
        System.out.println("Tentativa de login para usuário com CPF: " + loginDTO.getCpf());
        
        Authentication authentication = authenticateCredentials(loginDTO);
        validateUserStatus(authentication, loginDTO.getCpf());
        Usuario usuario = getAndValidateUser(loginDTO.getCpf());
        validateTwoFactorIfEnabled(usuario, loginDTO);
        String token = generateAndSaveToken(authentication, usuario, loginDTO);
        
        return token;
    }

    private Authentication authenticateCredentials(UsuarioAutenticarDTO loginDTO) {
        try {
            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCpf(), loginDTO.getSenha())
            );
        } catch (Exception e) {
            throw new AuthenticationFailedException("CPF ou senha incorretos");
        }
    }

    private void validateUserStatus(Authentication authentication, String cpf) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DELETED"))) {
            System.out.println("Tentativa de login de usuário inativo com CPF: " + cpf);
            throw new UserInactiveException("Usuário inativo ou deletado");
        }
    }

    private Usuario getAndValidateUser(String cpf) {
        Usuario usuario = usuarioService.buscarPorCpf(cpf);
        if (usuario == null) {
            throw new UserNotFoundException("Usuário não encontrado");
        }
        return usuario;
    }

    private void validateTwoFactorIfEnabled(Usuario usuario, UsuarioAutenticarDTO loginDTO) {
        boolean isTwoFactorEnabled = twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario());
        
        if (isTwoFactorEnabled) {
            if (loginDTO.getTwoFactorCode() == null) {
                System.out.println("2FA necessário para usuário com CPF: " + loginDTO.getCpf());
                throw new TwoFactorRequiredException("Código de autenticação de dois fatores é obrigatório");
            }
            
            boolean isValidCode = twoFactorAuthService.validateCode(usuario.getIdUsuario(), loginDTO.getTwoFactorCode());
            if (!isValidCode) {
                System.out.println("Código 2FA inválido para usuário com CPF: " + loginDTO.getCpf());
                throw new InvalidTwoFactorCodeException("Código de autenticação de dois fatores inválido");
            }
            
            System.out.println("Código 2FA validado com sucesso para usuário com CPF: " + loginDTO.getCpf());
        }
    }

    private String generateAndSaveToken(Authentication authentication, Usuario usuario, UsuarioAutenticarDTO loginDTO) {
        Boolean rememberMe = loginDTO.getRememberMe() != null ? loginDTO.getRememberMe() : false;
        String token = jwtService.generateToken(authentication, rememberMe);
        
        System.out.println("Token gerado para usuário com CPF: " + loginDTO.getCpf() + 
                          " (Remember Me: " + rememberMe + ")");
        
        usuario.setTokenAtual(token);
        usuarioService.salvar(usuario);
        System.out.println("Token salvo para usuário com CPF: " + loginDTO.getCpf());
        
        return token;
    }

    public String reautenticar(Long userId) {
        try {
            System.out.println("Reautenticando usuário ID: " + userId);
            
            // Buscar o usuário pelo ID
            Usuario usuario = usuarioService.buscarPorId(userId);
            if (usuario == null) {
                throw new UserNotFoundException("Usuário não encontrado");
            }
            
            // Buscar o CPF do usuário
            String cpf = usuario.getCpf();
            
            // Buscar a role atual do usuário
            String role = usuario.getRole();
            System.out.println("Role atual do usuário: " + role);
            
            // Criar uma autenticação com a role atualizada
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                cpf, 
                null, 
                java.util.Collections.singletonList(new SimpleGrantedAuthority(role))
            );
            
            // Gerar novo token com a role atualizada
            String novoToken = jwtService.generateToken(authentication);
            System.out.println("Novo token gerado para usuário ID: " + userId);
            
            // Revogar o token antigo se existir e for diferente do novo
            String tokenAntigo = usuario.getTokenAtual();
            if (tokenAntigo != null && !tokenAntigo.equals(novoToken)) {
                System.out.println("Revogando token antigo para usuário ID: " + userId);
                revogarToken(tokenAntigo);
            }
            
            // Atualizar o token no usuário
            usuario.setTokenAtual(novoToken);
            usuarioService.salvar(usuario);
            
            return novoToken;
        } catch (Exception e) {
            throw new AuthenticationFailedException("Erro ao reautenticar: " + e.getMessage());
        }
    }

    public boolean checkTwoFactorRequirement(String cpf) {
        try {
            if (cpf == null || cpf.trim().isEmpty()) {
                throw new IllegalArgumentException("CPF é obrigatório");
            }
            
            // Buscar o usuário pelo CPF
            Usuario usuario = usuarioService.buscarPorCpf(cpf);
            if (usuario == null) {
                return false;
            }
            
            // Verificar se o 2FA está ativado
            return twoFactorAuthService.isTwoFactorEnabled(usuario.getIdUsuario());
        } catch (Exception e) {
            System.err.println("Erro ao verificar 2FA: " + e.getMessage());
            throw new AuthenticationFailedException("Erro interno do servidor");
        }
    }

    public String authenticate(Authentication authentication) {
        return jwtService.generateToken(authentication);
    }
    
    public String authenticate(Authentication authentication, Boolean rememberMe) {
        return jwtService.generateToken(authentication, rememberMe);
    }
    
    public boolean isTokenRevoked(String token) {
        return tokenRevogadoRepository.existsByToken(token);
    }

    public void revogarToken(String token) {
        if (token != null && !token.isEmpty()) {
            TokenRevogado tokenRevogado = new TokenRevogado(token);
            tokenRevogadoRepository.save(tokenRevogado);
        }
    }
}