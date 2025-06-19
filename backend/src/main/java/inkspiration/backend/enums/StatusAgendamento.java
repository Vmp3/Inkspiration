package inkspiration.backend.enums;

public enum StatusAgendamento {
    AGENDADO("Agendado"),
    CANCELADO("Cancelado"),
    CONCLUIDO("Concluido");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusAgendamento fromDescricao(String descricao) {
        if (descricao == null) {
            throw new IllegalArgumentException("Status não pode ser nulo");
        }

        String statusNormalizado = descricao.toUpperCase().trim();

        for (StatusAgendamento status : StatusAgendamento.values()) {
            if (status.name().equals(statusNormalizado)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
            "Status inválido. Opções válidas: AGENDADO, CANCELADO, CONCLUIDO"
        );
    }
} 