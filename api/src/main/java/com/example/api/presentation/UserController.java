package com.example.api.presentation;

import com.example.api.domain.User;
import com.example.api.infrastructure.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<List<User>> getTestUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}