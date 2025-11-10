package app.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {
    
    @GetMapping("/api")
    public Map<String, String> home() {
        return Map.of("message", "Welcome to the API!");
    }
}
