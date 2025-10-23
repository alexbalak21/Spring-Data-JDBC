package app.repository;

import app.model.User;
import app.model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setRole(UserRole.valueOf(rs.getString("role").toUpperCase()));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                         rs.getTimestamp("created_at").toLocalDateTime() : null);
        user.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                         rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return user;
    };

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            update(user);
            return user;
        }
    }

    private User create(User user) {
        String sql = "INSERT INTO users (name, email, role, password) VALUES (?, ?, ?, ?)";
        var keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name().toLowerCase());
            ps.setString(4, user.getPassword());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated key after insert");
        }
        long id = key.longValue();
        return findById(id).orElseThrow(() -> new IllegalStateException("Failed to retrieve saved user"));
    }

    @Override
    public int update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, password = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        int updated = jdbcTemplate.update(sql, 
            user.getName(), 
            user.getEmail(), 
            user.getRole().name().toLowerCase(), 
            user.getPassword(), 
            user.getId()
        );
        
        if (updated > 0) {
            // Refresh the user to get the updated timestamps
            findById(user.getId()).ifPresent(updatedUser -> {
                user.setCreatedAt(updatedUser.getCreatedAt());
                user.setUpdatedAt(updatedUser.getUpdatedAt());
            });
        }
        return updated;
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
