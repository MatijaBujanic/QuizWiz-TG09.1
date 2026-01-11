package com.example.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SupabaseService {

    private static final Logger log = LoggerFactory.getLogger(SupabaseService.class);

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service_role_key}")
    private String serviceRoleKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SupabaseService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean userExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Cannot check user existence — email is null or empty");
            return false;
        }

        try {
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String url = supabaseUrl + "/users?email=eq." + encodedEmail;

            HttpHeaders headers = createHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            String body = response.getBody();
            if (body == null) {
                log.debug("No response body for email check: {}", email);
                return false;
            }

            List<Map<String, Object>> users = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>(){});
            boolean exists = !users.isEmpty();
            log.debug("User existence check for {}: {}", email, exists);
            return exists;

        } catch (Exception e) {
            log.error("Error checking if user exists for email: {}", email, e);
            return false;
        }
    }

    public boolean saveUser(Map<String, Object> user) {
        String url = supabaseUrl + "/users";

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=minimal");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            log.info("User inserted into Supabase: {}, Status: {}", user.get("email"), response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (org.springframework.web.client.HttpClientErrorException.Conflict e) {
            log.warn("User already exists in Supabase: {}", user.get("email"));
            return false;
        } catch (Exception e) {
            log.error("Error saving user to Supabase: {}", user.get("email"), e);
            return false;
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        return headers;
    }

    public List<Map<String, Object>> getAllUsers() {
        try {
            String url = supabaseUrl + "/users?select=user_id,username,email,contact_number,role,created_at&order=created_at.desc";

            HttpHeaders headers = createHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            String body = response.getBody();
            if (body == null || body.trim().isEmpty()) {
                log.info("No users found in Supabase");
                return Collections.emptyList();
            }

            List<Map<String, Object>> users = objectMapper.readValue(
                    body,
                    new TypeReference<List<Map<String, Object>>>(){}
            );

            log.info("Fetched {} users from Supabase", users.size());
            return users;

        } catch (Exception e) {
            log.error("Error fetching all users from Supabase", e);
            return Collections.emptyList();
        }
    }
}