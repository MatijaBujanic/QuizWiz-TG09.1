package com.example.demo.controller;

import com.example.demo.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRoleController {

    private final SupabaseService supabaseService;

    public UserRoleController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
        System.out.println("UserRoleController initialized with SupabaseService");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("GET /api/users/test called");
        return ResponseEntity.ok("UserRoleController is working!");
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRoleByEmail(@RequestParam String email) {
        System.out.println("=== GET /api/users/role START ===");
        System.out.println("Email parameter: '" + email + "'");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email parameter is required"));
        }

        try {
            // Get all users and find the one with matching email
            List<Map<String, Object>> allUsers = supabaseService.getAllUsers();
            System.out.println("Total users in Supabase: " + allUsers.size());

            for (Map<String, Object> user : allUsers) {
                String userEmail = (String) user.get("email");
                System.out.println("Checking user email: " + userEmail);

                if (email.equalsIgnoreCase(userEmail)) {
                    System.out.println("Found matching user!");
                    return ResponseEntity.ok(Map.of(
                            "email", userEmail,
                            "role", user.get("role"),
                            "username", user.get("username"),
                            "userId", user.get("user_id")
                    ));
                }
            }

            System.out.println("User not found after checking " + allUsers.size() + " users");
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found",
                    "email", email
            ));

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Internal server error",
                    "message", e.getMessage()
            ));
        }
    }
}