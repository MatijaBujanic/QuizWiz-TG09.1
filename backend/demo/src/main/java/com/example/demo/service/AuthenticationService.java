package com.example.demo.service;

import com.example.demo.model.Users;
import com.example.demo.security.AuthenticationUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service za rad sa autentifikacijom korisnika
 */
@Service
public class AuthenticationService {

    private final UsersService usersService;

    public AuthenticationService(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * Vraća trenutnog prijavljenog korisnika
     * @return Users objekt ili Optional.empty() ako nije prijavljen
     */
    public Optional<Users> getCurrentUser() {
        String email = AuthenticationUtils.getCurrentUserEmail();
        System.out.println("AUTH EMAIL = " + email);

        if (email == null) {
            return Optional.empty();
        }
        return usersService.findByEmail(email);
    }


    /**
     * Vraća ID trenutnog korisnika (user_id)
     * @return ID korisnika ili null ako nije prijavljen
     */
    public Long getCurrentUserId() {
        return getCurrentUser().map(Users::getUserId).orElse(null);
    }

    /**
     * Vraća ulogu trenutnog korisnika
     * @return uloga (USER, ORGANIZER, ADMIN) ili null ako nije prijavljen
     */
    public String getCurrentUserRole() {
        return getCurrentUser().map(Users::getRole).orElse(null);
    }

    /**
     * Provjerava je li trenutni korisnik admin
     */
    public boolean isCurrentUserAdmin() {
        String role = getCurrentUserRole();
        return "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Provjerava je li trenutni korisnik organizator
     */
    public boolean isCurrentUserOrganizer() {
        String role = getCurrentUserRole();
        return "ORGANIZER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Provjerava je li korisnik prijavljen
     */
    public boolean isAuthenticated() {
        return AuthenticationUtils.isAuthenticated() && getCurrentUser().isPresent();
    }
}

