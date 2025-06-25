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

import org.springframework.security.crypto.password.PasswordEncoder;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.exception.usuario.InvalidProfileImageException;
import inkspiration.backend.exception.usuario.TokenValidationException;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.TokenRevogadoRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.security.JwtService;
import inkspiration.backend.service.EnderecoService;
import inkspiration.backend.service.UsuarioService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceExcecaoTest {

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
    @DisplayName("Deve lançar exceção ao tentar criar usuário com email já existente")
    void deveLancarExcecaoAoTentarCriarUsuarioComEmailJaExistente() {
        UsuarioDTO dto = criarUsuarioDTO();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        UsuarioException.EmailJaExisteException exception = assertThrows(
            UsuarioException.EmailJaExisteException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository).existsByEmail(dto.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário com CPF já existente")
    void deveLancarExcecaoAoTentarCriarUsuarioComCpfJaExistente() {
        UsuarioDTO dto = criarUsuarioDTO();
        
        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(anyString())).thenReturn(true);

        UsuarioException.CpfJaExisteException exception = assertThrows(
            UsuarioException.CpfJaExisteException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(usuarioRepository).existsByCpf("11144477735");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente por ID")
    void deveLancarExcecaoAoBuscarUsuarioInexistentePorId() {
        Long idUsuario = 999L;
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.empty());

        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> usuarioService.buscarPorId(idUsuario)
        );
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente por email")
    void deveLancarExcecaoAoBuscarUsuarioInexistentePorEmail() {
        String email = "inexistente@teste.com";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> usuarioService.buscarPorEmail(email)
        );
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente por CPF")
    void deveLancarExcecaoAoBuscarUsuarioInexistentePorCpf() {
        String cpf = "99999999999";
        
        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> usuarioService.buscarPorCpf(cpf)
        );
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com nome obrigatório vazio")
    void deveLancarExcecaoAoCriarUsuarioComNomeObrigatorioVazio() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setNome("");

        UsuarioValidationException.NomeObrigatorioException exception = assertThrows(
            UsuarioValidationException.NomeObrigatorioException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email obrigatório vazio")
    void deveLancarExcecaoAoCriarUsuarioComEmailObrigatorioVazio() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setEmail("");

        UsuarioValidationException.EmailObrigatorioException exception = assertThrows(
            UsuarioValidationException.EmailObrigatorioException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email inválido")
    void deveLancarExcecaoAoCriarUsuarioComEmailInvalido() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setEmail("email-invalido");

        UsuarioValidationException.EmailInvalidoException exception = assertThrows(
            UsuarioValidationException.EmailInvalidoException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertEquals("Email inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com CPF obrigatório vazio")
    void deveLancarExcecaoAoCriarUsuarioComCpfObrigatorioVazio() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setCpf("");

        UsuarioValidationException.CpfObrigatorioException exception = assertThrows(
            UsuarioValidationException.CpfObrigatorioException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com CPF inválido")
    void deveLancarExcecaoAoCriarUsuarioComCpfInvalido() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setCpf("123.456.789-00");

        UsuarioValidationException.CpfInvalidoException exception = assertThrows(
            UsuarioValidationException.CpfInvalidoException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertEquals("CPF inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com data de nascimento obrigatória vazia")
    void deveLancarExcecaoAoCriarUsuarioComDataNascimentoObrigatoriaVazia() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setDataNascimento("");

        UsuarioValidationException.DataNascimentoObrigatoriaException exception = assertThrows(
            UsuarioValidationException.DataNascimentoObrigatoriaException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com data de nascimento inválida")
    void deveLancarExcecaoAoCriarUsuarioComDataNascimentoInvalida() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setDataNascimento("data-invalida");

        UsuarioValidationException.DataInvalidaException exception = assertThrows(
            UsuarioValidationException.DataInvalidaException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertEquals("Data de nascimento inválida. Use o formato DD/MM/YYYY", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário menor de idade")
    void deveLancarExcecaoAoCriarUsuarioMenorDeIdade() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setDataNascimento("01/01/2010");

        UsuarioValidationException.IdadeMinimaException exception = assertThrows(
            UsuarioValidationException.IdadeMinimaException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com senha obrigatória vazia")
    void deveLancarExcecaoAoCriarUsuarioComSenhaObrigatoriaVazia() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setSenha("");

        UsuarioValidationException.SenhaObrigatoriaException exception = assertThrows(
            UsuarioValidationException.SenhaObrigatoriaException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com senha inválida")
    void deveLancarExcecaoAoCriarUsuarioComSenhaInvalida() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setSenha("123");

        UsuarioValidationException.SenhaInvalidaException exception = assertThrows(
            UsuarioValidationException.SenhaInvalidaException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com telefone obrigatório vazio")
    void deveLancarExcecaoAoCriarUsuarioComTelefoneObrigatorioVazio() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setTelefone("");

        UsuarioValidationException.TelefoneObrigatorioException exception = assertThrows(
            UsuarioValidationException.TelefoneObrigatorioException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com telefone inválido")
    void deveLancarExcecaoAoCriarUsuarioComTelefoneInvalido() {
        UsuarioDTO dto = criarUsuarioDTO();
        dto.setTelefone("123");

        UsuarioValidationException.TelefoneInvalidoException exception = assertThrows(
            UsuarioValidationException.TelefoneInvalidoException.class,
            () -> usuarioService.criar(dto)
        );
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reativar usuário que não está desativado")
    void deveLancarExcecaoAoTentarReativarUsuarioQueNaoEstaDesativado() {
        Long idUsuario = 1L;
        Usuario usuario = criarUsuarioMock();
        usuario.setRole(UserRole.ROLE_USER.getRole());
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> usuarioService.reativar(idUsuario)
        );
        
        assertEquals("Usuário não está desativado", exception.getMessage());
        verify(usuarioRepository).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar foto de perfil com imagem vazia")
    void deveLancarExcecaoAoAtualizarFotoPerfilComImagemVazia() {
        Long idUsuario = 1L;
        String imagemBase64 = "";

        InvalidProfileImageException exception = assertThrows(
            InvalidProfileImageException.class,
            () -> usuarioService.atualizarFotoPerfilComAutorizacao(idUsuario, imagemBase64)
        );
        
        assertEquals("Imagem não fornecida", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token com token vazio")
    void deveLancarExcecaoAoValidarTokenComTokenVazio() {
        Long idUsuario = 1L;
        String token = "";

        TokenValidationException exception = assertThrows(
            TokenValidationException.class,
            () -> usuarioService.validateTokenComplete(idUsuario, token)
        );
        
        assertEquals("Token não fornecido", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar token de usuário sem token ativo")
    void deveLancarExcecaoAoValidarTokenDeUsuarioSemTokenAtivo() {
        Long idUsuario = 1L;
        String token = "token123";
        Usuario usuario = criarUsuarioMock();
        usuario.setTokenAtual(null);
        
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario));

        TokenValidationException exception = assertThrows(
            TokenValidationException.class,
            () -> usuarioService.validateTokenComplete(idUsuario, token)
        );
        
        assertEquals("Usuário não possui token ativo", exception.getMessage());
        verify(usuarioRepository).findById(idUsuario);
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