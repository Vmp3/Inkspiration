package inkspiration.backend.controller.usuarioController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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
@DisplayName("UsuarioController - Testes de Integração")
class UsuarioControllerIntegracaoTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;
    private UsuarioSeguroDTO usuarioSeguroDTO;
    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = criarUsuarioDTO();
        usuarioSeguroDTO = criarUsuarioSeguroDTO();
        usuarioResponseDTO = criarUsuarioResponseDTO();
    }

    @Test
    @DisplayName("Deve integrar busca por ID com autorização bem-sucedida")
    void deveIntegrarBuscaPorIdComAutorizacaoBemSucedida() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarPorIdComAutorizacao(id)).thenReturn(usuarioSeguroDTO);

        // Act
        ResponseEntity<UsuarioSeguroDTO> response = usuarioController.buscarPorId(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getIdUsuario());
        assertEquals("João Silva", response.getBody().getNome());
        assertEquals("***.***.***-35", response.getBody().getCpfMascarado());

        verify(usuarioService).buscarPorIdComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve integrar atualização completa de usuário")
    void deveIntegrarAtualizacaoCompletaDeUsuario() {
        // Arrange
        Long id = 1L;
        UsuarioDTO dtoAtualizado = criarUsuarioDTOAtualizado();
        UsuarioSeguroDTO usuarioAtualizado = criarUsuarioSeguroDTOAtualizado();

        when(usuarioService.atualizarComAutorizacao(id, dtoAtualizado)).thenReturn(usuarioAtualizado);

        // Act
        ResponseEntity<UsuarioSeguroDTO> response = usuarioController.atualizar(id, dtoAtualizado);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Maria Silva Atualizada", response.getBody().getNome());

        verify(usuarioService).atualizarComAutorizacao(id, dtoAtualizado);
    }

    @Test
    @DisplayName("Deve integrar listagem paginada com filtros")
    void deveIntegrarListagemPaginadaComFiltros() {
        // Arrange
        Map<String, Object> responseCompleto = criarResponsePaginadoCompleto();
        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), eq("Maria")))
            .thenReturn(responseCompleto);

        // Act
        ResponseEntity<Map<String, Object>> response = usuarioController.listarTodos(0, 10, "Maria");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("content"));
        assertTrue(body.containsKey("totalElements"));
        assertTrue(body.containsKey("totalPages"));
        assertEquals(2L, body.get("totalElements"));
        assertEquals(1, body.get("totalPages"));

        verify(usuarioService).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), eq("Maria"));
    }

    @Test
    @DisplayName("Deve integrar fluxo completo de inativação e reativação")
    void deveIntegrarFluxoCompletoDeInativacaoEReativacao() {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioService).inativarComAutorizacao(id);
        doNothing().when(usuarioService).reativarComAutorizacao(id);

        // Act - Inativar
        ResponseEntity<String> responseInativar = usuarioController.inativarUsuario(id);

        // Assert - Inativação
        assertNotNull(responseInativar);
        assertEquals(HttpStatus.OK, responseInativar.getStatusCode());
        assertEquals("Usuário inativado com sucesso.", responseInativar.getBody());

        // Act - Reativar
        ResponseEntity<String> responseReativar = usuarioController.reativarUsuario(id);

        // Assert - Reativação
        assertNotNull(responseReativar);
        assertEquals(HttpStatus.OK, responseReativar.getStatusCode());
        assertEquals("Usuário reativado com sucesso.", responseReativar.getBody());

        // Verify
        verify(usuarioService).inativarComAutorizacao(id);
        verify(usuarioService).reativarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve integrar atualização de foto de perfil e validação de token")
    void deveIntegrarAtualizacaoFotoPerfilEValidacaoToken() {
        // Arrange
        Long id = 1L;
        Map<String, String> requestFoto = new HashMap<>();
        requestFoto.put("imagemBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABA");
        
        Map<String, String> requestToken = new HashMap<>();
        requestToken.put("token", "token-valido-123");

        doNothing().when(usuarioService).atualizarFotoPerfilComAutorizacao(id, requestFoto.get("imagemBase64"));
        when(usuarioService.validateTokenComplete(id, requestToken.get("token"))).thenReturn(true);

        // Act - Atualizar foto
        ResponseEntity<Void> responseFoto = usuarioController.atualizarFotoPerfil(id, requestFoto);

        // Assert - Foto
        assertNotNull(responseFoto);
        assertEquals(HttpStatus.OK, responseFoto.getStatusCode());
        assertNull(responseFoto.getBody());

        // Act - Validar token
        ResponseEntity<Map<String, Object>> responseToken = usuarioController.validateToken(id, requestToken);

        // Assert - Token
        assertNotNull(responseToken);
        assertEquals(HttpStatus.OK, responseToken.getStatusCode());
        assertNotNull(responseToken.getBody());
        assertTrue((Boolean) responseToken.getBody().get("valid"));

        // Verify
        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, requestFoto.get("imagemBase64"));
        verify(usuarioService).validateTokenComplete(id, requestToken.get("token"));
    }

    @Test
    @DisplayName("Deve integrar busca de detalhes com dados completos")
    void deveIntegrarBuscaDeDetalhesComDadosCompletos() {
        // Arrange
        Long id = 1L;
        UsuarioResponseDTO detalhesCompletos = criarUsuarioResponseDTOCompleto();
        when(usuarioService.buscarDetalhesComAutorizacao(id)).thenReturn(detalhesCompletos);

        // Act
        ResponseEntity<UsuarioResponseDTO> response = usuarioController.buscarDetalhes(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        UsuarioResponseDTO body = response.getBody();
        assertEquals(id, body.getIdUsuario());
        assertEquals("João Silva", body.getNome());
        assertEquals("11144477735", body.getCpf());
        assertEquals("joao@exemplo.com", body.getEmail());
        assertEquals("01/01/1990", body.getDataNascimento());
        assertEquals("(11) 99999-9999", body.getTelefone());
        assertEquals("ROLE_USER", body.getRole());

        verify(usuarioService).buscarDetalhesComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve integrar exclusão de usuário com verificações")
    void deveIntegrarExclusaoDeUsuarioComVerificacoes() {
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
    @DisplayName("Deve integrar paginação com diferentes tamanhos de página")
    void deveIntegrarPaginacaoComDiferentesTamanhosDePagina() {
        // Arrange
        Map<String, Object> response5Items = criarResponsePaginado(5);
        Map<String, Object> response20Items = criarResponsePaginado(20);

        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), isNull()))
            .thenReturn(response5Items)
            .thenReturn(response20Items);

        // Act - Página com 5 itens
        ResponseEntity<Map<String, Object>> response1 = usuarioController.listarTodos(0, 5, null);

        // Act - Página com 20 itens
        ResponseEntity<Map<String, Object>> response2 = usuarioController.listarTodos(0, 20, null);

        // Assert
        assertNotNull(response1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(5, response1.getBody().get("size"));

        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(20, response2.getBody().get("size"));

        verify(usuarioService, times(2)).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), isNull());
    }

    @Test
    @DisplayName("Deve integrar validação de token com diferentes cenários")
    void deveIntegrarValidacaoDeTokenComDiferentesCenarios() {
        // Arrange
        Long id = 1L;
        Map<String, String> tokenValido = new HashMap<>();
        tokenValido.put("token", "token-valido");
        
        Map<String, String> tokenInvalido = new HashMap<>();
        tokenInvalido.put("token", "token-invalido");

        when(usuarioService.validateTokenComplete(id, "token-valido")).thenReturn(true);
        when(usuarioService.validateTokenComplete(id, "token-invalido")).thenReturn(false);

        // Act & Assert - Token válido
        ResponseEntity<Map<String, Object>> responseValido = usuarioController.validateToken(id, tokenValido);
        assertNotNull(responseValido);
        assertTrue((Boolean) responseValido.getBody().get("valid"));

        // Act & Assert - Token inválido
        ResponseEntity<Map<String, Object>> responseInvalido = usuarioController.validateToken(id, tokenInvalido);
        assertNotNull(responseInvalido);
        assertFalse((Boolean) responseInvalido.getBody().get("valid"));

        verify(usuarioService).validateTokenComplete(id, "token-valido");
        verify(usuarioService).validateTokenComplete(id, "token-invalido");
    }

    @Test
    @DisplayName("Deve integrar múltiplas operações em sequência")
    void deveIntegrarMultiplasOperacoesEmSequencia() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarPorIdComAutorizacao(id)).thenReturn(usuarioSeguroDTO);
        when(usuarioService.atualizarComAutorizacao(eq(id), any(UsuarioDTO.class))).thenReturn(usuarioSeguroDTO);
        doNothing().when(usuarioService).atualizarFotoPerfilComAutorizacao(eq(id), anyString());

        // Act - Buscar usuário
        ResponseEntity<UsuarioSeguroDTO> responseBusca = usuarioController.buscarPorId(id);

        // Act - Atualizar usuário
        ResponseEntity<UsuarioSeguroDTO> responseAtualizar = usuarioController.atualizar(id, usuarioDTO);

        // Act - Atualizar foto
        Map<String, String> requestFoto = new HashMap<>();
        requestFoto.put("imagemBase64", "nova-imagem");
        ResponseEntity<Void> responseFoto = usuarioController.atualizarFotoPerfil(id, requestFoto);

        // Assert
        assertEquals(HttpStatus.OK, responseBusca.getStatusCode());
        assertEquals(HttpStatus.OK, responseAtualizar.getStatusCode());
        assertEquals(HttpStatus.OK, responseFoto.getStatusCode());

        // Verify sequence
        verify(usuarioService).buscarPorIdComAutorizacao(id);
        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, "nova-imagem");
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

    private UsuarioDTO criarUsuarioDTOAtualizado() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(1L);
        dto.setNome("Maria Silva Atualizada");
        dto.setEmail("maria.atualizada@exemplo.com");
        dto.setCpf("11144477735");
        dto.setDataNascimento("15/02/1985");
        dto.setSenha("NovaSenha@456");
        dto.setTelefone("(11) 88888-8888");
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

    private UsuarioSeguroDTO criarUsuarioSeguroDTOAtualizado() {
        UsuarioSeguroDTO dto = new UsuarioSeguroDTO();
        dto.setIdUsuario(1L);
        dto.setNome("Maria Silva Atualizada");
        dto.setCpfMascarado("***.***.***-35");
        dto.setImagemPerfil("nova-imagem-perfil.jpg");
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

    private UsuarioResponseDTO criarUsuarioResponseDTOCompleto() {
        return new UsuarioResponseDTO(
            1L,
            "João Silva",
            "11144477735",
            "joao@exemplo.com",
            "01/01/1990",
            "(11) 99999-9999",
            "imagem-perfil-completa.jpg",
            null, // Endereco pode ser null para este teste
            "ROLE_USER"
        );
    }

    private Map<String, Object> criarResponsePaginadoCompleto() {
        Map<String, Object> response = new HashMap<>();
        response.put("content", Arrays.asList(
            criarUsuarioResponseDTO(),
            new UsuarioResponseDTO(2L, "Maria Silva", "22255588899", "maria@exemplo.com", 
                                  "15/03/1985", "(11) 88888-8888", "maria.jpg", null, "ROLE_USER")
        ));
        response.put("totalElements", 2L);
        response.put("totalPages", 1);
        response.put("currentPage", 0);
        response.put("size", 10);
        response.put("hasNext", false);
        response.put("hasPrevious", false);
        return response;
    }

    private Map<String, Object> criarResponsePaginado(int size) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", Arrays.asList(criarUsuarioResponseDTO()));
        response.put("totalElements", 1L);
        response.put("totalPages", 1);
        response.put("currentPage", 0);
        response.put("size", size);
        response.put("hasNext", false);
        response.put("hasPrevious", false);
        return response;
    }
} 