package inkspiration.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import inkspiration.backend.dto.PortfolioDTO;
import inkspiration.backend.entities.Portfolio;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.exception.profissional.ProfissionalNaoEncontradoException;
import inkspiration.backend.exception.portfolio.PortfolioAtualizacaoException;
import inkspiration.backend.exception.portfolio.PortfolioNaoEncontradoException;
import inkspiration.backend.exception.portfolio.PortfolioRemocaoException;
import inkspiration.backend.repository.PortfolioRepository;
import inkspiration.backend.repository.ProfissionalRepository;
import inkspiration.backend.security.AuthorizationService;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AuthorizationService authorizationService;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, 
                            ProfissionalRepository profissionalRepository,
                            AuthorizationService authorizationService) {
        this.portfolioRepository = portfolioRepository;
        this.profissionalRepository = profissionalRepository;
        this.authorizationService = authorizationService;
    }

    public Page<Portfolio> listarTodos(Pageable pageable) {
        return portfolioRepository.findAll(pageable);
    }

    @Transactional
    public Portfolio criar(PortfolioDTO dto) {
        Portfolio portfolio = new Portfolio();
        preencherPortfolio(portfolio, dto);
        
        portfolio = portfolioRepository.save(portfolio);
        
        // Se fornecido, associar ao profissional
        if (dto.getIdProfissional() != null) {
            Profissional profissional = profissionalRepository.findById(dto.getIdProfissional())
                .orElseThrow(() -> new ProfissionalNaoEncontradoException("Profissional não encontrado com ID: " + dto.getIdProfissional()));
            
            profissional.setPortfolio(portfolio);
            portfolio.setProfissional(profissional);
            profissionalRepository.save(profissional);
        }
        
        return portfolio;
    }

    @Transactional
    public Portfolio atualizar(Long id, PortfolioDTO dto) {
        Portfolio portfolio = buscarPorId(id);
        preencherPortfolio(portfolio, dto);
        
        if (dto.getIdProfissional() != null) {
            if (portfolio.getProfissional() != null && 
                !portfolio.getProfissional().getIdProfissional().equals(dto.getIdProfissional())) {
                
                Profissional antigoProfissional = portfolio.getProfissional();
                antigoProfissional.setPortfolio(null);
                portfolio.setProfissional(null);
                profissionalRepository.save(antigoProfissional);
            }
            
            Profissional novoProfissional = profissionalRepository.findById(dto.getIdProfissional())
                .orElseThrow(() -> new ProfissionalNaoEncontradoException("Profissional não encontrado com ID: " + dto.getIdProfissional()));
            
            novoProfissional.setPortfolio(portfolio);
            portfolio.setProfissional(novoProfissional);
            profissionalRepository.save(novoProfissional);
        }
        
        return portfolioRepository.save(portfolio);
    }

    public Portfolio buscarPorId(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new PortfolioNaoEncontradoException("Portfolio não encontrado com ID: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        Portfolio portfolio = buscarPorId(id);
        
        if (portfolio.getProfissional() != null) {
            Profissional profissional = portfolio.getProfissional();
            profissional.setPortfolio(null);
            portfolio.setProfissional(null);
            profissionalRepository.save(profissional);
        }
        
        portfolioRepository.delete(portfolio);
    }

    private void preencherPortfolio(Portfolio portfolio, PortfolioDTO dto) {
        portfolio.setDescricao(dto.getDescricao());
        portfolio.setExperiencia(dto.getExperiencia());
        portfolio.setEspecialidade(dto.getEspecialidade());
        portfolio.setWebsite(dto.getWebsite());
        portfolio.setTiktok(dto.getTiktok());
        portfolio.setInstagram(dto.getInstagram());
        portfolio.setFacebook(dto.getFacebook());
        portfolio.setTwitter(dto.getTwitter());
    }
    
    public PortfolioDTO converterParaDto(Portfolio portfolio) {
        if (portfolio == null) return null;
        
        Long idProfissional = null;
        if (portfolio.getProfissional() != null) {
            idProfissional = portfolio.getProfissional().getIdProfissional();
        }
        
        return new PortfolioDTO(
            portfolio.getIdPortfolio(),
            idProfissional,
            portfolio.getDescricao(),
            portfolio.getExperiencia(),
            portfolio.getEspecialidade(),
            portfolio.getWebsite(),
            portfolio.getTiktok(),
            portfolio.getInstagram(),
            portfolio.getFacebook(),
            portfolio.getTwitter()
        );
    }

    public List<PortfolioDTO> listarComAutorizacao(Pageable pageable) {
        authorizationService.requireAdmin();
        
        Page<Portfolio> portfolios = listarTodos(pageable);
        
        return portfolios.getContent().stream()
                .map(this::converterParaDto)
                .collect(Collectors.toList());
    }

    public PortfolioDTO buscarPorIdComValidacao(Long id) {
        Portfolio portfolio = buscarPorId(id);
        return converterParaDto(portfolio);
    }

    public PortfolioDTO atualizarComValidacao(Long id, PortfolioDTO dto) {
        try {
            Portfolio portfolio = atualizar(id, dto);
            return converterParaDto(portfolio);
        } catch (PortfolioNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new PortfolioAtualizacaoException(e.getMessage());
        }
    }

    public void deletarComValidacao(Long id) {
        try {
            deletar(id);
        } catch (PortfolioNaoEncontradoException e) {
            throw e;
        } catch (Exception e) {
            throw new PortfolioRemocaoException(e.getMessage());
        }
    }
} 