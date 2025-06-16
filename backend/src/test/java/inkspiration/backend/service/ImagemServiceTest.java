package inkspiration.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortifolioRepository;

@ExtendWith(MockitoExtension.class)
class ImagemServiceTest {

    @Mock
    private ImagemRepository imagemRepository;

    @Mock
    private PortifolioRepository portifolioRepository;

    @InjectMocks
    private ImagemService imagemService;

    private Imagem imagem;
    private ImagemDTO imagemDTO;
    private Portifolio portifolio;

    @BeforeEach
    void setUp() {
        portifolio = new Portifolio();
        portifolio.setIdPortifolio(1L);
        portifolio.setDescricao("Portifólio de teste");

        imagem = new Imagem();
        imagem.setIdImagem(1L);
        imagem.setImagemBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/");
        imagem.setPortifolio(portifolio);

        imagemDTO = new ImagemDTO();
        imagemDTO.setIdImagem(1L);
        imagemDTO.setImagemBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/");
        imagemDTO.setIdPortifolio(1L);
    }

    @Test
    void testListarPorPortifolio_Success() {
        // Arrange
        Long idPortifolio = 1L;
        List<Imagem> imagens = Arrays.asList(imagem);
        
        when(imagemRepository.findByPortifolioIdPortifolio(idPortifolio)).thenReturn(imagens);

        // Act
        List<ImagemDTO> result = imagemService.listarPorPortifolio(idPortifolio);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(imagem.getIdImagem(), result.get(0).getIdImagem());
        assertEquals(imagem.getImagemBase64(), result.get(0).getImagemBase64());
        assertEquals(portifolio.getIdPortifolio(), result.get(0).getIdPortifolio());
        
        verify(imagemRepository, times(1)).findByPortifolioIdPortifolio(idPortifolio);
    }

    @Test
    void testListarPorPortifolio_EmptyList() {
        // Arrange
        Long idPortifolio = 1L;
        List<Imagem> imagensVazias = Arrays.asList();
        
        when(imagemRepository.findByPortifolioIdPortifolio(idPortifolio)).thenReturn(imagensVazias);

        // Act
        List<ImagemDTO> result = imagemService.listarPorPortifolio(idPortifolio);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(imagemRepository, times(1)).findByPortifolioIdPortifolio(idPortifolio);
    }

    @Test
    void testListarPorPortifolio_MultipleImages() {
        // Arrange
        Long idPortifolio = 1L;
        
        Imagem imagem2 = new Imagem();
        imagem2.setIdImagem(2L);
        imagem2.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA");
        imagem2.setPortifolio(portifolio);
        
        List<Imagem> imagens = Arrays.asList(imagem, imagem2);
        
        when(imagemRepository.findByPortifolioIdPortifolio(idPortifolio)).thenReturn(imagens);

        // Act
        List<ImagemDTO> result = imagemService.listarPorPortifolio(idPortifolio);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(imagem.getIdImagem(), result.get(0).getIdImagem());
        assertEquals(imagem2.getIdImagem(), result.get(1).getIdImagem());
        
        verify(imagemRepository, times(1)).findByPortifolioIdPortifolio(idPortifolio);
    }

    @Test
    void testBuscarPorId_Success() {
        // Arrange
        Long id = 1L;
        
        when(imagemRepository.findById(id)).thenReturn(Optional.of(imagem));

        // Act
        ImagemDTO result = imagemService.buscarPorId(id);

        // Assert
        assertNotNull(result);
        assertEquals(imagem.getIdImagem(), result.getIdImagem());
        assertEquals(imagem.getImagemBase64(), result.getImagemBase64());
        assertEquals(portifolio.getIdPortifolio(), result.getIdPortifolio());
        
        verify(imagemRepository, times(1)).findById(id);
    }

    @Test
    void testBuscarPorId_NotFound() {
        // Arrange
        Long id = 999L;
        
        when(imagemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            imagemService.buscarPorId(id);
        });
        
