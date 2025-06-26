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
class UsuarioServiceCoreTest {

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
    }

    @Test
    @DisplayName("Deve criar usuário com dados válidos")
    void deveCriarUsuarioComDadosValidos() {
        // Arrange
        UsuarioDTO dto = criarUsuarioDTO();
        Usuario usuarioEsperado = criarUsuarioMock();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(dto.getSenha())).thenReturn("senha_encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEsperado);

        // Act
        Usuario resultado = usuarioService.criar(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioEsperado.getNome(), resultado.getNome());
        assertEquals(usuarioEsperado.getEmail(), resultado.getEmail());
        assertEquals(usuarioEsperado.getCpf(), resultado.getCpf());
        
        verify(usuarioRepository).existsByEmail(dto.getEmail());
        verify(usuarioRepository).existsByCpf("11144477735");
        verify(passwordEncoder).encode(dto.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void deveBuscarUsuarioPorId() {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuarioMock = criarUsuarioMock();
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioMock));

        // Act
        Usuario resultado = usuarioService.buscarPorId(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioMock.getIdUsuario(), resultado.getIdUsuario());
        assertEquals(usuarioMock.getNome(), resultado.getNome());
        
        verify(usuarioRepository).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve listar todos os usuários com resposta")
    void deveListarTodosUsuariosComResposta() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> usuarios = Arrays.asList(criarUsuarioMock(), criarUsuarioMock());
        Page<Usuario> page = new PageImpl<>(usuarios);
        
        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        // Act
        List<UsuarioResponseDTO> resultado = usuarioService.listarTodosResponse(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar usuários com paginação e busca")
    void deveListarUsuariosComPaginacaoEBusca() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "João";
        List<Usuario> usuarios = Arrays.asList(criarUsuarioMock());
        Page<Usuario> page = new PageImpl<>(usuarios);
        
        when(usuarioRepository.findByNomeContainingIgnoreCase(searchTerm, pageable)).thenReturn(page);

        // Act
        Map<String, Object> resultado = usuarioService.listarTodosResponseComPaginacao(pageable, searchTerm);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("usuarios"));
        assertTrue(resultado.containsKey("totalElements"));
        assertTrue(resultado.containsKey("totalPages"));
        verify(usuarioRepository).findByNomeContainingIgnoreCase(searchTerm, pageable);
    }

    @Test
    @DisplayName("Deve atualizar usuário com dados válidos")
    void deveAtualizarUsuarioComDadosValidos() {
        // Arrange
        Long idUsuario = 1L;
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setEmail("novoemail@teste.com");
        
        Usuario usuarioExistente = criarUsuarioMock();
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // Act
        Usuario resultado = usuarioService.atualizar(idUsuario, dto);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository).findById(idUsuario);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve inativar usuário")
    void deveInativarUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual("token123456789");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.inativar(idUsuario);

        // Assert
        verify(usuarioRepository).findById(idUsuario);
        verify(tokenRevogadoRepository).save(any());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve reativar usuário")
    void deveReativarUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setRole(UserRole.ROLE_DELETED.getRole());
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario_IdUsuario(idUsuario)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.reativar(idUsuario);

        // Assert
        verify(usuarioRepository).findById(idUsuario);
        verify(profissionalRepository).existsByUsuario_IdUsuario(idUsuario);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve deletar usuário")
    void deveDeletarUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual("token123456789");
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.deletar(idUsuario);

        // Assert
        verify(usuarioRepository).findById(idUsuario);
        verify(tokenRevogadoRepository).save(any());
        verify(usuarioRepository).delete(usuario);
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarUsuarioPorEmail() {
        // Arrange
        String email = "teste@email.com";
        Usuario usuario = criarUsuarioMock();
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.buscarPorEmail(email);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuario.getEmail(), resultado.getEmail());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF")
    void deveBuscarUsuarioPorCpf() {
        // Arrange
        String cpf = "11144477735";
        Usuario usuario = criarUsuarioMock();
        
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        // Act
        Usuario resultado = usuarioService.buscarPorCpf(cpf);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuario.getCpf(), resultado.getCpf());
        verify(usuarioRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve atualizar foto de perfil")
    void deveAtualizarFotoPerfil() {
        // Arrange
        Long idUsuario = 1L;
        String imagemBase64 = "base64image";
        Usuario usuario = criarUsuarioMock();
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.atualizarFotoPerfil(idUsuario, imagemBase64);

        // Assert
        verify(usuarioRepository).findById(idUsuario);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve validar token completo")
    void deveValidarTokenCompleto() {
        // Arrange
        Long idUsuario = 1L;
        String token = "token123456789";
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual(token);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        // Act
        boolean resultado = usuarioService.validateTokenComplete(idUsuario, token);

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).findById(idUsuario);
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
} 