package inkspiration.backend.service;

import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.repository.PortifolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PortifolioService")
class PortifolioServiceTest {

    @Mock
    private PortifolioRepository portifolioRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @InjectMocks
    private PortifolioService portifolioService;

    private Portifolio portifolio;
    private PortifolioDTO portifolioDTO;
    private Profissional profissional;

    @BeforeEach
    void setUp() {
        portifolio = new Portifolio();
        portifolio.setIdPortifolio(1L);
        portifolio.setDescricao("Descrição do portfólio");
        portifolio.setExperiencia("5 anos");
        portifolio.setEspecialidade("Realismo");
        portifolio.setWebsite("https://exemplo.com");
        portifolio.setInstagram("@exemplo");
        portifolio.setFacebook("facebook.com/exemplo");
        portifolio.setTwitter("@exemplo");
        portifolio.setTiktok("@exemplo");

        profissional = new Profissional();
        profissional.setIdProfissional(1L);

        portifolioDTO = new PortifolioDTO();
        portifolioDTO.setIdProfissional(1L);
        portifolioDTO.setDescricao("Descrição do portfólio");
        portifolioDTO.setExperiencia("5 anos");
        portifolioDTO.setEspecialidade("Realismo");
        portifolioDTO.setWebsite("https://exemplo.com");
        portifolioDTO.setInstagram("@exemplo");
        portifolioDTO.setFacebook("facebook.com/exemplo");
        portifolioDTO.setTwitter("@exemplo");
        portifolioDTO.setTiktok("@exemplo");
    }

