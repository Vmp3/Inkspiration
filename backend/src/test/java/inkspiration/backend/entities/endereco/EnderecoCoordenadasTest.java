package inkspiration.backend.entities.endereco;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;

@DisplayName("Testes de validação de coordenadas - Endereco")
public class EnderecoCoordenadasTest {

    private Endereco endereco;

    @BeforeEach
    void setUp() {
        endereco = new Endereco();
    }

    // Testes para Latitude
    @Test
    @DisplayName("Deve aceitar latitude válida zero")
    void deveAceitarLatitudeValidaZero() {
        endereco.setLatitude(0.0);
        assertEquals(0.0, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude válida positiva")
    void deveAceitarLatitudeValidaPositiva() {
        endereco.setLatitude(45.5);
        assertEquals(45.5, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude válida negativa")
    void deveAceitarLatitudeValidaNegativa() {
        endereco.setLatitude(-45.5);
        assertEquals(-45.5, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude máxima 90")
    void deveAceitarLatitudeMaxima90() {
        endereco.setLatitude(90.0);
        assertEquals(90.0, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude mínima -90")
    void deveAceitarLatitudeMinimaMenos90() {
        endereco.setLatitude(-90.0);
        assertEquals(-90.0, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude do Brasil (São Paulo)")
    void deveAceitarLatitudeDoBrasilSaoPaulo() {
        endereco.setLatitude(-23.5505);
        assertEquals(-23.5505, endereco.getLatitude());
    }

    @Test
    @DisplayName("Deve aceitar latitude nula")
    void deveAceitarLatitudeNula() {
        endereco.setLatitude(null);
        assertNull(endereco.getLatitude());
    }

    @Test
    @DisplayName("Não deve aceitar latitude maior que 90")
    void naoDeveAceitarLatitudeMaiorQue90() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLatitude(90.1);
        });
        assertEquals("Latitude deve ser entre -90 e 90", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar latitude menor que -90")
    void naoDeveAceitarLatitudeMenorQueMenos90() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLatitude(-90.1);
        });
        assertEquals("Latitude deve ser entre -90 e 90", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar latitude muito maior que 90")
    void naoDeveAceitarLatitudeMuitoMaiorQue90() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLatitude(180.0);
        });
        assertEquals("Latitude deve ser entre -90 e 90", exception.getMessage());
    }

    // Testes para Longitude
    @Test
    @DisplayName("Deve aceitar longitude válida zero")
    void deveAceitarLongitudeValidaZero() {
        endereco.setLongitude(0.0);
        assertEquals(0.0, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude válida positiva")
    void deveAceitarLongitudeValidaPositiva() {
        endereco.setLongitude(123.45);
        assertEquals(123.45, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude válida negativa")
    void deveAceitarLongitudeValidaNegativa() {
        endereco.setLongitude(-123.45);
        assertEquals(-123.45, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude máxima 180")
    void deveAceitarLongitudeMaxima180() {
        endereco.setLongitude(180.0);
        assertEquals(180.0, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude mínima -180")
    void deveAceitarLongitudeMinimaMenos180() {
        endereco.setLongitude(-180.0);
        assertEquals(-180.0, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude do Brasil (São Paulo)")
    void deveAceitarLongitudeDoBrasilSaoPaulo() {
        endereco.setLongitude(-46.6333);
        assertEquals(-46.6333, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar longitude nula")
    void deveAceitarLongitudeNula() {
        endereco.setLongitude(null);
        assertNull(endereco.getLongitude());
    }

    @Test
    @DisplayName("Não deve aceitar longitude maior que 180")
    void naoDeveAceitarLongitudeMaiorQue180() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLongitude(180.1);
        });
        assertEquals("Longitude deve ser entre -180 e 180", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar longitude menor que -180")
    void naoDeveAceitarLongitudeMenorQueMenos180() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLongitude(-180.1);
        });
        assertEquals("Longitude deve ser entre -180 e 180", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve aceitar longitude muito maior que 180")
    void naoDeveAceitarLongitudeMuitoMaiorQue180() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            endereco.setLongitude(360.0);
        });
        assertEquals("Longitude deve ser entre -180 e 180", exception.getMessage());
    }

    // Testes combinados
    @Test
    @DisplayName("Deve aceitar coordenadas válidas do Brasil")
    void deveAceitarCoordenadasValidasDoBrasil() {
        endereco.setLatitude(-23.5505); // São Paulo
        endereco.setLongitude(-46.6333); // São Paulo
        
        assertEquals(-23.5505, endereco.getLatitude());
        assertEquals(-46.6333, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar uma coordenada nula e outra válida")
    void deveAceitarUmaCoordenadaNulaEOutraValida() {
        endereco.setLatitude(null);
        endereco.setLongitude(-46.6333);
        
        assertNull(endereco.getLatitude());
        assertEquals(-46.6333, endereco.getLongitude());
    }

    @Test
    @DisplayName("Deve aceitar ambas coordenadas nulas")
    void deveAceitarAmbasCoordenadasNulas() {
        endereco.setLatitude(null);
        endereco.setLongitude(null);
        
        assertNull(endereco.getLatitude());
        assertNull(endereco.getLongitude());
    }
} 