package inkspiration.backend.service;

import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.EnderecoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProfissionalService")
class ProfissionalServiceTest {

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private PortifolioService portifolioService;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private DisponibilidadeService disponibilidadeService;

    @InjectMocks
    private ProfissionalService profissionalService;

    private Usuario usuario;
    private Endereco endereco;
    private Profissional profissional;
    private ProfissionalDTO profissionalDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("test@test.com");
        usuario.setRole("ROLE_USER");

        endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setCep("12345678");
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");

        profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setNota(new BigDecimal("0.0"));

        profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setIdUsuario(1L);
        profissionalDTO.setIdEndereco(1L);
        profissionalDTO.setNota(new BigDecimal("4.5"));
    }

    @Test
    @DisplayName("Deve criar profissional com sucesso")
    void deveCriarProfissionalComSucesso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario)).thenReturn(false);
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        // When
        Profissional resultado = profissionalService.criar(profissionalDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(endereco, resultado.getEndereco());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).existsByUsuario(usuario);
        verify(enderecoRepository, times(1)).findById(1L);
        verify(usuarioRepository, times(1)).save(usuario);
        verify(profissionalRepository, times(1)).save(any(Profissional.class));
        assertEquals("ROLE_PROF", usuario.getRole());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> profissionalService.criar(profissionalDTO)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando já existe perfil profissional")
    void deveLancarExcecaoQuandoJaExistePerfilProfissional() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> profissionalService.criar(profissionalDTO)
        );

        assertEquals("Já existe um perfil profissional para este usuário", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).existsByUsuario(usuario);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando endereço não encontrado")
    void deveLancarExcecaoQuandoEnderecoNaoEncontrado() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario)).thenReturn(false);
        when(enderecoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> profissionalService.criar(profissionalDTO)
        );

        assertEquals("Endereço não encontrado com ID: 1", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).existsByUsuario(usuario);
        verify(enderecoRepository, times(1)).findById(1L);
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar profissional com nota padrão quando não fornecida")
    void deveCriarProfissionalComNotaPadraoQuandoNaoFornecida() {
        // Given
        profissionalDTO.setNota(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario)).thenReturn(false);
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        // When
        Profissional resultado = profissionalService.criar(profissionalDTO);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve atualizar profissional com sucesso")
    void deveAtualizarProfissionalComSucesso() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));
        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);

        // When
        Profissional resultado = profissionalService.atualizar(1L, profissionalDTO);

        // Then
        assertNotNull(resultado);
        verify(profissionalRepository, times(1)).findById(1L);
        verify(enderecoRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).save(profissional);
    }

    @Test
    @DisplayName("Deve buscar profissional por ID")
    void deveBuscarProfissionalPorId() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));

        // When
        Profissional resultado = profissionalService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(profissional, resultado);
        verify(profissionalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional inexistente")
    void deveLancarExcecaoAoBuscarProfissionalInexistente() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> profissionalService.buscarPorId(1L)
        );

        assertEquals("Profissional não encontrado com ID: 1", exception.getMessage());
        verify(profissionalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar profissional por usuário")
    void deveBuscarProfissionalPorUsuario() {
        // Given
        when(profissionalRepository.findByUsuario_IdUsuario(1L)).thenReturn(Optional.of(profissional));

        // When
        Profissional resultado = profissionalService.buscarPorUsuario(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(profissional, resultado);
        verify(profissionalRepository, times(1)).findByUsuario_IdUsuario(1L);
    }

    @Test
    @DisplayName("Deve verificar se existe perfil profissional")
    void deveVerificarSeExistePerfilProfissional() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario)).thenReturn(true);

        // When
        boolean resultado = profissionalService.existePerfil(1L);

        // Then
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).existsByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve listar profissionais com paginação")
    void deveListarProfissionaisComPaginacao() {
        // Given
        Page<Profissional> pageProfissionais = new PageImpl<>(Collections.singletonList(profissional));
        Pageable pageable = mock(Pageable.class);
        when(profissionalRepository.findAll(pageable)).thenReturn(pageProfissionais);

        // When
        Page<Profissional> resultado = profissionalService.listar(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(profissional, resultado.getContent().get(0));
        verify(profissionalRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve deletar profissional com sucesso")
    void deveDeletarProfissionalComSucesso() {
        // Given
        when(profissionalRepository.findById(1L)).thenReturn(Optional.of(profissional));

        // When
        assertDoesNotThrow(() -> profissionalService.deletar(1L));

        // Then
        verify(profissionalRepository, times(1)).findById(1L);
        verify(profissionalRepository, times(1)).delete(profissional);
    }

    @Test
    @DisplayName("Deve converter profissional para DTO")
    void deveConverterProfissionalParaDTO() {
        // When
        ProfissionalDTO resultado = profissionalService.converterParaDto(profissional);

        // Then
        assertNotNull(resultado);
        assertEquals(profissional.getUsuario().getIdUsuario(), resultado.getIdUsuario());
        assertEquals(profissional.getEndereco().getIdEndereco(), resultado.getIdEndereco());
        assertEquals(profissional.getNota(), resultado.getNota());
    }

    @Test
    @DisplayName("Deve retornar null ao converter profissional nulo")
    void deveRetornarNullAoConverterProfissionalNulo() {
        // When
        ProfissionalDTO resultado = profissionalService.converterParaDto(null);

        // Then
        assertNull(resultado);
    }
} 