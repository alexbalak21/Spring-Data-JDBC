package app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

import app.dto.UserDto;
import app.model.User;
import app.model.UserRole;
import app.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping
    public String getAllUsers() {
        return "All users";
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            System.out.println("Fetching user with ID: " + id);
            Optional<User> user = userRepository.findById(id);
            
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching user: " + e.getMessage());
        }
    }

    /**
     * Creates a new user with the provided user details
     * @param userDto The user data transfer object containing name, email, and password
     * @return A response entity with the created user's ID and HTTP status
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            System.out.println("Received request to create user with email: " + userDto.getEmail());
            
            // Check if user with email already exists
            System.out.println("Checking if user exists with email: " + userDto.getEmail());
            Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
            if (existingUser.isPresent()) {
                System.out.println("User already exists with email: " + userDto.getEmail());
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT, 
                    "User with this email already exists"
                );
            }
            
            // Create and save new user
            System.out.println("Creating new user: " + userDto.getEmail());
            User newUser = new User();
            newUser.setName(userDto.getName());
            newUser.setEmail(userDto.getEmail());
            newUser.setPassword(userDto.getPassword()); // In a real app, password should be hashed
            newUser.setRole(UserRole.USER); // Set the role using the UserRole enum
            
            System.out.println("Attempting to save user to database...");
            User savedUser = userRepository.save(newUser);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully with ID: " + savedUser.getId());
                
        } catch (ResponseStatusException e) {
            System.err.println("ResponseStatusException: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error creating user: " + e.getMessage(),
                e
            );
        }
    }

    @PutMapping("/{id}")
    public String updateUser() {
        return "Update user";
    }

    @DeleteMapping("/{id}")
    public String deleteUser() {
        return "Delete user";
    }

}