        assertEquals("Imagem não encontrada com ID: " + id, exception.getMessage());
        verify(imagemRepository, times(1)).findById(id);
    }

    @Test
    void testSalvar_Success() {
        // Arrange
        when(portifolioRepository.findById(imagemDTO.getIdPortifolio())).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);

        // Act
        ImagemDTO result = imagemService.salvar(imagemDTO);

        // Assert
        assertNotNull(result);
        assertEquals(imagem.getIdImagem(), result.getIdImagem());
        assertEquals(imagem.getImagemBase64(), result.getImagemBase64());
        assertEquals(portifolio.getIdPortifolio(), result.getIdPortifolio());
        
        verify(portifolioRepository, times(1)).findById(imagemDTO.getIdPortifolio());
        verify(imagemRepository, times(1)).save(any(Imagem.class));
    }

    @Test
    void testSalvar_PortifolioNotFound() {
        // Arrange
        when(portifolioRepository.findById(imagemDTO.getIdPortifolio())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            imagemService.salvar(imagemDTO);
        });
        
        assertEquals("Portifólio não encontrado com ID: " + imagemDTO.getIdPortifolio(), exception.getMessage());
        verify(portifolioRepository, times(1)).findById(imagemDTO.getIdPortifolio());
        verify(imagemRepository, never()).save(any(Imagem.class));
    }

    @Test
    void testSalvar_NullDTO() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            imagemService.salvar(null);
        });
    }

    @Test
    void testSalvar_ImagemWithDifferentFormats() {
        // Arrange
        String[] imageFormats = {
            "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/",
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA",
            "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP//",
            "data:image/webp;base64,UklGRiIAAABXRUJQVlA4IBY"
        };
        
        when(portifolioRepository.findById(anyLong())).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);

        // Act & Assert
        for (String imageFormat : imageFormats) {
            ImagemDTO dto = new ImagemDTO();
            dto.setImagemBase64(imageFormat);
            dto.setIdPortifolio(1L);
            
            assertDoesNotThrow(() -> {
                imagemService.salvar(dto);
            });
        }
        
        verify(imagemRepository, times(imageFormats.length)).save(any(Imagem.class));
    }

    @Test
    void testDeletar_Success() {
        // Arrange
        Long id = 1L;
        
        when(imagemRepository.findById(id)).thenReturn(Optional.of(imagem));

        // Act
        assertDoesNotThrow(() -> {
            imagemService.deletar(id);
        });

        // Assert
        verify(imagemRepository, times(1)).findById(id);
        verify(imagemRepository, times(1)).delete(imagem);
    }

    @Test
    void testDeletar_NotFound() {
        // Arrange
        Long id = 999L;
        
        when(imagemRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            imagemService.deletar(id);
        });
        
        assertEquals("Imagem não encontrada com ID: " + id, exception.getMessage());
        verify(imagemRepository, times(1)).findById(id);
        verify(imagemRepository, never()).delete(any(Imagem.class));
    }

    @Test
    void testConverterParaDto() {
        // Arrange
        when(imagemRepository.findById(1L)).thenReturn(Optional.of(imagem));
        
        // Act
        ImagemDTO result = imagemService.buscarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(imagem.getIdImagem(), result.getIdImagem());
        assertEquals(imagem.getImagemBase64(), result.getImagemBase64());
        assertEquals(portifolio.getIdPortifolio(), result.getIdPortifolio());
    }

    @Test
    void testFullWorkflow() {
        // Arrange
        when(portifolioRepository.findById(anyLong())).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);
        when(imagemRepository.findById(anyLong())).thenReturn(Optional.of(imagem));
        when(imagemRepository.findByPortifolioIdPortifolio(anyLong())).thenReturn(Arrays.asList(imagem));

        // Act & Assert - Workflow completo: Salvar -> Buscar -> Listar -> Deletar
        
        // 1. Salvar
        ImagemDTO saved = imagemService.salvar(imagemDTO);
        assertNotNull(saved);
        
        // 2. Buscar por ID
        ImagemDTO found = imagemService.buscarPorId(1L);
        assertNotNull(found);
        
        // 3. Listar por portifólio
        List<ImagemDTO> list = imagemService.listarPorPortifolio(1L);
        assertEquals(1, list.size());
        
        // 4. Deletar
        assertDoesNotThrow(() -> {
            imagemService.deletar(1L);
        });
    }

    @Test
    void testConstructor() {
        // Arrange & Act
        ImagemService newService = new ImagemService(imagemRepository, portifolioRepository);

        // Assert
        assertNotNull(newService);
    }

    @Test
    void testLargeBase64Image() {
        // Arrange
        StringBuilder largeBase64 = new StringBuilder("data:image/jpeg;base64,");
        for (int i = 0; i < 1000; i++) {
            largeBase64.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
        }
        
        ImagemDTO largeImageDTO = new ImagemDTO();
        largeImageDTO.setImagemBase64(largeBase64.toString());
        largeImageDTO.setIdPortifolio(1L);
        
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);

        // Act & Assert
        assertDoesNotThrow(() -> {
            imagemService.salvar(largeImageDTO);
        });
        
        verify(imagemRepository, times(1)).save(any(Imagem.class));
    }

    @Test
    void testEmptyBase64Image() {
        // Arrange
        ImagemDTO emptyImageDTO = new ImagemDTO();
        emptyImageDTO.setImagemBase64("");
        emptyImageDTO.setIdPortifolio(1L);
        
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);

        // Act & Assert
        assertDoesNotThrow(() -> {
            imagemService.salvar(emptyImageDTO);
        });
        
        verify(imagemRepository, times(1)).save(any(Imagem.class));
    }

    @Test
    void testNullBase64Image() {
        // Arrange
        ImagemDTO nullImageDTO = new ImagemDTO();
        nullImageDTO.setImagemBase64(null);
        nullImageDTO.setIdPortifolio(1L);
        
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(imagemRepository.save(any(Imagem.class))).thenReturn(imagem);

        // Act & Assert
        assertDoesNotThrow(() -> {
            imagemService.salvar(nullImageDTO);
        });
        
        verify(imagemRepository, times(1)).save(any(Imagem.class));
    }

    @Test
    void testRepositoryExceptions() {
        // Arrange
        when(imagemRepository.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            imagemService.buscarPorId(1L);
        });
    }
} 