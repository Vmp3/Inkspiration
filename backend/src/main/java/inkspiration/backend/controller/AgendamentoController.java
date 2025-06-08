package inkspiration.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.service.AgendamentoService;

@RestController
@RequestMapping("/agendamentos")
@Validated
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;
    
    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }
    
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@Valid @RequestBody AgendamentoRequestDTO request) {
        try {
            Agendamento agendamento = agendamentoService.criarAgendamento(
                    request.getIdUsuario(),
                    request.getIdProfissional(),
                    request.getTipoServico(),
                    request.getDescricao(),
                    request.getDtInicio());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AgendamentoDTO(agendamento));
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            
            if (errorMessage.contains("não está trabalhando nesse horário")) {
                return ResponseEntity.badRequest().body(
                        "O profissional não está disponível para atendimento nesse horário. " +
                        "Por favor, consulte os horários de atendimento do profissional.");
            } else if (errorMessage.contains("já possui outro agendamento")) {
                return ResponseEntity.badRequest().body(
                        "O profissional já possui outro agendamento nesse horário. " +
                        "Por favor, selecione outro horário disponível.");
            } else if (errorMessage.contains("Tipo de serviço inválido")) {
                return ResponseEntity.badRequest().body(errorMessage);
            }
            
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            AgendamentoDTO agendamentoDTO = agendamentoService.buscarPorIdDTO(id);
            return ResponseEntity.ok(agendamentoDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(defaultValue = "0") int page) {
        
        try {
            Pageable pageable = PageRequest.of(page, 10);
            Page<AgendamentoDTO> agendamentosPage = agendamentoService.listarPorUsuarioDTO(idUsuario, pageable);
            return ResponseEntity.ok(agendamentosPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}")
    public ResponseEntity<?> listarPorProfissional(
            @PathVariable Long idProfissional,
            @RequestParam(defaultValue = "0") int page) {
        
        try {
            Pageable pageable = PageRequest.of(page, 10);
            Page<AgendamentoDTO> agendamentosPage = agendamentoService.listarPorProfissionalDTO(idProfissional, pageable);
            return ResponseEntity.ok(agendamentosPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/profissional/{idProfissional}/periodo")
    public ResponseEntity<?> listarPorProfissionalEPeriodo(
            @PathVariable Long idProfissional,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        
        try {
            List<AgendamentoDTO> agendamentoDTOs = agendamentoService.listarPorProfissionalEPeriodoDTO(
                    idProfissional, inicio, fim);
            return ResponseEntity.ok(agendamentoDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAgendamento(
            @PathVariable Long id,
            @Valid @RequestBody AgendamentoRequestDTO request) {
        
        try {
            Agendamento agendamento = agendamentoService.atualizarAgendamento(
                    id, request.getTipoServico(), request.getDescricao(), 
                    request.getDtInicio());
            return ResponseEntity.ok(new AgendamentoDTO(agendamento));
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            
            if (errorMessage.contains("não está trabalhando nesse horário")) {
                return ResponseEntity.badRequest().body(
                        "O profissional não está disponível para atendimento nesse horário. " +
                        "Por favor, consulte os horários de atendimento do profissional.");
            } else if (errorMessage.contains("já possui outro agendamento")) {
                return ResponseEntity.badRequest().body(
                        "O profissional já possui outro agendamento nesse horário. " +
                        "Por favor, selecione outro horário disponível.");
            } else if (errorMessage.contains("Tipo de serviço inválido")) {
                return ResponseEntity.badRequest().body(errorMessage);
            }
            
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }
    
    @GetMapping("/tipos-servico")
    public ResponseEntity<?> listarTiposServico() {
        try {
            List<Map<String, Object>> tiposServico = List.of(
                Map.of(
                    "tipo", TipoServico.TATUAGEM_PEQUENA.getDescricao(),
                    "duracaoHoras", TipoServico.TATUAGEM_PEQUENA.getDuracaoHoras(),
                    "exemplo", "Tatuagem pequena - 2 horas"
                ),
                Map.of(
                    "tipo", TipoServico.TATUAGEM_MEDIA.getDescricao(),
                    "duracaoHoras", TipoServico.TATUAGEM_MEDIA.getDuracaoHoras(),
                    "exemplo", "Tatuagem média - 4 horas"
                ),
                Map.of(
                    "tipo", TipoServico.TATUAGEM_GRANDE.getDescricao(),
                    "duracaoHoras", TipoServico.TATUAGEM_GRANDE.getDuracaoHoras(),
                    "exemplo", "Tatuagem grande - 6 horas"
                ),
                Map.of(
                    "tipo", TipoServico.SESSAO.getDescricao(),
                    "duracaoHoras", TipoServico.SESSAO.getDuracaoHoras(),
                    "exemplo", "Sessão completa - 8 horas"
                )
            );
            return ResponseEntity.ok(tiposServico);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAgendamento(@PathVariable Long id) {
        try {
            agendamentoService.excluirAgendamento(id);
            return ResponseEntity.ok("Agendamento excluído com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/meus-agendamentos")
    public ResponseEntity<?> listarMeusAgendamentos(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                Long userId = jwt.getClaim("userId");
                
                if (userId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Token não contém informações do usuário");
                }
                
                Pageable pageable = PageRequest.of(page, size);
                Page<AgendamentoDTO> agendamentosPage = agendamentoService.listarPorUsuarioDTO(userId, pageable);
                
                return ResponseEntity.ok(agendamentosPage);
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Autenticação inválida");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 