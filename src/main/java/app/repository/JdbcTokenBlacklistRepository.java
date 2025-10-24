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
        token.setId(rs.getLong("id"));
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
    public Optional<TokenBlacklist> findById(Long id) {
        String sql = "SELECT * FROM token_blacklist WHERE id = ?";
        List<TokenBlacklist> tokens = jdbcTemplate.query(sql, tokenBlacklistRowMapper, id);
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
        if (token.getId() == null) {
            return create(token);
        } else {
            update(token);
            return token;
        }
    }

    private TokenBlacklist create(TokenBlacklist token) {
        String sql = "INSERT INTO token_blacklist (jti, expires_at) VALUES (?, ?)";
        var keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, token.getJti());
            ps.setTimestamp(2, Timestamp.valueOf(token.getExpiresAt()));
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated key after insert");
        }
        long id = key.longValue();
        return findById(id).orElseThrow(() -> new IllegalStateException("Failed to retrieve saved token"));
    }

    private void update(TokenBlacklist token) {
        String sql = "UPDATE token_blacklist SET jti = ?, expires_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, 
            token.getJti(),
            Timestamp.valueOf(token.getExpiresAt()),
            token.getId()
        );
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM token_blacklist WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int deleteByExpiresAtBefore(LocalDateTime now) {
        String sql = "DELETE FROM token_blacklist WHERE expires_at < ?";
        return jdbcTemplate.update(sql, Timestamp.valueOf(now));
    }
}
