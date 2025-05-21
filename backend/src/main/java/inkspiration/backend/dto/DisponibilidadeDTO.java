package inkspiration.backend.dto;

import inkspiration.backend.entities.Disponibilidade;

public class DisponibilidadeDTO {
    private Long idDisponibilidade;
    private Long idProfissional;
    private String hrAtendimento;
    
    public DisponibilidadeDTO() {
    }
    
    public DisponibilidadeDTO(Disponibilidade disponibilidade) {
        this.idDisponibilidade = disponibilidade.getIdDisponibilidade();
        this.idProfissional = disponibilidade.getProfissional().getIdProfissional();
        this.hrAtendimento = disponibilidade.getHrAtendimento();
    }
    
    public DisponibilidadeDTO(Long idDisponibilidade, Long idProfissional, String hrAtendimento) {
        this.idDisponibilidade = idDisponibilidade;
        this.idProfissional = idProfissional;
        this.hrAtendimento = hrAtendimento;
    }
    
    public Long getIdDisponibilidade() {
        return idDisponibilidade;
    }
    
    public void setIdDisponibilidade(Long idDisponibilidade) {
        this.idDisponibilidade = idDisponibilidade;
    }
    
    public Long getIdProfissional() {
        return idProfissional;
    }
    
    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }
    
    public String getHrAtendimento() {
        return hrAtendimento;
    }
    
    public void setHrAtendimento(String hrAtendimento) {
        this.hrAtendimento = hrAtendimento;
    }
} 