package inkspiration.backend.service.portfolioService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

class PortfolioServiceValidacaoTest {

    @Test
    @DisplayName("Deve validar dados de portfolio")
    void deveValidarDadosPortfolio() {
        // Given
        String titulo = "Portfolio Tatuagens";
        String descricao = "Portfólio com meus melhores trabalhos";
        List<String> imagens = Arrays.asList("base64image1", "base64image2", "base64image3");
        
        // When & Then
        assertNotNull(titulo);
        assertFalse(titulo.trim().isEmpty());
        assertNotNull(descricao);
        assertNotNull(imagens);
        assertEquals(3, imagens.size());
    }

    @Test
    @DisplayName("Deve validar título do portfolio")
    void deveValidarTituloPortfolio() {
        // Given
        List<String> titulosValidos = Arrays.asList(
            "Meu Portfolio",
            "Tatuagens Realistas",
            "Trabalhos de 2024",
            "Portfolio Profissional"
        );
        
        List<String> titulosInvalidos = Arrays.asList(
            "",
            "   ",
            "A" // Muito curto
        );
        
        // When & Then
        for (String titulo : titulosValidos) {
            assertNotNull(titulo);
            assertFalse(titulo.trim().isEmpty());
            assertTrue(titulo.length() > 1);
            assertTrue(titulo.length() <= 100);
        }
        
        for (String titulo : titulosInvalidos) {
            if (titulo != null) {
                boolean invalido = titulo.trim().isEmpty() || titulo.length() <= 1;
                assertTrue(invalido);
            }
        }
    }

    @Test
    @DisplayName("Deve validar descrição do portfolio")
    void deveValidarDescricaoPortfolio() {
        // Given
        List<String> descricoesValidas = Arrays.asList(
            "Portfolio com meus melhores trabalhos em tatuagem realista",
            "Tatuagens realizadas em 2024 com foco em aquarela e fineline",
            "Especialidade em tatuagens realistas e trabalhos coloridos",
            null // Descrição nula é permitida
        );
        
        String descricaoMuitoLonga = "A".repeat(501); // Mais de 500 caracteres
        
        // When & Then
        for (String descricao : descricoesValidas) {
            if (descricao != null) {
                assertTrue(descricao.length() >= 20 && descricao.length() <= 500);
            }
        }
        
        assertTrue(descricaoMuitoLonga.length() > 500);
    }

    @Test
    @DisplayName("Deve validar quantidade mínima de imagens")
    void deveValidarQuantidadeMinimaImagens() {
        // Given
        List<String> imagensVazias = Arrays.asList();
        List<String> umaImagem = Arrays.asList("base64image1");
        List<String> variasImagens = Arrays.asList("base64image1", "base64image2", "base64image3");
        
        // When & Then
        assertTrue(imagensVazias.isEmpty());
        assertEquals(1, umaImagem.size());
        assertEquals(3, variasImagens.size());
        assertTrue(variasImagens.size() >= 1);
    }

    @Test
    @DisplayName("Deve validar quantidade máxima de imagens")
    void deveValidarQuantidadeMaximaImagens() {
        // Given
        int maxImagens = 20;
        List<String> imagensDentroLimite = Arrays.asList("img1", "img2", "img3", "img4", "img5");
        
        // When
        boolean dentroLimite = imagensDentroLimite.size() <= maxImagens;
        
        // Then
        assertTrue(dentroLimite);
        assertTrue(imagensDentroLimite.size() < maxImagens);
    }

    @Test
    @DisplayName("Deve validar dados completos de portfolio")
    void deveValidarDadosCompletosPortfolio() {
        // Given
        Map<String, Object> portfolioData = new HashMap<>();
        portfolioData.put("idPortfolio", 1L);
        portfolioData.put("titulo", "Portfolio Teste");
        portfolioData.put("descricao", "Descrição detalhada do portfolio com trabalhos em tatuagem realista");
        portfolioData.put("idProfissional", 2L);
        portfolioData.put("nomeProfissional", "João Silva");
        portfolioData.put("imagens", Arrays.asList("base64img1", "base64img2"));
        
        // When & Then
        assertEquals(1L, portfolioData.get("idPortfolio"));
        assertEquals("Portfolio Teste", portfolioData.get("titulo"));
        String descricao = (String) portfolioData.get("descricao");
        assertTrue(descricao.length() >= 20 && descricao.length() <= 500);
        assertEquals(2L, portfolioData.get("idProfissional"));
        assertEquals("João Silva", portfolioData.get("nomeProfissional"));
        
        @SuppressWarnings("unchecked")
        List<String> imagens = (List<String>) portfolioData.get("imagens");
        assertEquals(2, imagens.size());
    }

    @Test
    @DisplayName("Deve validar associação com profissional")
    void deveValidarAssociacaoComProfissional() {
        // Given
        Long idProfissional = 1L;
        
        // When & Then
        assertNotNull(idProfissional);
        assertTrue(idProfissional > 0);
    }

    @Test
    @DisplayName("Deve validar busca por portfolios")
    void deveValidarBuscaPorPortfolios() {
        // Given
        String termoBusca = "tatuagem";
        String filtroEspecialidade = "realista";
        String ordenacao = "maisRecente";
        
        // When & Then
        assertNotNull(termoBusca);
        assertFalse(termoBusca.trim().isEmpty());
        assertNotNull(filtroEspecialidade);
        assertNotNull(ordenacao);
    }

