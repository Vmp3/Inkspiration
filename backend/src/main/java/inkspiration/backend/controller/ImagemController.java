package inkspiration.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.service.ImagemService;

import java.util.List;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    private final ImagemService imagemService;

    @Autowired
    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @GetMapping("/portfolio/{idPortfolio}")
    public ResponseEntity<List<ImagemDTO>> listarPorPortfolio(@PathVariable Long idPortfolio) {
        List<ImagemDTO> imagens = imagemService.listarPorPortfolioComValidacao(idPortfolio);
        return ResponseEntity.ok(imagens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagemDTO> buscarPorId(@PathVariable Long id) {
        ImagemDTO imagem = imagemService.buscarPorIdComValidacao(id);
        return ResponseEntity.ok(imagem);
    }

    @PostMapping
    public ResponseEntity<ImagemDTO> salvar(@RequestBody ImagemDTO dto) {
        ImagemDTO imagemSalva = imagemService.salvarComValidacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagemSalva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        imagemService.deletarComValidacao(id);
        return ResponseEntity.noContent().build();
    }
} 