package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.PortifolioDTO;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.repository.PortifolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;

@Service
public class PortifolioService {

    private final PortifolioRepository portifolioRepository;
    private final ProfissionalRepository profissionalRepository;

    @Autowired
    public PortifolioService(PortifolioRepository portifolioRepository, ProfissionalRepository profissionalRepository) {
        this.portifolioRepository = portifolioRepository;
        this.profissionalRepository = profissionalRepository;
    }

    public Page<Portifolio> listarTodos(Pageable pageable) {
        return portifolioRepository.findAll(pageable);
    }

    @Transactional
    public Portifolio criar(PortifolioDTO dto) {
        Portifolio portifolio = new Portifolio();
        preencherPortifolio(portifolio, dto);
        
        // Salvar o portfólio
        portifolio = portifolioRepository.save(portifolio);
        
        // Se fornecido, associar ao profissional
        if (dto.getIdProfissional() != null) {
            Profissional profissional = profissionalRepository.findById(dto.getIdProfissional())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + dto.getIdProfissional()));
            
            profissional.setPortifolio(portifolio);
            portifolio.setProfissional(profissional);
            profissionalRepository.save(profissional);
        }
        
        return portifolio;
    }

    @Transactional
    public Portifolio atualizar(Long id, PortifolioDTO dto) {
        Portifolio portifolio = buscarPorId(id);
        preencherPortifolio(portifolio, dto);
        
        // Se fornecido um novo ID de profissional, atualizar a associação
        if (dto.getIdProfissional() != null) {
            // Se já estava associado a outro profissional, remover a associação anterior
            if (portifolio.getProfissional() != null && 
                !portifolio.getProfissional().getIdProfissional().equals(dto.getIdProfissional())) {
                
                Profissional antigoProfissional = portifolio.getProfissional();
                antigoProfissional.setPortifolio(null);
                portifolio.setProfissional(null);
                profissionalRepository.save(antigoProfissional);
            }
            
            // Associar ao novo profissional
            Profissional novoProfissional = profissionalRepository.findById(dto.getIdProfissional())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado com ID: " + dto.getIdProfissional()));
            
            novoProfissional.setPortifolio(portifolio);
            portifolio.setProfissional(novoProfissional);
            profissionalRepository.save(novoProfissional);
        }
        
        return portifolioRepository.save(portifolio);
    }

    public Portifolio buscarPorId(Long id) {
        return portifolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portifolio não encontrado com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        Portifolio portifolio = buscarPorId(id);
        
        // Remover a associação com profissional antes de deletar
        if (portifolio.getProfissional() != null) {
            Profissional profissional = portifolio.getProfissional();
            profissional.setPortifolio(null);
            portifolio.setProfissional(null);
            profissionalRepository.save(profissional);
        }
        
        portifolioRepository.delete(portifolio);
    }

    private void preencherPortifolio(Portifolio portifolio, PortifolioDTO dto) {
        portifolio.setDescricao(dto.getDescricao());
        portifolio.setExperiencia(dto.getExperiencia());
        portifolio.setEspecialidade(dto.getEspecialidade());
        portifolio.setWebsite(dto.getWebsite());
        portifolio.setTiktok(dto.getTiktok());
        portifolio.setInstagram(dto.getInstagram());
        portifolio.setFacebook(dto.getFacebook());
        portifolio.setTwitter(dto.getTwitter());
    }
    
    public PortifolioDTO converterParaDto(Portifolio portifolio) {
        if (portifolio == null) return null;
        
        // Buscar o ID do profissional associado, se houver
        Long idProfissional = null;
        if (portifolio.getProfissional() != null) {
            idProfissional = portifolio.getProfissional().getIdProfissional();
        }
        
        return new PortifolioDTO(
            portifolio.getIdPortifolio(),
            idProfissional,
            portifolio.getDescricao(),
            portifolio.getExperiencia(),
            portifolio.getEspecialidade(),
            portifolio.getWebsite(),
            portifolio.getTiktok(),
            portifolio.getInstagram(),
            portifolio.getFacebook(),
            portifolio.getTwitter()
        );
    }
} 