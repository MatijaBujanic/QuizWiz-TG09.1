package com.example.demo.dto;

public class CreateUserRequest {
    private String username;
    private String email;

    // Constructors
    public CreateUserRequest() {}

    public CreateUserRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}