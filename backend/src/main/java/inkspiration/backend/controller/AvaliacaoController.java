package inkspiration.backend.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inkspiration.backend.dto.AvaliacaoDTO;
import inkspiration.backend.dto.AvaliacaoRequestDTO;
import inkspiration.backend.entities.Avaliacao;
import inkspiration.backend.service.AvaliacaoService;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    
    private final AvaliacaoService avaliacaoService;
    
    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }
    
    @PostMapping
    public ResponseEntity<?> criarAvaliacao(@RequestBody AvaliacaoRequestDTO request) {
        try {
            Avaliacao avaliacao = avaliacaoService.criarAvaliacao(
                    request.getIdAgendamento(), 
                    request.getDescricao(), 
                    request.getRating());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AvaliacaoDTO(avaliacao));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            AvaliacaoDTO avaliacaoDTO = avaliacaoService.buscarPorIdDTO(id);
            return ResponseEntity.ok(avaliacaoDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page) {
        
        try {
            Pageable pageable = PageRequest.of(page, 10);
            Page<AvaliacaoDTO> avaliacoesPage = avaliacaoService.listarPorUsuarioDTO(idUsuario, pageable);
            return ResponseEntity.ok(avaliacoesPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}")
    public ResponseEntity<?> listarPorProfissional(
            @PathVariable Long idProfissional,
            @RequestParam(defaultValue = "0") int page) {
        
        try {
            Pageable pageable = PageRequest.of(page, 10);
            Page<AvaliacaoDTO> avaliacoesPage = avaliacaoService.listarPorProfissionalDTO(idProfissional, pageable);
            return ResponseEntity.ok(avaliacoesPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAvaliacao(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequestDTO request) {
        
        try {
            Avaliacao avaliacao = avaliacaoService.atualizarAvaliacao(id, request.getDescricao(), request.getRating());
            return ResponseEntity.ok(new AvaliacaoDTO(avaliacao));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAvaliacao(@PathVariable Long id) {
        try {
            avaliacaoService.excluirAvaliacao(id);
            return ResponseEntity.ok("Avaliação excluída com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 