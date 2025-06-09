package inkspiration.backend.controller;

import inkspiration.backend.service.TwoFactorAuthService;
import inkspiration.backend.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/two-factor")
@CrossOrigin(origins = "*")
public class TwoFactorAuthController {

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;

    @Autowired
    private JwtService jwtService;

    /**
     * Gera QR code para configurar 2FA
     */
    @PostMapping("/generate-qr")
    public ResponseEntity<?> generateQRCode(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);

            Map<String, String> qrData = twoFactorAuthService.generateQRCodeAndSecret(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("qrCode", qrData.get("qrCode"));
            response.put("secretKey", qrData.get("secretKey"));
            response.put("issuer", qrData.get("issuer"));
            response.put("accountName", qrData.get("accountName"));
            response.put("otpAuthUrl", qrData.get("otpAuthUrl"));
            response.put("message", "QR Code gerado com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao gerar QR Code: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Ativa 2FA após validar código
     */
    @PostMapping("/enable")
    public ResponseEntity<?> enableTwoFactor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);
            
            Integer verificationCode = request.get("code");
            if (verificationCode == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Código de verificação é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            boolean success = twoFactorAuthService.enableTwoFactor(userId, verificationCode);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Autenticação de dois fatores ativada com sucesso");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Código de verificação inválido");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao ativar 2FA: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Desativa 2FA após validar código
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disableTwoFactor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);
            
            Integer verificationCode = request.get("code");
            if (verificationCode == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Código de verificação é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            boolean success = twoFactorAuthService.disableTwoFactor(userId, verificationCode);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Autenticação de dois fatores desativada com sucesso");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Código de verificação inválido");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao desativar 2FA: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Verifica status do 2FA
     */
    @GetMapping("/status")
    public ResponseEntity<?> getTwoFactorStatus(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);

            boolean enabled = twoFactorAuthService.isTwoFactorEnabled(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("enabled", enabled);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao verificar status do 2FA: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Valida código durante o login
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateCode(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);
            
            Integer verificationCode = request.get("code");
            if (verificationCode == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Código de verificação é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            boolean valid = twoFactorAuthService.validateCode(userId, verificationCode);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", valid);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao validar código: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Envia código de recuperação por email
     */
    @PostMapping("/send-recovery-code")
    public ResponseEntity<?> sendRecoveryCode(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);

            boolean success = twoFactorAuthService.sendRecoveryCodeByEmail(userId);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Código de recuperação enviado para seu email");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Erro ao enviar código de recuperação");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao enviar código de recuperação: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Desativa 2FA usando código de recuperação por email
     */
    @PostMapping("/disable-with-recovery")
    public ResponseEntity<?> disableWithRecoveryCode(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtService.getUserIdFromToken(token);
            
            String recoveryCode = request.get("recoveryCode");
            if (recoveryCode == null || recoveryCode.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Código de recuperação é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            boolean success = twoFactorAuthService.disableTwoFactorWithRecoveryCode(userId, recoveryCode);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Autenticação de dois fatores desativada com sucesso");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Código de recuperação inválido ou expirado");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro ao desativar 2FA: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 