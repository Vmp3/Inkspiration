package inkspiration.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

@Service
public class AgendamentoService {
    
    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeService disponibilidadeService;
    
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
            String descricao, LocalDateTime dtInicio) throws Exception {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Profissional profissional = profissionalRepository.findById(idProfissional)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        if (usuario.getIdUsuario().equals(profissional.getUsuario().getIdUsuario())) {
            throw new RuntimeException("Não é possível agendar consigo mesmo");
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
        
        List<Agendamento> agendamentosUsuario = agendamentoRepository.findByUsuario(usuario)
                .stream()
                .filter(a -> a.getStatus() != StatusAgendamento.CANCELADO)
                .filter(a -> (a.getDtInicio().isBefore(dtFim) && a.getDtFim().isAfter(dtInicioAjustado)))
                .collect(Collectors.toList());
        
        if (!agendamentosUsuario.isEmpty()) {
            throw new RuntimeException("Você já possui outro agendamento nesse horário. Por favor, selecione um horário diferente.");
        }
        
        try {
            boolean estaNoHorarioDeTrabalho = disponibilidadeService.isProfissionalDisponivel(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (!estaNoHorarioDeTrabalho) {
                throw new RuntimeException("O profissional não está trabalhando nesse horário. " +
                        "Horário necessário: " + dtInicioAjustado.toLocalTime() + " às " + dtFim.toLocalTime() +
                        " (" + tipoServico.getDuracaoHoras() + " horas)");
            }
            
            boolean existeConflito = agendamentoRepository.existsConflitingSchedule(
                    idProfissional, dtInicioAjustado, dtFim);
            
            if (existeConflito) {
                throw new RuntimeException("O profissional já possui outro agendamento nesse horário. " +
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
    
    public List<Agendamento> listarPorProfissionalEPeriodo(Long idProfissional, 
            LocalDateTime inicio, LocalDateTime fim) {
        
        return agendamentoRepository.findByProfissionalAndPeriod(idProfissional, inicio, fim);
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
    
    public List<AgendamentoDTO> listarPorProfissionalEPeriodoDTO(Long idProfissional, 
            LocalDateTime inicio, LocalDateTime fim) {
        return listarPorProfissionalEPeriodo(idProfissional, inicio, fim).stream()
                .map(AgendamentoDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Agendamento atualizarStatusAgendamento(Long id, Long idUsuarioLogado, String status) {
        Agendamento agendamento = buscarPorId(id);
        
        if (!agendamento.getUsuario().getIdUsuario().equals(idUsuarioLogado)) {
            throw new RuntimeException("Não autorizado: este agendamento não pertence ao usuário logado");
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
            info.setSpacingAfter(20);
            document.add(info);
            
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
                    
                    document.add(table);
                    
                    Paragraph separator = new Paragraph("------------------------------------------------------");
                    separator.setAlignment(Element.ALIGN_CENTER);
                    document.add(separator);
                } catch (Exception e) {
                    System.err.println("Erro ao processar agendamento #" + agendamento.getIdAgendamento() + ": " + e.getMessage());
                }
            }
            
            Paragraph footer = new Paragraph("Relatório gerado em: " + 
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
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
} 