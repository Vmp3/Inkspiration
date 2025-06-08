package inkspiration.backend.controller;

import java.time.LocalDate;
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
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.security.AuthorizationService;
import inkspiration.backend.service.ProfissionalService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeController {
    
    private final DisponibilidadeService disponibilidadeService;
    private final AuthorizationService authorizationService;
    private final ProfissionalService profissionalService;
    
    public DisponibilidadeController(DisponibilidadeService disponibilidadeService, AuthorizationService authorizationService, ProfissionalService profissionalService) {
        this.disponibilidadeService = disponibilidadeService;
        this.authorizationService = authorizationService;
        this.profissionalService = profissionalService;
    }
    
    @PostMapping("/profissional/{idProfissional}")
    public ResponseEntity<?> cadastrarDisponibilidade(
            @PathVariable Long idProfissional,
            @RequestBody Map<String, List<Map<String, String>>> horarios) {
        
        try {
            // Busca o profissional para obter o ID do usuário
            var profissional = profissionalService.buscarPorId(idProfissional);
            Long idUsuario = profissional.getUsuario().getIdUsuario();
            
            // Verifica se o usuário pode cadastrar disponibilidade para este profissional
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
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
            // Busca o profissional para obter o ID do usuário
            var profissional = profissionalService.buscarPorId(idProfissional);
            Long idUsuario = profissional.getUsuario().getIdUsuario();
            
            // Verifica se o usuário pode acessar a disponibilidade deste profissional
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
            DisponibilidadeDTO disponibilidadeDTO = disponibilidadeService.buscarPorProfissionalDTO(idProfissional);
            return ResponseEntity.ok(disponibilidadeDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/verificar")
    public ResponseEntity<?> obterHorariosDisponiveis(
            @RequestParam Long idProfissional,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam String tipoServico) {
        
        try {
            TipoServico tipoServicoEnum;
            try {
                tipoServicoEnum = TipoServico.fromDescricao(tipoServico);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Tipo de serviço inválido",
                    "mensagem", "Tipos válidos: pequena, media, grande, sessao"
                ));
            }
            
            List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveis(
                    idProfissional, data, tipoServicoEnum);
            
            if (horariosDisponiveis.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensagem", "Não há horários disponíveis para este dia e tipo de serviço"));
            }
            
            return ResponseEntity.ok(horariosDisponiveis);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Erro ao processar JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 