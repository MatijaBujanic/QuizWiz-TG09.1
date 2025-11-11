package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service_role_key}")
    private String serviceRoleKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        var principal = (org.springframework.security.oauth2.core.user.DefaultOAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        String username = principal.getAttribute("name");


        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", ""); // no password for OAuth users
        user.put("contact_number", null);
        user.put("role", "user");

        if (!userExistsInSupabase(email)) {
            saveUserToSupabase(user);
        } else {
            System.out.println("User already exists in Supabase: " + email);
        }

        response.sendRedirect("http://localhost:5173");
    }

    private boolean userExistsInSupabase(String email) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Encode email for URL
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String url = supabaseUrl + "/users?email=eq." + encodedEmail;

            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", serviceRoleKey);
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // Supabase returns [] if no match found
            String body = response.getBody();
            return body != null && body.length() > 2;
        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    private void saveUserToSupabase(Map<String, Object> user) {
        RestTemplate restTemplate = new RestTemplate();
        String url = supabaseUrl + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("Prefer", "return=minimal");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("User inserted into Supabase: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Error saving user to Supabase: " + e.getMessage());
        }
    }
}
