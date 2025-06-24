package inkspiration.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPortfolio;
    
    @Size(min = 20, max = 500, message = "A descrição deve ter entre 20 e 500 caracteres")
    @Column(length = 500)
    private String descricao;
    
    @Size(max = 1000, message = "A experiência não pode exceder 1000 caracteres")
    @Column(length = 1000)
    private String experiencia;
    
    @Size(max = 500, message = "A especialidade não pode exceder 500 caracteres")
    @Column(length = 500)
    private String especialidade;
    
    @Size(max = 255, message = "O website não pode exceder 255 caracteres")
    @Pattern(regexp = "^(https?://).*|^$", message = "O website deve começar com http:// ou https://")
    @Column(length = 255)
    private String website;
    
    @Size(max = 50, message = "O TikTok não pode exceder 50 caracteres")
    @Pattern(regexp = "^@?[a-zA-Z0-9._]+$|^$", message = "TikTok deve conter apenas letras, números, pontos e underscores")
    @Column(length = 50)
    private String tiktok;
    
    @Size(max = 50, message = "O Instagram não pode exceder 50 caracteres")
    @Pattern(regexp = "^@?[a-zA-Z0-9._]+$|^$", message = "Instagram deve conter apenas letras, números, pontos e underscores")
    @Column(length = 50)
    private String instagram;
    
    @Size(max = 50, message = "O Facebook não pode exceder 50 caracteres")
    @Column(length = 50)
    private String facebook;
    
    @Size(max = 50, message = "O Twitter não pode exceder 50 caracteres")
    @Pattern(regexp = "^@?[a-zA-Z0-9._]+$|^$", message = "Twitter deve conter apenas letras, números, pontos e underscores")
    @Column(length = 50)
    private String twitter;
    
    @OneToOne(mappedBy = "portfolio", cascade = CascadeType.ALL)
    @JsonIgnore
    private Profissional profissional;
    
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagem> imagens = new ArrayList<>();
    
    public Portfolio() {}
    
    // Getters e Setters
    public Long getIdPortfolio() {
        return idPortfolio;
    }
    
    public void setIdPortfolio(Long idPortfolio) {
        this.idPortfolio = idPortfolio;
    }
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        if (descricao != null && descricao.length() > 500) {
            throw new IllegalArgumentException("A descrição não pode exceder 500 caracteres");
        }
        this.descricao = descricao;
    }
    
    public String getExperiencia() {
        return experiencia;
    }
    
    public void setExperiencia(String experiencia) {
        if (experiencia != null && experiencia.length() > 1000) {
            throw new IllegalArgumentException("A experiência não pode exceder 1000 caracteres");
        }
        this.experiencia = experiencia;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade) {
        if (especialidade != null && especialidade.length() > 500) {
            throw new IllegalArgumentException("A especialidade não pode exceder 500 caracteres");
        }
        this.especialidade = especialidade;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        if (website != null && !website.trim().isEmpty()) {
            if (website.length() > 255) {
                throw new IllegalArgumentException("O website não pode exceder 255 caracteres");
            }
            if (!website.matches("^(https?://).*")) {
                throw new IllegalArgumentException("O website deve começar com http:// ou https://");
            }
        }
        this.website = website;
    }
    
    public String getTiktok() {
        return tiktok;
    }
    
    public void setTiktok(String tiktok) {
        if (tiktok != null && !tiktok.trim().isEmpty()) {
            if (tiktok.length() > 50) {
                throw new IllegalArgumentException("O TikTok não pode exceder 50 caracteres");
            }
            String cleanTiktok = tiktok.startsWith("@") ? tiktok.substring(1) : tiktok;
            if (!cleanTiktok.matches("^[a-zA-Z0-9._]+$")) {
                throw new IllegalArgumentException("TikTok deve conter apenas letras, números, pontos e underscores");
            }
        }
        this.tiktok = tiktok;
    }
    
    public String getInstagram() {
        return instagram;
    }
    
    public void setInstagram(String instagram) {
        if (instagram != null && !instagram.trim().isEmpty()) {
            if (instagram.length() > 50) {
                throw new IllegalArgumentException("O Instagram não pode exceder 50 caracteres");
            }
            String cleanInstagram = instagram.startsWith("@") ? instagram.substring(1) : instagram;
            if (!cleanInstagram.matches("^[a-zA-Z0-9._]+$")) {
                throw new IllegalArgumentException("Instagram deve conter apenas letras, números, pontos e underscores");
            }
        }
        this.instagram = instagram;
    }
    
    public String getFacebook() {
        return facebook;
    }
    
    public void setFacebook(String facebook) {
        if (facebook != null && facebook.length() > 50) {
            throw new IllegalArgumentException("O Facebook não pode exceder 50 caracteres");
        }
        this.facebook = facebook;
    }
    
    public String getTwitter() {
        return twitter;
    }
    
    public void setTwitter(String twitter) {
        if (twitter != null && !twitter.trim().isEmpty()) {
            if (twitter.length() > 50) {
                throw new IllegalArgumentException("O Twitter não pode exceder 50 caracteres");
            }
            String cleanTwitter = twitter.startsWith("@") ? twitter.substring(1) : twitter;
            if (!cleanTwitter.matches("^[a-zA-Z0-9._]+$")) {
                throw new IllegalArgumentException("Twitter deve conter apenas letras, números, pontos e underscores");
            }
        }
        this.twitter = twitter;
    }
    
    public List<Imagem> getImagens() {
        return imagens;
    }
    
    public void setImagens(List<Imagem> imagens) {
        this.imagens = imagens;
    }
    
    public void adicionarImagem(Imagem imagem) {
        imagens.add(imagem);
        imagem.setPortfolio(this);
    }
    
    public void removerImagem(Imagem imagem) {
        imagens.remove(imagem);
        imagem.setPortfolio(null);
    }
} 