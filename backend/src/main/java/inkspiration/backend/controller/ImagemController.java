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

    @GetMapping("/portifolio/{idPortifolio}")
    public ResponseEntity<List<ImagemDTO>> listarPorPortifolio(@PathVariable Long idPortifolio) {
        List<ImagemDTO> imagens = imagemService.listarPorPortifolio(idPortifolio);
        return ResponseEntity.ok(imagens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagemDTO> buscarPorId(@PathVariable Long id) {
        ImagemDTO imagem = imagemService.buscarPorId(id);
        return ResponseEntity.ok(imagem);
    }

    @PostMapping
    public ResponseEntity<ImagemDTO> salvar(@RequestBody ImagemDTO dto) {
        ImagemDTO imagemSalva = imagemService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagemSalva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        imagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 