package inkspiration.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAvaliacao;
    
    @Column(nullable = false, length = 500)
    private String descricao;
    
    @Column(nullable = false)
    private Integer rating;
    
    @OneToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    // Construtores
    public Avaliacao() {}

    public Avaliacao(String descricao, Integer rating, Agendamento agendamento) {
        this.descricao = descricao;
        this.rating = rating;
        this.agendamento = agendamento;
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

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Avaliacao avaliacao = (Avaliacao) o;

        return idAvaliacao != null && idAvaliacao.equals(avaliacao.idAvaliacao);
    }

    @Override
    public int hashCode() {
        return idAvaliacao != null ? idAvaliacao.hashCode() : 0;
    }
} 