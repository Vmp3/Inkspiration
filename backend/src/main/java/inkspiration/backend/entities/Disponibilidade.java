package inkspiration.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilidade;
    
    @Column(columnDefinition = "TEXT")
    private String hrAtendimento;
    
    @OneToOne
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;
    
    public Disponibilidade() {}
    
    // Getters e Setters
    public Long getIdDisponibilidade() {
        return idDisponibilidade;
    }
    
    public void setIdDisponibilidade(Long idDisponibilidade) {
        this.idDisponibilidade = idDisponibilidade;
    }
    
    public String getHrAtendimento() {
        return hrAtendimento;
    }
    
    public void setHrAtendimento(String hrAtendimento) {
        this.hrAtendimento = hrAtendimento;
    }
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }
} 