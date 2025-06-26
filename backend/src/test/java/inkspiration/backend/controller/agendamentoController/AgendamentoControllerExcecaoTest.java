package inkspiration.backend.controller.agendamentoController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import inkspiration.backend.controller.AgendamentoController;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.exception.agendamento.AgendamentoNaoAutorizadoException;
import inkspiration.backend.exception.agendamento.AutoAgendamentoException;
import inkspiration.backend.exception.agendamento.CancelamentoNaoPermitidoException;
import inkspiration.backend.exception.agendamento.DataInvalidaAgendamentoException;
import inkspiration.backend.exception.agendamento.HorarioConflitanteException;
import inkspiration.backend.exception.agendamento.ProfissionalIndisponivelException;
import inkspiration.backend.exception.agendamento.TipoServicoInvalidoException;
import inkspiration.backend.exception.agendamento.TokenInvalidoException;
import inkspiration.backend.service.AgendamentoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoController - Testes de Exceção")
class AgendamentoControllerExcecaoTest {

    @Mock
    private AgendamentoService agendamentoService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AgendamentoController agendamentoController;

    private AgendamentoRequestDTO agendamentoRequestDTO;
    private AgendamentoUpdateDTO agendamentoUpdateDTO;

    @BeforeEach
    void setUp() {
        agendamentoRequestDTO = criarAgendamentoRequestDTO();
        agendamentoUpdateDTO = criarAgendamentoUpdateDTO();
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com auto agendamento")
    void deveLancarExcecaoAoCriarAgendamentoComAutoAgendamento() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenThrow(new AutoAgendamentoException("Não é possível agendar para si mesmo"));

        // Act & Assert
        assertThrows(AutoAgendamentoException.class, () -> {
            agendamentoController.criarAgendamento(agendamentoRequestDTO);
        });

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com horário conflitante")
    void deveLancarExcecaoAoCriarAgendamentoComHorarioConflitante() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenThrow(new HorarioConflitanteException("Horário já ocupado"));

        // Act & Assert
        assertThrows(HorarioConflitanteException.class, () -> {
            agendamentoController.criarAgendamento(agendamentoRequestDTO);
        });

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com profissional indisponível")
    void deveLancarExcecaoAoCriarAgendamentoComProfissionalIndisponivel() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenThrow(new ProfissionalIndisponivelException("Profissional não disponível"));

        // Act & Assert
        assertThrows(ProfissionalIndisponivelException.class, () -> {
            agendamentoController.criarAgendamento(agendamentoRequestDTO);
        });

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com data inválida")
    void deveLancarExcecaoAoCriarAgendamentoComDataInvalida() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenThrow(new DataInvalidaAgendamentoException("Data inválida"));

        // Act & Assert
        assertThrows(DataInvalidaAgendamentoException.class, () -> {
            agendamentoController.criarAgendamento(agendamentoRequestDTO);
        });

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com tipo de serviço inválido")
    void deveLancarExcecaoAoCriarAgendamentoComTipoServicoInvalido() {
        // Arrange
        when(agendamentoService.criarAgendamentoComValidacao(agendamentoRequestDTO))
            .thenThrow(new TipoServicoInvalidoException("Tipo de serviço inválido"));

        // Act & Assert
        assertThrows(TipoServicoInvalidoException.class, () -> {
            agendamentoController.criarAgendamento(agendamentoRequestDTO);
        });

        verify(agendamentoService).criarAgendamentoComValidacao(agendamentoRequestDTO);
    }

    @Test
    @DisplayName("Deve lançar exceção com parâmetros de paginação inválidos")
    void deveLancarExcecaoComParametrosDePaginacaoInvalidos() {
        // Act & Assert - PageRequest.of(-1, 0) lança IllegalArgumentException diretamente
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarPorUsuario(1L, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar agendamento não autorizado")
    void deveLancarExcecaoAoBuscarAgendamentoNaoAutorizado() {
        // Arrange
        Long id = 1L;
        when(agendamentoService.buscarPorIdComValidacao(id))
            .thenThrow(new AgendamentoNaoAutorizadoException("Acesso negado"));

        // Act & Assert
        assertThrows(AgendamentoNaoAutorizadoException.class, () -> {
            agendamentoController.buscarPorId(id);
        });

        verify(agendamentoService).buscarPorIdComValidacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar agendamentos de usuário não autorizado")
    void deveLancarExcecaoAoListarAgendamentosDeUsuarioNaoAutorizado() {
        // Arrange
        Long idUsuario = 1L;
        when(agendamentoService.listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class)))
            .thenThrow(new AgendamentoNaoAutorizadoException("Acesso negado"));

        // Act & Assert
        assertThrows(AgendamentoNaoAutorizadoException.class, () -> {
            agendamentoController.listarPorUsuario(idUsuario, 0, 10);
        });

        verify(agendamentoService).listarPorUsuarioComValidacao(eq(idUsuario), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar agendamentos de profissional não autorizado")
    void deveLancarExcecaoAoListarAgendamentosDeProfissionalNaoAutorizado() {
        // Arrange
        Long idProfissional = 1L;
        when(agendamentoService.listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class)))
            .thenThrow(new AgendamentoNaoAutorizadoException("Acesso negado"));

        // Act & Assert
        assertThrows(AgendamentoNaoAutorizadoException.class, () -> {
            agendamentoController.listarPorProfissional(idProfissional, 0, 10);
        });

        verify(agendamentoService).listarPorProfissionalComValidacao(eq(idProfissional), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar agendamento não autorizado")
    void deveLancarExcecaoAoAtualizarAgendamentoNaoAutorizado() {
        // Arrange
        Long id = 1L;
        when(agendamentoService.atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication))
            .thenThrow(new AgendamentoNaoAutorizadoException("Acesso negado"));

        // Act & Assert
        assertThrows(AgendamentoNaoAutorizadoException.class, () -> {
            agendamentoController.atualizarAgendamento(id, agendamentoUpdateDTO, authentication);
        });

        verify(agendamentoService).atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar agendamento com horário conflitante")
    void deveLancarExcecaoAoAtualizarAgendamentoComHorarioConflitante() {
        // Arrange
        Long id = 1L;
        when(agendamentoService.atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication))
            .thenThrow(new HorarioConflitanteException("Horário já ocupado"));

        // Act & Assert
        assertThrows(HorarioConflitanteException.class, () -> {
            agendamentoController.atualizarAgendamento(id, agendamentoUpdateDTO, authentication);
        });

        verify(agendamentoService).atualizarAgendamentoComAutenticacao(id, agendamentoUpdateDTO, authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir agendamento não autorizado")
    void deveLancarExcecaoAoExcluirAgendamentoNaoAutorizado() {
        // Arrange
        Long id = 1L;
        doThrow(new AgendamentoNaoAutorizadoException("Acesso negado"))
            .when(agendamentoService).excluirAgendamentoComValidacao(id);

        // Act & Assert
        assertThrows(AgendamentoNaoAutorizadoException.class, () -> {
            agendamentoController.excluirAgendamento(id);
        });

        verify(agendamentoService).excluirAgendamentoComValidacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir agendamento com cancelamento não permitido")
    void deveLancarExcecaoAoExcluirAgendamentoComCancelamentoNaoPermitido() {
        // Arrange
        Long id = 1L;
        doThrow(new CancelamentoNaoPermitidoException("Cancelamento não permitido"))
            .when(agendamentoService).excluirAgendamentoComValidacao(id);

        // Act & Assert
        assertThrows(CancelamentoNaoPermitidoException.class, () -> {
            agendamentoController.excluirAgendamento(id);
        });

        verify(agendamentoService).excluirAgendamentoComValidacao(id);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status com token inválido")
    void deveLancarExcecaoAoAtualizarStatusComTokenInvalido() {
        // Arrange
        Long id = 1L;
        String status = "CONFIRMADO";
        when(agendamentoService.atualizarStatusAgendamentoComAutenticacao(id, status, authentication))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.atualizarStatusAgendamento(id, status, authentication);
        });

        verify(agendamentoService).atualizarStatusAgendamentoComAutenticacao(id, status, authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar meus agendamentos com token inválido")
    void deveLancarExcecaoAoListarMeusAgendamentosComTokenInvalido() {
        // Arrange
        when(agendamentoService.listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.listarMeusAgendamentos(authentication, 0, 10);
        });

        verify(agendamentoService).listarMeusAgendamentosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar agendamentos futuros com token inválido")
    void deveLancarExcecaoAoListarAgendamentosFuturosComTokenInvalido() {
        // Arrange
        when(agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.listarMeusAgendamentosFuturos(authentication, 0, 10);
        });

        verify(agendamentoService).listarMeusAgendamentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar agendamentos passados com token inválido")
    void deveLancarExcecaoAoListarAgendamentosPassadosComTokenInvalido() {
        // Arrange
        when(agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.listarMeusAgendamentosPassados(authentication, 0, 10);
        });

        verify(agendamentoService).listarMeusAgendamentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao exportar PDF com token inválido")
    void deveLancarExcecaoAoExportarPDFComTokenInvalido() {
        // Arrange
        Integer ano = 2024;
        when(agendamentoService.exportarAgendamentosPDFComAutenticacao(ano, authentication))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.exportarAgendamentosPDF(ano, authentication);
        });

        verify(agendamentoService).exportarAgendamentosPDFComAutenticacao(ano, authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar atendimentos futuros com token inválido")
    void deveLancarExcecaoAoListarAtendimentosFuturosComTokenInvalido() {
        // Arrange
        when(agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.listarMeusAtendimentosFuturos(authentication, 0, 10);
        });

        verify(agendamentoService).listarMeusAtendimentosFuturosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar atendimentos passados com token inválido")
    void deveLancarExcecaoAoListarAtendimentosPassadosComTokenInvalido() {
        // Arrange
        when(agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class)))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.listarMeusAtendimentosPassados(authentication, 0, 10);
        });

        verify(agendamentoService).listarMeusAtendimentosPassadosComAutenticacao(eq(authentication), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao exportar atendimentos PDF com token inválido")
    void deveLancarExcecaoAoExportarAtendimentosPDFComTokenInvalido() {
        // Arrange
        Integer ano = 2024;
        Integer mes = 6;
        when(agendamentoService.exportarAtendimentosPDFComAutenticacao(ano, mes, authentication))
            .thenThrow(new TokenInvalidoException("Token inválido"));

        // Act & Assert
        assertThrows(TokenInvalidoException.class, () -> {
            agendamentoController.exportarAtendimentosPDF(ano, mes, authentication);
        });

        verify(agendamentoService).exportarAtendimentosPDFComAutenticacao(ano, mes, authentication);
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarPorProfissional")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarPorProfissional() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarPorProfissional(1L, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarMeusAgendamentos")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarMeusAgendamentos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarMeusAgendamentos(authentication, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarMeusAgendamentosFuturos")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarMeusAgendamentosFuturos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarMeusAgendamentosFuturos(authentication, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarMeusAgendamentosPassados")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarMeusAgendamentosPassados() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarMeusAgendamentosPassados(authentication, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarMeusAtendimentosFuturos")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarMeusAtendimentosFuturos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarMeusAtendimentosFuturos(authentication, -1, 0);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção com paginação inválida em listarMeusAtendimentosPassados")
    void deveLancarExcecaoComPaginacaoInvalidaEmListarMeusAtendimentosPassados() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            agendamentoController.listarMeusAtendimentosPassados(authentication, -1, 0);
        });
    }

    // Métodos auxiliares
    private AgendamentoRequestDTO criarAgendamentoRequestDTO() {
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão no braço direito com detalhes em cores");
        dto.setDtInicio(LocalDateTime.now().plusDays(7));
        dto.setValor(new BigDecimal("300.00"));
        dto.setIdProfissional(1L);
        dto.setIdUsuario(2L);
        return dto;
    }

    private AgendamentoUpdateDTO criarAgendamentoUpdateDTO() {
        AgendamentoUpdateDTO dto = new AgendamentoUpdateDTO();
        dto.setTipoServico("TATUAGEM");
        dto.setDescricao("Tatuagem de dragão atualizada com mais detalhes");
        dto.setDtInicio(LocalDateTime.now().plusDays(8));
        return dto;
    }
} 