package inkspiration.backend.dto;

import java.util.List;

import inkspiration.backend.enums.TipoServico;
import jakarta.validation.constraints.NotEmpty;

public class ProfissionalCriacaoDTO {
    private Long idUsuario;
    private Long idEndereco;
    
    @NotEmpty(message = "Pelo menos um tipo de serviço é obrigatório")
    private List<TipoServico> tiposServico;
    
    // Portifolio
    private String descricao;
    private String experiencia;
    private String especialidade;
    private List<String> estilosTatuagem;
    private String website;
    private String tiktok;
    private String instagram;
    private String facebook;
    private String twitter;
    
    // Disponibilidade
    private List<DisponibilidadeDTO> disponibilidades;
    
    public ProfissionalCriacaoDTO() {
    }
    
    public ProfissionalCriacaoDTO(Long idUsuario, Long idEndereco, List<TipoServico> tiposServico, String descricao, 
                                String experiencia, String especialidade,
                                List<String> estilosTatuagem, List<DisponibilidadeDTO> disponibilidades,
                                String website, String tiktok, String instagram, String facebook, String twitter) {
        this.idUsuario = idUsuario;
        this.idEndereco = idEndereco;
        this.tiposServico = tiposServico;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.especialidade = especialidade;
        this.estilosTatuagem = estilosTatuagem;
        this.disponibilidades = disponibilidades;
        this.website = website;
        this.tiktok = tiktok;
        this.instagram = instagram;
        this.facebook = facebook;
        this.twitter = twitter;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Long idEndereco) {
        this.idEndereco = idEndereco;
    }
    
    public List<TipoServico> getTiposServico() {
        return tiposServico;
    }
    
    public void setTiposServico(List<TipoServico> tiposServico) {
        this.tiposServico = tiposServico;
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

    public List<String> getEstilosTatuagem() {
        return estilosTatuagem;
    }

    public void setEstilosTatuagem(List<String> estilosTatuagem) {
        this.estilosTatuagem = estilosTatuagem;
    }

    public List<DisponibilidadeDTO> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<DisponibilidadeDTO> disponibilidades) {
        this.disponibilidades = disponibilidades;
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