    @Test
    @DisplayName("Deve listar todos os portfólios com paginação")
    void deveListarTodosPortifoliosComPaginacao() {
        // Given
        Page<Portifolio> pagePortifolios = new PageImpl<>(Collections.singletonList(portifolio));
        Pageable pageable = mock(Pageable.class);
        when(portifolioRepository.findAll(pageable)).thenReturn(pagePortifolios);

        // When
        Page<Portifolio> resultado = portifolioService.listarTodos(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(portifolio, resultado.getContent().get(0));
        verify(portifolioRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve criar portfólio sem profissional associado")
    void deveCriarPortifolioSemProfissional() {
        // Given
        portifolioDTO.setIdProfissional(null);
        when(portifolioRepository.save(any(Portifolio.class))).thenReturn(portifolio);

        // When
        Portifolio resultado = portifolioService.criar(portifolioDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(portifolio.getDescricao(), resultado.getDescricao());
        verify(portifolioRepository, times(1)).save(any(Portifolio.class));
        verify(profissionalRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve criar portfólio com profissional associado")
    void deveCriarPortifolioComProfissional() {
        // Given
        when(portifolioRepository.save(any(Portifolio.class))).thenReturn(portifolio);
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        // When
        Portifolio resultado = portifolioService.criar(portifolioDTO);

        // Then
        assertNotNull(resultado);
        verify(portifolioRepository, times(1)).save(any(Portifolio.class));
        verify(profissionalRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).save(profissional);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar portfólio com profissional inexistente")
    void deveLancarExcecaoAoCriarPortifolioComProfissionalInexistente() {
        // Given
        when(portifolioRepository.save(any(Portifolio.class))).thenReturn(portifolio);
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> portifolioService.criar(portifolioDTO)
        );

        assertEquals("Profissional não encontrado com ID: 1", exception.getMessage());
        verify(profissionalRepository, times(1)).findById(1L);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar portfólio sem alterar associação de profissional")
    void deveAtualizarPortifolioSemAlterarAssociacao() {
        // Given
        portifolio.setProfissional(profissional);
        portifolioDTO.setIdProfissional(1L);
        portifolioDTO.setDescricao("Nova descrição");
        
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(portifolioRepository.save(any(Portifolio.class))).thenReturn(portifolio);

        // When
        Portifolio resultado = portifolioService.atualizar(1L, portifolioDTO);

        // Then
        assertNotNull(resultado);
        verify(portifolioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(portifolioRepository, times(1)).save(portifolio);
    }

    @Test
    @DisplayName("Deve atualizar portfólio alterando associação de profissional")
    void deveAtualizarPortifolioAlterandoAssociacao() {
        // Given
        Profissional antigoProfissional = new Profissional();
        antigoProfissional.setIdProfissional(2L);
        portifolio.setProfissional(antigoProfissional);
        
        Profissional novoProfissional = new Profissional();
        novoProfissional.setIdProfissional(1L);
        
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(novoProfissional));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(novoProfissional);
        when(portifolioRepository.save(any(Portifolio.class))).thenReturn(portifolio);

        // When
        Portifolio resultado = portifolioService.atualizar(1L, portifolioDTO);

        // Then
        assertNotNull(resultado);
        verify(portifolioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(2)).save(any(Profissional.class)); // antigo e novo
        verify(portifolioRepository, times(1)).save(portifolio);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar portfólio inexistente")
    void deveLancarExcecaoAoAtualizarPortifolioInexistente() {
        // Given
        when(portifolioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> portifolioService.atualizar(1L, portifolioDTO)
        );

        assertEquals("Portifolio não encontrado com ID: 1", exception.getMessage());
        verify(portifolioRepository, times(1)).findById(1L);
        verify(portifolioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar portfólio por ID com sucesso")
    void deveBuscarPortifolioPorIdComSucesso() {
        // Given
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));

        // When
        Portifolio resultado = portifolioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(portifolio, resultado);
        verify(portifolioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar portfólio inexistente")
    void deveLancarExcecaoAoBuscarPortifolioInexistente() {
        // Given
        when(portifolioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> portifolioService.buscarPorId(1L)
        );

        assertEquals("Portifolio não encontrado com ID: 1", exception.getMessage());
        verify(portifolioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve deletar portfólio sem profissional associado")
    void deveDeletarPortifolioSemProfissional() {
        // Given
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));

        // When
        assertDoesNotThrow(() -> portifolioService.deletar(1L));

        // Then
        verify(portifolioRepository, times(1)).findById(1L);
        verify(portifolioRepository, times(1)).delete(portifolio);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar portfólio com profissional associado")
    void deveDeletarPortifolioComProfissional() {
        // Given
        portifolio.setProfissional(profissional);
        profissional.setPortifolio(portifolio);
        when(portifolioRepository.findById(1L)).thenReturn(Optional.of(portifolio));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        // When
        assertDoesNotThrow(() -> portifolioService.deletar(1L));

        // Then
        verify(portifolioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).save(profissional);
        verify(portifolioRepository, times(1)).delete(portifolio);
        assertNull(profissional.getPortifolio());
    }

    @Test
    @DisplayName("Deve converter portfólio para DTO com sucesso")
    void deveConverterPortifolioParaDTOComSucesso() {
        // Given
        portifolio.setProfissional(profissional);

        // When
        PortifolioDTO resultado = portifolioService.converterParaDto(portifolio);

        // Then
        assertNotNull(resultado);
        assertEquals(portifolio.getIdPortifolio(), resultado.getIdPortifolio());
        assertEquals(profissional.getIdProfissional(), resultado.getIdProfissional());
        assertEquals(portifolio.getDescricao(), resultado.getDescricao());
        assertEquals(portifolio.getExperiencia(), resultado.getExperiencia());
        assertEquals(portifolio.getEspecialidade(), resultado.getEspecialidade());
    }

    @Test
    @DisplayName("Deve retornar null ao converter portfólio nulo")
    void deveRetornarNullAoConverterPortifolioNulo() {
        // When
        PortifolioDTO resultado = portifolioService.converterParaDto(null);

        // Then
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve converter portfólio sem profissional para DTO")
    void deveConverterPortifolioSemProfissionalParaDTO() {
        // When
        PortifolioDTO resultado = portifolioService.converterParaDto(portifolio);

        // Then
        assertNotNull(resultado);
        assertEquals(portifolio.getIdPortifolio(), resultado.getIdPortifolio());
        assertNull(resultado.getIdProfissional());
        assertEquals(portifolio.getDescricao(), resultado.getDescricao());
    }
} 