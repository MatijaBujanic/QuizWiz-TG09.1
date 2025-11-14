package com.example.demo.controller;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.model.Users;
import com.example.demo.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/createuser")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
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
                response.put("success", false);
                response.put("message", "Failed to create user");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}