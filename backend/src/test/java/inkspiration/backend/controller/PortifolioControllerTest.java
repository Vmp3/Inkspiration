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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.PortifolioService;

@WebMvcTest(value = PortifolioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PortifolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortifolioService portifolioService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Portifolio portifolio;
    private PortifolioDTO portifolioDTO;

    @BeforeEach
    void setUp() {
        portifolio = new Portifolio();
        portifolio.setIdPortifolio(1L);
        portifolio.setDescricao("Test Portfolio");

        portifolioDTO = new PortifolioDTO();
        portifolioDTO.setIdPortifolio(1L);
        portifolioDTO.setDescricao("Test Portfolio");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListar_AdminSuccess() throws Exception {
        // Arrange
        Page<Portifolio> page = new PageImpl<>(List.of(portifolio));
        when(portifolioService.listarTodos(any(Pageable.class))).thenReturn(page);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(portifolioDTO);
        doNothing().when(authorizationService).requireAdmin();

        // Act & Assert
        mockMvc.perform(get("/portifolio")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPortifolio").value(1L))
                .andExpect(jsonPath("$[0].descricao").value("Test Portfolio"));

        verify(authorizationService).requireAdmin();
        verify(portifolioService).listarTodos(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testBuscarPorId_Success() throws Exception {
        // Arrange
        when(portifolioService.buscarPorId(1L)).thenReturn(portifolio);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(portifolioDTO);

        // Act & Assert
        mockMvc.perform(get("/portifolio/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPortifolio").value(1L))
                .andExpect(jsonPath("$.descricao").value("Test Portfolio"));

        verify(portifolioService).buscarPorId(1L);
    }

    @Test
    @WithMockUser
    void testCriar_Success() throws Exception {
        // Arrange
        when(portifolioService.criar(any(PortifolioDTO.class))).thenReturn(portifolio);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(portifolioDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/register/portifolio")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(portifolioDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPortifolio").value(1L))
                .andExpect(jsonPath("$.descricao").value("Test Portfolio"));

        verify(portifolioService).criar(any(PortifolioDTO.class));
    }

    @Test
    @WithMockUser
    void testAtualizar_Success() throws Exception {
        // Arrange
        when(portifolioService.atualizar(eq(1L), any(PortifolioDTO.class))).thenReturn(portifolio);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(portifolioDTO);

        // Act & Assert
        mockMvc.perform(put("/portifolio/atualizar/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(portifolioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPortifolio").value(1L))
                .andExpect(jsonPath("$.descricao").value("Test Portfolio"));

        verify(portifolioService).atualizar(eq(1L), any(PortifolioDTO.class));
    }

    @Test
    @WithMockUser
    void testDeletar_Success() throws Exception {
        // Arrange
        doNothing().when(portifolioService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/portifolio/deletar/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(portifolioService).deletar(1L);
    }
} 