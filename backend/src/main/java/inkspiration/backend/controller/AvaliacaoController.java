package inkspiration.backend.controller;

import inkspiration.backend.dto.AvaliacaoDTO;
import inkspiration.backend.service.AvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criarAvaliacao(@Valid @RequestBody AvaliacaoDTO avaliacaoDTO) {
        try {
            AvaliacaoDTO avaliacaoCriada = avaliacaoService.criarAvaliacao(avaliacaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoCriada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }



    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<AvaliacaoDTO> buscarAvaliacaoPorAgendamento(@PathVariable Long idAgendamento) {
        try {
            Optional<AvaliacaoDTO> avaliacao = avaliacaoService.buscarAvaliacaoPorAgendamento(idAgendamento);
            return avaliacao.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pode-avaliar/{idAgendamento}")
    public ResponseEntity<Boolean> podeAvaliar(@PathVariable Long idAgendamento) {
        try {
            boolean podeAvaliar = avaliacaoService.podeAvaliar(idAgendamento);
            return ResponseEntity.ok(podeAvaliar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


} 