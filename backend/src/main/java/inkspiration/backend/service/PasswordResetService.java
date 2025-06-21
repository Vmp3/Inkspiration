package inkspiration.backend.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.entities.PasswordResetCode;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.entities.UsuarioAutenticar;
import inkspiration.backend.exception.UsuarioValidationException;
import inkspiration.backend.exception.passwordreset.PasswordResetGeracaoException;
import inkspiration.backend.exception.passwordreset.PasswordResetProcessamentoException;
import inkspiration.backend.exception.passwordreset.PasswordResetValidacaoException;
import inkspiration.backend.repository.PasswordResetCodeRepository;
import inkspiration.backend.repository.UsuarioRepository;
import inkspiration.backend.util.Hashing;
import inkspiration.backend.util.PasswordValidator;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetCodeRepository passwordResetCodeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public String generatePasswordResetCode(String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        Usuario usuario = usuarioRepository.findByCpf(cleanCpf)
            .orElseThrow(() -> new PasswordResetGeracaoException("Usuário não encontrado com o CPF informado"));

        // Verificar limite de tentativas (máximo 3 códigos a cada 15 minutos)
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        int recentCodes = passwordResetCodeRepository.countRecentCodesByCpf(cleanCpf, fifteenMinutesAgo);
        
        if (recentCodes >= 3) {
            throw new PasswordResetGeracaoException("Muitas tentativas. Tente novamente em 15 minutos");
        }

        // Marcar todos os códigos anteriores como usados
        passwordResetCodeRepository.markAllAsUsedByCpf(cleanCpf);

        // Gerar código de 6 dígitos
        String code = String.format("%06d", random.nextInt(1000000));
        
        // Definir expiração para 15 minutos
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(15);

        // Salvar código no banco
        PasswordResetCode resetCode = new PasswordResetCode(cleanCpf, code, now, expiresAt);
        passwordResetCodeRepository.save(resetCode);

        // Imprimir no console para desenvolvimento
        System.out.println("=== CÓDIGO DE RECUPERAÇÃO ===");
        System.out.println("CPF: " + cleanCpf);
        System.out.println("Código: " + code);
        System.out.println("Email: " + usuario.getEmail());
        System.out.println("Expira em: " + expiresAt);
        System.out.println("=============================");

        // Enviar email
        try {
            emailService.sendPasswordResetCode(usuario.getEmail(), usuario.getNome(), code);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new PasswordResetProcessamentoException("Falha ao enviar email de recuperação. Tente novamente.");
        }

        return "Código de recuperação enviado para " + maskEmail(usuario.getEmail());
    }

    @Transactional
    public void resetPassword(String cpf, String code, String newPassword) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        
        // Validar nova senha
        if (!PasswordValidator.isValid(newPassword)) {
            throw new UsuarioValidationException.SenhaInvalidaException(PasswordValidator.getPasswordRequirements());
        }
        
        // Verificar se o código é válido
        PasswordResetCode resetCode = passwordResetCodeRepository
            .findByCpfAndCodeAndUsedFalse(cleanCpf, code)
            .orElseThrow(() -> new PasswordResetValidacaoException("Código inválido ou expirado"));

        if (!resetCode.isValid()) {
            throw new PasswordResetValidacaoException("Código expirado");
        }

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByCpf(cleanCpf)
            .orElseThrow(() -> new PasswordResetValidacaoException("Usuário não encontrado"));

        // Atualizar senha
        UsuarioAutenticar usuarioAuth = usuario.getUsuarioAutenticar();
        if (usuarioAuth != null) {
            usuarioAuth.setSenha(passwordEncoder.encode(newPassword));
            usuario.setUsuarioAutenticar(usuarioAuth);
        }

        // Marcar código como usado
        resetCode.setUsed(true);
        passwordResetCodeRepository.save(resetCode);

        // Salvar usuário com nova senha
        usuarioRepository.save(usuario);

        // Enviar email de confirmação
        try {
            emailService.sendPasswordResetConfirmation(usuario.getEmail(), usuario.getNome());
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    }

    @Transactional
    public void cleanupExpiredCodes() {
        passwordResetCodeRepository.deleteExpiredCodes(LocalDateTime.now());
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return "***@***.***";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return "***@***.***";
        }
        
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return "**" + localPart.charAt(localPart.length() - 1) + domainPart;
        } else {
            return localPart.substring(0, 2) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
        }
    }

    public String gerarCodigoRecuperacaoComValidacao(String cpf) {
        try {
            return generatePasswordResetCode(cpf);
        } catch (PasswordResetGeracaoException | PasswordResetProcessamentoException e) {
            throw e;
        } catch (Exception e) {
            throw new PasswordResetProcessamentoException("Erro interno do servidor. Tente novamente mais tarde.");
        }
    }

    public String redefinirSenhaComValidacao(String cpf, String code, String newPassword) {
        try {
            resetPassword(cpf, code, newPassword);
            return "Senha redefinida com sucesso";
        } catch (PasswordResetValidacaoException e) {
            throw e;
        } catch (Exception e) {
            throw new PasswordResetProcessamentoException("Erro interno do servidor. Tente novamente mais tarde.");
        }
    }
} 