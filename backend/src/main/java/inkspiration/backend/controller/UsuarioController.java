package inkspiration.backend.controller;

import java.util.List;
import java.util.Map;

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
import inkspiration.backend.security.AuthorizationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService service;
    private final AuthorizationService authorizationService;

    public UsuarioController(UsuarioService service, AuthorizationService authorizationService) {
        this.service = service;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(@RequestParam(defaultValue = "0") int page) {
        // Apenas administradores podem listar todos os usuários
        authorizationService.requireAdmin();
        
        Pageable pageable = PageRequest.of(page, 10);
        List<UsuarioResponseDTO> usuarios = service.listarTodosResponse(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSeguroDTO> buscarPorId(@PathVariable Long id) {
        // Verifica se o usuário pode acessar esses dados
        authorizationService.requireUserAccessOrAdmin(id);
        
        Usuario usuario = service.buscarPorId(id);
        UsuarioSeguroDTO dto = UsuarioSeguroDTO.fromUsuario(usuario);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/detalhes/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarDetalhes(@PathVariable Long id) {
        // Verifica se o usuário pode acessar esses dados detalhados
        authorizationService.requireUserAccessOrAdmin(id);
        
        Usuario usuario = service.buscarPorId(id);
        String dataNascimentoStr = null;
        
        if (usuario.getDataNascimento() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimentoStr = usuario.getDataNascimento().format(formatter);
        }
        
        UsuarioResponseDTO dto = new UsuarioResponseDTO(
            usuario.getIdUsuario(),
            usuario.getNome(),
            usuario.getCpf(),
            usuario.getEmail(),
            dataNascimentoStr,
            usuario.getTelefone(),
            usuario.getImagemPerfil(),
            usuario.getEndereco(),
            usuario.getRole()
        );
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
            // Verifica se o usuário pode editar este perfil
            authorizationService.requireUserAccessOrAdmin(id);
            
            Usuario usuario = service.atualizar(id, dto);
            return ResponseEntity.ok(UsuarioSeguroDTO.fromUsuario(usuario));
        } catch (UsuarioException.PermissaoNegadaException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas administradores podem alterar roles");
        }
    }

    @PostMapping("/inativar/{id}")
    public ResponseEntity<String> inativarUsuario(@PathVariable Long id) {
        // Apenas administradores podem inativar usuários
        authorizationService.requireAdmin();
        
        service.inativar(id);
        return ResponseEntity.ok("Usuário inativado com sucesso.");
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<String> excluirUsuario(@PathVariable Long id) {
        // Apenas administradores podem excluir usuários
        authorizationService.requireAdmin();
        
        service.deletar(id);
        return ResponseEntity.ok("Usuário excluído com sucesso.");
    }

    @PutMapping("/{id}/foto-perfil")
    public ResponseEntity<Void> atualizarFotoPerfil(@PathVariable Long id, @RequestBody Map<String, String> request) {
        // Verifica se o usuário pode editar este perfil
        authorizationService.requireUserAccessOrAdmin(id);
        
        String imagemBase64 = request.get("imagemBase64");
        if (imagemBase64 == null || imagemBase64.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        service.atualizarFotoPerfil(id, imagemBase64);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("valid", false, "message", "Token não fornecido")
                );
            }
            
            // Buscar o usuário diretamente sem validação de autorização prévia
            Usuario usuario;
            try {
                usuario = service.buscarPorId(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("valid", false, "message", "Usuário não encontrado")
                );
            }
            
            String tokenAtual = usuario.getTokenAtual();
            
            if (tokenAtual == null) {
                return ResponseEntity.ok(
                    Map.of("valid", false, "message", "Usuário não possui token ativo")
                );
            }
            
            // Verificar se o token enviado corresponde ao token atual no servidor
            boolean valid = token.equals(tokenAtual);
            
            if (valid) {
                return ResponseEntity.ok(Map.of("valid", true));
            } else {
                // Se o token for diferente, retornar o token atual do servidor
                return ResponseEntity.ok(
                    Map.of(
                        "valid", false, 
                        "message", "Token diferente do armazenado no servidor",
                        "newToken", tokenAtual
                    )
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                    "valid", false, 
                    "message", "Erro ao validar token: " + e.getMessage()
                )
            );
        }
    }
} 