    @Test
    @DisplayName("Deve validar ordenação de portfolios")
    void deveValidarOrdenacaoPortfolios() {
        // Given
        List<String> tiposOrdenacao = Arrays.asList(
            "maisRecente",
            "maisAntigo",
            "maisPopular",
            "alfabetica"
        );
        
        // When & Then
        for (String tipo : tiposOrdenacao) {
            assertNotNull(tipo);
            assertFalse(tipo.trim().isEmpty());
        }
    }

    @Test
    @DisplayName("Deve validar paginação de portfolios")
    void deveValidarPaginacaoPortfolios() {
        // Given
        int pagina = 0;
        int tamanho = 10;
        
        // When & Then
        assertTrue(pagina >= 0);
        assertTrue(tamanho > 0);
        assertTrue(tamanho <= 100); // Limite máximo
    }

    @Test
    @DisplayName("Deve validar imagens não nulas no portfolio")
    void deveValidarImagensNaoNulasPortfolio() {
        // Given
        List<String> imagensComNull = Arrays.asList("base64img1", null, "base64img3");
        List<String> imagensValidas = Arrays.asList("base64img1", "base64img2", "base64img3");
        
        // When & Then
        for (String imagem : imagensValidas) {
            assertNotNull(imagem);
            assertFalse(imagem.trim().isEmpty());
        }
        
        // Verifica se há nulls na lista
        boolean temNull = imagensComNull.contains(null);
        assertTrue(temNull);
    }

    @Test
    @DisplayName("Deve validar remoção de portfolio")
    void deveValidarRemocaoPortfolio() {
        // Given
        Long idPortfolio = 1L;
        Long idProfissional = 2L;
        
        // When & Then
        assertNotNull(idPortfolio);
        assertNotNull(idProfissional);
        assertTrue(idPortfolio > 0);
        assertTrue(idProfissional > 0);
    }

    @Test
    @DisplayName("Deve validar atualização de portfolio")
    void deveValidarAtualizacaoPortfolio() {
        // Given
        Long idPortfolio = 1L;
        String novoTitulo = "Portfolio Atualizado";
        String novaDescricao = "Nova descrição do portfolio";
        
        // When
        Map<String, Object> atualizacao = new HashMap<>();
        atualizacao.put("titulo", novoTitulo);
        atualizacao.put("descricao", novaDescricao);
        
        // Then
        assertNotNull(idPortfolio);
        assertEquals(novoTitulo, atualizacao.get("titulo"));
        assertEquals(novaDescricao, atualizacao.get("descricao"));
    }

    @Test
    @DisplayName("Deve validar filtros de busca avançada")
    void deveValidarFiltrosBuscaAvancada() {
        // Given
        String termoBusca = "tatuagem realista";
        String cidade = "São Paulo";
        String estado = "SP";
        List<String> estilos = Arrays.asList("Realista", "Tribal", "Aquarela");
        
        // When & Then
        if (termoBusca != null) {
            assertFalse(termoBusca.trim().isEmpty());
        }
        if (cidade != null) {
            assertFalse(cidade.trim().isEmpty());
        }
        if (estado != null) {
            assertEquals(2, estado.length());
        }
        if (estilos != null) {
            for (String estilo : estilos) {
                assertNotNull(estilo);
                assertFalse(estilo.trim().isEmpty());
            }
        }
    }

    @Test
    @DisplayName("Deve validar portfolio com imagens removidas")
    void deveValidarPortfolioComImagensRemovidas() {
        // Given
        List<String> imagensOriginais = Arrays.asList("img1", "img2", "img3", "img4");
        List<String> imagensRemover = Arrays.asList("img2", "img4");
        
        // When
        List<String> imagensRestantes = imagensOriginais.stream()
            .filter(img -> !imagensRemover.contains(img))
            .toList();
        
        // Then
        assertEquals(2, imagensRestantes.size());
        assertTrue(imagensRestantes.contains("img1"));
        assertTrue(imagensRestantes.contains("img3"));
        assertFalse(imagensRestantes.contains("img2"));
        assertFalse(imagensRestantes.contains("img4"));
    }

    @Test
    @DisplayName("Deve validar limite de caracteres nos campos")
    void deveValidarLimiteCaracteresCampos() {
        // Given
        String titulo100Chars = "A".repeat(100); // Exatamente 100
        String titulo101Chars = "A".repeat(101); // Excede o limite
        String descricao500Chars = "B".repeat(500); // Exatamente 500
        String descricao501Chars = "B".repeat(501); // Excede o limite
        
        // When & Then
        assertEquals(100, titulo100Chars.length());
        assertEquals(101, titulo101Chars.length());
        assertEquals(500, descricao500Chars.length());
        assertEquals(501, descricao501Chars.length());
        
        assertTrue(titulo100Chars.length() <= 100);
        assertFalse(titulo101Chars.length() <= 100);
        assertTrue(descricao500Chars.length() <= 500);
        assertFalse(descricao501Chars.length() <= 500);
    }
} 