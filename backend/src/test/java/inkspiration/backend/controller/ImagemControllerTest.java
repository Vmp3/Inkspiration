package inkspiration.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.security.AuthorizationService;

@WebMvcTest(value = ImagemController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ImagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImagemService imagemService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ImagemDTO imagemDTO;
    private Imagem imagem;

    @BeforeEach
    void setUp() {
        imagemDTO = new ImagemDTO();
        imagemDTO.setIdImagem(1L);
        imagemDTO.setImagemBase64("data:image/jpeg;base64,test");
        imagemDTO.setIdPortifolio(1L);
    }

    @Test
    @WithMockUser
    void testListarPorPortifolio_Success() throws Exception {
        // Arrange
        List<ImagemDTO> imagens = List.of(imagemDTO);
        when(imagemService.listarPorPortifolio(1L)).thenReturn(imagens);

        // Act & Assert
        mockMvc.perform(get("/imagens/portifolio/{idPortifolio}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idImagem").value(1L))
                .andExpect(jsonPath("$[0].imagemBase64").value("data:image/jpeg;base64,test"));

        verify(imagemService).listarPorPortifolio(1L);
    }

    @Test
    @WithMockUser
    void testBuscarPorId_Success() throws Exception {
        // Arrange
        when(imagemService.buscarPorId(1L)).thenReturn(imagemDTO);

        // Act & Assert
        mockMvc.perform(get("/imagens/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idImagem").value(1L))
                .andExpect(jsonPath("$.imagemBase64").value("data:image/jpeg;base64,test"));

        verify(imagemService).buscarPorId(1L);
    }

    @Test
    @WithMockUser
    void testSalvar_Success() throws Exception {
        // Arrange
        when(imagemService.salvar(any(ImagemDTO.class))).thenReturn(imagemDTO);

        // Act & Assert
        mockMvc.perform(post("/imagens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(imagemDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idImagem").value(1L))
                .andExpect(jsonPath("$.imagemBase64").value("data:image/jpeg;base64,test"));

        verify(imagemService).salvar(any(ImagemDTO.class));
    }

    @Test
    @WithMockUser
    void testDeletar_Success() throws Exception {
        // Arrange
        doNothing().when(imagemService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/imagens/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(imagemService).deletar(1L);
    }
} 