package inkspiration.backend.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AgendamentoRequestDTO {
    @NotBlank(message = "Tipo de serviço é obrigatório")
    private String tipoServico;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;
    
    @NotNull(message = "Data de início é obrigatória")
    private LocalDateTime dtInicio;
    
    @NotNull(message = "ID do profissional é obrigatório")
    private Long idProfissional;
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Long idUsuario;

    public AgendamentoRequestDTO() {}

    public AgendamentoRequestDTO(String tipoServico, String descricao, LocalDateTime dtInicio, 
                               Long idProfissional, Long idUsuario) {
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dtInicio = dtInicio;
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