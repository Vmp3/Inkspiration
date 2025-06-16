package inkspiration.backend.entities.disponibilidade;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;

public class DisponibilidadeTest {

    private Disponibilidade disponibilidade;
    private Profissional profissional;

    @BeforeEach
    void setUp() {
        disponibilidade = new Disponibilidade();
        profissional = new Profissional();
    }

    @Test
    void testGettersAndSettersIdDisponibilidade() {
        Long id = 1L;
        disponibilidade.setIdDisponibilidade(id);
        assertEquals(id, disponibilidade.getIdDisponibilidade(), "ID da disponibilidade deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersHrAtendimento() {
        String hrAtendimento = "08:00-18:00|Segunda a Sexta";
        disponibilidade.setHrAtendimento(hrAtendimento);
        assertEquals(hrAtendimento, disponibilidade.getHrAtendimento(), "Horário de atendimento deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersProfissional() {
        disponibilidade.setProfissional(profissional);
        assertEquals(profissional, disponibilidade.getProfissional(), "Profissional deve ser igual ao definido");
    }

    @Test
    void testConstrutorPadrao() {
        Disponibilidade disponibilidadeVazia = new Disponibilidade();
        
        assertNull(disponibilidadeVazia.getIdDisponibilidade(), "ID deve ser nulo inicialmente");
        assertNull(disponibilidadeVazia.getHrAtendimento(), "Horário de atendimento deve ser nulo inicialmente");
        assertNull(disponibilidadeVazia.getProfissional(), "Profissional deve ser nulo inicialmente");
    }

    @Test
    void testDisponibilidadeComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String hrAtendimento = "08:00-18:00|Segunda a Sexta";

        // Act
        disponibilidade.setIdDisponibilidade(id);
        disponibilidade.setHrAtendimento(hrAtendimento);
        disponibilidade.setProfissional(profissional);

        // Assert
        assertEquals(id, disponibilidade.getIdDisponibilidade());
        assertEquals(hrAtendimento, disponibilidade.getHrAtendimento());
        assertEquals(profissional, disponibilidade.getProfissional());
    }

    @Test
    void testHorarioAtendimentoComplexo() {
        String horarioComplexo = "Segunda: 08:00-12:00, 14:00-18:00|Terça: 09:00-17:00|Quarta: Fechado|Quinta: 08:00-16:00|Sexta: 10:00-14:00|Sábado: 08:00-12:00|Domingo: Fechado";
        
        disponibilidade.setHrAtendimento(horarioComplexo);
        assertEquals(horarioComplexo, disponibilidade.getHrAtendimento(), "Deve aceitar horário complexo");
    }

    @Test
    void testHorarioAtendimentoJSON() {
        String horarioJSON = "{\"segunda\":[\"08:00-12:00\",\"14:00-18:00\"],\"terca\":[\"09:00-17:00\"],\"quarta\":[],\"quinta\":[\"08:00-16:00\"],\"sexta\":[\"10:00-14:00\"],\"sabado\":[\"08:00-12:00\"],\"domingo\":[]}";
        
        disponibilidade.setHrAtendimento(horarioJSON);
        assertEquals(horarioJSON, disponibilidade.getHrAtendimento(), "Deve aceitar horário em formato JSON");
    }

    @Test
    void testHorarioAtendimentoVazio() {
        disponibilidade.setHrAtendimento("");
        assertEquals("", disponibilidade.getHrAtendimento(), "Deve aceitar string vazia");
        
        disponibilidade.setHrAtendimento(null);
        assertNull(disponibilidade.getHrAtendimento(), "Deve aceitar valor nulo");
    }

    @Test
    void testHorarioAtendimentoTextoLongo() {
        String textoLongo = "a".repeat(5000); // Simula texto muito longo
        
        assertDoesNotThrow(() -> {
            disponibilidade.setHrAtendimento(textoLongo);
        }, "Deve aceitar texto longo sem lançar exceção");
        
        assertEquals(textoLongo, disponibilidade.getHrAtendimento(), "Deve armazenar texto longo corretamente");
    }

    @Test
    void testProfissionalNulo() {
        disponibilidade.setProfissional(null);
        assertNull(disponibilidade.getProfissional(), "Deve aceitar profissional nulo");
    }

    @Test
    void testRelacionamentoBidirecional() {
        // Testando se o relacionamento funciona
        profissional.setIdProfissional(1L);
        disponibilidade.setProfissional(profissional);
        
        assertNotNull(disponibilidade.getProfissional(), "Profissional deve estar definido na disponibilidade");
        assertEquals(1L, disponibilidade.getProfissional().getIdProfissional(), "ID do profissional deve estar correto");
    }

    @Test
    void testHorarioAtendimentoFormatos() {
        String[] formatosHorario = {
            "08:00-18:00",
            "8h às 18h",
            "08:00 - 18:00",
            "Manhã: 08:00-12:00, Tarde: 14:00-18:00",
            "24 horas",
            "Sob agendamento",
            "Flexible schedule - contact for appointment"
        };

        for (String formato : formatosHorario) {
            assertDoesNotThrow(() -> {
                disponibilidade.setHrAtendimento(formato);
                assertEquals(formato, disponibilidade.getHrAtendimento());
            }, "Deve aceitar formato de horário: " + formato);
        }
    }

    @Test
    void testHorarioAtendimentoCaracteresEspeciais() {
        String horarioComEspeciais = "Horário: 08:00-18:00 (UTC-3) - Segunda à Sexta | Sábado: 08:00-12:00 | Feriados: Fechado ⏰";
        
        disponibilidade.setHrAtendimento(horarioComEspeciais);
        assertEquals(horarioComEspeciais, disponibilidade.getHrAtendimento(), "Deve aceitar caracteres especiais");
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        disponibilidade.setIdDisponibilidade(idMaximo);
        assertEquals(idMaximo, disponibilidade.getIdDisponibilidade(), "Deve aceitar ID máximo");
        
        disponibilidade.setIdDisponibilidade(idMinimo);
        assertEquals(idMinimo, disponibilidade.getIdDisponibilidade(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testHorarioAtendimentoMultiLinha() {
        String horarioMultiLinha = "Segunda-feira: 08:00 às 12:00 e 14:00 às 18:00\n" +
                                   "Terça-feira: 09:00 às 17:00\n" +
                                   "Quarta-feira: Fechado\n" +
                                   "Quinta-feira: 08:00 às 16:00\n" +
                                   "Sexta-feira: 10:00 às 14:00\n" +
                                   "Sábado: 08:00 às 12:00\n" +
                                   "Domingo: Fechado";
        
        disponibilidade.setHrAtendimento(horarioMultiLinha);
        assertEquals(horarioMultiLinha, disponibilidade.getHrAtendimento(), "Deve aceitar horário multi-linha");
    }

    @Test
    void testHorarioAtendimentoXML() {
        String horarioXML = "<horarios><dia nome=\"segunda\"><periodo inicio=\"08:00\" fim=\"12:00\"/><periodo inicio=\"14:00\" fim=\"18:00\"/></dia></horarios>";
        
        disponibilidade.setHrAtendimento(horarioXML);
        assertEquals(horarioXML, disponibilidade.getHrAtendimento(), "Deve aceitar horário em formato XML");
    }

    @Test
    void testHorarioAtendimentoComEmoji() {
        String horarioComEmoji = "🕐 08:00-18:00 Segunda a Sexta 📅 | 🕙 08:00-12:00 Sábado 🌅 | ❌ Domingo Fechado";
        
        disponibilidade.setHrAtendimento(horarioComEmoji);
        assertEquals(horarioComEmoji, disponibilidade.getHrAtendimento(), "Deve aceitar emojis no horário");
    }

    @Test
    void testHorarioAtendimentoAcentos() {
        String horarioComAcentos = "Segunda à Sexta: 08:00 às 18:00 | Sábado: 08:00 às 12:00 | Não atendemos em feriados nacionais";
        
        disponibilidade.setHrAtendimento(horarioComAcentos);
        assertEquals(horarioComAcentos, disponibilidade.getHrAtendimento(), "Deve aceitar acentos e caracteres especiais");
    }

    @Test
    void testHorarioAtendimentoComNumeros() {
        String horarioComNumeros = "Plantão 24/7 - Tel: (11) 99999-9999 | WhatsApp: (11) 88888-8888";
        
        disponibilidade.setHrAtendimento(horarioComNumeros);
        assertEquals(horarioComNumeros, disponibilidade.getHrAtendimento(), "Deve aceitar números no horário");
    }
} 