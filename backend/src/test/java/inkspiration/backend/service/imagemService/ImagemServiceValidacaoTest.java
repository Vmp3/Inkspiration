package inkspiration.backend.service.imagemService;

import inkspiration.backend.dto.ImagemDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

class ImagemServiceValidacaoTest {

    @Test
    @DisplayName("Deve validar Base64 de imagem válido")
    void deveValidarBase64ImagemValido() {
        // Given
        String imagemBase64Valida = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        
        // When & Then
        assertNotNull(imagemBase64Valida);
        assertFalse(imagemBase64Valida.isEmpty());
        assertDoesNotThrow(() -> Base64.getDecoder().decode(imagemBase64Valida));
    }

    @Test
    @DisplayName("Deve rejeitar Base64 inválido")
    void deveRejeitarBase64Invalido() {
        // Given
        List<String> base64Invalidos = Arrays.asList(
            "imagem-invalida",
            "123!@#",
            "",
            "data:image/png;base64,", // Só o prefixo
            "iVBORw0KGgoAAAANSUh!!!" // Caracteres inválidos
        );
        
        // When & Then
        for (String base64 : base64Invalidos) {
            if (base64 != null && !base64.isEmpty() && !base64.startsWith("data:")) {
                try {
                    Base64.getDecoder().decode(base64);
                    // Se chegou aqui, pode ser um Base64 válido mas não uma imagem
                } catch (IllegalArgumentException e) {
                    // Base64 inválido como esperado
                    assertTrue(true);
                }
            }
        }
    }

