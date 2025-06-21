package inkspiration.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.service.ProfissionalService;
import jakarta.validation.Valid;

@RestController
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    @Autowired
    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @GetMapping("/profissional")
    public ResponseEntity<List<ProfissionalDTO>> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        List<ProfissionalDTO> dtos = profissionalService.listarComAutorizacao(pageable);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profissional/publico")
    public ResponseEntity<List<ProfissionalDTO>> listarPublico() {
        Pageable pageable = PageRequest.of(0, 100);
        List<ProfissionalDTO> dtos = profissionalService.listarPublico(pageable);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profissional/completo")
    public ResponseEntity<Map<String, Object>> listarCompleto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String locationTerm,
            @RequestParam(defaultValue = "0") double minRating,
            @RequestParam(required = false) String[] selectedSpecialties,
            @RequestParam(defaultValue = "melhorAvaliacao") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Map<String, Object>> profissionais = profissionalService.listarCompletoComFiltros(
            pageable, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", profissionais.getContent());
        response.put("totalElements", profissionais.getTotalElements());
        response.put("totalPages", profissionais.getTotalPages());
        response.put("currentPage", profissionais.getNumber());
        response.put("size", profissionais.getSize());
        response.put("hasNext", profissionais.hasNext());
        response.put("hasPrevious", profissionais.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profissional/completo/{id}")
    public ResponseEntity<Map<String, Object>> buscarCompletoPorid(@PathVariable Long id) {
        Map<String, Object> profissionalCompleto = profissionalService.buscarCompletoComValidacao(id);
        return ResponseEntity.ok(profissionalCompleto);
    }

    @GetMapping("/profissional/{id}")
    public ResponseEntity<ProfissionalDTO> buscarPorId(@PathVariable Long id) {
        ProfissionalDTO profissional = profissionalService.converterParaDto(profissionalService.buscarPorId(id));
        return ResponseEntity.ok(profissional);
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}")
    public ResponseEntity<ProfissionalDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        ProfissionalDTO profissional = profissionalService.buscarPorUsuarioComAutorizacao(idUsuario);
        return ResponseEntity.ok(profissional);
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}/completo")
    public ResponseEntity<Map<String, Object>> buscarProfissionalCompleto(@PathVariable Long idUsuario) {
        ProfissionalService.ProfissionalCompletoData data = profissionalService.buscarProfissionalCompletoComAutorizacao(idUsuario);
        
            Map<String, Object> response = new HashMap<>();
        response.put("profissional", data.getProfissional());
        response.put("portfolio", data.getPortfolio());
        response.put("imagens", data.getImagens());
        response.put("disponibilidades", data.getDisponibilidades());
        response.put("tiposServico", data.getTiposServico());
        response.put("precosServicos", data.getPrecosServicos());
        response.put("tiposServicoPrecos", data.getTiposServicoPrecos());
            
            return ResponseEntity.ok(response);
    }
    
    @GetMapping("/profissional/verificar/{idUsuario}")
    public ResponseEntity<Boolean> verificarPerfil(@PathVariable Long idUsuario) {
        Boolean existePerfil = profissionalService.verificarPerfilComAutorizacao(idUsuario);
        return ResponseEntity.ok(existePerfil);
    }

    @GetMapping("/tipos-servico")
    public ResponseEntity<List<Map<String, Object>>> listarTiposServico() {
        List<Map<String, Object>> tiposServico = profissionalService.listarTiposServico();
        return ResponseEntity.ok(tiposServico);
    }

    @GetMapping("/tipos-servico/{idProfissional}")
    public ResponseEntity<List<Map<String, Object>>> listarTiposServicoPorProfissional(@PathVariable Long idProfissional) {
        List<Map<String, Object>> tiposServico = profissionalService.listarTiposServicoPorProfissionalComValidacao(idProfissional);
            return ResponseEntity.ok(tiposServico);
    }

    @PostMapping("/auth/register/profissional-completo")
    public ResponseEntity<ProfissionalDTO> criarProfissionalCompleto(@RequestBody @Valid ProfissionalCriacaoDTO dto) {
        ProfissionalDTO profissional = profissionalService.criarProfissionalCompletoComValidacao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profissional);
    }

    @PutMapping("/profissional/atualizar/{id}")
    public ResponseEntity<ProfissionalDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProfissionalDTO dto) {
        ProfissionalDTO profissionalAtualizado = profissionalService.atualizarComAutorizacao(id, dto);
        return ResponseEntity.ok(profissionalAtualizado);
    }

    @PutMapping("/profissional/usuario/{idUsuario}/atualizar-completo")
    public ResponseEntity<ProfissionalDTO> atualizarProfissionalCompleto(@PathVariable Long idUsuario, @RequestBody @Valid ProfissionalCriacaoDTO dto) {
        ProfissionalDTO profissionalAtualizado = profissionalService.atualizarProfissionalCompletoComAutorizacao(idUsuario, dto);
        return ResponseEntity.ok(profissionalAtualizado);
    }

    @PutMapping("/profissional/usuario/{idUsuario}/atualizar-completo-com-imagens")
    public ResponseEntity<Map<String, Object>> atualizarProfissionalCompletoComImagens(@PathVariable Long idUsuario, @RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = profissionalService.atualizarProfissionalCompletoComImagensComAutorizacao(idUsuario, requestData);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profissional/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        profissionalService.deletarComAutorizacao(id);
        return ResponseEntity.noContent().build();
    }
} 