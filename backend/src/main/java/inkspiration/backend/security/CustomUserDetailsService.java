package inkspiration.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.repository.UsuarioAutenticarRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioAutenticarRepository repository;

    @Autowired
    public CustomUserDetailsService(UsuarioAutenticarRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        UsuarioAutenticar usuarioAutenticar = repository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o CPF: " + cpf));

        return User.builder()
                .username(usuarioAutenticar.getCpf())
                .password(usuarioAutenticar.getSenha())
                .roles(usuarioAutenticar.getRole().replace("ROLE_", ""))
                .build();
    }
} 