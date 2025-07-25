package inkspiration.backend.service.profissionalService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import inkspiration.backend.entities.*;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import inkspiration.backend.repository.*;
import inkspiration.backend.service.*;
import inkspiration.backend.security.AuthorizationService;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceListagemTest {

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

    @Test
    @DisplayName("Deve listar com filtros quando não há resultados")
    void deveListarComFiltrosQuandoNaoHaResultados() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String searchTerm = "Não existe";
        String locationTerm = "Cidade inexistente";
        double minRating = 5.0;
        String[] selectedSpecialties = {"TATUAGEM_PEQUENA"};
        String sortBy = "melhorAvaliacao";

        when(profissionalRepository.findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole())).thenReturn(Collections.emptyList());

        // Act
        Page<Profissional> resultado = profissionalService.listarComFiltros(
            pageable, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        verify(profissionalRepository).findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole());
    }

    @Test
    @DisplayName("Deve listar com filtros completos")
    void deveListarComFiltrosCompletos() {
        // Arrange
        Profissional profissional = new Profissional();
        profissional.setIdProfissional(1L);
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setRole(UserRole.ROLE_PROF.getRole());
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
        when(profissionalRepository.findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole())).thenReturn(profissionais);

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
        verify(profissionalRepository).findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole());
    }

    @Test
    @DisplayName("Deve excluir profissionais com role DELETED da listagem")
    void deveExcluirProfissionaisComRoleDeletedDaListagem() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        // Profissional com role DELETED não deve aparecer
        Profissional profissionalDeletado = new Profissional();
        profissionalDeletado.setIdProfissional(1L);
        Usuario usuarioDeletado = new Usuario();
        usuarioDeletado.setNome("João Deletado");
        usuarioDeletado.setRole(UserRole.ROLE_DELETED.getRole());
        profissionalDeletado.setUsuario(usuarioDeletado);
        
        // Apenas profissionais ativos devem ser retornados
        Profissional profissionalAtivo = new Profissional();
        profissionalAtivo.setIdProfissional(2L);
        Usuario usuarioAtivo = new Usuario();
        usuarioAtivo.setNome("Maria Ativa");
        usuarioAtivo.setRole(UserRole.ROLE_PROF.getRole());
        profissionalAtivo.setUsuario(usuarioAtivo);
        
        List<Profissional> profissionaisAtivos = Arrays.asList(profissionalAtivo);
        when(profissionalRepository.findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole())).thenReturn(profissionaisAtivos);
        
        // Act
        Page<Profissional> resultado = profissionalService.listarComFiltros(
            pageable, null, null, 0, null, null);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        assertEquals("Maria Ativa", resultado.getContent().get(0).getUsuario().getNome());
        assertEquals(UserRole.ROLE_PROF.getRole(), resultado.getContent().get(0).getUsuario().getRole());
        verify(profissionalRepository).findByUsuarioRoleNot(UserRole.ROLE_DELETED.getRole());
    }
} 