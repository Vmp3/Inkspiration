package inkspiration.backend.entities.portifolio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Imagem;
import java.util.ArrayList;
import java.util.List;

public class PortifolioTest {

    private Portifolio portifolio;
    private Profissional profissional;
    private Imagem imagem;

    @BeforeEach
    void setUp() {
        portifolio = new Portifolio();
        profissional = new Profissional();
        imagem = new Imagem();
    }

    @Test
    void testGettersAndSettersIdPortifolio() {
        Long id = 1L;
        portifolio.setIdPortifolio(id);
        assertEquals(id, portifolio.getIdPortifolio(), "ID do portifólio deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersDescricao() {
        String descricao = "Artista especializado em tatuagens realistas e blackwork";
        portifolio.setDescricao(descricao);
        assertEquals(descricao, portifolio.getDescricao(), "Descrição deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersExperiencia() {
        String experiencia = "10 anos de experiência em tatuagem";
        portifolio.setExperiencia(experiencia);
        assertEquals(experiencia, portifolio.getExperiencia(), "Experiência deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersEspecialidade() {
        String especialidade = "Realismo, Blackwork, Old School";
        portifolio.setEspecialidade(especialidade);
        assertEquals(especialidade, portifolio.getEspecialidade(), "Especialidade deve ser igual à definida");
    }

    @Test
    void testGettersAndSettersWebsite() {
        String website = "https://www.meuportifolio.com";
        portifolio.setWebsite(website);
        assertEquals(website, portifolio.getWebsite(), "Website deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersTiktok() {
        String tiktok = "@meuportifolio";
        portifolio.setTiktok(tiktok);
        assertEquals(tiktok, portifolio.getTiktok(), "TikTok deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersInstagram() {
        String instagram = "@meuportifolio";
        portifolio.setInstagram(instagram);
        assertEquals(instagram, portifolio.getInstagram(), "Instagram deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersFacebook() {
        String facebook = "MeuPortifolio";
        portifolio.setFacebook(facebook);
        assertEquals(facebook, portifolio.getFacebook(), "Facebook deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersTwitter() {
        String twitter = "@meuportifolio";
        portifolio.setTwitter(twitter);
        assertEquals(twitter, portifolio.getTwitter(), "Twitter deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersProfissional() {
        portifolio.setProfissional(profissional);
        assertEquals(profissional, portifolio.getProfissional(), "Profissional deve ser igual ao definido");
    }

    @Test
    void testGettersAndSettersImagens() {
        List<Imagem> imagens = new ArrayList<>();
        imagens.add(imagem);
        
        portifolio.setImagens(imagens);
        assertEquals(imagens, portifolio.getImagens(), "Lista de imagens deve ser igual à definida");
    }

    @Test
    void testConstrutorPadrao() {
        Portifolio portifolioVazio = new Portifolio();
        
        assertNull(portifolioVazio.getIdPortifolio(), "ID deve ser nulo inicialmente");
        assertNull(portifolioVazio.getDescricao(), "Descrição deve ser nula inicialmente");
        assertNull(portifolioVazio.getExperiencia(), "Experiência deve ser nula inicialmente");
        assertNull(portifolioVazio.getEspecialidade(), "Especialidade deve ser nula inicialmente");
        assertNull(portifolioVazio.getWebsite(), "Website deve ser nulo inicialmente");
        assertNull(portifolioVazio.getTiktok(), "TikTok deve ser nulo inicialmente");
        assertNull(portifolioVazio.getInstagram(), "Instagram deve ser nulo inicialmente");
        assertNull(portifolioVazio.getFacebook(), "Facebook deve ser nulo inicialmente");
        assertNull(portifolioVazio.getTwitter(), "Twitter deve ser nulo inicialmente");
        assertNull(portifolioVazio.getProfissional(), "Profissional deve ser nulo inicialmente");
        assertNotNull(portifolioVazio.getImagens(), "Lista de imagens deve ser inicializada");
        assertTrue(portifolioVazio.getImagens().isEmpty(), "Lista de imagens deve estar vazia inicialmente");
    }

    @Test
    void testPortifolioComTodosOsCampos() {
        // Arrange
        Long id = 1L;
        String descricao = "Artista especializado em tatuagens realistas";
        String experiencia = "10 anos de experiência";
        String especialidade = "Realismo, Blackwork";
        String website = "https://www.exemplo.com";
        String tiktok = "@exemplo";
        String instagram = "@exemplo";
        String facebook = "Exemplo";
        String twitter = "@exemplo";

        // Act
        portifolio.setIdPortifolio(id);
        portifolio.setDescricao(descricao);
        portifolio.setExperiencia(experiencia);
        portifolio.setEspecialidade(especialidade);
        portifolio.setWebsite(website);
        portifolio.setTiktok(tiktok);
        portifolio.setInstagram(instagram);
        portifolio.setFacebook(facebook);
        portifolio.setTwitter(twitter);
        portifolio.setProfissional(profissional);

        // Assert
        assertEquals(id, portifolio.getIdPortifolio());
        assertEquals(descricao, portifolio.getDescricao());
        assertEquals(experiencia, portifolio.getExperiencia());
        assertEquals(especialidade, portifolio.getEspecialidade());
        assertEquals(website, portifolio.getWebsite());
        assertEquals(tiktok, portifolio.getTiktok());
        assertEquals(instagram, portifolio.getInstagram());
        assertEquals(facebook, portifolio.getFacebook());
        assertEquals(twitter, portifolio.getTwitter());
        assertEquals(profissional, portifolio.getProfissional());
    }

    @Test
    void testAdicionarImagem() {
        portifolio.adicionarImagem(imagem);
        
        assertTrue(portifolio.getImagens().contains(imagem), "Lista deve conter a imagem adicionada");
        assertEquals(portifolio, imagem.getPortifolio(), "Imagem deve referenciar o portifólio");
    }

    @Test
    void testRemoverImagem() {
        portifolio.adicionarImagem(imagem);
        portifolio.removerImagem(imagem);
        
        assertFalse(portifolio.getImagens().contains(imagem), "Lista não deve conter a imagem removida");
        assertNull(imagem.getPortifolio(), "Imagem não deve referenciar o portifólio");
    }

    @Test
    void testDescricaoLonga() {
        String descricaoLonga = "a".repeat(2000);
        
        assertDoesNotThrow(() -> {
            portifolio.setDescricao(descricaoLonga);
        }, "Deve aceitar descrição longa sem lançar exceção");
        
        assertEquals(descricaoLonga, portifolio.getDescricao(), "Deve armazenar descrição longa corretamente");
    }

    @Test
    void testExperienciaLonga() {
        String experienciaLonga = "a".repeat(1000);
        
        assertDoesNotThrow(() -> {
            portifolio.setExperiencia(experienciaLonga);
        }, "Deve aceitar experiência longa sem lançar exceção");
        
        assertEquals(experienciaLonga, portifolio.getExperiencia(), "Deve armazenar experiência longa corretamente");
    }

    @Test
    void testEspecialidadeLonga() {
        String especialidadeLonga = "a".repeat(500);
        
        assertDoesNotThrow(() -> {
            portifolio.setEspecialidade(especialidadeLonga);
        }, "Deve aceitar especialidade longa sem lançar exceção");
        
        assertEquals(especialidadeLonga, portifolio.getEspecialidade(), "Deve armazenar especialidade longa corretamente");
    }

    @Test
    void testURLsValidas() {
        String[] urlsValidas = {
            "https://www.exemplo.com",
            "http://exemplo.com",
            "https://exemplo.com.br",
            "https://subdomain.exemplo.com",
            "https://www.exemplo.com/portfolio",
            "https://exemplo.com/path/to/page"
        };

        for (String url : urlsValidas) {
            assertDoesNotThrow(() -> {
                portifolio.setWebsite(url);
                assertEquals(url, portifolio.getWebsite());
            }, "Deve aceitar URL válida: " + url);
        }
    }

    @Test
    void testRedesSociaisFormatos() {
        String[] formatosInstagram = {
            "@usuario",
            "usuario",
            "@usuario123",
            "@usuario_com_underscore",
            "@usuario.com.ponto"
        };

        for (String formato : formatosInstagram) {
            assertDoesNotThrow(() -> {
                portifolio.setInstagram(formato);
                assertEquals(formato, portifolio.getInstagram());
            }, "Deve aceitar formato Instagram: " + formato);
        }
    }

    @Test
    void testEspecialidadesVariadas() {
        String[] especialidades = {
            "Realismo",
            "Blackwork",
            "Old School",
            "Realismo, Blackwork, Old School",
            "Tatuagem feminina delicada",
            "Lettering e caligrafia",
            "Pontilhismo e Mandala",
            "Aquarela e Colorido"
        };

        for (String especialidade : especialidades) {
            assertDoesNotThrow(() -> {
                portifolio.setEspecialidade(especialidade);
                assertEquals(especialidade, portifolio.getEspecialidade());
            }, "Deve aceitar especialidade: " + especialidade);
        }
    }

    @Test
    void testCamposVazios() {
        portifolio.setDescricao("");
        portifolio.setExperiencia("");
        portifolio.setEspecialidade("");
        portifolio.setWebsite("");
        portifolio.setTiktok("");
        portifolio.setInstagram("");
        portifolio.setFacebook("");
        portifolio.setTwitter("");

        assertEquals("", portifolio.getDescricao(), "Deve aceitar descrição vazia");
        assertEquals("", portifolio.getExperiencia(), "Deve aceitar experiência vazia");
        assertEquals("", portifolio.getEspecialidade(), "Deve aceitar especialidade vazia");
        assertEquals("", portifolio.getWebsite(), "Deve aceitar website vazio");
        assertEquals("", portifolio.getTiktok(), "Deve aceitar TikTok vazio");
        assertEquals("", portifolio.getInstagram(), "Deve aceitar Instagram vazio");
        assertEquals("", portifolio.getFacebook(), "Deve aceitar Facebook vazio");
        assertEquals("", portifolio.getTwitter(), "Deve aceitar Twitter vazio");
    }

    @Test
    void testCamposNulos() {
        portifolio.setDescricao(null);
        portifolio.setExperiencia(null);
        portifolio.setEspecialidade(null);
        portifolio.setWebsite(null);
        portifolio.setTiktok(null);
        portifolio.setInstagram(null);
        portifolio.setFacebook(null);
        portifolio.setTwitter(null);
        portifolio.setProfissional(null);

        assertNull(portifolio.getDescricao(), "Deve aceitar descrição nula");
        assertNull(portifolio.getExperiencia(), "Deve aceitar experiência nula");
        assertNull(portifolio.getEspecialidade(), "Deve aceitar especialidade nula");
        assertNull(portifolio.getWebsite(), "Deve aceitar website nulo");
        assertNull(portifolio.getTiktok(), "Deve aceitar TikTok nulo");
        assertNull(portifolio.getInstagram(), "Deve aceitar Instagram nulo");
        assertNull(portifolio.getFacebook(), "Deve aceitar Facebook nulo");
        assertNull(portifolio.getTwitter(), "Deve aceitar Twitter nulo");
        assertNull(portifolio.getProfissional(), "Deve aceitar profissional nulo");
    }

    @Test
    void testMultiplasImagens() {
        Imagem imagem1 = new Imagem();
        Imagem imagem2 = new Imagem();
        Imagem imagem3 = new Imagem();

        portifolio.adicionarImagem(imagem1);
        portifolio.adicionarImagem(imagem2);
        portifolio.adicionarImagem(imagem3);

        assertEquals(3, portifolio.getImagens().size(), "Deve ter 3 imagens");
        assertTrue(portifolio.getImagens().contains(imagem1), "Deve conter imagem1");
        assertTrue(portifolio.getImagens().contains(imagem2), "Deve conter imagem2");
        assertTrue(portifolio.getImagens().contains(imagem3), "Deve conter imagem3");
    }

    @Test
    void testRemoverImagemInexistente() {
        Imagem imagemInexistente = new Imagem();
        
        assertDoesNotThrow(() -> {
            portifolio.removerImagem(imagemInexistente);
        }, "Deve remover imagem inexistente sem lançar exceção");
        
        assertTrue(portifolio.getImagens().isEmpty(), "Lista deve permanecer vazia");
    }

    @Test
    void testValoresLimite() {
        // Teste com IDs extremos
        Long idMaximo = Long.MAX_VALUE;
        Long idMinimo = 1L;
        
        portifolio.setIdPortifolio(idMaximo);
        assertEquals(idMaximo, portifolio.getIdPortifolio(), "Deve aceitar ID máximo");
        
        portifolio.setIdPortifolio(idMinimo);
        assertEquals(idMinimo, portifolio.getIdPortifolio(), "Deve aceitar ID mínimo válido");
    }

    @Test
    void testCaracteresEspeciais() {
        portifolio.setDescricao("Artista especializado em tatuagens com acentuação: ção, ã, õ, ê");
        portifolio.setExperiencia("Experiência com símbolos: @#$%&*()");
        portifolio.setEspecialidade("Realismo 3D & Lettering com ♥ ♦ ♣ ♠");

        assertNotNull(portifolio.getDescricao(), "Deve aceitar acentos na descrição");
        assertNotNull(portifolio.getExperiencia(), "Deve aceitar símbolos na experiência");
        assertNotNull(portifolio.getEspecialidade(), "Deve aceitar símbolos especiais na especialidade");
    }

    @Test
    void testRelacionamentoBidirecional() {
        profissional.setIdProfissional(1L);
        portifolio.setProfissional(profissional);
        
        assertNotNull(portifolio.getProfissional(), "Profissional deve estar definido no portifólio");
        assertEquals(1L, portifolio.getProfissional().getIdProfissional(), "ID do profissional deve estar correto");
    }

    @Test
    void testImagensComConteudo() {
        imagem.setImagemBase64("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
        
        portifolio.adicionarImagem(imagem);
        
        assertEquals(1, portifolio.getImagens().size(), "Deve ter uma imagem");
        assertNotNull(portifolio.getImagens().get(0).getImagemBase64(), "Imagem deve ter conteúdo Base64");
    }

    @Test
    void testDescricaoComQuebrasDeLinha() {
        String descricaoComQuebras = "Primeira linha\nSegunda linha\nTerceira linha";
        
        portifolio.setDescricao(descricaoComQuebras);
        assertEquals(descricaoComQuebras, portifolio.getDescricao(), "Deve aceitar quebras de linha na descrição");
    }
} 