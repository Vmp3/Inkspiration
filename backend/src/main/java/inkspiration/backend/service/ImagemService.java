package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portifolio;
import inkspiration.backend.exception.ResourceNotFoundException;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortifolioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final PortifolioRepository portifolioRepository;

    @Autowired
    public ImagemService(ImagemRepository imagemRepository, PortifolioRepository portifolioRepository) {
        this.imagemRepository = imagemRepository;
        this.portifolioRepository = portifolioRepository;
    }

    public List<ImagemDTO> listarPorPortifolio(Long idPortifolio) {
        List<Imagem> imagens = imagemRepository.findByPortifolioIdPortifolio(idPortifolio);
        return imagens.stream().map(this::converterParaDto).collect(Collectors.toList());
    }

    public ImagemDTO buscarPorId(Long id) {
        Imagem imagem = imagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada com ID: " + id));
        return converterParaDto(imagem);
    }

    @Transactional
    public ImagemDTO salvar(ImagemDTO dto) {
        Portifolio portifolio = portifolioRepository.findById(dto.getIdPortifolio())
                .orElseThrow(() -> new ResourceNotFoundException("Portifólio não encontrado com ID: " + dto.getIdPortifolio()));
        
        Imagem imagem = new Imagem();
        imagem.setImagemBase64(dto.getImagemBase64());
        imagem.setPortifolio(portifolio);
        
        imagem = imagemRepository.save(imagem);
        
        return converterParaDto(imagem);
    }

    @Transactional
    public void deletar(Long id) {
        Imagem imagem = imagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem não encontrada com ID: " + id));
        
        imagemRepository.delete(imagem);
    }
    
    private ImagemDTO converterParaDto(Imagem imagem) {
        return new ImagemDTO(
            imagem.getIdImagem(),
            imagem.getImagemBase64(),
            imagem.getPortifolio().getIdPortifolio()
        );
    }
} 