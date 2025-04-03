package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UsuarioAutenticarDTO {

    private Long id;

    @NotBlank
    private String email;
    private String senha;

    private String role;

    public UsuarioAutenticarDTO() {
    }

    public UsuarioAutenticarDTO(Long id, String email, String senha, String role) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.role = role;
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