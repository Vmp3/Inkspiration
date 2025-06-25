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

import inkspiration.backend.controller.UsuarioController;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.usuario.InvalidProfileImageException;
import inkspiration.backend.exception.usuario.TelefoneValidationException;
import inkspiration.backend.exception.usuario.TokenValidationException;
import inkspiration.backend.exception.usuario.UserAccessDeniedException;
import inkspiration.backend.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController - Testes de Exceção")
class UsuarioControllerExcecaoTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = criarUsuarioDTO();
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar usuários sem autorização")
    void deveLancarExcecaoAoListarUsuariosSemAutorizacao() {
        // Arrange
        when(usuarioService.listarTodosComPaginacaoComAutorizacao(any(Pageable.class), anyString()))
            .thenThrow(new UsuarioException.PermissaoNegadaException("Acesso negado"));

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.listarTodos(0, 10, "João");
        });

        verify(usuarioService).listarTodosComPaginacaoComAutorizacao(any(Pageable.class), eq("João"));
    }

    @Test
    @DisplayName("Deve lançar exceção com parâmetros de paginação inválidos")
    void deveLancarExcecaoComParametrosDePaginacaoInvalidos() {
        // Act & Assert - PageRequest.of(-1, 0) lança IllegalArgumentException diretamente
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioController.listarTodos(-1, 0, null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário não encontrado")
    void deveLancarExcecaoAoBuscarUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        when(usuarioService.buscarPorIdComAutorizacao(idInexistente))
            .thenThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.buscarPorId(idInexistente);
        });

        verify(usuarioService).buscarPorIdComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário sem autorização")
    void deveLancarExcecaoAoBuscarUsuarioSemAutorizacao() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarPorIdComAutorizacao(id))
            .thenThrow(new UserAccessDeniedException("Acesso negado"));

        // Act & Assert
        assertThrows(UserAccessDeniedException.class, () -> {
            usuarioController.buscarPorId(id);
        });

        verify(usuarioService).buscarPorIdComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhes de usuário não encontrado")
    void deveLancarExcecaoAoBuscarDetalhesDeUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        when(usuarioService.buscarDetalhesComAutorizacao(idInexistente))
            .thenThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.buscarDetalhes(idInexistente);
        });

        verify(usuarioService).buscarDetalhesComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhes sem autorização")
    void deveLancarExcecaoAoBuscarDetalhesSemAutorizacao() {
        // Arrange
        Long id = 1L;
        when(usuarioService.buscarDetalhesComAutorizacao(id))
            .thenThrow(new UsuarioException.PermissaoNegadaException("Acesso negado"));

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.buscarDetalhes(id);
        });

        verify(usuarioService).buscarDetalhesComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário com email já existente")
    void deveLancarExcecaoAoAtualizarUsuarioComEmailJaExistente() {
        // Arrange
        Long id = 1L;
        when(usuarioService.atualizarComAutorizacao(id, usuarioDTO))
            .thenThrow(new UsuarioException.EmailJaExisteException("Email já cadastrado"));

        // Act & Assert
        assertThrows(UsuarioException.EmailJaExisteException.class, () -> {
            usuarioController.atualizar(id, usuarioDTO);
        });

        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário com CPF já existente")
    void deveLancarExcecaoAoAtualizarUsuarioComCpfJaExistente() {
        // Arrange
        Long id = 1L;
        when(usuarioService.atualizarComAutorizacao(id, usuarioDTO))
            .thenThrow(new UsuarioException.CpfJaExisteException("CPF já cadastrado"));

        // Act & Assert
        assertThrows(UsuarioException.CpfJaExisteException.class, () -> {
            usuarioController.atualizar(id, usuarioDTO);
        });

        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário sem autorização")
    void deveLancarExcecaoAoAtualizarUsuarioSemAutorizacao() {
        // Arrange
        Long id = 1L;
        when(usuarioService.atualizarComAutorizacao(id, usuarioDTO))
            .thenThrow(new UsuarioException.PermissaoNegadaException("Acesso negado"));

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.atualizar(id, usuarioDTO);
        });

        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário com telefone inválido")
    void deveLancarExcecaoAoAtualizarUsuarioComTelefoneInvalido() {
        // Arrange
        Long id = 1L;
        when(usuarioService.atualizarComAutorizacao(id, usuarioDTO))
            .thenThrow(new TelefoneValidationException("Telefone inválido"));

        // Act & Assert
        assertThrows(TelefoneValidationException.class, () -> {
            usuarioController.atualizar(id, usuarioDTO);
        });

        verify(usuarioService).atualizarComAutorizacao(id, usuarioDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar usuário sem permissão de admin")
    void deveLancarExcecaoAoInativarUsuarioSemPermissaoDeAdmin() {
        // Arrange
        Long id = 1L;
        doThrow(new UsuarioException.PermissaoNegadaException("Apenas administradores podem realizar esta operação"))
            .when(usuarioService).inativarComAutorizacao(id);

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.inativarUsuario(id);
        });

        verify(usuarioService).inativarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar usuário não encontrado")
    void deveLancarExcecaoAoInativarUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"))
            .when(usuarioService).inativarComAutorizacao(idInexistente);

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.inativarUsuario(idInexistente);
        });

        verify(usuarioService).inativarComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar usuário sem permissão de admin")
    void deveLancarExcecaoAoReativarUsuarioSemPermissaoDeAdmin() {
        // Arrange
        Long id = 1L;
        doThrow(new UsuarioException.PermissaoNegadaException("Apenas administradores podem realizar esta operação"))
            .when(usuarioService).reativarComAutorizacao(id);

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.reativarUsuario(id);
        });

        verify(usuarioService).reativarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao reativar usuário não encontrado")
    void deveLancarExcecaoAoReativarUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"))
            .when(usuarioService).reativarComAutorizacao(idInexistente);

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.reativarUsuario(idInexistente);
        });

        verify(usuarioService).reativarComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário sem permissão de admin")
    void deveLancarExcecaoAoExcluirUsuarioSemPermissaoDeAdmin() {
        // Arrange
        Long id = 1L;
        doThrow(new UsuarioException.PermissaoNegadaException("Apenas administradores podem realizar esta operação"))
            .when(usuarioService).deletarComAutorizacao(id);

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.excluirUsuario(id);
        });

        verify(usuarioService).deletarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário não encontrado")
    void deveLancarExcecaoAoExcluirUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"))
            .when(usuarioService).deletarComAutorizacao(idInexistente);

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.excluirUsuario(idInexistente);
        });

        verify(usuarioService).deletarComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar foto de perfil com imagem inválida")
    void deveLancarExcecaoAoAtualizarFotoDePerfilComImagemInvalida() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("imagemBase64", "imagem-inválida");
        
        doThrow(new InvalidProfileImageException("Imagem de perfil inválida"))
            .when(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));

        // Act & Assert
        assertThrows(InvalidProfileImageException.class, () -> {
            usuarioController.atualizarFotoPerfil(id, request);
        });

        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar foto de perfil sem autorização")
    void deveLancarExcecaoAoAtualizarFotoDePerfilSemAutorizacao() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("imagemBase64", "data:image/jpeg;base64,/9j/4AAQSkZJRgABA...");
        
        doThrow(new UsuarioException.PermissaoNegadaException("Acesso negado"))
            .when(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));

        // Act & Assert
        assertThrows(UsuarioException.PermissaoNegadaException.class, () -> {
            usuarioController.atualizarFotoPerfil(id, request);
        });

        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, request.get("imagemBase64"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token inválido")
    void deveLancarExcecaoAoValidarTokenInvalido() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        request.put("token", "token-inválido");
        
        when(usuarioService.validateTokenComplete(id, "token-inválido"))
            .thenThrow(new TokenValidationException("Token inválido"));

        // Act & Assert
        assertThrows(TokenValidationException.class, () -> {
            usuarioController.validateToken(id, request);
        });

        verify(usuarioService).validateTokenComplete(id, "token-inválido");
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token para usuário não encontrado")
    void deveLancarExcecaoAoValidarTokenParaUsuarioNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        Map<String, String> request = new HashMap<>();
        request.put("token", "token-válido");
        
        when(usuarioService.validateTokenComplete(idInexistente, "token-válido"))
            .thenThrow(new UsuarioException.UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(UsuarioException.UsuarioNaoEncontradoException.class, () -> {
            usuarioController.validateToken(idInexistente, request);
        });

        verify(usuarioService).validateTokenComplete(idInexistente, "token-válido");
    }

    @Test
    @DisplayName("Deve lançar exceção com mapa de request vazio para foto de perfil")
    void deveLancarExcecaoComMapaDeRequestVazioParaFotoDePerfil() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        // Mapa vazio - sem imagemBase64
        
        doThrow(new InvalidProfileImageException("Imagem não fornecida"))
            .when(usuarioService).atualizarFotoPerfilComAutorizacao(id, null);

        // Act & Assert
        assertThrows(InvalidProfileImageException.class, () -> {
            usuarioController.atualizarFotoPerfil(id, request);
        });

        verify(usuarioService).atualizarFotoPerfilComAutorizacao(id, null);
    }

    @Test
    @DisplayName("Deve lançar exceção com mapa de request vazio para validação de token")
    void deveLancarExcecaoComMapaDeRequestVazioParaValidacaoDeToken() {
        // Arrange
        Long id = 1L;
        Map<String, String> request = new HashMap<>();
        // Mapa vazio - sem token
        
        when(usuarioService.validateTokenComplete(id, null))
            .thenThrow(new TokenValidationException("Token não fornecido"));

        // Act & Assert
        assertThrows(TokenValidationException.class, () -> {
            usuarioController.validateToken(id, request);
        });

        verify(usuarioService).validateTokenComplete(id, null);
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
} 