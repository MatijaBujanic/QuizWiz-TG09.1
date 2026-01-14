package com.example.demo.controller;

import com.example.demo.dto.LocationResponse;
import com.example.demo.mapper.LocationMapper;
import com.example.demo.model.Location;
import com.example.demo.repository.SupabaseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final SupabaseRepository supabase;

    public LocationController(SupabaseRepository supabase) {
        this.supabase = supabase;
    }

    @GetMapping
    public List<LocationResponse> list(@RequestParam(required = false) String name) {
        String qp = buildQuery(name);
        List<Location> locations = supabase.findAll("location", qp, Location.class);
        return locations.stream().map(LocationMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> get(@PathVariable Integer id) {
        return supabase.findByColumn("location", "location_id", id, Location.class)
                .map(LocationMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // helpers
    private static String buildQuery(String name) {
        StringBuilder qp = new StringBuilder();

        if (name != null && !name.isBlank()) {
            qp.append("location_name=ilike.*").append(enc(name)).append("*");
        }

        if (qp.length() > 0) qp.append("&");
        qp.append("order=location_name.asc");

        return qp.toString();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

