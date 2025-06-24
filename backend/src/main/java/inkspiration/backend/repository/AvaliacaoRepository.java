package inkspiration.backend.repository;

import inkspiration.backend.entities.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    
    @Query("SELECT a FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    Optional<Avaliacao> findByAgendamentoId(@Param("idAgendamento") Long idAgendamento);
    
    @Query("SELECT COUNT(a) > 0 FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    boolean existsByAgendamentoId(@Param("idAgendamento") Long idAgendamento);
    
    @Query("SELECT COALESCE(ROUND(AVG(CAST(a.rating AS double)), 1), 0.0) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :idProfissional")
    BigDecimal calcularMediaAvaliacoesPorProfissional(@Param("idProfissional") Long idProfissional);
    
    @Query("SELECT a FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :idProfissional ORDER BY a.rating DESC, a.idAvaliacao DESC")
    Page<Avaliacao> findByProfissionalId(@Param("idProfissional") Long idProfissional, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :idProfissional")
    Long countByProfissionalId(@Param("idProfissional") Long idProfissional);
} 