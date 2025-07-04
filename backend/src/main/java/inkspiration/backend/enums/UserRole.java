package inkspiration.backend.enums;

public enum UserRole {
    ROLE_ADMIN("Administrador"),
    ROLE_USER("Usuário"),
    ROLE_PROF("Profissional"),
    ROLE_DELETED("Usuário Deletado");

    private final String descricao;

    UserRole(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getRole() {
        return this.name();
    }

    public static UserRole fromString(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role não pode ser nula");
        }

        String roleNormalizada = role.toUpperCase().trim();
        
        if (!roleNormalizada.startsWith("ROLE_")) {
            roleNormalizada = "ROLE_" + roleNormalizada;
        }

        try {
            return UserRole.valueOf(roleNormalizada);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role inválida. Valores válidos: ADMIN, USER, PROF, DELETED");
        }
    }
} 