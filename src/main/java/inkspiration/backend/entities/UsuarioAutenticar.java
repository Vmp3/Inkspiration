package inkspiration.backend.entities;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.util.Hashing;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UsuarioAutenticar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuarioAutenticar;

    private String cpf;
    private String senha;
    private String role;

    public UsuarioAutenticar(Long idUsuarioAutenticar, String cpf, String senha, String role) {
        this.idUsuarioAutenticar = idUsuarioAutenticar;
        this.cpf = cpf;
        this.senha = Hashing.hash(senha);
        this.role = role;
    }

    public UsuarioAutenticar(UsuarioAutenticarDTO dto) {
        this.idUsuarioAutenticar = dto.getIdUsuarioAutenticar();
        this.cpf = dto.getCpf();
        this.senha = Hashing.hash(dto.getSenha());
        this.role = dto.getRole();
    }

    public UsuarioAutenticar() {
    }

    public Long getIdUsuarioAutenticar() {
        return idUsuarioAutenticar;
    }
    public void setIdUsuarioAutenticar(Long idUsuarioAutenticar) {
        this.idUsuarioAutenticar = idUsuarioAutenticar;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}