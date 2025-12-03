package app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";  // This should resolve to src/main/resources/templates/index.html
    }
}