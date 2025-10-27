// app/service/AuthService.java
package app.service;

import app.dto.AuthRequest;
import app.dto.AuthResponse;
import app.exception.AuthException;
import app.model.RefreshToken;
import app.model.User;
import app.model.UserRole;
import app.repository.RefreshTokenRepository;
import app.repository.UserRepository;
import app.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void logout(String token) {
        String email = jwtService.extractUsername(token);
        userRepository.findByEmail(email).ifPresent(user -> {
            refreshTokenRepository.deleteByUserId(user.getId());
        });
        jwtService.blacklistToken(token);
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // Find the refresh token in the database
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(() -> new AuthException("Invalid refresh token"));
        
        // Check if token is expired
        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteByToken(refreshToken);
            throw new AuthException("Refresh token was expired. Please log in again.");
        }
        
        // Get the user
        User user = userRepository.findById(token.getUserId())
            .orElseThrow(() -> new AuthException("User not found"));
        
        // Generate new tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = createRefreshToken(user);
        
        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }
    
    private String createRefreshToken(User user) {
        // Delete any existing refresh token for this user
        refreshTokenRepository.deleteByUserId(user.getId());
        
        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS)); // 7 days expiration
        refreshToken.setCreatedAt(Instant.now());
        
        // Save the refresh token
        refreshTokenRepository.save(refreshToken);
        
        return refreshToken.getToken();
    }

    public AuthResponse register(AuthRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER); // Default role
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = createRefreshToken(savedUser);
        
        // Create and return response
        return new AuthResponse(
            accessToken,
            refreshToken,
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getRole().name()
        );
    }

    public AuthResponse login(AuthRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AuthException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = createRefreshToken(user);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }
}