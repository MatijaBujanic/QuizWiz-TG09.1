package com.example.demo.controller;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.Users;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.security.RequireRole;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SupabaseRepository supabaseRepository;
    private final AuthenticationService authenticationService;

    public AdminController(SupabaseRepository supabaseRepository, AuthenticationService authenticationService) {
        this.supabaseRepository = supabaseRepository;
        this.authenticationService = authenticationService;
        System.out.println("AdminController initialized with SupabaseRepository");
    }

    @PostMapping("/createuser")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        System.out.println("POST /api/admin/createuser: " + request.getUsername() + ", " + request.getEmail());

        try {
            // Validacija ulaza
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Username is required");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            String emailNormalized = request.getEmail().trim().toLowerCase();

            // Provjera postoji li korisnik s tim emailom
            Optional<Users> existingUserOpt = supabaseRepository.findByColumn("users", "email", emailNormalized, Users.class);

            Users user;
            if (existingUserOpt.isPresent()) {
                // Ako postoji, uzmi tog korisnika i ažuriraj podatke
                user = existingUserOpt.get();
                user.setUsername(request.getUsername().trim());
                // Možeš ažurirati i druge podatke ako želiš
            } else {
                // Ako ne postoji, kreiraj novog korisnika
                user = new Users();  // VAŽNO: ovdje dodijelimo objekt varijabli 'user'
                user.setUsername(request.getUsername().trim());
                user.setEmail(emailNormalized);
                user.setPassword("");
                user.setContact_number(null);
                user.setRole("user");
            }

            // Pozovi upsert da kreira ili ažurira korisnika
            Users savedUser = supabaseRepository.upsert("users", user);

            System.out.println("User upserted successfully: " + savedUser.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", existingUserOpt.isPresent() ? "User updated successfully" : "User created successfully");
            response.put("user", Map.of(
                    "userId", savedUser.getUserId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "role", savedUser.getRole()
            ));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.out.println("Validation error creating user: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.out.println("Error creating/updating user: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create or update user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }



    @GetMapping("/users")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> getAllUsers() {
        System.out.println("GET /api/admin/users");

        try {
            // Get all users using repository
            List<Users> users = supabaseRepository.findAll("users", "order=created_at.desc", Users.class);
            System.out.println("Fetched " + users.size() + " users for admin");

            // Convert Users objects to Map for response
            List<Map<String, Object>> userList = new ArrayList<>();
            for (Users user : users) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("user_id", user.getUserId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail());
                userMap.put("contact_number", user.getContact_number());
                userMap.put("role", user.getRole());
                userMap.put("created_at", user.getCreatedAt());
                userList.add(userMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", userList);
            response.put("count", users.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error in getAllUsers: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error fetching users: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @DeleteMapping("/users/{userId}")
    @RequireRole({"ADMIN"})
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        System.out.println("DELETE /api/admin/users/" + userId);

        try {
            // Validate input
            if (userId == null || userId.trim().isEmpty()) {
                System.out.println("Error: User ID is required");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User ID is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Convert userId to Long
            Long userLongId;
            try {
                userLongId = Long.parseLong(userId);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid user ID format: " + userId);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid user ID format");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Check if user exists before deleting
            Optional<Users> existingUser = supabaseRepository.findByColumn("users", "user_id", userLongId, Users.class);
            if (!existingUser.isPresent()) {
                System.out.println("User not found with ID: " + userId);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found with ID: " + userId);
                return ResponseEntity.status(404).body(errorResponse);
            }

            // Prevent deleting admin users (optional)
            Users userToDelete = existingUser.get();
            if ("admin".equalsIgnoreCase(userToDelete.getRole())) {
                System.out.println("Cannot delete admin user: " + userToDelete.getEmail());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Cannot delete admin users");
                return ResponseEntity.status(403).body(errorResponse);
            }

            // Delete the user using repository
            supabaseRepository.delete("users", "user_id", userLongId);

            System.out.println("Successfully deleted user: " + userToDelete.getEmail() + " (ID: " + userId + ")");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully");
            response.put("deletedUser", Map.of(
                    "userId", userId,
                    "email", userToDelete.getEmail(),
                    "username", userToDelete.getUsername()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error in deleteUser: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting user: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}