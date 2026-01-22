package com.example.demo.controller;

import com.example.demo.dto.CreateQuizRequest;
import com.example.demo.dto.QuizResponse;
import com.example.demo.dto.QuizUpdateRequest;
import com.example.demo.mapper.QuizMapper;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizRow;
import com.example.demo.repository.SupabaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final SupabaseRepository supabase;
    private final ObjectMapper objectMapper;

    public QuizController(SupabaseRepository supabase, ObjectMapper objectMapper) {
        this.supabase = supabase;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/ping")
    public String ping() { return "pong"; }

    @GetMapping
    public List<QuizResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String theme,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo
    ) {
        String qp = buildQuery(name, theme, status, dateFrom, dateTo);
        List<Quiz> quizzes = supabase.findAll("quiz", qp, Quiz.class);
        return quizzes.stream().map(QuizMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizResponse> get(@PathVariable Integer id) {
        return supabase.findByColumn("quiz", "quiz_id", id, Quiz.class)
                .map(QuizMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<QuizResponse> create(@RequestBody CreateQuizRequest req) {
        QuizRow toSave = QuizMapper.toRow(req); // novi mapper
        QuizRow saved = supabase.save("quiz", toSave, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizMapper.toResponse(saved));
    }

    /**
     * PATCH /api/quizzes/{id}
     * prima DTO, ali šalje u Supabase samo non-null polja (partial update)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patch(@PathVariable Integer id, @RequestBody QuizUpdateRequest req) {
        Map<String, Object> fields = objectMapper.convertValue(req, Map.class);

        // izbaci null vrijednosti
        fields.entrySet().removeIf(e -> e.getValue() == null);

        // sigurnost: ne dopuštamo promjenu PK
        fields.remove("quiz_id");

        if (fields.isEmpty()) return ResponseEntity.badRequest().build();

        supabase.update("quiz", "quiz_id", id, fields);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        supabase.delete("quiz", "quiz_id", id);
        return ResponseEntity.noContent().build();
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
