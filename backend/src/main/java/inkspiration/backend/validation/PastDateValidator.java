package inkspiration.backend.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PastDateValidator implements ConstraintValidator<PastDate, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(PastDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        try {
            LocalDate date = LocalDate.parse(value, FORMATTER);
            return date.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }
} 