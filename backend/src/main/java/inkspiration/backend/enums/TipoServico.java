package inkspiration.backend.enums;

public enum TipoServico {
    TATUAGEM_PEQUENA("tatuagem pequena"),
    TATUAGEM_MEDIA("tatuagem media"),
    TATUAGEM_GRANDE("tatuagem grande"),
    SESSAO("sessão");
    
    private final String descricao;
    
    TipoServico(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static TipoServico fromDescricao(String descricao) {
        for (TipoServico tipo : TipoServico.values()) {
            if (tipo.getDescricao().equalsIgnoreCase(descricao)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de serviço inválido: " + descricao);
    }
} 