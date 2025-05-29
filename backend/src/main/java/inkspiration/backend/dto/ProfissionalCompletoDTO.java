package inkspiration.backend.dto;

import java.math.BigDecimal;

public class ProfissionalCompletoDTO {
    private Long idProfissional;
    private BigDecimal nota;
    
    private UsuarioSeguroDTO usuario;
    
    private EnderecoDTO endereco;
    
    private PortifolioDTO portifolio;
    
    public ProfissionalCompletoDTO() {
    }
    
    public ProfissionalCompletoDTO(Long idProfissional, BigDecimal nota, 
                                  UsuarioSeguroDTO usuario, EnderecoDTO endereco, 
                                  PortifolioDTO portifolio) {
        this.idProfissional = idProfissional;
        this.nota = nota;
        this.usuario = usuario;
        this.endereco = endereco;
        this.portifolio = portifolio;
    }

    public Long getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(Long idProfissional) {
        this.idProfissional = idProfissional;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public UsuarioSeguroDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSeguroDTO usuario) {
        this.usuario = usuario;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    public PortifolioDTO getPortifolio() {
        return portifolio;
    }

    public void setPortifolio(PortifolioDTO portifolio) {
        this.portifolio = portifolio;
    }
} 