package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PortifolioDTO {
    
    private Long idPortifolio;
    
    private Long idProfissional;
    
    @NotBlank(message = "Biografia é obrigatória")
    @Size(min = 20, max = 500, message = "Biografia deve ter entre 20 e 500 caracteres")
    private String descricao;
    
    @Size(max = 1000)
    private String experiencia;
    
    @Size(max = 500)
    private String especialidade;
    
    private String website;
    private String tiktok;
    private String instagram;
    private String facebook;
    private String twitter;
    
    public PortifolioDTO() {}
    
    public PortifolioDTO(Long idPortifolio, Long idProfissional, String descricao, String experiencia, String especialidade,
                         String website, String tiktok, String instagram, String facebook, String twitter) {
        this.idPortifolio = idPortifolio;
        this.idProfissional = idProfissional;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.especialidade = especialidade;
        this.website = website;
        this.tiktok = tiktok;
        this.instagram = instagram;
        this.facebook = facebook;
        this.twitter = twitter;
    }
    
    // Getters e Setters
    public Long getIdPortifolio() {
        return idPortifolio;
    }
    
    public void setIdPortifolio(Long idPortifolio) {
        this.idPortifolio = idPortifolio;
    }
    
    public Long getIdProfissional() {
        return idProfissional;
    }
    
    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
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
} 