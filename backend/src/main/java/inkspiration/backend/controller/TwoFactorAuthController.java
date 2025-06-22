package inkspiration.backend.controller;

import inkspiration.backend.service.TwoFactorAuthService;

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

    /**
     * Gera QR code para configurar 2FA
     */
    @PostMapping("/generate-qr")
    public ResponseEntity<Map<String, Object>> generateQRCode(@RequestHeader("Authorization") String authHeader) {
        Map<String, String> qrData = twoFactorAuthService.gerarQRCodeComValidacao(authHeader);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("qrCode", qrData.get("qrCode"));
        response.put("secretKey", qrData.get("secretKey"));
        response.put("issuer", qrData.get("issuer"));
        response.put("accountName", qrData.get("accountName"));
        response.put("otpAuthUrl", qrData.get("otpAuthUrl"));
        response.put("message", "QR Code gerado com sucesso");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Ativa 2FA após validar código
     */
    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enableTwoFactor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        
        Integer verificationCode = request.get("code");
        boolean success = twoFactorAuthService.ativarTwoFactorComValidacao(authHeader, verificationCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", "Autenticação de dois fatores ativada com sucesso");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Desativa 2FA após validar código
     */
    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableTwoFactor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        
        Integer verificationCode = request.get("code");
        boolean success = twoFactorAuthService.desativarTwoFactorComValidacao(authHeader, verificationCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", "Autenticação de dois fatores desativada com sucesso");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica status do 2FA
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getTwoFactorStatus(@RequestHeader("Authorization") String authHeader) {
        boolean enabled = twoFactorAuthService.obterStatusTwoFactorComValidacao(authHeader);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("enabled", enabled);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Valida código durante o login
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateCode(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Integer> request) {
        
        Integer verificationCode = request.get("code");
        boolean valid = twoFactorAuthService.validarCodigoComValidacao(authHeader, verificationCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("valid", valid);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Envia código de recuperação por email
     */
    @PostMapping("/send-recovery-code")
    public ResponseEntity<Map<String, Object>> sendRecoveryCode(@RequestHeader("Authorization") String authHeader) {
        boolean success = twoFactorAuthService.enviarCodigoRecuperacaoComValidacao(authHeader);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", "Código de recuperação enviado para seu email");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Desativa 2FA usando código de recuperação por email
     */
    @PostMapping("/disable-with-recovery")
    public ResponseEntity<Map<String, Object>> disableWithRecoveryCode(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        
        String recoveryCode = request.get("recoveryCode");
        boolean success = twoFactorAuthService.desativarComCodigoRecuperacaoComValidacao(authHeader, recoveryCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", "Autenticação de dois fatores desativada com sucesso");
        
        return ResponseEntity.ok(response);
    }
} 