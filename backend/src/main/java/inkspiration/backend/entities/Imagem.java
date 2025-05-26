package inkspiration.backend.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "imagem")
public class Imagem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagem")
    private Long idImagem;
    
    @Column(name = "imagem_base64", columnDefinition = "TEXT")
    private String imagemBase64;
    
    @ManyToOne
    @JoinColumn(name = "portifolio_id_portifolio")
    private Portifolio portifolio;
    
    public Imagem() {}
    
    public Imagem(String imagemBase64, Portifolio portifolio) {
        this.imagemBase64 = imagemBase64;
        this.portifolio = portifolio;
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
    
    public Portifolio getPortifolio() {
        return portifolio;
    }
    
    public void setPortifolio(Portifolio portifolio) {
        this.portifolio = portifolio;
    }
} 