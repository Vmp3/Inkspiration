package inkspiration.backend.controller.usuarioController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.UsuarioController;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.dto.UsuarioSeguroDTO;
import inkspiration.backend.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController - Testes de Casos de Sucesso")
class UsuarioControllerCoreTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private UsuarioSeguroDTO usuarioSeguroDTO;
    private UsuarioResponseDTO usuarioResponseDTO;
    private Map<String, Object> paginatedResponse;

    @BeforeEach
    void setUp() {
        usuarioDTO = criarUsuarioDTO();
        usuarioSeguroDTO = criarUsuarioSeguroDTO();
        usuarioResponseDTO = criarUsuarioResponseDTO();
        paginatedResponse = criarPaginatedResponse();
    }

    @Test
    @DisplayName("Deve listar usuários com paginação com sucesso")
    void deveListarUsuariosComPaginacaoComSucesso() {
        // Arrange
        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), anyString()))
            .thenReturn(paginatedResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.listarTodos(0, 10, "João");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("content"));
        assertTrue(response.getBody().containsKey("totalElements"));

        verify(usuarioService).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), eq("João"));
    }

    @Test
    @DisplayName("Deve listar usuários sem termo de busca com sucesso")
    void deveListarUsuariosSemTermoDeBuscaComSucesso() {
        // Arrange
        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), isNull()))
            .thenReturn(paginatedResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.listarTodos(0, 10, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarPorIdComAutorizacao(id)).thenReturn(usuarioSeguroDTO);

        // Act
        ResponseEntity<UsuarioSeguroDTO> response = usuarioController.buscarPorId(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioSeguroDTO.getIdUsuario(), response.getBody().getIdUsuario());
        assertEquals(usuarioSeguroDTO.getNome(), response.getBody().getNome());

        verify(usuarioService).buscarPorIdComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve buscar detalhes do usuário com sucesso")
    void deveBuscarDetalhesDoUsuarioComSucesso() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarDetalhesComAutorizacao(id)).thenReturn(usuarioResponseDTO);

        // Act
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.buscarDetalhes(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioResponseDTO.getIdUsuario(), response.getBody().getIdUsuario());
        assertEquals(usuarioResponseDTO.getNome(), response.getBody().getNome());
        assertEquals(usuarioResponseDTO.getEmail(), response.getBody().getEmail());

        verify(usuarioService).buscarDetalhesComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Arrange
        Long id = 1L;
        when(usuarioService.atualizarComAutorizacao(id, usuarioDTO)).thenReturn(usuarioSeguroDTO);

        // Act
        ResponseEntity<UsuarioSeguroDTO> response = usuarioController.atualizar(id, usuarioDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioSeguroDTO.getIdUsuario(), response.getBody().getIdUsuario());

        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve inativar usuário com sucesso")
    void deveInativarUsuarioComSucesso() {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioService).inativarComAutorizacao(id);

        // Act
        ResponseEntity<String> response = usuarioController.inativarUsuario(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário inativado com sucesso.", response.getBody());

        verify(usuarioService).inativarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve reativar usuário com sucesso")
    void deveReativarUsuarioComSucesso() {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioService).reativarComAutorizacao(id);

        // Act
        ResponseEntity<String> response = usuarioController.reativarUsuario(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário reativado com sucesso.", response.getBody());

        verify(usuarioService).reativarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve excluir usuário com sucesso")
    void deveExcluirUsuarioComSucesso() {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioService).deletarComAutorizacao(id);

        // Act
        ResponseEntity<String> response = usuarioController.excluirUsuario(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuário excluído com sucesso.", response.getBody());

        verify(usuarioService).deletarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve atualizar foto de perfil com sucesso")
    void deveAtualizarFotoDePerfilComSucesso() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("imagemBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABA...");
        
        doNothing().when(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));

        // Act
        ResponseEntity<Void> response = usuarioController.atualizarFotoPerfil(id, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));
    }

    @Test
    @DisplayName("Deve validar token com sucesso - token válido")
    void deveValidarTokenComSucessoTokenValido() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("token", "valid-token-123");
        
        when(usuarioService.validateTokenComplete(id, "valid-token-123")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.validateToken(id, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("valid"));
        assertTrue((Boolean) response.getBody().get("valid"));

        verify(usuarioService).validateTokenComplete(id, "valid-token-123");
    }

    @Test
    @DisplayName("Deve validar token com sucesso - token inválido")
    void deveValidarTokenComSucessoTokenInvalido() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("token", "invalid-token-123");
        
        when(usuarioService.validateTokenComplete(id, "invalid-token-123")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.validateToken(id, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("valid"));
        assertFalse((Boolean) response.getBody().get("valid"));

        verify(usuarioService).validateTokenComplete(id, "invalid-token-123");
    }

    @Test
    @DisplayName("Deve listar com paginação customizada")
    void deveListarComPaginacaoCustomizada() {
        // Arrange
        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), anyString()))
            .thenReturn(paginatedResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.listarTodos(2, 5, "test");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(usuarioService).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), eq("test"));
    }

    // Métodos auxiliares
    private UsuarioDTO criarUsuarioDTO() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(1L);
        dto.setNome("João Silva");
        dto.setEmail("joao@exemplo.com");
        dto.setCpf("11144477735");
        dto.setDataNascimento("01/01/1990");
        dto.setSenha("MinhaSenh@123");
        dto.setTelefone("(11) 99999-9999");
        dto.setRole("ROLE_USER");
        return dto;
    }

    private UsuarioSeguroDTO criarUsuarioSeguroDTO() {
        UsuarioSeguroDTO dto = new UsuarioSeguroDTO();
        dto.setIdUsuario(1L);
        dto.setNome("João Silva");
        dto.setCpfMascarado("***.***.***-35");
        dto.setImagemPerfil("imagem-perfil.jpg");
        dto.setRole("ROLE_USER");
        dto.setIdEndereco(1L);
        return dto;
    }

    private UsuarioResponseDTO criarUsuarioResponseDTO() {
        return new UsuarioResponseDTO(
            1L,
            "João Silva",
            "11144477735",
            "joao@exemplo.com",
            "01/01/1990",
            "(11) 99999-9999",
            "imagem-perfil.jpg",
            null,
            "ROLE_USER"
        );
    }

    private Map<String, Object> criarPaginatedResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", java.util.Arrays.asList(usuarioResponseDTO));
        response.put("totalElements", 1L);
        response.put("totalPages", 1);
        response.put("currentPage", 0);
        response.put("size", 10);
        response.put("hasNext", false);
        response.put("hasPrevious", false);
        return response;
    }
} 