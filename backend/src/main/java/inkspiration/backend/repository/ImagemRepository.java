package inkspiration.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Imagem;

import java.util.List;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findByPortfolioIdPortfolio(Long idPortfolio);
} 