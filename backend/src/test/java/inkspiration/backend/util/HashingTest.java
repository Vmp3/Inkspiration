package inkspiration.backend.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.Hashing;

public class HashingTest {

    @Test
    void testHashingSenhasIguais() {
        String senha = "minhasenha123";
        String hash1 = Hashing.hash(senha);
        String hash2 = Hashing.hash(senha);
        
        // Hashes podem ser diferentes devido ao salt, mas devem verificar corretamente
        assertTrue(Hashing.matches(senha, hash1), "Senha deveria ser verificada corretamente com seu hash");
        assertTrue(Hashing.matches(senha, hash2), "Senha deveria ser verificada corretamente com seu hash");
    }

    @Test
    void testHashingSenhasDiferentes() {
        String senha1 = "senha123";
        String senha2 = "senha456";
        
        String hash1 = Hashing.hash(senha1);
        String hash2 = Hashing.hash(senha2);
        
        assertFalse(Hashing.matches(senha1, hash2), "Senha1 não deveria verificar com hash da senha2");
        assertFalse(Hashing.matches(senha2, hash1), "Senha2 não deveria verificar com hash da senha1");
    }

    @Test
    void testHashingNaoNulo() {
        String senha = "teste123";
        String hash = Hashing.hash(senha);
        
        assertNotNull(hash, "Hash não deve ser nulo");
        assertFalse(hash.isEmpty(), "Hash não deve estar vazio");
        assertTrue(hash.length() > 0, "Hash deve ter comprimento maior que zero");
    }

    @Test
    void testHashingSenhaVazia() {
        String senhaVazia = "";
        
        assertDoesNotThrow(() -> Hashing.hash(senhaVazia), 
                          "Hash de senha vazia não deve lançar exceção");
        
        String hash = Hashing.hash(senhaVazia);
        assertTrue(Hashing.matches(senhaVazia, hash), 
                  "Senha vazia deve verificar com seu próprio hash");
    }

    @Test
    void testHashingSenhaNula() {
        assertThrows(Exception.class, () -> Hashing.hash(null), 
                    "Hash de senha nula deve lançar exceção");
    }

    @Test
    void testMatchesSenhaNula() {
        String hash = Hashing.hash("teste");
        
        assertThrows(Exception.class, () -> Hashing.matches(null, hash), 
                    "Matches com senha nula deve lançar exceção");
    }

    @Test
    void testMatchesHashNulo() {
        // BCrypt retorna false para hash nulo em vez de lançar exceção
        assertFalse(Hashing.matches("teste", null), 
                   "Matches com hash nulo deve retornar false");
    }

    @Test
    void testHashsSaoDiferentes() {
        String senha = "mesmasenha";
        String hash1 = Hashing.hash(senha);
        String hash2 = Hashing.hash(senha);
        
        // Se usa salt aleatório, os hashes devem ser diferentes
        // Mas ambos devem verificar a mesma senha
        assertTrue(Hashing.matches(senha, hash1), "Hash1 deve verificar a senha");
        assertTrue(Hashing.matches(senha, hash2), "Hash2 deve verificar a senha");
    }

    @Test
    void testSenhasComCaracteresEspeciais() {
        String[] senhasEspeciais = {
            "senha@123!",
            "põe#açúcar$",
            "ça&va*bien?",
            "test+equals=true",
            "nova|senha\\teste",
            "senha_com-underscore.ponto",
            "123456789",
            "!@#$%^&*()",
            "áéíóúçãõ"
        };

        for (String senha : senhasEspeciais) {
            String hash = Hashing.hash(senha);
            assertTrue(Hashing.matches(senha, hash), 
                      "Senha com caracteres especiais deve verificar corretamente: " + senha);
        }
    }

    @Test
    void testSenhasLongas() {
        String senhaLonga = "a".repeat(1000);
        String hash = Hashing.hash(senhaLonga);
        
        assertTrue(Hashing.matches(senhaLonga, hash), 
                  "Senha longa deve verificar corretamente");
    }

    @Test
    void testSenhasMuitoCurtas() {
        String[] senhasCurtas = {
            "a",
            "1",
            "!",
            "ç"
        };

        for (String senha : senhasCurtas) {
            String hash = Hashing.hash(senha);
            assertTrue(Hashing.matches(senha, hash), 
                      "Senha curta deve verificar corretamente: " + senha);
        }
    }

    @Test
    void testVerificacaoComSenhaIncorreta() {
        String senhaCorreta = "senhaCorreta123";
        String senhaIncorreta = "senhaIncorreta456";
        
        String hash = Hashing.hash(senhaCorreta);
        
        assertFalse(Hashing.matches(senhaIncorreta, hash), 
                   "Senha incorreta não deve verificar com hash de senha correta");
    }

