package inkspiration.backend.dto;

public class AvaliacaoRequestDTO {
    private Long idAgendamento;
    private String descricao;
    private Integer rating;

    public AvaliacaoRequestDTO() {}

    public AvaliacaoRequestDTO(Long idAgendamento, String descricao, Integer rating) {
        this.idAgendamento = idAgendamento;
        this.descricao = descricao;
        this.rating = rating;
    }

    // Getters e Setters
    public Long getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
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
} 