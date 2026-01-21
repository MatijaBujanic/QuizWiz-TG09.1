package com.example.demo.mapper;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class RoleMapper {
    private RoleMapper() {}

    public static SimpleGrantedAuthority toAuthority(String role) {
        if (role == null || role.isBlank()) return new SimpleGrantedAuthority("ROLE_USER");
        return new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase());
    }
}


