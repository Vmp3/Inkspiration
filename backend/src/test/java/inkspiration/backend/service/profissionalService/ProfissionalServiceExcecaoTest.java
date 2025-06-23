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

import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.profissional.EnderecoNaoEncontradoException;
import inkspiration.backend.exception.profissional.ProfissionalJaExisteException;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceExcecaoTest {

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
    @DisplayName("Deve lançar ProfissionalNaoEncontradoException ao buscar por ID inexistente")
    void deveLancarProfissionalNaoEncontradoExceptionAoBuscarPorIdInexistente() {
        
        Long idProfissional = 999L;
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.empty());

        
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> profissionalService.buscarPorId(idProfissional)
        );

        
        assertTrue(exception.getMessage().contains("Profissional não encontrado com ID: " + idProfissional));
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve lançar ProfissionalNaoEncontradoException ao buscar por usuário inexistente")
    void deveLancarProfissionalNaoEncontradoExceptionAoBuscarPorUsuarioInexistente() {
        
        Long idUsuario = 999L;
        when(profissionalRepository.findByUsuario_IdUsuario(idUsuario))
            .thenReturn(Optional.empty());

        
        ProfissionalNaoEncontradoException exception = assertThrows(
            ProfissionalNaoEncontradoException.class,
            () -> profissionalService.buscarPorUsuario(idUsuario)
        );

        
        assertTrue(exception.getMessage().contains("Perfil profissional não encontrado para o usuário com ID: " + idUsuario));
        verify(profissionalRepository).findByUsuario_IdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException ao verificar perfil com usuário inexistente")
    void deveLancarUsuarioNaoEncontradoExceptionAoVerificarPerfilComUsuarioInexistente() {
        
        Long idUsuario = 999L;
        when(usuarioRepository.findById(idUsuario))
            .thenReturn(Optional.empty());

        
        UsuarioException.UsuarioNaoEncontradoException exception = assertThrows(
            UsuarioException.UsuarioNaoEncontradoException.class,
            () -> profissionalService.existePerfil(idUsuario)
        );

        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar ProfissionalJaExisteException ao criar profissional para usuário que já possui")
    void deveLancarProfissionalJaExisteExceptionAoCriarProfissionalParaUsuarioQueJaPossui() {
        
        Long idUsuario = 1L;
        ProfissionalCriacaoDTO dto = criarProfissionalCriacaoDTO();
        dto.setIdUsuario(idUsuario);
        
        when(profissionalRepository.existsByUsuario_IdUsuario(idUsuario))
            .thenReturn(true);

        
        ProfissionalJaExisteException exception = assertThrows(
            ProfissionalJaExisteException.class,
            () -> profissionalService.criarProfissionalCompleto(dto)
        );

        
        assertEquals("Já existe um profissional cadastrado para este usuário", exception.getMessage());
        verify(profissionalRepository).existsByUsuario_IdUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve lançar EnderecoNaoEncontradoException ao atualizar com endereço inexistente")
    void deveLancarEnderecoNaoEncontradoExceptionAoAtualizarComEnderecoInexistente() {
        
        Long idProfissional = 1L;
        Long idEnderecoInexistente = 999L;
        Profissional profissionalExistente = criarProfissional(idProfissional);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(idEnderecoInexistente);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(idEnderecoInexistente))
            .thenReturn(Optional.empty());

        
        EnderecoNaoEncontradoException exception = assertThrows(
            EnderecoNaoEncontradoException.class,
            () -> profissionalService.atualizar(idProfissional, dto)
        );

        
        assertTrue(exception.getMessage().contains("Endereço não encontrado com ID: " + idEnderecoInexistente));
        verify(enderecoRepository).findById(idEnderecoInexistente);
    }

    @Test
    @DisplayName("Deve lançar EnderecoNaoEncontradoException ao buscar endereço inexistente")
    void deveLancarEnderecoNaoEncontradoExceptionAoBuscarEnderecoInexistente() {
        
        Long idEndereco = 999L;
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.empty());

        
        EnderecoNaoEncontradoException exception = assertThrows(
            EnderecoNaoEncontradoException.class,
            () -> profissionalService.buscarEnderecoPorId(idEndereco)
        );

        
        assertTrue(exception.getMessage().contains("Endereço não encontrado com ID: " + idEndereco));
        verify(enderecoRepository).findById(idEndereco);
    }

    @Test
    @DisplayName("Deve tratar erro de validação de endereço")
    void deveTratarErroValidacaoEndereco() {
        
        Long idProfissional = 1L;
        Long idEndereco = 1L;
        Profissional profissionalExistente = criarProfissional(idProfissional);
        Endereco endereco = criarEndereco(idEndereco);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setIdEndereco(idEndereco);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(idEndereco))
            .thenReturn(Optional.of(endereco));
        doThrow(new RuntimeException("Erro de validação de endereço"))
            .when(enderecoService).validarEndereco(endereco);

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.atualizar(idProfissional, dto)
        );

        
        assertEquals("Erro de validação de endereço", exception.getMessage());
        verify(enderecoService).validarEndereco(endereco);
    }

    @Test
    @DisplayName("Deve tratar erro de transação ao salvar profissional")
    void deveTratarErroTransacaoAoSalvarProfissional() {
        
        Long idProfissional = 1L;
        Profissional profissionalExistente = criarProfissional(idProfissional);
        
        ProfissionalDTO dto = criarProfissionalDTO();
        dto.setNota(new BigDecimal("4.5"));
        dto.setIdEndereco(1L);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissionalExistente));
        when(enderecoRepository.findById(1L))
            .thenReturn(Optional.of(criarEndereco(1L)));
        when(profissionalRepository.save(profissionalExistente))
            .thenThrow(new RuntimeException("Erro de transação"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.atualizar(idProfissional, dto)
        );

        
        assertEquals("Erro de transação", exception.getMessage());
        verify(profissionalRepository).save(profissionalExistente);
    }

    @Test
    @DisplayName("Deve tratar erro de constraint violation")
    void deveTratarErroConstraintViolation() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        doThrow(new RuntimeException("Constraint violation"))
            .when(profissionalRepository).delete(profissional);

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.deletar(idProfissional)
        );

        
        assertEquals("Constraint violation", exception.getMessage());
        verify(profissionalRepository).delete(profissional);
    }

    @Test
    @DisplayName("Deve tratar erro de acesso negado")
    void deveTratarErroAcessoNegado() {
        
        doThrow(new RuntimeException("Acesso negado"))
            .when(authorizationService).requireAdmin();

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.listarComAutorizacao(null)
        );

        
        assertEquals("Acesso negado", exception.getMessage());
        verify(authorizationService).requireAdmin();
    }

    @Test
    @DisplayName("Deve tratar erro de timeout no banco")
    void deveTratarErroTimeoutNoBanco() {
        
        Long idProfissional = 1L;
        when(profissionalRepository.findById(idProfissional))
            .thenThrow(new RuntimeException("Connection timeout"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.buscarPorId(idProfissional)
        );

        
        assertEquals("Connection timeout", exception.getMessage());
        verify(profissionalRepository).findById(idProfissional);
    }

    @Test
    @DisplayName("Deve tratar erro de JSON processing")
    void deveTratarErroJsonProcessing() throws Exception {
        
        Long idUsuario = 1L;
        ProfissionalCriacaoDTO dto = criarProfissionalCriacaoDTO();
        dto.setIdUsuario(idUsuario);
        // Criar uma disponibilidade para forçar a chamada do método
        DisponibilidadeDTO dispDTO = new DisponibilidadeDTO();
        dispDTO.setHrAtendimento("Segunda-08:00-12:00");
        dto.setDisponibilidades(Arrays.asList(dispDTO));
        
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
        when(portfolioService.criar(any()))
            .thenReturn(criarPortfolio(1L));
        when(profissionalRepository.findById(any()))
            .thenReturn(Optional.of(profissional));
        
        when(disponibilidadeService.cadastrarDisponibilidade(any(), any()))
            .thenThrow(new RuntimeException("JSON processing error"));

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.criarProfissionalCompleto(dto)
        );

        
        assertEquals("JSON processing error", exception.getMessage());
    }

    @Test
    @DisplayName("Deve tratar erro de deadlock no banco")
    void deveTratarErroDeadlockNoBanco() {
        
        Long idProfissional = 1L;
        Profissional profissional = criarProfissional(idProfissional);
        
        when(profissionalRepository.findById(idProfissional))
            .thenReturn(Optional.of(profissional));
        doThrow(new RuntimeException("Deadlock detected"))
            .when(profissionalRepository).delete(profissional);

        
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> profissionalService.deletar(idProfissional)
        );

        
        assertEquals("Deadlock detected", exception.getMessage());
        verify(profissionalRepository).delete(profissional);
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
        return dto;
    }

    private Portfolio criarPortfolio(Long id) {
        Portfolio portfolio = new Portfolio();
        portfolio.setIdPortfolio(id);
        portfolio.setDescricao("Descrição de portfolio para teste com pelo menos 20 caracteres");
        portfolio.setEspecialidade("Tatuagem Tradicional");
        portfolio.setExperiencia("5 anos de experiência");
        return portfolio;
    }
} 