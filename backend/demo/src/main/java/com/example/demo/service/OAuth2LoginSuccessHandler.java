package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.*;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "https://quiz-wiz-tg-09-1.vercel.app/oauth2/success";

    private final SupabaseService supabaseService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public OAuth2LoginSuccessHandler(SupabaseService supabaseService, JwtTokenService jwtTokenService) {
        this.supabaseService = supabaseService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");

        // if GitHub does not provide email directly
        if (email == null) {
            email = extractEmailFromOAuthAttributes(oAuth2User.getAttributes());
        }

        if (email == null) {
            System.err.println("No email returned from OAuth provider for user: " + username);
            // fallback email (avoid null in DB)
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

        // Generate JWT token for this user
        String token = jwtTokenService.generateToken(email);

        // Redirect to frontend with token
        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String extractEmailFromOAuthAttributes(Map<String, Object> attributes) {
        Object emailsAttr = attributes.get("emails");
        if (emailsAttr instanceof List<?> emails && !emails.isEmpty()) {
            Object firstEmail = ((Map<?, ?>) emails.get(0)).get("email");
            return firstEmail != null ? firstEmail.toString() : null;
        }
        return null;
    }

    // create user data map for supabase
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