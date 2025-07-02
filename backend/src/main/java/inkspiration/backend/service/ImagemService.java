package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.ImagemDTO;
import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.imagem.ImagemNaoEncontradaException;
import inkspiration.backend.exception.imagem.ImagemProcessamentoException;
import inkspiration.backend.exception.imagem.ImagemRemocaoException;
import inkspiration.backend.exception.imagem.ImagemSalvamentoException;
import inkspiration.backend.repository.ImagemRepository;
import inkspiration.backend.repository.PortfolioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImagemService {

    private final ImagemRepository imagemRepository;
    private final PortfolioRepository portfolioRepository;

    @Autowired
    public ImagemService(ImagemRepository imagemRepository, PortfolioRepository portfolioRepository) {
        this.imagemRepository = imagemRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public List<ImagemDTO> listarPorPortfolio(Long idPortfolio) {
        List<Imagem> imagens = imagemRepository.findByPortfolioIdPortfolio(idPortfolio);
        return imagens.stream().map(this::converterParaDto).collect(Collectors.toList());
    }

    public ImagemDTO buscarPorId(Long id) {
        Imagem imagem = imagemRepository.findById(id)
                .orElseThrow(() -> new ImagemNaoEncontradaException("Imagem não encontrada com ID: " + id));
        return converterParaDto(imagem);
    }

    @Transactional
    public ImagemDTO salvar(ImagemDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(dto.getIdPortfolio())
                .orElseThrow(() -> new PortfolioNaoEncontradoException("Portifólio não encontrado com ID: " + dto.getIdPortfolio()));
        
        Imagem imagem = new Imagem();
        imagem.setImagemBase64(dto.getImagemBase64());
        imagem.setPortfolio(portfolio);
        
        imagem = imagemRepository.save(imagem);
        
        return converterParaDto(imagem);
    }

    @Transactional
    public void deletar(Long id) {
        Imagem imagem = imagemRepository.findById(id)
                .orElseThrow(() -> new ImagemNaoEncontradaException("Imagem não encontrada com ID: " + id));
        
        imagemRepository.delete(imagem);
    }
    
    private ImagemDTO converterParaDto(Imagem imagem) {
        return new ImagemDTO(
            imagem.getIdImagem(),
            imagem.getImagemBase64(),
            imagem.getPortfolio().getIdPortfolio()
        );
    }

    // Novos métodos movidos do controller
    public List<ImagemDTO> listarPorPortfolioComValidacao(Long idPortfolio) {
        try {
            return listarPorPortfolio(idPortfolio);
        } catch (Exception e) {
            throw new ImagemProcessamentoException("Erro ao listar imagens do portfólio: " + e.getMessage());
        }
    }

    public ImagemDTO buscarPorIdComValidacao(Long id) {
        return buscarPorId(id);
    }

    public ImagemDTO salvarComValidacao(ImagemDTO dto) {
        try {
            // Validar tamanho da imagem do portfólio (10MB)
            validarTamanhoImagemPortfolio(dto.getImagemBase64());
            
            // Validar formato da imagem do portfólio
            validarFormatoImagemPortfolio(dto.getImagemBase64());
            
            return salvar(dto);
        } catch (PortfolioNaoEncontradoException e) {
            throw new ImagemSalvamentoException("Portfólio não encontrado com ID: " + dto.getIdPortfolio());
        } catch (Exception e) {
            throw new ImagemSalvamentoException("Erro ao salvar imagem: " + e.getMessage());
        }
    }
    
    private void validarTamanhoImagemPortfolio(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isEmpty()) {
            throw new ImagemProcessamentoException("Dados da imagem não fornecidos");
        }
        
        String base64Data = imagemBase64;
        if (base64Data.contains(",")) {
            base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
        }
        
        long sizeInBytes = (base64Data.length() * 3) / 4;
        
        long maxSizeInBytes = 10 * 1024 * 1024;
        
        if (sizeInBytes > maxSizeInBytes) {
            throw new ImagemProcessamentoException("Imagem do portfólio muito grande. Tamanho máximo permitido: 10MB");
        }
    }
    

    private void validarFormatoImagemPortfolio(String imagemBase64) {
        if (imagemBase64 == null || imagemBase64.isEmpty()) {
            throw new ImagemProcessamentoException("Dados da imagem não fornecidos");
        }
        
        if (!imagemBase64.startsWith("data:image/")) {
            throw new ImagemProcessamentoException("Formato de imagem inválido. Apenas PNG, JPG, JPEG e JFIF são permitidos");
        }
        
        String mimeType = imagemBase64.substring(5, imagemBase64.indexOf(";"));
        
        if (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png")) {
            throw new ImagemProcessamentoException("Formato de imagem inválido. Apenas PNG, JPG, JPEG e JFIF são permitidos");
        }
    }

    public void deletarComValidacao(Long id) {
        try {
            deletar(id);
        } catch (ImagemNaoEncontradaException e) {
            throw e;
        } catch (Exception e) {
            throw new ImagemRemocaoException("Erro ao deletar imagem: " + e.getMessage());
        }
    }
} 