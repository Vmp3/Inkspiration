package inkspiration.backend.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.service.PortifolioService;
import inkspiration.backend.service.ProfissionalService;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@WebMvcTest(value = ProfissionalController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ProfissionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfissionalService profissionalService;

    @MockBean
    private ImagemService imagemService;

    @MockBean
    private PortifolioService portifolioService;

    @MockBean
    private DisponibilidadeService disponibilidadeService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Profissional profissional;
    private ProfissionalDTO profissionalDTO;
    private ProfissionalCriacaoDTO profissionalCriacaoDTO;
    private Usuario usuario;
    private Endereco endereco;
    private Portifolio portifolio;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Test Professional");
        usuario.setEmail("professional@test.com");
        usuario.setTelefone("11999999999");

        endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setCep("12345-678");
        endereco.setRua("Test Street");
        endereco.setBairro("Test Neighborhood");
        endereco.setCidade("Test City");
        endereco.setEstado("SP");

        portifolio = new Portifolio();
        portifolio.setIdPortifolio(1L);
        portifolio.setDescricao("Test Portfolio");

        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setNota(new BigDecimal("4.5"));
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortifolio(portifolio);

        profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setIdProfissional(1L);
        profissionalDTO.setIdUsuario(1L);
        profissionalDTO.setIdEndereco(1L);
        profissionalDTO.setNota(new BigDecimal("4.5"));

        profissionalCriacaoDTO = new ProfissionalCriacaoDTO();
        profissionalCriacaoDTO.setIdUsuario(1L);
        profissionalCriacaoDTO.setIdEndereco(1L);
        profissionalCriacaoDTO.setEspecialidade("Tatuagem");
        profissionalCriacaoDTO.setExperiencia("5 anos");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListar_AdminSuccess() throws Exception {
        // Arrange
        Page<Profissional> page = new PageImpl<>(List.of(profissional));
        when(profissionalService.listar(any(Pageable.class))).thenReturn(page);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);
        doNothing().when(authorizationService).requireAdmin();

        // Act & Assert
        mockMvc.perform(get("/profissional")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProfissional").value(1L))
                .andExpect(jsonPath("$[0].nota").value(4.5));

        verify(authorizationService).requireAdmin();
        verify(profissionalService).listar(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testListarPublico_Success() throws Exception {
        // Arrange
        Page<Profissional> page = new PageImpl<>(List.of(profissional));
        when(profissionalService.listar(any(Pageable.class))).thenReturn(page);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);

        // Act & Assert
        mockMvc.perform(get("/profissional/publico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProfissional").value(1L))
                .andExpect(jsonPath("$[0].nota").value(4.5));

        verify(profissionalService).listar(any(Pageable.class));
    }

    @Test
    @WithMockUser
    void testListarCompleto_Success() throws Exception {
        // Arrange
        Page<Profissional> page = new PageImpl<>(List.of(profissional));
        when(profissionalService.listarComFiltros(any(Pageable.class), any(), any(), anyDouble(), any(), any()))
                .thenReturn(page);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(new PortifolioDTO());
        when(imagemService.listarPorPortifolio(anyLong())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(anyLong())).thenReturn(Collections.emptyMap());

        // Act & Assert
        mockMvc.perform(get("/profissional/completo")
                .param("page", "0")
                .param("size", "9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").exists());

        verify(profissionalService).listarComFiltros(any(Pageable.class), any(), any(), anyDouble(), any(), any());
    }

    @Test
    @WithMockUser
    void testBuscarCompletoPorid_Success() throws Exception {
        // Arrange
        when(profissionalService.buscarPorId(1L)).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);
        when(portifolioService.converterParaDto(any(Portifolio.class))).thenReturn(new PortifolioDTO());
        when(imagemService.listarPorPortifolio(anyLong())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(anyLong())).thenReturn(Collections.emptyMap());

        // Act & Assert
        mockMvc.perform(get("/profissional/completo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profissional").exists())
                .andExpect(jsonPath("$.usuario").exists())
                .andExpect(jsonPath("$.endereco").exists());

        verify(profissionalService).buscarPorId(1L);
    }

    @Test
    @WithMockUser
    void testBuscarPorId_Success() throws Exception {
        // Arrange
        when(profissionalService.buscarPorId(1L)).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);

        // Act & Assert
        mockMvc.perform(get("/profissional/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProfissional").value(1L))
                .andExpect(jsonPath("$.nota").value(4.5));

        verify(profissionalService).buscarPorId(1L);
    }

    @Test
    @WithMockUser
    void testBuscarPorUsuario_Success() throws Exception {
        // Arrange
        when(profissionalService.buscarPorUsuario(1L)).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);

        // Act & Assert
        mockMvc.perform(get("/profissional/usuario/{idUsuario}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProfissional").value(1L))
                .andExpect(jsonPath("$.nota").value(4.5));

        verify(profissionalService).buscarPorUsuario(1L);
    }

    @Test
    @WithMockUser
    void testVerificarPerfil_Success() throws Exception {
        // Arrange
        when(profissionalService.existePerfil(1L)).thenReturn(true);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(1L);

        // Act & Assert
        mockMvc.perform(get("/profissional/verificar/{idUsuario}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authorizationService).requireUserAccessOrAdmin(1L);
        verify(profissionalService).existePerfil(1L);
    }

    @Test
    @WithMockUser
    void testCriar_Success() throws Exception {
        // Arrange
        when(profissionalService.criar(any(ProfissionalDTO.class))).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/register/profissional")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissionalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProfissional").value(1L))
                .andExpect(jsonPath("$.nota").value(4.5));

        verify(profissionalService).criar(any(ProfissionalDTO.class));
    }

    @Test
    @WithMockUser
    void testCriarProfissionalCompleto_Success() throws Exception {
        // Arrange
        when(profissionalService.criarProfissionalCompleto(any(ProfissionalCriacaoDTO.class))).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);

        // Act & Assert
        mockMvc.perform(post("/auth/register/profissional-completo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissionalCriacaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProfissional").value(1L));

        verify(profissionalService).criarProfissionalCompleto(any(ProfissionalCriacaoDTO.class));
    }

    @Test
    @WithMockUser
    void testAtualizar_Success() throws Exception {
        // Arrange
        when(profissionalService.buscarPorId(1L)).thenReturn(profissional);
        when(profissionalService.atualizar(eq(1L), any(ProfissionalDTO.class))).thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(1L);

        // Act & Assert
        mockMvc.perform(put("/profissional/atualizar/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissionalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProfissional").value(1L))
                .andExpect(jsonPath("$.nota").value(4.5));

        verify(authorizationService).requireUserAccessOrAdmin(1L);
        verify(profissionalService).atualizar(eq(1L), any(ProfissionalDTO.class));
    }

    @Test
    @WithMockUser
    void testAtualizarProfissionalCompleto_Success() throws Exception {
        // Arrange
        when(profissionalService.criarProfissionalCompleto(any(ProfissionalCriacaoDTO.class)))
                .thenReturn(profissional);
        when(profissionalService.converterParaDto(any(Profissional.class))).thenReturn(profissionalDTO);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(1L);

        // Act & Assert
        mockMvc.perform(put("/profissional/usuario/{idUsuario}/atualizar-completo", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profissionalCriacaoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProfissional").value(1L));

        verify(authorizationService).requireUserAccessOrAdmin(1L);
        verify(profissionalService).criarProfissionalCompleto(any(ProfissionalCriacaoDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeletar_AdminSuccess() throws Exception {
        // Arrange
        when(profissionalService.buscarPorId(1L)).thenReturn(profissional);
        doNothing().when(authorizationService).requireAdmin();
        doNothing().when(profissionalService).deletar(1L);

        // Act & Assert
        mockMvc.perform(delete("/profissional/deletar/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(authorizationService).requireAdmin();
        verify(profissionalService).deletar(1L);
    }

    @Test
    @WithMockUser
    void testBuscarPorId_NotFound() throws Exception {
        // Arrange
        when(profissionalService.buscarPorId(999L)).thenThrow(new RuntimeException("Profissional não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/profissional/{id}", 999L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    void testCriarSemAutenticacao_Unauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/register/profissional")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
} 