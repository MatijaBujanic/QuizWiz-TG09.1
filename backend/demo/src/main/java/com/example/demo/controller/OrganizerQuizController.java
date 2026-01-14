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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/organizer/quizzes")
public class OrganizerQuizController {

    private final SupabaseRepository supabase;
    private final ObjectMapper objectMapper;

    public OrganizerQuizController(SupabaseRepository supabase, ObjectMapper objectMapper) {
        this.supabase = supabase;
        this.objectMapper = objectMapper;
    }

    // TODO: privremeno hardcode dok ne spojiš auth -> organizerId
    private Integer currentOrganizerId() { return 2; }

    @GetMapping
    public List<QuizResponse> myQuizzes() {
        String qp = "organizer_id=eq." + currentOrganizerId() + "&order=date.asc,time.asc";
        return supabase.findAll("quiz", qp, Quiz.class).stream()
                .map(QuizMapper::toResponse)
                .toList();
    }

    @PostMapping
    public ResponseEntity<QuizResponse> create(@RequestBody CreateQuizRequest req) {
        QuizRow row = QuizMapper.toRow(req);
        row.setOrganizerId(currentOrganizerId()); // KLJUČ: organizer_id ide sa servera
        QuizRow saved = supabase.save("quiz", row, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuizMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patch(@PathVariable Integer id, @RequestBody QuizUpdateRequest req) {
        // (opcionalno, ali preporuka) prvo provjeri da je kviz od ovog organizatora
        var quizOpt = supabase.findByColumn("quiz", "quiz_id", id, Quiz.class);
        if (quizOpt.isEmpty() || !Objects.equals(quizOpt.get().getOrganizerId(), currentOrganizerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> fields = objectMapper.convertValue(req, Map.class);
        fields.entrySet().removeIf(e -> e.getValue() == null);
        fields.remove("quiz_id");
        fields.remove("organizer_id"); // organizator se ne smije mijenjat

        if (fields.isEmpty()) return ResponseEntity.badRequest().build();

        supabase.update("quiz", "quiz_id", id, fields);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        var quizOpt = supabase.findByColumn("quiz", "quiz_id", id, Quiz.class);
        if (quizOpt.isEmpty() || !Objects.equals(quizOpt.get().getOrganizerId(), currentOrganizerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        supabase.delete("quiz", "quiz_id", id);
        return ResponseEntity.noContent().build();
    }
}
