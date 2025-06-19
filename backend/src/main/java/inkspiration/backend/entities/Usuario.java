package inkspiration.backend.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;
    private String telefone;
    
    @Column(length = 1000, columnDefinition = "TEXT")
    private String imagemPerfil;

    @JsonIgnore
    @Column(length = 1000, columnDefinition = "TEXT")
    private String tokenAtual;

    // Campos para autenticação de dois fatores
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;
    
    @JsonIgnore
    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_usuario_autenticar")
    private UsuarioAutenticar usuarioAutenticar;

    private String role;

    public Usuario() {}
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }
    
    // Getters e Setters
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
        this.cpf = cpf.replaceAll("[^0-9]", "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
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

    public String getTokenAtual() {
        return tokenAtual;
    }

    public void setTokenAtual(String tokenAtual) {
        this.tokenAtual = tokenAtual;
    }

    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}