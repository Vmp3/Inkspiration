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
import java.util.ArrayList;
import java.util.List;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPortfolio;
    
    @Column(length = 2000)
    private String descricao;
    
    @Column(length = 1000)
    private String experiencia;
    
    @Column(length = 500)
    private String especialidade;
    
    @Column(length = 255)
    private String website;
    
    @Column(length = 50)
    private String tiktok;
    
    @Column(length = 50)
    private String instagram;
    
    @Column(length = 50)
    private String facebook;
    
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
        this.descricao = descricao;
    }
    
    public String getExperiencia() {
        return experiencia;
    }
    
    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getTiktok() {
        return tiktok;
    }
    
    public void setTiktok(String tiktok) {
        this.tiktok = tiktok;
    }
    
    public String getInstagram() {
        return instagram;
    }
    
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
    
    public String getFacebook() {
        return facebook;
    }
    
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
    
    public String getTwitter() {
        return twitter;
    }
    
    public void setTwitter(String twitter) {
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