    @Test
    void testHashComEspacos() {
        String[] senhasComEspacos = {
            " senha ",
            "  senha  ",
            "sen ha",
            "senha com espacos",
            "\tsenha\t",
            "\nsenha\n"
        };

        for (String senha : senhasComEspacos) {
            String hash = Hashing.hash(senha);
            assertTrue(Hashing.matches(senha, hash), 
                      "Senha com espaços deve verificar corretamente: '" + senha + "'");
        }
    }

    @Test
    void testCaseSensitive() {
        String senhaMinuscula = "senha123";
        String senhaMaiuscula = "SENHA123";
        String senhaMista = "SeNhA123";
        
        String hashMinuscula = Hashing.hash(senhaMinuscula);
        String hashMaiuscula = Hashing.hash(senhaMaiuscula);
        String hashMista = Hashing.hash(senhaMista);
        
        // Deve ser case-sensitive
        assertFalse(Hashing.matches(senhaMaiuscula, hashMinuscula), 
                   "Hash deve ser case-sensitive");
        assertFalse(Hashing.matches(senhaMinuscula, hashMaiuscula), 
                   "Hash deve ser case-sensitive");
        assertFalse(Hashing.matches(senhaMista, hashMinuscula), 
                   "Hash deve ser case-sensitive");
    }

    @Test
    void testPerformance() {
        String senha = "senhaTestePadrao123";
        long startTime = System.currentTimeMillis();
        
        // Testa hash
        for (int i = 0; i < 100; i++) {
            Hashing.hash(senha);
        }
        
        long hashTime = System.currentTimeMillis() - startTime;
        
        // Testa verificação
        String hash = Hashing.hash(senha);
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            Hashing.matches(senha, hash);
        }
        
        long verifyTime = System.currentTimeMillis() - startTime;
        
        assertTrue(hashTime < 10000, "100 operações de hash devem ser razoavelmente rápidas (< 10s), levou: " + hashTime + "ms");
        assertTrue(verifyTime < 10000, "100 operações de verificação devem ser razoavelmente rápidas (< 10s), levou: " + verifyTime + "ms");
    }

    @Test
    void testHashFormatoConsistente() {
        String senha = "testeFormato";
        String hash = Hashing.hash(senha);
        
        // Verifica se o hash tem um formato consistente (dependendo da implementação)
        assertNotNull(hash, "Hash não deve ser nulo");
        assertTrue(hash.length() > 10, "Hash deve ter um tamanho mínimo razoável");
        
        // BCrypt hashes começam com $2a$, $2b$, ou similar
        assertTrue(hash.startsWith("$2"), "Hash BCrypt deve começar com $2");
        assertFalse(hash.equals(senha), "Hash não deve ser igual à senha original");
    }

    @Test
    void testSeguranca() {
        String senha = "senhaSecreta123";
        String hash = Hashing.hash(senha);
        
        // Hash não deve conter a senha em texto claro
        assertFalse(hash.contains(senha), "Hash não deve conter a senha em texto claro");
    }

    @Test
    void testUnicodeSupport() {
        String[] senhasUnicode = {
            "संस्कृत",
            "العربية",
            "中文",
            "русский",
            "🔐🔑💻",
            "café",
            "naïve",
            "Москва"
        };

        for (String senha : senhasUnicode) {
            String hash = Hashing.hash(senha);
            assertTrue(Hashing.matches(senha, hash), 
                      "Senha Unicode deve verificar corretamente: " + senha);
        }
    }

    @Test
    void testHashInvalido() {
        String senha = "teste";
        String hashInvalido = "hash_invalido_fake";
        
        assertFalse(Hashing.matches(senha, hashInvalido), 
                   "Verificação com hash inválido deve retornar false");
    }

    @Test
    void testBCryptFormat() {
        String senha = "testeBCrypt";
        String hash = Hashing.hash(senha);
        
        // BCrypt hash tem formato específico: $2a$10$...
        assertTrue(hash.matches("\\$2[abxy]\\$\\d{2}\\$.{53}"), 
                  "Hash deve seguir formato BCrypt");
        assertEquals(60, hash.length(), "Hash BCrypt deve ter 60 caracteres");
    }

    @Test
    void testSaltAleatorio() {
        String senha = "testeSalt";
        String hash1 = Hashing.hash(senha);
        String hash2 = Hashing.hash(senha);
        
        // Com salt aleatório, dois hashes da mesma senha devem ser diferentes
        assertNotEquals(hash1, hash2, "Hashes com salt aleatório devem ser diferentes");
        
        // Mas ambos devem funcionar
        assertTrue(Hashing.matches(senha, hash1), "Hash1 deve verificar a senha");
        assertTrue(Hashing.matches(senha, hash2), "Hash2 deve verificar a senha");
    }
} 