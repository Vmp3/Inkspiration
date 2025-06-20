package inkspiration.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Imagem;
import inkspiration.backend.entities.Portifolio;

import java.util.List;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findByPortifolio(Portifolio portifolio);
    List<Imagem> findByPortifolioIdPortifolio(Long idPortifolio);
} 