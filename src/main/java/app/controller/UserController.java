package app.controller;

import app.dto.UserResponse;
import app.model.User;
import app.repository.UserRepository;
import app.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(userDetails);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserResponse> userResponses = users.stream()
            .map(user -> {
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setName(user.getUsername()); // Assuming username is the name
                response.setEmail(user.getEmail());
                return response;
            })
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(userResponses);
    }
}