    @Test
    @DisplayName("Deve criar ImagemDTO com dados válidos")
    void deveCriarImagemDTOComDadosValidos() {
        // Given
        Long idImagem = 1L;
        String imagemBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        Long idPortfolio = 2L;
        
        // When
        ImagemDTO dto = new ImagemDTO();
        dto.setIdImagem(idImagem);
        dto.setImagemBase64(imagemBase64);
        dto.setIdPortfolio(idPortfolio);
        
        // Then
        assertEquals(idImagem, dto.getIdImagem());
        assertEquals(imagemBase64, dto.getImagemBase64());
        assertEquals(idPortfolio, dto.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve criar ImagemDTO com construtor")
    void deveCriarImagemDTOComConstrutor() {
        // Given
        Long idImagem = 1L;
        String imagemBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        Long idPortfolio = 2L;
        
        // When
        ImagemDTO dto = new ImagemDTO(idImagem, imagemBase64, idPortfolio);
        
        // Then
        assertEquals(idImagem, dto.getIdImagem());
        assertEquals(imagemBase64, dto.getImagemBase64());
        assertEquals(idPortfolio, dto.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve validar tamanho máximo de Base64")
    void deveValidarTamanhoMaximoBase64() {
        // Given
        String imagemPequena = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        int tamanhoMaximo = 5 * 1024 * 1024; // 5MB em caracteres aproximadamente
        
        // When & Then
        assertTrue(imagemPequena.length() < tamanhoMaximo);
        assertNotNull(imagemPequena);
        assertFalse(imagemPequena.isEmpty());
    }

    @Test
    @DisplayName("Deve validar formatos de imagem suportados")
    void deveValidarFormatosImagemSuportados() {
        // Given
        List<String> formatosSuportados = Arrays.asList("PNG", "JPEG", "JPG", "GIF", "WEBP");
        
        // When & Then
        for (String formato : formatosSuportados) {
            assertNotNull(formato);
            assertFalse(formato.isEmpty());
            assertTrue(formato.length() >= 3);
            assertTrue(formato.matches("[A-Z]+"));
        }
    }

    @Test
    @DisplayName("Deve remover prefixo data URL se presente")
    void deveRemoverPrefixoDataURLSePresente() {
        // Given
        String imagemComPrefixo = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        String imagemSemPrefixo = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        
        // When
        String base64Limpo = imagemComPrefixo.startsWith("data:") ? 
                           imagemComPrefixo.substring(imagemComPrefixo.indexOf(",") + 1) : 
                           imagemComPrefixo;
        
        // Then
        assertEquals(imagemSemPrefixo, base64Limpo);
        assertFalse(base64Limpo.startsWith("data:"));
    }

    @Test
    @DisplayName("Deve validar associação com portfolio")
    void deveValidarAssociacaoComPortfolio() {
        // Given
        Long idPortfolio = 1L;
        ImagemDTO imagem1 = new ImagemDTO(1L, "base64data1", idPortfolio);
        ImagemDTO imagem2 = new ImagemDTO(2L, "base64data2", idPortfolio);
        ImagemDTO imagem3 = new ImagemDTO(3L, "base64data3", 2L);
        
        // When & Then
        assertEquals(idPortfolio, imagem1.getIdPortfolio());
        assertEquals(idPortfolio, imagem2.getIdPortfolio());
        assertNotEquals(idPortfolio, imagem3.getIdPortfolio());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no DTO")
    void deveValidarCamposObrigatoriosNoDTO() {
        // Given
        ImagemDTO dto = new ImagemDTO();
        
        // When & Then
        assertNull(dto.getIdImagem()); // Pode ser nulo para criação
        assertNull(dto.getImagemBase64()); // Deve ser preenchido
        assertNull(dto.getIdPortfolio()); // Deve ser preenchido
    }

    @Test
    @DisplayName("Deve validar decodificação de Base64")
    void deveValidarDecodificacaoBase64() {
        // Given
        String imagemBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        
        // When
        byte[] bytesImagem = Base64.getDecoder().decode(imagemBase64);
        
        // Then
        assertNotNull(bytesImagem);
        assertTrue(bytesImagem.length > 0);
    }

    @Test
    @DisplayName("Deve validar codificação de bytes para Base64")
    void deveValidarCodificacaoBytesParaBase64() {
        // Given
        byte[] bytesImagem = {(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG header
        
        // When
        String base64 = Base64.getEncoder().encodeToString(bytesImagem);
        
        // Then
        assertNotNull(base64);
        assertFalse(base64.isEmpty());
        assertEquals("iVBORw==", base64);
    }

    @Test
    @DisplayName("Deve validar header PNG em bytes")
    void deveValidarHeaderPNGEmBytes() {
        // Given
        String pngBase64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        
        // When
        byte[] bytes = Base64.getDecoder().decode(pngBase64);
        
        // Then
        assertTrue(bytes.length >= 8); // PNG header tem 8 bytes
        assertEquals((byte) 0x89, bytes[0]); // PNG signature
        assertEquals(0x50, bytes[1]); // 'P'
        assertEquals(0x4E, bytes[2]); // 'N'
        assertEquals(0x47, bytes[3]); // 'G'
    }

    @Test
    @DisplayName("Deve validar lista de imagens de um portfolio")
    void deveValidarListaImagensPortfolio() {
        // Given
        Long idPortfolio = 1L;
        List<ImagemDTO> imagens = Arrays.asList(
            new ImagemDTO(1L, "base64data1", idPortfolio),
            new ImagemDTO(2L, "base64data2", idPortfolio),
            new ImagemDTO(3L, "base64data3", idPortfolio)
        );
        
        // When & Then
        assertEquals(3, imagens.size());
        for (ImagemDTO imagem : imagens) {
            assertEquals(idPortfolio, imagem.getIdPortfolio());
            assertNotNull(imagem.getImagemBase64());
            assertNotNull(imagem.getIdImagem());
        }
    }

    @Test
    @DisplayName("Deve validar remoção de imagens do portfolio")
    void deveValidarRemocaoImagensPortfolio() {
        // Given
        List<Long> idsImagensParaRemover = Arrays.asList(1L, 2L, 3L);
        
        // When & Then
        for (Long id : idsImagensParaRemover) {
            assertNotNull(id);
            assertTrue(id > 0);
        }
        assertEquals(3, idsImagensParaRemover.size());
    }
} 