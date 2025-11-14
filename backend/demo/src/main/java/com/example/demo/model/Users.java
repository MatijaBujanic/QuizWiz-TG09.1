package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Users {

    private Long userId;
    private String username;
    private String email;
    private String password;
    private String contact_number;
    private String role;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;


    public Users() {
    }

    public Users(Long userId, String username, String email, String password, String contact_number, String role, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.contact_number = contact_number;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getContact_number() { return contact_number; }
    public void setContact_number(String contact_number) { this.contact_number = contact_number; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
