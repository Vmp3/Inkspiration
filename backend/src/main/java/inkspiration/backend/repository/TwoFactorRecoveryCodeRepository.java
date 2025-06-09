package inkspiration.backend.repository;

import inkspiration.backend.entities.TwoFactorRecoveryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TwoFactorRecoveryCodeRepository extends JpaRepository<TwoFactorRecoveryCode, Long> {
    
    Optional<TwoFactorRecoveryCode> findByUserIdAndCodeAndUsedFalse(Long userId, String code);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactorRecoveryCode t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TwoFactorRecoveryCode t WHERE t.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
} 