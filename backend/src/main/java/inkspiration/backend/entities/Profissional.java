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

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
    @JoinColumn(name = "portfolio_idPortfolio")
    private Portfolio portfolio;
    
    @Column(precision = 3, scale = 1)
    private BigDecimal nota;
    
    @Column(name = "tipos_servico", length = 1000)
    private String tiposServicoStr;
    
    @Transient
    private Map<String, BigDecimal> tiposServicoPrecos = new HashMap<>();
    
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
    
    public Portfolio getPortfolio() {
        return portfolio;
    }
    
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    
    public BigDecimal getNota() {
        return nota;
    }
    
    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
    
    public String getTiposServicoStr() {
        return tiposServicoStr;
    }
    
    public void setTiposServicoStr(String tiposServicoStr) {
        this.tiposServicoStr = tiposServicoStr;
    }
    
    public Map<String, BigDecimal> getTiposServicoPrecos() {
        return tiposServicoPrecos;
    }
    
    public void setTiposServicoPrecos(Map<String, BigDecimal> tiposServicoPrecos) {
        this.tiposServicoPrecos = tiposServicoPrecos != null ? tiposServicoPrecos : new HashMap<>();
    }
    
    // Métodos de compatibilidade para obter tipos de serviço como lista
    public List<TipoServico> getTiposServico() {
        if (tiposServicoPrecos == null || tiposServicoPrecos.isEmpty()) {
            return new ArrayList<>();
        }
        return tiposServicoPrecos.keySet().stream()
                .map(TipoServico::valueOf)
                .collect(Collectors.toList());
    }
    
    // Método para obter apenas os preços
    public Map<String, BigDecimal> getPrecosServicos() {
        return new HashMap<>(tiposServicoPrecos != null ? tiposServicoPrecos : new HashMap<>());
    }
} 