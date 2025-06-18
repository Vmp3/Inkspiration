package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.repository.UsuarioAutenticarRepository;
import inkspiration.backend.util.Hashing;

@Service
public class UsuarioAutenticarService {

    private final UsuarioAutenticarRepository repository;

    @Autowired
    public UsuarioAutenticarService(UsuarioAutenticarRepository repository) {
        this.repository = repository;
    }

    public boolean authenticate(String cpf, String senha) {
        UsuarioAutenticar usuarioAutenticar = repository.findByCpf(cpf).orElse(null);
        if (usuarioAutenticar != null) {
            return Hashing.matches(senha, usuarioAutenticar.getSenha());
        }
        return false;
    }

    public UsuarioAutenticar criar(UsuarioAutenticarDTO dto) {
        String cpfLimpo = dto.getCpf().replaceAll("[^0-9]", "");
        
        UsuarioAutenticar usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf(cpfLimpo);
        usuarioAutenticar.setSenha(dto.getSenha());
        usuarioAutenticar.setRole(dto.getRole());
        
        return repository.save(usuarioAutenticar);
    }

    public UsuarioAutenticar buscarPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        return repository.findByCpf(cpfLimpo)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void salvar(UsuarioAutenticar usuarioAutenticar) {
        repository.save(usuarioAutenticar);
    }

    public void deletar(UsuarioAutenticar usuarioAutenticar) {
        repository.delete(usuarioAutenticar);
    }
}