package inkspiration.backend.dto;

import java.time.LocalDateTime;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.TipoServico;

public class AgendamentoDTO {
    private Long idAgendamento;
    private TipoServico tipoServico;
    private String descricao;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    private Long idProfissional;
    private Long idUsuario;

    public AgendamentoDTO() {
    }
    
    public AgendamentoDTO(Agendamento agendamento) {
        this.idAgendamento = agendamento.getIdAgendamento();
        this.tipoServico = agendamento.getTipoServico();
        this.descricao = agendamento.getDescricao();
        this.dtInicio = agendamento.getDtInicio();
        this.dtFim = agendamento.getDtFim();
        this.idProfissional = agendamento.getProfissional().getIdProfissional();
        this.idUsuario = agendamento.getUsuario().getIdUsuario();
    }

    public AgendamentoDTO(Long idAgendamento, TipoServico tipoServico, String descricao, 
                         LocalDateTime dtInicio, LocalDateTime dtFim, Long idProfissional, Long idUsuario) {
        this.idAgendamento = idAgendamento;
        this.tipoServico = tipoServico;
        this.descricao = descricao;
        this.dtInicio = dtInicio;
        this.dtFim = dtFim;
        this.idProfissional = idProfissional;
        this.idUsuario = idUsuario;
    }

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