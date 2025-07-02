package inkspiration.backend.dto;

import jakarta.validation.constraints.*;

public class AvaliacaoDTO {
    
    private Long idAvaliacao;
    
    @NotBlank(message = "Descrição não pode ser nula ou vazia")
    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres")
    private String descricao;
    
    @NotNull(message = "A avaliação é obrigatória")
    @Min(value = 1, message = "A avaliação deve ser entre 1 e 5 estrelas")
    @Max(value = 5, message = "A avaliação deve ser entre 1 e 5 estrelas")
    private Integer rating;
    
    @NotNull(message = "O ID do agendamento é obrigatório")
    private Long idAgendamento;
    
    // Campos adicionais para exibição pública das avaliações
    private String nomeCliente;
    private String imagemCliente;
    private String tipoServico;

    // Construtores
    public AvaliacaoDTO() {}

    public AvaliacaoDTO(String descricao, Integer rating, Long idAgendamento) {
        this.descricao = descricao;
        this.rating = rating;
        this.idAgendamento = idAgendamento;
    }

    public AvaliacaoDTO(Long idAvaliacao, String descricao, Integer rating, Long idAgendamento) {
        this.idAvaliacao = idAvaliacao;
        this.descricao = descricao;
        this.rating = rating;
        this.idAgendamento = idAgendamento;
    }

    // Getters e Setters
    public Long getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(Long idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
    }
    
    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getImagemCliente() {
        return imagemCliente;
    }

    public void setImagemCliente(String imagemCliente) {
        this.imagemCliente = imagemCliente;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }
} 