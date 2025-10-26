package app.repository;

import app.model.TokenBlacklist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenBlacklistRepository {
    List<TokenBlacklist> findAll();
    Optional<TokenBlacklist> findByUserId(Long userId);
    Optional<TokenBlacklist> findByJti(String jti);
    boolean existsByJti(String jti);
    TokenBlacklist save(TokenBlacklist token);
    int deleteByUserId(Long userId);
    int deleteByExpiresAtBefore(LocalDateTime now);
}
