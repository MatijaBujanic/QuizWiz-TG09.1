package com.example.demo.service;

import com.example.demo.dto.CreateTeamRequest;
import com.example.demo.dto.TeamResponse;
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

        Team team = new Team();
        team.setTeamName(request.getTeamName());
        team.setQuizId(request.getQuizId());
        team.setCreatedBy(createdByUserId);
        team.setMembers(request.getMembers());
        team.setNumberOfMembers(request.getMembers() != null ? request.getMembers().length : 0);
        team.setApplicationStatus("pending"); // Početni status
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
        // Provjeri da li je korisnik kreator tima
        Optional<Team> existing = supabaseRepository.findByColumn("team", "team_id", teamId, Team.class);

        if (existing.isEmpty()) {
            throw new RuntimeException("Team not found");
        }

        if (!existing.get().getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only team creator can update team");
        }

        Map<String, Object> updates = objectMapper.convertValue(request, Map.class);
        updates.entrySet().removeIf(e -> e.getValue() == null);

        // Ažuriraj broj članova ako su članovi promijenjeni
        if (request.getMembers() != null) {
            updates.put("number_of_members", request.getMembers().length);
        }

        supabaseRepository.update("team", "team_id", teamId, updates);

        return getTeamById(teamId).orElseThrow();
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
}