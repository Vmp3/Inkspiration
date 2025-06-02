package inkspiration.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.service.DisponibilidadeService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeController {
    
    private final DisponibilidadeService disponibilidadeService;
    
    public DisponibilidadeController(DisponibilidadeService disponibilidadeService) {
        this.disponibilidadeService = disponibilidadeService;
    }
    
    @PostMapping("/profissional/{idProfissional}")
    public ResponseEntity<?> cadastrarDisponibilidade(
            @PathVariable Long idProfissional,
            @RequestBody Map<String, List<Map<String, String>>> horarios) {
        
        try {
            DisponibilidadeDTO disponibilidadeDTO = disponibilidadeService.cadastrarDisponibilidadeDTO(
                    idProfissional, horarios);
            return ResponseEntity.status(HttpStatus.CREATED).body(disponibilidadeDTO);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}")
    public ResponseEntity<?> obterDisponibilidade(@PathVariable Long idProfissional) {
        try {
            Map<String, List<Map<String, String>>> disponibilidade = 
                    disponibilidadeService.obterDisponibilidade(idProfissional);
            return ResponseEntity.ok(disponibilidade);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}/dto")
    public ResponseEntity<?> obterDisponibilidadeDTO(@PathVariable Long idProfissional) {
        try {
            DisponibilidadeDTO disponibilidadeDTO = disponibilidadeService.buscarPorProfissionalDTO(idProfissional);
            return ResponseEntity.ok(disponibilidadeDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}/verificar")
    public ResponseEntity<?> verificarDisponibilidade(
            @PathVariable Long idProfissional,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        
        try {
            boolean disponivel = disponibilidadeService.isProfissionalDisponivel(
                    idProfissional, inicio, fim);
            
            if (disponivel) {
                return ResponseEntity.ok(Map.of(
                    "disponivel", true,
                    "mensagem", "O profissional está trabalhando nesse horário"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "disponivel", false,
                    "mensagem", "O profissional não está trabalhando nesse horário"
                ));
            }
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 