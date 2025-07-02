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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
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
@DisplayName("ProfissionalService - Testes buscarProfissionalCompletoComAutorizacao")
class ProfissionalServiceBuscarCompletoTest {

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
        lenient().doNothing().when(authorizationService).requireUserAccessOrAdmin(anyLong());
    }

    @Test
    @DisplayName("Deve buscar profissional completo com autorização - caso com portfolio")
    void deveBuscarProfissionalCompletoComAutorizacaoComPortfolio() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco();
        Portfolio portfolio = criarPortfolio();
        Profissional profissional = criarProfissional(usuario, endereco, portfolio);
        
        PortfolioDTO portfolioDTO = criarPortfolioDTO();
        List<ImagemDTO> imagens = Arrays.asList(criarImagemDTO());
        Map<String, List<Map<String, String>>> disponibilidades = criarDisponibilidades();
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(portfolio)).thenReturn(portfolioDTO);
        when(imagemService.listarPorPortfolio(1L)).thenReturn(imagens);
        when(disponibilidadeService.obterDisponibilidade(1L)).thenReturn(disponibilidades);

        // Act
        ProfissionalService.ProfissionalCompletoData resultado = 
            profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getProfissional());
        assertNotNull(resultado.getPortfolio());
        assertNotNull(resultado.getImagens());
        assertEquals(1, resultado.getImagens().size());
        assertNotNull(resultado.getDisponibilidades());
        assertNotNull(resultado.getTiposServico());
        assertNotNull(resultado.getPrecosServicos());
        assertNotNull(resultado.getTiposServicoPrecos());
        
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        verify(portfolioService).converterParaDto(portfolio);
        verify(imagemService).listarPorPortfolio(1L);
        verify(disponibilidadeService).obterDisponibilidade(1L);
    }

    @Test
    @DisplayName("Deve buscar profissional completo com autorização - caso sem portfolio")
    void deveBuscarProfissionalCompletoComAutorizacaoSemPortfolio() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco();
        Profissional profissional = criarProfissional(usuario, endereco, null); // Sem portfolio
        
        Map<String, List<Map<String, String>>> disponibilidades = criarDisponibilidades();
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(disponibilidadeService.obterDisponibilidade(1L)).thenReturn(disponibilidades);

        // Act
        ProfissionalService.ProfissionalCompletoData resultado = 
            profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getProfissional());
        assertNull(resultado.getPortfolio()); // Portfolio deve ser null
        assertNotNull(resultado.getImagens());
        assertTrue(resultado.getImagens().isEmpty()); // Lista vazia pois não há portfolio
        assertNotNull(resultado.getDisponibilidades());
        assertNotNull(resultado.getTiposServico());
        assertNotNull(resultado.getPrecosServicos());
        assertNotNull(resultado.getTiposServicoPrecos());
        
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        verify(portfolioService, never()).converterParaDto(any()); // Não deve chamar pois portfolio é null
        verify(imagemService, never()).listarPorPortfolio(any()); // Não deve chamar pois portfolio é null
        verify(disponibilidadeService).obterDisponibilidade(1L);
    }

    @Test
    @DisplayName("Deve buscar profissional completo com autorização - tratando exceção de disponibilidade")
    void deveBuscarProfissionalCompletoComAutorizacaoTratandoExcecaoDisponibilidade() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco();
        Portfolio portfolio = criarPortfolio();
        Profissional profissional = criarProfissional(usuario, endereco, portfolio);
        
        PortfolioDTO portfolioDTO = criarPortfolioDTO();
        List<ImagemDTO> imagens = Arrays.asList(criarImagemDTO());
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(portfolio)).thenReturn(portfolioDTO);
        when(imagemService.listarPorPortfolio(1L)).thenReturn(imagens);
        when(disponibilidadeService.obterDisponibilidade(1L))
            .thenThrow(new RuntimeException("Erro ao buscar disponibilidades"));

        // Act
        ProfissionalService.ProfissionalCompletoData resultado = 
            profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getProfissional());
        assertNotNull(resultado.getPortfolio());
        assertNotNull(resultado.getImagens());
        assertEquals(1, resultado.getImagens().size());
        assertNotNull(resultado.getDisponibilidades());
        assertTrue(resultado.getDisponibilidades().isEmpty()); // Deve ser empty map devido à exceção
        assertNotNull(resultado.getTiposServico());
        assertNotNull(resultado.getPrecosServicos());
        assertNotNull(resultado.getTiposServicoPrecos());
        
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        verify(portfolioService).converterParaDto(portfolio);
        verify(imagemService).listarPorPortfolio(1L);
        verify(disponibilidadeService).obterDisponibilidade(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando profissional não existe para busca completa")
    void deveLancarExcecaoQuandoProfissionalNaoExisteParaBuscaCompleta() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 999L;
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.empty());

        // Act & Assert
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario)
        );

        assertTrue(exception.getMessage().contains("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        // Não deve chamar outros services quando profissional não é encontrado
        verify(portfolioService, never()).converterParaDto(any());
        verify(imagemService, never()).listarPorPortfolio(any());
        verify(disponibilidadeService, never()).obterDisponibilidade(any());
    }

    @Test
    @DisplayName("Deve verificar autorização antes de buscar profissional completo")
    void deveVerificarAutorizacaoAntesDeBuscarProfissionalCompleto() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        
        // Simula falha na autorização
        doThrow(new RuntimeException("Acesso negado"))
            .when(authorizationService).requireUserAccessOrAdmin(idUsuario);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario)
        );

        assertEquals("Acesso negado", exception.getMessage());
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        // Não deve chamar buscarPorUsuario nem outros métodos quando autorização falha
        verify(profissionalRepository, never()).findByUsuario_IdUsuario(any());
        verify(portfolioService, never()).converterParaDto(any());
        verify(imagemService, never()).listarPorPortfolio(any());
        verify(disponibilidadeService, never()).obterDisponibilidade(any());
    }

    @Test
    @DisplayName("Deve buscar profissional completo com portfolio mas sem imagens")
    void deveBuscarProfissionalCompletoComPortfolioMasSemImagens() throws JsonProcessingException {
        // Arrange
        Long idUsuario = 1L;
        Usuario usuario = criarUsuario(idUsuario);
        Endereco endereco = criarEndereco();
        Portfolio portfolio = criarPortfolio();
        Profissional profissional = criarProfissional(usuario, endereco, portfolio);
        
        PortfolioDTO portfolioDTO = criarPortfolioDTO();
        List<ImagemDTO> imagensVazia = Collections.emptyList();
        Map<String, List<Map<String, String>>> disponibilidades = criarDisponibilidades();
        
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.of(profissional));
        when(portfolioService.converterParaDto(portfolio)).thenReturn(portfolioDTO);
        when(imagemService.listarPorPortfolio(1L)).thenReturn(imagensVazia);
        when(disponibilidadeService.obterDisponibilidade(1L)).thenReturn(disponibilidades);

        // Act
        ProfissionalService.ProfissionalCompletoData resultado = 
            profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getProfissional());
        assertNotNull(resultado.getPortfolio());
        assertNotNull(resultado.getImagens());
        assertTrue(resultado.getImagens().isEmpty()); // Lista vazia de imagens
        assertNotNull(resultado.getDisponibilidades());
        assertNotNull(resultado.getTiposServico());
        assertNotNull(resultado.getPrecosServicos());
        assertNotNull(resultado.getTiposServicoPrecos());
        
        verify(authorizationService).requireUserAccessOrAdmin(idUsuario);
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
        verify(portfolioService).converterParaDto(portfolio);
        verify(imagemService).listarPorPortfolio(1L);
        verify(disponibilidadeService).obterDisponibilidade(1L);
    }

    // Métodos auxiliares para criar objetos de teste
    private Usuario criarUsuario(Long idUsuario) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setRole(UserRole.ROLE_PROF.getRole());
        return usuario;
    }

    private Endereco criarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(1L);
        endereco.setCep("12345678");
        endereco.setRua("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        return endereco;
    }

    private Portfolio criarPortfolio() {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(1L);
        portfolio.setDescricao("Descrição do portfolio");
        portfolio.setEspecialidade("Tatuagem Realista");
        portfolio.setExperiencia("5 anos");
        return portfolio;
    }

    private Profissional criarProfissional(Usuario usuario, Endereco endereco, Portfolio portfolio) {
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortfolio(portfolio);
        profissional.setNota(new BigDecimal("4.5"));
        
        // Configurar tipos de serviço com preços
        Map<String, BigDecimal> tiposServicoPrecos = new HashMap<>();
        tiposServicoPrecos.put("TATUAGEM_PEQUENA", new BigDecimal("150.00"));
        tiposServicoPrecos.put("TATUAGEM_MEDIA", new BigDecimal("300.00"));
        profissional.setTiposServicoPrecos(tiposServicoPrecos);
        
        return profissional;
    }

    private ImagemDTO criarImagemDTO() {
        ImagemDTO imagemDTO = new ImagemDTO();
        imagemDTO.setIdImagem(1L);
        imagemDTO.setImagemBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD");
        imagemDTO.setIdPortfolio(1L);
        return imagemDTO;
    }

    private PortfolioDTO criarPortfolioDTO() {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setIdPortfolio(1L);
        portfolioDTO.setDescricao("Descrição do portfolio");
        portfolioDTO.setEspecialidade("Tatuagem Realista");
        portfolioDTO.setExperiencia("5 anos");
        return portfolioDTO;
    }

    private Map<String, List<Map<String, String>>> criarDisponibilidades() {
        Map<String, List<Map<String, String>>> disponibilidades = new HashMap<>();
        
        List<Map<String, String>> segundaFeira = new ArrayList<>();
        Map<String, String> horario1 = new HashMap<>();
        horario1.put("inicio", "09:00");
        horario1.put("fim", "12:00");
        segundaFeira.add(horario1);
        
        Map<String, String> horario2 = new HashMap<>();
        horario2.put("inicio", "14:00");
        horario2.put("fim", "17:00");
        segundaFeira.add(horario2);
        
        disponibilidades.put("SEGUNDA", segundaFeira);
        
        return disponibilidades;
    }
} 