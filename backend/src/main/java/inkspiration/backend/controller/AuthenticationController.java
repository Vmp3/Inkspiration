package inkspiration.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inkspiration.backend.dto.UsuarioAutenticarDTO;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.security.AuthenticationService;
import inkspiration.backend.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UsuarioService usuarioService;

    public AuthenticationController(AuthenticationService authService, 
                                  UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid UsuarioAutenticarDTO loginDTO) {
        String token = authService.login(loginDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("token", token);
        response.put("message", "Login realizado com sucesso");
        
        return ResponseEntity.ok(response);
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
        String newToken = authService.refreshToken(userId);
        return ResponseEntity.ok(newToken);
    }
    
    @PostMapping("/reauth/{userId}")
    public ResponseEntity<String> reautenticar(@PathVariable Long userId) {
        String newToken = authService.reautenticar(userId);
        return ResponseEntity.ok(newToken);
    }

    @PostMapping("/check-2fa")
    public ResponseEntity<Map<String, Object>> checkTwoFactorRequirement(@RequestBody Map<String, String> request) {
        String cpf = request.get("cpf");
        boolean requiresTwoFactor = authService.checkTwoFactorRequirement(cpf);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("requiresTwoFactor", requiresTwoFactor);
        
        return ResponseEntity.ok(response);
    }
}