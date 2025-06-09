package inkspiration.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Disponibilidade;
import inkspiration.backend.entities.Profissional;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {
    Optional<Disponibilidade> findByProfissional(Profissional profissional);
    Optional<Disponibilidade> findByProfissional_IdProfissional(Long idProfissional);
    boolean existsByProfissional_IdProfissional(Long idProfissional);
} 