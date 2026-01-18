package com.example.demo.controller;

import com.example.demo.dto.UpdateMeRequest;
import com.example.demo.model.Users;
import com.example.demo.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");

        String role = usersService.findByEmail(email)
                .map(Users::getRole)
                .orElse("user");

        // Map OAuth2User attributes to User fields,
        Users user = new Users();
        user.setUsername((String) attributes.get("preferred_username"));
        user.setEmail(email);
        user.setRole(role);
        user.setContact_number(null);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me/edit")
    public ResponseEntity<Users> updateMe(Authentication authentication, @RequestBody UpdateMeRequest req) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oauth2User.getAttributes().get("email");
        if (email == null || email.isBlank()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Users user = usersService.findByEmail(email)
                .orElseThrow(); // ili kreiraj ako ga nema

        if (req.getUsername() != null && !req.getUsername().trim().isBlank()) {
            user.setUsername(req.getUsername().trim());
        }
        if (req.getContact_number() != null) {
            String cn = req.getContact_number().trim();
            user.setContact_number(cn.isBlank() ? null : cn);
        }

        return ResponseEntity.ok(usersService.save(user));
    }

}
