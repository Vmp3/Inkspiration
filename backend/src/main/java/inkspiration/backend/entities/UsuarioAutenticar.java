package inkspiration.backend.entities;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.util.Hashing;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "usuario_autenticar")
public class UsuarioAutenticar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuarioAutenticar;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve ter 11 dígitos")
    @Column(unique = true)
    private String cpf;
    
    @NotBlank(message = "A senha é obrigatória")
    private String senha;
    
    @NotBlank(message = "O papel do usuário é obrigatório")
    @Pattern(regexp = "^ROLE_(ADMIN|USER|PROF|DELETED)$", message = "Role do usuário inválida")
    private String role;

    public UsuarioAutenticar(Long idUsuarioAutenticar, String cpf, String senha, String role) {
        this.idUsuarioAutenticar = idUsuarioAutenticar;
        this.setCpf(cpf);
        this.senha = senha;
        this.setRole(role);
    }

    public UsuarioAutenticar(UsuarioAutenticarDTO dto) {
        this.idUsuarioAutenticar = dto.getIdUsuarioAutenticar();
        this.setCpf(dto.getCpf());
        this.senha = dto.getSenha();
        this.setRole(dto.getRole());
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
        if (cpf != null) {
            this.cpf = cpf.replaceAll("[^0-9]", "");
        } else {
            this.cpf = null;
        }
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
        if (role != null) {
            UserRole validatedRole = UserRole.fromString(role);
            this.role = validatedRole.getRole();
        } else {
            throw new IllegalArgumentException("Role não pode ser nula");
        }
    }
}