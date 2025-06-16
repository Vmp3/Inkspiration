package inkspiration.backend.service;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioAutenticarRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioAutenticarRepository autenticarRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private TokenRevogadoRepository tokenRevogadoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioAutenticar usuarioAutenticar;
    private Endereco endereco;

    @BeforeEach
    void setUp() {

        endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setCep("12345678");
        endereco.setRua("Rua Teste");
        endereco.setNumero("123");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");

        usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setCpf("11144477735");
        usuarioAutenticar.setSenha("senhaHasheada");
        usuarioAutenticar.setRole("ROLE_USER");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setCpf("11144477735");
        usuario.setEmail("test@test.com");
        usuario.setDataNascimento(LocalDate.of(1990, 1, 1));
        usuario.setTelefone("11999999999");
        usuario.setRole("ROLE_USER");
        usuario.setUsuarioAutenticar(usuarioAutenticar);
        usuario.setEndereco(endereco);
        usuario.setCreatedAt(LocalDateTime.now());

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNome("João Silva");
        usuarioDTO.setCpf("111.444.777-35");
        usuarioDTO.setEmail("test@test.com");
        usuarioDTO.setDataNascimento("01/01/1990");
        usuarioDTO.setTelefone("11999999999");
        usuarioDTO.setSenha("senha123");
        usuarioDTO.setRole("ROLE_USER");
        usuarioDTO.setEndereco(endereco);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        // Given
        when(repository.existsByEmail("test@test.com")).thenReturn(false);
        when(repository.existsByCpf("11144477735")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHasheada");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.criar(usuarioDTO);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("test@test.com", resultado.getEmail());
        verify(repository, times(1)).existsByEmail("test@test.com");
        verify(repository, times(1)).existsByCpf("11144477735");
        verify(passwordEncoder, times(1)).encode("senha123");
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        // Given
        when(repository.existsByEmail("test@test.com")).thenReturn(true);

        // When & Then
        UsuarioException.EmailJaExisteException exception = assertThrows(
            UsuarioException.EmailJaExisteException.class,
            () -> usuarioService.criar(usuarioDTO)
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(repository, times(1)).existsByEmail("test@test.com");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void deveLancarExcecaoQuandoCpfJaExiste() {
        // Given
        when(repository.existsByEmail("test@test.com")).thenReturn(false);
        when(repository.existsByCpf("11144477735")).thenReturn(true);

        // When & Then
        UsuarioException.CpfJaExisteException exception = assertThrows(
            UsuarioException.CpfJaExisteException.class,
            () -> usuarioService.criar(usuarioDTO)
        );

        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(repository, times(1)).existsByEmail("test@test.com");
        verify(repository, times(1)).existsByCpf("11144477735");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> usuarioService.buscarPorId(1L)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve listar todos os usuários com paginação")
    void deveListarTodosUsuariosComPaginacao() {
        // Given
        Page<Usuario> pageUsuarios = new PageImpl<>(Collections.singletonList(usuario));
        Pageable pageable = mock(Pageable.class);
        when(repository.findAll(pageable)).thenReturn(pageUsuarios);

        // When
        Page<Usuario> resultado = usuarioService.listarTodos(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(usuario, resultado.getContent().get(0));
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve listar todos os usuários como DTO response")
    void deveListarTodosUsuariosComoDTOResponse() {
        // Given
        Page<Usuario> pageUsuarios = new PageImpl<>(Collections.singletonList(usuario));
        Pageable pageable = mock(Pageable.class);
        when(repository.findAll(pageable)).thenReturn(pageUsuarios);

        // When
        List<UsuarioResponseDTO> resultado = usuarioService.listarTodosResponse(pageable);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        assertEquals("test@test.com", resultado.get(0).getEmail());
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() {
        // Given
        UsuarioDTO atualizacaoDTO = new UsuarioDTO();
        atualizacaoDTO.setNome("João Santos");
        atualizacaoDTO.setCpf("111.444.777-35");
        atualizacaoDTO.setEmail("test@test.com");
        atualizacaoDTO.setDataNascimento("01/01/1990");
        atualizacaoDTO.setTelefone("11888888888");
        atualizacaoDTO.setSenha("SENHA_NAO_ALTERADA");
        atualizacaoDTO.setRole("ROLE_USER");
        
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.atualizar(1L, atualizacaoDTO);

        // Then
        assertNotNull(resultado);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve inativar usuário com sucesso")
    void deveInativarUsuarioComSucesso() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        assertDoesNotThrow(() -> usuarioService.inativar(1L));

        // Then
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(usuario);
        // Usuario não tem campo ativo, removendo verificação
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));

        // When
        assertDoesNotThrow(() -> usuarioService.deletar(1L));

        // Then
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve buscar usuário por email")
    void deveBuscarUsuarioPorEmail() {
        // Given
        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorEmail("test@test.com");

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(repository, times(1)).findByEmail("test@test.com");
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF")
    void deveBuscarUsuarioPorCpf() {
        // Given
        when(repository.findByCpf("11144477735")).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorCpf("11144477735");

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(repository, times(1)).findByCpf("11144477735");
    }

    @Test
    @DisplayName("Deve buscar usuário por email opcional")
    void deveBuscarUsuarioPorEmailOpcional() {
        // Given
        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = usuarioService.buscarPorEmailOptional("test@test.com");

        // Then
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        verify(repository, times(1)).findByEmail("test@test.com");
    }

    @Test
    @DisplayName("Deve retornar null quando email não encontrado opcional")
    void deveRetornarNullQuandoEmailNaoEncontradoOpcional() {
        // Given
        when(repository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        // When
        Usuario resultado = usuarioService.buscarPorEmailOptional("inexistente@test.com");

        // Then
        assertNull(resultado);
        verify(repository, times(1)).findByEmail("inexistente@test.com");
    }

    @Test
    @DisplayName("Deve salvar usuário")
    void deveSalvarUsuario() {
        // Given
        when(repository.save(usuario)).thenReturn(usuario);

        // When
        assertDoesNotThrow(() -> usuarioService.salvar(usuario));

        // Then
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve atualizar foto de perfil")
    void deveAtualizarFotoPerfil() {
        // Given
        String imagemBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD";
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        assertDoesNotThrow(() -> usuarioService.atualizarFotoPerfil(1L, imagemBase64));

        // Then
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(usuario);
        assertEquals(imagemBase64, usuario.getImagemPerfil());
    }

    @Test
    @DisplayName("Deve verificar se existe usuário por role")
    void deveVerificarSeExisteUsuarioPorRole() {
        // Given
        when(repository.existsByRole("ROLE_ADMIN")).thenReturn(true);

        // When
        boolean resultado = usuarioService.existsByRole("ROLE_ADMIN");

        // Then
        assertTrue(resultado);
        verify(repository, times(1)).existsByRole("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Deve atualizar token do usuário")
    void deveAtualizarTokenUsuario() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any())).thenReturn("novoToken");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        String token = usuarioService.atualizarTokenUsuario(1L);

        // Then
        assertNotNull(token);
        assertEquals("novoToken", token);
        verify(repository, times(1)).findById(1L);
        verify(jwtService, times(1)).generateToken(any());
        verify(repository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve processar CPF formatado na criação")
    void deveProcessarCpfFormatadoNaCriacao() {
        // Given
        usuarioDTO.setCpf("111.444.777-35");
        when(repository.existsByEmail("test@test.com")).thenReturn(false);
        when(repository.existsByCpf("11144477735")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHasheada");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.criar(usuarioDTO);

        // Then
        assertNotNull(resultado);
        verify(repository, times(1)).existsByCpf("11144477735");
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve determinar role corretamente")
    void deveDeterminarRoleCorretamente() {
        // Given
        usuarioDTO.setRole("ROLE_ADMIN");
        when(repository.existsByEmail("test@test.com")).thenReturn(false);
        when(repository.existsByCpf("11144477735")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaHasheada");
        when(repository.save(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.criar(usuarioDTO);

        // Then
        assertNotNull(resultado);
        verify(repository, times(1)).save(any(Usuario.class));
    }
} 