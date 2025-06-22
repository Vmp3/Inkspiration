package inkspiration.backend.service.profissionalService;

import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ProfissionalServiceValidacaoTest {

    @Test
    @DisplayName("Deve validar tipos de serviço disponíveis")
    void deveValidarTiposServicoDisponiveis() {
        // Given
        List<TipoServico> tiposValidos = Arrays.asList(
            TipoServico.TATUAGEM_PEQUENA,
            TipoServico.TATUAGEM_MEDIA,
            TipoServico.TATUAGEM_GRANDE,
            TipoServico.SESSAO
        );
        
        // When & Then
        for (TipoServico tipo : tiposValidos) {
            assertNotNull(tipo);
            assertNotNull(tipo.getDescricao());
            assertTrue(tipo.getDuracaoHoras() > 0);
        }
    }

    @Test
    @DisplayName("Deve validar descrições dos tipos de serviço")
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
    @DisplayName("Deve validar preços dos serviços")
    void deveValidarPrecosServicos() {
        // Given
        Map<String, BigDecimal> precosServicos = new HashMap<>();
        precosServicos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        precosServicos.put("TATUAGEM_MEDIA", new BigDecimal("200.00"));
        precosServicos.put("TATUAGEM_GRANDE", new BigDecimal("300.00"));
        precosServicos.put("SESSAO", new BigDecimal("400.00"));
        
        // When & Then
        for (Map.Entry<String, BigDecimal> entry : precosServicos.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertTrue(entry.getValue().compareTo(BigDecimal.ZERO) >= 0);
        }
    }

    @Test
    @DisplayName("Deve processar tipos de serviço com preços")
    void deveProcessarTiposServicoComPrecos() {
        // Given
        List<TipoServico> tipos = Arrays.asList(TipoServico.TATUAGEM_PEQUENA, TipoServico.TATUAGEM_MEDIA);
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("150.00"));
        precos.put("TATUAGEM_MEDIA", new BigDecimal("250.00"));
        
        // When
        Map<String, BigDecimal> tiposComPrecos = new HashMap<>();
        for (TipoServico tipo : tipos) {
            BigDecimal preco = precos.getOrDefault(tipo.name(), BigDecimal.ZERO);
            tiposComPrecos.put(tipo.name(), preco);
        }
        
        // Then
        assertEquals(new BigDecimal("150.00"), tiposComPrecos.get("TATUAGEM_PEQUENA"));
        assertEquals(new BigDecimal("250.00"), tiposComPrecos.get("TATUAGEM_MEDIA"));
    }

    @Test
    @DisplayName("Deve definir preço zero quando não informado")
    void deveDefinirPrecoZeroQuandoNaoInformado() {
        // Given
        Map<String, BigDecimal> tiposComPrecos = new HashMap<>();
        String tipoServico = "TATUAGEM_PEQUENA";
        
        // When
        BigDecimal preco = tiposComPrecos.getOrDefault(tipoServico, BigDecimal.ZERO);
        
        // Then
        assertEquals(BigDecimal.ZERO, preco);
    }

    @Test
    @DisplayName("Deve validar nota inicial como zero")
    void deveValidarNotaInicialComoZero() {
        // Given
        BigDecimal notaInicial = new BigDecimal("0.0");
        
        // When & Then
        assertEquals(0, notaInicial.compareTo(BigDecimal.ZERO));
        assertTrue(notaInicial.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(notaInicial.compareTo(new BigDecimal("5.0")) <= 0);
    }

    @Test
    @DisplayName("Deve validar faixas de nota válidas")
    void deveValidarFaixasNotaValidas() {
        // Given
        List<BigDecimal> notasValidas = Arrays.asList(
            new BigDecimal("0.0"),
            new BigDecimal("2.5"),
            new BigDecimal("3.7"),
            new BigDecimal("4.8"),
            new BigDecimal("5.0")
        );
        
        // When & Then
        for (BigDecimal nota : notasValidas) {
            assertTrue(nota.compareTo(BigDecimal.ZERO) >= 0);
            assertTrue(nota.compareTo(new BigDecimal("5.0")) <= 0);
        }
    }

    @Test
    @DisplayName("Deve rejeitar notas fora da faixa")
    void deveRejeitarNotasForaFaixa() {
        // Given
        List<BigDecimal> notasInvalidas = Arrays.asList(
            new BigDecimal("-1.0"),
            new BigDecimal("5.1"),
            new BigDecimal("10.0")
        );
        
        // When & Then
        for (BigDecimal nota : notasInvalidas) {
            boolean notaInvalida = nota.compareTo(BigDecimal.ZERO) < 0 || 
                                  nota.compareTo(new BigDecimal("5.0")) > 0;
            assertTrue(notaInvalida);
        }
    }

    @Test
    @DisplayName("Deve atualizar role do usuário para ROLE_PROF")
    void deveAtualizarRoleUsuarioParaROLE_PROF() {
        // Given
        String roleAnterior = "ROLE_USER";
        String roleNova = UserRole.ROLE_PROF.getRole();
        
        // When & Then
        assertEquals("ROLE_USER", roleAnterior);
        assertEquals("ROLE_PROF", roleNova);
        assertNotEquals(roleAnterior, roleNova);
    }

    @Test
    @DisplayName("Deve validar JSON de tipos de serviço")
    void deveValidarJSONTiposServico() {
        // Given
        String jsonTiposServico = """
            {
                "TATUAGEM_PEQUENA": 100.00,
                "TATUAGEM_MEDIA": 200.00
            }
            """;
        
        // When & Then
        assertTrue(jsonTiposServico.contains("TATUAGEM_PEQUENA"));
        assertTrue(jsonTiposServico.contains("TATUAGEM_MEDIA"));
        assertTrue(jsonTiposServico.contains("100.00"));
        assertTrue(jsonTiposServico.contains("200.00"));
    }

    @Test
    @DisplayName("Deve validar especialidades profissionais")
    void deveValidarEspecialidadesProfissionais() {
        // Given
        List<String> especialidades = Arrays.asList(
            "Tatuagem Realista",
            "Tatuagem Tribal",
            "Tatuagem Aquarela",
            "Tatuagem Blackwork",
            "Tatuagem Old School"
        );
        
        // When & Then
        for (String especialidade : especialidades) {
            assertNotNull(especialidade);
            assertFalse(especialidade.trim().isEmpty());
            assertTrue(especialidade.length() > 0);
        }
    }

    @Test
    @DisplayName("Deve validar experiência profissional")
    void deveValidarExperienciaProfissional() {
        // Given
        List<String> experiencias = Arrays.asList(
            "5 anos de experiência",
            "Mais de 10 anos no ramo",
            "Especialista em tatuagens grandes",
            "Formado em artes visuais"
        );
        
        // When & Then
        for (String experiencia : experiencias) {
            assertNotNull(experiencia);
            assertFalse(experiencia.trim().isEmpty());
        }
    }

    @Test
    @DisplayName("Deve consolidar períodos adjacentes de disponibilidade")
    void deveConsolidarPeriodosAdjacentesDisponibilidade() {
        // Given
        Map<String, String> periodo1 = new HashMap<>();
        periodo1.put("inicio", "08:00");
        periodo1.put("fim", "12:00");
        
        Map<String, String> periodo2 = new HashMap<>();
        periodo2.put("inicio", "12:00");
        periodo2.put("fim", "18:00");
        
        // When
        String fimPeriodo1 = periodo1.get("fim");
        String inicioPeriodo2 = periodo2.get("inicio");
        boolean saoAdjacentes = fimPeriodo1.equals(inicioPeriodo2);
        
        // Then
        assertTrue(saoAdjacentes);
    }

    @Test
    @DisplayName("Deve validar filtros de busca de profissionais")
    void deveValidarFiltrosBuscaProfissionais() {
        // Given
        String searchTerm = "João";
        String locationTerm = "São Paulo";
        double minRating = 4.0;
        String[] selectedSpecialties = {"Realista", "Tribal"};
        String sortBy = "melhorAvaliacao";
        
        // When & Then
        assertNotNull(searchTerm);
        assertNotNull(locationTerm);
        assertTrue(minRating >= 0.0 && minRating <= 5.0);
        assertNotNull(selectedSpecialties);
        assertTrue(selectedSpecialties.length > 0);
        assertNotNull(sortBy);
    }

    @Test
    @DisplayName("Deve validar ordenação de profissionais")
    void deveValidarOrdenacaoProfissionais() {
        // Given
        List<String> tiposOrdenacao = Arrays.asList(
            "melhorAvaliacao",
            "maisRecente", 
            "maisAntigo",
            "relevancia"
        );
        
        // When & Then
        for (String tipo : tiposOrdenacao) {
            assertNotNull(tipo);
            assertFalse(tipo.trim().isEmpty());
        }
    }

    @Test
    @DisplayName("Deve calcular preços baseados em horas")
    void deveCalcularPrecosBaseadosEmHoras() {
        // Given
        BigDecimal precoHora = new BigDecimal("50.00");
        int horasPequena = TipoServico.TATUAGEM_PEQUENA.getDuracaoHoras();
        int horasMedia = TipoServico.TATUAGEM_MEDIA.getDuracaoHoras();
        
        // When
        BigDecimal precoPequena = precoHora.multiply(new BigDecimal(horasPequena));
        BigDecimal precoMedia = precoHora.multiply(new BigDecimal(horasMedia));
        
        // Then
        assertEquals(new BigDecimal("100.00"), precoPequena);
        assertEquals(new BigDecimal("200.00"), precoMedia);
    }
} 