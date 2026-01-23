package com.example.demo.controller;

import com.example.demo.dto.CreateTeamRequest;
import com.example.demo.dto.TeamResponse;
import com.example.demo.dto.UpdateTeamRequest;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final AuthenticationService authenticationService;

    public TeamController(
            TeamService teamService,
            AuthenticationService authenticationService
    ) {
        this.teamService = teamService;
        this.authenticationService = authenticationService;
    }

    /**
     * POST /api/teams
     * Kreiranje novog tima za kviz
     */
    @PostMapping
    public ResponseEntity<?> createTeam(
            @RequestBody CreateTeamRequest request) {

        try {
            if (request.getQuizId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "quiz_id is required"
                ));
            }

            Long userId = authenticationService.getCurrentUserId();

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "User not authenticated"
                ));
            }

            TeamResponse team = teamService.createTeam(
                    request,
                    userId.intValue()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "team", team,
                    "message", "Team created successfully"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


    /**
     * GET /api/teams?quizId={id}
     * Dohvaća timove za specifičan kviz
     */
    @GetMapping
    public ResponseEntity<?> getTeams(@RequestParam Integer quizId) {
        try {
            List<TeamResponse> teams = teamService.getTeamsByQuiz(quizId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "teams", teams,
                    "count", teams.size(),
                    "quizId", quizId
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error fetching teams"
            ));
        }
    }

    /**
     * GET /api/teams/user/{userId}
     * Dohvaća timove koje je korisnik kreirao
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTeams(@PathVariable Integer userId) {
        try {
            List<TeamResponse> teams = teamService.getTeamsByCreator(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "teams", teams,
                    "count", teams.size()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error fetching user teams"
            ));
        }
    }

    /**
     * GET /api/teams/{id}
     * Dohvaća detalje tima
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeam(@PathVariable Integer id) {
        return teamService.getTeamById(id)
                .map(team -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "team", team
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/teams/{id}
     * Update tima (samo kreator)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeam(
            @PathVariable Integer id,
            @RequestBody UpdateTeamRequest request,
            Authentication authentication
    ) {
        try {
            Long userId = authenticationService.getCurrentUserId();
            TeamResponse team = teamService.updateTeam(id, request, Math.toIntExact(userId));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "team", team,
                    "message", "Team updated successfully"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * DELETE /api/teams/{id}
     * Povlačenje prijave (brisanje tima)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> withdrawApplication(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        try {
            Long userId = authenticationService.getCurrentUserId();
            teamService.withdrawApplication(id, Math.toIntExact(userId));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Application withdrawn successfully"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /api/teams/quiz/{quizId}/ranking
     * Ranking timova u kvizu
     */
    @GetMapping("/quiz/{quizId}/ranking")
    public ResponseEntity<?> getQuizRanking(@PathVariable Integer quizId) {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "quizId", quizId,
                "ranking", teamService.getQuizRanking(quizId)
        ));
    }
}
