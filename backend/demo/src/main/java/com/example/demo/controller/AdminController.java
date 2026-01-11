package com.example.demo.controller;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.service.AdminService;
import com.example.demo.service.SupabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final SupabaseService supabaseService;

    public AdminController(AdminService adminService, SupabaseService supabaseService) {
        this.adminService = adminService;
        this.supabaseService = supabaseService;
        System.out.println("AdminController initialized");
    }

    @PostMapping("/createuser")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        System.out.println("POST /api/admin/createuser: " + request.getUsername() + ", " + request.getEmail());
        try {
            boolean success = adminService.createUser(request.getUsername(), request.getEmail());

            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "User created successfully");
                response.put("user", Map.of(
                        "username", request.getUsername(),
                        "email", request.getEmail(),
                        "role", "user"
                ));
                return ResponseEntity.ok(response);
            } else {
                //response.put("success", false);
                response.put("message", "Failed to create user");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Error creating user: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getAllUsers() {
        System.out.println("GET /api/admin/users");
        try {
            List<Map<String, Object>> users = supabaseService.getAllUsers();
            System.out.println("Fetched " + users.size() + " users for admin");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
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
}