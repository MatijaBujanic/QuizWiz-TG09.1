package com.example.demo.controller;

import com.example.demo.dto.CreateLocationRequest;
import com.example.demo.mapper.LocationMapper;
import com.example.demo.model.Location;
import com.example.demo.model.LocationRow;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.security.RequireRole;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizer/locations")
public class OrganizerLocationController {

    private final SupabaseRepository supabase;
    private final AuthenticationService authenticationService;

    public OrganizerLocationController(SupabaseRepository supabase, AuthenticationService authenticationService) {
        this.supabase = supabase;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<Location> createLocation(@RequestBody CreateLocationRequest req) {
        LocationRow toSave = LocationMapper.toRow(req);
        Location saved = supabase.insert("location", toSave, Location.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @PatchMapping("/{id}")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<Void> patch(@PathVariable Integer id, @RequestBody Location req) {
        // partial update je bolje raditi s Map<String,Object> kao kod quiz-a
        supabase.update("location", "location_id", id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        supabase.delete("location", "location_id", id);
        return ResponseEntity.noContent().build();
    }
}
