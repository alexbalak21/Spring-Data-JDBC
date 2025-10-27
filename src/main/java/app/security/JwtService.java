package app.security;

import app.model.User;
import app.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import app.model.TokenBlacklist;
import app.repository.UserRepository;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.UUID;

/**
 * Service for handling JWT (JSON Web Token) operations including generation, validation,
 * and extraction of claims from JWT tokens.
 */
@Service
public class JwtService {
    
    // Secret key used for signing the JWT (injected from application properties)
    @Value("${jwt.secret}")
    private String secret;
    
    // Token expiration time in milliseconds (default: 24 hours)
    @Value("${jwt.expiration:86400000}")
    private long expirationMs;

    // Repository for checking blacklisted tokens
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection
     * @param tokenBlacklistRepository Repository for checking token blacklist status
     */
    public JwtService(TokenBlacklistRepository tokenBlacklistRepository, UserRepository userRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Generates a JWT token for the given user
     * @param user The user for whom to generate the token
     * @return A signed JWT token
     */
    public String generateToken(User user) {
        // Add custom claims to the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        return createToken(claims, user.getEmail());
    }
    
    /**
     * Creates a JWT token with the specified claims and subject
     * @param claims The claims to include in the token
     * @param subject The subject (typically username or email) of the token
     * @return A signed JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        // Generate a unique ID for the token (JWT ID)
        String id = UUID.randomUUID().toString();
        
        // Build and return the JWT token
        return Jwts.builder()
                .setClaims(claims)                  // Add custom claims
                .setId(id)                          // Set unique token ID
                .setSubject(subject)                // Set subject (username/email)
                .setIssuedAt(new Date(System.currentTimeMillis()))  // Set issue time
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))  // Set expiration
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Sign with the secret key
                .compact();                         // Generate the token string
    }
    
    /**
     * Validates if the given token is valid for the specified username
     * @param token The JWT token to validate
     * @param username The username to validate against
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        final String jti = extractClaim(token, Claims::getId);
        // Check if:
        // 1. The token's subject matches the username
        // 2. The token is not expired
        // 3. The token is not blacklisted
        return (tokenUsername.equals(username) && !isTokenExpired(token) && !isBlacklisted(jti));
    }

    public void blacklistToken(String token) {
        final String jti = extractClaim(token, Claims::getId);
        final String email = extractUsername(token);
        final long userId = userRepository.findByEmail(email).get().getId();
        final Date expiresAt = extractExpiration(token);
        tokenBlacklistRepository.save(new TokenBlacklist(userId, jti, expiresAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
    }
    
    /**
     * Extracts the username (subject) from the token
     * @param token The JWT token
     * @return The username (subject) from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extracts the expiration date from the token
     * @param token The JWT token
     * @return The expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Generic method to extract a specific claim from the token
     * @param <T> The type of the claim to extract
     * @param token The JWT token
     * @param claimsResolver Function to extract the claim
     * @return The extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from the token
     * @param token The JWT token
     * @return All claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())  // Set the signing key for verification
                .build()
                .parseClaimsJws(token)           // Parse the token
                .getBody();                      // Get the claims
    }
    
    /**
     * Checks if the token is expired
     * @param token The JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Checks if the token is blacklisted
     * @param jti The JWT ID to check
     * @return true if the token is blacklisted, false otherwise
     */
    private Boolean isBlacklisted(String jti) {
        return tokenBlacklistRepository.existsByJti(jti);
    }
    
    /**
     * Generates the signing key from the secret
     * @return The signing key for JWT verification
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);  // Create a key suitable for HMAC-SHA algorithms
    }
}
