package inkspiration.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "password_reset_code")
public class PasswordResetCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve ter exatamente 11 dígitos")
    @Column(nullable = false)
    private String cpf;
    
    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 8, message = "O código deve ter entre 6 e 8 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{6,8}$", message = "O código deve conter apenas letras maiúsculas e números")
    @Column(nullable = false)
    private String code;
    
    @NotNull(message = "A data de criação é obrigatória")
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @NotNull(message = "A data de expiração é obrigatória")
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;

    public PasswordResetCode() {}

    public PasswordResetCode(String cpf, String code, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.setCpf(cpf);
        this.setCode(code);
        this.setCreatedAt(createdAt);
        this.setExpiresAt(expiresAt);
        this.used = false;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf != null) {
            String cleanCpf = cpf.replaceAll("[^0-9]", "");
            if (cleanCpf.length() != 11) {
                throw new IllegalArgumentException("CPF deve ter exatamente 11 dígitos");
            }
            this.cpf = cleanCpf;
        } else {
            throw new IllegalArgumentException("O CPF não pode ser nulo");
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("O código não pode ser nulo ou vazio");
        }
        String cleanCode = code.trim().toUpperCase();
        if (cleanCode.length() < 6 || cleanCode.length() > 8) {
            throw new IllegalArgumentException("O código deve ter entre 6 e 8 caracteres");
        }
        if (!cleanCode.matches("^[A-Z0-9]{6,8}$")) {
            throw new IllegalArgumentException("O código deve conter apenas letras maiúsculas e números");
        }
        this.code = cleanCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("A data de criação não pode ser nula");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            throw new IllegalArgumentException("A data de expiração não pode ser nula");
        }
        if (createdAt != null && (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt))) {
            throw new IllegalArgumentException("A data de expiração deve ser posterior à data de criação");
        }
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
} 