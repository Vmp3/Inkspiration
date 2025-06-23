package inkspiration.backend.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static boolean isValid(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }

        try {
            // First check if the string matches the expected format
            if (!dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return false;
            }

            // Parse the date to validate day and month values
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Validate day and month ranges
            if (day < 1 || day > 31) {
                return false;
            }
            if (month < 1 || month > 12) {
                return false;
            }

            // Additional validation for specific months
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
                return false;
            }
            if (month == 2) {
                // February validation
                if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
                    if (day > 29) return false;
                } else {
                    if (day > 28) return false;
                }
            }

            // Try to parse the date to ensure it's valid
            LocalDate date = LocalDate.parse(dateStr, FORMATTER);
            
            // Check if the date is in the past
            return date.isBefore(LocalDate.now());
        } catch (DateTimeParseException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String dateStr) {
        if (!isValid(dateStr)) {
            throw new IllegalArgumentException("Data inválida: " + dateStr);
        }
        return LocalDate.parse(dateStr, FORMATTER);
    }

    /**
     * Calcula a idade em anos a partir de uma data de nascimento
     * @param birthDate Data de nascimento
     * @return Idade em anos
     */
    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula");
        }
        return LocalDate.now().getYear() - birthDate.getYear() - 
               (LocalDate.now().getDayOfYear() < birthDate.getDayOfYear() ? 1 : 0);
    }

    /**
     * Calcula a idade em anos a partir de uma string de data de nascimento
     * @param birthDateStr Data de nascimento em formato dd/MM/yyyy
     * @return Idade em anos
     */
    public static int calculateAge(String birthDateStr) {
        LocalDate birthDate = parseDate(birthDateStr);
        return calculateAge(birthDate);
    }

    /**
     * Verifica se a pessoa tem pelo menos a idade mínima especificada
     * @param birthDateStr Data de nascimento em formato dd/MM/yyyy
     * @param minimumAge Idade mínima
     * @return true se tem a idade mínima, false caso contrário
     */
    public static boolean hasMinimumAge(String birthDateStr, int minimumAge) {
        return calculateAge(birthDateStr) >= minimumAge;
    }
} 