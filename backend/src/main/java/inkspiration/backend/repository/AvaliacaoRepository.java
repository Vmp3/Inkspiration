package inkspiration.backend.repository;

import inkspiration.backend.entities.Avaliacao;
import inkspiration.backend.entities.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    // Métodos de busca por usuário e profissional
    List<Avaliacao> findByAgendamentoUsuarioIdUsuario(Long idUsuario);
    List<Avaliacao> findByAgendamentoProfissionalIdProfissional(Long idProfissional);
    Page<Avaliacao> findByAgendamentoUsuarioIdUsuario(Long idUsuario, Pageable pageable);
    Page<Avaliacao> findByAgendamentoProfissionalIdProfissional(Long idProfissional, Pageable pageable);
    Optional<Avaliacao> findByAgendamento(Agendamento agendamento);
    Optional<Avaliacao> findByAgendamento_IdAgendamento(Long idAgendamento);
    boolean existsByAgendamento(Agendamento agendamento);

    // Métodos de busca por id do agendamento (versão customizada)
    @Query("SELECT a FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    Optional<Avaliacao> findByAgendamentoId(@Param("idAgendamento") Long idAgendamento);

    @Query("SELECT COUNT(a) > 0 FROM Avaliacao a WHERE a.agendamento.idAgendamento = :idAgendamento")
    boolean existsByAgendamentoId(@Param("idAgendamento") Long idAgendamento);

    // Calcular a média de avaliações de um profissional
    @Query("SELECT COALESCE(ROUND(AVG(CAST(a.rating AS double)), 1), 0.0) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :profissionalId")
    BigDecimal calcularMediaAvaliacoesPorProfissional(@Param("profissionalId") Long profissionalId);

    @Query("SELECT AVG(a.rating) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :profissionalId")
    Double calculateAverageRatingByProfissional(@Param("profissionalId") Long profissionalId);

    // Contar o número de avaliações de um profissional
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :profissionalId")
    Long countByProfissionalId(@Param("profissionalId") Long profissionalId);

    // Contar o número de avaliações com comentário de um profissional
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :profissionalId AND a.descricao IS NOT NULL AND a.descricao != ''")
    Long countByProfissionalIdAndDescricaoNotNull(@Param("profissionalId") Long profissionalId);

    // Busca paginada por avaliações de um profissional
    @Query("SELECT a FROM Avaliacao a WHERE a.agendamento.profissional.idProfissional = :idProfissional ORDER BY a.rating DESC, a.idAvaliacao DESC")
    Page<Avaliacao> findByProfissionalId(@Param("idProfissional") Long idProfissional, Pageable pageable);
} 