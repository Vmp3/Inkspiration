package inkspiration.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
} 