package inkspiration.backend.security;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenResponseFilter - Testes Unitários")
class TokenResponseFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TokenResponseFilter filter;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve continuar chain quando não há header de autorização")
    void deveContinuarChainQuandoNaoHaHeaderAutorizacao() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Deve continuar chain quando header não começa com Bearer")
    void deveContinuarChainQuandoHeaderNaoComecaComBearer() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Token abc123");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Deve continuar chain quando token está revogado")
    void deveContinuarChainQuandoTokenRevogado() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");
        when(jwtService.isTokenRevogado("abc123")).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).isTokenRevogado("abc123");
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    @DisplayName("Deve adicionar novo token no header quando token atual é diferente")
    void deveAdicionarNovoTokenNoHeaderQuandoTokenAtualDiferente() throws ServletException, IOException {
        // Arrange
        String token = "abc123";
        String novoToken = "xyz789";
        Long userId = 1L;

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenRevogado(token)).thenReturn(false);
        when(jwtService.getUserIdFromToken(token)).thenReturn(userId);

        Usuario usuario = new Usuario();
        usuario.setTokenAtual(novoToken);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader("New-Auth-Token", novoToken);
        verify(response).setHeader("Access-Control-Expose-Headers", "New-Auth-Token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve adicionar novo token quando token atual é igual")
    void naoDeveAdicionarNovoTokenQuandoTokenAtualIgual() throws ServletException, IOException {
        // Arrange
        String token = "abc123";
        Long userId = 1L;

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenRevogado(token)).thenReturn(false);
        when(jwtService.getUserIdFromToken(token)).thenReturn(userId);

        Usuario usuario = new Usuario();
        usuario.setTokenAtual(token);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, never()).setHeader(eq("New-Auth-Token"), any());
        verify(response, never()).setHeader(eq("Access-Control-Expose-Headers"), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar chain quando usuário não é encontrado")
    void deveContinuarChainQuandoUsuarioNaoEncontrado() throws ServletException, IOException {
        // Arrange
        String token = "abc123";
        Long userId = 1L;

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenRevogado(token)).thenReturn(false);
        when(jwtService.getUserIdFromToken(token)).thenReturn(userId);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response, never()).setHeader(eq("New-Auth-Token"), any());
        verify(response, never()).setHeader(eq("Access-Control-Expose-Headers"), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar chain quando ocorre exceção ao processar token")
    void deveContinuarChainQuandoOcorreExcecaoAoProcessarToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");
        when(jwtService.isTokenRevogado("abc123")).thenThrow(new RuntimeException("Erro ao processar token"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setHeader(eq("New-Auth-Token"), any());
        verify(response, never()).setHeader(eq("Access-Control-Expose-Headers"), any());
    }
} 