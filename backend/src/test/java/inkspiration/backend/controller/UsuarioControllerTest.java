package inkspiration.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.dto.UsuarioSeguroDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.UsuarioService;

@WebMvcTest(value = UsuarioController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioDTO usuarioAtualizadoDTO;
    private UsuarioResponseDTO usuarioResponseDTO;
    private UsuarioSeguroDTO usuarioSeguroDTO;
    private String authHeader = "Bearer test-token";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setCpf("12345678901");
        usuario.setNome("Test User");
        usuario.setEmail("test@example.com");
        usuario.setRole("ROLE_USER");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setTelefone("11999999999");

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("Test User");
        usuarioDTO.setCpf("12345678901");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setSenha("password123");
        usuarioDTO.setDataNascimento("01/01/1990");

        usuarioAtualizadoDTO = new UsuarioDTO();
        usuarioAtualizadoDTO.setNome("Test User Updated");
        usuarioAtualizadoDTO.setCpf("12345678901");
        usuarioAtualizadoDTO.setEmail("test.updated@example.com");
        usuarioAtualizadoDTO.setSenha("newPassword123");
        usuarioAtualizadoDTO.setDataNascimento("01/01/1990");

        usuarioResponseDTO = new UsuarioResponseDTO(
            1L, "Test User", "12345678901", "test@example.com", 
            "01/01/1990", "11999999999", null, null, "ROLE_USER"
        );

        usuarioSeguroDTO = UsuarioSeguroDTO.fromUsuario(usuario);

        // Mock JWT service para retornar valores válidos
        when(jwtService.getUserIdFromToken("test-token")).thenReturn(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListarTodos_AdminSuccess() throws Exception {
        // Arrange
        List<UsuarioResponseDTO> usuarios = List.of(usuarioResponseDTO);
        when(service.listarTodosResponse(any(Pageable.class))).thenReturn(usuarios);
        doNothing().when(authorizationService).requireAdmin();

        // Act & Assert
        mockMvc.perform(get("/usuario")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Test User"));

        verify(authorizationService).requireAdmin();
        verify(service).listarTodosResponse(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testListarTodos_UserForbidden() throws Exception {
        // Arrange
        doThrow(new UsuarioException.PermissaoNegadaException("Acesso negado"))
            .when(authorizationService).requireAdmin();

        // Act & Assert
        mockMvc.perform(get("/usuario"))
                .andExpect(status().isForbidden());

        verify(authorizationService).requireAdmin();
    }

    @Test
    @WithMockUser
    void testBuscarPorId_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(service.buscarPorId(userId)).thenReturn(usuario);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(userId);

        // Act & Assert
        mockMvc.perform(get("/usuario/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nome").value("Test User"));

        verify(authorizationService).requireUserAccessOrAdmin(userId);
        verify(service).buscarPorId(userId);
    }

    @Test
    @WithMockUser
    void testBuscarDetalhes_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(service.buscarPorId(userId)).thenReturn(usuario);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(userId);

        // Act & Assert
        mockMvc.perform(get("/usuario/detalhes/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nome").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(authorizationService).requireUserAccessOrAdmin(userId);
        verify(service).buscarPorId(userId);
    }

    @Test
    @WithMockUser
    void testBuscarPorCpf_Success() throws Exception {
        // Arrange
        String cpf = "12345678901";
        when(service.buscarPorCpf(cpf)).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(get("/usuario/buscar-por-cpf/{cpf}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nome").value("Test User"));

        verify(service).buscarPorCpf(cpf);
    }

    @Test
    @WithMockUser
    void testBuscarPorCpf_NotFound() throws Exception {
        // Arrange
        String cpf = "12345678901";
        when(service.buscarPorCpf(cpf)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/usuario/buscar-por-cpf/{cpf}", cpf))
                .andExpect(status().isNotFound());

        verify(service).buscarPorCpf(cpf);
    }

    @Test
    @WithMockUser
    void testAtualizar_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(service.atualizar(eq(userId), any(UsuarioDTO.class))).thenReturn(usuario);
        doNothing().when(authorizationService).requireUserAccessOrAdmin(userId);

        // Act & Assert
        mockMvc.perform(put("/usuario/atualizar/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioAtualizadoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.nome").value("Test User"));

        verify(authorizationService).requireUserAccessOrAdmin(userId);
        verify(service).atualizar(eq(userId), any(UsuarioDTO.class));
    }

    @Test
    @WithMockUser
    void testAtualizar_PermissionDenied() throws Exception {
        // Arrange
        Long userId = 1L;
        doThrow(new UsuarioException.PermissaoNegadaException("Permissão negada"))
            .when(authorizationService).requireUserAccessOrAdmin(userId);

        // Act & Assert
        mockMvc.perform(put("/usuario/atualizar/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioAtualizadoDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Apenas administradores podem alterar roles"));

        verify(authorizationService).requireUserAccessOrAdmin(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testInativarUsuario_AdminSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        doNothing().when(authorizationService).requireAdmin();
        doNothing().when(service).inativar(userId);

        // Act & Assert
        mockMvc.perform(post("/usuario/inativar/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário inativado com sucesso."));

        verify(authorizationService).requireAdmin();
        verify(service).inativar(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testExcluirUsuario_AdminSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        doNothing().when(authorizationService).requireAdmin();
        doNothing().when(service).deletar(userId);

        // Act & Assert
        mockMvc.perform(delete("/usuario/deletar/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário excluído com sucesso."));

        verify(authorizationService).requireAdmin();
        verify(service).deletar(userId);
    }

    @Test
    @WithMockUser
    void testAtualizarFotoPerfil_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        Map<String, String> request = Map.of("imagemBase64", "base64EncodedImage");
        doNothing().when(authorizationService).requireUserAccessOrAdmin(userId);
        doNothing().when(service).atualizarFotoPerfil(userId, "base64EncodedImage");

        // Act & Assert
        mockMvc.perform(put("/usuario/{id}/foto-perfil", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authorizationService).requireUserAccessOrAdmin(userId);
        verify(service).atualizarFotoPerfil(userId, "base64EncodedImage");
    }

    @Test
    @WithMockUser
    void testAtualizarFotoPerfil_BadRequest() throws Exception {
        // Arrange
        Long userId = 1L;
        Map<String, String> request = Map.of("imagemBase64", "");

        // Act & Assert
        mockMvc.perform(put("/usuario/{id}/foto-perfil", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testValidateToken_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        usuario.setTokenAtual("valid-token");
        Map<String, String> request = Map.of("token", "valid-token");
        when(service.buscarPorId(userId)).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/usuario/{id}/validate-token", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        verify(service).buscarPorId(userId);
    }

    @Test
    @WithMockUser
    void testValidateToken_Invalid() throws Exception {
        // Arrange
        Long userId = 1L;
        usuario.setTokenAtual("different-token");
        Map<String, String> request = Map.of("token", "invalid-token");
        when(service.buscarPorId(userId)).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/usuario/{id}/validate-token", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.newToken").value("different-token"));

        verify(service).buscarPorId(userId);
    }

    @Test
    @WithMockUser
    void testValidateToken_UserNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        Map<String, String> request = Map.of("token", "any-token");
        when(service.buscarPorId(userId)).thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(post("/usuario/{id}/validate-token", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

        verify(service).buscarPorId(userId);
    }

    @Test
    @WithMockUser
    void testValidateToken_NoToken() throws Exception {
        // Arrange
        Long userId = 1L;
        Map<String, String> request = Map.of("token", "");

        // Act & Assert
        mockMvc.perform(post("/usuario/{id}/validate-token", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Token não fornecido"));
    }
} 