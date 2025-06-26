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
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.entities.Endereco;
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
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceIntegracaoTest {

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
    @DisplayName("Deve executar fluxo completo de criação de usuário")
    void deveExecutarFluxoCompletoDeCriacaoUsuario() {
        UsuarioDTO dto = criarUsuarioDTO();
        Usuario usuarioEsperado = criarUsuarioMock();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEsperado);

        Usuario resultado = usuarioService.criar(dto);

        assertNotNull(resultado);
        assertEquals(usuarioEsperado.getNome(), resultado.getNome());
        assertEquals(usuarioEsperado.getEmail(), resultado.getEmail());
        
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve manter consistência entre Usuario e UsuarioAutenticar")
    void deveManterConsistenciaEntreUsuarioEUsuarioAutenticar() {
        UsuarioDTO dto = criarUsuarioDTO();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(1L);
            return usuario;
        });

        Usuario resultado = usuarioService.criar(dto);

        assertNotNull(resultado);
        assertNotNull(resultado.getUsuarioAutenticar());
        assertEquals(resultado.getCpf(), resultado.getUsuarioAutenticar().getCpf());
        assertEquals(resultado.getRole(), resultado.getUsuarioAutenticar().getRole());
    }

    @Test
    @DisplayName("Deve processar múltiplos usuários com busca e paginação")
    void deveProcessarMultiplosUsuariosComBuscaEPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "João";
        
        List<Usuario> usuarios = Arrays.asList(
            criarUsuarioMockComNome("João Silva"),
            criarUsuarioMockComNome("João Santos")
        );
        Page<Usuario> page = new PageImpl<>(usuarios, pageable, usuarios.size());
        
        when(usuarioRepository.findByNomeContainingIgnoreCase(searchTerm.trim(), pageable))
            .thenReturn(page);

        Map<String, Object> resultado = usuarioService.listarTodosResponseComPaginacao(pageable, searchTerm);

        assertNotNull(resultado);
        assertTrue(resultado.containsKey("usuarios"));
        assertTrue(resultado.containsKey("totalElements"));
        assertEquals(2L, resultado.get("totalElements"));
    }

    @Test
    @DisplayName("Deve executar ciclo completo: criar, buscar, atualizar, inativar")
    void deveExecutarCicloCompleto() {
        Long idUsuario = 1L;
        
        UsuarioDTO criacaoDTO = criarUsuarioDTO();
        Usuario usuarioCriado = criarUsuarioMock();
        
        when(usuarioRepository.existsByEmail(criacaoDTO.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioCriado);

        Usuario criado = usuarioService.criar(criacaoDTO);
        assertNotNull(criado);

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioCriado));
        Usuario buscado = usuarioService.buscarPorId(idUsuario);
        assertNotNull(buscado);
        assertEquals(criado.getIdUsuario(), buscado.getIdUsuario());

        UsuarioDTO atualizacaoDTO = criarUsuarioDTO();
        atualizacaoDTO.setNome("Nome Atualizado");
        atualizacaoDTO.setManterSenhaAtual(true);
        
        lenient().when(usuarioRepository.existsByEmail(atualizacaoDTO.getEmail())).thenReturn(false);
        lenient().when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        
        Usuario atualizado = usuarioService.atualizar(idUsuario, atualizacaoDTO);
        assertNotNull(atualizado);

        usuarioService.inativar(idUsuario);
        verify(usuarioRepository, atLeast(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve executar fluxo completo de atualização com validações")
    void deveExecutarFluxoCompletoDeAtualizacaoComValidacoes() {
        Long idUsuario = 1L;
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setEmail("novoemail@teste.com");
        dto.setSenha("NovaSenha@123");
        
        Usuario usuarioExistente = criarUsuarioMock();
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.atualizar(idUsuario, dto);

        assertNotNull(resultado);
        verify(usuarioRepository).findById(idUsuario);
        verify(usuarioRepository).existsByEmail(dto.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve validar integridade de dados entre operações sequenciais")
    void deveValidarIntegridadeDadosEntreOperacoesSequenciais() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        String emailOriginal = usuario.getEmail();
        String cpfOriginal = usuario.getCpf();
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        // Primeira busca
        Usuario primeiro = usuarioService.buscarPorId(idUsuario);
        
        // Segunda busca
        Usuario segundo = usuarioService.buscarPorId(idUsuario);

        // Verificar consistência
        assertEquals(primeiro.getIdUsuario(), segundo.getIdUsuario());
        assertEquals(emailOriginal, primeiro.getEmail());
        assertEquals(emailOriginal, segundo.getEmail());
        assertEquals(cpfOriginal, primeiro.getCpf());
        assertEquals(cpfOriginal, segundo.getCpf());
        
        verify(usuarioRepository, times(2)).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve processar diferentes métodos de busca mantendo consistência")
    void deveProcessarDiferentesMetodosBuscaMantentoConsistencia() {
        Long idUsuario = 1L;
        String email = "teste@email.com";
        String cpf = "11144477735";
        
        Usuario usuario = criarUsuarioMock();
        usuario.setEmail(email);
        usuario.setCpf(cpf);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        Usuario porId = usuarioService.buscarPorId(idUsuario);
        Usuario porEmail = usuarioService.buscarPorEmail(email);
        Usuario porCpf = usuarioService.buscarPorCpf(cpf);

        assertEquals(porId.getIdUsuario(), porEmail.getIdUsuario());
        assertEquals(porEmail.getIdUsuario(), porCpf.getIdUsuario());
        assertEquals(porId.getEmail(), email);
        assertEquals(porId.getCpf(), cpf);
        
        verify(usuarioRepository).findById(idUsuario);
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve manter estado consistente durante operações transacionais")
    void deveManterEstadoConsistenteDuranteOperacoesTransacionais() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual("token_original");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String roleOriginal = usuario.getRole();
        String tokenOriginal = usuario.getTokenAtual();

        usuarioService.inativar(idUsuario);

        assertEquals(UserRole.ROLE_DELETED.getRole(), usuario.getRole());
        assertNull(usuario.getTokenAtual());
        verify(tokenRevogadoRepository).save(any());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // Métodos auxiliares
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

    private Usuario criarUsuarioMockComNome(String nome) {
        Usuario usuario = criarUsuarioMock();
        usuario.setNome(nome);
        return usuario;
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setCep("12345-678");
        endereco.setRua("Rua Teste");
        endereco.setBairro("Bairro Teste");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setNumero("123");
        return endereco;
    }
} 