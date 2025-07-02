package inkspiration.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import inkspiration.backend.exception.authentication.AuthenticationFailedException;
import inkspiration.backend.exception.authentication.InvalidTwoFactorCodeException;
import inkspiration.backend.exception.authentication.TwoFactorRequiredException;
import inkspiration.backend.exception.authentication.UserInactiveException;
import inkspiration.backend.exception.authentication.UserNotFoundException;
import inkspiration.backend.exception.usuario.InvalidProfileImageException;
import inkspiration.backend.exception.usuario.TokenValidationException;
import inkspiration.backend.exception.usuario.UserAccessDeniedException;
import inkspiration.backend.exception.usuario.TelefoneValidationException;
import inkspiration.backend.exception.agendamento.AgendamentoNaoAutorizadoException;
import inkspiration.backend.exception.agendamento.AutoAgendamentoException;
import inkspiration.backend.exception.agendamento.CancelamentoNaoPermitidoException;
import inkspiration.backend.exception.agendamento.DataInvalidaAgendamentoException;
import inkspiration.backend.exception.agendamento.HorarioConflitanteException;
import inkspiration.backend.exception.agendamento.ProfissionalIndisponivelException;
import inkspiration.backend.exception.agendamento.TipoServicoInvalidoException;
import inkspiration.backend.exception.agendamento.TokenInvalidoException;
import inkspiration.backend.exception.profissional.DadosCompletosProfissionalException;
import inkspiration.backend.exception.profissional.DisponibilidadeProcessamentoException;
import inkspiration.backend.exception.profissional.ProfissionalAcessoNegadoException;
import inkspiration.backend.exception.profissional.ProfissionalJaExisteException;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.exception.profissional.TipoServicoInvalidoProfissionalException;
import inkspiration.backend.exception.profissional.EnderecoNaoEncontradoException;
import inkspiration.backend.exception.twofactor.CodigoRecuperacaoException;
import inkspiration.backend.exception.twofactor.CodigoVerificacaoInvalidoException;
import inkspiration.backend.exception.twofactor.QRCodeGeracaoException;
import inkspiration.backend.exception.twofactor.TwoFactorAtivacaoException;
import inkspiration.backend.exception.twofactor.TwoFactorDesativacaoException;
import inkspiration.backend.exception.twofactor.TwoFactorStatusException;
import inkspiration.backend.exception.portfolio.PortfolioAcessoNegadoException;
import inkspiration.backend.exception.portfolio.PortfolioAtualizacaoException;
import inkspiration.backend.exception.portfolio.PortfolioCriacaoException;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.portfolio.PortfolioRemocaoException;
import inkspiration.backend.exception.passwordreset.PasswordResetGeracaoException;
import inkspiration.backend.exception.passwordreset.PasswordResetProcessamentoException;
import inkspiration.backend.exception.passwordreset.PasswordResetValidacaoException;
import inkspiration.backend.exception.imagem.ImagemNaoEncontradaException;
import inkspiration.backend.exception.imagem.ImagemProcessamentoException;
import inkspiration.backend.exception.imagem.ImagemRemocaoException;
import inkspiration.backend.exception.imagem.ImagemSalvamentoException;
import inkspiration.backend.exception.emailverification.EmailVerificationCriacaoUsuarioException;
import inkspiration.backend.exception.emailverification.EmailVerificationEnvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationReenvioException;
import inkspiration.backend.exception.emailverification.EmailVerificationValidacaoException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeAcessoException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeCadastroException;
import inkspiration.backend.exception.disponibilidade.DisponibilidadeConsultaException;
import inkspiration.backend.exception.disponibilidade.TipoServicoInvalidoDisponibilidadeException;
import inkspiration.backend.exception.endereco.EnderecoValidacaoException;
import inkspiration.backend.exception.endereco.CepInvalidoException;
import inkspiration.backend.exception.endereco.EstadoInvalidoException;
import inkspiration.backend.exception.endereco.CidadeInvalidaException;
import inkspiration.backend.exception.avaliacao.AvaliacaoJaExisteException;
import inkspiration.backend.exception.avaliacao.AvaliacaoNaoEncontradaException;
import inkspiration.backend.exception.avaliacao.AvaliacaoNaoPermitidaException;
import inkspiration.backend.exception.agendamento.AgendamentoNaoEncontradoException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        UsuarioException.UsuarioNaoEncontradoException.class,
        UsuarioException.EmailJaExisteException.class,
        UsuarioException.CpfJaExisteException.class,
        UsuarioException.PermissaoNegadaException.class,
        UsuarioException.UsuarioInativoException.class,
        UsuarioException.AutenticacaoFalhouException.class
    })
    public ResponseEntity<Map<String, String>> handleUsuarioExceptions(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof UsuarioException.UsuarioNaoEncontradoException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof UsuarioException.EmailJaExisteException || 
                  ex instanceof UsuarioException.CpfJaExisteException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof UsuarioException.PermissaoNegadaException ||
                  ex instanceof UsuarioException.UsuarioInativoException) {
            status = HttpStatus.FORBIDDEN;
        } else if (ex instanceof UsuarioException.AutenticacaoFalhouException) {
            status = HttpStatus.UNAUTHORIZED;
        }
        
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler({
        UsuarioValidationException.NomeObrigatorioException.class,
        UsuarioValidationException.EmailObrigatorioException.class,
        UsuarioValidationException.EmailInvalidoException.class,
        UsuarioValidationException.CpfObrigatorioException.class,
        UsuarioValidationException.CpfInvalidoException.class,
        UsuarioValidationException.DataNascimentoObrigatoriaException.class,
        UsuarioValidationException.DataInvalidaException.class,
        UsuarioValidationException.SenhaObrigatoriaException.class,
        UsuarioValidationException.IdadeMinimaException.class,
        UsuarioValidationException.EnderecoObrigatorioException.class,
        UsuarioValidationException.TelefoneObrigatorioException.class,
        UsuarioValidationException.TelefoneInvalidoException.class
    })
    public ResponseEntity<Map<String, String>> handleUsuarioValidationExceptions(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Erro de validação");
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Acesso negado: você não tem permissão para realizar esta operação");
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TwoFactorRequiredException.class)
    public ResponseEntity<Map<String, Object>> handleTwoFactorRequiredException(TwoFactorRequiredException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("requiresTwoFactor", true);
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.PRECONDITION_REQUIRED);
    }

    @ExceptionHandler(InvalidTwoFactorCodeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTwoFactorCodeException(InvalidTwoFactorCodeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("requiresTwoFactor", true);
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<Map<String, String>> handleUserInactiveException(UserInactiveException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleUserAccessDeniedException(UserAccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TelefoneValidationException.class)
    public ResponseEntity<Map<String, String>> handleTelefoneValidationException(TelefoneValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProfileImageException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProfileImageException(InvalidProfileImageException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<Map<String, Object>> handleTokenValidationException(TokenValidationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", false);
        response.put("message", ex.getMessage());
        
        if (ex.getNewToken() != null) {
            response.put("newToken", ex.getNewToken());
        }
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Exceções de Agendamento
    @ExceptionHandler(ProfissionalIndisponivelException.class)
    public ResponseEntity<Map<String, String>> handleProfissionalIndisponivelException(ProfissionalIndisponivelException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "O profissional não está disponível para atendimento nesse horário. Por favor, consulte os horários de atendimento do profissional.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HorarioConflitanteException.class)
    public ResponseEntity<Map<String, String>> handleHorarioConflitanteException(HorarioConflitanteException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getMessage().contains("Você já possui outro agendamento")) {
            errors.put("error", ex.getMessage());
        } else {
            errors.put("error", "O profissional já possui outro agendamento nesse horário. Por favor, selecione outro horário disponível.");
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TipoServicoInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleTipoServicoInvalidoException(TipoServicoInvalidoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AutoAgendamentoException.class)
    public ResponseEntity<Map<String, String>> handleAutoAgendamentoException(AutoAgendamentoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Não é possível agendar um serviço consigo mesmo como profissional.");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataInvalidaAgendamentoException.class)
    public ResponseEntity<Map<String, String>> handleDataInvalidaAgendamentoException(DataInvalidaAgendamentoException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getMessage().contains("dia seguinte")) {
            errors.put("error", "Só é possível fazer agendamentos a partir do dia seguinte. Por favor, selecione uma data a partir do próximo dia.");
        } else if (ex.getMessage().contains("amanhã")) {
            errors.put("error", "Só é possível fazer agendamentos a partir de amanhã. Por favor, selecione uma data a partir do próximo dia.");
        } else {
            errors.put("error", ex.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AgendamentoNaoAutorizadoException.class)
    public ResponseEntity<Map<String, String>> handleAgendamentoNaoAutorizadoException(AgendamentoNaoAutorizadoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Você não tem permissão para acessar este agendamento.");
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CancelamentoNaoPermitidoException.class)
    public ResponseEntity<Map<String, String>> handleCancelamentoNaoPermitidoException(CancelamentoNaoPermitidoException ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex.getMessage().contains("3 dias de antecedência")) {
            errors.put("error", "O cancelamento só é permitido com no mínimo 3 dias de antecedência.");
        } else {
            errors.put("error", ex.getMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, String>> handleTokenInvalidoException(TokenInvalidoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    // Exceções de Profissional
    @ExceptionHandler(ProfissionalNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleProfissionalNaoEncontradoException(ProfissionalNaoEncontradoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfissionalJaExisteException.class)
    public ResponseEntity<Map<String, String>> handleProfissionalJaExisteException(ProfissionalJaExisteException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProfissionalAcessoNegadoException.class)
    public ResponseEntity<Map<String, String>> handleProfissionalAcessoNegadoException(ProfissionalAcessoNegadoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TipoServicoInvalidoProfissionalException.class)
    public ResponseEntity<Map<String, String>> handleTipoServicoInvalidoProfissionalException(TipoServicoInvalidoProfissionalException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisponibilidadeProcessamentoException.class)
    public ResponseEntity<Map<String, String>> handleDisponibilidadeProcessamentoException(DisponibilidadeProcessamentoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DadosCompletosProfissionalException.class)
    public ResponseEntity<Map<String, String>> handleDadosCompletosProfissionalException(DadosCompletosProfissionalException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EnderecoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleEnderecoNaoEncontradoException(EnderecoNaoEncontradoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    // Exceções de TwoFactor
    @ExceptionHandler(QRCodeGeracaoException.class)
    public ResponseEntity<Map<String, Object>> handleQRCodeGeracaoException(QRCodeGeracaoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CodigoVerificacaoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoVerificacaoInvalidoException(CodigoVerificacaoInvalidoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TwoFactorAtivacaoException.class)
    public ResponseEntity<Map<String, Object>> handleTwoFactorAtivacaoException(TwoFactorAtivacaoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TwoFactorDesativacaoException.class)
    public ResponseEntity<Map<String, Object>> handleTwoFactorDesativacaoException(TwoFactorDesativacaoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TwoFactorStatusException.class)
    public ResponseEntity<Map<String, Object>> handleTwoFactorStatusException(TwoFactorStatusException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CodigoRecuperacaoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoRecuperacaoException(CodigoRecuperacaoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceções de Portfolio
    @ExceptionHandler(PortfolioNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handlePortfolioNaoEncontradoException(PortfolioNaoEncontradoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PortfolioAcessoNegadoException.class)
    public ResponseEntity<Map<String, String>> handlePortfolioAcessoNegadoException(PortfolioAcessoNegadoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PortfolioCriacaoException.class)
    public ResponseEntity<Map<String, String>> handlePortfolioCriacaoException(PortfolioCriacaoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PortfolioAtualizacaoException.class)
    public ResponseEntity<Map<String, String>> handlePortfolioAtualizacaoException(PortfolioAtualizacaoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PortfolioRemocaoException.class)
    public ResponseEntity<Map<String, String>> handlePortfolioRemocaoException(PortfolioRemocaoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceções de PasswordReset
    @ExceptionHandler(PasswordResetGeracaoException.class)
    public ResponseEntity<String> handlePasswordResetGeracaoException(PasswordResetGeracaoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordResetValidacaoException.class)
    public ResponseEntity<String> handlePasswordResetValidacaoException(PasswordResetValidacaoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordResetProcessamentoException.class)
    public ResponseEntity<String> handlePasswordResetProcessamentoException(PasswordResetProcessamentoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Exceções de Imagem
    @ExceptionHandler(ImagemNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleImagemNaoEncontradaException(ImagemNaoEncontradaException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ImagemProcessamentoException.class)
    public ResponseEntity<Map<String, String>> handleImagemProcessamentoException(ImagemProcessamentoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImagemSalvamentoException.class)
    public ResponseEntity<Map<String, String>> handleImagemSalvamentoException(ImagemSalvamentoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImagemRemocaoException.class)
    public ResponseEntity<Map<String, String>> handleImagemRemocaoException(ImagemRemocaoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceções de EmailVerification
    @ExceptionHandler(EmailVerificationEnvioException.class)
    public ResponseEntity<Map<String, Object>> handleEmailVerificationEnvioException(EmailVerificationEnvioException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailVerificationValidacaoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailVerificationValidacaoException(EmailVerificationValidacaoException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailVerificationCriacaoUsuarioException.class)
    public ResponseEntity<Map<String, Object>> handleEmailVerificationCriacaoUsuarioException(EmailVerificationCriacaoUsuarioException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailVerificationReenvioException.class)
    public ResponseEntity<Map<String, Object>> handleEmailVerificationReenvioException(EmailVerificationReenvioException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceções de Disponibilidade
    @ExceptionHandler(DisponibilidadeCadastroException.class)
    public ResponseEntity<Map<String, String>> handleDisponibilidadeCadastroException(DisponibilidadeCadastroException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", "Erro de validação");
        errors.put("mensagem", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisponibilidadeAcessoException.class)
    public ResponseEntity<Map<String, String>> handleDisponibilidadeAcessoException(DisponibilidadeAcessoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", "Acesso negado");
        errors.put("mensagem", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DisponibilidadeConsultaException.class)
    public ResponseEntity<Map<String, String>> handleDisponibilidadeConsultaException(DisponibilidadeConsultaException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", "Erro de validação");
        errors.put("mensagem", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TipoServicoInvalidoDisponibilidadeException.class)
    public ResponseEntity<Map<String, String>> handleTipoServicoInvalidoDisponibilidadeException(TipoServicoInvalidoDisponibilidadeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("erro", "Tipo de serviço inválido");
        errors.put("mensagem", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleAuthenticationException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        
        if (ex instanceof BadCredentialsException) {
            errors.put("error", "Credenciais inválidas");
        } else if (ex.getMessage().contains("User is disabled")) {
            errors.put("error", "Usuário está inativo ou foi excluído do sistema");
        } else {
            errors.put("error", ex.getMessage());
        }
        
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DisponibilidadeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleHorarioInvalidoException(DisponibilidadeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("erro", "Erro de validação de horário");
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    // Exceções de Endereço
    @ExceptionHandler({
        EnderecoValidacaoException.class,
        CepInvalidoException.class,
        EstadoInvalidoException.class,
        CidadeInvalidaException.class
    })
    public ResponseEntity<Map<String, String>> handleEnderecoValidacaoExceptions(RuntimeException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Exceções de Avaliação
    @ExceptionHandler(AvaliacaoJaExisteException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoJaExisteException(AvaliacaoJaExisteException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AvaliacaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoNaoEncontradaException(AvaliacaoNaoEncontradaException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AvaliacaoNaoPermitidaException.class)
    public ResponseEntity<Map<String, String>> handleAvaliacaoNaoPermitidaException(AvaliacaoNaoPermitidaException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AgendamentoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleAgendamentoNaoEncontradoException(AgendamentoNaoEncontradoException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}