package inkspiration.backend.entities.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inkspiration.backend.util.Hashing;

public class HashingTest {

    @Test
    void testHashSenha() {
        String senha = "minhasenha123";
        String hash = Hashing.hash(senha);
        
        assertNotNull(hash, "Hash não deve ser nulo");
        assertNotEquals(senha, hash, "Hash deve ser diferente da senha original");
        assertTrue(hash.length() > 0, "Hash deve ter conteúdo");
    }

    @Test
    void testHashsSenhasDiferentes() {
        String senha1 = "senha123";
        String senha2 = "senha456";
        
        String hash1 = Hashing.hash(senha1);
        String hash2 = Hashing.hash(senha2);
        
        assertNotEquals(hash1, hash2, "Hashes de senhas diferentes devem ser diferentes");
    }

    @Test
    void testHashMesmaSenha() {
        String senha = "mesmasenha";
        
        String hash1 = Hashing.hash(senha);
        String hash2 = Hashing.hash(senha);
        
        // Com BCrypt, mesmo hash da mesma senha pode ser diferente devido ao salt
        assertNotEquals(hash1, hash2, "BCrypt deve gerar hashes diferentes mesmo para a mesma senha");
        
        // Mas ambos devem corresponder à senha original
        assertTrue(Hashing.matches(senha, hash1), "Primeiro hash deve corresponder à senha");
        assertTrue(Hashing.matches(senha, hash2), "Segundo hash deve corresponder à senha");
    }

    @Test
    void testMatchesSenhaCorreta() {
        String senha = "senhavalida123";
        String hash = Hashing.hash(senha);
        
        assertTrue(Hashing.matches(senha, hash), "Senha correta deve fazer match com seu hash");
    }

    @Test
    void testMatchesSenhaIncorreta() {
        String senhaOriginal = "senhaoriginal";
        String senhaErrada = "senhaerrada";
        String hash = Hashing.hash(senhaOriginal);
        
        assertFalse(Hashing.matches(senhaErrada, hash), "Senha incorreta não deve fazer match");
    }

    @Test
    void testHashSenhaVazia() {
        String senhaVazia = "";
        String hash = Hashing.hash(senhaVazia);
        
        assertNotNull(hash, "Hash de senha vazia não deve ser nulo");
        assertTrue(Hashing.matches(senhaVazia, hash), "Senha vazia deve fazer match com seu hash");
    }

    @Test
    void testHashSenhaComEspacos() {
        String senhaComEspacos = "senha com espacos";
        String hash = Hashing.hash(senhaComEspacos);
        
        assertTrue(Hashing.matches(senhaComEspacos, hash), "Senha com espaços deve fazer match");
        assertFalse(Hashing.matches("senhacomeespacos", hash), "Senha sem espaços não deve fazer match");
    }

    @Test
    void testHashSenhaComCaracteresEspeciais() {
        String senhaEspecial = "senha@#$%&*()!";
        String hash = Hashing.hash(senhaEspecial);
        
        assertTrue(Hashing.matches(senhaEspecial, hash), "Senha com caracteres especiais deve fazer match");
    }

    @Test
    void testHashSenhaLonga() {
        String senhaLonga = "a".repeat(1000);
        String hash = Hashing.hash(senhaLonga);
        
        assertNotNull(hash, "Hash de senha longa não deve ser nulo");
        assertTrue(Hashing.matches(senhaLonga, hash), "Senha longa deve fazer match com seu hash");
    }

    @Test
    void testHashComNumeros() {
        String senhaComNumeros = "senha123456789";
        String hash = Hashing.hash(senhaComNumeros);
        
        assertTrue(Hashing.matches(senhaComNumeros, hash), "Senha com números deve fazer match");
        assertFalse(Hashing.matches("senha987654321", hash), "Senha com números diferentes não deve fazer match");
    }

    @Test
    void testCaseSensitive() {
        String senhaMinuscula = "senhatest";
        String senhaMaiuscula = "SENHATEST";
        String hashMinuscula = Hashing.hash(senhaMinuscula);
        
        assertTrue(Hashing.matches(senhaMinuscula, hashMinuscula), "Senha minúscula deve fazer match");
        assertFalse(Hashing.matches(senhaMaiuscula, hashMinuscula), "Senha maiúscula não deve fazer match com hash de minúscula");
    }

    @Test
    void testHashNaoEReversivel() {
        String senha = "senhasecreta";
        String hash = Hashing.hash(senha);
        
        // Não deve ser possível recuperar a senha do hash
        assertNotEquals(senha, hash, "Hash não deve ser igual à senha");
        assertFalse(hash.contains(senha), "Hash não deve conter a senha em texto claro");
    }

    @Test
    void testMatchesComHashInvalido() {
        String senha = "senhaqualquer";
        String hashInvalido = "hashinvalido";
        
        assertFalse(Hashing.matches(senha, hashInvalido), "Senha não deve fazer match com hash inválido");
    }

    @Test
    void testMultiplosHashesDaMesmaSenha() {
        String senha = "testsenha";
        
        // Gerar múltiplos hashes da mesma senha
        String[] hashes = new String[5];
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = Hashing.hash(senha);
        }
        
        // Todos devem ser diferentes (devido ao salt do BCrypt)
        for (int i = 0; i < hashes.length; i++) {
            for (int j = i + 1; j < hashes.length; j++) {
                assertNotEquals(hashes[i], hashes[j], "Hashes devem ser únicos");
            }
        }
        
        // Mas todos devem fazer match com a senha original
        for (String hash : hashes) {
            assertTrue(Hashing.matches(senha, hash), "Todos os hashes devem fazer match com a senha");
        }
    }
} 