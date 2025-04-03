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
    private Long id;

    private String email;
    private String senha;
    private String role;

    public UsuarioAutenticar(Long id, String email, String senha, String role) {
        this.id = id;
        this.email = email;
        this.senha = Hashing.hash(senha);
        this.role = role;
    }

    public UsuarioAutenticar(UsuarioAutenticarDTO dto) {
        this.id = dto.getId();
        this.email = dto.getEmail();
        this.senha = Hashing.hash(dto.getSenha());
        this.role = dto.getRole();
    }

    public UsuarioAutenticar() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
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