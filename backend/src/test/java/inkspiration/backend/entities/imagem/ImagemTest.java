package inkspiration.backend.entities.imagem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portifolio;

public class ImagemTest {

    private Imagem imagem;
    private Portifolio portifolio;

    @BeforeEach
    void setUp() {
        imagem = new Imagem();
        portifolio = new Portifolio();
    }

    @Test
    void testGettersAndSettersIdImagem() {
        Long id = 1L;
        imagem.setIdImagem(id);
        assertEquals(id, imagem.getIdImagem(), "ID da imagem deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersImagemBase64() {
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64(), "Imagem Base64 deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersPortifolio() {
        imagem.setPortifolio(portifolio);
        assertEquals(portifolio, imagem.getPortifolio(), "Portifólio deve ser igual ao definido");
    }

    @Test
    void testConstrutorPadrao() {
        Imagem imagemVazia = new Imagem();
        
        assertNull(imagemVazia.getIdImagem(), "ID deve ser nulo inicialmente");
        assertNull(imagemVazia.getImagemBase64(), "Imagem Base64 deve ser nula inicialmente");
        assertNull(imagemVazia.getPortifolio(), "Portifólio deve ser nulo inicialmente");
    }

    @Test
    void testConstrutorComParametros() {
        String imagemBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCdABmX/9k=";
        
        Imagem imagemComParametros = new Imagem(imagemBase64, portifolio);
        
        assertEquals(imagemBase64, imagemComParametros.getImagemBase64(), "Imagem Base64 deve ser igual à fornecida no construtor");
        assertEquals(portifolio, imagemComParametros.getPortifolio(), "Portifólio deve ser igual ao fornecido no construtor");
    }

    @Test
    void testConstrutorComParametrosNulos() {
        Imagem imagemComNulos = new Imagem(null, null);
        
        assertNull(imagemComNulos.getImagemBase64(), "Imagem Base64 pode ser nula no construtor");
        assertNull(imagemComNulos.getPortifolio(), "Portifólio pode ser nulo no construtor");
    }

    @Test
    void testImagemComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";

        // Act
        imagem.setIdImagem(id);
        imagem.setImagemBase64(imagemBase64);
        imagem.setPortifolio(portifolio);

        // Assert
        assertEquals(id, imagem.getIdImagem());
        assertEquals(imagemBase64, imagem.getImagemBase64());
        assertEquals(portifolio, imagem.getPortifolio());
    }

    @Test
    void testImagemBase64Vazia() {
        imagem.setImagemBase64("");
        assertEquals("", imagem.getImagemBase64(), "Deve aceitar string vazia");
        
        imagem.setImagemBase64(null);
        assertNull(imagem.getImagemBase64(), "Deve aceitar valor nulo");
    }

    @Test
    void testImagemBase64Formatos() {
        // Teste com diferentes formatos de imagem Base64
        String[] formatosBase64 = {
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==",
            "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD",
            "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
            "data:image/webp;base64,UklGRiQAAABXRUJQVlA4IBgAAAAwAQCdASoBAAEAAwA0JaQAA3AA/vuUAAA=",
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==" // Sem prefixo data:
        };

        for (String formato : formatosBase64) {
            assertDoesNotThrow(() -> {
                imagem.setImagemBase64(formato);
                assertEquals(formato, imagem.getImagemBase64());
            }, "Deve aceitar formato Base64: " + formato.substring(0, Math.min(50, formato.length())) + "...");
        }
    }

    @Test
    void testImagemBase64Grande() {
        // Simula uma imagem Base64 muito grande
        String imagemGrande = "data:image/png;base64," + "a".repeat(10000);
        
        assertDoesNotThrow(() -> {
            imagem.setImagemBase64(imagemGrande);
        }, "Deve aceitar imagem Base64 grande sem lançar exceção");
        
        assertEquals(imagemGrande, imagem.getImagemBase64(), "Deve armazenar imagem Base64 grande corretamente");
    }

    @Test
    void testPortifolioNulo() {
        imagem.setPortifolio(null);
        assertNull(imagem.getPortifolio(), "Deve aceitar portifólio nulo");
    }

    @Test
    void testRelacionamentoBidirecional() {
        // Testando se o relacionamento funciona nos dois sentidos
        portifolio.setIdPortifolio(1L);
        imagem.setPortifolio(portifolio);
        
        assertNotNull(imagem.getPortifolio(), "Portifólio deve estar definido na imagem");
        assertEquals(1L, imagem.getPortifolio().getIdPortifolio(), "ID do portifólio deve estar correto");
    }

    @Test
    void testImagemBase64ComCaracteresEspeciais() {
        // Teste com caracteres especiais que podem aparecer em Base64
        String base64ComEspeciais = "data:image/png;base64,ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
        
        imagem.setImagemBase64(base64ComEspeciais);
        assertEquals(base64ComEspeciais, imagem.getImagemBase64(), "Deve aceitar todos os caracteres válidos de Base64");
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        imagem.setIdImagem(idMaximo);
        assertEquals(idMaximo, imagem.getIdImagem(), "Deve aceitar ID máximo");
        
        imagem.setIdImagem(idMinimo);
        assertEquals(idMinimo, imagem.getIdImagem(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testImagemBase64ComMetadados() {
        // Teste com diferentes tipos de metadados em Base64
        String[] tiposComMetadados = {
            "data:image/png;base64,",
            "data:image/jpeg;charset=utf-8;base64,",
            "data:image/svg+xml;base64,",
            "data:image/tiff;base64,"
        };

        for (String tipo : tiposComMetadados) {
            String imagemCompleta = tipo + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
            
            assertDoesNotThrow(() -> {
                imagem.setImagemBase64(imagemCompleta);
                assertEquals(imagemCompleta, imagem.getImagemBase64());
            }, "Deve aceitar metadados: " + tipo);
        }
    }

    @Test
    void testConstrutorComImagemVaziaEPortifolioValido() {
        Imagem imagemTestee = new Imagem("", portifolio);
        
        assertEquals("", imagemTestee.getImagemBase64(), "Deve aceitar string vazia no construtor");
        assertEquals(portifolio, imagemTestee.getPortifolio(), "Portifólio deve ser definido corretamente");
    }

    @Test
    void testConstrutorComImagemValidaEPortifolioNulo() {
        String imagemBase64 = "data:image/png;base64,teste";
        Imagem imagemTestee = new Imagem(imagemBase64, null);
        
        assertEquals(imagemBase64, imagemTestee.getImagemBase64(), "Imagem deve ser definida corretamente");
        assertNull(imagemTestee.getPortifolio(), "Portifólio pode ser nulo no construtor");
    }
} 