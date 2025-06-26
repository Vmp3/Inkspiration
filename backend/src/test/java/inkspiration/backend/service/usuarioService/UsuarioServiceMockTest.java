package inkspiration.backend.service.usuarioService;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.EnderecoService;
import inkspiration.backend.service.UsuarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceMockTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        lenient().doNothing().when(enderecoService).validarEndereco(any());
        lenient().doNothing().when(authorizationService).requireAdmin();
        lenient().doNothing().when(authorizationService).requireUserAccessOrAdmin(anyLong());
    }

    @Test
    @DisplayName("Deve verificar interações com repository ao criar usuário")
    void deveVerificarInteracoesComRepositoryAoCriarUsuario() {
        UsuarioDTO dto = criarUsuarioDTO();
        Usuario usuarioEsperado = criarUsuarioMock();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEsperado);

        usuarioService.criar(dto);

        verify(usuarioRepository).existsByEmail(dto.getEmail());
        verify(usuarioRepository).existsByCpf("11144477735");
        verify(passwordEncoder).encode(dto.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve verificar interações com authorization service")
    void deveVerificarInteracoesComAuthorizationService() {
        Long idUsuario = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> usuarios = Arrays.asList(criarUsuarioMock());
        Page<Usuario> page = new PageImpl<>(usuarios);
        
        when(usuarioRepository.findAll(pageable)).thenReturn(page);
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(criarUsuarioMock()));

        usuarioService.listarTodosComAutorizacao(pageable);
        usuarioService.buscarPorIdComAutorizacao(idUsuario);

        verify(authorizationService, times(1)).requireAdmin();
        verify(authorizationService, times(1)).requireUserAccessOrAdmin(idUsuario);
    }

    @Test
    @DisplayName("Deve verificar interações ao inativar usuário")
    void deveVerificarInteracoesAoInativarUsuario() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual("token123456789");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.inativar(idUsuario);

        verify(usuarioRepository).findById(idUsuario);
        verify(tokenRevogadoRepository).save(any());
        verify(usuarioRepository).save(any(Usuario.class));
        
        assertEquals(UserRole.ROLE_DELETED.getRole(), usuario.getRole());
        assertNull(usuario.getTokenAtual());
    }

    @Test
    @DisplayName("Deve verificar interações ao reativar usuário")
    void deveVerificarInteracoesAoReativarUsuario() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setRole(UserRole.ROLE_DELETED.getRole());
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario_IdUsuario(idUsuario)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.reativar(idUsuario);

        verify(usuarioRepository).findById(idUsuario);
        verify(profissionalRepository).existsByUsuario_IdUsuario(idUsuario);
        verify(usuarioRepository).save(any(Usuario.class));
        
        assertEquals(UserRole.ROLE_USER.getRole(), usuario.getRole());
    }

    @Test
    @DisplayName("Deve verificar interações ao deletar usuário")
    void deveVerificarInteracoesAoDeletarUsuario() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual("token123456789");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        usuarioService.deletar(idUsuario);

        verify(usuarioRepository).findById(idUsuario);
        verify(tokenRevogadoRepository).save(any());
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    @DisplayName("Deve verificar que buscarPorEmailOptional não lança exceção")
    void deveVerificarQueBuscarPorEmailOptionalNaoLancaExcecao() {
        String email = "inexistente@teste.com";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.buscarPorEmailOptional(email);

        assertNull(resultado);
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve verificar que buscarPorCpfOptional não lança exceção")
    void deveVerificarQueBuscarPorCpfOptionalNaoLancaExcecao() {
        String cpf = "99999999999";
        
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        Usuario resultado = usuarioService.buscarPorCpfOptional(cpf);

        assertNull(resultado);
        verify(usuarioRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve verificar interações ao salvar usuário")
    void deveVerificarInteracoesAoSalvarUsuario() {
        Usuario usuario = criarUsuarioMock();
        
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        usuarioService.salvar(usuario);

        verify(usuarioRepository).save(usuario);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Deve verificar comportamento ao buscar com search term vazio")
    void deveVerificarComportamentoAoBuscarComSearchTermVazio() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "";
        List<Usuario> usuarios = Arrays.asList(criarUsuarioMock());
        Page<Usuario> page = new PageImpl<>(usuarios);
        
        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        usuarioService.listarTodosResponseComPaginacao(pageable, searchTerm);

        verify(usuarioRepository).findAll(pageable);
        verify(usuarioRepository, never()).findByNomeContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve verificar comportamento ao buscar com search term válido")
    void deveVerificarComportamentoAoBuscarComSearchTermValido() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "João";
        List<Usuario> usuarios = Arrays.asList(criarUsuarioMock());
        Page<Usuario> page = new PageImpl<>(usuarios);
        
        when(usuarioRepository.findByNomeContainingIgnoreCase(searchTerm.trim(), pageable)).thenReturn(page);

        usuarioService.listarTodosResponseComPaginacao(pageable, searchTerm);

        verify(usuarioRepository).findByNomeContainingIgnoreCase(searchTerm.trim(), pageable);
        verify(usuarioRepository, never()).findAll(any(Pageable.class));
    }

    private UsuarioDTO criarUsuarioDTO() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNome("João Silva");
        dto.setEmail("joao@teste.com");
        dto.setCpf("111.444.777-35");
        dto.setDataNascimento("01/01/1990");
        dto.setSenha("MinhaSenh@123");
        dto.setTelefone("(11) 99999-9999");
        dto.setRole("ROLE_USER");
        return dto;
    }

    private Usuario criarUsuarioMock() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@teste.com");
        usuario.setCpf("11144477735");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setTelefone("11999999999");
        usuario.setRole(UserRole.ROLE_USER.getRole());
        usuario.setCreatedAt(LocalDateTime.now());
        
        UsuarioAutenticar auth = new UsuarioAutenticar();
        auth.setCpf("11144477735");
        auth.setSenha("senha_encoded");
        auth.setRole(UserRole.ROLE_USER.getRole());
        
        usuario.setUsuarioAutenticar(auth);
        
        return usuario;
    }
} 