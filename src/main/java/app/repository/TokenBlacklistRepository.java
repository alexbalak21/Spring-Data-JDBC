package app.repository;

import app.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    List<TokenBlacklist> findAll();
    Optional<TokenBlacklist> findByUserId(Long userId);
    Optional<TokenBlacklist> findByJti(String jti);
    boolean existsByJti(String jti);
    int deleteByUserId(Long userId);
    int deleteByExpiresAtBefore(LocalDateTime now);
}
