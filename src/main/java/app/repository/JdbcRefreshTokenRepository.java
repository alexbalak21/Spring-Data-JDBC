// app/repository/JdbcRefreshTokenRepository.java
package app.repository;

import app.model.RefreshToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class JdbcRefreshTokenRepository implements RefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRefreshTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<RefreshToken> refreshTokenRowMapper = (rs, rowNum) -> {
        RefreshToken token = new RefreshToken();
        token.setId(rs.getLong("id"));
        token.setUserId(rs.getLong("user_id"));
        token.setToken(rs.getString("token"));
        token.setExpiresAt(rs.getTimestamp("expires_at").toInstant());
        token.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return token;
    };

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, refreshTokenRowMapper, token));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        String sql = "SELECT * FROM refresh_tokens WHERE user_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, refreshTokenRowMapper, userId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int save(RefreshToken refreshToken) {
        if (refreshToken.getId() == null) {
            // Insert new token
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO refresh_tokens (user_id, token, expires_at) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setLong(1, refreshToken.getUserId());
                ps.setString(2, refreshToken.getToken());
                ps.setTimestamp(3, Timestamp.from(refreshToken.getExpiresAt()));
                return ps;
            }, keyHolder);
            
            if (keyHolder.getKey() != null) {
                refreshToken.setId(keyHolder.getKey().longValue());
                return 1;
            }
            return 0;
        } else {
            // Update existing token
            return jdbcTemplate.update(
                "UPDATE refresh_tokens SET token = ?, expires_at = ? WHERE id = ?",
                refreshToken.getToken(),
                Timestamp.from(refreshToken.getExpiresAt()),
                refreshToken.getId()
            );
        }
    }

    @Override
    public int deleteByUserId(Long userId) {
        return jdbcTemplate.update("DELETE FROM refresh_tokens WHERE user_id = ?", userId);
    }

    @Override
    public int deleteByToken(String token) {
        return jdbcTemplate.update("DELETE FROM refresh_tokens WHERE token = ?", token);
    }
}