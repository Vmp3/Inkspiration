package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UsuarioAutenticarDTO {

    private Long idUsuarioAutenticar;

    @NotBlank
    private String cpf;
    private String senha;

    private String role;
    
    // Campo opcional para código de 2FA
    private Integer twoFactorCode;

    public UsuarioAutenticarDTO() {
    }

    public UsuarioAutenticarDTO(Long idUsuarioAutenticar, String cpf, String senha, String role) {
        this.idUsuarioAutenticar = idUsuarioAutenticar;
        this.cpf = cpf;
        this.senha = senha;
        this.role = role;
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
    
    public Integer getTwoFactorCode() {
        return twoFactorCode;
    }
    
    public void setTwoFactorCode(Integer twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }
}