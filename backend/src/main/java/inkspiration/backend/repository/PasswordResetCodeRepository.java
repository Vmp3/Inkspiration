package inkspiration.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.PasswordResetCode;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    
    Optional<PasswordResetCode> findByCpfAndCodeAndUsedFalse(String cpf, String code);
    
    @Modifying
    @Query("UPDATE PasswordResetCode p SET p.used = true WHERE p.cpf = :cpf")
    void markAllAsUsedByCpf(@Param("cpf") String cpf);
    
    @Modifying
    @Query("DELETE FROM PasswordResetCode p WHERE p.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(p) FROM PasswordResetCode p WHERE p.cpf = :cpf AND p.createdAt > :since")
    int countRecentCodesByCpf(@Param("cpf") String cpf, @Param("since") LocalDateTime since);
} 