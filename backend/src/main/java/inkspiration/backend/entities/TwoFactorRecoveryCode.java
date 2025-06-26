package inkspiration.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "two_factor_recovery_codes")
public class TwoFactorRecoveryCode {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "O ID do usuário é obrigatório")
    @Positive(message = "O ID do usuário deve ser positivo")
    @Column(nullable = false)
    private Long userId;
    
    @NotBlank(message = "O código é obrigatório")
    @Size(min = 6, max = 6, message = "O código deve ter exatamente 6 caracteres")
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "O código deve conter apenas letras maiúsculas e números")
    @Column(nullable = false, length = 6)
    private String code;
    
    @NotNull(message = "A data de criação é obrigatória")
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @NotNull(message = "A data de expiração é obrigatória")
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    public TwoFactorRecoveryCode() {}
    
    public TwoFactorRecoveryCode(Long userId, String code, LocalDateTime expiresAt) {
        this.setUserId(userId);
        this.setCode(code);
        this.createdAt = LocalDateTime.now();
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("O ID do usuário não pode ser nulo");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }
        this.userId = userId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("O código não pode ser nulo ou vazio");
        }
        String cleanCode = code.trim().toUpperCase();
        if (!cleanCode.matches("^[A-Z0-9]{6}$")) {
            throw new IllegalArgumentException("O código deve ter exatamente 6 caracteres alfanuméricos");
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
        return !isUsed() && !isExpired();
    }
} 