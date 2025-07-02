package inkspiration.backend.entities;

import java.math.BigDecimal;
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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.groups.Default;

import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;

@Entity
public class Agendamento {
    
    // Grupos de validação
    public interface OnCreate extends Default {}
    public interface OnUpdate {}
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgendamento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Tipo de serviço é obrigatório")
    private TipoServico tipoServico;
    
    @Column(length = 500)
    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
    @NotBlank(message = "Descrição não pode ser nula ou vazia")
    private String descricao;
    
    @Column(nullable = false)
    @NotNull(message = "Data de início é obrigatória")
    @Future(message = "Data de início deve ser no futuro", groups = OnCreate.class)
    private LocalDateTime dtInicio;
    
    @Column(nullable = false)
    @NotNull(message = "Data de fim é obrigatória")
    private LocalDateTime dtFim;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor deve ser maior que zero")
    @DecimalMax(value = "999999.99", message = "Valor não pode exceder R$ 999.999,99")
    @Column(precision = 10, scale = 2)
    private BigDecimal valor;
    
    @NotNull(message = "Profissional é obrigatório")
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;
    
    @NotNull(message = "Usuário é obrigatório")
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
        if (tipoServico == null) {
            throw new IllegalArgumentException("Tipo de serviço não pode ser nulo");
        }
        this.tipoServico = tipoServico;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
        String cleanDescricao = descricao.trim();
        if (cleanDescricao.length() > 500) {
            throw new IllegalArgumentException("A descrição não pode exceder 500 caracteres");
        }
        this.descricao = cleanDescricao;
    }
    
    public LocalDateTime getDtInicio() {
        return dtInicio;
    }
    
    public void setDtInicio(LocalDateTime dtInicio) {
        if (dtInicio == null) {
            throw new IllegalArgumentException("Data de início não pode ser nula");
        }
        this.dtInicio = dtInicio;
    }
    
    // Método específico para definir data com validação de futuro (usado na criação)
    public void setDtInicioWithFutureValidation(LocalDateTime dtInicio) {
        if (dtInicio == null) {
            throw new IllegalArgumentException("Data de início não pode ser nula");
        }
        if (dtInicio.isBefore(LocalDateTime.now()) || dtInicio.isEqual(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data de início deve ser no futuro");
        }
        this.dtInicio = dtInicio;
    }
    
    public LocalDateTime getDtFim() {
        return dtFim;
    }
    
    public void setDtFim(LocalDateTime dtFim) {
        if (dtFim == null) {
            throw new IllegalArgumentException("Data de fim não pode ser nula");
        }
        if (dtInicio != null && dtFim.isBefore(dtInicio)) {
            throw new IllegalArgumentException("Data de fim deve ser posterior à data de início");
        }
        if (dtInicio != null && dtFim.equals(dtInicio)) {
            throw new IllegalArgumentException("Data de fim deve ser diferente da data de início");
        }
        this.dtFim = dtFim;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        if (valor != null) {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero quando fornecido");
            }
            if (valor.compareTo(new BigDecimal("999999.99")) > 0) {
                throw new IllegalArgumentException("Valor não pode exceder R$ 999.999,99");
            }
        }
        this.valor = valor;
    }
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        if (profissional == null) {
            throw new IllegalArgumentException("Profissional não pode ser nulo");
        }
        this.profissional = profissional;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo");
        }
        this.usuario = usuario;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }
        this.status = status;
    }
} 