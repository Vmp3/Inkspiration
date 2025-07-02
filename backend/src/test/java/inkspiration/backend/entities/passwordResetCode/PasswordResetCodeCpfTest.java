package inkspiration.backend.entities.passwordResetCode;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.PasswordResetCode;

@DisplayName("Testes de validação de CPF - PasswordResetCode")
public class PasswordResetCodeCpfTest {

    private PasswordResetCode passwordResetCode;

    @BeforeEach
    void setUp() {
        passwordResetCode = new PasswordResetCode();
    }

    @Test
    @DisplayName("Deve aceitar CPF válido com 11 dígitos")
    void deveAceitarCpfValidoCom11Digitos() {
        String cpf = "12345678901";
        passwordResetCode.setCpf(cpf);
        assertEquals(cpf, passwordResetCode.getCpf());
    }

    @Test
    @DisplayName("Deve limpar formatação do CPF")
    void deveLimparFormatacaoDoCpf() {
        passwordResetCode.setCpf("123.456.789-01");
        assertEquals("12345678901", passwordResetCode.getCpf());
    }

    @Test
    @DisplayName("Deve remover espaços do CPF")
    void deveRemoverEspacosDoCpf() {
        passwordResetCode.setCpf("123 456 789 01");
        assertEquals("12345678901", passwordResetCode.getCpf());
    }

    @Test
    @DisplayName("Deve remover caracteres especiais do CPF")
    void deveRemoverCaracteresEspeciaisDoCpf() {
        passwordResetCode.setCpf("123@456#789$01");
        assertEquals("12345678901", passwordResetCode.getCpf());
    }

    @Test
    @DisplayName("Não deve aceitar CPF nulo")
    void naoDeveAceitarCpfNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCpf(null);
        });
        assertEquals("O CPF não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CPF com menos de 11 dígitos")
    void naoDeveAceitarCpfComMenosDe11Digitos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCpf("1234567890");
        });
        assertEquals("CPF deve ter exatamente 11 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CPF com mais de 11 dígitos")
    void naoDeveAceitarCpfComMaisDe11Digitos() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCpf("123456789012");
        });
        assertEquals("CPF deve ter exatamente 11 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CPF apenas com letras")
    void naoDeveAceitarCpfApenasComLetras() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCpf("abcdefghijk");
        });
        assertEquals("CPF deve ter exatamente 11 dígitos", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar CPF vazio")
    void naoDeveAceitarCpfVazio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetCode.setCpf("");
        });
        assertEquals("CPF deve ter exatamente 11 dígitos", exception.getMessage());
    }
} 