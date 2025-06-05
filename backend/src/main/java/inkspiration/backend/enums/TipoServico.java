package inkspiration.backend.enums;

public enum TipoServico {
    TATUAGEM_PEQUENA("tatuagem pequena", 2),
    TATUAGEM_MEDIA("tatuagem media", 4),
    TATUAGEM_GRANDE("tatuagem grande", 6),
    SESSAO("sessão", 8);
    
    private final String descricao;
    private final int duracaoHoras;
    
    TipoServico(String descricao, int duracaoHoras) {
        this.descricao = descricao;
        this.duracaoHoras = duracaoHoras;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public int getDuracaoHoras() {
        return duracaoHoras;
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