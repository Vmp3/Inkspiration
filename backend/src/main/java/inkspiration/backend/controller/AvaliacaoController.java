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
            AvaliacaoDTO dto = new AvaliacaoDTO();
            dto.setIdAgendamento(request.getIdAgendamento());
            dto.setDescricao(request.getDescricao());
            dto.setRating(request.getRating());
            AvaliacaoDTO created = avaliacaoService.criarAvaliacao(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            // Não existe buscarPorIdDTO, então buscar por agendamento e converter para DTO
            Avaliacao avaliacao = avaliacaoService.buscarPorAgendamento(id);
            if (avaliacao == null) {
                return ResponseEntity.notFound().build();
            }
            AvaliacaoDTO dto = new AvaliacaoDTO(avaliacao);
            return ResponseEntity.ok(dto);
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
            
            // Criar resposta com informações completas de paginação
            var response = new java.util.HashMap<String, Object>();
            response.put("content", avaliacoesPage.getContent());
            response.put("totalElements", avaliacoesPage.getTotalElements());
            response.put("totalPages", avaliacoesPage.getTotalPages());
            response.put("currentPage", avaliacoesPage.getNumber());
            response.put("hasNext", avaliacoesPage.hasNext());
            response.put("hasPrevious", avaliacoesPage.hasPrevious());
            response.put("size", avaliacoesPage.getSize());
            response.put("numberOfElements", avaliacoesPage.getNumberOfElements());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}/stats")
    public ResponseEntity<?> obterEstatisticasProfissional(@PathVariable Long idProfissional) {
        try {
            var stats = avaliacaoService.obterEstatisticasProfissional(idProfissional);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAvaliacao(
            @PathVariable Long id,
            @RequestBody AvaliacaoRequestDTO request) {
        
        try {
            AvaliacaoDTO dto = new AvaliacaoDTO();
            dto.setIdAvaliacao(id);
            dto.setDescricao(request.getDescricao());
            dto.setRating(request.getRating());
            AvaliacaoDTO updated = avaliacaoService.atualizarAvaliacao(dto);
            return ResponseEntity.ok(updated);
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
    
    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<?> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        try {
            Avaliacao avaliacao = avaliacaoService.buscarPorAgendamento(idAgendamento);
            if (avaliacao == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(new AvaliacaoDTO(avaliacao));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/can-rate-test/{idAgendamento}")
    public ResponseEntity<Boolean> podeAvaliar(@PathVariable Long idAgendamento) {
        try {
            boolean pode = avaliacaoService.podeAvaliar(idAgendamento);
            return ResponseEntity.ok(pode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/by-schedule-test/{idAgendamento}")
    public ResponseEntity<AvaliacaoDTO> buscarAvaliacaoPorAgendamento(@PathVariable Long idAgendamento) {
        try {
            var opt = avaliacaoService.buscarAvaliacaoPorAgendamento(idAgendamento);
            if (opt.isPresent()) {
                return ResponseEntity.ok(opt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/dto")
    public ResponseEntity<AvaliacaoDTO> criarAvaliacao(@RequestBody AvaliacaoDTO dto) {
        try {
            AvaliacaoDTO created = avaliacaoService.criarAvaliacao(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
} 