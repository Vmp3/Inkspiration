package inkspiration.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import inkspiration.backend.enums.TipoServico;

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
    
    @Column(name = "tipos_servico", length = 500)
    private String tiposServicoStr;
    
    @Transient
    private List<TipoServico> tiposServico = new ArrayList<>();
    
    @PostLoad
    private void onLoad() {
        if (tiposServicoStr != null && !tiposServicoStr.isEmpty()) {
            tiposServico = Arrays.stream(tiposServicoStr.split(","))
                    .map(TipoServico::valueOf)
                    .collect(Collectors.toList());
        }
    }
    
    @PrePersist
    @PreUpdate
    private void onSave() {
        if (tiposServico != null && !tiposServico.isEmpty()) {
            tiposServicoStr = tiposServico.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(","));
        } else {
            tiposServicoStr = "";
        }
    }
    
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
    
    public List<TipoServico> getTiposServico() {
        return tiposServico;
    }
    
    public void setTiposServico(List<TipoServico> tiposServico) {
        this.tiposServico = tiposServico != null ? tiposServico : new ArrayList<>();
    }
    
    public String getTiposServicoStr() {
        return tiposServicoStr;
    }
    
    public void setTiposServicoStr(String tiposServicoStr) {
        this.tiposServicoStr = tiposServicoStr;
    }
} 