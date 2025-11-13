package com.example.demo.service;

import com.example.demo.model.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UsersService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service_role_key}")
    private String serviceRoleKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public Optional<Users> findByUsername(String username) {
        String url = supabaseUrl + "/users?username=eq." + username;
        List<Users> users = queryUsers(url);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Optional<Users> findByEmail(String email) {
        String url = supabaseUrl + "/users?email=eq." + email;
        List<Users> users = queryUsers(url);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public Users save(Users user) {
        String url = supabaseUrl + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("Prefer", "return=representation"); // get inserted record back

        HttpEntity<Users> request = new HttpEntity<>(user, headers);

        ResponseEntity<Users[]> response = restTemplate.postForEntity(url, request, Users[].class);
        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
            return response.getBody()[0];
        }
        throw new RuntimeException("Failed to save user to Supabase");
    }

    private List<Users> queryUsers(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Users[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Users[].class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }
        return Collections.emptyList();
    }
}
