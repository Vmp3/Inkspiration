package inkspiration.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Entity
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEndereco;
    
    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "^[0-9]{8}$", message = "CEP deve ter exatamente 8 dígitos")
    @Column(length = 8)
    private String cep;
    
    @NotBlank(message = "A rua é obrigatória")
    @Size(min = 3, max = 200, message = "A rua deve ter entre 3 e 200 caracteres")
    @Column(length = 200)
    private String rua;
    
    @NotBlank(message = "O bairro é obrigatório")
    @Size(min = 3, max = 100, message = "O bairro deve ter entre 3 e 100 caracteres")
    @Column(length = 100)
    private String bairro;
    
    @Size(max = 100, message = "O complemento não pode exceder 100 caracteres")
    @Column(length = 100)
    private String complemento;
    
    @NotBlank(message = "A cidade é obrigatória")
    @Size(min = 2, max = 100, message = "A cidade deve ter entre 2 e 100 caracteres")
    @Column(length = 100)
    private String cidade;
    
    @NotBlank(message = "O estado é obrigatório")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Estado deve ter exatamente 2 letras maiúsculas")
    @Column(length = 2)
    private String estado;
    
    @DecimalMin(value = "-90.0", message = "Latitude deve ser entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve ser entre -90 e 90")
    private Double latitude;
    
    @DecimalMin(value = "-180.0", message = "Longitude deve ser entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve ser entre -180 e 180")
    private Double longitude;
    
    @NotBlank(message = "O número é obrigatório")
    @Size(min = 1, max = 10, message = "O número deve ter entre 1 e 10 caracteres")
    @Column(length = 10)
    private String numero;
    
    public Endereco() {}
    
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
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("O CEP não pode ser nulo ou vazio");
        }
        String cleanCep = cep.replaceAll("[^0-9]", "");
        if (cleanCep.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter exatamente 8 dígitos");
        }
        this.cep = cleanCep;
    }
    
    public String getRua() {
        return rua;
    }
    
    public void setRua(String rua) {
        if (rua == null || rua.trim().isEmpty()) {
            throw new IllegalArgumentException("A rua não pode ser nula ou vazia");
        }
        String cleanRua = rua.trim();
        if (cleanRua.length() < 3 || cleanRua.length() > 200) {
            throw new IllegalArgumentException("A rua deve ter entre 3 e 200 caracteres");
        }
        this.rua = cleanRua;
    }
    
    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("O bairro não pode ser nulo ou vazio");
        }
        String cleanBairro = bairro.trim();
        if (cleanBairro.length() < 3 || cleanBairro.length() > 100) {
            throw new IllegalArgumentException("O bairro deve ter entre 3 e 100 caracteres");
        }
        this.bairro = cleanBairro;
    }
    
    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        if (complemento != null) {
            String cleanComplemento = complemento.trim();
            if (cleanComplemento.length() > 100) {
                throw new IllegalArgumentException("O complemento não pode exceder 100 caracteres");
            }
            this.complemento = cleanComplemento.isEmpty() ? null : cleanComplemento;
        } else {
            this.complemento = null;
        }
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("A cidade não pode ser nula ou vazia");
        }
        String cleanCidade = cidade.trim();
        if (cleanCidade.length() < 2 || cleanCidade.length() > 100) {
            throw new IllegalArgumentException("A cidade deve ter entre 2 e 100 caracteres");
        }
        this.cidade = cleanCidade;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("O estado não pode ser nulo ou vazio");
        }
        String cleanEstado = estado.trim().toUpperCase();
        if (!cleanEstado.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("Estado deve ter exatamente 2 letras maiúsculas");
        }
        this.estado = cleanEstado;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        if (latitude != null) {
            if (latitude < -90.0 || latitude > 90.0) {
                throw new IllegalArgumentException("Latitude deve ser entre -90 e 90");
            }
        }
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        if (longitude != null) {
            if (longitude < -180.0 || longitude > 180.0) {
                throw new IllegalArgumentException("Longitude deve ser entre -180 e 180");
            }
        }
        this.longitude = longitude;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("O número não pode ser nulo ou vazio");
        }
        String cleanNumero = numero.trim();
        if (cleanNumero.length() < 1 || cleanNumero.length() > 10) {
            throw new IllegalArgumentException("O número deve ter entre 1 e 10 caracteres");
        }
        this.numero = cleanNumero;
    }
} 