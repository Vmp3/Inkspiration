package inkspiration.backend.entities.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;

@DisplayName("Testes de validação de profissional - Disponibilidade")
public class DisponibilidadeProfissionalTest {

    private Disponibilidade disponibilidade;
    private Profissional profissional;

    @BeforeEach
    void setUp() {
        disponibilidade = new Disponibilidade();
        profissional = new Profissional();
    }

    @Test
    @DisplayName("Deve aceitar profissional válido")
    void deveAceitarProfissionalValido() {
        disponibilidade.setProfissional(profissional);
        assertEquals(profissional, disponibilidade.getProfissional());
    }

    @Test
    @DisplayName("Deve aceitar diferentes instâncias de profissional")
    void deveAceitarDiferentesInstanciasDeProfissional() {
        Profissional profissional1 = new Profissional();
        Profissional profissional2 = new Profissional();
        
        disponibilidade.setProfissional(profissional1);
        assertEquals(profissional1, disponibilidade.getProfissional());
        
        disponibilidade.setProfissional(profissional2);
        assertEquals(profissional2, disponibilidade.getProfissional());
    }

    @Test
    @DisplayName("Não deve aceitar profissional nulo")
    void naoDeveAceitarProfissionalNulo() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            disponibilidade.setProfissional(null);
        });
        assertEquals("O profissional não pode ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve manter profissional após definir horários")
    void deveManterProfissionalAposDefinirHorarios() {
        disponibilidade.setProfissional(profissional);
        disponibilidade.setHrAtendimento("08:00-18:00");
        
        assertEquals(profissional, disponibilidade.getProfissional());
        assertEquals("08:00-18:00", disponibilidade.getHrAtendimento());
    }

    @Test
    @DisplayName("Deve aceitar redefinir profissional")
    void deveAceitarRedefinirProfissional() {
        Profissional profissionalOriginal = new Profissional();
        Profissional novoProfissional = new Profissional();
        
        disponibilidade.setProfissional(profissionalOriginal);
        assertEquals(profissionalOriginal, disponibilidade.getProfissional());
        
        disponibilidade.setProfissional(novoProfissional);
        assertEquals(novoProfissional, disponibilidade.getProfissional());
    }
} 