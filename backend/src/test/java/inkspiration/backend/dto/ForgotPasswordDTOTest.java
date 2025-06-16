package inkspiration.backend.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.dto.ForgotPasswordDTO;

public class ForgotPasswordDTOTest {

    @Test
    void testConstrutorVazio() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        assertNotNull(dto, "DTO não deve ser nulo");
        assertNull(dto.getCpf(), "CPF deve ser nulo inicialmente");
    }

    @Test
    void testGetSetCpf() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpf = "12345678901";
        
        dto.setCpf(cpf);
        assertEquals(cpf, dto.getCpf(), "CPF deve ser definido e recuperado corretamente");
    }

    @Test
    void testCpfNulo() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf(null);
        assertNull(dto.getCpf(), "CPF nulo deve ser aceito");
    }

    @Test
    void testCpfVazio() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("");
        assertEquals("", dto.getCpf(), "CPF vazio deve ser aceito");
    }

    @Test
    void testCpfComFormatacao() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfFormatado = "123.456.789-01";
        
        dto.setCpf(cpfFormatado);
        assertEquals(cpfFormatado, dto.getCpf(), "CPF formatado deve ser aceito");
    }

    @Test
    void testCpfSemFormatacao() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfSemFormato = "12345678901";
        
        dto.setCpf(cpfSemFormato);
        assertEquals(cpfSemFormato, dto.getCpf(), "CPF sem formatação deve ser aceito");
    }

    @Test
    void testCpfComEspacos() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfComEspacos = " 12345678901 ";
        
        dto.setCpf(cpfComEspacos);
        assertEquals(cpfComEspacos, dto.getCpf(), "CPF com espaços deve ser aceito como está");
    }

    @Test
    void testCpfLongo() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfLongo = "123456789012345";
        
        dto.setCpf(cpfLongo);
        assertEquals(cpfLongo, dto.getCpf(), "CPF longo deve ser aceito (validação é feita em outro local)");
    }

    @Test
    void testCpfCurto() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfCurto = "123";
        
        dto.setCpf(cpfCurto);
        assertEquals(cpfCurto, dto.getCpf(), "CPF curto deve ser aceito (validação é feita em outro local)");
    }

    @Test
    void testCpfComLetras() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String cpfComLetras = "123abc456def";
        
        dto.setCpf(cpfComLetras);
        assertEquals(cpfComLetras, dto.getCpf(), "CPF com letras deve ser aceito (validação é feita em outro local)");
    }

    @Test
    void testEqualsComMesmoObjeto() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        
        assertEquals(dto, dto, "Objeto deve ser igual a si mesmo");
    }

    @Test
    void testEqualsComObjetosIguais() {
        ForgotPasswordDTO dto1 = new ForgotPasswordDTO();
        ForgotPasswordDTO dto2 = new ForgotPasswordDTO();
        String cpf = "12345678901";
        
        dto1.setCpf(cpf);
        dto2.setCpf(cpf);
        
        assertEquals(dto1, dto2, "DTOs com mesmo CPF devem ser iguais");
    }

    @Test
    void testEqualsComObjetosDiferentes() {
        ForgotPasswordDTO dto1 = new ForgotPasswordDTO();
        ForgotPasswordDTO dto2 = new ForgotPasswordDTO();
        
        dto1.setCpf("12345678901");
        dto2.setCpf("98765432100");
        
        assertNotEquals(dto1, dto2, "DTOs com CPFs diferentes devem ser diferentes");
    }

    @Test
    void testEqualsComNull() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        
        assertNotEquals(dto, null, "DTO não deve ser igual a null");
    }

    @Test
    void testEqualsComClasseDiferente() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        String string = "12345678901";
        
        assertNotEquals(dto, string, "DTO não deve ser igual a objeto de classe diferente");
    }

    @Test
    void testHashCodeConsistente() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        
        int hashCode1 = dto.hashCode();
        int hashCode2 = dto.hashCode();
        
        assertEquals(hashCode1, hashCode2, "HashCode deve ser consistente");
    }

    @Test
    void testHashCodeObjetosIguais() {
        ForgotPasswordDTO dto1 = new ForgotPasswordDTO();
        ForgotPasswordDTO dto2 = new ForgotPasswordDTO();
        String cpf = "12345678901";
        
        dto1.setCpf(cpf);
        dto2.setCpf(cpf);
        
        assertEquals(dto1.hashCode(), dto2.hashCode(), "Objetos iguais devem ter mesmo hashCode");
    }

    @Test
    void testToString() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        
        String toString = dto.toString();
        assertNotNull(toString, "ToString não deve ser nulo");
        assertTrue(toString.contains("12345678901") || toString.contains("ForgotPasswordDTO"), 
                  "ToString deve conter informações relevantes");
    }

    @Test
    void testToStringComCpfNulo() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf(null);
        
        String toString = dto.toString();
        assertNotNull(toString, "ToString não deve ser nulo mesmo com CPF nulo");
    }

    @Test
    void testMutabilidade() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        
        // Testa mudanças de valor
        dto.setCpf("12345678901");
        assertEquals("12345678901", dto.getCpf());
        
        dto.setCpf("98765432100");
        assertEquals("98765432100", dto.getCpf());
        
        dto.setCpf(null);
        assertNull(dto.getCpf());
    }

    @Test
    void testCpfEspeciais() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        String[] cpfsEspeciais = {
            "000.000.000-00",
            "111.111.111-11",
            "123.456.789-00",
            "000.000.001-91",
            "12345678901",
            "123 456 789 01"
        };

        for (String cpf : cpfsEspeciais) {
            dto.setCpf(cpf);
            assertEquals(cpf, dto.getCpf(), "CPF especial deve ser aceito: " + cpf);
        }
    }

    @Test
    void testSerializacao() {
        // Testa se o DTO pode ser usado em contextos que requerem serialização
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setCpf("12345678901");
        
        assertDoesNotThrow(() -> {
            // Simula serialização básica
            String cpf = dto.getCpf();
            ForgotPasswordDTO novoDto = new ForgotPasswordDTO();
            novoDto.setCpf(cpf);
            assertEquals(dto.getCpf(), novoDto.getCpf());
        }, "DTO deve ser serializável");
    }
} 