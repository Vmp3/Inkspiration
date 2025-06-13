package inkspiration.backend.enums;

public enum StatusAgendamento {
    AGENDADO("Agendado"),
    CANCELADO("Cancelado"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusAgendamento fromDescricao(String descricao) {
        for (StatusAgendamento status : StatusAgendamento.values()) {
            if (status.getDescricao().equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + descricao);
    }
} 