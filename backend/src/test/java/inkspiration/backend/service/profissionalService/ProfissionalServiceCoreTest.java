package inkspiration.backend.service.profissionalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.profissional.EnderecoNaoEncontradoException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.repository.EnderecoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.service.EnderecoService;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.service.PortfolioService;
import inkspiration.backend.service.ProfissionalService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceCoreTest {

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private DisponibilidadeService disponibilidadeService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ImagemService imagemService;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private ProfissionalService profissionalService;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(authorizationService).requireAdmin();
        lenient().doNothing().when(enderecoService).validarEndereco(any());
    }

    @Test
    @DisplayName("Deve buscar profissional por ID com sucesso")
    void deveBuscarProfissionalPorIdComSucesso() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));

        
        Profissional resultado = profissionalService.buscarPorId(idProfissional);

        
        assertNotNull(resultado);
        assertEquals(idProfissional, resultado.getIdProfissional());
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional inexistente")
    void deveLancarExcecaoAoBuscarProfissionalInexistente() {
        
        Long idProfissional = 999L;
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.empty());

        
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> profissionalService.buscarPorId(idProfissional)
        );

        
        assertTrue(exception.getMessage().contains("Profissional não encontrado com ID: " + idProfissional));
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve buscar profissional por usuário com sucesso")
    void deveBuscarProfissionalPorUsuarioComSucesso() {
        
        Long idUsuario = 1L;
        Profissional profissional = criarProfissional(1L);
        profissional.getUsuario().setIdUsuario(idUsuario);
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));

        
        Profissional resultado = profissionalService.buscarPorUsuario(idUsuario);

        
        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getUsuario().getIdUsuario());
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar profissional por usuário inexistente")
    void deveLancarExcecaoAoBuscarProfissionalPorUsuarioInexistente() {
        
        Long idUsuario = 999L;
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.empty());

        
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> profissionalService.buscarPorUsuario(idUsuario)
        );

        
        assertTrue(exception.getMessage().contains("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve verificar se existe perfil profissional")
    void deveVerificarSeExistePerfilProfissional() {
        
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(true);

        
        boolean existe = profissionalService.existePerfil(idUsuario);

        
        assertTrue(existe);
        verify(usuarioRepository).findById(idUsuario);
        verify(profissionalRepository).existsByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve retornar false quando não existe perfil profissional")
    void deveRetornarFalseQuandoNaoExistePerfilProfissional() {
        
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(false);

        
        boolean existe = profissionalService.existePerfil(idUsuario);

        
        assertFalse(existe);
        verify(usuarioRepository).findById(idUsuario);
        verify(profissionalRepository).existsByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar perfil com usuário inexistente")
    void deveLancarExcecaoAoVerificarPerfilComUsuarioInexistente() {
        
        Long idUsuario = 999L;
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.empty());

        
        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> profissionalService.existePerfil(idUsuario)
        );

        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve listar profissionais com paginação")
    void deveListarProfissionaisComPaginacao() {
        
        Pageable pageable = PageRequest.of(0, 10);
        List<Profissional> profissionais = Arrays.asList(
            criarProfissional(1L),
            criarProfissional(2L),
            criarProfissional(3L)
        );
        Page<Profissional> pageProfissionais = new PageImpl<>(profissionais, pageable, profissionais.size());
        
        when(profissionalRepository.findAll(pageable))
            .thenReturn(pageProfissionais);

        
        Page<Profissional> resultado = profissionalService.listar(pageable);

        
        assertNotNull(resultado);
        assertEquals(3, resultado.getContent().size());
        assertEquals(3, resultado.getTotalElements());
        verify(profissionalRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar profissional com sucesso")
    void deveAtualizarProfissionalComSucesso() {
        
        Long idProfissional = 1L;
        Long novoIdEndereco = 2L;
        Profissional profissionalExistente = criarProfissional(idProfissional);
        Endereco novoEndereco = criarEndereco(novoIdEndereco);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(novoIdEndereco);
        dto.setNota(new BigDecimal("4.5"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(novoIdEndereco))
            .thenReturn(Optional.of(novoEndereco));
        when(profissionalRepository.save(profissionalExistente))
            .thenReturn(profissionalExistente);

        
        Profissional resultado = profissionalService.atualizar(idProfissional, dto);

        
        assertNotNull(resultado);
        assertEquals(novoEndereco, resultado.getEndereco());
        assertEquals(new BigDecimal("4.5"), resultado.getNota());
        verify(profissionalRepository).findById(idProfissional);
        verify(enderecoRepository).findById(novoIdEndereco);
        verify(enderecoService).validarEndereco(novoEndereco);
        verify(profissionalRepository).save(profissionalExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com endereço inexistente")
    void deveLancarExcecaoAoAtualizarComEnderecoInexistente() {
        
        Long idProfissional = 1L;
        Long idEnderecoInexistente = 999L;
        Profissional profissionalExistente = criarProfissional(idProfissional);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(idEnderecoInexistente);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(idEnderecoInexistente))
            .thenReturn(Optional.empty());

        
        EnderecoNaoEncontradoException exception = assertThrows(
            EnderecoNaoEncontradoException.class,
            () -> profissionalService.atualizar(idProfissional, dto)
        );

        
        assertTrue(exception.getMessage().contains("Endereço não encontrado com ID: " + idEnderecoInexistente));
        verify(enderecoRepository).findById(idEnderecoInexistente);
    }

    @Test
    @DisplayName("Deve deletar profissional com sucesso")
    void deveDeletarProfissionalComSucesso() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        doNothing().when(profissionalRepository).delete(profissional);

        
        profissionalService.deletar(idProfissional);

        
        verify(profissionalRepository).findById(idProfissional);
        verify(profissionalRepository).delete(profissional);
    }

    @Test
    @DisplayName("Deve converter entidade para DTO corretamente")
    void deveConverterEntidadeParaDTOCorretamente() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        profissional.setNota(new BigDecimal("4.2"));

        
        ProfissionalDTO resultado = profissionalService.converterParaDto(profissional);

        
        assertNotNull(resultado);
        assertEquals(idProfissional, resultado.getIdProfissional());
        assertEquals(profissional.getUsuario().getIdUsuario(), resultado.getIdUsuario());
        assertEquals(profissional.getEndereco().getIdEndereco(), resultado.getIdEndereco());
        assertEquals(new BigDecimal("4.2"), resultado.getNota());
    }

    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void deveBuscarEnderecoPorIdComSucesso() {
        
        Long idEndereco = 1L;
        Endereco endereco = criarEndereco(idEndereco);
        
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.of(endereco));

        
        Endereco resultado = profissionalService.buscarEnderecoPorId(idEndereco);

        
        assertNotNull(resultado);
        assertEquals(idEndereco, resultado.getIdEndereco());
        verify(enderecoRepository).findById(idEndereco);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar endereço inexistente")
    void deveLancarExcecaoAoBuscarEnderecoInexistente() {
        
        Long idEndereco = 999L;
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.empty());

        
        EnderecoNaoEncontradoException exception = assertThrows(
            EnderecoNaoEncontradoException.class,
            () -> profissionalService.buscarEnderecoPorId(idEndereco)
        );

        
        assertTrue(exception.getMessage().contains("Endereço não encontrado com ID: " + idEndereco));
        verify(enderecoRepository).findById(idEndereco);
    }

    @Test
    @DisplayName("Deve listar profissionais público com paginação")
    void deveListarProfissionaisPublicoComPaginacao() {
        
        Pageable pageable = PageRequest.of(0, 5);
        List<Profissional> profissionais = Arrays.asList(
            criarProfissional(1L),
            criarProfissional(2L)
        );
        Page<Profissional> pageProfissionais = new PageImpl<>(profissionais, pageable, profissionais.size());
        
        when(profissionalRepository.findAll(pageable))
            .thenReturn(pageProfissionais);

        
        List<ProfissionalDTO> resultado = profissionalService.listarPublico(pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(profissionalRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar profissionais com autorização de admin")
    void deveListarProfissionaisComAutorizacaoAdmin() {
        
        Pageable pageable = PageRequest.of(0, 5);
        List<Profissional> profissionais = Arrays.asList(
            criarProfissional(1L),
            criarProfissional(2L)
        );
        Page<Profissional> pageProfissionais = new PageImpl<>(profissionais, pageable, profissionais.size());
        
        when(profissionalRepository.findAll(pageable))
            .thenReturn(pageProfissionais);

        
        List<ProfissionalDTO> resultado = profissionalService.listarComAutorizacao(pageable);

        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(authorizationService).requireAdmin();
        verify(profissionalRepository).findAll(pageable);
    }

    
    private Profissional criarProfissional(Long id) {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(id);
        profissional.setUsuario(criarUsuario(id));
        profissional.setEndereco(criarEndereco(id));
        profissional.setNota(new BigDecimal("3.5"));
        return profissional;
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setNome("Usuario Teste " + id);
        usuario.setEmail("usuario" + id + "@teste.com");
        usuario.setRole(UserRole.ROLE_USER.getRole());
        return usuario;
    }

    private Endereco criarEndereco(Long id) {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(id);
        endereco.setCep("12345-678");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setBairro("Centro");
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        return endereco;
    }

    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("3.0"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }
} 