package inkspiration.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.repository.DisponibilidadeRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DisponibilidadeService")
class DisponibilidadeServiceTest {

    @Mock
    private DisponibilidadeRepository disponibilidadeRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DisponibilidadeService disponibilidadeService;

    private Profissional profissional;
    private Disponibilidade disponibilidade;
    private Map<String, List<Map<String, String>>> horariosMap;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
        profissional.setIdProfissional(1L);

        disponibilidade = new Disponibilidade();
        disponibilidade.setIdDisponibilidade(1L);
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento("{\"Segunda\":[{\"inicio\":\"08:00\",\"fim\":\"12:00\"}]}");

        // Configurar horários de exemplo
        horariosMap = new HashMap<>();
        List<Map<String, String>> horariosDia = new ArrayList<>();
        Map<String, String> periodo = new HashMap<>();
        periodo.put("inicio", "08:00");
        periodo.put("fim", "12:00");
        horariosDia.add(periodo);
        horariosMap.put("Segunda", horariosDia);

        // Configurar mock do ObjectMapper no construtor
        disponibilidadeService = new DisponibilidadeService(disponibilidadeRepository, profissionalRepository);
        // Injetar mock do ObjectMapper via reflection
        try {
            java.lang.reflect.Field field = DisponibilidadeService.class.getDeclaredField("objectMapper");
            field.setAccessible(true);
            field.set(disponibilidadeService, objectMapper);
        } catch (Exception e) {
            // Se não conseguir injetar, usar uma instância real
            disponibilidadeService = new DisponibilidadeService(disponibilidadeRepository, profissionalRepository);
        }
    }

    @Test
    @DisplayName("Deve cadastrar disponibilidade com sucesso")
    void deveCadastrarDisponibilidadeComSucesso() throws JsonProcessingException {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(objectMapper.writeValueAsString(horariosMap)).thenReturn("{\"Segunda\":[{\"inicio\":\"08:00\",\"fim\":\"12:00\"}]}");
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);

        // When
        Disponibilidade resultado = disponibilidadeService.cadastrarDisponibilidade(1L, horariosMap);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(objectMapper, times(1)).writeValueAsString(horariosMap);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
        verify(disponibilidadeRepository, times(1)).save(any(Disponibilidade.class));
    }

    @Test
    @DisplayName("Deve atualizar disponibilidade existente")
    void deveAtualizarDisponibilidadeExistente() throws JsonProcessingException {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(objectMapper.writeValueAsString(horariosMap)).thenReturn("{\"Segunda\":[{\"inicio\":\"08:00\",\"fim\":\"12:00\"}]}");
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);

        // When
        Disponibilidade resultado = disponibilidadeService.cadastrarDisponibilidade(1L, horariosMap);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(objectMapper, times(1)).writeValueAsString(horariosMap);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
        verify(disponibilidadeRepository, times(1)).save(disponibilidade);
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não encontrado")
    void deveLancarExcecaoQuandoProfissionalNaoEncontrado() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> disponibilidadeService.cadastrarDisponibilidade(1L, horariosMap)
        );

        assertEquals("Profissional não encontrado", exception.getMessage());
        verify(profissionalRepository, times(1)).findById(1L);
        verify(disponibilidadeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve obter disponibilidade com sucesso")
    void deveObterDisponibilidadeComSucesso() throws JsonProcessingException {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(horariosMap);

        // When
        Map<String, List<Map<String, String>>> resultado = disponibilidadeService.obterDisponibilidade(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(horariosMap, resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
        verify(objectMapper, times(1)).readValue(anyString(), any(TypeReference.class));
    }

    @Test
    @DisplayName("Deve verificar se profissional está disponível")
    void deveVerificarSeProfissionalEstaDisponivel() throws JsonProcessingException {
        // Given
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 8, 9, 0); // Segunda-feira 09:00
        LocalDateTime fim = LocalDateTime.of(2024, 1, 8, 11, 0);   // Segunda-feira 11:00
        
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(horariosMap);

        // When
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(1L, inicio, fim);

        // Then
        assertTrue(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
    }

    @Test
    @DisplayName("Deve retornar false quando horário de início é após o fim")
    void deveRetornarFalseQuandoHorarioInicioApósFim() throws JsonProcessingException {
        // Given
        LocalDateTime inicio = LocalDateTime.of(2024, 1, 8, 12, 0);
        LocalDateTime fim = LocalDateTime.of(2024, 1, 8, 9, 0);

        // When
        boolean resultado = disponibilidadeService.isProfissionalDisponivel(1L, inicio, fim);

        // Then
        assertFalse(resultado);
        verify(profissionalRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve buscar disponibilidade por ID como DTO")
    void deveBuscarDisponibilidadePorIdComoDTO() {
        // Given
        when(disponibilidadeRepository.findById(1L)).thenReturn(Optional.of(disponibilidade));

        // When
        DisponibilidadeDTO resultado = disponibilidadeService.buscarPorIdDTO(1L);

        // Then
        assertNotNull(resultado);
        verify(disponibilidadeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar disponibilidade por profissional como DTO")
    void deveBuscarDisponibilidadePorProfissionalComoDTO() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));

        // When
        DisponibilidadeDTO resultado = disponibilidadeService.buscarPorProfissionalDTO(1L);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não tem disponibilidade cadastrada")
    void deveLancarExcecaoQuandoProfissionalNaoTemDisponibilidadeCadastrada() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> disponibilidadeService.buscarPorProfissionalDTO(1L)
        );

        assertEquals("Disponibilidade não cadastrada", exception.getMessage());
        verify(profissionalRepository, times(1)).findById(1L);
        verify(disponibilidadeRepository, times(1)).findByProfissional(profissional);
    }

    @Test
    @DisplayName("Deve cadastrar disponibilidade e retornar DTO")
    void deveCadastrarDisponibilidadeERetornarDTO() throws JsonProcessingException {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(objectMapper.writeValueAsString(horariosMap)).thenReturn("{\"Segunda\":[{\"inicio\":\"08:00\",\"fim\":\"12:00\"}]}");
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.empty());
        when(disponibilidadeRepository.save(any(Disponibilidade.class))).thenReturn(disponibilidade);

        // When
        DisponibilidadeDTO resultado = disponibilidadeService.cadastrarDisponibilidadeDTO(1L, horariosMap);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(objectMapper, times(1)).writeValueAsString(horariosMap);
        verify(disponibilidadeRepository, times(1)).save(any(Disponibilidade.class));
    }

    @Test
    @DisplayName("Deve testar conversão de dias da semana")
    void deveTestarConversaoDiasSemana() throws JsonProcessingException {
        // Testar diferentes dias da semana
        Map<String, List<Map<String, String>>> horariosCompletos = new HashMap<>();
        
        // Adicionar horários para todos os dias
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        for (String dia : dias) {
            List<Map<String, String>> horariosDia = new ArrayList<>();
            Map<String, String> periodo = new HashMap<>();
            periodo.put("inicio", "09:00");
            periodo.put("fim", "17:00");
            horariosDia.add(periodo);
            horariosCompletos.put(dia, horariosDia);
        }
        
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(horariosCompletos);

        // Testar cada dia da semana
        LocalDateTime[] inicios = {
            LocalDateTime.of(2024, 1, 8, 10, 0),  // Segunda
            LocalDateTime.of(2024, 1, 9, 10, 0),  // Terça
            LocalDateTime.of(2024, 1, 10, 10, 0), // Quarta
            LocalDateTime.of(2024, 1, 11, 10, 0), // Quinta
            LocalDateTime.of(2024, 1, 12, 10, 0), // Sexta
            LocalDateTime.of(2024, 1, 13, 10, 0), // Sábado
            LocalDateTime.of(2024, 1, 14, 10, 0)  // Domingo
        };
        
        for (LocalDateTime inicio : inicios) {
            LocalDateTime fim = inicio.plusHours(1);
            boolean resultado = disponibilidadeService.isProfissionalDisponivel(1L, inicio, fim);
            assertTrue(resultado, "Deveria estar disponível em " + inicio.getDayOfWeek());
        }
    }

    @Test
    @DisplayName("Deve lidar com múltiplos períodos no mesmo dia")
    void deveLidarComMultiplosPeriodosMesmoDia() throws JsonProcessingException {
        // Given - Configurar dois períodos para segunda-feira
        Map<String, List<Map<String, String>>> horariosMultiplos = new HashMap<>();
        List<Map<String, String>> periodos = new ArrayList<>();
        
        Map<String, String> manha = new HashMap<>();
        manha.put("inicio", "08:00");
        manha.put("fim", "12:00");
        periodos.add(manha);
        
        Map<String, String> tarde = new HashMap<>();
        tarde.put("inicio", "14:00");
        tarde.put("fim", "18:00");
        periodos.add(tarde);
        
        horariosMultiplos.put("Segunda", periodos);
        
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(disponibilidadeRepository.findByProfissional(profissional)).thenReturn(Optional.of(disponibilidade));
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(horariosMultiplos);

        // When - Testar horário da manhã
        LocalDateTime inicioManha = LocalDateTime.of(2024, 1, 8, 9, 0);
        LocalDateTime fimManha = LocalDateTime.of(2024, 1, 8, 11, 0);
        boolean resultadoManha = disponibilidadeService.isProfissionalDisponivel(1L, inicioManha, fimManha);

        // When - Testar horário da tarde
        LocalDateTime inicioTarde = LocalDateTime.of(2024, 1, 8, 15, 0);
        LocalDateTime fimTarde = LocalDateTime.of(2024, 1, 8, 17, 0);
        boolean resultadoTarde = disponibilidadeService.isProfissionalDisponivel(1L, inicioTarde, fimTarde);

        // When - Testar horário de intervalo (não disponível)
        LocalDateTime inicioIntervalo = LocalDateTime.of(2024, 1, 8, 12, 30);
        LocalDateTime fimIntervalo = LocalDateTime.of(2024, 1, 8, 13, 30);
        boolean resultadoIntervalo = disponibilidadeService.isProfissionalDisponivel(1L, inicioIntervalo, fimIntervalo);

        // Then
        assertTrue(resultadoManha, "Deveria estar disponível de manhã");
        assertTrue(resultadoTarde, "Deveria estar disponível de tarde");
        assertFalse(resultadoIntervalo, "Não deveria estar disponível no intervalo");
    }
} 