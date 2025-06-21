package inkspiration.backend.controller;

import java.time.LocalDate;
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

import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.service.DisponibilidadeService;
import inkspiration.backend.service.ProfissionalService;

@RestController
@RequestMapping("/disponibilidades")
public class DisponibilidadeController {
    
    private final DisponibilidadeService disponibilidadeService;
    private final ProfissionalService profissionalService;
    
    public DisponibilidadeController(DisponibilidadeService disponibilidadeService, ProfissionalService profissionalService) {
        this.disponibilidadeService = disponibilidadeService;
        this.profissionalService = profissionalService;
    }
    
    @PostMapping("/profissional/{idProfissional}")
    public ResponseEntity<DisponibilidadeDTO> cadastrarDisponibilidade(
            @PathVariable Long idProfissional,
            @RequestBody Map<String, List<Map<String, String>>> horarios) {
        
            // Busca o profissional para obter o ID do usuário
            var profissional = profissionalService.buscarPorId(idProfissional);
            Long idUsuario = profissional.getUsuario().getIdUsuario();
            
        DisponibilidadeDTO disponibilidadeDTO = disponibilidadeService.cadastrarDisponibilidadeDTOComValidacao(
                idProfissional, horarios, idUsuario);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(disponibilidadeDTO);
    }
    
    @GetMapping("/profissional/{idProfissional}")
    public ResponseEntity<Map<String, List<Map<String, String>>>> obterDisponibilidade(@PathVariable Long idProfissional) {
            Map<String, List<Map<String, String>>> disponibilidade = 
                disponibilidadeService.obterDisponibilidadeComValidacao(idProfissional);
        
            return ResponseEntity.ok(disponibilidade);
    }
    
    @GetMapping("/profissional/{idProfissional}/dto")
    public ResponseEntity<DisponibilidadeDTO> obterDisponibilidadeDTO(@PathVariable Long idProfissional) {
            // Busca o profissional para obter o ID do usuário
            var profissional = profissionalService.buscarPorId(idProfissional);
            Long idUsuario = profissional.getUsuario().getIdUsuario();
            
        DisponibilidadeDTO disponibilidadeDTO = disponibilidadeService.buscarPorProfissionalDTOComValidacao(idProfissional, idUsuario);
            
            return ResponseEntity.ok(disponibilidadeDTO);
    }
    
    @GetMapping("/verificar")
    public ResponseEntity<?> obterHorariosDisponiveis(
            @RequestParam Long idProfissional,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam String tipoServico) {
        
        List<String> horariosDisponiveis = disponibilidadeService.obterHorariosDisponiveisComValidacao(
                idProfissional, data, tipoServico);
            
            if (horariosDisponiveis.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("mensagem", "Não há horários disponíveis para este dia e tipo de serviço"));
            }
            
            return ResponseEntity.ok(horariosDisponiveis);
    }
} 