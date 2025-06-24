package inkspiration.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendamentoUpdateDTO {
    @NotBlank(message = "Tipo de serviço é obrigatório")
    private String tipoServico;
    
    @NotBlank(message = "Descrição não pode ser nula ou vazia")
    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
    private String descricao;
    
    @NotNull(message = "Data de início é obrigatória")
    private LocalDateTime dtInicio;
    
    public AgendamentoUpdateDTO() {}

    public AgendamentoUpdateDTO(String tipoServico, String descricao, LocalDateTime dtInicio) {
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dtInicio = dtInicio;
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
} 