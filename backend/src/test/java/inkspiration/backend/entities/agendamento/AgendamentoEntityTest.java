package inkspiration.backend.entities.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.enums.TipoServico;

@DisplayName("Testes gerais da entidade Agendamento")
public class AgendamentoEntityTest {

    @Test
    @DisplayName("Deve criar agendamento com construtor padrão")
    void deveCriarAgendamentoComConstrutorPadrao() {
        Agendamento agendamento = new Agendamento();
        
        assertNotNull(agendamento);
        assertNull(agendamento.getIdAgendamento());
        assertNull(agendamento.getTipoServico());
        assertNull(agendamento.getDescricao());
        assertNull(agendamento.getDtInicio());
        assertNull(agendamento.getDtFim());
        assertNull(agendamento.getValor());
        assertNull(agendamento.getProfissional());
        assertNull(agendamento.getUsuario());
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve definir e obter ID do agendamento")
    void deveDefinirEObterIdDoAgendamento() {
        Agendamento agendamento = new Agendamento();
        Long id = 789L;
        
        agendamento.setIdAgendamento(id);
        assertEquals(id, agendamento.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        Agendamento agendamento = new Agendamento();
        
        agendamento.setIdAgendamento(null);
        assertNull(agendamento.getIdAgendamento());
    }

    @Test
    @DisplayName("Deve criar agendamento completo válido")
    void deveCriarAgendamentoCompletoValido() {
        Agendamento agendamento = new Agendamento();
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusHours(3);
        BigDecimal valor = new BigDecimal("250.00");
        Profissional profissional = new Profissional();
        Usuario usuario = new Usuario();
        
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao("Tatuagem pequena no pulso");
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        agendamento.setValor(valor);
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        
        assertEquals(TipoServico.TATUAGEM_PEQUENA, agendamento.getTipoServico());
        assertEquals("Tatuagem pequena no pulso", agendamento.getDescricao());
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
        assertEquals(valor, agendamento.getValor());
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve criar agendamento mínimo válido")
    void deveCriarAgendamentoMinimoValido() {
        Agendamento agendamento = new Agendamento();
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusHours(1);
        Profissional profissional = new Profissional();
        Usuario usuario = new Usuario();
        
        agendamento.setTipoServico(TipoServico.TATUAGEM_PEQUENA);
        agendamento.setDescricao("Tatuagem pequena");
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        
        assertEquals(TipoServico.TATUAGEM_PEQUENA, agendamento.getTipoServico());
        assertEquals("Tatuagem pequena", agendamento.getDescricao());
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
        assertNull(agendamento.getValor()); // Valor é opcional
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus()); // Status padrão
    }

    @Test
    @DisplayName("Campos obrigatórios devem ser validados")
    void camposObrigatoriosDevemSerValidados() {
        Agendamento agendamento = new Agendamento();
        
        // Tipo de serviço é obrigatório
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setTipoServico(null);
        });
        
        // Descrição é obrigatória
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDescricao(null);
        });
        
        // Data de início é obrigatória
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtInicio(null);
        });
        
        // Data de fim é obrigatória
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtFim(null);
        });
        
        // Profissional é obrigatório
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setProfissional(null);
        });
        
        // Usuário é obrigatório
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setUsuario(null);
        });
        
        // Status é obrigatório
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setStatus(null);
        });
    }

    @Test
    @DisplayName("Campos opcionais devem ser aceitos como nulos")
    void camposOpcionaisDevemSerAceitosComoNulos() {
        Agendamento agendamento = new Agendamento();
        
        // ID pode ser nulo
        agendamento.setIdAgendamento(null);
        assertNull(agendamento.getIdAgendamento());
        
        // Valor pode ser nulo
        agendamento.setValor(null);
        assertNull(agendamento.getValor());
    }

    @Test
    @DisplayName("Deve manter integridade entre todos os campos")
    void deveManterIntegridadeEntreTodosOsCampos() {
        Agendamento agendamento = new Agendamento();
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        LocalDateTime fim = inicio.plusHours(2);
        BigDecimal valor = new BigDecimal("150.00");
        Profissional profissional = new Profissional();
        Usuario usuario = new Usuario();
        
        // Define todos os campos
        agendamento.setTipoServico(TipoServico.TATUAGEM_MEDIA);
        agendamento.setDescricao("Tatuagem média colorida");
        agendamento.setDtInicio(inicio);
        agendamento.setDtFim(fim);
        agendamento.setValor(valor);
        agendamento.setProfissional(profissional);
        agendamento.setUsuario(usuario);
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        
        // Verifica se todos mantêm seus valores
        assertEquals(TipoServico.TATUAGEM_MEDIA, agendamento.getTipoServico());
        assertEquals("Tatuagem média colorida", agendamento.getDescricao());
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
        assertEquals(valor, agendamento.getValor());
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
        
        // Altera um campo e verifica se os outros se mantêm
        agendamento.setDescricao("Nova descrição para tatuagem média");
        
        assertEquals(TipoServico.TATUAGEM_MEDIA, agendamento.getTipoServico());
        assertEquals("Nova descrição para tatuagem média", agendamento.getDescricao());
        assertEquals(inicio, agendamento.getDtInicio());
        assertEquals(fim, agendamento.getDtFim());
        assertEquals(valor, agendamento.getValor());
        assertEquals(profissional, agendamento.getProfissional());
        assertEquals(usuario, agendamento.getUsuario());
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
    }

    @Test
    @DisplayName("Deve validar regras de negócio combinadas")
    void deveValidarRegrasNegocioCombinadas() {
        Agendamento agendamento = new Agendamento();
        LocalDateTime inicio = LocalDateTime.now().plusHours(2);
        
        // Define data de início primeiro
        agendamento.setDtInicio(inicio);
        
        // Tenta definir data de fim anterior - deve falhar
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setDtFim(inicio.minusHours(1));
        });
        
        // Define data de fim correta
        agendamento.setDtFim(inicio.plusHours(2));
        
        // Tenta definir valor inválido - deve falhar
        assertThrows(IllegalArgumentException.class, () -> {
            agendamento.setValor(BigDecimal.ZERO);
        });
    }
} 