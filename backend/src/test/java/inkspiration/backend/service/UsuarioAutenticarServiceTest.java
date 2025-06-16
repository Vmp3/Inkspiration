package inkspiration.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.repository.UsuarioAutenticarRepository;
import inkspiration.backend.util.Hashing;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticarServiceTest {

    @Mock
    private UsuarioAutenticarRepository repository;

    @InjectMocks
    private UsuarioAutenticarService usuarioAutenticarService;

    private UsuarioAutenticar usuarioAutenticar;
    private UsuarioAutenticarDTO usuarioAutenticarDTO;

    @BeforeEach
    void setUp() {
        usuarioAutenticar = new UsuarioAutenticar();
        usuarioAutenticar.setIdUsuarioAutenticar(1L);
        usuarioAutenticar.setCpf("12345678901");
        usuarioAutenticar.setSenha("$2a$10$hashedPassword");
        usuarioAutenticar.setRole("USUARIO");

        usuarioAutenticarDTO = new UsuarioAutenticarDTO();
        usuarioAutenticarDTO.setCpf("123.456.789-01");
        usuarioAutenticarDTO.setSenha("senhaPlana");
        usuarioAutenticarDTO.setRole("USUARIO");
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String cpf = "12345678901";
        String senha = "senhaCorreta";
        
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));
        
        try (MockedStatic<Hashing> hashingMock = mockStatic(Hashing.class)) {
            hashingMock.when(() -> Hashing.matches(senha, usuarioAutenticar.getSenha()))
                      .thenReturn(true);

            // Act
            boolean result = usuarioAutenticarService.authenticate(cpf, senha);

            // Assert
            assertTrue(result);
            verify(repository, times(1)).findByCpf(cpf);
        }
    }

    @Test
    void testAuthenticate_WrongPassword() {
        // Arrange
        String cpf = "12345678901";
        String senha = "senhaErrada";
        
        when(repository.findByCpf(cpf)).thenReturn(Optional.of(usuarioAutenticar));
        
        try (MockedStatic<Hashing> hashingMock = mockStatic(Hashing.class)) {
            hashingMock.when(() -> Hashing.matches(senha, usuarioAutenticar.getSenha()))
                      .thenReturn(false);

            // Act
            boolean result = usuarioAutenticarService.authenticate(cpf, senha);

            // Assert
            assertFalse(result);
            verify(repository, times(1)).findByCpf(cpf);
        }
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        String cpf = "12345678901";
        String senha = "qualquerSenha";
        
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act
        boolean result = usuarioAutenticarService.authenticate(cpf, senha);

        // Assert
        assertFalse(result);
        verify(repository, times(1)).findByCpf(cpf);
    }

    @Test
    void testAuthenticate_CpfWithMask() {
        // Arrange
        String cpfComMascara = "123.456.789-01";
        String cpfLimpo = "12345678901";
        String senha = "senhaCorreta";
        
        // O authenticate vai limpar o CPF internamente, então devemos mockar para o CPF limpo
        when(repository.findByCpf(cpfLimpo)).thenReturn(Optional.of(usuarioAutenticar));
        
        try (MockedStatic<Hashing> hashingMock = mockStatic(Hashing.class)) {
            hashingMock.when(() -> Hashing.matches(senha, usuarioAutenticar.getSenha()))
                      .thenReturn(true);

            // Act - passa o CPF com máscara, que será limpo internamente
            boolean result = usuarioAutenticarService.authenticate(cpfComMascara, senha);

            // Assert
            assertTrue(result);
            verify(repository, times(1)).findByCpf(cpfLimpo);
        }
    }

    @Test
    void testAuthenticate_NullCpf() {
        // Arrange
        String cpf = null;
        String senha = "qualquerSenha";

        // Act
        boolean result = usuarioAutenticarService.authenticate(cpf, senha);

        // Assert - CPF null resulta em false, não em exceção
        assertFalse(result);
    }

    @Test
    void testAuthenticate_NullSenha() {
        // Arrange
        String cpf = "12345678901";
        String senha = null;
        
        // Act - não precisa mockar nada pois o método retorna false para senha null
        boolean result = usuarioAutenticarService.authenticate(cpf, senha);

        // Assert
        assertFalse(result);
    }

    @Test
    void testCriar_Success() {
        // Arrange
        when(repository.save(any(UsuarioAutenticar.class))).thenReturn(usuarioAutenticar);

        // Act
        UsuarioAutenticar result = usuarioAutenticarService.criar(usuarioAutenticarDTO);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioAutenticar.getCpf(), result.getCpf());
        verify(repository, times(1)).save(any(UsuarioAutenticar.class));
    }

    @Test
    void testCriar_CpfCleaning() {
        // Arrange
        UsuarioAutenticarDTO dtoComCpfMascarado = new UsuarioAutenticarDTO();
        dtoComCpfMascarado.setCpf("123.456.789-01");
        dtoComCpfMascarado.setSenha("senha123");
        dtoComCpfMascarado.setRole("USUARIO");

        when(repository.save(any(UsuarioAutenticar.class))).thenAnswer(invocation -> {
            UsuarioAutenticar saved = invocation.getArgument(0);
            assertEquals("12345678901", saved.getCpf());
            return saved;
        });

        // Act
        UsuarioAutenticar result = usuarioAutenticarService.criar(dtoComCpfMascarado);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(any(UsuarioAutenticar.class));
    }

    @Test
    void testCriar_NullDTO() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            usuarioAutenticarService.criar(null);
        });
    }

    @Test
    void testBuscarPorCpf_Success() {
        // Arrange
        String cpf = "123.456.789-01";
        String cpfLimpo = "12345678901";
        
        when(repository.findByCpf(cpfLimpo)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UsuarioAutenticar result = usuarioAutenticarService.buscarPorCpf(cpf);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioAutenticar.getCpf(), result.getCpf());
        verify(repository, times(1)).findByCpf(cpfLimpo);
    }

    @Test
    void testBuscarPorCpf_NotFound() {
        // Arrange
        String cpf = "12345678901";
        
        when(repository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            usuarioAutenticarService.buscarPorCpf(cpf);
        });
        verify(repository, times(1)).findByCpf(cpf);
    }

    @Test
    void testBuscarPorCpf_CpfCleaning() {
        // Arrange
        String cpfComMascara = "123.456.789-01";
        String cpfLimpo = "12345678901";
        
        when(repository.findByCpf(cpfLimpo)).thenReturn(Optional.of(usuarioAutenticar));

        // Act
        UsuarioAutenticar result = usuarioAutenticarService.buscarPorCpf(cpfComMascara);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findByCpf(cpfLimpo);
    }

    @Test
    void testSalvar_Success() {
        // Arrange
        when(repository.save(usuarioAutenticar)).thenReturn(usuarioAutenticar);

        // Act
        assertDoesNotThrow(() -> {
            usuarioAutenticarService.salvar(usuarioAutenticar);
        });

        // Assert
        verify(repository, times(1)).save(usuarioAutenticar);
    }

    @Test
    void testSalvar_NullEntity() {
        // Act & Assert - o repository pode aceitar null, então testamos o comportamento
        assertDoesNotThrow(() -> {
            usuarioAutenticarService.salvar(null);
        });
        
        verify(repository, times(1)).save(null);
    }

    @Test
    void testDeletar_Success() {
        // Act
        assertDoesNotThrow(() -> {
            usuarioAutenticarService.deletar(usuarioAutenticar);
        });

        // Assert
        verify(repository, times(1)).delete(usuarioAutenticar);
    }

    @Test
    void testDeletar_NullEntity() {
        // Act & Assert - o repository pode aceitar null, então testamos o comportamento
        assertDoesNotThrow(() -> {
            usuarioAutenticarService.deletar(null);
        });
        
        verify(repository, times(1)).delete(null);
    }

    @Test
    void testConstructor() {
        // Arrange & Act
        UsuarioAutenticarService newService = new UsuarioAutenticarService(repository);

        // Assert
        assertNotNull(newService);
    }

    @Test
    void testMultipleOperations() {
        // Arrange
        when(repository.save(any(UsuarioAutenticar.class))).thenReturn(usuarioAutenticar);
        when(repository.findByCpf(anyString())).thenReturn(Optional.of(usuarioAutenticar));

        try (MockedStatic<Hashing> hashingMock = mockStatic(Hashing.class)) {
            hashingMock.when(() -> Hashing.matches(anyString(), anyString()))
                      .thenReturn(true);

            // Act
            UsuarioAutenticar created = usuarioAutenticarService.criar(usuarioAutenticarDTO);
            boolean authenticated = usuarioAutenticarService.authenticate("12345678901", "senha");
            UsuarioAutenticar found = usuarioAutenticarService.buscarPorCpf("12345678901");

            // Assert
            assertNotNull(created);
            assertTrue(authenticated);
            assertNotNull(found);
        }
    }

    @Test
    void testDifferentRoles() {
        // Arrange
        String[] roles = {"USUARIO", "PROFISSIONAL", "ADMIN"};
        
        when(repository.save(any(UsuarioAutenticar.class))).thenReturn(usuarioAutenticar);

        // Act & Assert
        for (String role : roles) {
            UsuarioAutenticarDTO dto = new UsuarioAutenticarDTO();
            dto.setCpf("12345678901");
            dto.setSenha("senha123");
            dto.setRole(role);

            assertDoesNotThrow(() -> {
                usuarioAutenticarService.criar(dto);
            });
        }

        verify(repository, times(roles.length)).save(any(UsuarioAutenticar.class));
    }

    @Test
    void testSpecialCharactersInCpf() {
        // Arrange
        String[] cpfsComCaracteresEspeciais = {
            "123.456.789-01",
            "123 456 789 01",
            "123-456-789-01",
            "123.456.789.01"
        };

        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(usuarioAutenticar));

        // Act & Assert
        for (String cpf : cpfsComCaracteresEspeciais) {
            assertDoesNotThrow(() -> {
                usuarioAutenticarService.buscarPorCpf(cpf);
            });
        }

        verify(repository, times(cpfsComCaracteresEspeciais.length)).findByCpf("12345678901");
    }
} 