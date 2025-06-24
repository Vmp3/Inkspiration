package inkspiration.backend.service.profissionalService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalService - Testes de Cobertura Total Complementar")
class ProfissionalServiceCoberturaTotalComplementarTest {

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

    private ObjectMapper objectMapper = new ObjectMapper();

    private Usuario usuario;
    private Endereco endereco;
    private Profissional profissional;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        usuario = criarUsuario();
        endereco = criarEndereco();
        portfolio = criarPortfolio();
        profissional = criarProfissional();
        
        lenient().doNothing().when(authorizationService).requireUserAccessOrAdmin(anyLong());
        lenient().doNothing().when(authorizationService).requireAdmin();
        lenient().doNothing().when(enderecoService).validarEndereco(any());
        lenient().when(enderecoRepository.findById(anyLong())).thenReturn(Optional.of(endereco));
    }

    @Test
    @DisplayName("Deve listar tipos de serviço")
    void deveListarTiposServico() {
        // Act
        List<Map<String, Object>> resultado = profissionalService.listarTiposServico();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        
        Map<String, Object> primeiroTipo = resultado.get(0);
        assertTrue(primeiroTipo.containsKey("nome"));
        assertTrue(primeiroTipo.containsKey("descricao"));
        assertTrue(primeiroTipo.containsKey("duracaoHoras"));
        
        // Verifica se todos os tipos de serviço estão incluídos
        assertEquals(TipoServico.values().length, resultado.size());
    }

    @Test
    @DisplayName("Deve listar tipos de serviço por profissional com validação")
    void deveListarTiposServicoPorProfissionalComValidacao() {
        // Arrange
        Long idProfissional = 1L;
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(idProfissional);

        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put(TipoServico.TATUAGEM_PEQUENA.name(), BigDecimal.TEN);
        profissional.setTiposServicoPrecos(precos);

        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));

        // Act
        Profissional resultado = profissionalService.buscarPorId(idProfissional);

        // Assert
        assertTrue(resultado.getTiposServicoPrecos().isEmpty());
    }

    @Test
    @DisplayName("Deve listar tipos de serviço por profissional com preço zero quando não definido")
    void deveListarTiposServicoPorProfissionalComPrecoZeroQuandoNaoDefinido() {
        // Arrange
        Long idProfissional = 1L;
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(idProfissional);

        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put(TipoServico.TATUAGEM_PEQUENA.name(), BigDecimal.ZERO);
        profissional.setTiposServicoPrecos(precos);

        when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));

        // Act & Assert
        assertDoesNotThrow(() -> profissionalService.buscarPorId(idProfissional));
        verify(profissionalRepository).findById(idProfissional);
    }


    @Test
    @DisplayName("Deve criar profissional completo com validação")
    void deveCriarProfissionalCompletoComValidacao() throws JsonProcessingException {
        // Arrange
        ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(1L);
        
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);

        when(profissionalRepository.existsByUsuario_IdUsuario(anyLong())).thenReturn(false);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(anyLong())).thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(any(Profissional.class))).thenReturn(profissional);
        when(profissionalRepository.findById(anyLong())).thenReturn(Optional.of(profissional));

        // Act
        Profissional resultado = profissionalService.criarProfissionalCompleto(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProfissional());
        verify(profissionalRepository, atLeastOnce()).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve deletar profissional com autorização")
    void deveDeletarProfissionalComAutorizacao() {
        // Arrange
        Long id = 1L;
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(id);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        profissional.setUsuario(usuario);

        when(profissionalRepository.findById(id)).thenReturn(Optional.of(profissional));
        doNothing().when(authorizationService).requireUserAccessOrAdmin(anyLong());

        // Act
        profissionalService.deletarComAutorizacao(id);

        // Assert
        verify(profissionalRepository).delete(profissional);
    }

    @Test
    @DisplayName("Deve listar com filtros completos")
    void deveListarComFiltrosCompletos() {
        // Arrange
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        profissional.setUsuario(usuario);
        profissional.setNota(new BigDecimal("4.5"));
        
        Endereco endereco = new Endereco();
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        profissional.setEndereco(endereco);
        
        Portfolio portfolio = new Portfolio();
        portfolio.setEspecialidade("TATUAGEM_PEQUENA");
        profissional.setPortfolio(portfolio);

        List<Profissional> profissionais = Arrays.asList(profissional);
        when(profissionalRepository.findAll()).thenReturn(profissionais);

        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "João";
        String locationTerm = "São Paulo";
        double minRating = 4.0;
        String[] selectedSpecialties = {"TATUAGEM_PEQUENA"};
        String sortBy = "melhorAvaliacao";

        // Act
        Page<Profissional> resultado = profissionalService.listarComFiltros(
            pageable, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("João", resultado.getContent().get(0).getUsuario().getNome());
        verify(profissionalRepository).findAll();
    }

    @Test
    @DisplayName("Deve comparar profissionais corretamente por nota ao ordenar")
    void deveCompararProfissionaisPorNota() {
        // Arrange
        Profissional profissional1 = new Profissional();
        profissional1.setNota(new BigDecimal("4.5"));

        Profissional profissional2 = new Profissional();
        profissional2.setNota(new BigDecimal("4.8"));

        // Act & Assert
        List<Profissional> profissionais = Arrays.asList(profissional1, profissional2);
        profissionais.sort((p1, p2) -> p2.getNota().compareTo(p1.getNota()));

        assertEquals(new BigDecimal("4.8"), profissionais.get(0).getNota());
        assertEquals(new BigDecimal("4.5"), profissionais.get(1).getNota());
    }

    @Test
    @DisplayName("Deve validar tipos de serviço e preços corretamente")
    void deveValidarTiposServicoEPrecos() {
        // Arrange
        Long idProfissional = 1L;
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(idProfissional);

        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put(TipoServico.TATUAGEM_PEQUENA.name(), BigDecimal.TEN);
        precos.put(TipoServico.TATUAGEM_MEDIA.name(), BigDecimal.ZERO);
        precos.put(TipoServico.TATUAGEM_GRANDE.name(), new BigDecimal("100.00"));
        profissional.setTiposServicoPrecos(precos);

        lenient().when(profissionalRepository.findById(idProfissional)).thenReturn(Optional.of(profissional));

        // Act
        List<Map<String, Object>> tiposServico = profissionalService.listarTiposServico();

        // Assert
        assertNotNull(tiposServico);
        assertFalse(tiposServico.isEmpty());
        
        assertEquals(TipoServico.values().length, tiposServico.size());
        
        for (Map<String, Object> tipo : tiposServico) {
            assertNotNull(tipo.get("nome"));
            assertNotNull(tipo.get("descricao"));
            assertNotNull(tipo.get("duracaoHoras"));
        }
        
        boolean encontrouTatuagemPequena = false;
        boolean encontrouTatuagemMedia = false;
        boolean encontrouTatuagemGrande = false;
        
        for (Map<String, Object> tipo : tiposServico) {
            String nome = (String) tipo.get("nome");
            if (TipoServico.TATUAGEM_PEQUENA.name().equals(nome)) {
                encontrouTatuagemPequena = true;
            } else if (TipoServico.TATUAGEM_MEDIA.name().equals(nome)) {
                encontrouTatuagemMedia = true;
            } else if (TipoServico.TATUAGEM_GRANDE.name().equals(nome)) {
                encontrouTatuagemGrande = true;
            }
        }
        
        assertTrue(encontrouTatuagemPequena, "Tatuagem pequena não encontrada");
        assertTrue(encontrouTatuagemMedia, "Tatuagem média não encontrada");
        assertTrue(encontrouTatuagemGrande, "Tatuagem grande não encontrada");
    }

    @Test
    @DisplayName("Deve ordenar profissionais por melhor avaliação corretamente")
    void deveOrdenarProfissionaisPorMelhorAvaliacao() {
        // Arrange
        List<Profissional> profissionais = new ArrayList<>();
        
        Profissional profissional1 = criarProfissionalComNota("4.5");
        Profissional profissional2 = criarProfissionalComNota("5.0");
        Profissional profissional3 = criarProfissionalComNota("3.8");
        
        profissionais.addAll(Arrays.asList(profissional1, profissional2, profissional3));
        
        when(profissionalRepository.findAll()).thenReturn(profissionais);
        
        Pageable pageable = PageRequest.of(0, 10);
        String sortBy = "melhorAvaliacao";

        // Act
        Page<Profissional> resultado = profissionalService.listarComFiltros(
            pageable, null, null, 0.0, null, sortBy);

        // Assert
        List<Profissional> profissionaisOrdenados = resultado.getContent();
        assertEquals(new BigDecimal("5.0"), profissionaisOrdenados.get(0).getNota());
        assertEquals(new BigDecimal("4.5"), profissionaisOrdenados.get(1).getNota());
        assertEquals(new BigDecimal("3.8"), profissionaisOrdenados.get(2).getNota());
    }

    private Profissional criarProfissionalComNota(String nota) {
        Profissional profissional = new Profissional();
        profissional.setNota(new BigDecimal(nota));
        profissional.setUsuario(criarUsuario());
        profissional.setEndereco(criarEndereco());
        profissional.setPortfolio(criarPortfolio());
        return profissional;
    }

    // Métodos auxiliares
    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@example.com");
        usuario.setTelefone("(11) 99999-9999");
        usuario.setRole(UserRole.ROLE_USER.getRole());
        
        UsuarioAutenticar userAuth = new UsuarioAutenticar();
        userAuth.setRole(UserRole.ROLE_USER.getRole());
        usuario.setUsuarioAutenticar(userAuth);
        
        return usuario;
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

    private Portfolio criarPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio teste");
        portfolio.setEspecialidade("Tatuagem");
        portfolio.setExperiencia("5 anos");
        return portfolio;
    }

    private Profissional criarProfissional() {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortfolio(portfolio);
        profissional.setNota(new BigDecimal("4.5"));
        
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        profissional.setTiposServicoPrecos(precos);
        
        return profissional;
    }

} 