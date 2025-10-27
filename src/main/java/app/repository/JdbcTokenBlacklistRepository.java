package app.repository;

import app.model.TokenBlacklist;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing blacklisted JWT tokens in the database.
 * Handles CRUD operations for token blacklist entries.
 */
@Repository
public class JdbcTokenBlacklistRepository implements TokenBlacklistRepository {

    // JdbcTemplate for executing SQL queries
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor for dependency injection of JdbcTemplate
     * @param jdbcTemplate The JdbcTemplate instance to be used for database operations
     */
    public JdbcTokenBlacklistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper to convert database rows into TokenBlacklist objects
    private final RowMapper<TokenBlacklist> tokenBlacklistRowMapper = (rs, rowNum) -> {
        TokenBlacklist token = new TokenBlacklist();
        token.setUserId(rs.getLong("user_id"));
        token.setJti(rs.getString("jti"));
        token.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        return token;
    };

    /**
     * Retrieve all blacklisted tokens from the database
     * @return List of all TokenBlacklist entries
     */
    @Override
    public List<TokenBlacklist> findAll() {
        String sql = "SELECT * FROM token_blacklist";
        return jdbcTemplate.query(sql, tokenBlacklistRowMapper);
    }

    /**
     * Find a blacklisted token by user ID
     * @param userId The ID of the user
     * @return Optional containing the TokenBlacklist if found, empty otherwise
     */
    @Override
    public Optional<TokenBlacklist> findByUserId(Long userId) {
        String sql = "SELECT * FROM token_blacklist WHERE user_id = ?";
        List<TokenBlacklist> tokens = jdbcTemplate.query(sql, tokenBlacklistRowMapper, userId);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    /**
     * Find a blacklisted token by its JWT ID (jti)
     * @param jti The JWT ID to search for
     * @return Optional containing the TokenBlacklist if found, empty otherwise
     */
    @Override
    public Optional<TokenBlacklist> findByJti(String jti) {
        String sql = "SELECT * FROM token_blacklist WHERE jti = ?";
        List<TokenBlacklist> tokens = jdbcTemplate.query(sql, tokenBlacklistRowMapper, jti);
        return tokens.isEmpty() ? Optional.empty() : Optional.of(tokens.get(0));
    }

    /**
     * Check if a token with the given JWT ID exists in the blacklist
     * @param jti The JWT ID to check
     * @return true if the token is blacklisted, false otherwise
     */
    @Override
    public boolean existsByJti(String jti) {
        String sql = "SELECT COUNT(*) FROM token_blacklist WHERE jti = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, jti);
        return count != null && count > 0;
    }

    /**
     * Save or update a token in the blacklist
     * If a token for the user already exists, it will be updated
     * Otherwise, a new entry will be created
     * @param token The TokenBlacklist to save
     * @return The saved TokenBlacklist
     */
    @Override
    public TokenBlacklist save(TokenBlacklist token) {
        if (findByUserId(token.getUserId()).isPresent()) {
            update(token);
        } else {
            create(token);
        }
        return token;
    }

    /**
     * Create a new blacklist entry in the database
     * @param token The TokenBlacklist to create
     */
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

    /**
     * Update an existing blacklist entry in the database
     * @param token The TokenBlacklist with updated values
     */
    private void update(TokenBlacklist token) {
        String sql = "UPDATE token_blacklist SET jti = ?, expires_at = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, 
            token.getJti(),
            Timestamp.valueOf(token.getExpiresAt()),
            token.getUserId()
        );
    }

    /**
     * Delete a blacklist entry by user ID
     * @param userId The ID of the user whose token should be removed from blacklist
     * @return Number of rows affected (should be 0 or 1)
     */
    @Override
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM token_blacklist WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    /**
     * Clean up expired tokens from the blacklist
     * @param now The current timestamp to compare against
     * @return Number of rows deleted
     */
    @Override
    public int deleteByExpiresAtBefore(LocalDateTime now) {
        String sql = "DELETE FROM token_blacklist WHERE expires_at < ?";
        return jdbcTemplate.update(sql, Timestamp.valueOf(now));
    }
}
