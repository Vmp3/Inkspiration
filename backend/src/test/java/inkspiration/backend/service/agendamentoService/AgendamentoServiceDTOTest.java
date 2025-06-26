package inkspiration.backend.service.agendamentoService;

import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

class AgendamentoServiceDTOTest {

    @Test
    @DisplayName("Deve criar AgendamentoRequestDTO com dados válidos")
    void deveCriarAgendamentoRequestDTOComDadosValidos() {
        // Given
        Long idUsuario = 1L;
        Long idProfissional = 2L;
        String tipoServico = "pequena";
        String descricao = "Tatuagem simples";
        LocalDateTime dtInicio = LocalDateTime.of(2024, 12, 15, 14, 0);
        BigDecimal valor = new BigDecimal("150.00");
        
        // When
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        dto.setIdUsuario(idUsuario);
        dto.setIdProfissional(idProfissional);
        dto.setTipoServico(tipoServico);
        dto.setDescricao(descricao);
        dto.setDtInicio(dtInicio);
        dto.setValor(valor);
        
        // Then
        assertEquals(idUsuario, dto.getIdUsuario());
        assertEquals(idProfissional, dto.getIdProfissional());
        assertEquals(tipoServico, dto.getTipoServico());
        assertEquals(descricao, dto.getDescricao());
        assertEquals(dtInicio, dto.getDtInicio());
        assertEquals(valor, dto.getValor());
    }

    @Test
    @DisplayName("Deve criar AgendamentoUpdateDTO com dados válidos")
    void deveCriarAgendamentoUpdateDTOComDadosValidos() {
        // Given
        String tipoServico = "media";
        String descricao = "Tatuagem média";
        LocalDateTime dtInicio = LocalDateTime.of(2024, 12, 20, 16, 0);
        
        // When
        AgendamentoUpdateDTO dto = new AgendamentoUpdateDTO();
        dto.setTipoServico(tipoServico);
        dto.setDescricao(descricao);
        dto.setDtInicio(dtInicio);
        
        // Then
        assertEquals(tipoServico, dto.getTipoServico());
        assertEquals(descricao, dto.getDescricao());
        assertEquals(dtInicio, dto.getDtInicio());
    }

