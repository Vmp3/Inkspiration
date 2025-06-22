package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;

@DisplayName("Testes de validação de relacionamentos - Agendamento")
public class AgendamentoRelacionamentosTest {

    private Agendamento agendamento;
    private Profissional profissional;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        agendamento = new Agendamento();
        profissional = new Profissional();
        usuario = new Usuario();
    }

    // Testes para Profissional
    @Test
    @DisplayName("Deve aceitar profissional válido")
    void deveAceitarProfissionalValido() {
        agendamento.setProfissional(profissional);
        assertEquals(profissional, agendamento.getProfissional());
    }

    @Test
    @DisplayName("Deve aceitar diferentes instâncias de profissional")
    void deveAceitarDiferentesInstanciasProfissional() {
        Profissional profissional1 = new Profissional();
        Profissional profissional2 = new Profissional();
        
        agendamento.setProfissional(profissional1);
        assertEquals(profissional1, agendamento.getProfissional());
        
        agendamento.setProfissional(profissional2);
        assertEquals(profissional2, agendamento.getProfissional());
    }

    @Test
    @DisplayName("Não deve aceitar profissional nulo")
    void naoDeveAceitarProfissionalNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setProfissional(null);
        });
        assertEquals("Profissional não pode ser nulo", exception.getMessage());
    }

    // Testes para Usuário
    @Test
    @DisplayName("Deve aceitar usuário válido")
    void deveAceitarUsuarioValido() {
        agendamento.setUsuario(usuario);
        assertEquals(usuario, agendamento.getUsuario());
    }

    @Test
    @DisplayName("Deve aceitar diferentes instâncias de usuário")
    void deveAceitarDiferentesInstanciasUsuario() {
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        
        agendamento.setUsuario(usuario1);
        assertEquals(usuario1, agendamento.getUsuario());
        
        agendamento.setUsuario(usuario2);
        assertEquals(usuario2, agendamento.getUsuario());
    }

    @Test
    @DisplayName("Não deve aceitar usuário nulo")
    void naoDeveAceitarUsuarioNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setUsuario(null);
        });
        assertEquals("Usuário não pode ser nulo", exception.getMessage());
    }

    // Testes combinados
    @Test
    @DisplayName("Deve manter ambos relacionamentos")
    void deveManterAmbosRelacionamentos() {
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
    }

    @Test
    @DisplayName("Deve aceitar redefinir profissional mantendo usuário")
    void deveAceitarRedefinirProfissionalMantendoUsuario() {
        Profissional novoProfissional = new Profissional();
        
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        
        agendamento.setProfissional(novoProfissional);
        
        assertEquals(novoProfissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
    }

    @Test
    @DisplayName("Deve aceitar redefinir usuário mantendo profissional")
    void deveAceitarRedefinirUsuarioMantendoProfissional() {
        Usuario novoUsuario = new Usuario();
        
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        
        agendamento.setUsuario(novoUsuario);
        
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(novoUsuario, agendamento.getUsuario());
    }

    @Test
    @DisplayName("Deve manter relacionamentos após definir outros campos")
    void deveManterRelacionamentosAposDefinirOutrosCampos() {
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        agendamento.setDescricao("Tatuagem tribal no braço direito");
        
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
        assertEquals("Tatuagem tribal no braço direito", agendamento.getDescricao());
    }

    @Test
    @DisplayName("Deve aceitar mesmo profissional em diferentes agendamentos")
    void deveAceitarMesmoProfissionalEmDiferentesAgendamentos() {
        Agendamento agendamento1 = new Agendamento();
        Agendamento agendamento2 = new Agendamento();
        
        agendamento1.setProfissional(profissional);
        agendamento2.setProfissional(profissional);
        
        assertEquals(profissional, agendamento1.getProfissional());
        assertEquals(profissional, agendamento2.getProfissional());
        assertSame(agendamento1.getProfissional(), agendamento2.getProfissional());
    }

    @Test
    @DisplayName("Deve aceitar mesmo usuário em diferentes agendamentos")
    void deveAceitarMesmoUsuarioEmDiferentesAgendamentos() {
        Agendamento agendamento1 = new Agendamento();
        Agendamento agendamento2 = new Agendamento();
        
        agendamento1.setUsuario(usuario);
        agendamento2.setUsuario(usuario);
        
        assertEquals(usuario, agendamento1.getUsuario());
        assertEquals(usuario, agendamento2.getUsuario());
        assertSame(agendamento1.getUsuario(), agendamento2.getUsuario());
    }
} 