package inkspiration.backend.dto;

import inkspiration.backend.entities.Avaliacao;
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

    // Campo adicional para informações detalhadas do agendamento
    private AgendamentoInfo agendamento;

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

    // Construtor para converter da entidade
    public AvaliacaoDTO(Avaliacao avaliacao) {
        this.idAvaliacao = avaliacao.getIdAvaliacao();
        this.descricao = avaliacao.getDescricao();
        this.rating = avaliacao.getRating();
        this.idAgendamento = avaliacao.getAgendamento().getIdAgendamento();
        this.agendamento = new AgendamentoInfo(avaliacao.getAgendamento());
        // Se desejar, pode popular nomeCliente, imagemCliente e tipoServico aqui também
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

    public AgendamentoInfo getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(AgendamentoInfo agendamento) {
        this.agendamento = agendamento;
    }

    // Classe interna para informações do agendamento
    public static class AgendamentoInfo {
        private Long idAgendamento;
        private String dataHora;
        private String tipoTatuagem;
        private UsuarioInfo usuario;
        private ProfissionalInfo profissional;

        public AgendamentoInfo() {}

        public AgendamentoInfo(inkspiration.backend.entities.Agendamento agendamento) {
            this.idAgendamento = agendamento.getIdAgendamento();
            this.dataHora = agendamento.getDtInicio() != null ? agendamento.getDtInicio().toString() : null;
            this.tipoTatuagem = agendamento.getTipoServico() != null ? agendamento.getTipoServico().toString() : null;
            this.usuario = agendamento.getUsuario() != null ? new UsuarioInfo(agendamento.getUsuario()) : null;
            this.profissional = agendamento.getProfissional() != null ? new ProfissionalInfo(agendamento.getProfissional()) : null;
        }

        // Getters e Setters
        public Long getIdAgendamento() { return idAgendamento; }
        public void setIdAgendamento(Long idAgendamento) { this.idAgendamento = idAgendamento; }
        public String getDataHora() { return dataHora; }
        public void setDataHora(String dataHora) { this.dataHora = dataHora; }
        public String getTipoTatuagem() { return tipoTatuagem; }
        public void setTipoTatuagem(String tipoTatuagem) { this.tipoTatuagem = tipoTatuagem; }
        public UsuarioInfo getUsuario() { return usuario; }
        public void setUsuario(UsuarioInfo usuario) { this.usuario = usuario; }
        public ProfissionalInfo getProfissional() { return profissional; }
        public void setProfissional(ProfissionalInfo profissional) { this.profissional = profissional; }
    }

    // Classe interna para informações do usuário
    public static class UsuarioInfo {
        private Long idUsuario;
        private String nome;
        private String email;
        private String fotoPerfil;

        public UsuarioInfo() {}

        public UsuarioInfo(inkspiration.backend.entities.Usuario usuario) {
            this.idUsuario = usuario.getIdUsuario();
            this.nome = usuario.getNome();
            this.email = usuario.getEmail();
            this.fotoPerfil = usuario.getImagemPerfil();
        }

        // Getters e Setters
        public Long getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFotoPerfil() { return fotoPerfil; }
        public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    }

    // Classe interna para informações do profissional
    public static class ProfissionalInfo {
        private Long idProfissional;
        private String nome;
        private String email;

        public ProfissionalInfo() {}

        public ProfissionalInfo(inkspiration.backend.entities.Profissional profissional) {
            this.idProfissional = profissional.getIdProfissional();
            this.nome = profissional.getUsuario() != null ? profissional.getUsuario().getNome() : null;
            this.email = profissional.getUsuario() != null ? profissional.getUsuario().getEmail() : null;
        }

        // Getters e Setters
        public Long getIdProfissional() { return idProfissional; }
        public void setIdProfissional(Long idProfissional) { this.idProfissional = idProfissional; }
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
} 