package inkspiration.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class ProfissionalDTO {
    
    private Long idProfissional;
    
    @NotNull
    private Long idUsuario;
    
    @NotNull
    private Long idEndereco;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Digits(integer = 1, fraction = 1)
    private BigDecimal nota;
    
    public ProfissionalDTO() {}
    
    public ProfissionalDTO(Long idProfissional, Long idUsuario, Long idEndereco, 
                          BigDecimal nota) {
        this.idProfissional = idProfissional;
        this.idUsuario = idUsuario;
        this.idEndereco = idEndereco;
        this.nota = nota;
    }
    
    // Getters e Setters
    public Long getIdProfissional() {
        return idProfissional;
    }
    
    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Long getIdEndereco() {
        return idEndereco;
    }
    
    public void setIdEndereco(Long idEndereco) {
        this.idEndereco = idEndereco;
    }
    
    public BigDecimal getNota() {
        return nota;
    }
    
    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }
} 