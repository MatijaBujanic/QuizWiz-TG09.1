package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.repository.SupabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService implements UserService {

    private final SupabaseRepository supabaseRepository;

    public UsersService(SupabaseRepository supabaseRepository) {
        this.supabaseRepository = supabaseRepository;
    }

    @Override
    public Optional<Users> findByUsername(String username) {
        return supabaseRepository.findByColumn("users", "username", username, Users.class);
    }

    @Override
    public Optional<Users> findByEmail(String email) {
        return supabaseRepository.findByColumn("users", "email", email, Users.class);
    }

    @Override
    public Users save(Users user) {
        return supabaseRepository.save("users", user, true);
    }

    @Override
    public List<Users> findAll() {
        return supabaseRepository.findAll("users", "order=created_at.desc", Users.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}