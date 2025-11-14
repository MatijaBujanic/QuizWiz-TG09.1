package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final UsersService usersService;

    public UserRoleController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRoleByEmail(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email parameter is required"));
        }

        try {
            Optional<Users> user = usersService.findByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "role", user.get().getRole(),
                        "username", user.get().getUsername()
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found",
                        "email", email
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error",
                    "message", e.getMessage()
            ));
        }
    }
}