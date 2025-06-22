package inkspiration.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.dto.UsuarioSeguroDTO;
import inkspiration.backend.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String searchTerm) {

        Pageable pageable = PageRequest.of(page, size);
        Map<String, Object> response = service.listarTodosComPaginacaoComAutorizacao(pageable, searchTerm);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSeguroDTO> buscarPorId(@PathVariable Long id) {
        UsuarioSeguroDTO dto = service.buscarPorIdComAutorizacao(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/detalhes/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarDetalhes(@PathVariable Long id) {
        UsuarioResponseDTO dto = service.buscarDetalhesComAutorizacao(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<UsuarioSeguroDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO dto) {
        UsuarioSeguroDTO usuario = service.atualizarComAutorizacao(id, dto);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/inativar/{id}")
    public ResponseEntity<String> inativarUsuario(@PathVariable Long id) {
        service.inativarComAutorizacao(id);
        return ResponseEntity.ok("Usuário inativado com sucesso.");
    }
    
    @PostMapping("/reativar/{id}")
    public ResponseEntity<String> reativarUsuario(@PathVariable Long id) {
        service.reativarComAutorizacao(id);
        return ResponseEntity.ok("Usuário reativado com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> excluirUsuario(@PathVariable Long id) {
        service.deletarComAutorizacao(id);
        return ResponseEntity.ok("Usuário excluído com sucesso.");
    }

    @PutMapping("/{id}/foto-perfil")
    public ResponseEntity<Void> atualizarFotoPerfil(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String imagemBase64 = request.get("imagemBase64");
        service.atualizarFotoPerfilComAutorizacao(id, imagemBase64);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean isValid = service.validateTokenComplete(id, token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        
        return ResponseEntity.ok(response);
    }
} 