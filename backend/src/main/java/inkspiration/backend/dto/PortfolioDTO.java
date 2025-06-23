package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PortfolioDTO {
    
    private Long idPortfolio;
    
    private Long idProfissional;
    
    @NotBlank(message = "Biografia é obrigatória")
    @Size(min = 20, max = 500, message = "Biografia deve ter entre 20 e 500 caracteres")
    private String descricao;
    
    @Size(max = 1000)
    private String experiencia;
    
    @Size(max = 500)
    private String especialidade;
    
    @Size(max = 255, message = "Website deve ter no máximo 255 caracteres")
    private String website;
    
    @Size(max = 50, message = "TikTok deve ter no máximo 50 caracteres")
    private String tiktok;
    
    @Size(max = 50, message = "Instagram deve ter no máximo 50 caracteres")
    private String instagram;
    
    @Size(max = 50, message = "Facebook deve ter no máximo 50 caracteres")
    private String facebook;
    
    @Size(max = 50, message = "Twitter deve ter no máximo 50 caracteres")
    private String twitter;
    
    public PortfolioDTO() {}
    
    public PortfolioDTO(Long idPortfolio, Long idProfissional, String descricao, String experiencia, String especialidade,
                         String website, String tiktok, String instagram, String facebook, String twitter) {
        this.idPortfolio = idPortfolio;
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
    public Long getIdPortfolio() {
        return idPortfolio;
    }
    
    public void setIdPortfolio(Long idPortfolio) {
        this.idPortfolio = idPortfolio;
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