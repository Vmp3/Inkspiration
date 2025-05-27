package inkspiration.backend.dto;

public class ImagemDTO {
    private Long idImagem;
    private String imagemBase64;
    private Long idPortifolio;
    
    public ImagemDTO() {}
    
    public ImagemDTO(Long idImagem, String imagemBase64, Long idPortifolio) {
        this.idImagem = idImagem;
        this.imagemBase64 = imagemBase64;
        this.idPortifolio = idPortifolio;
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
    
    public Long getIdPortifolio() {
        return idPortifolio;
    }
    
    public void setIdPortifolio(Long idPortifolio) {
        this.idPortifolio = idPortifolio;
    }
} 