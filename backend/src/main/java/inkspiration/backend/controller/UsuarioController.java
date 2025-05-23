package inkspiration.backend.controller;

import java.util.List;

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

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.dto.UsuarioResponseDTO;
import inkspiration.backend.dto.UsuarioSeguroDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
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
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        List<UsuarioResponseDTO> usuarios = service.listarTodosResponse(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSeguroDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id);
        UsuarioSeguroDTO dto = UsuarioSeguroDTO.fromUsuario(usuario);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/buscar-por-cpf/{cpf}")
    public ResponseEntity<UsuarioSeguroDTO> buscarPorCpf(@PathVariable String cpf) {
        try {
            Usuario usuario = service.buscarPorCpf(cpf);
            UsuarioSeguroDTO dto = UsuarioSeguroDTO.fromUsuario(usuario);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDTO dto) {
        try {
            Usuario usuario = service.atualizar(id, dto);
            return ResponseEntity.ok(UsuarioSeguroDTO.fromUsuario(usuario));
        } catch (UsuarioException.PermissaoNegadaException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas administradores podem alterar roles");
        }
    }

    @PostMapping("/inativar/{id}")
    public ResponseEntity<String> inativarUsuario(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.ok("Usuário inativado com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> excluirUsuario(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.ok("Usuário excluído com sucesso.");
    }
} 