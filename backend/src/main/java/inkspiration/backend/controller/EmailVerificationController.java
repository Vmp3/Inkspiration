package inkspiration.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inkspiration.backend.dto.EmailVerificationRequest;
import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.service.EmailVerificationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/request-verification")
    public ResponseEntity<Map<String, Object>> requestEmailVerification(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        emailVerificationService.requestEmailVerificationComValidacao(usuarioDTO);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Email de verificação enviado com sucesso");
        response.put("email", usuarioDTO.getEmail());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestBody @Valid EmailVerificationRequest request) {
        Usuario usuario = emailVerificationService.verifyEmailAndCreateUserComValidacao(
            request.getEmail(), 
            request.getCode()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Conta criada com sucesso!");
        response.put("usuario", usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        emailVerificationService.resendVerificationCodeComValidacao(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Código de verificação reenviado com sucesso");
        
        return ResponseEntity.ok(response);
    }
} 