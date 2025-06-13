package inkspiration.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;

@Entity
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgendamento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Tipo de serviço é obrigatório")
    private TipoServico tipoServico;
    
    @Column(nullable = false, length = 500)
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @Column(nullable = false)
    private LocalDateTime dtInicio;
    
    @Column(nullable = false)
    private LocalDateTime dtFim;
    
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusAgendamento status = StatusAgendamento.AGENDADO;
    
    public Agendamento() {}
    
    public Long getIdAgendamento() {
        return idAgendamento;
    }
    
    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
    }
    
    public TipoServico getTipoServico() {
        return tipoServico;
    }
    
    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public LocalDateTime getDtInicio() {
        return dtInicio;
    }
    
    public void setDtInicio(LocalDateTime dtInicio) {
        this.dtInicio = dtInicio;
    }
    
    public LocalDateTime getDtFim() {
        return dtFim;
    }
    
    public void setDtFim(LocalDateTime dtFim) {
        this.dtFim = dtFim;
    }
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
} 