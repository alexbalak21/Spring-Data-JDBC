package app.model;

import java.time.LocalDateTime;

public class TokenBlacklist {
    private Long userId;
    private String jti;
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
        return "{\"userId\"=" + userId + ", \"jti\"=" + jti + ", \"expiresAt\"=" + expiresAt + "}";
    }
}
