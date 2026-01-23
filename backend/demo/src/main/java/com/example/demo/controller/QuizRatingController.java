package com.example.demo.controller;

import com.example.demo.dto.QuizRatingResponse;
import com.example.demo.dto.RateQuizRequest;
import com.example.demo.model.QuizRating;
import com.example.demo.service.QuizRatingService;
import com.example.demo.service.SupabaseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
public class QuizRatingController {

    private final QuizRatingService ratingService;
    private final SupabaseService supabaseService;

    // Replace this secret with your JWT secret key used for signing tokens
    private static final String JWT_SECRET = "your-jwt-secret-key";

    public QuizRatingController(QuizRatingService ratingService, SupabaseService supabaseService) {
        this.ratingService = ratingService;
        this.supabaseService = supabaseService;
    }

    @PostMapping("/{quizId}/rate")
    public ResponseEntity<?> rateQuiz(
            @PathVariable Integer quizId,
            @RequestBody RateQuizRequest request,
            @RequestParam(required = false) Integer userId  // obavezno iz query parametra
    ) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User ID is required"
                ));
            }

            QuizRating rating = ratingService.rateQuiz(quizId, userId, request.getRating());

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

    @GetMapping("/{quizId}/rating")
    public ResponseEntity<?> getQuizRating(
            @PathVariable Integer quizId,
            @RequestParam(required = false) Integer userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        try {
            Integer actualUserId = userId;

            if (actualUserId == null && authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String email = extractEmailFromJwt(token);
                if (email != null) {
                    actualUserId = getUserIdByEmail(email);
                }
            }

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
    @DeleteMapping("/{quizId}/rate")
    public ResponseEntity<?> deleteRating(
            @PathVariable Integer quizId,
            @RequestParam(required = false) Integer userId
    ) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User ID is required"
                ));
            }

            ratingService.deleteRating(quizId, userId);

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


    private String extractEmailFromJwt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (JwtException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Integer getUserIdByEmail(String email) {
        List<Map<String, Object>> users = supabaseService.getAllUsers();
        for (Map<String, Object> user : users) {
            if (email != null && email.equalsIgnoreCase((String) user.get("email"))) {
                Object userIdObj = user.get("user_id");
                if (userIdObj instanceof Integer) {
                    return (Integer) userIdObj;
                } else if (userIdObj instanceof Number) {
                    return ((Number) userIdObj).intValue();
                }
            }
        }
        return null;
    }
}
