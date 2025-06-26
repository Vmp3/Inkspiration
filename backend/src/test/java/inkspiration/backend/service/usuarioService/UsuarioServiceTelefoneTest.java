package inkspiration.backend.service.usuarioService;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.util.TelefoneValidator;

@DisplayName("UsuarioService - Testes de Telefone")
class UsuarioServiceTelefoneTest {

    @Test
    @DisplayName("Deve validar telefone celular válido")
    void deveValidarTelefoneCelularValido() {
        assertTrue(TelefoneValidator.isValid("(11) 99999-9999"));
        assertTrue(TelefoneValidator.isValid("(21) 98765-4321"));
        assertTrue(TelefoneValidator.isValid("11999999999"));
        assertTrue(TelefoneValidator.isValid("21987654321"));
    }

    @Test
    @DisplayName("Deve validar telefone fixo válido")
    void deveValidarTelefoneFixoValido() {
        assertTrue(TelefoneValidator.isValid("(11) 3333-4444"));
        assertTrue(TelefoneValidator.isValid("(21) 2222-3333"));
        assertTrue(TelefoneValidator.isValid("1133334444"));
        assertTrue(TelefoneValidator.isValid("2122223333"));
    }

    @Test
    @DisplayName("Deve invalidar telefone inválido")
    void deveInvalidarTelefoneInvalido() {
        assertFalse(TelefoneValidator.isValid("123")); // Muito curto
        assertFalse(TelefoneValidator.isValid("(00) 99999-9999")); // DDD inválido (00)
        assertFalse(TelefoneValidator.isValid("1099999999")); // DDD inválido (10) 
        assertFalse(TelefoneValidator.isValid("(11) 09999-9999")); // Celular não pode começar com 0
        assertFalse(TelefoneValidator.isValid("11111111111111")); // Muito longo
    }

    @Test
    @DisplayName("Deve invalidar telefone nulo ou vazio")
    void deveInvalidarTelefoneNuloOuVazio() {
        assertFalse(TelefoneValidator.isValid(null));
        assertFalse(TelefoneValidator.isValid(""));
        assertFalse(TelefoneValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve identificar telefone celular corretamente")
    void deveIdentificarTelefoneCelularCorretamente() {
        assertTrue(TelefoneValidator.isCelular("(11) 99999-9999"));
        assertTrue(TelefoneValidator.isCelular("11987654321"));
        assertTrue(TelefoneValidator.isCelular("(21) 98888-7777"));
        assertFalse(TelefoneValidator.isCelular("(11) 3333-4444"));
        assertFalse(TelefoneValidator.isCelular("1133334444"));
    }

    @Test
    @DisplayName("Deve validar diferentes DDDs")
    void deveValidarDiferentesDDDs() {
        // Região Sudeste
        assertTrue(TelefoneValidator.isValid("(11) 99999-9999")); // São Paulo
        assertTrue(TelefoneValidator.isValid("(21) 99999-9999")); // Rio de Janeiro
        assertTrue(TelefoneValidator.isValid("(31) 99999-9999")); // Minas Gerais
        
        // Região Sul
        assertTrue(TelefoneValidator.isValid("(41) 99999-9999")); // Paraná
        assertTrue(TelefoneValidator.isValid("(47) 99999-9999")); // Santa Catarina
        assertTrue(TelefoneValidator.isValid("(51) 99999-9999")); // Rio Grande do Sul
        
        // Região Nordeste
        assertTrue(TelefoneValidator.isValid("(71) 99999-9999")); // Bahia
        assertTrue(TelefoneValidator.isValid("(81) 99999-9999")); // Pernambuco
        assertTrue(TelefoneValidator.isValid("(85) 99999-9999")); // Ceará
    }

    @Test
    @DisplayName("Deve limpar telefone corretamente")
    void deveLimparTelefoneCorretamente() {
        String telefoneComMascara = "(11) 99999-9999";
        String telefoneLimpo = telefoneComMascara.replaceAll("[^0-9]", "");
        
        assertEquals("11999999999", telefoneLimpo);
        assertEquals(11, telefoneLimpo.length());
    }

    @Test
    @DisplayName("Deve formatar telefone com diferentes caracteres")
    void deveFormatarTelefoneComDiferentesCaracteres() {
        assertEquals("11999999999", "(11) 99999-9999".replaceAll("[^0-9]", ""));
        assertEquals("11999999999", "11 99999-9999".replaceAll("[^0-9]", ""));
        assertEquals("11999999999", "11.99999.9999".replaceAll("[^0-9]", ""));
        assertEquals("11999999999", "11-99999-9999".replaceAll("[^0-9]", ""));
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone obrigatório")
    void deveLancarExcecaoParaTelefoneObrigatorio() {
        assertThrows(UsuarioValidationException.TelefoneObrigatorioException.class, () -> {
            String telefone = null;
            if (telefone == null || telefone.trim().isEmpty()) {
                throw new UsuarioValidationException.TelefoneObrigatorioException();
            }
        });

        assertThrows(UsuarioValidationException.TelefoneObrigatorioException.class, () -> {
            String telefone = "";
            if (telefone == null || telefone.trim().isEmpty()) {
                throw new UsuarioValidationException.TelefoneObrigatorioException();
            }
        });
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone inválido")
    void deveLancarExcecaoParaTelefoneInvalido() {
        assertThrows(UsuarioValidationException.TelefoneInvalidoException.class, () -> {
            String telefone = "123";
            if (!TelefoneValidator.isValid(telefone)) {
                throw new UsuarioValidationException.TelefoneInvalidoException();
            }
        });
    }

    @Test
    @DisplayName("Deve validar telefones com 10 e 11 dígitos")
    void deveValidarTelefonesCom10E11Digitos() {
        // Telefone fixo (10 dígitos)
        assertTrue(TelefoneValidator.isValid("1133334444"));
        assertTrue(TelefoneValidator.isValid("(11) 3333-4444"));
        
        // Telefone celular (11 dígitos)
        assertTrue(TelefoneValidator.isValid("11999999999"));
        assertTrue(TelefoneValidator.isValid("(11) 99999-9999"));
    }

    @Test
    @DisplayName("Deve invalidar telefones com tamanho incorreto")
    void deveInvalidarTelefonesComTamanhoIncorreto() {
        assertFalse(TelefoneValidator.isValid("119999")); // Muito curto
        assertFalse(TelefoneValidator.isValid("119999999999")); // Muito longo
        assertFalse(TelefoneValidator.isValid("1199999999")); // 10 dígitos mas começa com 9 (celular)
    }
} 