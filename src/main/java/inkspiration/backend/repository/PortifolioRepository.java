package inkspiration.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Portifolio;

@Repository
public interface PortifolioRepository extends JpaRepository<Portifolio, Long> {
} 