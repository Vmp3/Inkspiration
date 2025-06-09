package inkspiration.backend.dto;

import jakarta.validation.constraints.NotBlank;

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
} 