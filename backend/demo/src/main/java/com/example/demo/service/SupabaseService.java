package com.example.demo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SupabaseService {

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
     // check if user exists in supabas(by email),
    public boolean userExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.err.println("Cannot check user existence — email is null or empty");
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
            if (body == null) return false;

            List<Map<String, Object>> users = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>(){});
            return !users.isEmpty();

        } catch (Exception e) {
            System.err.println("Error checking if user exists: " + e.getMessage());
            return false;
        }
    }

    //save user to supabase
    public boolean saveUser(Map<String, Object> user) {
        String url = supabaseUrl + "/users";

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Prefer", "return=minimal");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("User inserted into Supabase: " + response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (org.springframework.web.client.HttpClientErrorException.Conflict e) {
            System.out.println("User already exists in Supabase (caught http 409 conflict): " + user.get("email"));
            return false;
        } catch (Exception e) {
            System.err.println("Error saving user to Supabase: " + e.getMessage());
            return false;
        }
    }

     //create standardized headers for supabase API calls
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
                return Collections.emptyList();
            }

            // Parse the JSON response into a list of maps
            List<Map<String, Object>> users = objectMapper.readValue(
                    body,
                    new TypeReference<List<Map<String, Object>>>(){}
            );

            return users;

        } catch (Exception e) {
            System.err.println("Error fetching all users from Supabase: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}