package app.repository;

import app.model.TokenBlacklist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTokenBlacklistRepository implements TokenBlacklistRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTokenBlacklistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TokenBlacklist> tokenBlacklistRowMapper = (rs, rowNum) -> {
        TokenBlacklist token = new TokenBlacklist();
        token.setUserId(rs.getLong("user_id"));
        token.setJti(rs.getString("jti"));
        token.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        return token;
    };

    @Override
    public List<TokenBlacklist> findAll() {
        String sql = "SELECT * FROM token_blacklist";
        return jdbcTemplate.query(sql, tokenBlacklistRowMapper);
    }

    @Override
    public Optional<TokenBlacklist> findByUserId(Long userId) {
        String sql = "SELECT * FROM token_blacklist WHERE user_id = ?";
        List<TokenBlacklist> tokens = jdbcTemplate.query(sql, tokenBlacklistRowMapper, userId);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    @Override
    public Optional<TokenBlacklist> findByJti(String jti) {
        String sql = "SELECT * FROM token_blacklist WHERE jti = ?";
        List<TokenBlacklist> tokens = jdbcTemplate.query(sql, tokenBlacklistRowMapper, jti);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    @Override
    public boolean existsByJti(String jti) {
        String sql = "SELECT COUNT(*) FROM token_blacklist WHERE jti = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, jti);
        return count != null && count > 0;
    }

    @Override
    public TokenBlacklist save(TokenBlacklist token) {
        // If user already has a token, update it, otherwise create a new one
        if (findByUserId(token.getUserId()).isPresent()) {
            update(token);
        } else {
            create(token);
        }
        return token;
    }

    private void create(TokenBlacklist token) {
        String sql = "INSERT INTO token_blacklist (user_id, jti, expires_at) VALUES (?, ?, ?)";
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.NO_GENERATED_KEYS);
            ps.setLong(1, token.getUserId());
            ps.setString(2, token.getJti());
            ps.setTimestamp(3, Timestamp.valueOf(token.getExpiresAt()));
            return ps;
        });
    }

    private void update(TokenBlacklist token) {
        String sql = "UPDATE token_blacklist SET jti = ?, expires_at = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, 
            token.getJti(),
            Timestamp.valueOf(token.getExpiresAt()),
            token.getUserId()
        );
    }

    @Override
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM token_blacklist WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    @Override
    public int deleteByExpiresAtBefore(LocalDateTime now) {
        String sql = "DELETE FROM token_blacklist WHERE expires_at < ?";
        return jdbcTemplate.update(sql, Timestamp.valueOf(now));
    }
}
