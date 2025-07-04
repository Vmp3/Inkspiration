package inkspiration.backend.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import inkspiration.backend.dto.UsuarioDTO;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.exception.UsuarioException;
import inkspiration.backend.exception.emailverification.EmailVerificationCriacaoUsuarioException;
import inkspiration.backend.exception.emailverification.EmailVerificationEnvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationReenvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationValidacaoException;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UsuarioService usuarioService;

    // Armazenamento em memória para dados temporários de verificação
    private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();

    public void requestEmailVerificationComValidacao(UsuarioDTO usuarioDTO) {
        try {
            requestEmailVerification(usuarioDTO);
        } catch (UsuarioException.EmailJaExisteException e) {
            throw new EmailVerificationValidacaoException("Email já cadastrado");
        } catch (UsuarioException.CpfJaExisteException e) {
            throw new EmailVerificationValidacaoException("CPF já cadastrado");
        } catch (Exception e) {
            throw new EmailVerificationEnvioException("Erro ao enviar email de verificação: " + e.getMessage());
        }
    }

    public void requestEmailVerification(UsuarioDTO usuarioDTO) {
        // Verificar se o email ou CPF já existem
        if (usuarioService.buscarPorEmailOptional(usuarioDTO.getEmail()) != null) {
            throw new UsuarioException.EmailJaExisteException("Email já cadastrado");
        }
        
        String cpfLimpo = usuarioDTO.getCpf().replaceAll("[^0-9]", "");
        if (usuarioService.buscarPorCpfOptional(cpfLimpo) != null) {
            throw new UsuarioException.CpfJaExisteException("CPF já cadastrado");
        }

        // Gerar código de verificação único
        String verificationCode = generateVerificationCode();
        
        // Armazenar dados temporários por 15 minutos
        PendingRegistration pendingReg = new PendingRegistration(
            usuarioDTO, 
            verificationCode, 
            LocalDateTime.now().plusMinutes(15)
        );
        
        // Usar o email como chave primária
        pendingRegistrations.put(usuarioDTO.getEmail(), pendingReg);
        
        // Enviar email de verificação
        try {
            emailService.sendEmailVerification(usuarioDTO.getEmail(), usuarioDTO.getNome(), verificationCode);
        } catch (Exception e) {
            // Remover da memória se falhar ao enviar email
            pendingRegistrations.remove(usuarioDTO.getEmail());
            throw new RuntimeException("Erro ao enviar email de verificação: " + e.getMessage());
        }
    }

    public Usuario verifyEmailAndCreateUserComValidacao(String email, String code) {
        try {
            return verifyEmailAndCreateUser(email, code);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Código de verificação não encontrado") || 
                e.getMessage().contains("Código de verificação expirado") ||
                e.getMessage().contains("Código de verificação inválido")) {
                throw new EmailVerificationValidacaoException(e.getMessage());
            } else if (e.getMessage().contains("Erro ao criar usuário")) {
                throw new EmailVerificationCriacaoUsuarioException(e.getMessage());
            } else {
                throw new EmailVerificationCriacaoUsuarioException("Erro durante a verificação do email: " + e.getMessage());
            }
        }
    }

    public Usuario verifyEmailAndCreateUser(String email, String code) {
        cleanExpiredRegistrations();
        
        PendingRegistration pendingReg = pendingRegistrations.get(email);
        
        if (pendingReg == null) {
            throw new RuntimeException("Código de verificação não encontrado ou expirado");
        }
        
        if (pendingReg.isExpired()) {
            pendingRegistrations.remove(email);
            throw new RuntimeException("Código de verificação expirado");
        }
        
        if (!pendingReg.getVerificationCode().equals(code)) {
            throw new RuntimeException("Código de verificação inválido");
        }
        
        // Criar o usuário
        try {
            Usuario usuario = usuarioService.criar(pendingReg.getUsuarioDTO());
            
            // Definir data de criação
            usuario.setCreatedAt(LocalDateTime.now());
            usuarioService.salvar(usuario);
            
            // Remover dados temporários após sucesso
            pendingRegistrations.remove(email);
            
            return usuario;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
    }

    public void resendVerificationCodeComValidacao(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new EmailVerificationValidacaoException("Email é obrigatório");
            }
            resendVerificationCode(email);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Nenhuma solicitação de verificação encontrada")) {
                throw new EmailVerificationValidacaoException(e.getMessage());
            } else {
                throw new EmailVerificationReenvioException("Erro ao reenviar código de verificação: " + e.getMessage());
            }
        }
    }

    public void resendVerificationCode(String email) {
        PendingRegistration pendingReg = pendingRegistrations.get(email);
        
        if (pendingReg == null) {
            throw new RuntimeException("Nenhuma solicitação de verificação encontrada para este email");
        }
        
        String newCode = generateVerificationCode();
        
        pendingReg.setVerificationCode(newCode);
        pendingReg.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        
        emailService.sendEmailVerification(
            email, 
            pendingReg.getUsuarioDTO().getNome(), 
            newCode
        );
    }

    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private void cleanExpiredRegistrations() {
        pendingRegistrations.entrySet().removeIf(entry -> {
            PendingRegistration reg = entry.getValue();
            return reg.isExpired();
        });
    }

    private static class PendingRegistration {
        private final UsuarioDTO usuarioDTO;
        private String verificationCode;
        private LocalDateTime expiresAt;

        public PendingRegistration(UsuarioDTO usuarioDTO, String verificationCode, LocalDateTime expiresAt) {
            this.usuarioDTO = usuarioDTO;
            this.verificationCode = verificationCode;
            this.expiresAt = expiresAt;
        }

        public UsuarioDTO getUsuarioDTO() {
            return usuarioDTO;
        }

        public String getVerificationCode() {
            return verificationCode;
        }

        public void setVerificationCode(String verificationCode) {
            this.verificationCode = verificationCode;
        }

        public void setExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
} 