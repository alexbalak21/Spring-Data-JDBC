package app.config;

import app.security.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration  // Marks this class as a source of bean definitions
@EnableWebSecurity  // Enables Spring Security's web security support
public class SecurityConfig {

    // JWT Authentication Filter that will be used to validate tokens
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor injection of the JWT filter
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Defines the security filter chain for the application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection (common in REST APIs that use token-based auth)
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new CorsConfiguration();
                corsConfig.setAllowedOrigins(List.of(
                    "http://localhost:3000",  // React dev server
                    "http://127.0.0.1:3000"
                ));
                corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfig.setAllowedHeaders(List.of("*"));
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints that don't require authentication
                .requestMatchers(
                    "/api",
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/refresh-token",
                    "/api/users"  // Allow unauthenticated access to users list for demo
                ).permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // Allow CORS preflight requests
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                // All other requests must be authenticated
                .anyRequest().authenticated()
            )
            
            // Configure session management to be stateless (no HTTP session)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Add our JWT filter before the default username/password authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Exposes the AuthenticationManager as a bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configures the password encoder (BCrypt in this case)
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is a strong hashing algorithm that automatically handles salting
        return new BCryptPasswordEncoder();
    }
}