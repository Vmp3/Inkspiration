package inkspiration.backend.dto;

import inkspiration.backend.entities.Endereco;

public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nome;
    private String cpf;
    private String email;
    private String dataNascimento;
    private String telefone;
    private String imagemPerfil;
    private Endereco endereco;
    private String role;

    public UsuarioResponseDTO(Long idUsuario, String nome, String cpf, String email, 
                             String dataNascimento, String telefone, String imagemPerfil, 
                             Endereco endereco, String role) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.imagemPerfil = imagemPerfil;
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
    
    public Endereco getEndereco() {
        return endereco;
    }
    
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 