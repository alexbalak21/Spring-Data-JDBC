package app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(nullable = false, unique = true)
    private String jti;
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;


    public TokenBlacklist() {
    }
    
    public TokenBlacklist(Long userId, String jti, LocalDateTime expiresAt) {
        this.userId = userId;
        this.jti = jti;
        this.expiresAt = expiresAt;
    }
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "{\"id\"=" + id + ", \"userId\"=" + userId + ", \"jti\"=" + jti + ", \"expiresAt\"=" + expiresAt + "}";
    }
    
    // Getters and setters for id
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
