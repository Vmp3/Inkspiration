package inkspiration.backend.service.profissionalService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.profissional.EnderecoNaoEncontradoException;
import inkspiration.backend.exception.profissional.ProfissionalJaExisteException;
import inkspiration.backend.repository.EnderecoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.service.EnderecoService;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.service.PortfolioService;
import inkspiration.backend.service.ProfissionalService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalService - Testes de Métodos Privados e Edge Cases")
class ProfissionalServiceMetodosPrivadosTest {

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

    private Usuario usuario;
    private Endereco endereco;
    private Profissional profissional;
    private UsuarioAutenticar usuarioAutenticar;

    @BeforeEach
    void setUp() {
        // Criar UsuarioAutenticar
        usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setIdUsuarioAutenticar(1L);
        usuarioAutenticar.setCpf("12345678901");
        usuarioAutenticar.setSenha("senha123");
        usuarioAutenticar.setRole(UserRole.ROLE_USER.getRole());

        // Criar Usuario
        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setTelefone("(11) 99999-9999");
        usuario.setRole(UserRole.ROLE_USER.getRole());
        usuario.setUsuarioAutenticar(usuarioAutenticar);

        endereco = criarEndereco();
        profissional = criarProfissional();
    }

    @Test
    @DisplayName("Deve testar método carregarTiposServicoPrecos com JSON válido")
    void deveTestarCarregarTiposServicoPrecos() throws Exception {
        // Arrange
        profissional.setTiposServicoStr("{\"TATUAGEM_PEQUENA\":100.50,\"TATUAGEM_MEDIA\":200.00}");
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("carregarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertNotNull(profissional.getTiposServicoPrecos());
        assertEquals(2, profissional.getTiposServicoPrecos().size());
        assertEquals(new BigDecimal("100.50"), profissional.getTiposServicoPrecos().get("TATUAGEM_PEQUENA"));
        assertEquals(new BigDecimal("200.00"), profissional.getTiposServicoPrecos().get("TATUAGEM_MEDIA"));
    }

    @Test
    @DisplayName("Deve testar método carregarTiposServicoPrecos com JSON inválido")
    void deveTestarCarregarTiposServicoComJSONInvalido() throws Exception {
        // Arrange
        profissional.setTiposServicoStr("{ json inválido }");
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("carregarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertNotNull(profissional.getTiposServicoPrecos());
        assertTrue(profissional.getTiposServicoPrecos().isEmpty());
    }

    @Test
    @DisplayName("Deve testar método carregarTiposServicoPrecos com string nula")
    void deveTestarCarregarTiposServicoComStringNula() throws Exception {
        // Arrange
        profissional.setTiposServicoStr(null);
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("carregarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertNotNull(profissional.getTiposServicoPrecos());
        assertTrue(profissional.getTiposServicoPrecos().isEmpty());
    }

    @Test
    @DisplayName("Deve testar método carregarTiposServicoPrecos com string vazia")
    void deveTestarCarregarTiposServicoComStringVazia() throws Exception {
        // Arrange
        profissional.setTiposServicoStr("");
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("carregarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertNotNull(profissional.getTiposServicoPrecos());
        assertTrue(profissional.getTiposServicoPrecos().isEmpty());
    }

    @Test
    @DisplayName("Deve testar método salvarTiposServicoPrecos com preços válidos")
    void deveTestarSalvarTiposServicoPrecos() throws Exception {
        // Arrange
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.50"));
        precos.put("TATUAGEM_MEDIA", new BigDecimal("200.00"));
        profissional.setTiposServicoPrecos(precos);
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("salvarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertNotNull(profissional.getTiposServicoStr());
        assertFalse(profissional.getTiposServicoStr().isEmpty());
        assertTrue(profissional.getTiposServicoStr().contains("TATUAGEM_PEQUENA"));
        assertTrue(profissional.getTiposServicoStr().contains("100.50"));
    }

    @Test
    @DisplayName("Deve testar método salvarTiposServicoPrecos com preços nulos")
    void deveTestarSalvarTiposServicoComPrecosNulos() throws Exception {
        // Arrange
        profissional.setTiposServicoPrecos(null);
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("salvarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertEquals("", profissional.getTiposServicoStr());
    }

    @Test
    @DisplayName("Deve testar método salvarTiposServicoPrecos com preços vazios")
    void deveTestarSalvarTiposServicoComPrecosVazios() throws Exception {
        // Arrange
        profissional.setTiposServicoPrecos(new HashMap<>());
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("salvarTiposServicoPrecos", Profissional.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional);
        
        // Assert
        assertEquals("", profissional.getTiposServicoStr());
    }

    @Test
    @DisplayName("Deve testar método processarTiposServicoComPrecos com preços fornecidos")
    void deveTestarProcessarTiposServicoComPrecos() throws Exception {
        // Arrange
        List<TipoServico> tiposServico = Arrays.asList(TipoServico.TATUAGEM_PEQUENA, TipoServico.TATUAGEM_MEDIA);
        Map<String, BigDecimal> precosServicos = new HashMap<>();
        precosServicos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        precosServicos.put("TATUAGEM_MEDIA", new BigDecimal("200.00"));
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("processarTiposServicoComPrecos", 
            Profissional.class, List.class, Map.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional, tiposServico, precosServicos);
        
        // Assert
        assertEquals(tiposServico, profissional.getTiposServico());
        assertNotNull(profissional.getTiposServicoPrecos());
        assertEquals(new BigDecimal("100.00"), profissional.getTiposServicoPrecos().get("TATUAGEM_PEQUENA"));
        assertEquals(new BigDecimal("200.00"), profissional.getTiposServicoPrecos().get("TATUAGEM_MEDIA"));
    }

    @Test
    @DisplayName("Deve testar método processarTiposServicoComPrecos sem preços fornecidos")
    void deveTestarProcessarTiposServicoSemPrecos() throws Exception {
        // Arrange
        List<TipoServico> tiposServico = Arrays.asList(TipoServico.TATUAGEM_PEQUENA, TipoServico.TATUAGEM_MEDIA);
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("processarTiposServicoComPrecos", 
            Profissional.class, List.class, Map.class);
        method.setAccessible(true);
        method.invoke(profissionalService, profissional, tiposServico, null);
        
        // Assert
        assertEquals(tiposServico, profissional.getTiposServico());
        assertNotNull(profissional.getTiposServicoPrecos());
        assertEquals(BigDecimal.ZERO, profissional.getTiposServicoPrecos().get("TATUAGEM_PEQUENA"));
        assertEquals(BigDecimal.ZERO, profissional.getTiposServicoPrecos().get("TATUAGEM_MEDIA"));
    }

    @Test
    @DisplayName("Deve testar método criar com profissional já existente")
    void deveTestarCriarComProfissionalJaExistente() throws Exception {
        // Arrange
        ProfissionalDTO dto = criarProfissionalDTO();
        
        when(usuarioRepository.findById(dto.getIdUsuario()))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(true);
        
        // Act & Assert - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("criar", ProfissionalDTO.class);
        method.setAccessible(true);
        
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            try {
                method.invoke(profissionalService, dto);
            } catch (Exception e) {
                if (e.getCause() instanceof IllegalStateException) {
                    throw (IllegalStateException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });
        
        assertEquals("Já existe um perfil profissional para este usuário", exception.getMessage());
        verify(usuarioRepository).findById(dto.getIdUsuario());
        verify(profissionalRepository).existsByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve testar método criar com endereço não encontrado")
    void deveTestarCriarComEnderecoNaoEncontrado() throws Exception {
        // Arrange
        ProfissionalDTO dto = criarProfissionalDTO();
        
        when(usuarioRepository.findById(dto.getIdUsuario()))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(false);
        when(enderecoRepository.findById(dto.getIdEndereco()))
            .thenReturn(Optional.empty());
        
        // Act & Assert - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("criar", ProfissionalDTO.class);
        method.setAccessible(true);
        
        EnderecoNaoEncontradoException exception = assertThrows(EnderecoNaoEncontradoException.class, () -> {
            try {
                method.invoke(profissionalService, dto);
            } catch (Exception e) {
                if (e.getCause() instanceof EnderecoNaoEncontradoException) {
                    throw (EnderecoNaoEncontradoException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });
        
        assertTrue(exception.getMessage().contains("Endereço não encontrado com ID:"));
        verify(usuarioRepository).findById(dto.getIdUsuario());
        verify(profissionalRepository).existsByUsuario(usuario);
        verify(enderecoRepository).findById(dto.getIdEndereco());
    }

    @Test
    @DisplayName("Deve testar método criar com sucesso")
    void deveTestarCriarComSucesso() throws Exception {
        // Arrange
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setNota(null); // Testa o caso onde nota é null
        
        when(usuarioRepository.findById(dto.getIdUsuario()))
            .thenReturn(Optional.of(usuario));
        when(profissionalRepository.existsByUsuario(usuario))
            .thenReturn(false);
        when(enderecoRepository.findById(dto.getIdEndereco()))
            .thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(any(Profissional.class)))
            .thenReturn(profissional);
        when(usuarioRepository.save(any(Usuario.class)))
            .thenReturn(usuario);
        doNothing().when(enderecoService).validarEndereco(any());
        
        // Act - usando reflexão para acessar método privado
        Method method = ProfissionalService.class.getDeclaredMethod("criar", ProfissionalDTO.class);
        method.setAccessible(true);
        Profissional resultado = (Profissional) method.invoke(profissionalService, dto);
        
        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
        verify(profissionalRepository).save(any(Profissional.class));
        assertEquals(UserRole.ROLE_PROF.getRole(), usuario.getRole());
        assertEquals(UserRole.ROLE_PROF.getRole(), usuario.getUsuarioAutenticar().getRole());
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setCep("01234567");
        endereco.setRua("Rua Teste");
        endereco.setBairro("Bairro Teste");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setNumero("123");
        return endereco;
    }

    private Profissional criarProfissional() {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setNota(new BigDecimal("4.5"));
        
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        profissional.setTiposServicoPrecos(precos);
        
        return profissional;
    }

    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdProfissional(1L);
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("4.5"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }
} 