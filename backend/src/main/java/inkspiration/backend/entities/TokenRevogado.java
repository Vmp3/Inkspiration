package inkspiration.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class TokenRevogado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "O token é obrigatório")
    @Size(min = 10, max = 1000, message = "O token deve ter entre 10 e 1000 caracteres")
    @Column(length = 1000, nullable = false)
    private String token;
    
    @NotNull(message = "A data de revogação é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataRevogacao;

    public TokenRevogado() {}

    public TokenRevogado(String token) {
        this.setToken(token);
        this.dataRevogacao = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("O token não pode ser nulo ou vazio");
        }
        String cleanToken = token.trim();
        if (cleanToken.length() < 10) {
            throw new IllegalArgumentException("O token deve ter pelo menos 10 caracteres");
        }
        if (cleanToken.length() > 1000) {
            throw new IllegalArgumentException("O token não pode exceder 1000 caracteres");
        }
        this.token = cleanToken;
    }

    public LocalDateTime getDataRevogacao() {
        return dataRevogacao;
    }

    public void setDataRevogacao(LocalDateTime dataRevogacao) {
        if (dataRevogacao == null) {
            throw new IllegalArgumentException("A data de revogação não pode ser nula");
        }
        this.dataRevogacao = dataRevogacao;
    }
    
    public boolean isExpired(int horasExpiracao) {
        if (horasExpiracao <= 0) {
            return false;
        }
        return LocalDateTime.now().isAfter(dataRevogacao.plusHours(horasExpiracao));
    }
} 