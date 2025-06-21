package inkspiration.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.service.PortifolioService;
import jakarta.validation.Valid;

@RestController
public class PortifolioController {

    private final PortifolioService portifolioService;

    @Autowired
    public PortifolioController(PortifolioService portifolioService) {
        this.portifolioService = portifolioService;
    }

    @GetMapping("/portifolio")
    public ResponseEntity<List<PortifolioDTO>> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        List<PortifolioDTO> dtos = portifolioService.listarComAutorizacao(pageable);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/portifolio/{id}")
    public ResponseEntity<PortifolioDTO> buscarPorId(@PathVariable Long id) {
        PortifolioDTO portifolio = portifolioService.buscarPorIdComValidacao(id);
        return ResponseEntity.ok(portifolio);
    }

    @PutMapping("/portifolio/atualizar/{id}")
    public ResponseEntity<PortifolioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PortifolioDTO dto) {
        PortifolioDTO portifolio = portifolioService.atualizarComValidacao(id, dto);
        return ResponseEntity.ok(portifolio);
    }

    @DeleteMapping("/portifolio/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        portifolioService.deletarComValidacao(id);
        return ResponseEntity.noContent().build();
    }
} 