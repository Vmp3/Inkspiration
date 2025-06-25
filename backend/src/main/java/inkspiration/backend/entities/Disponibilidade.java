package inkspiration.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Entity
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDisponibilidade;
    
    @Size(max = 5000, message = "Horários de atendimento não podem exceder 5000 caracteres")
    @Pattern(regexp = "^[\\s\\S]*$", message = "Formato de horários inválido")
    @Column(columnDefinition = "TEXT")
    private String hrAtendimento;
    
    @NotNull(message = "O profissional é obrigatório")
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
        if (hrAtendimento != null) {
            String cleanHorarios = hrAtendimento.trim();
            if (cleanHorarios.isEmpty()) {
                this.hrAtendimento = null;
                return;
            }
            if (cleanHorarios.length() > 5000) {
                throw new IllegalArgumentException("Horários de atendimento não podem exceder 5000 caracteres");
            }
            this.hrAtendimento = cleanHorarios;
        } else {
            this.hrAtendimento = null;
        }
    }
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        if (profissional == null) {
            throw new IllegalArgumentException("O profissional não pode ser nulo");
        }
        this.profissional = profissional;
    }
} 