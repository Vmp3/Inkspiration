package inkspiration.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "imagem")
public class Imagem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagem")
    private Long idImagem;
    
    @Size(min = 10, message = "A imagem deve ter pelo menos 10 caracteres quando fornecida")
    @Pattern(regexp = "^data:image\\/(jpeg|jpg|png|gif|bmp|webp);base64,[A-Za-z0-9+/=]+$", 
             message = "Formato de imagem base64 inválido")
    @Column(name = "imagem_base64", columnDefinition = "TEXT")
    private String imagemBase64;
    
    @ManyToOne
    @JoinColumn(name = "portfolio_id_portfolio")
    private Portfolio portfolio;
    
    public Imagem() {}
    
    public Imagem(String imagemBase64, Portfolio portfolio) {
        this.setImagemBase64(imagemBase64);
        this.portfolio = portfolio;
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
        if (imagemBase64 != null) {
            String cleanImage = imagemBase64.trim();
            if (cleanImage.isEmpty()) {
                this.imagemBase64 = null;
                return;
            }
            if (cleanImage.length() < 10) {
                throw new IllegalArgumentException("A imagem deve ter pelo menos 10 caracteres quando fornecida");
            }
            if (!cleanImage.matches("^data:image\\/(jpeg|jpg|png|gif|bmp|webp);base64,[A-Za-z0-9+/=]+$")) {
                throw new IllegalArgumentException("Formato de imagem base64 inválido");
            }
            this.imagemBase64 = cleanImage;
        } else {
            this.imagemBase64 = null;
        }
    }
    
    public Portfolio getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
} 