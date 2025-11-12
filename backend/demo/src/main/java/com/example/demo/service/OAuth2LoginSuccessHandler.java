package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "http://localhost:5173";

    private final SupabaseService supabaseService;

    @Autowired
    public OAuth2LoginSuccessHandler(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        var principal = (org.springframework.security.oauth2.core.user.DefaultOAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        String username = principal.getAttribute("name");

        // if GitHub does not provide email directly
        if (email == null) {
            email = extractEmailFromOAuthAttributes(principal.getAttributes());
        }

        if (email == null) {
            System.err.println("No email returned from OAuth provider for user: " + username);
            // fallback email(avoid null in DB)
            email = username + "@oauth.github";
        }

        Map<String, Object> user = createUserData(username, email);

        if (email != null && !email.isEmpty()) {
            if (!supabaseService.userExists(email)) {
                supabaseService.saveUser(user);
            } else {
                // confirm user
                System.out.println("User already exists in Supabase: " + email);
            }
        } else {
            System.err.println("Cannot save user: Email is null or empty");
        }
        // redirect after handling user
        response.sendRedirect(REDIRECT_URL);
    }


    private String extractEmailFromOAuthAttributes(Map<String, Object> attributes) {
        Object emailsAttr = attributes.get("emails");
        if (emailsAttr instanceof List<?> emails && !emails.isEmpty()) {
            Object firstEmail = ((Map<?, ?>) emails.get(0)).get("email");
            return firstEmail != null ? firstEmail.toString() : null;
        }
        return null;
    }


     //create user data map for supabase
    private Map<String, Object> createUserData(String username, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username != null ? username : "unknown");
        user.put("email", email);
        user.put("password", "");
        user.put("contact_number", null);
        user.put("role", "user");
        return user;
    }
}