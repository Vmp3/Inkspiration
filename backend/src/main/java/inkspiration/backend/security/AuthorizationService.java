package inkspiration.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.UsuarioRepository;

@Service
public class AuthorizationService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Verifica se o usuário atual é administrador
     */
    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Obtém o ID do usuário atual a partir do token JWT
     */
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }

        if (auth instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
            Jwt jwt = jwtAuth.getToken();
            return jwt.getClaim("userId");
        }

        return null;
    }

    /**
     * Obtém o CPF do usuário atual
     */
    public String getCurrentUserCpf() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        
        return auth.getName(); // O subject do JWT é o CPF
    }

    /**
     * Verifica se o usuário atual pode acessar dados do usuário com o ID especificado
     * Administradores podem acessar qualquer usuário, usuários comuns só podem acessar seus próprios dados
     */
    public boolean canAccessUser(Long targetUserId) {
        if (isCurrentUserAdmin()) {
            return true;
        }

        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(targetUserId);
    }

    /**
     * Verifica se o usuário atual é dono do perfil profissional ou administrador
     */
    public boolean canAccessProfessional(Long professionalUserId) {
        if (isCurrentUserAdmin()) {
            return true;
        }

        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(professionalUserId);
    }

    /**
     * Verifica se a operação requer permissão de administrador
     */
    public void requireAdmin() {
        if (!isCurrentUserAdmin()) {
            throw new UsuarioException.PermissaoNegadaException("Acesso negado. Apenas administradores podem realizar esta operação.");
        }
    }

    /**
     * Verifica se o usuário pode acessar o recurso ou lança exceção
     */
    public void requireUserAccessOrAdmin(Long targetUserId) {
        if (!canAccessUser(targetUserId)) {
            throw new UsuarioException.PermissaoNegadaException("Acesso negado. Você só pode acessar seus próprios dados.");
        }
    }

    /**
     * Obtém o usuário atual
     */
    public Usuario getCurrentUser() {
        String cpf = getCurrentUserCpf();
        if (cpf == null) {
            throw new UsuarioException.UsuarioNaoEncontradoException("Usuário não autenticado");
        }

        return usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));
    }

    /**
     * Verifica se o token pertence realmente ao usuário especificado
     */
    public boolean validateTokenOwnership(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * Verifica se o usuário atual pode editar o perfil especificado
     */
    public boolean canEditProfile(Long profileUserId) {
        return isCurrentUserAdmin() || canAccessUser(profileUserId);
    }
} 