package inkspiration.backend.util;

public class CpfValidator {
    
    public static boolean isValid(String cpf) {
        // Remove non-numeric characters
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Check if length is 11
        if (cpf.length() != 11) {
            return false;
        }
        
        // Check if all digits are the same
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calculate first verification digit
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        
        // Calculate second verification digit
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;
        
        // Check if verification digits match
        return (cpf.charAt(9) - '0' == firstDigit) && 
               (cpf.charAt(10) - '0' == secondDigit);
    }
    
    public static String format(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9);
    }
} 