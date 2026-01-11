package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserService userService;
    private final SupabaseService supabaseService;

    public AdminService(UserService userService, SupabaseService supabaseService) {
        this.userService = userService;
        this.supabaseService = supabaseService;
    }

    public boolean createUser(String username, String email) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // Check if user already exists
        if (userService.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create user data map
        Map<String, Object> user = new HashMap<>();
        user.put("username", username.trim());
        user.put("email", email.trim().toLowerCase());
        user.put("password", "");
        user.put("contact_number", null);
        user.put("role", "user");

        return supabaseService.saveUser(user);
    }


}