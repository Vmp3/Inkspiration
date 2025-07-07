package inkspiration.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    
    @Query("SELECT p FROM Portfolio p WHERE p.profissional.usuario.idUsuario = :idUsuario")
    Optional<Portfolio> findByUsuarioId(@Param("idUsuario") Long idUsuario);
    
    Optional<Portfolio> findByProfissional_IdProfissional(Long idProfissional);
    
    Optional<Portfolio> findByProfissional_Usuario_IdUsuario(Long idUsuario);
} 