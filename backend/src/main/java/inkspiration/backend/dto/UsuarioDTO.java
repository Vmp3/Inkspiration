package inkspiration.backend.dto;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.validation.PastDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UsuarioDTO {

    private Long idUsuario;

    @NotBlank
    private String nome;
    
    @NotBlank
    private String cpf;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Data deve estar no formato DD/MM/YYYY")
    @PastDate
    private String dataNascimento;
    
    private String telefone;
    
    private String imagemPerfil;
    
    @NotBlank
    private String senha;
    
    private Endereco endereco;
    
    private UsuarioAutenticar usuarioAutenticar;

    private String role;
    
    private boolean manterSenhaAtual;
    private String senhaAtual;

    public UsuarioDTO() {}

    public UsuarioDTO(Long idUsuario, String nome, String cpf, String email, String dataNascimento, 
                     String telefone, String imagemPerfil, String senha, Endereco endereco, String role) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.imagemPerfil = imagemPerfil;
        this.senha = senha;
        this.endereco = endereco;
        this.role = role;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getImagemPerfil() {
        return imagemPerfil;
    }
    
    public void setImagemPerfil(String imagemPerfil) {
        this.imagemPerfil = imagemPerfil;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public Endereco getEndereco() {
        return endereco;
    }
    
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    public UsuarioAutenticar getUsuarioAutenticar() {
        return usuarioAutenticar;
    }
    
    public void setUsuarioAutenticar(UsuarioAutenticar usuarioAutenticar) {
        this.usuarioAutenticar = usuarioAutenticar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isManterSenhaAtual() {
        return manterSenhaAtual;
    }
    
    public void setManterSenhaAtual(boolean manterSenhaAtual) {
        this.manterSenhaAtual = manterSenhaAtual;
    }
    
    public String getSenhaAtual() {
        return senhaAtual;
    }
    
    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }
}