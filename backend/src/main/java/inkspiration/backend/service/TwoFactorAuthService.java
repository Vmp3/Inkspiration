package inkspiration.backend.service;

import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.TwoFactorRecoveryCode;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.repository.TwoFactorRecoveryCodeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class TwoFactorAuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TwoFactorRecoveryCodeRepository twoFactorRecoveryCodeRepository;

    @Autowired
    private EmailService emailService;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /**
     * Gera uma nova chave secreta para 2FA e retorna QR code e secret key
     */
    public Map<String, String> generateQRCodeAndSecret(Long userId) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        
        // Gera uma nova chave secreta usando GoogleAuth
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        
        // Salva o secret no banco (mas não ativa o 2FA ainda)
        usuario.setTwoFactorSecret(secretKey);
        usuarioRepository.save(usuario);
        
        // Gera URL otpauth diretamente (sem usar QRGenerator)
        String issuer = "Inkspiration";
        String accountName = usuario.getEmail();
        String otpAuthUrl = String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
            issuer, accountName, secretKey, issuer
        );
        
        System.out.println("OTP Auth URL gerada: " + otpAuthUrl);
        
        // Gera QR Code como imagem diretamente com a URL otpauth
        String qrCodeBase64 = generateQRCodeImage(otpAuthUrl);
        
        // Retorna tanto o QR code quanto o secret key
        Map<String, String> result = new HashMap<>();
        result.put("qrCode", "data:image/png;base64," + qrCodeBase64);
        result.put("secretKey", secretKey);
        result.put("issuer", issuer);
        result.put("accountName", accountName);
        result.put("otpAuthUrl", otpAuthUrl);
        
        return result;
    }

    /**
     * Método legacy para compatibilidade
     */
    public String generateQRCode(Long userId) throws Exception {
        Map<String, String> result = generateQRCodeAndSecret(userId);
        return result.get("qrCode");
    }

    /**
     * Ativa o 2FA após validar o código fornecido pelo usuário
     */
    public boolean enableTwoFactor(Long userId, int verificationCode) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        
        if (usuario.getTwoFactorSecret() == null) {
            return false;
        }

        // Valida o código fornecido pelo usuário usando GoogleAuth
        boolean isCodeValid = gAuth.authorize(usuario.getTwoFactorSecret(), verificationCode);
        
        if (isCodeValid) {
            usuario.setTwoFactorEnabled(true);
            usuarioRepository.save(usuario);
            return true;
        }
        
        return false;
    }

    /**
     * Desativa o 2FA após validar o código
     */
    public boolean disableTwoFactor(Long userId, int verificationCode) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.getTwoFactorEnabled() || usuario.getTwoFactorSecret() == null) {
            return false;
        }

        // Valida o código fornecido pelo usuário
        boolean isCodeValid = gAuth.authorize(usuario.getTwoFactorSecret(), verificationCode);
        
        if (isCodeValid) {
            usuario.setTwoFactorEnabled(false);
            usuario.setTwoFactorSecret(null);
            usuarioRepository.save(usuario);
            return true;
        }
        
        return false;
    }

    /**
     * Valida um código de verificação para login
     */
    public boolean validateCode(Long userId, int verificationCode) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.getTwoFactorEnabled() || usuario.getTwoFactorSecret() == null) {
            return false;
        }

        return gAuth.authorize(usuario.getTwoFactorSecret(), verificationCode);
    }

    /**
     * Verifica se o usuário tem 2FA habilitado
     */
    public boolean isTwoFactorEnabled(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        return usuario.getTwoFactorEnabled() != null && usuario.getTwoFactorEnabled();
    }

    /**
     * Gera e envia código de recuperação por email
     */
    public boolean sendRecoveryCodeByEmail(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.getTwoFactorEnabled()) {
            return false;
        }

        // Limpa códigos antigos do usuário
        twoFactorRecoveryCodeRepository.deleteByUserId(userId);
        
        // Gera código de 6 dígitos
        String code = generateRecoveryCode();
        
        // Define expiração para 15 minutos
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        
        // Salva o código no banco
        TwoFactorRecoveryCode recoveryCode = new TwoFactorRecoveryCode(userId, code, expiresAt);
        twoFactorRecoveryCodeRepository.save(recoveryCode);
        
        // Envia por email
        try {
            emailService.sendTwoFactorRecoveryCode(usuario.getEmail(), usuario.getNome(), code);
            return true;
        } catch (Exception e) {
            // Remove o código se falhou ao enviar email
            twoFactorRecoveryCodeRepository.delete(recoveryCode);
            return false;
        }
    }

    /**
     * Valida código de recuperação enviado por email
     */
    public boolean validateRecoveryCode(Long userId, String code) {
        // Limpa códigos expirados
        twoFactorRecoveryCodeRepository.deleteExpiredCodes(LocalDateTime.now());
        
        Optional<TwoFactorRecoveryCode> recoveryCodeOpt = 
            twoFactorRecoveryCodeRepository.findByUserIdAndCodeAndUsedFalse(userId, code);
        
        if (!recoveryCodeOpt.isPresent()) {
            return false;
        }
        
        TwoFactorRecoveryCode recoveryCode = recoveryCodeOpt.get();
        
        if (recoveryCode.isExpired()) {
            return false;
        }
        
        // Marca como usado
        recoveryCode.setUsed(true);
        twoFactorRecoveryCodeRepository.save(recoveryCode);
        
        return true;
    }

    /**
     * Desativa 2FA usando código de recuperação por email
     */
    public boolean disableTwoFactorWithRecoveryCode(Long userId, String recoveryCode) {
        if (!validateRecoveryCode(userId, recoveryCode)) {
            return false;
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setTwoFactorEnabled(false);
        usuario.setTwoFactorSecret(null);
        usuarioRepository.save(usuario);
        
        // Limpa todos os códigos de recuperação do usuário
        twoFactorRecoveryCodeRepository.deleteByUserId(userId);
        
        return true;
    }

    /**
     * Gera código numérico de 6 dígitos
     */
    private String generateRecoveryCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Gera QR Code como imagem em base64
     */
    private String generateQRCodeImage(String text) throws WriterException, IOException {
        int width = 300;
        int height = 300;
        
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
} 