package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EnderecoDTO {
    
    private Long idEndereco;
    
    @NotBlank
    @Size(max = 10)
    private String cep;
    
    @NotBlank
    @Size(max = 255)
    private String rua;
    
    @NotBlank
    @Size(max = 100)
    private String bairro;
    
    @Size(max = 255)
    private String complemento;
    
    @NotBlank
    @Size(max = 100)
    private String cidade;
    
    @NotBlank
    @Size(max = 50)
    private String estado;
    
    private Double latitude;
    private Double longitude;
    
    @NotBlank
    @Size(max = 20)
    private String numero;
    
    public EnderecoDTO() {}
    
    public EnderecoDTO(Long idEndereco, String cep, String rua, String bairro, String complemento,
                      String cidade, String estado, Double latitude, Double longitude, String numero) {
        this.idEndereco = idEndereco;
        this.cep = cep;
        this.rua = rua;
        this.bairro = bairro;
        this.complemento = complemento;
        this.cidade = cidade;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numero = numero;
    }
    
    // Getters e Setters
    public Long getIdEndereco() {
        return idEndereco;
    }
    
    public void setIdEndereco(Long idEndereco) {
        this.idEndereco = idEndereco;
    }
    
    public String getCep() {
        return cep;
    }
    
    public void setCep(String cep) {
        this.cep = cep;
    }
    
    public String getRua() {
        return rua;
    }
    
    public void setRua(String rua) {
        this.rua = rua;
    }
    
    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    
    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
} 