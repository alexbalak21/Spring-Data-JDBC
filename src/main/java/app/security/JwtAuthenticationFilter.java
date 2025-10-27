package app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Main method to process the HTTP request and authenticate using JWT.
     * 
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if an error occurs during the filter chain
     * @throws IOException      if an I/O error occurs during the filter chain
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. Get the Authorization header from the HTTP request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if the Authorization header exists and starts with "Bearer "
        //    If not, skip JWT processing and continue with the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the JWT token (removing "Bearer " prefix)
        jwt = authHeader.substring(7);
        // 4. Extract the username (email) from the JWT token
        userEmail = jwtService.extractUsername(jwt);

        // 5. If we have a username and there's no existing authentication in the security context
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. Load user details from the database using the username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // 7. Validate the JWT token
            if (jwtService.validateToken(jwt, userEmail)) {
                // 8. Create an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,  // credentials are null because we're using JWT
                        userDetails.getAuthorities()  // user's roles/permissions
                );
                
                // 9. Add additional request details to the authentication token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // 10. Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 11. Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
