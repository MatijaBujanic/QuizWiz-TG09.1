package com.example.demo.controller;

import com.example.demo.dto.CreateTeamRequest;
import com.example.demo.dto.TeamResponse;
import com.example.demo.dto.UpdateTeamRequest;
import com.example.demo.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final com.example.demo.service.SupabaseService supabaseService;

    public TeamController(TeamService teamService, com.example.demo.service.SupabaseService supabaseService) {
        this.teamService = teamService;
        this.supabaseService = supabaseService;
    }

    /**
     * POST /api/teams
     * Kreiranje novog tima za kviz
     */
    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody CreateTeamRequest request,
                                        @RequestParam(required = false) Integer createdBy,
                                        Authentication authentication) {
        try {
            if (request.getQuizId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "quiz_id is required"
                ));
            }

            Integer userId = null;

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof OAuth2User) {
                try {
                    userId = getUserIdFromAuth(authentication);
                } catch (Exception e) {
                    System.out.println("Could not get user from auth: " + e.getMessage());
                }
            }

            // Ako nema iz auth, koristi query parametar
            if (userId == null) {
                userId = createdBy;
            }

            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "created_by is required (either login or provide ?createdBy=X parameter)"
                ));
            }

            TeamResponse team = teamService.createTeam(request, userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("team", team);
            response.put("message", "Team created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/teams?quizId={id}
     * Dohvaća timove za specifičan kviz
     */
    @GetMapping
    public ResponseEntity<?> getTeams(@RequestParam(required = false) Integer quizId) {
        try {
            if (quizId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "quizId parameter is required"
                ));
            }

            List<TeamResponse> teams = teamService.getTeamsByQuiz(quizId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "teams", teams,
                    "count", teams.size(),
                    "quizId", quizId
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
    /**
     * GET /api/teams/{id}
     * dohvaca detalje o timu, opcionalno za inplementirat
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeam(@PathVariable Integer id) {
        return teamService.getTeamById(id)
                .map(team -> ResponseEntity.ok(Map.of("success", true, "team", team)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/teams/{id}
     * updateanja tima tj podataka o timu (samo kreator)
     */
    //za testiranje patcha i deletea bi trebalo dodat "test mode" z swagger da preskocimo oauth za userid (onaj user koji je i kreiraao kviz)
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Integer id,
                                        @RequestBody UpdateTeamRequest request,
                                        @RequestParam(required = false) Integer userId,
                                        Authentication authentication) {
        try {
            Integer actualUserId = null;

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof OAuth2User) {
                try {
                    actualUserId = getUserIdFromAuth(authentication);
                } catch (Exception e) {
                    System.out.println("Could not get user from auth: " + e.getMessage());
                }
            }

            if (actualUserId == null) {
                actualUserId = userId;
            }

            if (actualUserId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "userId is required (either login or provide ?userId=X parameter)"
                ));
            }

            TeamResponse team = teamService.updateTeam(id, request, actualUserId);

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
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
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
    public ResponseEntity<?> withdrawApplication(@PathVariable Integer id,
                                                 @RequestParam(required = false) Integer userId,
                                                 Authentication authentication) {
        try {
            Integer actualUserId = null;

            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof OAuth2User) {
                try {
                    actualUserId = getUserIdFromAuth(authentication);
                } catch (Exception e) {
                    System.out.println("Could not get user from auth: " + e.getMessage());
                }
            }

            if (actualUserId == null) {
                actualUserId = userId;
            }

            if (actualUserId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "userId is required (either login or provide ?userId=X parameter)"
                ));
            }

            teamService.withdrawApplication(id, actualUserId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Application withdrawn successfully"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Helper metoda za dohvaćanje user ID iz OAuth2 autentikacije
     */
    private Integer getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            throw new RuntimeException("User not authenticated");
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null) {
            throw new RuntimeException("User email not found");
        }

        // Dohvati user_id iz baze
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

        throw new RuntimeException("User not found in database");
    }
 //ranking tima u kvizu
 @GetMapping("/quiz/{quizId}/ranking")
 public ResponseEntity<?> getQuizRanking(@PathVariable Integer quizId) {
     return ResponseEntity.ok(Map.of(
             "success", true,
             "quizId", quizId,
             "ranking", teamService.getQuizRanking(quizId)
     ));
 }

}