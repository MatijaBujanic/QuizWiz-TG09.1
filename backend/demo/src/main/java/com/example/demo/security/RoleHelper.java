package com.example.demo.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.demo.service.AuthenticationService;

/**
 * Helper klasa za autentifikaciju u kontrolerima
 */
public class RoleHelper {

    /**
     * Provjerava ima li korisnik potrebnu ulogu
     * Ako nema, vraća 403 Forbidden ResponseEntity
     *
     * Primjer:
     * ResponseEntity<?> check = RoleHelper.requireRole(authService, "ORGANIZER");
     * if (check != null) return check;
     */
    public static ResponseEntity<?> requireRole(AuthenticationService authService, String... requiredRoles) {
        String currentRole = authService.getCurrentUserRole();

        if (currentRole == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not authenticated");
        }

        boolean hasRole = false;
        for (String role : requiredRoles) {
            if (role.equalsIgnoreCase(currentRole)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User does not have required role: " + String.join(", ", requiredRoles));
        }

        return null; // Dozvoljeno
    }
}

