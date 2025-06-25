package inkspiration.backend.entities.imagem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Imagem;

@DisplayName("Testes de validação de imagem base64 - Imagem")
public class ImagemBase64Test {

    private Imagem imagem;

    @BeforeEach
    void setUp() {
        imagem = new Imagem();
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 JPEG válida")
    void deveAceitarImagemBase64JpegValida() {
        String imagemBase64 = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 JPG válida")
    void deveAceitarImagemBase64JpgValida() {
        String imagemBase64 = "data:image/jpg;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 PNG válida")
    void deveAceitarImagemBase64PngValida() {
        String imagemBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 GIF válida")
    void deveAceitarImagemBase64GifValida() {
        String imagemBase64 = "data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 BMP válida")
    void deveAceitarImagemBase64BmpValida() {
        String imagemBase64 = "data:image/bmp;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem base64 WEBP válida")
    void deveAceitarImagemBase64WebpValida() {
        String imagemBase64 = "data:image/webp;base64,iVBORw0KGgoAAAANSUhEUgAAAA=";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64, imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve remover espaços das bordas da imagem")
    void deveRemoverEspacosDasBodasDaImagem() {
        String imagemBase64 = " data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA= ";
        imagem.setImagemBase64(imagemBase64);
        assertEquals(imagemBase64.trim(), imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem nula")
    void deveAceitarImagemNula() {
        imagem.setImagemBase64(null);
        assertNull(imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem vazia e converter para null")
    void deveAceitarImagemVaziaEConverterParaNull() {
        imagem.setImagemBase64("");
        assertNull(imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Deve aceitar imagem com apenas espaços e converter para null")
    void deveAceitarImagemComApenasEspacosEConverterParaNull() {
        imagem.setImagemBase64("   ");
        assertNull(imagem.getImagemBase64());
    }

    @Test
    @DisplayName("Não deve aceitar imagem com menos de 10 caracteres quando fornecida")
    void naoDeveAceitarImagemComMenosDe10CaracteresQuandoFornecida() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("data:img");
        });
        assertEquals("A imagem deve ter pelo menos 10 caracteres quando fornecida", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar formato base64 inválido")
    void naoDeveAceitarFormatoBase64Invalido() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar tipo MIME inválido")
    void naoDeveAceitarTipoMimeInvalido() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("data:image/svg;base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar sem data prefix")
    void naoDeveAceitarSemDataPrefix() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("base64,iVBORw0KGgoAAAANSUhEUgAAAA=");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar sem base64 prefix")
    void naoDeveAceitarSemBase64Prefix() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("data:image/png,iVBORw0KGgoAAAANSUhEUgAAAA=");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar sem dados base64")
    void naoDeveAceitarSemDadosBase64() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("data:image/png;base64,");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar caracteres especiais no base64")
    void naoDeveAceitarCaracteresEspeciaisNoBase64() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imagem.setImagemBase64("data:image/png;base64,iVBORw0@#$%UhEUgAAAA=");
        });
        assertEquals("Formato de imagem base64 inválido", exception.getMessage());
    }
} 