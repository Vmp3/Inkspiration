package inkspiration.backend.entities.profissional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portifolio;
import java.math.BigDecimal;

public class ProfissionalTest {

    private Profissional profissional;
    private Usuario usuario;
    private Endereco endereco;
    private Portifolio portifolio;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
        usuario = new Usuario();
        endereco = new Endereco();
        portifolio = new Portifolio();
    }

    @Test
    void testGettersAndSettersIdProfissional() {
        Long id = 1L;
        profissional.setIdProfissional(id);
        assertEquals(id, profissional.getIdProfissional(), "ID do profissional deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersUsuario() {
        profissional.setUsuario(usuario);
        assertEquals(usuario, profissional.getUsuario(), "Usuário deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersEndereco() {
        profissional.setEndereco(endereco);
        assertEquals(endereco, profissional.getEndereco(), "Endereço deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersPortifolio() {
        profissional.setPortifolio(portifolio);
        assertEquals(portifolio, profissional.getPortifolio(), "Portifólio deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersNota() {
        BigDecimal nota = new BigDecimal("4.5");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota(), "Nota deve ser igual à definida");
    }

    @Test
    void testConstrutorPadrao() {
        Profissional profissionalVazio = new Profissional();
        
        assertNull(profissionalVazio.getIdProfissional(), "ID deve ser nulo inicialmente");
        assertNull(profissionalVazio.getUsuario(), "Usuário deve ser nulo inicialmente");
        assertNull(profissionalVazio.getEndereco(), "Endereço deve ser nulo inicialmente");
        assertNull(profissionalVazio.getPortifolio(), "Portifólio deve ser nulo inicialmente");
        assertNull(profissionalVazio.getNota(), "Nota deve ser nula inicialmente");
    }

    @Test
    void testProfissionalComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        BigDecimal nota = new BigDecimal("4.8");

        // Act
        profissional.setIdProfissional(id);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortifolio(portifolio);
        profissional.setNota(nota);

        // Assert
        assertEquals(id, profissional.getIdProfissional());
        assertEquals(usuario, profissional.getUsuario());
        assertEquals(endereco, profissional.getEndereco());
        assertEquals(portifolio, profissional.getPortifolio());
        assertEquals(nota, profissional.getNota());
    }

    @Test
    void testNotasValidas() {
        BigDecimal[] notasValidas = {
            new BigDecimal("0.0"),
            new BigDecimal("1.0"),
            new BigDecimal("2.5"),
            new BigDecimal("3.7"),
            new BigDecimal("4.2"),
            new BigDecimal("5.0")
        };

        for (BigDecimal nota : notasValidas) {
            assertDoesNotThrow(() -> {
                profissional.setNota(nota);
                assertEquals(nota, profissional.getNota());
            }, "Deve aceitar nota válida: " + nota);
        }
    }

    @Test
    void testNotaComPrecisao() {
        BigDecimal notaPrecisa = new BigDecimal("4.73");
        profissional.setNota(notaPrecisa);
        assertEquals(notaPrecisa, profissional.getNota(), "Deve manter precisão da nota");
    }

    @Test
    void testNotaZero() {
        BigDecimal notaZero = BigDecimal.ZERO;
        profissional.setNota(notaZero);
        assertEquals(notaZero, profissional.getNota(), "Deve aceitar nota zero");
    }

    @Test
    void testNotaMaxima() {
        BigDecimal notaMaxima = new BigDecimal("5.0");
        profissional.setNota(notaMaxima);
        assertEquals(notaMaxima, profissional.getNota(), "Deve aceitar nota máxima");
    }

    @Test
    void testNotaNula() {
        profissional.setNota(null);
        assertNull(profissional.getNota(), "Deve aceitar nota nula");
    }

    @Test
    void testUsuarioNulo() {
        profissional.setUsuario(null);
        assertNull(profissional.getUsuario(), "Deve aceitar usuário nulo");
    }

    @Test
    void testEnderecoNulo() {
        profissional.setEndereco(null);
        assertNull(profissional.getEndereco(), "Deve aceitar endereço nulo");
    }

    @Test
    void testPortifolioNulo() {
        profissional.setPortifolio(null);
        assertNull(profissional.getPortifolio(), "Deve aceitar portifólio nulo");
    }

    @Test
    void testRelacionamentoComUsuario() {
        usuario.setNome("João Silva");
        profissional.setUsuario(usuario);
        
        assertNotNull(profissional.getUsuario(), "Usuário deve estar definido no profissional");
        assertEquals("João Silva", profissional.getUsuario().getNome(), "Nome do usuário deve estar correto");
    }

    @Test
    void testRelacionamentoComEndereco() {
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        profissional.setEndereco(endereco);
        
        assertNotNull(profissional.getEndereco(), "Endereço deve estar definido no profissional");
        assertEquals("São Paulo", profissional.getEndereco().getCidade(), "Cidade do endereço deve estar correta");
        assertEquals("SP", profissional.getEndereco().getEstado(), "Estado do endereço deve estar correto");
    }

    @Test
    void testRelacionamentoComPortifolio() {
        portifolio.setDescricao("Artista especializado em tatuagens realistas");
        profissional.setPortifolio(portifolio);
        
        assertNotNull(profissional.getPortifolio(), "Portifólio deve estar definido no profissional");
        assertEquals("Artista especializado em tatuagens realistas", 
                    profissional.getPortifolio().getDescricao(), 
                    "Descrição do portifólio deve estar correta");
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        profissional.setIdProfissional(idMaximo);
        assertEquals(idMaximo, profissional.getIdProfissional(), "Deve aceitar ID máximo");
        
        profissional.setIdProfissional(idMinimo);
        assertEquals(idMinimo, profissional.getIdProfissional(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testNotaComUmaCasaDecimal() {
        BigDecimal nota = new BigDecimal("4.5");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota(), "Deve aceitar nota com uma casa decimal");
    }

    @Test
    void testNotaInteira() {
        BigDecimal nota = new BigDecimal("4");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota(), "Deve aceitar nota inteira");
    }

    @Test
    void testNotaComMultiplasCasasDecimais() {
        BigDecimal nota = new BigDecimal("4.567");
        profissional.setNota(nota);
        assertEquals(nota, profissional.getNota(), "Deve aceitar nota com múltiplas casas decimais");
    }

    @Test
    void testProfissionalCompleto() {
        // Configurando usuário
        usuario.setNome("Maria Silva");
        usuario.setEmail("maria@exemplo.com");
        usuario.setCpf("12345678901");
        
        // Configurando endereço
        endereco.setCep("01234-567");
        endereco.setRua("Rua das Flores");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        
        // Configurando portifólio
        portifolio.setDescricao("Especialista em tatuagens femininas delicadas");
        portifolio.setExperiencia("8 anos de experiência");
        
        // Configurando profissional
        profissional.setIdProfissional(1L);
        profissional.setUsuario(usuario);
        profissional.setEndereco(endereco);
        profissional.setPortifolio(portifolio);
        profissional.setNota(new BigDecimal("4.9"));

        // Validações
        assertAll("Profissional completo",
            () -> assertEquals(1L, profissional.getIdProfissional()),
            () -> assertEquals("Maria Silva", profissional.getUsuario().getNome()),
            () -> assertEquals("maria@exemplo.com", profissional.getUsuario().getEmail()),
            () -> assertEquals("12345678901", profissional.getUsuario().getCpf()),
            () -> assertEquals("01234-567", profissional.getEndereco().getCep()),
            () -> assertEquals("São Paulo", profissional.getEndereco().getCidade()),
            () -> assertEquals("SP", profissional.getEndereco().getEstado()),
            () -> assertEquals("Especialista em tatuagens femininas delicadas", profissional.getPortifolio().getDescricao()),
            () -> assertEquals("8 anos de experiência", profissional.getPortifolio().getExperiencia()),
            () -> assertEquals(new BigDecimal("4.9"), profissional.getNota())
        );
    }

    @Test
    void testNotasComparacoes() {
        BigDecimal nota1 = new BigDecimal("4.5");
        BigDecimal nota2 = new BigDecimal("4.5");
        BigDecimal nota3 = new BigDecimal("3.8");

        profissional.setNota(nota1);
        assertEquals(0, profissional.getNota().compareTo(nota2), "Notas iguais devem ser comparadas como iguais");
        assertTrue(profissional.getNota().compareTo(nota3) > 0, "Nota maior deve ser comparada como maior");
    }

    @Test
    void testRelacionamentosMultiplos() {
        // Criando múltiplos profissionais com o mesmo endereço (relacionamento ManyToOne)
        Profissional profissional2 = new Profissional();
        
        profissional.setEndereco(endereco);
        profissional2.setEndereco(endereco);
        
        assertSame(endereco, profissional.getEndereco(), "Primeiro profissional deve referenciar o endereço");
        assertSame(endereco, profissional2.getEndereco(), "Segundo profissional deve referenciar o mesmo endereço");
        assertSame(profissional.getEndereco(), profissional2.getEndereco(), "Ambos profissionais devem referenciar o mesmo endereço");
    }

    @Test
    void testEscalaNota() {
        // Testando diferentes representações da mesma nota
        BigDecimal nota1 = new BigDecimal("4.5");
        BigDecimal nota2 = new BigDecimal("4.50");
        
        profissional.setNota(nota1);
        
        // Mesmo valor, mas escalas diferentes
        assertEquals(0, profissional.getNota().compareTo(nota2), "Notas com escalas diferentes mas mesmo valor devem ser iguais");
    }

    @Test
    void testNotaComOperacoes() {
        BigDecimal notaInicial = new BigDecimal("4.0");
        BigDecimal incremento = new BigDecimal("0.5");
        
        profissional.setNota(notaInicial);
        BigDecimal novaNota = profissional.getNota().add(incremento);
        profissional.setNota(novaNota);
        
        assertEquals(new BigDecimal("4.5"), profissional.getNota(), "Deve aceitar resultado de operações com BigDecimal");
    }

    @Test
    void testRelacionamentoBidirecionalPortifolio() {
        portifolio.setProfissional(profissional);
        profissional.setPortifolio(portifolio);
        
        assertSame(profissional, portifolio.getProfissional(), "Portifólio deve referenciar o profissional");
        assertSame(portifolio, profissional.getPortifolio(), "Profissional deve referenciar o portifólio");
    }
} 