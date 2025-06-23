package inkspiration.backend.repository;

import inkspiration.backend.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    
    @Query("SELECT a FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    Optional<Avaliacao> findByAgendamentoId(@Param("idAgendamento") Long idAgendamento);
    
    @Query("SELECT COUNT(a) > 0 FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    boolean existsByAgendamentoId(@Param("idAgendamento") Long idAgendamento);
} 