package com.example.demo.controller;

import com.example.demo.dto.QuizHistoryResponse;
import com.example.demo.dto.QuizResponse;
import com.example.demo.dto.UpdateMeRequest;
import com.example.demo.model.Users;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.security.RequireRole;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.QuizHistoryService;
import com.example.demo.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersService usersService;
    private final AuthenticationService authenticationService;
    private final QuizHistoryService quizHistoryService;

    public UsersController(UsersService usersService, AuthenticationService authenticationService, QuizHistoryService quizHistoryService) {
        this.usersService = usersService;
        this.authenticationService = authenticationService;
        this.quizHistoryService = quizHistoryService;
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");

        String role = usersService.findByEmail(email)
                .map(Users::getRole)
                .orElse("user");

        // Map OAuth2User attributes to User fields,
        Users user = new Users();
        user.setUsername((String) attributes.get("preferred_username"));
        user.setEmail(email);
        user.setRole(role);
        user.setContact_number(null);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/me/edit")
    public ResponseEntity<?> updateMe(
            Authentication authentication,
            @RequestBody UpdateMeRequest req,
            @RequestParam(required = false) String email) { // Za testiranje u Swaggeru

        // Dohvati email iz auth ili parametra
        String userEmail = null;

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            userEmail = (String) oauth2User.getAttributes().get("email");
        }

        // Fallback na query parametar za testiranje
        if (userEmail == null || userEmail.isBlank()) {
            userEmail = email;
        }

        if (userEmail == null || userEmail.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email required (login or provide ?email=X parameter)"
            ));
        }

        // Dohvati korisnika
        Users user = usersService.findByEmail(userEmail)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }

        // Ažuriraj samo poslana polja
        boolean hasChanges = false;

        if (req.getUsername() != null && !req.getUsername().trim().isBlank()) {
            user.setUsername(req.getUsername().trim());
            hasChanges = true;
        }

        if (req.getContact_number() != null) {
            String cn = req.getContact_number().trim();
            user.setContact_number(cn.isBlank() ? null : cn);
            hasChanges = true;
        }

        if (!hasChanges) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nothing to update"
            ));
        }

        // Koristi UPSERT (zaobilazi PATCH problem)
        Users updated = usersService.upsert(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Profile updated successfully",
                "user", updated
        ));
    }

    /**
     * Dohvaća povijest kvizova na kojima je korisnik sudjelovao
     * Dostupno za sve autentizirane korisnike
     */
    @GetMapping("/me/quiz-history")
    public ResponseEntity<List<QuizHistoryResponse>> getMyQuizHistory() {
        List<QuizHistoryResponse> history = quizHistoryService.getUserQuizHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Dohvaća sve kvizove koje je organizer organizirao
     * Dostupno samo za ORGANIZER i ADMIN uloge
     */
    @GetMapping("/me/organized-quizzes")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<List<QuizHistoryResponse>> getOrganizerQuizzes() {
        List<QuizHistoryResponse> history = quizHistoryService.getOrganizerQuizHistory();
        return ResponseEntity.ok(history);
    }
}
