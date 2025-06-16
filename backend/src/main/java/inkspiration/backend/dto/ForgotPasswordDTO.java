package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class ForgotPasswordDTO {
    
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    public ForgotPasswordDTO() {}

    public ForgotPasswordDTO(String cpf) {
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForgotPasswordDTO that = (ForgotPasswordDTO) o;
        return Objects.equals(cpf, that.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return "ForgotPasswordDTO{" +
                "cpf='" + cpf + '\'' +
                '}';
    }
} 