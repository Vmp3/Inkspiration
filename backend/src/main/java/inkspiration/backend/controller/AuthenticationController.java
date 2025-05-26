package inkspiration.backend.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.TokenRevogado;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.security.AuthenticationService;
import inkspiration.backend.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authService;
    private final UsuarioService usuarioService;

    public AuthenticationController(AuthenticationManager authenticationManager, AuthenticationService authService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UsuarioAutenticarDTO loginDTO) {
        System.out.println("Tentativa de login para usuário com CPF: " + loginDTO.getCpf());
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getCpf(), loginDTO.getSenha())
        );
    
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_DELETED"))) {
            System.out.println("Tentativa de login de usuário inativo com CPF: " + loginDTO.getCpf());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário inativo ou deletado");
        }
    
        String token = authService.authenticate(authentication);
        System.out.println("Token gerado para usuário com CPF: " + loginDTO.getCpf());
        
        // Salva o novo token no usuário
        Usuario usuario = usuarioService.buscarPorCpf(loginDTO.getCpf());
        if (usuario != null) {
            usuario.setTokenAtual(token);
            usuarioService.salvar(usuario);
            System.out.println("Token salvo para usuário com CPF: " + loginDTO.getCpf());
        }
    
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        Usuario novoUsuario = usuarioService.criar(usuarioDTO);
        return ResponseEntity.status(201).body(novoUsuario);
    }
    
    @PostMapping("/register/usuario")
    public ResponseEntity<Usuario> registerUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        Usuario novoUsuario = usuarioService.criar(usuarioDTO);
        return ResponseEntity.status(201).body(novoUsuario);
    }
    
    @PostMapping("/refresh-token/{userId}")
    public ResponseEntity<String> refreshToken(@PathVariable Long userId) {
        try {
            String newToken = usuarioService.atualizarTokenUsuario(userId);
            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar token: " + e.getMessage());
        }
    }
    
    @PostMapping("/verify-token")
    public ResponseEntity<Boolean> verifyToken(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }
        
        boolean isRevoked = authService.isTokenRevoked(token);
        return ResponseEntity.ok(isRevoked);
    }
    
    @PostMapping("/reauth/{userId}")
    public ResponseEntity<String> reautenticar(@PathVariable Long userId) {
        try {
            System.out.println("Reautenticando usuário ID: " + userId);
            
            // Buscar o usuário pelo ID
            Usuario usuario = usuarioService.buscarPorId(userId);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
            
            // Buscar o CPF do usuário
            String cpf = usuario.getCpf();
            
            // Buscar a role atual do usuário
            String role = usuario.getRole();
            System.out.println("Role atual do usuário: " + role);
            
            // Criar uma autenticação com a role atualizada
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                cpf, 
                null, 
                java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority(role)
                )
            );
            
            // Atualizar o token usando o serviço de usuário
            String token = usuarioService.atualizarTokenUsuario(userId);
            System.out.println("Novo token gerado para usuário ID: " + userId);
            
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao reautenticar: " + e.getMessage());
        }
    }
}