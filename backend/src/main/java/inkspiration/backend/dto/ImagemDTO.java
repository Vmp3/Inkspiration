package inkspiration.backend.dto;

public class ImagemDTO {
    private Long idImagem;
    private String imagemBase64;
    private Long idPortfolio;
    
    public ImagemDTO() {}
    
    public ImagemDTO(Long idImagem, String imagemBase64, Long idPortfolio) {
        this.idImagem = idImagem;
        this.imagemBase64 = imagemBase64;
        this.idPortfolio = idPortfolio;
    }
    
    // Getters e Setters
    public Long getIdImagem() {
        return idImagem;
    }
    
    public void setIdImagem(Long idImagem) {
        this.idImagem = idImagem;
    }
    
    public String getImagemBase64() {
        return imagemBase64;
    }
    
    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }
    
    public Long getIdPortfolio() {
        return idPortfolio;
    }
    
    public void setIdPortfolio(Long idPortfolio) {
        this.idPortfolio = idPortfolio;
    }
} 