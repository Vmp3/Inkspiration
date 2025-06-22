package inkspiration.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendamentoRequestDTO {
    @NotBlank(message = "Tipo de serviço é obrigatório")
    private String tipoServico;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 20, max = 500, message = "Descrição deve ter entre 20 e 500 caracteres")
    private String descricao;
    
    @NotNull(message = "Data de início é obrigatória")
    private LocalDateTime dtInicio;
    
    private BigDecimal valor;
    
    @NotNull(message = "ID do profissional é obrigatório")
    private Long idProfissional;
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;

    public AgendamentoRequestDTO() {}

    public AgendamentoRequestDTO(String tipoServico, String descricao, LocalDateTime dtInicio, 
                               BigDecimal valor, Long idProfissional, Long idUsuario) {
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dtInicio = dtInicio;
        this.valor = valor;
        this.idProfissional = idProfissional;
        this.idUsuario = idUsuario;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
} 