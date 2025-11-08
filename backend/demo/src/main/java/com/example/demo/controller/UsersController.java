package com.example.demo.controller;

import com.example.demo.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Map OAuth2User attributes to User fields,
        Users user = new Users();
        user.setUsername((String) attributes.get("preferred_username"));
        user.setEmail((String) attributes.get("email"));
        user.setRole("ROLE_USER"); // Default role, spring serurity(at runtime), kasnije mapiramo roleove
        user.setContact_number(null);

        return ResponseEntity.ok(user);
    }
}
