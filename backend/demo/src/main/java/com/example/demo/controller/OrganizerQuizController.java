package com.example.demo.controller;

import com.example.demo.dto.CreateQuizRequest;
import com.example.demo.dto.QuizResponse;
import com.example.demo.dto.QuizUpdateRequest;
import com.example.demo.dto.TeamResultRequest;
import com.example.demo.mapper.QuizMapper;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizRow;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.security.RequireRole;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.TeamService;
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
    private final TeamService teamService;
    private final AuthenticationService authenticationService;

    public OrganizerQuizController(
            SupabaseRepository supabase,
            ObjectMapper objectMapper,
            TeamService teamService,
            AuthenticationService authenticationService
    ) {
        this.supabase = supabase;
        this.objectMapper = objectMapper;
        this.teamService = teamService;
        this.authenticationService = authenticationService;
    }

    /**
     * Vraća ID organizatora koji je trenutno prijavljen
     * Ako korisnik nije prijavljen ili nije organizator, vraća null
     */
    private Integer currentOrganizerId() {
        Long userId = authenticationService.getCurrentUserId();
        return userId != null ? userId.intValue() : null;
    }

    @GetMapping
    @RequireRole({"ORGANIZER", "ADMIN"})
    public List<QuizResponse> myQuizzes() {
        String qp = "organizer_id=eq." + currentOrganizerId() + "&order=date.asc,time.asc";
        return supabase.findAll("quiz", qp, Quiz.class).stream()
                .map(QuizMapper::toResponse)
                .toList();
    }

    @PostMapping
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<QuizResponse> create(@RequestBody CreateQuizRequest req) {
        QuizRow row = QuizMapper.toRow(req);
        row.setOrganizerId(currentOrganizerId());
        QuizRow saved = supabase.save("quiz", row, true);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuizMapper.toResponse(saved));
    }

    @PatchMapping("/{id}")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<Void> patch(
            @PathVariable Integer id,
            @RequestBody QuizUpdateRequest req
    ) {
        var quizOpt = supabase.findByColumn("quiz", "quiz_id", id, QuizRow.class);
        if (quizOpt.isEmpty() || !Objects.equals(quizOpt.get().getOrganizerId(), currentOrganizerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, Object> fields = objectMapper.convertValue(req, Map.class);
        fields.entrySet().removeIf(e -> e.getValue() == null);
        fields.remove("quiz_id");
        fields.remove("organizer_id");

        if (fields.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        supabase.update("quiz", "quiz_id", id, fields);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        var quizOpt = supabase.findByColumn("quiz", "quiz_id", id, QuizRow.class);
        if (quizOpt.isEmpty()
                || !Objects.equals(quizOpt.get().getOrganizerId(), currentOrganizerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        supabase.delete("quiz", "quiz_id", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/results")
    @RequireRole({"ORGANIZER", "ADMIN"})
    public ResponseEntity<?> submitResults(
            @PathVariable Integer quizId,
            @RequestBody List<TeamResultRequest> results
    ) {

        try {
            Integer organizerId = currentOrganizerId();
            teamService.submitQuizResults(quizId, results, organizerId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Results submitted successfully"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}