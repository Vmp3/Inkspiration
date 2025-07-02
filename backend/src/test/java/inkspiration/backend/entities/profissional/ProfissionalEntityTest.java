package inkspiration.backend.entities.profissional;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Endereco;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;

@DisplayName("Testes gerais da entidade Profissional")
public class ProfissionalEntityTest {

    private Profissional profissional;

    @BeforeEach
    void setUp() {
        profissional = new Profissional();
    }

    @Test
    @DisplayName("Deve criar profissional com construtor padrão")
    void deveCriarProfissionalComConstrutorPadrao() {
        assertNotNull(profissional);
        assertNull(profissional.getIdProfissional());
        assertNull(profissional.getUsuario());
        assertNull(profissional.getEndereco());
        assertNull(profissional.getPortfolio());
        assertNull(profissional.getNota());
        assertNull(profissional.getTiposServicoStr());
        assertNotNull(profissional.getTiposServicoPrecos());
    }

    @Test
    @DisplayName("Deve definir e obter ID")
    void deveDefinirEObterID() {
        Long id = 123L;
        profissional.setIdProfissional(id);
        assertEquals(id, profissional.getIdProfissional());
    }

    @Test
    @DisplayName("Deve aceitar ID nulo")
    void deveAceitarIdNulo() {
        profissional.setIdProfissional(null);
        assertNull(profissional.getIdProfissional());
    }

    @Test
    @DisplayName("Deve definir e obter endereço")
    void deveDefinirEObterEndereco() {
        Endereco endereco = new Endereco();
        profissional.setEndereco(endereco);
        assertEquals(endereco, profissional.getEndereco());
    }

    @Test
    @DisplayName("Deve aceitar endereço nulo")
    void deveAceitarEnderecoNulo() {
        profissional.setEndereco(null);
        assertNull(profissional.getEndereco());
    }

    @Test
    @DisplayName("Deve definir e obter portfolio")
    void deveDefinirEObterPortfolio() {
        Portfolio portfolio = new Portfolio();
        profissional.setPortfolio(portfolio);
        assertEquals(portfolio, profissional.getPortfolio());
    }

    @Test
    @DisplayName("Deve aceitar portfolio nulo")
    void deveAceitarPortfolioNulo() {
        profissional.setPortfolio(null);
        assertNull(profissional.getPortfolio());
    }

    @Test
    @DisplayName("Deve definir e obter tipos de serviço preços")
    void deveDefinirEObterTiposServicoPrecos() {
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        precos.put("SESSAO", new BigDecimal("200.00"));
        
        profissional.setTiposServicoPrecos(precos);
        assertEquals(precos, profissional.getTiposServicoPrecos());
    }

    @Test
    @DisplayName("Deve aceitar tipos de serviço preços nulo")
    void deveAceitarTiposServicoPercosNulo() {
        profissional.setTiposServicoPrecos(null);
        assertNotNull(profissional.getTiposServicoPrecos());
        assertTrue(profissional.getTiposServicoPrecos().isEmpty());
    }

    @Test
    @DisplayName("Deve obter tipos de serviço como lista")
    void deveObterTiposServicoComoLista() {
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        precos.put("TATUAGEM_MEDIA", new BigDecimal("150.00"));
        
        profissional.setTiposServicoPrecos(precos);
        List<TipoServico> tipos = profissional.getTiposServico();
        
        assertEquals(2, tipos.size());
        assertTrue(tipos.contains(TipoServico.TATUAGEM_PEQUENA));
        assertTrue(tipos.contains(TipoServico.TATUAGEM_MEDIA));
    }

    @Test
    @DisplayName("Deve retornar lista vazia para tipos de serviço sem preços")
    void deveRetornarListaVaziaParaTiposServicoSemPrecos() {
        List<TipoServico> tipos = profissional.getTiposServico();
        assertNotNull(tipos);
        assertTrue(tipos.isEmpty());
    }

    @Test
    @DisplayName("Deve obter preços de serviços")
    void deveObterPrecosServicos() {
        Map<String, BigDecimal> precos = new HashMap<>();
        precos.put("TATUAGEM_PEQUENA", new BigDecimal("100.00"));
        
        profissional.setTiposServicoPrecos(precos);
        Map<String, BigDecimal> precosObtidos = profissional.getPrecosServicos();
        
        assertEquals(precos, precosObtidos);
        assertNotSame(precos, precosObtidos); // Deve ser uma cópia
    }
} 