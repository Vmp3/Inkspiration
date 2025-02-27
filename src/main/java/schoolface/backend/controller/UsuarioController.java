package schoolface.backend.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import schoolface.backend.dto.UsuarioDTO;
import schoolface.backend.dto.UsuarioResponseDTO;
import schoolface.backend.entities.Usuario;
import schoolface.backend.service.UsuarioService;
import schoolface.backend.exception.UsuarioException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos(@RequestParam(defaultValue = "1") int page) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        return service.listarTodosResponse(pageable);
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // Permite tanto USER quanto ADMIN acessarem
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO dto) {
        try {
            Usuario usuario = service.atualizar(id, dto);
            return ResponseEntity.ok(usuario);
        } catch (UsuarioException.PermissaoNegadaException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas administradores podem alterar roles");
        }
    }

    @PostMapping("/inativar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> inativarUsuario(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.ok("Usuário inativado com sucesso.");
    }

    @DeleteMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> excluirUsuario(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.ok("Usuário excluído com sucesso.");
    }
} 