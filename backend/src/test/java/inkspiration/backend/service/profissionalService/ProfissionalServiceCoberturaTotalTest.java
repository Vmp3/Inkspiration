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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.profissional.DadosCompletosProfissionalException;
import inkspiration.backend.exception.profissional.TipoServicoInvalidoProfissionalException;
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
@DisplayName("ProfissionalService - Testes de Cobertura Total")
class ProfissionalServiceCoberturaTotalTest {

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
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        usuario = criarUsuario();
        endereco = criarEndereco();
        portfolio = criarPortfolio();
        profissional = criarProfissional();
        
        lenient().doNothing().when(authorizationService).requireUserAccessOrAdmin(anyLong());
        lenient().when(enderecoRepository.findById(anyLong())).thenReturn(Optional.of(endereco));
    }

    @Test
    @DisplayName("Deve atualizar profissional completo com imagens com sucesso")
    void deveAtualizarProfissionalCompletoComImagensComSucesso() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = criarRequestDataCompleto();

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(imagemService.listarPorPortfolio(any())).thenReturn(Arrays.asList(criarImagemDTO()));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(criarDisponibilidades());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("profissional"));
        assertTrue(resultado.containsKey("usuario"));
        assertTrue(resultado.containsKey("endereco"));
        assertTrue(resultado.containsKey("portfolio"));
        assertTrue(resultado.containsKey("imagens"));
        assertTrue(resultado.containsKey("disponibilidades"));
        assertTrue(resultado.containsKey("tiposServico"));
        assertTrue(resultado.containsKey("precosServicos"));
        
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository, times(2)).findByUsuario_IdUsuario(idUsuario);
        verify(imagemService, times(2)).listarPorPortfolio(any());
        verify(disponibilidadeService).cadastrarDisponibilidade(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de serviço inválido")
    void deveLancarExcecaoParaTipoServicoInvalido() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("tiposServico", Arrays.asList("TIPO_INVALIDO"));
        requestData.put("portfolio", criarPortfolioMap());
        requestData.put("precosServicos", criarPrecosServicos());

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));

        // Act & Assert
        DadosCompletosProfissionalException exception = assertThrows(
            DadosCompletosProfissionalException.class,
            () -> profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData)
        );

        // Verifica que a mensagem da exceção contém a informação sobre o tipo de serviço inválido
        assertTrue(exception.getMessage().contains("Tipo de serviço inválido"));
    }

    @Test
    @DisplayName("Deve processar atualização sem imagens")
    void deveProcessarAtualizacaoSemImagens() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("portfolio", criarPortfolioMap());
        requestData.put("tiposServico", Arrays.asList("TATUAGEM_PEQUENA"));
        requestData.put("precosServicos", criarPrecosServicos());

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(Collections.emptyMap());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        verify(imagemService, never()).deletar(any());
        verify(imagemService, never()).salvar(any());
        verify(profissionalRepository).save(any());
        verify(portfolioService).converterParaDto(any());
        
        assertTrue(resultado.containsKey("portfolio"));
        assertTrue(resultado.containsKey("imagens"));
        List<ImagemDTO> imagens = (List<ImagemDTO>) resultado.get("imagens");
        assertTrue(imagens.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar imagens existentes e adicionar novas")
    void deveDeletarImagensExistentesEAdicionarNovas() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = criarRequestDataCompleto();
        
        ImagemDTO imagemExistente = criarImagemDTO();
        imagemExistente.setIdImagem(1L);

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(imagemService.listarPorPortfolio(portfolio.getIdPortfolio()))
            .thenReturn(Arrays.asList(imagemExistente));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(criarDisponibilidades());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        verify(imagemService).deletar(imagemExistente.getIdImagem());
        verify(imagemService).salvar(any(ImagemDTO.class));
    }

    @Test
    @DisplayName("Deve processar disponibilidades quando fornecidas")
    void deveProcessarDisponibilidadesQuandoFornecidas() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("disponibilidades", criarDisponibilidades());
        requestData.put("portfolio", criarPortfolioMap());
        requestData.put("tiposServico", Arrays.asList("TATUAGEM_PEQUENA"));
        requestData.put("precosServicos", criarPrecosServicos());

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(Collections.emptyMap());
        when(disponibilidadeService.cadastrarDisponibilidade(any(), any())).thenReturn(new Disponibilidade());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        verify(disponibilidadeService).cadastrarDisponibilidade(profissional.getIdProfissional(), criarDisponibilidades());
        verify(profissionalRepository).save(any());
        verify(portfolioService).converterParaDto(any());
        
        assertTrue(resultado.containsKey("disponibilidades"));
        assertTrue(resultado.containsKey("portfolio"));
        assertTrue(resultado.containsKey("tiposServico"));
        assertTrue(resultado.containsKey("precosServicos"));
    }

    @Test
    @DisplayName("Deve lançar exceção DadosCompletosProfissionalException em caso de erro")
    void deveLancarExcecaoDadosCompletosProfissionalException() {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = criarRequestDataCompleto();

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenThrow(new RuntimeException("Erro de banco"));

        // Act & Assert
        DadosCompletosProfissionalException exception = assertThrows(
            DadosCompletosProfissionalException.class,
            () -> profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData)
        );

        assertTrue(exception.getMessage().contains("Erro ao atualizar dados completos"));
    }

    @Test
    @DisplayName("Deve montar profissional completo com todos os dados")
    void deveMontarProfissionalCompletoComTodosOsDados() throws JsonProcessingException {
        // Arrange
        when(portfolioService.converterParaDto(portfolio)).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(portfolio.getIdPortfolio()))
            .thenReturn(Arrays.asList(criarImagemDTO()));
        when(disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional()))
            .thenReturn(criarDisponibilidades());

        // Act
        Map<String, Object> resultado = (Map<String, Object>) invokePrivateMethod(
            profissionalService, "montarProfissionalCompleto", profissional);

        // Assert
        assertNotNull(resultado);
        
        // Verificar estrutura do profissional
        assertTrue(resultado.containsKey("profissional"));
        ProfissionalDTO profissionalDto = (ProfissionalDTO) resultado.get("profissional");
        assertNotNull(profissionalDto);
        
        // Verificar dados do usuário
        assertTrue(resultado.containsKey("usuario"));
        Map<String, Object> usuarioInfo = (Map<String, Object>) resultado.get("usuario");
        assertEquals(usuario.getIdUsuario(), usuarioInfo.get("idUsuario"));
        assertEquals(usuario.getNome(), usuarioInfo.get("nome"));
        assertEquals(usuario.getEmail(), usuarioInfo.get("email"));
        assertEquals(usuario.getTelefone(), usuarioInfo.get("telefone"));
        assertEquals(usuario.getImagemPerfil(), usuarioInfo.get("imagemPerfil"));
        
        // Verificar dados do endereço
        assertTrue(resultado.containsKey("endereco"));
        Map<String, Object> enderecoInfo = (Map<String, Object>) resultado.get("endereco");
        assertEquals(endereco.getIdEndereco(), enderecoInfo.get("idEndereco"));
        assertEquals(endereco.getCep(), enderecoInfo.get("cep"));
        assertEquals(endereco.getRua(), enderecoInfo.get("rua"));
        assertEquals(endereco.getBairro(), enderecoInfo.get("bairro"));
        assertEquals(endereco.getCidade(), enderecoInfo.get("cidade"));
        assertEquals(endereco.getEstado(), enderecoInfo.get("estado"));
        assertEquals(endereco.getNumero(), enderecoInfo.get("numero"));
        assertEquals(endereco.getComplemento(), enderecoInfo.get("complemento"));
        assertEquals(endereco.getLatitude(), enderecoInfo.get("latitude"));
        assertEquals(endereco.getLongitude(), enderecoInfo.get("longitude"));
        
        // Verificar outros campos
        assertTrue(resultado.containsKey("portfolio"));
        assertTrue(resultado.containsKey("imagens"));
        assertTrue(resultado.containsKey("disponibilidades"));
        assertTrue(resultado.containsKey("tiposServico"));
        assertTrue(resultado.containsKey("precosServicos"));
        assertTrue(resultado.containsKey("tiposServicoPrecos"));
    }

    @Test
    @DisplayName("Deve montar profissional completo com dados nulos")
    void deveMontarProfissionalCompletoComDadosNulos() throws JsonProcessingException {
        // Arrange
        Profissional profissionalSemDados = new Profissional();
        profissionalSemDados.setIdProfissional(1L);
        
        // Criar usuário vazio para evitar NullPointerException
        Usuario usuarioVazio = new Usuario();
        profissionalSemDados.setUsuario(usuarioVazio);
        
        // Criar endereço vazio para evitar NullPointerException
        Endereco enderecoVazio = new Endereco();
        profissionalSemDados.setEndereco(enderecoVazio);

        // Act
        Map<String, Object> resultado = (Map<String, Object>) invokePrivateMethod(
            profissionalService, "montarProfissionalCompleto", profissionalSemDados);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("profissional"));
        assertTrue(resultado.containsKey("usuario"));
        assertTrue(resultado.containsKey("endereco"));
        assertTrue(resultado.containsKey("portfolio"));
        assertTrue(resultado.containsKey("imagens"));
        assertTrue(resultado.containsKey("disponibilidades"));
        
        // Verificar que dados nulos são tratados adequadamente
        Map<String, Object> usuarioInfo = (Map<String, Object>) resultado.get("usuario");
        assertTrue(usuarioInfo.values().stream().allMatch(v -> v == null));
        
        Map<String, Object> enderecoInfo = (Map<String, Object>) resultado.get("endereco");
        assertTrue(enderecoInfo.values().stream().allMatch(v -> v == null));
        
        assertNull(resultado.get("portfolio"));
        
        List<ImagemDTO> imagens = (List<ImagemDTO>) resultado.get("imagens");
        assertTrue(imagens.isEmpty());
        
        Map<String, List<Map<String, String>>> disponibilidades = 
            (Map<String, List<Map<String, String>>>) resultado.get("disponibilidades");
        assertTrue(disponibilidades.isEmpty());
    }

    @Test
    @DisplayName("Deve tratar exceção ao obter disponibilidades")
    void deveTratarExcecaoAoObterDisponibilidades() throws JsonProcessingException {
        // Arrange
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any()))
            .thenThrow(new RuntimeException("Erro ao buscar disponibilidades"));

        // Act
        Map<String, Object> resultado = (Map<String, Object>) invokePrivateMethod(
            profissionalService, "montarProfissionalCompleto", profissional);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("disponibilidades"));
        Map<String, List<Map<String, String>>> disponibilidades = 
            (Map<String, List<Map<String, String>>>) resultado.get("disponibilidades");
        assertTrue(disponibilidades.isEmpty());
    }

    @Test
    @DisplayName("Deve processar imagens sem campo imagemBase64")
    void deveProcessarImagensSemCampoImagemBase64() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("portfolio", criarPortfolioMap());
        
        // Criar imagem sem campo imagemBase64
        Map<String, Object> imagemSemBase64 = new HashMap<>();
        imagemSemBase64.put("id", 1);
        imagemSemBase64.put("nome", "imagem.jpg");
        
        List<Map<String, Object>> imagensData = Arrays.asList(imagemSemBase64);
        requestData.put("imagens", imagensData);

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(Collections.emptyMap());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        // Verifica que não tentou salvar imagem sem base64
        verify(imagemService, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve processar quando não há portfolio no profissional")
    void deveProcessarQuandoNaoHaPortfolioNoProfissional() throws JsonProcessingException {
        // Arrange
        Profissional profissionalSemPortfolio = new Profissional();
        profissionalSemPortfolio.setIdProfissional(1L);
        profissionalSemPortfolio.setUsuario(usuario);
        profissionalSemPortfolio.setEndereco(endereco);
        profissionalSemPortfolio.setPortfolio(null); // Sem portfolio
        // Act
        Map<String, Object> resultado = (Map<String, Object>) invokePrivateMethod(
            profissionalService, "montarProfissionalCompleto", profissionalSemPortfolio);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("portfolio"));
        assertNull(resultado.get("portfolio"));
        
        List<ImagemDTO> imagens = (List<ImagemDTO>) resultado.get("imagens");
        assertTrue(imagens.isEmpty());
        
        verify(portfolioService, never()).converterParaDto(any());
    }

    @Test
    @DisplayName("Deve montar profissional completo com portfolio nulo mas portfolio service retorna dados")
    void deveMontarProfissionalCompletoComPortfolioNuloMasServiceRetornaDados() throws JsonProcessingException {
        // Arrange
        Profissional profissionalSemPortfolio = new Profissional();
        profissionalSemPortfolio.setIdProfissional(1L);
        profissionalSemPortfolio.setUsuario(usuario);
        profissionalSemPortfolio.setEndereco(endereco);
        profissionalSemPortfolio.setPortfolio(null);

        // Act
        Map<String, Object> resultado = (Map<String, Object>) invokePrivateMethod(
            profissionalService, "montarProfissionalCompleto", profissionalSemPortfolio);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("portfolio"));
        assertNull(resultado.get("portfolio"));
        
        // Verifica que não tentou chamar serviços com portfolio nulo
        verify(portfolioService, never()).converterParaDto(any());
    }

    @Test
    @DisplayName("Deve processar atualização sem tipos de serviço")
    void deveProcessarAtualizacaoSemTiposServico() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("portfolio", criarPortfolioMap());
        // Não incluir tiposServico para testar esse caminho

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(Collections.emptyMap());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("tiposServico"));
    }

    @Test
    @DisplayName("Deve processar atualização sem preços de serviços")
    void deveProcessarAtualizacaoSemPrecosServicos() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("portfolio", criarPortfolioMap());
        requestData.put("tiposServico", Arrays.asList("TATUAGEM_PEQUENA"));
        // Não incluir precosServicos para testar esse caminho

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(any())).thenReturn(criarPortfolioDTO());
        when(imagemService.listarPorPortfolio(any())).thenReturn(Collections.emptyList());
        when(disponibilidadeService.obterDisponibilidade(any())).thenReturn(Collections.emptyMap());

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("precosServicos"));
    }

    @Test
    @DisplayName("Deve atualizar profissional com dados de portfolio e preços completos")
    void deveAtualizarProfissionalComDadosPortfolioEPrecosCompletos() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        
        // Portfolio completo com todos os campos
        Map<String, Object> portfolioCompleto = new HashMap<>();
        portfolioCompleto.put("descricao", "Descrição completa");
        portfolioCompleto.put("especialidade", "Tatuagem Realista");
        portfolioCompleto.put("experiencia", "10 anos");
        portfolioCompleto.put("website", "www.exemplo.com");
        portfolioCompleto.put("instagram", "@exemplo");
        portfolioCompleto.put("facebook", "facebook.com/exemplo");
        portfolioCompleto.put("twitter", "@exemplo");
        portfolioCompleto.put("tiktok", "@exemplo");
        
        requestData.put("portfolio", portfolioCompleto);
        requestData.put("tiposServico", Arrays.asList("TATUAGEM_PEQUENA", "TATUAGEM_MEDIA", "TATUAGEM_GRANDE"));
        
        // Preços para múltiplos serviços
        Map<String, Object> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", 200.00);
        precos.put("TATUAGEM_MEDIA", 400.00);
        precos.put("TATUAGEM_GRANDE", 800.00);
        requestData.put("precosServicos", precos);

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("precosServicos"));
        
        verify(profissionalRepository, atLeast(1)).save(any());
    }

    @Test
    @DisplayName("Deve lidar com múltiplas imagens no processamento")
    void deveLidarComMultiplasImagensNoProcessamento() throws Exception {
        // Arrange
        Long idUsuario = 1L;
        Map<String, Object> requestData = new HashMap<>();
        
        // Múltiplas imagens
        List<Map<String, Object>> imagensData = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> imagem = new HashMap<>();
            imagem.put("imagemBase64", "base64imagemdata" + i);
            imagensData.add(imagem);
        }
        requestData.put("imagens", imagensData);
        requestData.put("portfolio", criarPortfolioMap());

        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(profissionalRepository.save(any())).thenReturn(profissional);
        when(profissionalRepository.findById(any())).thenReturn(Optional.of(profissional));

        // Act
        Map<String, Object> resultado = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);

        // Assert
        assertNotNull(resultado);
        // Verifica que tentou salvar 3 imagens
        verify(imagemService, times(3)).salvar(any(ImagemDTO.class));
    }

    // Métodos auxiliares
    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setCpf("12345678901");
        usuario.setTelefone("11999999999");
        usuario.setImagemPerfil("imagem_perfil.jpg");
        usuario.setRole(UserRole.ROLE_PROF.getRole());
        return usuario;
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setCep("01234-567");
        endereco.setComplemento("Apt 101");
        endereco.setLatitude(-23.5505);
        endereco.setLongitude(-46.6333);
        return endereco;
    }

    private Portfolio criarPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio de tatuagens");
        portfolio.setEspecialidade("Tatuagem Realista");
        portfolio.setExperiencia("5 anos");
        portfolio.setWebsite("http://www.exemplo.com");
        return portfolio;
    }

    private Profissional criarProfissional() {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortfolio(portfolio);
        
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("200.00"));
        precos.put("TATUAGEM_MEDIA", new BigDecimal("400.00"));
        profissional.setTiposServicoPrecos(precos);
        
        return profissional;
    }

    private ImagemDTO criarImagemDTO() {
        ImagemDTO imagem = new ImagemDTO();
        imagem.setIdImagem(1L);
        imagem.setIdPortfolio(1L);
        imagem.setImagemBase64("base64imagemdata");
        return imagem;
    }

    private PortfolioDTO criarPortfolioDTO() {
        PortfolioDTO portfolio = new PortfolioDTO();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Portfolio de tatuagens");
        portfolio.setEspecialidade("Tatuagem Realista");
        portfolio.setExperiencia("5 anos");
        return portfolio;
    }

    private Map<String, Object> criarRequestDataCompleto() {
        Map<String, Object> requestData = new HashMap<>();
        
        requestData.put("portfolio", criarPortfolioMap());
        requestData.put("tiposServico", Arrays.asList("TATUAGEM_PEQUENA", "TATUAGEM_MEDIA"));
        requestData.put("precosServicos", criarPrecosServicos());
        requestData.put("imagens", criarImagensData());
        requestData.put("disponibilidades", criarDisponibilidades());
        
        return requestData;
    }

    private Map<String, Object> criarPortfolioMap() {
        Map<String, Object> portfolio = new HashMap<>();
        portfolio.put("descricao", "Portfolio atualizado");
        portfolio.put("especialidade", "Tatuagem Realista");
        portfolio.put("experiencia", "5 anos");
        return portfolio;
    }

    private Map<String, Object> criarPrecosServicos() {
        Map<String, Object> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", 200.00);
        precos.put("TATUAGEM_MEDIA", 400.00);
        return precos;
    }

    private List<Map<String, Object>> criarImagensData() {
        Map<String, Object> imagem = new HashMap<>();
        imagem.put("imagemBase64", "base64imagemdata");
        return Arrays.asList(imagem);
    }

    private Map<String, List<Map<String, String>>> criarDisponibilidades() {
        Map<String, String> horario = new HashMap<>();
        horario.put("inicio", "09:00");
        horario.put("fim", "18:00");
        
        Map<String, List<Map<String, String>>> disponibilidades = new HashMap<>();
        disponibilidades.put("segunda", Arrays.asList(horario));
        
        return disponibilidades;
    }

    // Método utilitário para invocar métodos privados usando reflection
    private Object invokePrivateMethod(Object target, String methodName, Object... args) {
        try {
            Class<?> clazz = target.getClass();
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            
            java.lang.reflect.Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao invocar método privado: " + methodName, e);
        }
    }
} 