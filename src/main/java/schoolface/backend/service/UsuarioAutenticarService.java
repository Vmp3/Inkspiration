package schoolface.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import schoolface.backend.entities.UsuarioAutenticar;
import schoolface.backend.repository.UsuarioAutenticarRepository;
import schoolface.backend.util.Hashing;

@Service
public class UsuarioAutenticarService {

    private final UsuarioAutenticarRepository usuarioAutenticarRepository;

    @Autowired
    public UsuarioAutenticarService(UsuarioAutenticarRepository usuarioAutenticarRepository) {
        this.usuarioAutenticarRepository = usuarioAutenticarRepository;
    }

    public boolean authenticate(String email, String senha) {
        UsuarioAutenticar usuarioAutenticar = usuarioAutenticarRepository.findByEmail(email).orElse(null);
        if (usuarioAutenticar != null) {
            return Hashing.matches(senha, usuarioAutenticar.getSenha());
        }
        return false;
    }

    public Optional<UsuarioAutenticar> buscarPorEmail(String email) {
        return usuarioAutenticarRepository.findByEmail(email);
    }

    public void salvar(UsuarioAutenticar usuarioAutenticar) {
        usuarioAutenticarRepository.save(usuarioAutenticar);
    }

    public void deletar(UsuarioAutenticar usuarioAutenticar) {
        usuarioAutenticarRepository.delete(usuarioAutenticar);
    }
}