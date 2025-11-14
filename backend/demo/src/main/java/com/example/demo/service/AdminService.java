package com.example.demo.service;

import com.example.demo.model.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminService {

    private final UsersService usersService;

    public AdminService(UsersService usersService) {
        this.usersService = usersService;
    }

    public Users createUser(String username, String email) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check if user already exists
        Optional<Users> existingUser = usersService.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        Optional<Users> existingUsername = usersService.findByUsername(username);
        if (existingUsername.isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }

        // Create new user
        Users newUser = new Users();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim().toLowerCase());
        newUser.setPassword(""); // Empty password for admin-created users
        newUser.setContact_number(null);
        newUser.setRole("user"); // Default role
        newUser.setCreatedAt(LocalDateTime.now());

        try {
            return usersService.save(newUser);
        } catch (Exception e) {
            throw new IllegalArgumentException("Database constraint violation: " + e.getMessage());
        }
    }
}