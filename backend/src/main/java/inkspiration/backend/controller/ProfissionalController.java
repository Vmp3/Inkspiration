package inkspiration.backend.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.dto.ProfissionalCriacaoDTO;
import inkspiration.backend.dto.ProfissionalDTO;
import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.dto.DisponibilidadeDTO;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.service.ImagemService;
import inkspiration.backend.service.ProfissionalService;
import inkspiration.backend.service.PortifolioService;
import inkspiration.backend.service.DisponibilidadeService;
import jakarta.validation.Valid;

@RestController
public class ProfissionalController {

    private final ProfissionalService profissionalService;
    private final ImagemService imagemService;
    private final PortifolioService portifolioService;
    private final DisponibilidadeService disponibilidadeService;

    @Autowired
    public ProfissionalController(ProfissionalService profissionalService, ImagemService imagemService, PortifolioService portifolioService, DisponibilidadeService disponibilidadeService) {
        this.profissionalService = profissionalService;
        this.imagemService = imagemService;
        this.portifolioService = portifolioService;
        this.disponibilidadeService = disponibilidadeService;
    }

    @GetMapping("/profissional")
    public ResponseEntity<List<ProfissionalDTO>> listar(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Profissional> profissionais = profissionalService.listar(pageable);
        
        List<ProfissionalDTO> dtos = profissionais.getContent().stream()
                .map(profissionalService::converterParaDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profissional/{id}")
    public ResponseEntity<ProfissionalDTO> buscarPorId(@PathVariable Long id) {
        Profissional profissional = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(profissionalService.converterParaDto(profissional));
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}")
    public ResponseEntity<ProfissionalDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        try {
            Profissional profissional = profissionalService.buscarPorUsuario(idUsuario);
            return ResponseEntity.ok(profissionalService.converterParaDto(profissional));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}/completo")
    public ResponseEntity<?> buscarProfissionalCompleto(@PathVariable Long idUsuario) {
        try {
            // Buscar o profissional pelo ID do usuário
            Profissional profissional = profissionalService.buscarPorUsuario(idUsuario);
            
            // Converter para DTO básico
            ProfissionalDTO profissionalDto = profissionalService.converterParaDto(profissional);
            
            // Buscar dados do portfólio
            PortifolioDTO portfolioDto = null;
            if (profissional.getPortifolio() != null) {
                portfolioDto = portifolioService.converterParaDto(profissional.getPortifolio());
            }
            
            // Buscar imagens do portfólio se existir
            List<ImagemDTO> imagens = Collections.emptyList();
            if (profissional.getPortifolio() != null) {
                imagens = imagemService.listarPorPortifolio(profissional.getPortifolio().getIdPortifolio());
            }
            
            // Buscar disponibilidades
            Map<String, Map<String, String>> disponibilidades = Collections.emptyMap();
            try {
                // Retornar o Map diretamente como no endpoint /disponibilidades/profissional/1
                disponibilidades = disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional());
            } catch (Exception e) {
                // Se não houver disponibilidades cadastradas, manter mapa vazio
                System.out.println("Nenhuma disponibilidade encontrada para o profissional: " + e.getMessage());
            }
            
            // Criar resposta com todas as informações necessárias para edição
            Map<String, Object> response = new HashMap<>();
            response.put("profissional", profissionalDto);
            response.put("portfolio", portfolioDto);
            response.put("imagens", imagens);
            response.put("disponibilidades", disponibilidades);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/profissional/verificar/{idUsuario}")
    public ResponseEntity<Boolean> verificarPerfil(@PathVariable Long idUsuario) {
        boolean existePerfil = profissionalService.existePerfil(idUsuario);
        return ResponseEntity.ok(existePerfil);
    }

    @PostMapping("/auth/register/profissional")
    public ResponseEntity<ProfissionalDTO> criar(@RequestBody @Valid ProfissionalDTO dto) {
        Profissional profissional = profissionalService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalService.converterParaDto(profissional));
    }

    @PostMapping("/auth/register/profissional-completo")
    public ResponseEntity<?> criarProfissionalCompleto(@RequestBody @Valid ProfissionalCriacaoDTO dto) {
        try {
            Profissional profissional = profissionalService.criarProfissionalCompleto(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(profissionalService.converterParaDto(profissional));
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar disponibilidades: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/profissional/atualizar/{id}")
    public ResponseEntity<ProfissionalDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProfissionalDTO dto) {
        // Verifica se o usuário autenticado é o dono do perfil ou um admin
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        Profissional profissionalExistente = profissionalService.buscarPorId(id);
        
        if (autenticacao != null) {
            boolean isAdmin = autenticacao.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            String cpfAutenticado = autenticacao.getName();
            String cpfProfissional = profissionalExistente.getUsuario().getUsuarioAutenticar().getCpf();
            
            if (!isAdmin && !cpfAutenticado.equals(cpfProfissional)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        Profissional profissionalAtualizado = profissionalService.atualizar(id, dto);
        return ResponseEntity.ok(profissionalService.converterParaDto(profissionalAtualizado));
    }

    @PutMapping("/profissional/usuario/{idUsuario}/atualizar-completo")
    public ResponseEntity<?> atualizarProfissionalCompleto(@PathVariable Long idUsuario, @RequestBody @Valid ProfissionalCriacaoDTO dto) {
        try {
            // Buscar o profissional pelo ID do usuário
            Profissional profissionalExistente = profissionalService.buscarPorUsuario(idUsuario);
            
            // Verificar autorização
            Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
            if (autenticacao != null) {
                boolean isAdmin = autenticacao.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
                String cpfAutenticado = autenticacao.getName();
                String cpfProfissional = profissionalExistente.getUsuario().getUsuarioAutenticar().getCpf();
                
                if (!isAdmin && !cpfAutenticado.equals(cpfProfissional)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            // Garantir que o DTO tem o ID do usuário correto
            dto.setIdUsuario(idUsuario);
            
            // Para atualização, vamos usar o método de criação que já trata todos os campos
            // O service deve detectar se já existe e fazer update em vez de create
            Profissional profissionalAtualizado = profissionalService.criarProfissionalCompleto(dto);
            return ResponseEntity.ok(profissionalService.converterParaDto(profissionalAtualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/profissional/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Verifica se o usuário autenticado é o dono do perfil ou um admin
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        Profissional profissional = profissionalService.buscarPorId(id);
        
        if (autenticacao != null) {
            boolean isAdmin = autenticacao.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            String cpfAutenticado = autenticacao.getName();
            String cpfProfissional = profissional.getUsuario().getUsuarioAutenticar().getCpf();
            
            if (!isAdmin && !cpfAutenticado.equals(cpfProfissional)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        profissionalService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profissional/{id}/imagens")
    public ResponseEntity<List<ImagemDTO>> buscarImagensProfissional(@PathVariable Long id) {
        Profissional profissional = profissionalService.buscarPorId(id);
        
        if (profissional.getPortifolio() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        
        Long idPortifolio = profissional.getPortifolio().getIdPortifolio();
        List<ImagemDTO> imagens = imagemService.listarPorPortifolio(idPortifolio);
        
        return ResponseEntity.ok(imagens);
    }
} 