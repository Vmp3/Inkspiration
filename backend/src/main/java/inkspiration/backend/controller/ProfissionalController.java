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
import inkspiration.backend.security.AuthorizationService;
import jakarta.validation.Valid;

@RestController
public class ProfissionalController {

    private final ProfissionalService profissionalService;
    private final ImagemService imagemService;
    private final PortifolioService portifolioService;
    private final DisponibilidadeService disponibilidadeService;
    private final AuthorizationService authorizationService;

    @Autowired
    public ProfissionalController(ProfissionalService profissionalService, ImagemService imagemService, PortifolioService portifolioService, DisponibilidadeService disponibilidadeService, AuthorizationService authorizationService) {
        this.profissionalService = profissionalService;
        this.imagemService = imagemService;
        this.portifolioService = portifolioService;
        this.disponibilidadeService = disponibilidadeService;
        this.authorizationService = authorizationService;
    }

    @GetMapping("/profissional")
    public ResponseEntity<List<ProfissionalDTO>> listar(@RequestParam(defaultValue = "0") int page) {
        // Apenas administradores podem listar todos os profissionais com paginação
        authorizationService.requireAdmin();
        
        Pageable pageable = PageRequest.of(page, 10);
        Page<Profissional> profissionais = profissionalService.listar(pageable);
        
        List<ProfissionalDTO> dtos = profissionais.getContent().stream()
                .map(profissionalService::converterParaDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profissional/publico")
    public ResponseEntity<List<ProfissionalDTO>> listarPublico() {
        // Endpoint público para listar profissionais (sem paginação, dados básicos)
        Pageable pageable = PageRequest.of(0, 100); // Limite razoável
        Page<Profissional> profissionais = profissionalService.listar(pageable);
        
        List<ProfissionalDTO> dtos = profissionais.getContent().stream()
                .map(profissionalService::converterParaDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profissional/completo")
    public ResponseEntity<Map<String, Object>> listarCompleto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String locationTerm,
            @RequestParam(defaultValue = "0") double minRating,
            @RequestParam(required = false) String[] selectedSpecialties,
            @RequestParam(defaultValue = "melhorAvaliacao") String sortBy) {
        
        // Endpoint público para listar profissionais com informações completas (portfolio, endereço, nota)
        Pageable pageable = PageRequest.of(page, size);
        Page<Profissional> profissionais = profissionalService.listarComFiltros(
            pageable, searchTerm, locationTerm, minRating, selectedSpecialties, sortBy);
        
        List<Map<String, Object>> profissionaisCompletos = profissionais.getContent().stream()
                .map(profissional -> {
                    Map<String, Object> profissionalCompleto = new HashMap<>();
                    
                    // Informações básicas do profissional
                    ProfissionalDTO profissionalDto = profissionalService.converterParaDto(profissional);
                    profissionalCompleto.put("profissional", profissionalDto);
                    
                    // Informações do usuário (nome e outros dados)
                    Map<String, Object> usuarioInfo = new HashMap<>();
                    if (profissional.getUsuario() != null) {
                        usuarioInfo.put("idUsuario", profissional.getUsuario().getIdUsuario());
                        usuarioInfo.put("nome", profissional.getUsuario().getNome());
                        usuarioInfo.put("email", profissional.getUsuario().getEmail());
                        usuarioInfo.put("telefone", profissional.getUsuario().getTelefone());
                        usuarioInfo.put("imagemPerfil", profissional.getUsuario().getImagemPerfil());
                    }
                    profissionalCompleto.put("usuario", usuarioInfo);
                    
                    // Informações do endereço
                    Map<String, Object> enderecoInfo = new HashMap<>();
                    if (profissional.getEndereco() != null) {
                        enderecoInfo.put("idEndereco", profissional.getEndereco().getIdEndereco());
                        enderecoInfo.put("cep", profissional.getEndereco().getCep());
                        enderecoInfo.put("rua", profissional.getEndereco().getRua());
                        enderecoInfo.put("bairro", profissional.getEndereco().getBairro());
                        enderecoInfo.put("cidade", profissional.getEndereco().getCidade());
                        enderecoInfo.put("estado", profissional.getEndereco().getEstado());
                        enderecoInfo.put("numero", profissional.getEndereco().getNumero());
                        enderecoInfo.put("complemento", profissional.getEndereco().getComplemento());
                        enderecoInfo.put("latitude", profissional.getEndereco().getLatitude());
                        enderecoInfo.put("longitude", profissional.getEndereco().getLongitude());
                    }
                    profissionalCompleto.put("endereco", enderecoInfo);
                    
                    // Buscar dados do portfólio
                    PortifolioDTO portfolioDto = null;
                    if (profissional.getPortifolio() != null) {
                        portfolioDto = portifolioService.converterParaDto(profissional.getPortifolio());
                    }
                    profissionalCompleto.put("portfolio", portfolioDto);
                    
                    // Buscar imagens do portfólio se existir
                    List<ImagemDTO> imagens = Collections.emptyList();
                    if (profissional.getPortifolio() != null) {
                        imagens = imagemService.listarPorPortifolio(profissional.getPortifolio().getIdPortifolio());
                    }
                    profissionalCompleto.put("imagens", imagens);
                    
                    // Buscar disponibilidades
                    Map<String, List<Map<String, String>>> disponibilidades = Collections.emptyMap();
                    try {
                        disponibilidades = disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional());
                    } catch (Exception e) {
                        // Se não houver disponibilidades cadastradas, manter mapa vazio
                        System.out.println("Nenhuma disponibilidade encontrada para o profissional: " + e.getMessage());
                    }
                    profissionalCompleto.put("disponibilidades", disponibilidades);
                    
                    return profissionalCompleto;
                })
                .collect(Collectors.toList());
        
        // Retornar dados paginados
        Map<String, Object> response = new HashMap<>();
        response.put("content", profissionaisCompletos);
        response.put("totalElements", profissionais.getTotalElements());
        response.put("totalPages", profissionais.getTotalPages());
        response.put("currentPage", profissionais.getNumber());
        response.put("size", profissionais.getSize());
        response.put("hasNext", profissionais.hasNext());
        response.put("hasPrevious", profissionais.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profissional/completo/{id}")
    public ResponseEntity<Map<String, Object>> buscarCompletoPorid(@PathVariable Long id) {
        // Endpoint público para buscar um profissional específico com informações completas
        Profissional profissional = profissionalService.buscarPorId(id);
        
        Map<String, Object> profissionalCompleto = new HashMap<>();
        
        // Informações básicas do profissional
        ProfissionalDTO profissionalDto = profissionalService.converterParaDto(profissional);
        profissionalCompleto.put("profissional", profissionalDto);
        
        // Informações do usuário (nome e outros dados)
        Map<String, Object> usuarioInfo = new HashMap<>();
        if (profissional.getUsuario() != null) {
            usuarioInfo.put("idUsuario", profissional.getUsuario().getIdUsuario());
            usuarioInfo.put("nome", profissional.getUsuario().getNome());
            usuarioInfo.put("email", profissional.getUsuario().getEmail());
            usuarioInfo.put("telefone", profissional.getUsuario().getTelefone());
            usuarioInfo.put("imagemPerfil", profissional.getUsuario().getImagemPerfil());
        }
        profissionalCompleto.put("usuario", usuarioInfo);
        
        // Informações do endereço
        Map<String, Object> enderecoInfo = new HashMap<>();
        if (profissional.getEndereco() != null) {
            enderecoInfo.put("idEndereco", profissional.getEndereco().getIdEndereco());
            enderecoInfo.put("cep", profissional.getEndereco().getCep());
            enderecoInfo.put("rua", profissional.getEndereco().getRua());
            enderecoInfo.put("bairro", profissional.getEndereco().getBairro());
            enderecoInfo.put("cidade", profissional.getEndereco().getCidade());
            enderecoInfo.put("estado", profissional.getEndereco().getEstado());
            enderecoInfo.put("numero", profissional.getEndereco().getNumero());
            enderecoInfo.put("complemento", profissional.getEndereco().getComplemento());
            enderecoInfo.put("latitude", profissional.getEndereco().getLatitude());
            enderecoInfo.put("longitude", profissional.getEndereco().getLongitude());
        }
        profissionalCompleto.put("endereco", enderecoInfo);
        
        // Buscar dados do portfólio
        PortifolioDTO portfolioDto = null;
        if (profissional.getPortifolio() != null) {
            portfolioDto = portifolioService.converterParaDto(profissional.getPortifolio());
        }
        profissionalCompleto.put("portfolio", portfolioDto);
        
        // Buscar imagens do portfólio se existir
        List<ImagemDTO> imagens = Collections.emptyList();
        if (profissional.getPortifolio() != null) {
            imagens = imagemService.listarPorPortifolio(profissional.getPortifolio().getIdPortifolio());
        }
        profissionalCompleto.put("imagens", imagens);
        
        // Buscar disponibilidades
        Map<String, List<Map<String, String>>> disponibilidades = Collections.emptyMap();
        try {
            disponibilidades = disponibilidadeService.obterDisponibilidade(profissional.getIdProfissional());
        } catch (Exception e) {
            // Se não houver disponibilidades cadastradas, manter mapa vazio
            System.out.println("Nenhuma disponibilidade encontrada para o profissional: " + e.getMessage());
        }
        profissionalCompleto.put("disponibilidades", disponibilidades);
        
        return ResponseEntity.ok(profissionalCompleto);
    }

    @GetMapping("/profissional/{id}")
    public ResponseEntity<ProfissionalDTO> buscarPorId(@PathVariable Long id) {
        Profissional profissional = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(profissionalService.converterParaDto(profissional));
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}")
    public ResponseEntity<ProfissionalDTO> buscarPorUsuario(@PathVariable Long idUsuario) {
        try {
            // Verifica se o usuário pode acessar este perfil profissional
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
            Profissional profissional = profissionalService.buscarPorUsuario(idUsuario);
            return ResponseEntity.ok(profissionalService.converterParaDto(profissional));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/profissional/usuario/{idUsuario}/completo")
    public ResponseEntity<?> buscarProfissionalCompleto(@PathVariable Long idUsuario) {
        try {
            // Verifica se o usuário pode acessar este perfil profissional completo
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
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
            Map<String, List<Map<String, String>>> disponibilidades = Collections.emptyMap();
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
        // Verifica se o usuário pode verificar este perfil
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
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
        // Busca o profissional para obter o ID do usuário
        Profissional profissionalExistente = profissionalService.buscarPorId(id);
        Long idUsuario = profissionalExistente.getUsuario().getIdUsuario();
        
        // Verifica se o usuário pode editar este perfil profissional
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        Profissional profissionalAtualizado = profissionalService.atualizar(id, dto);
        return ResponseEntity.ok(profissionalService.converterParaDto(profissionalAtualizado));
    }

    @PutMapping("/profissional/usuario/{idUsuario}/atualizar-completo")
    public ResponseEntity<?> atualizarProfissionalCompleto(@PathVariable Long idUsuario, @RequestBody @Valid ProfissionalCriacaoDTO dto) {
        try {
            // Verifica se o usuário pode editar este perfil profissional
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
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

    @PutMapping("/profissional/usuario/{idUsuario}/atualizar-completo-com-imagens")
    public ResponseEntity<?> atualizarProfissionalCompletoComImagens(@PathVariable Long idUsuario, @RequestBody Map<String, Object> requestData) {
        try {
            // Verifica se o usuário pode editar este perfil profissional
            authorizationService.requireUserAccessOrAdmin(idUsuario);
            
            // Extrair dados profissionais
            @SuppressWarnings("unchecked")
            Map<String, Object> profissionalData = (Map<String, Object>) requestData.get("profissional");
            
            // Extrair dados do portfólio
            @SuppressWarnings("unchecked")
            Map<String, Object> portfolioData = (Map<String, Object>) requestData.get("portfolio");
            
            // Extrair imagens
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> imagensData = (List<Map<String, Object>>) requestData.get("imagens");
            
            // Extrair disponibilidades no formato de Map para usar com cadastrarDisponibilidade
            @SuppressWarnings("unchecked")
            Map<String, List<Map<String, String>>> disponibilidadesData = (Map<String, List<Map<String, String>>>) requestData.get("disponibilidades");
            
            // Buscar o profissional existente
            Profissional profissional = profissionalService.buscarPorUsuario(idUsuario);
            
            // Atualizar dados básicos do profissional se fornecidos
            if (profissionalData != null) {
                // Atualizar campos do profissional conforme necessário
                // (implementar lógica de atualização baseada nos dados recebidos)
            }
            
            // Atualizar portfólio se fornecido
            if (portfolioData != null && profissional.getPortifolio() != null) {
                Long portfolioId = profissional.getPortifolio().getIdPortifolio();
                
                // Atualizar dados do portfólio
                PortifolioDTO portfolioDto = new PortifolioDTO();
                portfolioDto.setIdPortifolio(portfolioId);
                
                if (portfolioData.containsKey("descricao")) {
                    portfolioDto.setDescricao((String) portfolioData.get("descricao"));
                }
                if (portfolioData.containsKey("especialidade")) {
                    portfolioDto.setEspecialidade((String) portfolioData.get("especialidade"));
                }
                if (portfolioData.containsKey("experiencia")) {
                    portfolioDto.setExperiencia((String) portfolioData.get("experiencia"));
                }
                if (portfolioData.containsKey("instagram")) {
                    portfolioDto.setInstagram((String) portfolioData.get("instagram"));
                }
                if (portfolioData.containsKey("tiktok")) {
                    portfolioDto.setTiktok((String) portfolioData.get("tiktok"));
                }
                if (portfolioData.containsKey("facebook")) {
                    portfolioDto.setFacebook((String) portfolioData.get("facebook"));
                }
                if (portfolioData.containsKey("twitter")) {
                    portfolioDto.setTwitter((String) portfolioData.get("twitter"));
                }
                if (portfolioData.containsKey("website")) {
                    portfolioDto.setWebsite((String) portfolioData.get("website"));
                }
                
                portifolioService.atualizar(portfolioId, portfolioDto);
            }
            
            // Atualizar imagens do portfólio se fornecidas
            if (imagensData != null && profissional.getPortifolio() != null) {
                Long portfolioId = profissional.getPortifolio().getIdPortifolio();
                
                // Buscar e deletar imagens antigas
                List<ImagemDTO> imagensAtuais = imagemService.listarPorPortifolio(portfolioId);
                for (ImagemDTO imagem : imagensAtuais) {
                    imagemService.deletar(imagem.getIdImagem());
                }
                
                // Adicionar novas imagens
                for (Map<String, Object> imagemData : imagensData) {
                    if (imagemData.containsKey("imagemBase64")) {
                        ImagemDTO imagemDto = new ImagemDTO();
                        imagemDto.setImagemBase64((String) imagemData.get("imagemBase64"));
                        imagemDto.setIdPortifolio(portfolioId);
                        imagemService.salvar(imagemDto);
                    }
                }
            }
            
            // Atualizar disponibilidades se fornecidas
            if (disponibilidadesData != null) {
                disponibilidadeService.cadastrarDisponibilidade(profissional.getIdProfissional(), disponibilidadesData);
            }
            
            // Retornar dados completos atualizados no mesmo formato do endpoint /completo
            return buscarCompletoPorid(profissional.getIdProfissional());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar dados completos: " + e.getMessage());
        }
    }

    @DeleteMapping("/profissional/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        // Busca o profissional para obter o ID do usuário
        Profissional profissional = profissionalService.buscarPorId(id);
        Long idUsuario = profissional.getUsuario().getIdUsuario();
        
        // Verifica se o usuário pode deletar este perfil profissional
        authorizationService.requireUserAccessOrAdmin(idUsuario);
        
        profissionalService.deletar(id);
        return ResponseEntity.noContent().build();
    }
} 