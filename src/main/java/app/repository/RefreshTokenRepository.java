package app.repository;

import app.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    int save(RefreshToken refreshToken);
    int deleteByUserId(Long userId);
    int deleteByToken(String token);
}