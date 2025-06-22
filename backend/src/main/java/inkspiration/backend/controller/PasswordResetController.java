package inkspiration.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inkspiration.backend.dto.ForgotPasswordDTO;
import inkspiration.backend.dto.ResetPasswordDTO;
import inkspiration.backend.service.PasswordResetService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
        String message = passwordResetService.gerarCodigoRecuperacaoComValidacao(dto.getCpf());
        return ResponseEntity.ok(message);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        String message = passwordResetService.redefinirSenhaComValidacao(dto.getCpf(), dto.getCode(), dto.getNewPassword());
        return ResponseEntity.ok(message);
    }
} 