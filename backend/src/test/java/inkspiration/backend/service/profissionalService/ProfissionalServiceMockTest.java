package inkspiration.backend.service.profissionalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
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
class ProfissionalServiceMockTest {

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
    @DisplayName("Deve chamar repositório correto ao buscar profissional por ID")
    void deveChamarRepositorioCorretoAoBuscarProfissionalPorId() {
        
        Long idProfissional = 1L;
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(criarProfissional(idProfissional)));

        
        profissionalService.buscarPorId(idProfissional);

        
        verify(profissionalRepository, times(1)).findById(idProfissional);
        verifyNoMoreInteractions(profissionalRepository);
    }

    @Test
    @DisplayName("Deve capturar parâmetros corretos ao buscar por usuário")
    void deveCapturaParametrosCorretosAoBuscarPorUsuario() {
        
        Long idUsuario = 123L;
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(criarProfissional(1L)));
        
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);

        
        profissionalService.buscarPorUsuario(idUsuario);

        
        verify(profissionalRepository).findByUsuario_IdUsuario(idCaptor.capture());
        assertEquals(idUsuario, idCaptor.getValue());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao atualizar profissional")
    void deveVerificarOrdemChamadasAoAtualizarProfissional() {
        
        Long idProfissional = 1L;
        Long idEndereco = 2L;
        Profissional profissional = criarProfissional(idProfissional);
        Endereco endereco = criarEndereco(idEndereco);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(idEndereco);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        profissionalService.atualizar(idProfissional, dto);

        
        InOrder inOrder = inOrder(profissionalRepository, enderecoRepository, enderecoService);
        inOrder.verify(profissionalRepository).findById(idProfissional);
        inOrder.verify(enderecoRepository).findById(idEndereco);
        inOrder.verify(enderecoService).validarEndereco(endereco);
        inOrder.verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve capturar entidade correta ao salvar profissional")
    void deveCapturaEntidadeCorretaAoSalvarProfissional() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setNota(new BigDecimal("4.8"));
        dto.setIdEndereco(1L);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(enderecoRepository.findById(1L))
            .thenReturn(Optional.of(criarEndereco(1L)));
        
        ArgumentCaptor<Profissional> profissionalCaptor = ArgumentCaptor.forClass(Profissional.class);

        
        profissionalService.atualizar(idProfissional, dto);

        
        verify(profissionalRepository).save(profissionalCaptor.capture());
        
        Profissional profissionalCapturado = profissionalCaptor.getValue();
        assertEquals(idProfissional, profissionalCapturado.getIdProfissional());
        assertEquals(new BigDecimal("4.8"), profissionalCapturado.getNota());
    }

    @Test
    @DisplayName("Deve verificar ordem de chamadas ao deletar profissional")
    void deveVerificarOrdemChamadasAoDeletarProfissional() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));

        
        profissionalService.deletar(idProfissional);

        
        InOrder inOrder = inOrder(profissionalRepository);
        inOrder.verify(profissionalRepository).findById(idProfissional);
        inOrder.verify(profissionalRepository).delete(profissional);
    }

    @Test
    @DisplayName("Deve capturar profissional correto ao deletar")
    void deveCapturaProfiissionalCorretoAoDeletar() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        
        ArgumentCaptor<Profissional> profissionalCaptor = ArgumentCaptor.forClass(Profissional.class);

        
        profissionalService.deletar(idProfissional);

        
        verify(profissionalRepository).delete(profissionalCaptor.capture());
        assertEquals(profissional, profissionalCaptor.getValue());
    }

    @Test
    @DisplayName("Deve configurar mock para múltiplas chamadas")
    void deveConfigurarMockParaMultiplasChamadas() {
        
        Long idProfissional = 1L;
        Profissional primeiroProfissional = criarProfissional(idProfissional);
        primeiroProfissional.setNota(new BigDecimal("3.0"));
        
        Profissional segundoProfissional = criarProfissional(idProfissional);
        segundoProfissional.setNota(new BigDecimal("4.0"));
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(primeiroProfissional))
            .thenReturn(Optional.of(segundoProfissional));

        
        Profissional primeiroResultado = profissionalService.buscarPorId(idProfissional);
        Profissional segundoResultado = profissionalService.buscarPorId(idProfissional);

        
        assertEquals(new BigDecimal("3.0"), primeiroResultado.getNota());
        assertEquals(new BigDecimal("4.0"), segundoResultado.getNota());
        verify(profissionalRepository, times(2)).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve resetar mock entre operações")
    void deveResetarMockEntreOperacoes() {
        
        Long idProfissional = 1L;
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(criarProfissional(idProfissional)));

        
        profissionalService.buscarPorId(idProfissional);
        
        
        reset(profissionalRepository);
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(criarProfissional(idProfissional)));
        
        
        profissionalService.buscarPorId(idProfissional);

        
        verify(profissionalRepository, times(1)).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve verificar que não há interações desnecessárias")
    void deveVerificarQueNaoHaInteracoesDesnecessarias() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(profissionalRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        profissionalService.listar(pageable);

        
        verify(profissionalRepository, only()).findAll(pageable);
        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(enderecoRepository);
    }

    @Test
    @DisplayName("Deve usar argumentMatchers corretamente")
    void deveUsarArgumentMatchersCorretamente() {
        
        when(profissionalRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarProfissional(1L)));
        when(usuarioRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarUsuario(1L)));
        when(profissionalRepository.existsByUsuario(any(Usuario.class)))
            .thenReturn(false);

        
        profissionalService.buscarPorId(1L);
        profissionalService.existePerfil(1L);

        
        verify(profissionalRepository).findById(anyLong());
        verify(usuarioRepository).findById(anyLong());
        verify(profissionalRepository).existsByUsuario(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve configurar stub para retornar exception")
    void deveConfigurarStubParaRetornarException() {
        
        Long idProfissional = 1L;
        when(profissionalRepository.findById(idProfissional))
            .thenThrow(new RuntimeException("Erro simulado"));

        
        assertThrows(RuntimeException.class, 
            () -> profissionalService.buscarPorId(idProfissional));
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve verificar invocação de métodos com timeout")
    void deveVerificarInvocacaoMetodosComTimeout() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(profissionalRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        profissionalService.listar(pageable);

        
        verify(profissionalRepository, timeout(1000)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve capturar múltiplos argumentos em sequência")
    void deveCapturaMultiplosArgumentosEmSequencia() {
        
        when(profissionalRepository.findById(anyLong()))
            .thenReturn(Optional.of(criarProfissional(1L)));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        
        profissionalService.buscarPorId(1L);
        profissionalService.buscarPorId(2L);
        profissionalService.buscarPorId(3L);

        
        verify(profissionalRepository, times(3)).findById(captor.capture());
        
        List<Long> idsCapturados = captor.getAllValues();
        assertEquals(Arrays.asList(1L, 2L, 3L), idsCapturados);
    }

    @Test
    @DisplayName("Deve verificar autorização antes de listar profissionais")
    void deveVerificarAutorizacaoAntesDeListarProfissionais() {
        
        Pageable pageable = PageRequest.of(0, 10);
        when(profissionalRepository.findAll(pageable))
            .thenReturn(new PageImpl<>(Arrays.asList()));

        
        profissionalService.listarComAutorizacao(pageable);

        
        InOrder inOrder = inOrder(authorizationService, profissionalRepository);
        inOrder.verify(authorizationService).requireAdmin();
        inOrder.verify(profissionalRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve verificar validação de endereço ao atualizar")
    void deveVerificarValidacaoEnderecoAoAtualizar() {
        
        Long idProfissional = 1L;
        Long idEndereco = 2L;
        Profissional profissional = criarProfissional(idProfissional);
        Endereco endereco = criarEndereco(idEndereco);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(idEndereco);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        profissionalService.atualizar(idProfissional, dto);

        
        verify(enderecoService).validarEndereco(endereco);
        verify(enderecoRepository).findById(idEndereco);
    }

    @Test
    @DisplayName("Deve verificar existência de usuário ao verificar perfil")
    void deveVerificarExistenciaUsuarioAoVerificarPerfil() {
        
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(true);

        
        profissionalService.existePerfil(idUsuario);

        
        InOrder inOrder = inOrder(usuarioRepository, profissionalRepository);
        inOrder.verify(usuarioRepository).findById(idUsuario);
        inOrder.verify(profissionalRepository).existsByUsuario(usuario);
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