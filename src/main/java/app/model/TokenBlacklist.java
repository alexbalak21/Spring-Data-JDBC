package app.model;

import java.time.LocalDateTime;

public class TokenBlacklist {
    private Long id;
    private String jti;
    private LocalDateTime expiresAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "{\"id\"=" + id + ", \"jti\"=" + jti + ", \"expiresAt\"=" + expiresAt + "}";
    }
}
