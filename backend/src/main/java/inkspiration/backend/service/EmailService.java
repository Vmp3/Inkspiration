package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@inkspiration.com}")
    private String fromEmail;

    public void sendPasswordResetCode(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Inkspiration - Código de Recuperação de Senha");
            
            String htmlContent = createPasswordResetEmailTemplate(userName, code);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            System.out.println("Email de recuperação enviado para: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email de recuperação");
        }
    }

    public void sendPasswordResetConfirmation(String toEmail, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Inkspiration - Senha Alterada com Sucesso");
            
            String htmlContent = createPasswordResetConfirmationTemplate(userName);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            System.out.println("Email de confirmação enviado para: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    }

    public void sendTwoFactorRecoveryCode(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Inkspiration - Código de Recuperação 2FA");
            
            String htmlContent = createTwoFactorRecoveryTemplate(userName, code);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            System.out.println("Email de recuperação 2FA enviado para: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de recuperação 2FA: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email de recuperação 2FA");
        }
    }

    public void sendEmailVerification(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Inkspiration - Verificação de Email");
            
            String htmlContent = createEmailVerificationTemplate(userName, code);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            System.out.println("Email de verificação enviado para: " + toEmail);
            System.out.println("🔐 CÓDIGO DE VERIFICAÇÃO: " + code + " 🔐");
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de verificação: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email de verificação");
        }
    }

    private String createPasswordResetEmailTemplate(String userName, String code) {
        return String.format("""
            <!DOCTYPE html>
            <html lang=\"pt-BR\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <title>Código de Recuperação - Inkspiration</title>
            </head>
            <body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff;\">
                <div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; border: 1px solid #eee;\">
                    <!-- Header -->
                    <div style=\"background: #111; padding: 32px 30px; text-align: center; border-radius: 8px 8px 0 0;\">
                        <h1 style=\"color: #fff; margin: 0; font-size: 28px; font-weight: 700; letter-spacing: 1px;\">
                             Inkspiration
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style=\"padding: 40px 30px;\">
                        <h2 style=\"color: #111; margin: 0 0 20px 0; font-size: 22px; font-weight: 600;\">
                            Olá, %s!
                        </h2>
                        
                        <p style=\"color: #222; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;\">
                            Recebemos sua solicitação para recuperar a senha de sua conta no Inkspiration.
                        </p>
                        
                        <!-- Code Box -->
                        <div style=\"background-color: #fafafa; border: 2px solid #111; border-radius: 10px; padding: 28px; text-align: center; margin: 30px 0;\">
                            <p style=\"color: #111; font-size: 13px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 1px; font-weight: 600;\">
                                SEU CÓDIGO DE VERIFICAÇÃO
                            </p>
                            <div style=\"background-color: #111; color: #fff; font-size: 32px; font-weight: bold; padding: 18px; border-radius: 8px; letter-spacing: 8px; font-family: 'Courier New', monospace; display: inline-block; min-width: 180px;\">
                                %s
                            </div>
                            <p style=\"color: #888; font-size: 12px; margin: 15px 0 0 0;\">
                                ⏰ Este código expira em <strong>15 minutos</strong>
                            </p>
                        </div>
                        
                        <div style=\"background-color: #f6f6f6; border-left: 4px solid #111; padding: 15px; margin: 30px 0; border-radius: 4px;\">
                            <p style=\"color: #222; font-size: 14px; margin: 0; line-height: 1.5;\">
                                <strong>⚠️ Importante:</strong> Se você não solicitou esta recuperação, ignore este email. Sua conta permanece segura.
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style=\"background-color: #fafafa; padding: 24px; text-align: center; border-top: 1px solid #eee; border-radius: 0 0 8px 8px;\">
                        <p style=\"color: #888; font-size: 14px; margin: 0 0 10px 0;\">
                            Atenciosamente,<br>
                            <strong>Equipe Inkspiration</strong>
                        </p>
                        <p style=\"color: #bbb; font-size: 12px; margin: 0;\">
                            Este é um email automático, não responda a esta mensagem.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, code);
    }

    private String createPasswordResetConfirmationTemplate(String userName) {
        return String.format("""
            <!DOCTYPE html>
            <html lang=\"pt-BR\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <title>Senha Alterada - Inkspiration</title>
            </head>
            <body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff;\">
                <div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; border: 1px solid #eee;\">
                    <!-- Header -->
                    <div style=\"background: #111; padding: 32px 30px; text-align: center; border-radius: 8px 8px 0 0;\">
                        <h1 style=\"color: #fff; margin: 0; font-size: 28px; font-weight: 700; letter-spacing: 1px;\">
                            Inkspiration
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style=\"padding: 40px 30px;\">
                        <div style=\"text-align: center; margin-bottom: 30px;\">
                            <div style=\"background-color: #e6e6e6; color: #111; font-size: 48px; padding: 20px; border-radius: 50%; display: inline-block; width: 80px; height: 80px; line-height: 80px;\">
                                ✅
                            </div>
                        </div>
                        
                        <h2 style=\"color: #111; margin: 0 0 20px 0; font-size: 22px; font-weight: 600; text-align: center;\">
                            Senha Alterada com Sucesso!
                        </h2>
                        
                        <p style=\"color: #222; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;\">
                            Olá <strong>%s</strong>,
                        </p>
                        
                        <p style=\"color: #222; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;\">
                            Sua senha foi alterada com sucesso no Inkspiration. Agora você pode fazer login com sua nova senha.
                        </p>
                        
                        <div style=\"background-color: #f6f6f6; border-left: 4px solid #111; padding: 15px; margin: 30px 0; border-radius: 4px;\">
                            <p style=\"color: #222; font-size: 14px; margin: 0; line-height: 1.5;\">
                                <strong>🔒 Segurança:</strong> Se você não realizou esta alteração, entre em contato conosco imediatamente através do suporte.
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style=\"background-color: #fafafa; padding: 24px; text-align: center; border-top: 1px solid #eee; border-radius: 0 0 8px 8px;\">
                        <p style=\"color: #888; font-size: 14px; margin: 0 0 10px 0;\">
                            Atenciosamente,<br>
                            <strong>Equipe Inkspiration</strong>
                        </p>
                        <p style=\"color: #bbb; font-size: 12px; margin: 0;\">
                            Este é um email automático, não responda a esta mensagem.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
    }

    private String createTwoFactorRecoveryTemplate(String userName, String code) {
        return String.format("""
            <!DOCTYPE html>
            <html lang=\"pt-BR\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <title>Código de Recuperação 2FA - Inkspiration</title>
            </head>
            <body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff;\">
                <div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; border: 1px solid #eee;\">
                    <!-- Header -->
                    <div style=\"background: #111; padding: 32px 30px; text-align: center; border-radius: 8px 8px 0 0;\">
                        <h1 style=\"color: #fff; margin: 0; font-size: 28px; font-weight: 700; letter-spacing: 1px;\">
                             Inkspiration
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style=\"padding: 40px 30px;\">
                        <h2 style=\"color: #111; margin: 0 0 20px 0; font-size: 22px; font-weight: 600;\">
                            Olá, %s!
                        </h2>
                        
                        <p style=\"color: #222; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;\">
                            Recebemos sua solicitação para recuperar o código de recuperação 2FA para sua conta no Inkspiration.
                        </p>
                        
                        <!-- Code Box -->
                        <div style=\"background-color: #fafafa; border: 2px solid #111; border-radius: 10px; padding: 28px; text-align: center; margin: 30px 0;\">
                            <p style=\"color: #111; font-size: 13px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 1px; font-weight: 600;\">
                                SEU CÓDIGO DE VERIFICAÇÃO
                            </p>
                            <div style=\"background-color: #111; color: #fff; font-size: 32px; font-weight: bold; padding: 18px; border-radius: 8px; letter-spacing: 8px; font-family: 'Courier New', monospace; display: inline-block; min-width: 180px;\">
                                %s
                            </div>
                            <p style=\"color: #888; font-size: 12px; margin: 15px 0 0 0;\">
                                ⏰ Este código expira em <strong>15 minutos</strong>
                            </p>
                        </div>
                        
                        <div style=\"background-color: #f6f6f6; border-left: 4px solid #111; padding: 15px; margin: 30px 0; border-radius: 4px;\">
                            <p style=\"color: #222; font-size: 14px; margin: 0; line-height: 1.5;\">
                                <strong>⚠️ Importante:</strong> Se você não solicitou esta recuperação, ignore este email. Sua conta permanece segura.
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style=\"background-color: #fafafa; padding: 24px; text-align: center; border-top: 1px solid #eee; border-radius: 0 0 8px 8px;\">
                        <p style=\"color: #888; font-size: 14px; margin: 0 0 10px 0;\">
                            Atenciosamente,<br>
                            <strong>Equipe Inkspiration</strong>
                        </p>
                        <p style=\"color: #bbb; font-size: 12px; margin: 0;\">
                            Este é um email automático, não responda a esta mensagem.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, code);
    }

    private String createEmailVerificationTemplate(String userName, String code) {
        return String.format("""
            <!DOCTYPE html>
            <html lang=\"pt-BR\">
            <head>
                <meta charset=\"UTF-8\">
                <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">
                <title>Verificação de Email - Inkspiration</title>
            </head>
            <body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fff;\">
                <div style=\"max-width: 600px; margin: 0 auto; background-color: #fff; border: 1px solid #eee;\">
                    <!-- Header -->
                    <div style=\"background: #111; padding: 32px 30px; text-align: center; border-radius: 8px 8px 0 0;\">
                        <h1 style=\"color: #fff; margin: 0; font-size: 28px; font-weight: 700; letter-spacing: 1px;\">
                             Inkspiration
                        </h1>
                    </div>
                    
                    <!-- Content -->
                    <div style=\"padding: 40px 30px;\">
                        <h2 style=\"color: #111; margin: 0 0 20px 0; font-size: 22px; font-weight: 600;\">
                            Bem-vindo, %s!
                        </h2>
                        
                        <p style=\"color: #222; font-size: 16px; line-height: 1.6; margin: 0 0 30px 0;\">
                            Obrigado por se cadastrar no Inkspiration! Para finalizar seu cadastro, insira o código de verificação abaixo no aplicativo.
                        </p>
                        
                        <!-- Code Box -->
                        <div style=\"background-color: #fafafa; border: 2px solid #111; border-radius: 10px; padding: 28px; text-align: center; margin: 30px 0;\">
                            <p style=\"color: #111; font-size: 13px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 1px; font-weight: 600;\">
                                SEU CÓDIGO DE VERIFICAÇÃO
                            </p>
                            <div style=\"background-color: #111; color: #fff; font-size: 32px; font-weight: bold; padding: 18px; border-radius: 8px; letter-spacing: 8px; font-family: 'Courier New', monospace; display: inline-block; min-width: 180px;\">
                                %s
                            </div>
                            <p style=\"color: #888; font-size: 12px; margin: 15px 0 0 0;\">
                                ⏰ Este código expira em <strong>15 minutos</strong>
                            </p>
                        </div>
                        
                        <div style=\"background-color: #f6f6f6; border-left: 4px solid #111; padding: 15px; margin: 30px 0; border-radius: 4px;\">
                            <p style=\"color: #222; font-size: 14px; margin: 0; line-height: 1.5;\">
                                <strong>⚠️ Importante:</strong> Se você não criou uma conta no Inkspiration, ignore este email.
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style=\"background-color: #fafafa; padding: 24px; text-align: center; border-top: 1px solid #eee; border-radius: 0 0 8px 8px;\">
                        <p style=\"color: #888; font-size: 14px; margin: 0 0 10px 0;\">
                            Atenciosamente,<br>
                            <strong>Equipe Inkspiration</strong>
                        </p>
                        <p style=\"color: #bbb; font-size: 12px; margin: 0;\">
                            Este é um email automático, não responda a esta mensagem.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, code);
    }
} 