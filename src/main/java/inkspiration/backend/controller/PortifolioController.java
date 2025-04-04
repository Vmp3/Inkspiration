package inkspiration.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import inkspiration.backend.entities.Portifolio;
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
    public ResponseEntity<List<PortifolioDTO>> listar(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Portifolio> portifolios = portifolioService.listarTodos(pageable);
        
        List<PortifolioDTO> dtos = portifolios.getContent().stream()
                .map(portifolioService::converterParaDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/portifolio/{id}")
    public ResponseEntity<PortifolioDTO> buscarPorId(@PathVariable Long id) {
        Portifolio portifolio = portifolioService.buscarPorId(id);
        return ResponseEntity.ok(portifolioService.converterParaDto(portifolio));
    }

    @PostMapping("/auth/register/portifolio")
    public ResponseEntity<PortifolioDTO> criar(@RequestBody @Valid PortifolioDTO dto) {
        Portifolio portifolio = portifolioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(portifolioService.converterParaDto(portifolio));
    }

    @PutMapping("/portifolio/atualizar/{id}")
    public ResponseEntity<PortifolioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PortifolioDTO dto) {
        Portifolio portifolio = portifolioService.atualizar(id, dto);
        return ResponseEntity.ok(portifolioService.converterParaDto(portifolio));
    }

    @DeleteMapping("/portifolio/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        portifolioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 