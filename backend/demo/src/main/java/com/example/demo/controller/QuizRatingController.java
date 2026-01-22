package com.example.demo.controller;

import com.example.demo.dto.QuizRatingResponse;
import com.example.demo.dto.RateQuizRequest;
import com.example.demo.model.QuizRating;
import com.example.demo.service.QuizRatingService;
import com.example.demo.service.SupabaseService; // Pretpostavljam da koristiš ovaj servis
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRatingController {

    private final QuizRatingService ratingService;
    private final SupabaseService supabaseService;

    public QuizRatingController(QuizRatingService ratingService, SupabaseService supabaseService) {
        this.ratingService = ratingService;
        this.supabaseService = supabaseService;
    }

    /**
     * POST /api/quizzes/{quizId}/rate
     * Korisnik ocjenjuje kviz (1-5 zvjezdica)
     */
    @PostMapping("/{quizId}/rate")
    public ResponseEntity<?> rateQuiz(
            @PathVariable Integer quizId,
            @RequestBody RateQuizRequest request,
            @RequestParam(required = false) Integer userId, // Za testiranje
            Authentication authentication) {
        try {
            // Dohvati userId iz auth ili parametra
            Integer actualUserId = getUserIdFromAuth(authentication, userId);

            if (actualUserId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User authentication required"
                ));
            }

            QuizRating rating = ratingService.rateQuiz(quizId, actualUserId, request.getRating());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Quiz rated successfully",
                    "rating", rating
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error rating quiz: " + e.getMessage()
            ));
        }
    }

    /**
     * GET /api/quizzes/{quizId}/rating
     * Dohvaća prosječnu ocjenu kviza
     */
    @GetMapping("/{quizId}/rating")
    public ResponseEntity<?> getQuizRating(
            @PathVariable Integer quizId,
            @RequestParam(required = false) Integer userId,
            Authentication authentication) {
        try {
            Integer actualUserId = getUserIdFromAuth(authentication, userId);
            QuizRatingResponse rating = ratingService.getQuizRating(quizId, actualUserId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "rating", rating
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error fetching rating: " + e.getMessage()
            ));
        }
    }

    /**
     * DELETE /api/quizzes/{quizId}/rate
     * Briše ocjenu korisnika
     */
    @DeleteMapping("/{quizId}/rate")
    public ResponseEntity<?> deleteRating(
            @PathVariable Integer quizId,
            @RequestParam(required = false) Integer userId,
            Authentication authentication) {
        try {
            Integer actualUserId = getUserIdFromAuth(authentication, userId);

            if (actualUserId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User authentication required"
                ));
            }

            ratingService.deleteRating(quizId, actualUserId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Rating deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Error deleting rating: " + e.getMessage()
            ));
        }
    }

    /**
     * Helper metoda za dohvaćanje userId iz auth ili parametra
     */
    private Integer getUserIdFromAuth(Authentication authentication, Integer userId) {
        if (userId != null) {
            // Ako je userId poslan kao parametar (za testiranje), koristi ga
            return userId;
        }

        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            return null; // Ili baci iznimku ako želiš strožu kontrolu
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null) {
            return null; // Ili baci iznimku
        }

        // Dohvati user_id iz baze korisnika
        List<Map<String, Object>> users = supabaseService.getAllUsers();
        for (Map<String, Object> user : users) {
            if (email.equalsIgnoreCase((String) user.get("email"))) {
                Object userIdObj = user.get("user_id");
                if (userIdObj instanceof Integer) {
                    return (Integer) userIdObj;
                } else if (userIdObj instanceof Number) {
                    return ((Number) userIdObj).intValue();
                }
            }
        }

        return null; // Korisnik nije pronađen
    }
}
