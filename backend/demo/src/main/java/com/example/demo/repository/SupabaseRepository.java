package com.example.demo.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class SupabaseRepository {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service_role_key}")
    private String serviceRoleKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SupabaseRepository(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // Generic find all with query parameters
    public <T> List<T> findAll(String table, String queryParams, Class<T> type) {
        try {
            String url = buildUrl(table, queryParams);

            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return objectMapper.readValue(
                        response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, type)
                );
            }

            return Collections.emptyList();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching from table: " + table, e);
        }
    }

    // Find by single column
    public <T> Optional<T> findByColumn(String table, String column, Object value, Class<T> type) {
        try {
            String encodedValue = encodeValue(value);
            String url = supabaseUrl + "/" + table + "?" + column + "=eq." + encodedValue;

            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<T> results = objectMapper.readValue(
                        response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, type)
                );

                return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
            }

            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("Error finding by " + column + " in table: " + table, e);
        }
    }

    // Save entity
    public <T> T save(String table, T entity, boolean returnRepresentation) {
        try {
            String url = supabaseUrl + "/" + table;

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", returnRepresentation ? "return=representation" : "return=minimal");

            System.out.println("SUPABASE JSON: " + objectMapper.writeValueAsString(entity));

            HttpEntity<T> request = new HttpEntity<>(entity, headers);

            if (returnRepresentation) {
                // This is a workaround for generic array creation
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
                if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                    List<T> results = objectMapper.readValue(
                            response.getBody(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, (Class<T>) entity.getClass())
                    );
                    return results.get(0);
                }
            } else {
                ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    return entity;
                }
            }

            throw new RuntimeException("Failed to save entity to table: " + table);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save to Supabase table: " + table, e);
        }
    }

    public <REQ, RES> RES insert(String table, REQ entity, Class<RES> responseType) {
        try {
            String url = supabaseUrl + "/" + table;

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", "return=representation");

            System.out.println("SUPABASE JSON: " + objectMapper.writeValueAsString(entity));

            HttpEntity<REQ> request = new HttpEntity<>(entity, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                List<RES> results = objectMapper.readValue(
                        response.getBody(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, responseType)
                );
                return results.get(0);
            }

            throw new RuntimeException("Failed to insert entity to table: " + table);
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert to Supabase table: " + table, e);
        }
    }


    // Update entity
    public <T> void update(String table, String idColumn, Object idValue, T entity) {
        try {
            String encodedValue = encodeValue(idValue);
            String url = supabaseUrl + "/" + table + "?" + idColumn + "=eq." + encodedValue;

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", "return=minimal");

            HttpEntity<T> request = new HttpEntity<>(entity, headers);

            restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update in Supabase table: " + table, e);
        }
    }

    public void update(String table, String idColumn, Object idValue, Map<String, Object> fields) {
        try {
            String encodedValue = encodeValue(idValue);
            String url = supabaseUrl + "/" + table + "?" + idColumn + "=eq." + encodedValue;

            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Prefer", "return=minimal");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(fields, headers);
            restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update in Supabase table: " + table, e);
        }
    }

    // Delete entity
    public void delete(String table, String idColumn, Object idValue) {
        try {
            String encodedValue = encodeValue(idValue);
            String url = supabaseUrl + "/" + table + "?" + idColumn + "=eq." + encodedValue;

            HttpEntity<Void> request = new HttpEntity<>(createHeaders());
            restTemplate.exchange(url, HttpMethod.DELETE, request, Void.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete from Supabase table: " + table, e);
        }
    }

    // Helper methods
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", serviceRoleKey);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private String buildUrl(String table, String queryParams) {
        String url = supabaseUrl + "/" + table;
        if (queryParams != null && !queryParams.isEmpty()) {
            url += "?" + queryParams;
        }
        return url;
    }

    private String encodeValue(Object value) {
        if (value == null) return "";
        return URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
    }
}