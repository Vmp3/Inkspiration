package inkspiration.backend.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import inkspiration.backend.entities.UsuarioAutenticar;

public class UserAuthenticated implements UserDetails {

    private final UsuarioAutenticar usuario;

    public UserAuthenticated(UsuarioAutenticar usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = usuario.getRole();
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role não pode ser nulo ou vazio");
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getCpf();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !usuario.getRole().equals("ROLE_DELETED");
    }
}