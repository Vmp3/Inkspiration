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

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceIntegracaoTest {

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
    @DisplayName("Deve executar fluxo completo de atualização de profissional")
    void deveExecutarFluxoCompletoDeAtualizacaoProfissional() throws Exception {
        
        Long idUsuario = 1L;
        Long idEndereco = 2L;
        
        ProfissionalCriacaoDTO dto = criarProfissionalCriacaoDTO();
        dto.setIdUsuario(idUsuario);
        dto.setIdEndereco(idEndereco);
        dto.setDescricao("Nova descrição com pelo menos 20 caracteres");
        
        Profissional profissionalExistente = criarProfissional(1L);
        profissionalExistente.setPortfolio(criarPortfolio(1L));
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.of(criarEndereco(idEndereco)));
        when(profissionalRepository.save(profissionalExistente))
            .thenReturn(profissionalExistente);
        when(profissionalRepository.findById(1L))
            .thenReturn(Optional.of(profissionalExistente));
        when(portfolioService.atualizar(any(), any()))
            .thenReturn(criarPortfolio(1L));
        doNothing().when(profissionalRepository).flush();

        
        Profissional resultado = profissionalService.atualizarProfissionalCompleto(dto);

        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProfissional());
        
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        verify(profissionalRepository, times(1)).save(profissionalExistente);
        verify(portfolioService).atualizar(any(), any());
    }

    @Test
    @DisplayName("Deve validar integridade referencial entre profissional e usuário")
    void deveValidarIntegridadeReferencialEntreProfissionalEUsuario() {
        
        Long idUsuario = 1L;
        Long idProfissional = 1L;
        
        Usuario usuario = criarUsuario(idUsuario);
        Profissional profissional = criarProfissional(idProfissional);
        profissional.setUsuario(usuario);
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));

        
        Profissional resultado = profissionalService.buscarPorUsuario(idUsuario);

        
        assertNotNull(resultado);
        assertEquals(idProfissional, resultado.getIdProfissional());
        assertEquals(idUsuario, resultado.getUsuario().getIdUsuario());
        
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve processar múltiplos profissionais com filtros")
    void deveProcessarMultiplosProfissionaisComFiltros() {
        
        List<Profissional> profissionais = Arrays.asList(
            criarProfissional(1L),
            criarProfissional(2L),
            criarProfissional(3L)
        );
        
        profissionais.get(0).getUsuario().setNome("João Silva");
        profissionais.get(1).getUsuario().setNome("Maria Santos");
        profissionais.get(2).getUsuario().setNome("Pedro Oliveira");
        
        when(profissionalRepository.findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole()))
            .thenReturn(profissionais);

        
        Page<Profissional> resultado = profissionalService.listarComFiltros(
            PageRequest.of(0, 10), 
            "João", 
            "São Paulo", 
            3.0, 
            new String[]{"TATUAGEM_PEQUENA"}, 
            "nome"
        );

        
        assertNotNull(resultado);
        verify(profissionalRepository).findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole());
    }

    @Test
    @DisplayName("Deve manter consistência em operações sequenciais")
    void deveManterConsistenciaEmOperacoesSequenciais() throws Exception {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setNota(new BigDecimal("4.5"));
        dto.setIdEndereco(1L);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        when(enderecoRepository.findById(1L))
            .thenReturn(Optional.of(criarEndereco(1L)));
        when(profissionalRepository.save(profissional))
            .thenReturn(profissional);

        
        Profissional profissionalBuscado = profissionalService.buscarPorId(idProfissional);
        Profissional profissionalAtualizado = profissionalService.atualizar(idProfissional, dto);

        
        assertNotNull(profissionalBuscado);
        assertNotNull(profissionalAtualizado);
        assertEquals(idProfissional, profissionalBuscado.getIdProfissional());
        assertEquals(idProfissional, profissionalAtualizado.getIdProfissional());
        
        verify(profissionalRepository, times(2)).findById(idProfissional);
        verify(profissionalRepository).save(profissional);
    }

    @Test
    @DisplayName("Deve validar transações e rollback em caso de erro")
    void deveValidarTransacoesERollbackEmCasoDeErro() throws Exception {
        
        Long idUsuario = 1L;
        ProfissionalCriacaoDTO dto = criarProfissionalCriacaoDTO();
        dto.setIdUsuario(idUsuario);
        
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco(1L);
        Profissional profissional = criarProfissional(1L);
        
        when(profissionalRepository.existsByUsuario_IdUsuario(idUsuario))
            .thenReturn(false);
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(1L))
            .thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(any(Profissional.class)))
            .thenReturn(profissional);
        when(usuarioRepository.save(usuario))
            .thenReturn(usuario);
        when(portfolioService.criar(any(PortfolioDTO.class)))
            .thenThrow(new RuntimeException("Falha na criação do portfolio"));

        
        assertThrows(RuntimeException.class,
            () -> profissionalService.criarProfissionalCompleto(dto));
        
        verify(profissionalRepository).save(any(Profissional.class));
        verify(portfolioService).criar(any(PortfolioDTO.class));
    }

    @Test
    @DisplayName("Deve processar tipos de serviço com preços corretamente")
    void deveProcessarTiposServicoComPrecosCorretamente() throws Exception {
        
        Long idUsuario = 1L;
        ProfissionalCriacaoDTO dto = criarProfissionalCriacaoDTO();
        dto.setIdUsuario(idUsuario);
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA, TipoServico.TATUAGEM_MEDIA));
        
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("150.00"));
        precos.put("TATUAGEM_MEDIA", new BigDecimal("200.00"));
        dto.setPrecosServicos(precos);
        
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco(1L);
        Profissional profissionalSalvo = criarProfissional(1L);
        
        when(profissionalRepository.existsByUsuario_IdUsuario(idUsuario))
            .thenReturn(false);
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.of(usuario));
        when(enderecoRepository.findById(1L))
            .thenReturn(Optional.of(endereco));
        when(profissionalRepository.save(any(Profissional.class)))
            .thenReturn(profissionalSalvo);
        when(usuarioRepository.save(usuario))
            .thenReturn(usuario);
        when(portfolioService.criar(any(PortfolioDTO.class)))
            .thenReturn(criarPortfolio(1L));
        when(profissionalRepository.findById(1L))
            .thenReturn(Optional.of(profissionalSalvo));

        
        Profissional resultado = profissionalService.criarProfissionalCompleto(dto);

        
        assertNotNull(resultado);
        verify(profissionalRepository, times(2)).save(any(Profissional.class));
    }

    @Test
    @DisplayName("Deve converter profissional para DTO com todos os dados")
    void deveConverterProfissionalParaDTOComTodosOsDados() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        profissional.setNota(new BigDecimal("4.7"));
        profissional.setTiposServicoPrecos(Map.of(
            "TATUAGEM_PEQUENA", new BigDecimal("150.00"),
            "TATUAGEM_MEDIA", new BigDecimal("200.00")
        ));

        
        ProfissionalDTO resultado = profissionalService.converterParaDto(profissional);

        
        assertNotNull(resultado);
        assertEquals(idProfissional, resultado.getIdProfissional());
        assertEquals(profissional.getUsuario().getIdUsuario(), resultado.getIdUsuario());
        assertEquals(profissional.getEndereco().getIdEndereco(), resultado.getIdEndereco());
        assertEquals(new BigDecimal("4.7"), resultado.getNota());
        assertNotNull(resultado.getTiposServico());
        assertEquals(2, resultado.getTiposServico().size());
    }

    @Test
    @DisplayName("Deve listar tipos de serviço disponíveis")
    void deveListarTiposServicoDisponiveis() {
        
        List<Map<String, Object>> resultado = profissionalService.listarTiposServico();

        
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        
        
        assertTrue(resultado.stream().anyMatch(map -> 
            "TATUAGEM_PEQUENA".equals(map.get("nome"))));
        assertTrue(resultado.stream().anyMatch(map -> 
            "TATUAGEM_MEDIA".equals(map.get("nome"))));
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

    private Portfolio criarPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        portfolio.setDescricao("Descrição de portfolio para teste com pelo menos 20 caracteres");
        portfolio.setEspecialidade("Tatuagem Tradicional");
        portfolio.setExperiencia("5 anos de experiência");
        return portfolio;
    }

    private ProfissionalDTO criarProfissionalDTO() {
        ProfissionalDTO dto = new ProfissionalDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setNota(new BigDecimal("3.0"));
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }

    private ProfissionalCriacaoDTO criarProfissionalCriacaoDTO() {
        ProfissionalCriacaoDTO dto = new ProfissionalCriacaoDTO();
        dto.setIdUsuario(1L);
        dto.setIdEndereco(1L);
        dto.setDescricao("Descrição de portfolio para teste com pelo menos 20 caracteres");
        dto.setEspecialidade("Tatuagem Tradicional");
        dto.setExperiencia("5 anos de experiência");
        dto.setTiposServico(Arrays.asList(TipoServico.TATUAGEM_PEQUENA));
        return dto;
    }
} 