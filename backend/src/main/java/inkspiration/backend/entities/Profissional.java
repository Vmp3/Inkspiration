package inkspiration.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
public class Profissional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfissional;
    
    @OneToOne
    @JoinColumn(name = "usuario_idUsuario")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "endereco_idEndereco")
    private Endereco endereco;
    
    @OneToOne
    @JoinColumn(name = "portifolio_idPortifolio")
    private Portifolio portifolio;
    
    @Column(precision = 3, scale = 1)
    private BigDecimal nota;
    
    public Profissional() {}
    
    // Getters e Setters
    public Long getIdProfissional() {
        return idProfissional;
    }
    
    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Endereco getEndereco() {
        return endereco;
    }
    
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    
    public Portifolio getPortifolio() {
        return portifolio;
    }
    
    public void setPortifolio(Portifolio portifolio) {
        this.portifolio = portifolio;
    }
    
    public BigDecimal getNota() {
        return nota;
    }
    
    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
} 