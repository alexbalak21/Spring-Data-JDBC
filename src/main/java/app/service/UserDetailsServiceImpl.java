package app.service;

import app.model.User;
import app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Core service for loading user-specific data during authentication.
 * 
 * This service is called by Spring Security when a user tries to authenticate.
 * It's responsible for:
 * 1. Loading user data from the database using the provided username/email
 * 2. Converting the User entity into a UserDetails object that Spring Security can use
 * 3. Throwing appropriate exceptions if the user is not found or disabled
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address (used as username in this application).
     * 
     * @param email The email address of the user to load
     * @return UserDetails containing the user's information and authorities
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user in the database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert our User entity to Spring Security's UserDetails
        return UserDetailsImpl.build(user);
    }
}
