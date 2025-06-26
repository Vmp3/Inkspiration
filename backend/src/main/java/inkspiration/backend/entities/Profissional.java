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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

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
    
    @NotNull(message = "O usuário é obrigatório")
    @OneToOne
    @JoinColumn(name = "usuario_idUsuario")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "endereco_idEndereco")
    private Endereco endereco;
    
    @OneToOne
    @JoinColumn(name = "portfolio_idPortfolio")
    private Portfolio portfolio;
    
    @DecimalMin(value = "0.0", message = "A nota deve ser maior ou igual a 0")
    @DecimalMax(value = "5.0", message = "A nota deve ser menor ou igual a 5")
    @Column(precision = 3, scale = 1)
    private BigDecimal nota;
    
    @Size(max = 1000, message = "Os tipos de serviço não podem exceder 1000 caracteres")
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
        if (usuario == null) {
            throw new IllegalArgumentException("O usuário não pode ser nulo");
        }
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
        if (nota != null) {
            if (nota.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("A nota deve ser maior ou igual a 0");
            }
            if (nota.compareTo(new BigDecimal("5.0")) > 0) {
                throw new IllegalArgumentException("A nota deve ser menor ou igual a 5");
            }
        }
        this.nota = nota;
    }
    
    public String getTiposServicoStr() {
        return tiposServicoStr;
    }
    
    public void setTiposServicoStr(String tiposServicoStr) {
        if (tiposServicoStr != null && tiposServicoStr.length() > 1000) {
            throw new IllegalArgumentException("Os tipos de serviço não podem exceder 1000 caracteres");
        }
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