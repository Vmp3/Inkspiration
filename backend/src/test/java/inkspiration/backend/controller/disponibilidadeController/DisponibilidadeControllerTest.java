package inkspiration.backend.controller.disponibilidadeController;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import inkspiration.backend.controller.DisponibilidadeController;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeAcessoException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeCadastroException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeConsultaException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.service.ProfissionalService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DisponibilidadeController - Testes Completos")
class DisponibilidadeControllerTest {

    @Mock
    private DisponibilidadeService disponibilidadeService;

    @Mock
    private ProfissionalService profissionalService;

    @InjectMocks
    private DisponibilidadeController disponibilidadeController;

    private DisponibilidadeDTO disponibilidadeDTO;
    private Profissional profissional;
    private Usuario usuario;
    private Map<String, List<Map<String, String>>> horariosRequest;
    private Map<String, List<Map<String, String>>> horariosResponse;

    @BeforeEach
    void setUp() {
        setupEntidades();
        setupDTOs();
        setupMapas();
    }

    private void setupEntidades() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        
        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
    }

    private void setupDTOs() {
        disponibilidadeDTO = new DisponibilidadeDTO();
        disponibilidadeDTO.setIdDisponibilidade(1L);
        disponibilidadeDTO.setIdProfissional(1L);
        disponibilidadeDTO.setHrAtendimento("08:00-17:00");
    }

    private void setupMapas() {
        Map<String, String> horario1 = new HashMap<>();
        horario1.put("inicio", "08:00");
        horario1.put("fim", "12:00");
        
        Map<String, String> horario2 = new HashMap<>();
        horario2.put("inicio", "14:00");
        horario2.put("fim", "18:00");
        
        horariosRequest = new HashMap<>();
        horariosRequest.put("segunda", Arrays.asList(horario1, horario2));
        horariosRequest.put("terca", Arrays.asList(horario1));
        
        horariosResponse = new HashMap<>();
        horariosResponse.put("segunda", Arrays.asList(horario1, horario2));
        horariosResponse.put("terca", Arrays.asList(horario1));
    }

    // =================== TESTES DE CADASTRO DE DISPONIBILIDADE ===================

    @Test
    @DisplayName("Deve cadastrar disponibilidade com sucesso")
    void deveCadastrarDisponibilidadeComSucesso() {
        // Arrange
        Long idProfissional = 1L;
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(profissional);
        when(disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosRequest), eq(1L)))
                .thenReturn(disponibilidadeDTO);

        // Act
        ResponseEntity<DisponibilidadeDTO> response = 
                disponibilidadeController.cadastrarDisponibilidade(idProfissional, horariosRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(disponibilidadeDTO.getIdDisponibilidade(), response.getBody().getIdDisponibilidade());
        assertEquals(disponibilidadeDTO.getIdProfissional(), response.getBody().getIdProfissional());

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService).cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosRequest), eq(1L));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar disponibilidade com profissional não encontrado")
    void deveLancarExcecaoAoCadastrarDisponibilidadeComProfissionalNaoEncontrado() {
        // Arrange
        Long idProfissional = 999L;
        when(profissionalService.buscarPorId(idProfissional))
                .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            disponibilidadeController.cadastrarDisponibilidade(idProfissional, horariosRequest);
        });

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService, never()).cadastrarDisponibilidadeDTOComValidacao(any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar disponibilidade com dados inválidos")
    void deveLancarExcecaoAoCadastrarDisponibilidadeComDadosInvalidos() {
        // Arrange
        Long idProfissional = 1L;
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(profissional);
        when(disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosRequest), eq(1L)))
                .thenThrow(new DisponibilidadeCadastroException("Dados de disponibilidade inválidos"));

        // Act & Assert
        assertThrows(DisponibilidadeCadastroException.class, () -> {
            disponibilidadeController.cadastrarDisponibilidade(idProfissional, horariosRequest);
        });

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService).cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosRequest), eq(1L));
    }

    // =================== TESTES DE OBTENÇÃO DE DISPONIBILIDADE ===================

    @Test
    @DisplayName("Deve obter disponibilidade com sucesso")
    void deveObterDisponibilidadeComSucesso() {
        // Arrange
        Long idProfissional = 1L;
        when(disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional))
                .thenReturn(horariosResponse);

        // Act
        ResponseEntity<Map<String, List<Map<String, String>>>> response = 
                disponibilidadeController.obterDisponibilidade(idProfissional);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(horariosResponse, response.getBody());
        assertTrue(response.getBody().containsKey("segunda"));
        assertTrue(response.getBody().containsKey("terca"));

        verify(disponibilidadeService).obterDisponibilidadeComValidacao(idProfissional);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter disponibilidade com profissional inexistente")
    void deveLancarExcecaoAoObterDisponibilidadeComProfissionalInexistente() {
        // Arrange
        Long idProfissional = 999L;
        when(disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional))
                .thenThrow(new DisponibilidadeConsultaException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(DisponibilidadeConsultaException.class, () -> {
            disponibilidadeController.obterDisponibilidade(idProfissional);
        });

        verify(disponibilidadeService).obterDisponibilidadeComValidacao(idProfissional);
    }

    // =================== TESTES DE OBTENÇÃO DE DISPONIBILIDADE DTO ===================

    @Test
    @DisplayName("Deve obter disponibilidade DTO com sucesso")
    void deveObterDisponibilidadeDTOComSucesso() {
        // Arrange
        Long idProfissional = 1L;
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(profissional);
        when(disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, 1L))
                .thenReturn(disponibilidadeDTO);

        // Act
        ResponseEntity<DisponibilidadeDTO> response = 
                disponibilidadeController.obterDisponibilidadeDTO(idProfissional);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(disponibilidadeDTO.getIdDisponibilidade(), response.getBody().getIdDisponibilidade());
        assertEquals(disponibilidadeDTO.getIdProfissional(), response.getBody().getIdProfissional());

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService).buscarPorProfissionalDTOComValidacao(idProfissional, 1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter DTO com profissional não encontrado")
    void deveLancarExcecaoAoObterDTOComProfissionalNaoEncontrado() {
        // Arrange
        Long idProfissional = 999L;
        when(profissionalService.buscarPorId(idProfissional))
                .thenThrow(new ProfissionalNaoEncontradoException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(ProfissionalNaoEncontradoException.class, () -> {
            disponibilidadeController.obterDisponibilidadeDTO(idProfissional);
        });

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService, never()).buscarPorProfissionalDTOComValidacao(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção de acesso ao obter DTO")
    void deveLancarExcecaoDeAcessoAoObterDTO() {
        // Arrange
        Long idProfissional = 1L;
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(profissional);
        when(disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, 1L))
                .thenThrow(new DisponibilidadeAcessoException("Acesso negado"));

        // Act & Assert
        assertThrows(DisponibilidadeAcessoException.class, () -> {
            disponibilidadeController.obterDisponibilidadeDTO(idProfissional);
        });

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService).buscarPorProfissionalDTOComValidacao(idProfissional, 1L);
    }

    // =================== TESTES DE VERIFICAÇÃO DE HORÁRIOS DISPONÍVEIS ===================

    @Test
    @DisplayName("Deve obter horários disponíveis com sucesso")
    void deveObterHorariosDisponiveisComSucesso() {
        // Arrange
        Long idProfissional = 1L;
        LocalDate data = LocalDate.now().plusDays(1);
        String tipoServico = TipoServico.TATUAGEM_PEQUENA.name();
        List<String> horariosDisponiveis = Arrays.asList("09:00", "10:00", "14:00", "15:00");
        
        when(disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico))
                .thenReturn(horariosDisponiveis);

        // Act
        ResponseEntity<?> response = disponibilidadeController.obterHorariosDisponiveis(
                idProfissional, data, tipoServico);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof List);
        @SuppressWarnings("unchecked")
        List<String> horarios = (List<String>) response.getBody();
        assertEquals(4, horarios.size());
        assertTrue(horarios.contains("09:00"));
        assertTrue(horarios.contains("14:00"));

        verify(disponibilidadeService).obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico);
    }

    @Test
    @DisplayName("Deve retornar NO_CONTENT quando não há horários disponíveis")
    void deveRetornarNoContentQuandoNaoHaHorariosDisponiveis() {
        // Arrange
        Long idProfissional = 1L;
        LocalDate data = LocalDate.now().plusDays(1);
        String tipoServico = TipoServico.TATUAGEM_GRANDE.name();
        List<String> horariosVazios = new ArrayList<>();
        
        when(disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico))
                .thenReturn(horariosVazios);

        // Act
        ResponseEntity<?> response = disponibilidadeController.obterHorariosDisponiveis(
                idProfissional, data, tipoServico);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> mensagem = (Map<String, String>) response.getBody();
        assertEquals("Não há horários disponíveis para este dia e tipo de serviço", 
                mensagem.get("mensagem"));

        verify(disponibilidadeService).obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar horários com profissional inexistente")
    void deveLancarExcecaoAoVerificarHorariosComProfissionalInexistente() {
        // Arrange
        Long idProfissional = 999L;
        LocalDate data = LocalDate.now().plusDays(1);
        String tipoServico = TipoServico.SESSAO.name();
        
        when(disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico))
                .thenThrow(new DisponibilidadeConsultaException("Profissional não encontrado"));

        // Act & Assert
        assertThrows(DisponibilidadeConsultaException.class, () -> {
            disponibilidadeController.obterHorariosDisponiveis(idProfissional, data, tipoServico);
        });

        verify(disponibilidadeService).obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico);
    }

    @Test
    @DisplayName("Deve verificar horários com diferentes tipos de serviço")
    void deveVerificarHorariosComDiferentesTiposDeServico() {
        // Arrange
        Long idProfissional = 1L;
        LocalDate data = LocalDate.now().plusDays(1);
        String tipoServicoPiercing = TipoServico.TATUAGEM_MEDIA.name();
        List<String> horariosDisponiveis = Arrays.asList("11:00", "16:00");
        
        when(disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServicoPiercing))
                .thenReturn(horariosDisponiveis);

        // Act
        ResponseEntity<?> response = disponibilidadeController.obterHorariosDisponiveis(
                idProfissional, data, tipoServicoPiercing);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<String> horarios = (List<String>) response.getBody();
        assertEquals(2, horarios.size());
        assertTrue(horarios.contains("11:00"));
        assertTrue(horarios.contains("16:00"));

        verify(disponibilidadeService).obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServicoPiercing);
    }

    @Test
    @DisplayName("Deve verificar horários com data específica")
    void deveVerificarHorariosComDataEspecifica() {
        // Arrange
        Long idProfissional = 1L;
        LocalDate dataEspecifica = LocalDate.of(2024, 12, 25);
        String tipoServico = TipoServico.TATUAGEM_GRANDE.name();
        List<String> horariosDisponiveis = Arrays.asList("13:00");
        
        when(disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, dataEspecifica, tipoServico))
                .thenReturn(horariosDisponiveis);

        // Act
        ResponseEntity<?> response = disponibilidadeController.obterHorariosDisponiveis(
                idProfissional, dataEspecifica, tipoServico);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<String> horarios = (List<String>) response.getBody();
        assertEquals(1, horarios.size());
        assertEquals("13:00", horarios.get(0));

        verify(disponibilidadeService).obterHorariosDisponiveisComValidacao(
                idProfissional, dataEspecifica, tipoServico);
    }

    // =================== TESTES DE CENÁRIOS DE BORDA ===================

    @Test
    @DisplayName("Deve lidar com mapa de horários vazio")
    void deveLidarComMapaDeHorariosVazio() {
        // Arrange
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> horariosVazios = new HashMap<>();
        when(profissionalService.buscarPorId(idProfissional)).thenReturn(profissional);
        when(disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosVazios), eq(1L)))
                .thenReturn(disponibilidadeDTO);

        // Act
        ResponseEntity<DisponibilidadeDTO> response = 
                disponibilidadeController.cadastrarDisponibilidade(idProfissional, horariosVazios);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(profissionalService).buscarPorId(idProfissional);
        verify(disponibilidadeService).cadastrarDisponibilidadeDTOComValidacao(
                eq(idProfissional), eq(horariosVazios), eq(1L));
    }

    @Test
    @DisplayName("Deve obter disponibilidade vazia")
    void deveObterDisponibilidadeVazia() {
        // Arrange
        Long idProfissional = 1L;
        Map<String, List<Map<String, String>>> disponibilidadeVazia = new HashMap<>();
        when(disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional))
                .thenReturn(disponibilidadeVazia);

        // Act
        ResponseEntity<Map<String, List<Map<String, String>>>> response = 
                disponibilidadeController.obterDisponibilidade(idProfissional);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(disponibilidadeService).obterDisponibilidadeComValidacao(idProfissional);
    }
}