package inkspiration.backend.util;

import java.util.regex.Pattern;

public class PasswordValidator {
    
    // Padrão para validação de senha:
    // - Pelo menos 8 caracteres
    // - Pelo menos uma letra maiúscula
    // - Pelo menos um número
    // - Pelo menos um caractere especial
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    /**
     * Valida se a senha atende aos critérios de segurança
     * @param password a senha a ser validada
     * @return true se a senha for válida, false caso contrário
     */
    public static boolean isValid(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        return pattern.matcher(password).matches();
    }
    
    /**
     * Retorna a mensagem de erro para senhas inválidas
     * @return mensagem descrevendo os critérios da senha
     */
    public static String getPasswordRequirements() {
        return "A senha deve ter no mínimo 8 caracteres, incluindo pelo menos uma letra maiúscula, um número e um caractere especial";
    }
} 