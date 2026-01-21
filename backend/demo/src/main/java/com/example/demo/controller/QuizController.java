package com.example.demo.controller;

import com.example.demo.dto.QuizRatingResponse;
import com.example.demo.dto.QuizResponse;
import com.example.demo.mapper.QuizMapper;
import com.example.demo.model.QuizRow;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.service.QuizRatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final SupabaseRepository supabase;
    private final QuizRatingService ratingService;

    public QuizController(SupabaseRepository supabase, QuizRatingService ratingService) {
        this.supabase = supabase;
        this.ratingService = ratingService;
    }

    @GetMapping("/ping")
    public String ping() { return "pong"; }

    @GetMapping
    public List<QuizResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Integer userId // Za user-specific rating
    ) {
        String qp = buildQuery(name, theme, status, dateFrom, dateTo);
        List<QuizRow> quizzes = supabase.findAll("quiz", qp, QuizRow.class);

        // Mapa svaki quiz u response i dodaj ocjene
        return quizzes.stream()
                .map(QuizMapper::toResponse)
                .map(quizResponse -> enrichWithRating(quizResponse, userId))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> get(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {

        return supabase.findByColumn("quiz", "quiz_id", id, QuizRow.class)
                .map(QuizMapper::toResponse)
                .map(quizResponse -> enrichWithRating(quizResponse, userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Helper metoda koja dodaje rating podatke u QuizResponse
     */
    private QuizResponse enrichWithRating(QuizResponse quizResponse, Integer userId) {
        try {
            QuizRatingResponse rating = ratingService.getQuizRating(
                    quizResponse.getQuizId(),
                    userId
            );

            quizResponse.setAverageRating(rating.getAverageRating());
            quizResponse.setRatingCount(rating.getRatingCount());

        } catch (Exception e) {
            // Ako nešto ne uspije, postavi default vrijednosti
            System.err.println("Error fetching rating for quiz " +
                    quizResponse.getQuizId() + ": " + e.getMessage());
            quizResponse.setAverageRating(0.0);
            quizResponse.setRatingCount(0);
        }

        return quizResponse;
    }

    // helpers

    private static String buildQuery(String name, String theme, String status, String dateFrom, String dateTo) {
        StringBuilder qp = new StringBuilder();

        if (name != null && !name.isBlank()) add(qp, "quiz_name=ilike.*" + enc(name) + "*");
        if (theme != null && !theme.isBlank()) add(qp, "quiz_theme=ilike.*" + enc(theme) + "*");
        if (status != null && !status.isBlank()) add(qp, "status=eq." + enc(status));
        if (dateFrom != null && !dateFrom.isBlank()) add(qp, "date=gte." + enc(dateFrom));
        if (dateTo != null && !dateTo.isBlank()) add(qp, "date=lte." + enc(dateTo));

        add(qp, "order=date.asc,time.asc");
        return qp.toString();
    }

    private static void add(StringBuilder qp, String part) {
        if (qp.length() > 0) qp.append("&");
        qp.append(part);
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}