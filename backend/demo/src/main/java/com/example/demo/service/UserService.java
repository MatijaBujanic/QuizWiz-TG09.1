package com.example.demo.service;

import com.example.demo.model.Users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    Users save(Users user);
    List<Users> findAll();
    boolean existsByEmail(String email);
}
