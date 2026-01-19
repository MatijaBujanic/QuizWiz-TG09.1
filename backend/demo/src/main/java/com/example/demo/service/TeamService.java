package com.example.demo.service;

import com.example.demo.dto.CreateTeamRequest;
import com.example.demo.dto.TeamResponse;
import com.example.demo.dto.TeamResultRequest;
import com.example.demo.dto.UpdateTeamRequest;
import com.example.demo.model.Team;
import com.example.demo.repository.SupabaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Service
public class TeamService {

    private final SupabaseRepository supabaseRepository;
    private final ObjectMapper objectMapper;

    public TeamService(SupabaseRepository supabaseRepository, ObjectMapper objectMapper) {
        this.supabaseRepository = supabaseRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Kreira novi tim za kviz
     * quiz_id i created_by su OBAVEZNI (foreign keys)
     */
    public TeamResponse createTeam(CreateTeamRequest request, Integer createdByUserId) {
        // Validacija
        if (request.getQuizId() == null) {
            throw new IllegalArgumentException("quiz_id is required");
        }
        if (createdByUserId == null) {
            throw new IllegalArgumentException("User must be authenticated");
        }
        if (request.getTeamName() == null || request.getTeamName().trim().isEmpty()) {
            throw new IllegalArgumentException("team_name is required");
        }

        //check:isto ime tima na istom kvizu
        String queryParams = "quiz_id=eq." + request.getQuizId();
        List<Team> existingTeams =
                supabaseRepository.findAll("team", queryParams, Team.class);

        for (Team existing : existingTeams) {
            if (existing.getTeamName() != null &&
                    existing.getTeamName().equalsIgnoreCase(request.getTeamName().trim())) {
                throw new RuntimeException("Već postoji tim s ovim imenom");
            }
        }

        Team team = new Team();
        team.setTeamName(request.getTeamName().trim());
        team.setQuizId(request.getQuizId());
        team.setCreatedBy(createdByUserId);
        team.setMembers(request.getMembers());
        team.setNumberOfMembers(request.getMembers() != null ? request.getMembers().length : 0);
        team.setApplicationStatus("pending");
        team.setSentAt(LocalDateTime.now());
        team.setPoints(0);

        Team saved = supabaseRepository.save("team", team, true);
        return mapToResponse(saved);
    }

    /**
     * Dohvaća timove za specifičan kviz
     */
    public List<TeamResponse> getTeamsByQuiz(Integer quizId) {
        String queryParams = "quiz_id=eq." + quizId + "&order=sent_at.desc";
        List<Team> teams = supabaseRepository.findAll("team", queryParams, Team.class);
        return teams.stream().map(this::mapToResponse).toList();
    }

    /**
     * Dohvaća timove koje je kreirao specifičan korisnik
     */
    public List<TeamResponse> getTeamsByCreator(Integer userId) {
        String queryParams = "created_by=eq." + userId + "&order=sent_at.desc";
        List<Team> teams = supabaseRepository.findAll("team", queryParams, Team.class);
        return teams.stream().map(this::mapToResponse).toList();
    }

    /**
     * Dohvaća detalje o jednom timu
     */
    public Optional<TeamResponse> getTeamById(Integer teamId) {
        return supabaseRepository.findByColumn("team", "team_id", teamId, Team.class)
                .map(this::mapToResponse);
    }

    /**
     * Ažurira tim (samo kreator može)
     */
    public TeamResponse updateTeam(Integer teamId, UpdateTeamRequest request, Integer userId) {
        Optional<Team> existing = supabaseRepository.findByColumn("team", "team_id", teamId, Team.class);

        if (existing.isEmpty()) {
            throw new RuntimeException("Team not found");
        }

        Team team = existing.get();

        if (!team.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only team creator can update team");
        }

        boolean hasChanges = false;

        // Ažuriraj ime tima
        if (request.getTeamName() != null && !request.getTeamName().trim().isEmpty()) {
            // Provjeri duple imena timova
            String queryParams = "quiz_id=eq." + team.getQuizId();
            List<Team> teams = supabaseRepository.findAll("team", queryParams, Team.class);

            for (Team t : teams) {
                if (!t.getTeamId().equals(teamId) &&
                        t.getTeamName() != null &&
                        t.getTeamName().equalsIgnoreCase(request.getTeamName().trim())) {
                    throw new RuntimeException("Već postoji tim s ovim imenom na ovom kvizu");
                }
            }

            team.setTeamName(request.getTeamName().trim());
            hasChanges = true;
        }

        // Ažuriraj članove
        if (request.getMembers() != null) {
            team.setMembers(request.getMembers());
            team.setNumberOfMembers(request.getMembers().length);
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new RuntimeException("Nothing to update");
        }

        // upsert, ne patch
        supabaseRepository.upsert("team", team);

        return mapToResponse(team);
    }

    /**
     * Povlači prijavu tima (briše tim)
     */
    public void withdrawApplication(Integer teamId, Integer userId) {
        Optional<Team> team = supabaseRepository.findByColumn("team", "team_id", teamId, Team.class);

        if (team.isEmpty()) {
            throw new RuntimeException("Team not found");
        }

        if (!team.get().getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only team creator can withdraw application");
        }

        // Provjeri da li je prijava još uvijek pending
        if (!"pending".equals(team.get().getApplicationStatus())) {
            throw new RuntimeException("Cannot withdraw application - status is " + team.get().getApplicationStatus());
        }

        supabaseRepository.delete("team", "team_id", teamId);
    }

    /**
     * Helper za mapiranje Team -> TeamResponse
     */
    private TeamResponse mapToResponse(Team team) {
        TeamResponse response = new TeamResponse();
        response.setTeamId(team.getTeamId());
        response.setTeamName(team.getTeamName());
        response.setNumberOfMembers(team.getNumberOfMembers());
        response.setApplicationStatus(team.getApplicationStatus());
        response.setPoints(team.getPoints());
        response.setRank(team.getRank());
        response.setSentAt(team.getSentAt());
        response.setApprovedAt(team.getApprovedAt());
        response.setCreatedBy(team.getCreatedBy());
        response.setQuizId(team.getQuizId());
        response.setMembers(team.getMembers());
        return response;
    }

    public void submitQuizResults(
            Integer quizId,
            List<TeamResultRequest> results,
            Integer organizerId
    ) {
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Results cannot be empty");
        }

        // Dohvati sve timove kviza
        String queryParams = "quiz_id=eq." + quizId;
        List<Team> teams = supabaseRepository.findAll("team", queryParams, Team.class);

        if (teams.isEmpty()) {
            throw new RuntimeException("No teams found for this quiz");
        }

        // Ažuriraj bodove koristeći UPSERT (zaobilazi PATCH problem)
        for (TeamResultRequest r : results) {
            Optional<Team> teamOpt = supabaseRepository.findByColumn("team", "team_id", r.getTeamId(), Team.class);

            if (teamOpt.isEmpty()) {
                throw new RuntimeException("Team not found: " + r.getTeamId());
            }

            Team team = teamOpt.get();
            team.setPoints(r.getPoints());

            // UPSERT
            supabaseRepository.upsert("team", team);
        }

        // Ponovo dohvati timove s ažuriranim bodovima
        teams = supabaseRepository.findAll("team", queryParams, Team.class);

        // Sortiraj po bodovima
        teams.sort((a, b) -> {
            Integer aPoints = a.getPoints() != null ? a.getPoints() : 0;
            Integer bPoints = b.getPoints() != null ? b.getPoints() : 0;
            return bPoints.compareTo(aPoints);
        });

        // Dodijeli rank
        int rank = 1;
        Integer lastPoints = null;

        for (Team team : teams) {
            Integer currentPoints = team.getPoints() != null ? team.getPoints() : 0;

            if (lastPoints != null && !currentPoints.equals(lastPoints)) {
                rank++;
            }

            team.setRank(rank);
            // UPSERT za rank također
            supabaseRepository.upsert("team", team);

            lastPoints = currentPoints;
        }
    }
    public List<TeamResponse> getQuizRanking(Integer quizId) {
        return getTeamsByQuiz(quizId).stream()
                .sorted((a, b) -> b.getPoints().compareTo(a.getPoints()))
                .toList();
    }


}