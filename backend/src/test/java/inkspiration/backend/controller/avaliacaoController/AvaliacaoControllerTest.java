package inkspiration.backend.controller.avaliacaoController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.AvaliacaoController;
import inkspiration.backend.service.AvaliacaoService;
import inkspiration.backend.dto.AvaliacaoDTO;
import inkspiration.backend.exception.avaliacao.*;
import inkspiration.backend.exception.agendamento.AgendamentoNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvaliacaoController - Testes Completos")
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    private AvaliacaoDTO avaliacaoDTO;
    private final Long ID_AGENDAMENTO = 1L;

    @BeforeEach
    void setUp() {
        // Setup mock AvaliacaoDTO
        avaliacaoDTO = new AvaliacaoDTO();
        avaliacaoDTO.setIdAvaliacao(1L);
        avaliacaoDTO.setDescricao("Excelente atendimento, profissional muito atencioso e competente");
        avaliacaoDTO.setRating(5);
        avaliacaoDTO.setIdAgendamento(ID_AGENDAMENTO);
    }

    // Testes para criarAvaliacao()
    @Test
    @DisplayName("Deve criar avaliação com sucesso")
    void deveCriarAvaliacaoComSucesso() {
        // Arrange
        when(avaliacaoService.criarAvaliacao(any(AvaliacaoDTO.class)))
            .thenReturn(avaliacaoDTO);

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.criarAvaliacao(avaliacaoDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(avaliacaoDTO, response.getBody());
        verify(avaliacaoService).criarAvaliacao(avaliacaoDTO);
    }

    @Test
    @DisplayName("Deve retornar bad request ao tentar criar avaliação com erro")
    void deveRetornarBadRequestAoTentarCriarAvaliacaoComErro() {
        // Arrange
        when(avaliacaoService.criarAvaliacao(any(AvaliacaoDTO.class)))
            .thenThrow(new RuntimeException("Erro ao criar avaliação"));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.criarAvaliacao(avaliacaoDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Testes para buscarAvaliacaoPorAgendamento()
    @Test
    @DisplayName("Deve buscar avaliação por agendamento com sucesso")
    void deveBuscarAvaliacaoPorAgendamentoComSucesso() {
        // Arrange
        when(avaliacaoService.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO))
            .thenReturn(Optional.of(avaliacaoDTO));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(avaliacaoDTO, response.getBody());
        verify(avaliacaoService).buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO);
    }

    @Test
    @DisplayName("Deve retornar not found quando avaliação não existe")
    void deveRetornarNotFoundQuandoAvaliacaoNaoExiste() {
        // Arrange
        when(avaliacaoService.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO))
            .thenReturn(Optional.empty());

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar bad request ao buscar avaliação com erro")
    void deveRetornarBadRequestAoBuscarAvaliacaoComErro() {
        // Arrange
        when(avaliacaoService.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO))
            .thenThrow(new RuntimeException("Erro ao buscar avaliação"));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.buscarAvaliacaoPorAgendamento(ID_AGENDAMENTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Testes para podeAvaliar()
    @Test
    @DisplayName("Deve retornar true quando pode avaliar")
    void deveRetornarTrueQuandoPodeAvaliar() {
        // Arrange
        when(avaliacaoService.podeAvaliar(ID_AGENDAMENTO))
            .thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = avaliacaoController.podeAvaliar(ID_AGENDAMENTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(avaliacaoService).podeAvaliar(ID_AGENDAMENTO);
    }

    @Test
    @DisplayName("Deve retornar false quando não pode avaliar")
    void deveRetornarFalseQuandoNaoPodeAvaliar() {
        // Arrange
        when(avaliacaoService.podeAvaliar(ID_AGENDAMENTO))
            .thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = avaliacaoController.podeAvaliar(ID_AGENDAMENTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar bad request ao verificar se pode avaliar com erro")
    void deveRetornarBadRequestAoVerificarSePodeAvaliarComErro() {
        // Arrange
        when(avaliacaoService.podeAvaliar(ID_AGENDAMENTO))
            .thenThrow(new RuntimeException("Erro ao verificar se pode avaliar"));

        // Act
        ResponseEntity<Boolean> response = avaliacaoController.podeAvaliar(ID_AGENDAMENTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Testes para exceções específicas
    @Test
    @DisplayName("Deve retornar bad request quando agendamento não encontrado")
    void deveRetornarBadRequestQuandoAgendamentoNaoEncontrado() {
        // Arrange
        when(avaliacaoService.criarAvaliacao(any(AvaliacaoDTO.class)))
            .thenThrow(new AgendamentoNaoEncontradoException("Agendamento não encontrado"));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.criarAvaliacao(avaliacaoDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar bad request quando avaliação não permitida")
    void deveRetornarBadRequestQuandoAvaliacaoNaoPermitida() {
        // Arrange
        when(avaliacaoService.criarAvaliacao(any(AvaliacaoDTO.class)))
            .thenThrow(new AvaliacaoNaoPermitidaException("Avaliação não permitida"));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.criarAvaliacao(avaliacaoDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar bad request quando avaliação já existe")
    void deveRetornarBadRequestQuandoAvaliacaoJaExiste() {
        // Arrange
        when(avaliacaoService.criarAvaliacao(any(AvaliacaoDTO.class)))
            .thenThrow(new AvaliacaoJaExisteException("Avaliação já existe"));

        // Act
        ResponseEntity<AvaliacaoDTO> response = avaliacaoController.criarAvaliacao(avaliacaoDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
} 