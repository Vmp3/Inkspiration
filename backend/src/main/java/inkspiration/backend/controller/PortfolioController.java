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

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.service.PortfolioService;
import jakarta.validation.Valid;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolio")
    public ResponseEntity<List<PortfolioDTO>> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        List<PortfolioDTO> dtos = portfolioService.listarComAutorizacao(pageable);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/portfolio/{id}")
    public ResponseEntity<PortfolioDTO> buscarPorId(@PathVariable Long id) {
        PortfolioDTO portfolio = portfolioService.buscarPorIdComValidacao(id);
        return ResponseEntity.ok(portfolio);
    }

    @PutMapping("/portfolio/atualizar/{id}")
    public ResponseEntity<PortfolioDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PortfolioDTO dto) {
        PortfolioDTO portfolio = portfolioService.atualizarComValidacao(id, dto);
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/portfolio/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        portfolioService.deletarComValidacao(id);
        return ResponseEntity.noContent().build();
    }
} 