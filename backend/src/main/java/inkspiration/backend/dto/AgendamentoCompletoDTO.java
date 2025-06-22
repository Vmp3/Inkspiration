package inkspiration.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;

public class AgendamentoCompletoDTO {
    private Long idAgendamento;
    private TipoServico tipoServico;
    private String descricao;
    private LocalDateTime dtInicio;
    private LocalDateTime dtFim;
    private BigDecimal valor;
    private StatusAgendamento status;
    
    // Informações do profissional
    private Long idProfissional;
    private String nomeProfissional;
    
    private Long idUsuario;
    private String nomeUsuario;
    
    // Informações do endereço
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String complemento;
    
    public AgendamentoCompletoDTO(Agendamento agendamento) {
        this.idAgendamento = agendamento.getIdAgendamento();
        this.tipoServico = agendamento.getTipoServico();
        this.descricao = agendamento.getDescricao();
        this.dtInicio = agendamento.getDtInicio();
        this.dtFim = agendamento.getDtFim();
        this.valor = agendamento.getValor();
        this.status = agendamento.getStatus();
        
        if (agendamento.getProfissional() != null) {
            this.idProfissional = agendamento.getProfissional().getIdProfissional();
            if (agendamento.getProfissional().getUsuario() != null) {
                this.nomeProfissional = agendamento.getProfissional().getUsuario().getNome();
            }
            if (agendamento.getProfissional().getEndereco() != null) {
                this.rua = agendamento.getProfissional().getEndereco().getRua();
                this.numero = agendamento.getProfissional().getEndereco().getNumero();
                this.bairro = agendamento.getProfissional().getEndereco().getBairro();
                this.cidade = agendamento.getProfissional().getEndereco().getCidade();
                this.estado = agendamento.getProfissional().getEndereco().getEstado();
                this.cep = agendamento.getProfissional().getEndereco().getCep();
                this.complemento = agendamento.getProfissional().getEndereco().getComplemento();
            }
        }
        
        if (agendamento.getUsuario() != null) {
            this.idUsuario = agendamento.getUsuario().getIdUsuario();
            this.nomeUsuario = agendamento.getUsuario().getNome();
        }
    }

    // Getters e Setters
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public Long getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
} 