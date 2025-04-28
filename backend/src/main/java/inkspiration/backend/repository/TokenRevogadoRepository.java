package inkspiration.backend.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.TokenRevogado;


@Repository
public interface TokenRevogadoRepository extends JpaRepository<TokenRevogado, Long> {
    boolean existsByToken(String token);
    
    @Modifying
    @Query("DELETE FROM TokenRevogado t WHERE t.dataRevogacao <= :data")
    void deleteByDataRevogacaoBefore(LocalDateTime data);
} 