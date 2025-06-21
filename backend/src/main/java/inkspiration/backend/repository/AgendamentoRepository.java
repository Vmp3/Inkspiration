package inkspiration.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Agendamento;
import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;
import inkspiration.backend.enums.StatusAgendamento;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByUsuario(Usuario usuario);
    List<Agendamento> findByProfissional(Profissional profissional);
    Page<Agendamento> findByUsuario(Usuario usuario, Pageable pageable);
    Page<Agendamento> findByProfissional(Profissional profissional, Pageable pageable);
    
    @Query("SELECT a FROM Agendamento a WHERE a.profissional.idProfissional = :idProfissional " +
           "AND ((a.dtInicio BETWEEN :inicio AND :fim) OR (a.dtFim BETWEEN :inicio AND :fim))")
    List<Agendamento> findByProfissionalAndPeriod(
            @Param("idProfissional") Long idProfissional,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Agendamento a " +
           "WHERE a.profissional.idProfissional = :idProfissional " +
           "AND ((a.dtInicio BETWEEN :inicio AND :fim) OR (a.dtFim BETWEEN :inicio AND :fim))")
    boolean existsConflitingSchedule(
            @Param("idProfissional") Long idProfissional,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    Page<Agendamento> findByUsuarioAndDtFimAfterOrderByDtInicioAsc(
            Usuario usuario, LocalDateTime dataReferencia, Pageable pageable);
            
    Page<Agendamento> findByUsuarioAndDtFimBeforeOrderByDtInicioDesc(
            Usuario usuario, LocalDateTime dataReferencia, Pageable pageable);
            
    Page<Agendamento> findByProfissionalAndDtFimAfterOrderByDtInicioAsc(
            Profissional profissional, LocalDateTime dataReferencia, Pageable pageable);
            
    Page<Agendamento> findByProfissionalAndDtFimBeforeOrderByDtInicioDesc(
            Profissional profissional, LocalDateTime dataReferencia, Pageable pageable);

    List<Agendamento> findByStatusAndDtFimBefore(StatusAgendamento status, LocalDateTime data);

    @Query("SELECT a FROM Agendamento a WHERE a.usuario.id = :idUsuario AND a.status = :status AND YEAR(a.dtInicio) = :ano ORDER BY a.dtInicio")
    List<Agendamento> findByUsuarioIdAndStatusAndAno(Long idUsuario, StatusAgendamento status, Integer ano);

    @Query("SELECT a FROM Agendamento a WHERE a.profissional.id = :idProfissional AND a.status = :status AND YEAR(a.dtInicio) = :ano AND MONTH(a.dtInicio) = :mes ORDER BY a.dtInicio")
    List<Agendamento> findByProfissionalIdAndStatusAndAnoMes(Long idProfissional, StatusAgendamento status, Integer ano, Integer mes);
} 