package inkspiration.backend.util;

import java.util.regex.Pattern;

public class TelefoneValidator {
    
    // Regex que aceita celular (com 9) e telefone fixo
    private static final String TELEFONE_REGEX = "^\\(?[1-9]{2}\\)?\\s?(?:[2-8]|9[1-9])[0-9]{3}\\s?\\-?[0-9]{4}$";
    private static final Pattern TELEFONE_PATTERN = Pattern.compile(TELEFONE_REGEX);
    
    // Regex específico para celular (deve ter 9 no início)
    private static final String CELULAR_REGEX = "^\\(?[1-9]{2}\\)?\\s?9[1-9][0-9]{3}\\s?\\-?[0-9]{4}$";
    private static final Pattern CELULAR_PATTERN = Pattern.compile(CELULAR_REGEX);
    
    public static boolean isValid(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        
        // Remove espaços e caracteres especiais para validação
        String cleanTelefone = telefone.trim();
        return TELEFONE_PATTERN.matcher(cleanTelefone).matches();
    }
    
    public static boolean isCelular(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        
        String cleanTelefone = telefone.trim();
        return CELULAR_PATTERN.matcher(cleanTelefone).matches();
    }
    
    public static String format(String telefone) {
        if (telefone == null) {
            return null;
        }
        
        // Remove tudo que não é número
        String numeros = telefone.replaceAll("[^0-9]", "");
        
        if (numeros.length() == 11) {
            // Formato celular: (99) 99999-9999
            return String.format("(%s) %s-%s", 
                numeros.substring(0, 2), 
                numeros.substring(2, 7), 
                numeros.substring(7));
        } else if (numeros.length() == 10) {
            // Formato fixo: (99) 9999-9999
            return String.format("(%s) %s-%s", 
                numeros.substring(0, 2), 
                numeros.substring(2, 6), 
                numeros.substring(6));
        }
        
        return telefone; // Retorna original se não conseguir formatar
    }
    
    public static String getFormatExample() {
        return "Para celulares: (11) 91234-5678 ou 11912345678\n" +
               "Para telefones fixos: (11) 1234-5678 ou 1112345678";
    }
    
    public static String getValidationMessage(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return "Telefone é obrigatório";
        }
        
        if (!isValid(telefone)) {
            return "Telefone inválido. Use o formato (99) 99999-9999.";
        }
        
        return null; // Válido
    }
} 