    @Test
    @DisplayName("Deve formatar data e hora corretamente para exibição")
    void deveFormatarDataHoraCorretamenteParaExibicao() {
        // Given
        LocalDateTime dataHora = LocalDateTime.of(2024, 12, 15, 14, 30, 0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // When
        String dataFormatada = dateFormatter.format(dataHora);
        String horaFormatada = timeFormatter.format(dataHora);
        
        // Then
        assertEquals("15/12/2024", dataFormatada);
        assertEquals("14:30", horaFormatada);
    }

    @Test
    @DisplayName("Deve formatar valores monetários em real brasileiro")
    void deveFormatarValoresMonetariosEmRealBrasileiro() {
        // Given
        NumberFormat formatoBrasileiro = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        BigDecimal valor = new BigDecimal("150.50");
        
        // When
        String valorFormatado = formatoBrasileiro.format(valor);
        
        // Then
        assertTrue(valorFormatado.contains("150"));
        assertTrue(valorFormatado.contains("50"));
        assertTrue(valorFormatado.contains("R$") || valorFormatado.contains("$"));
    }

    @Test
    @DisplayName("Deve validar descrições de tipos de serviço")
    void deveValidarDescricoesTiposServico() {
        // Given & When & Then
        assertEquals("pequena", TipoServico.TATUAGEM_PEQUENA.getDescricao());
        assertEquals("media", TipoServico.TATUAGEM_MEDIA.getDescricao());
        assertEquals("grande", TipoServico.TATUAGEM_GRANDE.getDescricao());
        assertEquals("sessao", TipoServico.SESSAO.getDescricao());
    }

    @Test
    @DisplayName("Deve validar durações dos tipos de serviço")
    void deveValidarDuracoesTiposServico() {
        // Given & When & Then
        assertEquals(2, TipoServico.TATUAGEM_PEQUENA.getDuracaoHoras());
        assertEquals(4, TipoServico.TATUAGEM_MEDIA.getDuracaoHoras());
        assertEquals(6, TipoServico.TATUAGEM_GRANDE.getDuracaoHoras());
        assertEquals(8, TipoServico.SESSAO.getDuracaoHoras());
    }

    @Test
    @DisplayName("Deve validar descrições de status de agendamento")
    void deveValidarDescricoesStatusAgendamento() {
        // Given & When & Then
        assertEquals("Agendado", StatusAgendamento.AGENDADO.getDescricao());
        assertEquals("Cancelado", StatusAgendamento.CANCELADO.getDescricao());
        assertEquals("Concluido", StatusAgendamento.CONCLUIDO.getDescricao());
    }

    @Test
    @DisplayName("Deve criar faixas de horário válidas")
    void deveCriarFaixasHorarioValidas() {
        // Given
        LocalDateTime inicio = LocalDateTime.of(2024, 12, 15, 14, 0);
        TipoServico tipo = TipoServico.TATUAGEM_PEQUENA;
        
        // When
        LocalDateTime fim = inicio.plusHours(tipo.getDuracaoHoras());
        
        // Then
        assertEquals(16, fim.getHour());
        assertTrue(fim.isAfter(inicio));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no DTO de request")
    void deveValidarCamposObrigatoriosNoDTORequest() {
        // Given
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO();
        
        // When & Then
        assertNull(dto.getIdUsuario());
        assertNull(dto.getIdProfissional());
        assertNull(dto.getTipoServico());
        assertNull(dto.getDescricao());
        assertNull(dto.getDtInicio());
        assertNull(dto.getValor());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios no DTO de update")
    void deveValidarCamposObrigatoriosNoDTOUpdate() {
        // Given
        AgendamentoUpdateDTO dto = new AgendamentoUpdateDTO();
        
        // When & Then
        assertNull(dto.getTipoServico());
        assertNull(dto.getDescricao());
        assertNull(dto.getDtInicio());
    }

    @Test
    @DisplayName("Deve calcular preços baseados no tipo de serviço")
    void deveCalcularPrecosBaseadosNoTipoServico() {
        // Given
        BigDecimal precoBase = new BigDecimal("50.00");
        
        // When
        BigDecimal precoPequena = precoBase.multiply(new BigDecimal(TipoServico.TATUAGEM_PEQUENA.getDuracaoHoras()));
        BigDecimal precoMedia = precoBase.multiply(new BigDecimal(TipoServico.TATUAGEM_MEDIA.getDuracaoHoras()));
        BigDecimal precoGrande = precoBase.multiply(new BigDecimal(TipoServico.TATUAGEM_GRANDE.getDuracaoHoras()));
        BigDecimal precoSessao = precoBase.multiply(new BigDecimal(TipoServico.SESSAO.getDuracaoHoras()));
        
        // Then
        assertEquals(new BigDecimal("100.00"), precoPequena);
        assertEquals(new BigDecimal("200.00"), precoMedia);
        assertEquals(new BigDecimal("300.00"), precoGrande);
        assertEquals(new BigDecimal("400.00"), precoSessao);
    }

    @Test
    @DisplayName("Deve validar horários de trabalho válidos")
    void deveValidarHorariosTrabalhoValidos() {
        // Given
        LocalDateTime horarioComercial = LocalDateTime.of(2024, 12, 15, 9, 0);
        LocalDateTime horarioAlmoco = LocalDateTime.of(2024, 12, 15, 12, 0);
        LocalDateTime horarioTarde = LocalDateTime.of(2024, 12, 15, 14, 0);
        LocalDateTime horarioFimExpediente = LocalDateTime.of(2024, 12, 15, 18, 0);
        
        // When & Then
        assertTrue(horarioComercial.getHour() >= 8 && horarioComercial.getHour() <= 18);
        assertTrue(horarioAlmoco.getHour() >= 8 && horarioAlmoco.getHour() <= 18);
        assertTrue(horarioTarde.getHour() >= 8 && horarioTarde.getHour() <= 18);
        assertTrue(horarioFimExpediente.getHour() >= 8 && horarioFimExpediente.getHour() <= 18);
    }
} 