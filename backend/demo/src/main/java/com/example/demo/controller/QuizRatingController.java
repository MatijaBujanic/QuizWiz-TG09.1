package com.example.demo.controller;

import com.example.demo.dto.QuizRatingResponse;
import com.example.demo.dto.RateQuizRequest;
import com.example.demo.model.QuizRating;
import com.example.demo.service.QuizRatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRatingController {

    private final QuizRatingService ratingService;

    public QuizRatingController(QuizRatingService ratingService) {
        this.ratingService = ratingService;
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
     * Helper metoda za dohvaćanje userId
     */
    private Integer getUserIdFromAuth(Authentication authentication, Integer fallbackUserId) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            // Ovdje implementiraj logiku za dohvaćanje userId iz OAuth2User
            // Za sada vraćamo fallback
        }
        return fallbackUserId;
    }
}