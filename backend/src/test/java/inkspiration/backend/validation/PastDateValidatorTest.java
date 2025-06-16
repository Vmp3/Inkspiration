package inkspiration.backend.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import inkspiration.backend.validation.PastDateValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PastDateValidator")
public class PastDateValidatorTest {

    private PastDateValidator validator;
    
    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private PastDate pastDate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new PastDateValidator();
        validator.initialize(pastDate);
    }

    @Test
    @DisplayName("Deve validar data nula como válida")
    void deveValidarDataNulaComoValida() {
        // When
        boolean resultado = validator.isValid(null, context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar string vazia como válida")
    void deveValidarStringVaziaComoValida() {
        // When
        boolean resultado = validator.isValid("", context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar string com espaços como válida")
    void deveValidarStringComEspacosComoValida() {
        // When
        boolean resultado = validator.isValid("   ", context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar data passada como válida")
    void deveValidarDataPassadaComoValida() {
        // Given
        LocalDate dataPassada = LocalDate.now().minusDays(1);
        String dataFormatada = dataPassada.format(FORMATTER);

        // When
        boolean resultado = validator.isValid(dataFormatada, context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar data de nascimento típica como válida")
    void deveValidarDataNascimentoTipicaComoValida() {
        // Given
        String dataNascimento = "15/05/1990";

        // When
        boolean resultado = validator.isValid(dataNascimento, context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve invalidar data atual")
    void deveInvalidarDataAtual() {
        // Given
        String dataAtual = LocalDate.now().format(FORMATTER);

        // When
        boolean resultado = validator.isValid(dataAtual, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar data futura")
    void deveInvalidarDataFutura() {
        // Given
        LocalDate dataFutura = LocalDate.now().plusDays(1);
        String dataFormatada = dataFutura.format(FORMATTER);

        // When
        boolean resultado = validator.isValid(dataFormatada, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar formato de data inválido")
    void deveInvalidarFormatoDataInvalido() {
        // Given
        String dataInvalida = "1990-05-15"; // Formato ISO, não dd/MM/yyyy

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar data com formato incorreto")
    void deveInvalidarDataComFormatoIncorreto() {
        // Given
        String dataInvalida = "15/13/1990"; // Mês inválido

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar string não numérica")
    void deveInvalidarStringNaoNumerica() {
        // Given
        String dataInvalida = "abc/def/ghij";

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar data com dia inválido")
    void deveInvalidarDataComDiaInvalido() {
        // Given
        String dataInvalida = "32/01/1990"; // Dia 32 não existe

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve validar data limite (ontem)")
    void deveValidarDataLimiteOntem() {
        // Given
        LocalDate ontem = LocalDate.now().minusDays(1);
        String dataFormatada = ontem.format(FORMATTER);

        // When
        boolean resultado = validator.isValid(dataFormatada, context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve validar data muito antiga")
    void deveValidarDataMuitoAntiga() {
        // Given
        String dataAntiga = "01/01/1900";

        // When
        boolean resultado = validator.isValid(dataAntiga, context);

        // Then
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Deve invalidar formato com barras invertidas")
    void deveInvalidarFormatoComBarrasInvertidas() {
        // Given
        String dataInvalida = "1990\\05\\15";

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar formato com pontos")
    void deveInvalidarFormatoComPontos() {
        // Given
        String dataInvalida = "15.05.1990";

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve invalidar formato com hífens")
    void deveInvalidarFormatoComHifens() {
        // Given
        String dataInvalida = "15-05-1990";

        // When
        boolean resultado = validator.isValid(dataInvalida, context);

        // Then
        assertFalse(resultado);
    }

    @Test
    void testDataPassadaValida() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        String ontemStr = String.format("%02d/%02d/%d", ontem.getDayOfMonth(), ontem.getMonthValue(), ontem.getYear());
        assertTrue(validator.isValid(ontemStr, context), "Data passada deveria ser válida");
    }

    @Test
    void testDataFuturaInvalida() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        String amanhaStr = String.format("%02d/%02d/%d", amanha.getDayOfMonth(), amanha.getMonthValue(), amanha.getYear());
        assertFalse(validator.isValid(amanhaStr, context), "Data futura deveria ser inválida");
    }

    @Test
    void testDataHojeInvalida() {
        LocalDate hoje = LocalDate.now();
        String hojeStr = String.format("%02d/%02d/%d", hoje.getDayOfMonth(), hoje.getMonthValue(), hoje.getYear());
        assertFalse(validator.isValid(hojeStr, context), "Data de hoje deveria ser inválida (deve ser passada)");
    }

    @Test
    void testDataNulaValida() {
        assertTrue(validator.isValid(null, context), "Data nula deveria ser considerada válida (permite nulos)");
    }

    @Test
    void testDataMuitoPassada() {
        String dataAntiga = "01/01/1900";
        assertTrue(validator.isValid(dataAntiga, context), "Data muito passada deveria ser válida");
    }

    @Test
    void testDataOntem() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        String ontemStr = String.format("%02d/%02d/%d", ontem.getDayOfMonth(), ontem.getMonthValue(), ontem.getYear());
        assertTrue(validator.isValid(ontemStr, context), "Ontem deveria ser uma data passada válida");
    }

    @Test
    void testDataAnoPassado() {
        LocalDate anoPassado = LocalDate.now().minusYears(1);
        String anoPassadoStr = String.format("%02d/%02d/%d", anoPassado.getDayOfMonth(), anoPassado.getMonthValue(), anoPassado.getYear());
        assertTrue(validator.isValid(anoPassadoStr, context), "Data do ano passado deveria ser válida");
    }

    @Test
    void testDataMesPassado() {
        LocalDate mesPassado = LocalDate.now().minusMonths(1);
        String mesPassadoStr = String.format("%02d/%02d/%d", mesPassado.getDayOfMonth(), mesPassado.getMonthValue(), mesPassado.getYear());
        assertTrue(validator.isValid(mesPassadoStr, context), "Data do mês passado deveria ser válida");
    }

    @Test
    void testDataProximoAno() {
        LocalDate proximoAno = LocalDate.now().plusYears(1);
        String proximoAnoStr = String.format("%02d/%02d/%d", proximoAno.getDayOfMonth(), proximoAno.getMonthValue(), proximoAno.getYear());
        assertFalse(validator.isValid(proximoAnoStr, context), "Data do próximo ano deveria ser inválida");
    }

    @Test
    void testDataProximoMes() {
        LocalDate proximoMes = LocalDate.now().plusMonths(1);
        String proximoMesStr = String.format("%02d/%02d/%d", proximoMes.getDayOfMonth(), proximoMes.getMonthValue(), proximoMes.getYear());
        assertFalse(validator.isValid(proximoMesStr, context), "Data do próximo mês deveria ser inválida");
    }

    @Test
    void testDataProximaSemana() {
        LocalDate proximaSemana = LocalDate.now().plusWeeks(1);
        String proximaSemanaStr = String.format("%02d/%02d/%d", proximaSemana.getDayOfMonth(), proximaSemana.getMonthValue(), proximaSemana.getYear());
        assertFalse(validator.isValid(proximaSemanaStr, context), "Data da próxima semana deveria ser inválida");
    }

    @Test
    void testDataSemanaPassada() {
        LocalDate semanaPassada = LocalDate.now().minusWeeks(1);
        String semanaPassadaStr = String.format("%02d/%02d/%d", semanaPassada.getDayOfMonth(), semanaPassada.getMonthValue(), semanaPassada.getYear());
        assertTrue(validator.isValid(semanaPassadaStr, context), "Data da semana passada deveria ser válida");
    }

    @Test
    void testLimitesTemporais() {
        LocalDate agora = LocalDate.now();
        
        // Testa momentos específicos
        String hojeStr = String.format("%02d/%02d/%d", agora.getDayOfMonth(), agora.getMonthValue(), agora.getYear());
        String ontemStr = String.format("%02d/%02d/%d", agora.minusDays(1).getDayOfMonth(), agora.minusDays(1).getMonthValue(), agora.minusDays(1).getYear());
        String amanhaStr = String.format("%02d/%02d/%d", agora.plusDays(1).getDayOfMonth(), agora.plusDays(1).getMonthValue(), agora.plusDays(1).getYear());
        
        assertFalse(validator.isValid(hojeStr, context), "Hoje não deveria ser válido");
        assertTrue(validator.isValid(ontemStr, context), "Ontem deveria ser válido");
        assertFalse(validator.isValid(amanhaStr, context), "Amanhã não deveria ser válido");
    }

    @Test
    void testDataNascimentoTipica() {
        String nascimento1990 = "15/06/1990";
        String nascimento2000 = "25/12/2000";
        String nascimento2010 = "10/03/2010";
        
        assertTrue(validator.isValid(nascimento1990, context), "Data de nascimento de 1990 deveria ser válida");
        assertTrue(validator.isValid(nascimento2000, context), "Data de nascimento de 2000 deveria ser válida");
        assertTrue(validator.isValid(nascimento2010, context), "Data de nascimento de 2010 deveria ser válida");
    }

    @Test
    void testDataNascimentoFutura() {
        LocalDate nascimentoFuturo = LocalDate.now().plusYears(1);
        String nascimentoFuturoStr = String.format("%02d/%02d/%d", nascimentoFuturo.getDayOfMonth(), nascimentoFuturo.getMonthValue(), nascimentoFuturo.getYear());
        assertFalse(validator.isValid(nascimentoFuturoStr, context), "Data de nascimento futura deveria ser inválida");
    }

    @Test
    void testDataHistorica() {
        String[] datasHistoricas = {
            "22/04/1500", // Descobrimento do Brasil
            "07/09/1822", // Independência do Brasil
            "15/11/1889", // Proclamação da República
            "08/05/1945", // Fim da Segunda Guerra
            "01/01/2000"  // Virada do milênio
        };

        for (String data : datasHistoricas) {
            assertTrue(validator.isValid(data, context), 
                      "Data histórica deveria ser válida: " + data);
        }
    }

    @Test
    void testComparacaoComHoje() {
        LocalDate hoje = LocalDate.now();
        String hojeStr = String.format("%02d/%02d/%d", hoje.getDayOfMonth(), hoje.getMonthValue(), hoje.getYear());
        String ontemStr = String.format("%02d/%02d/%d", hoje.minusDays(1).getDayOfMonth(), hoje.minusDays(1).getMonthValue(), hoje.minusDays(1).getYear());
        String amanhaStr = String.format("%02d/%02d/%d", hoje.plusDays(1).getDayOfMonth(), hoje.plusDays(1).getMonthValue(), hoje.plusDays(1).getYear());
        
        assertTrue(validator.isValid(ontemStr, context), "Um dia antes deveria ser válido");
        assertFalse(validator.isValid(hojeStr, context), "Hoje não deveria ser válido");
        assertFalse(validator.isValid(amanhaStr, context), "Um dia depois não deveria ser válido");
    }

    @Test
    void testContextoNaoUtilizado() {
        // Verifica que o contexto pode ser nulo sem problemas
        LocalDate ontem = LocalDate.now().minusDays(1);
        String ontemStr = String.format("%02d/%02d/%d", ontem.getDayOfMonth(), ontem.getMonthValue(), ontem.getYear());
        assertTrue(validator.isValid(ontemStr, null), "Deveria funcionar mesmo com contexto nulo");
    }

    @Test
    void testInicializacao() {
        // Testa se o validador pode ser inicializado sem problemas
        PastDateValidator novoValidator = new PastDateValidator();
        LocalDate ontem = LocalDate.now().minusDays(1);
        String ontemStr = String.format("%02d/%02d/%d", ontem.getDayOfMonth(), ontem.getMonthValue(), ontem.getYear());
        assertTrue(novoValidator.isValid(ontemStr, context), "Novo validador deveria funcionar");
    }

    @Test
    void testMultiplasValidacoes() {
        LocalDate[] datasPassadas = {
            LocalDate.now().minusDays(1),
            LocalDate.now().minusWeeks(1),
            LocalDate.now().minusMonths(1),
            LocalDate.now().minusYears(1)
        };

        LocalDate[] datasFuturas = {
            LocalDate.now().plusDays(1),
            LocalDate.now().plusWeeks(1),
            LocalDate.now().plusMonths(1),
            LocalDate.now().plusYears(1)
        };

        for (LocalDate data : datasPassadas) {
            String dataStr = String.format("%02d/%02d/%d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
            assertTrue(validator.isValid(dataStr, context), 
                      "Data passada deveria ser válida: " + dataStr);
        }

        for (LocalDate data : datasFuturas) {
            String dataStr = String.format("%02d/%02d/%d", data.getDayOfMonth(), data.getMonthValue(), data.getYear());
            assertFalse(validator.isValid(dataStr, context), 
                       "Data futura deveria ser inválida: " + dataStr);
        }
    }

    @Test
    void testAnosBissextos() {
        String bissexto2020 = "29/02/2020";
        String bissexto2024 = "29/02/2024";
        
        assertTrue(validator.isValid(bissexto2020, context), 
                  "Data de ano bissexto passado deveria ser válida");
        
        // Se 2024 já passou
        if (LocalDate.now().isAfter(LocalDate.of(2024, 2, 29))) {
            assertTrue(validator.isValid(bissexto2024, context), 
                      "Data de ano bissexto passado deveria ser válida");
        } else {
            assertFalse(validator.isValid(bissexto2024, context), 
                       "Data de ano bissexto futuro deveria ser inválida");
        }
    }

    @Test
    void testDataInvalida() {
        String[] datasInvalidas = {
            "32/12/2020", // dia inválido
            "01/13/2020", // mês inválido
            "abc/def/ghij", // formato inválido
            "",
            "   "
        };

        for (String data : datasInvalidas) {
            if (data != null && !data.trim().isEmpty()) {
                assertFalse(validator.isValid(data, context), 
                           "Data inválida deveria ser rejeitada: " + data);
            } else {
                // Strings vazias ou nulas podem ser aceitas pelo validador
                boolean result = validator.isValid(data, context);
                // Não fazemos assert aqui pois é comportamento específico do validador
            }
        }
    }
} 