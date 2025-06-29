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
            return salvar(dto);
        } catch (PortfolioNaoEncontradoException e) {
            throw new ImagemSalvamentoException("Portfólio não encontrado com ID: " + dto.getIdPortfolio());
        } catch (Exception e) {
            throw new ImagemSalvamentoException("Erro ao salvar imagem: " + e.getMessage());
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