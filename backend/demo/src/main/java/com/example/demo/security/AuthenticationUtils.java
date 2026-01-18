package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * Utility klasa za rad sa autentifikacijom i trenutnim korisnikom
 */
@Component
public class AuthenticationUtils {

    /**
     * Vraća email trenutno prijavljenog korisnika
     * @return email korisnika ili null ako nije prijavljen
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            return email != null && !email.isEmpty() ? email : null;
        }

        if (principal instanceof String) {
            return (String) principal;
        }

        return null;
    }

    /**
     * Provjerava je li korisnik prijavljen
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Provjerava je li korisnik anoniman
     */
    public static boolean isAnonymous() {
        String email = getCurrentUserEmail();
        return email == null;
    }
}

