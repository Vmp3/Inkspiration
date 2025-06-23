package inkspiration.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import inkspiration.backend.dto.AgendamentoDTO;
import inkspiration.backend.dto.AgendamentoCompletoDTO;
import inkspiration.backend.dto.AgendamentoRequestDTO;
import inkspiration.backend.dto.AgendamentoUpdateDTO;
import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.TipoServico;
import inkspiration.backend.enums.StatusAgendamento;
import inkspiration.backend.repository.AgendamentoRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.repository.UsuarioRepository;
import java.io.ByteArrayOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;
import inkspiration.backend.exception.agendamento.AutoAgendamentoException;
import inkspiration.backend.exception.agendamento.DataInvalidaAgendamentoException;
import inkspiration.backend.exception.agendamento.HorarioConflitanteException;
import inkspiration.backend.exception.agendamento.ProfissionalIndisponivelException;
import inkspiration.backend.exception.agendamento.TipoServicoInvalidoException;
import inkspiration.backend.exception.agendamento.TokenInvalidoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Service
public class AgendamentoService {
    
    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeService disponibilidadeService;
    private final NumberFormat formatoBrasileiro = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            ProfissionalRepository profissionalRepository,
            UsuarioRepository usuarioRepository,
            DisponibilidadeService disponibilidadeService) {
        this.agendamentoRepository = agendamentoRepository;
        this.profissionalRepository = profissionalRepository;
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadeService = disponibilidadeService;
    }
    
    private LocalDateTime ajustarHorarioInicio(LocalDateTime dtInicio) {
        return dtInicio.withMinute(0).withSecond(0).withNano(0);
    }
    
    private LocalDateTime calcularHorarioFim(LocalDateTime dtInicioAjustado, TipoServico tipoServico) {
        return dtInicioAjustado
                .plusHours(tipoServico.getDuracaoHoras())
                .minusSeconds(1) 
                .withNano(0);
    }
    
    @Transactional
    public Agendamento criarAgendamento(Long idUsuario, Long idProfissional, String tipoServicoStr, 
            String descricao, LocalDateTime dtInicio, BigDecimal valor) throws Exception {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        if (usuario.getIdUsuario().equals(profissional.getUsuario().getIdUsuario())) {
            throw new AutoAgendamentoException("Não é possível agendar consigo mesmo");
        }
        
        LocalDateTime amanha = LocalDate.now().plusDays(1).atStartOfDay();
        if (dtInicio.isBefore(amanha)) {
            throw new DataInvalidaAgendamentoException("Só é possível fazer agendamentos a partir do dia seguinte");
        }
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new TipoServicoInvalidoException("Tipo de serviço inválido. Opções válidas: " +
                    "pequena, media, grande, sessao");
        }
        
        LocalDateTime dtInicioAjustado = ajustarHorarioInicio(dtInicio);
        
        LocalDateTime dtFim = calcularHorarioFim(dtInicioAjustado, tipoServico);
        
        List<Agendamento> agendamentosUsuario = agendamentoRepository.findByUsuario(usuario)
                .stream()
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicioAjustado)))
                .collect(Collectors.toList());
        
        if (!agendamentosUsuario.isEmpty()) {
            throw new HorarioConflitanteException("Você já possui outro agendamento nesse horário. Por favor, selecione um horário diferente.");
        }
        
        try {
            boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (!estaNoHorarioDeTrabalho) {
                throw new ProfissionalIndisponivelException("O profissional não está trabalhando nesse horário. " +
                        "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                        " (" + tipoServico.getDuracaoHoras() + " horas)");
            }
            
            boolean existeConflito = agendamentoRepository.existsConflitingSchedule(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (existeConflito) {
                throw new HorarioConflitanteException("O profissional já possui outro agendamento nesse horário. " +
                        "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                        " (" + tipoServico.getDuracaoHoras() + " horas)");
            }
            
            Agendamento agendamento = new Agendamento();
            agendamento.setUsuario(usuario);
            agendamento.setProfissional(profissional);
            agendamento.setTipoServico(tipoServico);
            agendamento.setDescricao(descricao);
            agendamento.setDtInicio(dtInicioAjustado);
            agendamento.setDtFim(dtFim);
            agendamento.setValor(valor);
            
            return agendamentoRepository.save(agendamento);
            
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar disponibilidade do profissional", e);
        }
    }
    
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }
    
    public List<Agendamento> listarPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return agendamentoRepository.findByUsuario(usuario);
    }
    
    public Page<Agendamento> listarPorUsuario(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        return agendamentoRepository.findByUsuario(usuario, pageable);
    }
    
    public List<Agendamento> listarPorProfissional(Long idProfissional) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        return agendamentoRepository.findByProfissional(profissional);
    }
    
    public Page<Agendamento> listarPorProfissional(Long idProfissional, Pageable pageable) {
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        return agendamentoRepository.findByProfissional(profissional, pageable);
    }
    
    @Transactional
    public Agendamento atualizarAgendamento(Long id, Long idUsuarioLogado, String tipoServicoStr, String descricao,
            LocalDateTime dtInicio) throws Exception {
        
        Agendamento agendamento = buscarPorId(id);
        
        if (!agendamento.getUsuario().getIdUsuario().equals(idUsuarioLogado)) {
            throw new RuntimeException("Não autorizado: este agendamento não pertence ao usuário logado");
        }
        
        LocalDateTime amanha = LocalDate.now().plusDays(1).atStartOfDay();
        if (dtInicio.isBefore(amanha)) {
            throw new RuntimeException("Só é possível fazer agendamentos a partir do dia seguinte");
        }
        
        TipoServico tipoServico;
        try {
            tipoServico = TipoServico.fromDescricao(tipoServicoStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de serviço inválido. Opções válidas: " +
                    "pequena, media, grande, sessao");
        }
        
        LocalDateTime dtInicioAjustado = ajustarHorarioInicio(dtInicio);
        
        LocalDateTime dtFim = calcularHorarioFim(dtInicioAjustado, tipoServico);
        
        if (!agendamento.getDtInicio().equals(dtInicioAjustado) || !agendamento.getDtFim().equals(dtFim)) {
            List<Agendamento> agendamentosUsuario = agendamentoRepository.findByUsuario(agendamento.getUsuario())
                    .stream()
                    .filter(a -> !a.getIdAgendamento().equals(id))
                    .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                    .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicioAjustado)))
                    .collect(Collectors.toList());
            
            if (!agendamentosUsuario.isEmpty()) {
                throw new RuntimeException("Você já possui outro agendamento nesse horário. Por favor, selecione um horário diferente.");
            }
            try {
                boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                        agendamento.getProfissional().getIdProfissional(), dtInicioAjustado, dtFim);
                
                if (!estaNoHorarioDeTrabalho) {
                    throw new RuntimeException("O profissional não está trabalhando nesse horário. " +
                            "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                            " (" + tipoServico.getDuracaoHoras() + " horas)");
                }
                
                List<Agendamento> agendamentosConflitantes = agendamentoRepository
                        .findByProfissionalAndPeriod(
                                agendamento.getProfissional().getIdProfissional(), 
                                dtInicioAjustado.minusHours(1), 
                                dtFim.plusHours(1))
                        .stream()
                        .filter(a -> !a.getIdAgendamento().equals(id)) 
                        .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicioAjustado)))
                        .collect(Collectors.toList());
                
                if (!agendamentosConflitantes.isEmpty()) {
                    throw new RuntimeException("O profissional já possui outro agendamento nesse horário. " +
                            "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                            " (" + tipoServico.getDuracaoHoras() + " horas)");
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao processar disponibilidade do profissional", e);
            }
        }
        
        agendamento.setTipoServico(tipoServico);
        agendamento.setDescricao(descricao);
        agendamento.setDtInicio(dtInicioAjustado);
        agendamento.setDtFim(dtFim);
        // Valor é preservado - não alterado durante edições
        
        return agendamentoRepository.save(agendamento);
    }
    
    @Transactional
    public void excluirAgendamento(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamentoRepository.delete(agendamento);
    }
    
    public AgendamentoDTO buscarPorIdDTO(Long id) {
        Agendamento agendamento = buscarPorId(id);
        return new AgendamentoDTO(agendamento);
    }
    
    public List<AgendamentoDTO> listarPorUsuarioDTO(Long idUsuario) {
        return listarPorUsuario(idUsuario).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AgendamentoDTO> listarPorUsuarioDTO(Long idUsuario, Pageable pageable) {
        Page<Agendamento> agendamentosPage = listarPorUsuario(idUsuario, pageable);
        List<AgendamentoDTO> agendamentosDTO = agendamentosPage.getContent().stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }
    
    public List<AgendamentoDTO> listarPorProfissionalDTO(Long idProfissional) {
        return listarPorProfissional(idProfissional).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
    
    public Page<AgendamentoDTO> listarPorProfissionalDTO(Long idProfissional, Pageable pageable) {
        Page<Agendamento> agendamentosPage = listarPorProfissional(idProfissional, pageable);
        List<AgendamentoDTO> agendamentosDTO = agendamentosPage.getContent().stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }
    
    @Transactional
    public Agendamento atualizarStatusAgendamento(Long id, Long idUsuarioLogado, String status, List<String> roles) {
        Agendamento agendamento = buscarPorId(id);
        
        boolean isAuthorized = false;
        
        if (agendamento.getUsuario().getIdUsuario().equals(idUsuarioLogado)) {
            isAuthorized = true;
        }
        
        if (!isAuthorized && roles.contains("ROLE_PROF")) {
            try {
                Usuario usuario = usuarioRepository.findById(idUsuarioLogado)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
                
                Profissional profissionalLogado = profissionalRepository.findByUsuario(usuario)
                        .orElseThrow(() -> new RuntimeException("Profissional não encontrado para o usuário logado"));
                
                
                if (agendamento.getProfissional().getIdProfissional().equals(profissionalLogado.getIdProfissional())) {
                    isAuthorized = true;
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
                isAuthorized = false;
            }
        }
        
        if (!isAuthorized) {
            throw new RuntimeException("Não autorizado: você não tem permissão para alterar este agendamento");
        }
        
        try {
            StatusAgendamento novoStatus = StatusAgendamento.fromDescricao(status);
            
            if (novoStatus == StatusAgendamento.CANCELADO && agendamento.getStatus() != StatusAgendamento.AGENDADO) {
                throw new RuntimeException("Somente agendamentos com status 'Agendado' podem ser cancelados");
            }
            
            if (novoStatus == StatusAgendamento.CANCELADO) {
                LocalDateTime agora = LocalDateTime.now();
                LocalDateTime dataLimite = agendamento.getDtInicio().minusDays(3);
                
                if (agora.isAfter(dataLimite)) {
                    throw new RuntimeException("O cancelamento só é permitido com no mínimo 3 dias de antecedência");
                }
            }
            
            agendamento.setStatus(novoStatus);
            return agendamentoRepository.save(agendamento);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status inválido. Opções válidas: Agendado, Cancelado, Concluído");
        }
    }
    
    public Page<AgendamentoCompletoDTO> listarAgendamentosFuturos(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        LocalDateTime agora = LocalDateTime.now();
        Page<Agendamento> agendamentosPage = agendamentoRepository.findByUsuarioAndDtFimAfterOrderByDtInicioAsc(
                usuario, agora, pageable);
        
        List<AgendamentoCompletoDTO> agendamentosDTO = agendamentosPage.getContent().stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }
    
    public Page<AgendamentoCompletoDTO> listarAgendamentosPassados(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        LocalDateTime agora = LocalDateTime.now();
        Page<Agendamento> agendamentosPage = agendamentoRepository.findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(
                usuario, agora, pageable);
        
        List<Agendamento> agendamentosAtualizados = agendamentosPage.getContent().stream()
                .map(this::atualizarStatusSeNecessario)
                .collect(Collectors.toList());
        
        List<AgendamentoCompletoDTO> agendamentosDTO = agendamentosAtualizados.stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(agendamentosDTO, pageable, agendamentosPage.getTotalElements());
    }

    @Transactional
    private Agendamento atualizarStatusSeNecessario(Agendamento agendamento) {
        if (agendamento.getStatus() == StatusAgendamento.AGENDADO && 
            agendamento.getDtFim().isBefore(LocalDateTime.now())) {
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            return agendamentoRepository.save(agendamento);
        }
        return agendamento;
    }
    
    public Page<AgendamentoCompletoDTO> listarAtendimentosFuturos(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        LocalDateTime agora = LocalDateTime.now();
        Page<Agendamento> atendimentosPage = agendamentoRepository.findByProfissionalAndDtFimAfterOrderByDtInicioAsc(
                profissional, agora, pageable);
        
        List<AgendamentoCompletoDTO> atendimentosDTO = atendimentosPage.getContent().stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(atendimentosDTO, pageable, atendimentosPage.getTotalElements());
    }
    
    public Page<AgendamentoCompletoDTO> listarAtendimentosPassados(Long idUsuario, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        LocalDateTime agora = LocalDateTime.now();
        Page<Agendamento> atendimentosPage = agendamentoRepository.findByProfissionalAndDtFimBeforeOrderByDtInicioDesc(
                profissional, agora, pageable);
        
        List<Agendamento> atendimentosAtualizados = atendimentosPage.getContent().stream()
                .map(this::atualizarStatusSeNecessario)
                .collect(Collectors.toList());
        
        List<AgendamentoCompletoDTO> atendimentosDTO = atendimentosAtualizados.stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(atendimentosDTO, pageable, atendimentosPage.getTotalElements());
    }
    
    public byte[] gerarPDFAgendamentos(Long idUsuario, Integer ano) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = null;
        PdfWriter writer = null;
        
        try {
            List<Agendamento> agendamentos = agendamentoRepository.findByUsuarioIdAndStatusAndAno(
                idUsuario, StatusAgendamento.CONCLUIDO, ano);
            
            if (agendamentos.isEmpty()) {
                throw new Exception("Nenhum agendamento concluído encontrado para o ano " + ano);
            }
            
            List<AgendamentoCompletoDTO> agendamentosDTO = agendamentos.stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
            
            document = new Document(com.lowagie.text.PageSize.A4);
            writer = PdfWriter.getInstance(document, out);
            
            document.addCreator("Inkspiration App");
            document.addAuthor("Inkspiration");
            document.addSubject("Relatório de Agendamentos");
            document.addTitle("Agendamentos Concluídos - " + ano);
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Paragraph title = new Paragraph("Agendamentos Concluídos - " + ano, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph info = new Paragraph("Total de agendamentos concluídos: " + agendamentosDTO.size(), normalFont);
            info.setAlignment(Element.ALIGN_LEFT);
            info.setSpacingAfter(10);
            document.add(info);
            
            // Calcular valor total dos agendamentos
            double valorTotal = agendamentosDTO.stream()
                .filter(agendamento -> agendamento.getValor() != null)
                .mapToDouble(agendamento -> agendamento.getValor().doubleValue())
                .sum();
            
            Font valorFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph valorInfo = new Paragraph("Valor total dos agendamentos: " + formatoBrasileiro.format(valorTotal), valorFont);
            valorInfo.setAlignment(Element.ALIGN_LEFT);
            valorInfo.setSpacingAfter(20);
            document.add(valorInfo);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (AgendamentoCompletoDTO agendamento : agendamentosDTO) {
                try {
                    Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
                    String titleText = "Agendamento #" + agendamento.getIdAgendamento();
                    if (agendamento.getDtInicio() != null) {
                        titleText += " - " + dateFormatter.format(agendamento.getDtInicio());
                    }
                    
                    Paragraph sectionTitle = new Paragraph(titleText, sectionFont);
                    sectionTitle.setSpacingBefore(15);
                    sectionTitle.setSpacingAfter(10);
                    document.add(sectionTitle);
                    
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);
                    
                    float[] columnWidths = {1f, 3f};
                    table.setWidths(columnWidths);
                    
                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
                    Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
                    
                    addTableRow(table, "Profissional", 
                        agendamento.getNomeProfissional() != null ? agendamento.getNomeProfissional() : "Não especificado", 
                        headerFont, cellFont);
                    
                    String tipoServico = "Não especificado";
                    if (agendamento.getTipoServico() != null) {
                        switch (agendamento.getTipoServico()) {
                            case TATUAGEM_PEQUENA:
                                tipoServico = "Tatuagem Pequena";
                                break;
                            case TATUAGEM_MEDIA:
                                tipoServico = "Tatuagem Média";
                                break;
                            case TATUAGEM_GRANDE:
                                tipoServico = "Tatuagem Grande";
                                break;
                            case SESSAO:
                                tipoServico = "Sessão";
                                break;
                            default:
                                tipoServico = agendamento.getTipoServico().getDescricao();
                        }
                    }
                    addTableRow(table, "Serviço", tipoServico, headerFont, cellFont);
                    
                    if (agendamento.getDtInicio() != null) {
                        addTableRow(table, "Data", dateFormatter.format(agendamento.getDtInicio()), headerFont, cellFont);
                    }
                    
                    String horario = "Não especificado";
                    if (agendamento.getDtInicio() != null) {
                        horario = timeFormatter.format(agendamento.getDtInicio());
                        if (agendamento.getDtFim() != null) {
                            horario += " - " + timeFormatter.format(agendamento.getDtFim());
                        }
                    }
                    addTableRow(table, "Horário", horario, headerFont, cellFont);
                    
                    String endereco = "Endereço não disponível";
                    
                    if (agendamento.getRua() != null) {
                        endereco = agendamento.getRua();
                        if (agendamento.getNumero() != null) {
                            endereco += ", " + agendamento.getNumero();
                        }
                        
                        if (agendamento.getComplemento() != null && !agendamento.getComplemento().isEmpty()) {
                            endereco += "\n" + agendamento.getComplemento();
                        }
                        
                        if (agendamento.getBairro() != null && !agendamento.getBairro().isEmpty()) {
                            endereco += "\n" + agendamento.getBairro();
                        }
                        
                        if (agendamento.getCidade() != null) {
                            endereco += "\n" + agendamento.getCidade();
                            if (agendamento.getEstado() != null) {
                                endereco += "/" + agendamento.getEstado();
                            }
                        }
                        
                        if (agendamento.getCep() != null) {
                            endereco += "\nCEP: " + agendamento.getCep();
                        }
                    }
                    
                    addTableRow(table, "Local", endereco, headerFont, cellFont);
                    
                    if (agendamento.getDescricao() != null && !agendamento.getDescricao().isEmpty()) {
                        addTableRow(table, "Descrição", agendamento.getDescricao(), headerFont, cellFont);
                    }
                    
                    if (agendamento.getValor() != null) {
                        String valorFormatado = formatoBrasileiro.format(agendamento.getValor());
                        addTableRow(table, "Valor", valorFormatado, headerFont, cellFont);
                    }
                    
                    document.add(table);
                    
                    Paragraph separator = new Paragraph("------------------------------------------------------");
                    separator.setAlignment(Element.ALIGN_CENTER);
                    document.add(separator);
                } catch (Exception e) {
                    System.err.println("Erro ao processar agendamento #" + agendamento.getIdAgendamento() + ": " + e.getMessage());
                }
            }
            
            Paragraph footer = new Paragraph("Relatório gerado em: " + 
                java.time.LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            writer.close();
            
            return out.toByteArray();
            
        } catch (DocumentException e) {
            throw new Exception("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            try {
                if (document != null && document.isOpen()) {
                    document.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                System.err.println("Erro ao fechar recursos PDF: " + e.getMessage());
            }
        }
    }
    
    private void addTableRow(PdfPTable table, String header, String value, Font headerFont, Font cellFont) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
        headerCell.setBackgroundColor(new Color(44, 62, 80));
        headerCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, cellFont));
        valueCell.setPadding(5);
        
        table.addCell(headerCell);
        table.addCell(valueCell);
    }

    public byte[] gerarPDFAtendimentos(Long idUsuario, Integer ano, Integer mes) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = null;
        PdfWriter writer = null;
        
        try {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            Profissional profissional = profissionalRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
            
            List<Agendamento> atendimentos = agendamentoRepository.findByProfissionalIdAndStatusAndAnoMes(
                profissional.getIdProfissional(), StatusAgendamento.CONCLUIDO, ano, mes);
            
            if (atendimentos.isEmpty()) {
                throw new Exception("Nenhum atendimento concluído encontrado para " + String.format("%02d", mes) + "/" + ano);
            }
            
            List<AgendamentoCompletoDTO> atendimentosDTO = atendimentos.stream()
                .map(AgendamentoCompletoDTO::new)
                .collect(Collectors.toList());
            
            document = new Document(com.lowagie.text.PageSize.A4);
            writer = PdfWriter.getInstance(document, out);
            
            document.addCreator("Inkspiration App");
            document.addAuthor("Inkspiration");
            document.addSubject("Relatório de Atendimentos");
            document.addTitle("Atendimentos Concluídos - " + String.format("%02d", mes) + "/" + ano);
            
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            String[] meses = {"", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
                             "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
            String mesNome = mes <= 12 ? meses[mes] : "Mês " + mes;
            
            Paragraph title = new Paragraph("Atendimentos Concluídos - " + mesNome + "/" + ano, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph info = new Paragraph("Total de atendimentos concluídos: " + atendimentosDTO.size(), normalFont);
            info.setAlignment(Element.ALIGN_LEFT);
            info.setSpacingAfter(10);
            document.add(info);
            
            // Calcular valor total dos atendimentos
            double valorTotal = atendimentosDTO.stream()
                .filter(atendimento -> atendimento.getValor() != null)
                .mapToDouble(atendimento -> atendimento.getValor().doubleValue())
                .sum();
            
            Font valorFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Paragraph valorInfo = new Paragraph("Valor total dos atendimentos: " + formatoBrasileiro.format(valorTotal), valorFont);
            valorInfo.setAlignment(Element.ALIGN_LEFT);
            valorInfo.setSpacingAfter(20);
            document.add(valorInfo);
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            
            for (AgendamentoCompletoDTO atendimento : atendimentosDTO) {
                try {
                    Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);
                    String titleText = "Atendimento #" + atendimento.getIdAgendamento();
                    if (atendimento.getDtInicio() != null) {
                        titleText += " - " + dateFormatter.format(atendimento.getDtInicio());
                    }
                    
                    Paragraph sectionTitle = new Paragraph(titleText, sectionFont);
                    sectionTitle.setSpacingBefore(15);
                    sectionTitle.setSpacingAfter(10);
                    document.add(sectionTitle);
                    
                    PdfPTable table = new PdfPTable(2);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);
                    
                    float[] columnWidths = {1f, 3f};
                    table.setWidths(columnWidths);
                    
                    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
                    Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
                    
                    addTableRow(table, "Cliente", 
                        atendimento.getNomeUsuario() != null ? atendimento.getNomeUsuario() : "Não especificado", 
                        headerFont, cellFont);
                    
                    String tipoServico = "Não especificado";
                    if (atendimento.getTipoServico() != null) {
                        switch (atendimento.getTipoServico()) {
                            case TATUAGEM_PEQUENA:
                                tipoServico = "Tatuagem Pequena";
                                break;
                            case TATUAGEM_MEDIA:
                                tipoServico = "Tatuagem Média";
                                break;
                            case TATUAGEM_GRANDE:
                                tipoServico = "Tatuagem Grande";
                                break;
                            case SESSAO:
                                tipoServico = "Sessão";
                                break;
                            default:
                                tipoServico = atendimento.getTipoServico().getDescricao();
                        }
                    }
                    addTableRow(table, "Serviço", tipoServico, headerFont, cellFont);
                    
                    if (atendimento.getDtInicio() != null) {
                        addTableRow(table, "Data", dateFormatter.format(atendimento.getDtInicio()), headerFont, cellFont);
                    }
                    
                    String horario = "Não especificado";
                    if (atendimento.getDtInicio() != null) {
                        horario = timeFormatter.format(atendimento.getDtInicio());
                        if (atendimento.getDtFim() != null) {
                            horario += " - " + timeFormatter.format(atendimento.getDtFim());
                        }
                    }
                    addTableRow(table, "Horário", horario, headerFont, cellFont);
                    
                    if (atendimento.getDescricao() != null && !atendimento.getDescricao().isEmpty()) {
                        addTableRow(table, "Descrição", atendimento.getDescricao(), headerFont, cellFont);
                    }
                    
                    if (atendimento.getValor() != null) {
                        String valorFormatado = formatoBrasileiro.format(atendimento.getValor());
                        addTableRow(table, "Valor", valorFormatado, headerFont, cellFont);
                    }
                    
                    document.add(table);
                    
                    Paragraph separator = new Paragraph("------------------------------------------------------");
                    separator.setAlignment(Element.ALIGN_CENTER);
                    document.add(separator);
                } catch (Exception e) {
                    System.err.println("Erro ao processar atendimento #" + atendimento.getIdAgendamento() + ": " + e.getMessage());
                }
            }
            
            Paragraph footer = new Paragraph("Relatório gerado em: " + 
                java.time.LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            writer.close();
            
            return out.toByteArray();
            
        } catch (DocumentException e) {
            throw new Exception("Erro ao gerar PDF: " + e.getMessage());
        } finally {
            try {
                if (document != null && document.isOpen()) {
                    document.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                System.err.println("Erro ao fechar recursos PDF: " + e.getMessage());
            }
        }
    }

    public AgendamentoDTO criarAgendamentoComValidacao(AgendamentoRequestDTO request) {
        try {
            Agendamento agendamento = criarAgendamento(
                request.getIdUsuario(),
                request.getIdProfissional(),
                request.getTipoServico(),
                request.getDescricao(),
                request.getDtInicio(),
                request.getValor()
            );
            return new AgendamentoDTO(agendamento);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public AgendamentoDTO buscarPorIdComValidacao(Long id) {
        try {
            return buscarPorIdDTO(id);
        } catch (Exception e) {
            throw new RuntimeException("Agendamento não encontrado");
        }
    }

    public List<AgendamentoDTO> listarPorUsuarioComValidacao(Long idUsuario, Pageable pageable) {
        try {
            Page<AgendamentoDTO> agendamentosPage = listarPorUsuarioDTO(idUsuario, pageable);
            return agendamentosPage.getContent();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<AgendamentoDTO> listarPorProfissionalComValidacao(Long idProfissional, Pageable pageable) {
        try {
            Page<AgendamentoDTO> agendamentosPage = listarPorProfissionalDTO(idProfissional, pageable);
            return agendamentosPage.getContent();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AgendamentoDTO atualizarAgendamentoComAutenticacao(Long id, AgendamentoUpdateDTO request, Authentication authentication) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            Agendamento agendamento = atualizarAgendamento(id, userId, request.getTipoServico(), request.getDescricao(), request.getDtInicio());
            return new AgendamentoDTO(agendamento);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void excluirAgendamentoComValidacao(Long id) {
        try {
            excluirAgendamento(id);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<AgendamentoDTO> listarMeusAgendamentosComAutenticacao(Authentication authentication, Pageable pageable) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return listarPorUsuarioDTO(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AgendamentoDTO atualizarStatusAgendamentoComAutenticacao(Long id, String status, Authentication authentication) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            String scope = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("scope");
            List<String> roles = new ArrayList<>();
            if (scope != null) {
                roles.add(scope);
            }
            
            Agendamento agendamento = atualizarStatusAgendamento(id, userId, status, roles);
            return new AgendamentoDTO(agendamento);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Page<AgendamentoCompletoDTO> listarMeusAgendamentosFuturosComAutenticacao(Authentication authentication, Pageable pageable) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return listarAgendamentosFuturos(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<AgendamentoCompletoDTO> listarMeusAgendamentosPassadosComAutenticacao(Authentication authentication, Pageable pageable) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return listarAgendamentosPassados(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] exportarAgendamentosPDFComAutenticacao(Integer ano, Authentication authentication) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return gerarPDFAgendamentos(userId, ano);
        } catch (Exception e) {
            if (e.getMessage().contains("Nenhum agendamento concluído encontrado")) {
                throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    public Page<AgendamentoCompletoDTO> listarMeusAtendimentosFuturosComAutenticacao(Authentication authentication, Pageable pageable) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return listarAtendimentosFuturos(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Page<AgendamentoCompletoDTO> listarMeusAtendimentosPassadosComAutenticacao(Authentication authentication, Pageable pageable) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return listarAtendimentosPassados(userId, pageable);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public byte[] exportarAtendimentosPDFComAutenticacao(Integer ano, Integer mes, Authentication authentication) {
        Long userId = extrairUserIdDoToken(authentication);
        
        try {
            return gerarPDFAtendimentos(userId, ano, mes);
        } catch (Exception e) {
            if (e.getMessage().contains("Nenhum atendimento concluído encontrado")) {
                throw new RuntimeException(e.getMessage());
            }
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    private Long extrairUserIdDoToken(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken)) {
            throw new TokenInvalidoException("Autenticação inválida");
        }
        
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
        Jwt jwt = jwtAuth.getToken();
        Long userId = jwt.getClaim("userId");
        
        if (userId == null) {
            throw new TokenInvalidoException("Token não contém informações do usuário");
        }
        
        return userId;
    }
} 