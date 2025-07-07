package inkspiration.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.service.AgendamentoService;

@RestController
@RequestMapping("/agendamentos")
@Validated
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;
    
    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }
    
    @PostMapping
    public ResponseEntity<AgendamentoDTO> criarAgendamento(@Valid @RequestBody AgendamentoRequestDTO request) {
        AgendamentoDTO agendamento = agendamentoService.criarAgendamentoComValidacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> buscarPorId(@PathVariable Long id) {
        AgendamentoDTO agendamentoDTO = agendamentoService.buscarPorIdComValidacao(id);
            return ResponseEntity.ok(agendamentoDTO);
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AgendamentoDTO>> listarPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<AgendamentoDTO> agendamentos = agendamentoService.listarPorUsuarioComValidacao(idUsuario, pageable);
        return ResponseEntity.ok(agendamentos);
    }
    
    @GetMapping("/profissional/{idProfissional}")
    public ResponseEntity<List<AgendamentoDTO>> listarPorProfissional(
            @PathVariable Long idProfissional,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<AgendamentoDTO> agendamentos = agendamentoService.listarPorProfissionalComValidacao(idProfissional, pageable);
        return ResponseEntity.ok(agendamentos);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> atualizarAgendamento(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoUpdateDTO request,
            Authentication authentication) {
        
        AgendamentoDTO agendamento = agendamentoService.atualizarAgendamentoComAutenticacao(id, request, authentication);
        return ResponseEntity.ok(agendamento);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirAgendamento(@PathVariable Long id) {
        agendamentoService.excluirAgendamentoComValidacao(id);
            return ResponseEntity.ok("Agendamento excluído com sucesso");
    }

    @GetMapping("/meus-agendamentos")
    public ResponseEntity<Page<AgendamentoDTO>> listarMeusAgendamentos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendamentoDTO> agendamentosPage = agendamentoService.listarMeusAgendamentosComAutenticacao(authentication, pageable);
        return ResponseEntity.ok(agendamentosPage);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AgendamentoDTO> atualizarStatusAgendamento(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        
        AgendamentoDTO agendamento = agendamentoService.atualizarStatusAgendamentoComAutenticacao(id, status, authentication);
        return ResponseEntity.ok(agendamento);
    }

    @GetMapping("/meus-agendamentos/futuros")
    public ResponseEntity<Page<AgendamentoCompletoDTO>> listarMeusAgendamentosFuturos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendamentoCompletoDTO> agendamentosPage = agendamentoService.listarMeusAgendamentosFuturosComAutenticacao(authentication, pageable);
        return ResponseEntity.ok(agendamentosPage);
    }

    @GetMapping("/meus-agendamentos/passados")
    public ResponseEntity<Page<AgendamentoCompletoDTO>> listarMeusAgendamentosPassados(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendamentoCompletoDTO> agendamentosPage = agendamentoService.listarMeusAgendamentosPassadosComAutenticacao(authentication, pageable);
        return ResponseEntity.ok(agendamentosPage);
    }

    @GetMapping("/relatorios/exportar-pdf")
    public ResponseEntity<?> exportarAgendamentosPDF(
            @RequestParam(required = true) Integer ano,
            Authentication authentication) {
        
        try {
            byte[] pdfBytes = agendamentoService.exportarAgendamentosPDFComAutenticacao(ano, authentication);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=agendamentos-" + ano + ".pdf");
            headers.add("Content-Length", String.valueOf(pdfBytes.length));
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Tratamento específico para mensagens de PDF sem dados
            if (e.getMessage() != null && e.getMessage().contains("Nenhum agendamento concluído encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
            }
            throw e;
        }
    }

    @GetMapping("/profissional/meus-atendimentos/futuros")
    public ResponseEntity<Page<AgendamentoCompletoDTO>> listarMeusAtendimentosFuturos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendamentoCompletoDTO> atendimentosPage = agendamentoService.listarMeusAtendimentosFuturosComAutenticacao(authentication, pageable);
        return ResponseEntity.ok(atendimentosPage);
    }

    @GetMapping("/profissional/meus-atendimentos/passados")
    public ResponseEntity<Page<AgendamentoCompletoDTO>> listarMeusAtendimentosPassados(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AgendamentoCompletoDTO> atendimentosPage = agendamentoService.listarMeusAtendimentosPassadosComAutenticacao(authentication, pageable);
        return ResponseEntity.ok(atendimentosPage);
    }

    @GetMapping("/profissional/relatorios/exportar-pdf")
    public ResponseEntity<?> exportarAtendimentosPDF(
            @RequestParam(required = true) Integer ano,
            @RequestParam(required = true) Integer mes,
            Authentication authentication) {
        
        try {
            byte[] pdfBytes = agendamentoService.exportarAtendimentosPDFComAutenticacao(ano, mes, authentication);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "attachment; filename=atendimentos-" + String.format("%02d", mes) + "-" + ano + ".pdf");
            headers.add("Content-Length", String.valueOf(pdfBytes.length));
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Tratamento específico para mensagens de PDF sem dados
            if (e.getMessage() != null && e.getMessage().contains("Nenhum atendimento concluído encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
            }
            throw e;
        }
    }
} 