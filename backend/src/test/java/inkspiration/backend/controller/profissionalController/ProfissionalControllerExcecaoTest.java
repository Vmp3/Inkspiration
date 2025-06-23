package inkspiration.backend.controller.profissionalController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.controller.ProfissionalController;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.exception.profissional.ProfissionalAcessoNegadoException;
import inkspiration.backend.exception.profissional.DadosCompletosProfissionalException;
import inkspiration.backend.service.ProfissionalService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalController - Testes de Exceção")
class ProfissionalControllerExcecaoTest {

    @Mock
    private ProfissionalService profissionalService;

    @InjectMocks
    private ProfissionalController profissionalController;

    private ProfissionalDTO profissionalDTO;
    private ProfissionalCriacaoDTO profissionalCriacaoDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        profissionalDTO = criarProfissionalDTO();
        profissionalCriacaoDTO = criarProfissionalCriacaoDTO();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional não encontrado")
    void deveLancarExcecaoAoBuscarProfissionalNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        when(profissionalService.buscarPorId(idInexistente))
            .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            profissionalController.buscarPorId(idInexistente);
        });

        verify(profissionalService).buscarPorId(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional completo não encontrado")
    void deveLancarExcecaoAoBuscarProfissionalCompletoNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        when(profissionalService.buscarCompletoComValidacao(idInexistente))
            .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            profissionalController.buscarCompletoPorid(idInexistente);
        });

        verify(profissionalService).buscarCompletoComValidacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional por usuário sem acesso")
    void deveLancarExcecaoAoBuscarProfissionalPorUsuarioSemAcesso() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.buscarPorUsuarioComAutorizacao(idUsuario))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.buscarPorUsuario(idUsuario);
        });

        verify(profissionalService).buscarPorUsuarioComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional completo por usuário sem acesso")
    void deveLancarExcecaoAoBuscarProfissionalCompletoPorUsuarioSemAcesso() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.buscarProfissionalCompleto(idUsuario);
        });

        verify(profissionalService).buscarProfissionalCompletoComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar perfil sem acesso")
    void deveLancarExcecaoAoVerificarPerfilSemAcesso() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.verificarPerfilComAutorizacao(idUsuario))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.verificarPerfil(idUsuario);
        });

        verify(profissionalService).verificarPerfilComAutorizacao(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar tipos de serviço por profissional não encontrado")
    void deveLancarExcecaoAoListarTiposDeServicoPorProfissionalNaoEncontrado() {
        // Arrange
        Long idProfissionalInexistente = 999L;
        when(profissionalService.listarTiposServicoPorProfissionalComValidacao(idProfissionalInexistente))
            .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            profissionalController.listarTiposServicoPorProfissional(idProfissionalInexistente);
        });

        verify(profissionalService).listarTiposServicoPorProfissionalComValidacao(idProfissionalInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar profissional com dados inválidos")
    void deveLancarExcecaoAoCriarProfissionalComDadosInvalidos() {
        // Arrange
        when(profissionalService.criarProfissionalCompletoComValidacao(profissionalCriacaoDTO))
            .thenThrow(new DadosCompletosProfissionalException("Dados inválidos"));

        // Act & Assert
        assertThrows(DadosCompletosProfissionalException.class, () -> {
            profissionalController.criarProfissionalCompleto(profissionalCriacaoDTO);
        });

        verify(profissionalService).criarProfissionalCompletoComValidacao(profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar profissional sem acesso")
    void deveLancarExcecaoAoAtualizarProfissionalSemAcesso() {
        // Arrange
        Long id = 1L;
        when(profissionalService.atualizarComAutorizacao(id, profissionalDTO))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.atualizar(id, profissionalDTO);
        });

        verify(profissionalService).atualizarComAutorizacao(id, profissionalDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar profissional não encontrado")
    void deveLancarExcecaoAoAtualizarProfissionalNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        when(profissionalService.atualizarComAutorizacao(idInexistente, profissionalDTO))
            .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            profissionalController.atualizar(idInexistente, profissionalDTO);
        });

        verify(profissionalService).atualizarComAutorizacao(idInexistente, profissionalDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar profissional completo sem acesso")
    void deveLancarExcecaoAoAtualizarProfissionalCompletoSemAcesso() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.atualizarProfissionalCompleto(idUsuario, profissionalCriacaoDTO);
        });

        verify(profissionalService).atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar profissional completo com dados inválidos")
    void deveLancarExcecaoAoAtualizarProfissionalCompletoComDadosInvalidos() {
        // Arrange
        Long idUsuario = 1L;
        when(profissionalService.atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO))
            .thenThrow(new DadosCompletosProfissionalException("Dados inválidos"));

        // Act & Assert
        assertThrows(DadosCompletosProfissionalException.class, () -> {
            profissionalController.atualizarProfissionalCompleto(idUsuario, profissionalCriacaoDTO);
        });

        verify(profissionalService).atualizarProfissionalCompletoComAutorizacao(idUsuario, profissionalCriacaoDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar profissional completo com imagens sem acesso")
    void deveLancarExcecaoAoAtualizarProfissionalCompletoComImagensSemAcesso() {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        when(profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData))
            .thenThrow(new ProfissionalAcessoNegadoException("Acesso negado"));

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.atualizarProfissionalCompletoComImagens(idUsuario, requestData);
        });

        verify(profissionalService).atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar profissional sem acesso")
    void deveLancarExcecaoAoDeletarProfissionalSemAcesso() {
        // Arrange
        Long id = 1L;
        doThrow(new ProfissionalAcessoNegadoException("Acesso negado"))
            .when(profissionalService).deletarComAutorizacao(id);

        // Act & Assert
        assertThrows(ProfissionalAcessoNegadoException.class, () -> {
            profissionalController.deletar(id);
        });

        verify(profissionalService).deletarComAutorizacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar profissional não encontrado")
    void deveLancarExcecaoAoDeletarProfissionalNaoEncontrado() {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"))
            .when(profissionalService).deletarComAutorizacao(idInexistente);

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            profissionalController.deletar(idInexistente);
        });

        verify(profissionalService).deletarComAutorizacao(idInexistente);
    }

    @Test
    @DisplayName("Deve lançar exceção com parâmetros inválidos na listagem completa")
    void deveLancarExcecaoComParametrosInvalidosNaListagemCompleta() {
        // Act & Assert - PageRequest.of(-1, 0) lança IllegalArgumentException diretamente
        assertThrows(IllegalArgumentException.class, () -> {
            profissionalController.listarCompleto(
                -1, 0, null, null, -1.0, null, "ordenacaoInvalida");
        });
        
        // Não há verificação de service pois a exceção é lançada antes de chegar ao service
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar profissional completo e falhar na busca posterior")
    void deveLancarExcecaoAoCriarProfissionalCompletoEFalharNaBuscaPosterior() {
        // Arrange
        when(profissionalService.criarProfissionalCompletoComValidacao(profissionalCriacaoDTO))
            .thenReturn(profissionalDTO);
        when(profissionalService.buscarProfissionalCompletoComAutorizacao(profissionalCriacaoDTO.getIdUsuario()))
            .thenThrow(new RuntimeException("Erro interno"));

        // Act
        var response = profissionalController.criarProfissionalCompleto(profissionalCriacaoDTO);

        // Assert - Deve retornar apenas dados básicos quando busca completa falha
        assertNotNull(response);
        assertTrue(response.getBody().containsKey("profissional"));
        
        verify(profissionalService).criarProfissionalCompletoComValidacao(profissionalCriacaoDTO);
        verify(profissionalService).buscarProfissionalCompletoComAutorizacao(profissionalCriacaoDTO.getIdUsuario());
    }

    // Métodos auxiliares
    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdProfissional(1L);
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("4.5"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }

    private ProfissionalCriacaoDTO criarProfissionalCriacaoDTO() {
        ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        dto.setDescricao("Descrição detalhada do profissional com mais de 20 caracteres para atender à validação");
        return dto;
    }
} 