package com.example.demo.service;

import com.example.demo.dto.QuizHistoryResponse;
import com.example.demo.dto.TeamHistoryResponse;
import com.example.demo.model.Location;
import com.example.demo.model.QuizRow;
import com.example.demo.model.Team;
import com.example.demo.model.Users;
import com.example.demo.repository.SupabaseRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizHistoryService {

    private final SupabaseRepository supabaseRepository;
    private final AuthenticationService authenticationService;

    public QuizHistoryService(SupabaseRepository supabaseRepository, AuthenticationService authenticationService) {
        this.supabaseRepository = supabaseRepository;
        this.authenticationService = authenticationService;
    }

    /**
     * Dohvaća kvizove na kojima je korisnik sudjelovao
     * (kao član tima koji je kreirao ili je dio tima)
     */
    public List<QuizHistoryResponse> getUserQuizHistory() {
        Optional<Users> currentUser = authenticationService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return Collections.emptyList();
        }

        String username = currentUser.get().getUsername();
        List<QuizHistoryResponse> history = new ArrayList<>();

        // Dohvati sve timove gdje je korisnik kreirao tim
        List<Team> createdTeams = getTeamsByCreator(currentUser.get().getUserId());

        // Dohvati sve timove gdje je korisnik član
        List<Team> memberTeams = getTeamsByMember(username);

        // Kombiniraj sve timove i ukloni duplikate
        Set<Integer> processedQuizIds = new HashSet<>();
        Map<Integer, Team> teamsByQuizId = new HashMap<>();

        for (Team team : createdTeams) {
            if (team.getQuizId() != null) {
                teamsByQuizId.put(team.getQuizId(), team);
            }
        }

        for (Team team : memberTeams) {
            if (team.getQuizId() != null && !teamsByQuizId.containsKey(team.getQuizId())) {
                teamsByQuizId.put(team.getQuizId(), team);
            }
        }

        // Za svakog kviza, dohvati detaljne informacije
        for (Map.Entry<Integer, Team> entry : teamsByQuizId.entrySet()) {
            Integer quizId = entry.getKey();
            Team team = entry.getValue();

            Optional<QuizRow> quiz = supabaseRepository.findByColumn("quiz", "quiz_id", quizId, QuizRow.class);
            if (quiz.isPresent()) {
                QuizHistoryResponse response = mapQuizToHistory(quiz.get());

                // Dodaj informacije o timu na kojem je korisnik sudjelovao
                TeamHistoryResponse teamResponse = mapTeamToHistory(team);
                response.setUserTeam(teamResponse);

                history.add(response);
                processedQuizIds.add(quizId);
            }
        }

        return history;
    }

    /**
     * Dohvaća sve kvizove koje je korisnik organizirao (samo za ORGANIZER i ADMIN)
     */
    public List<QuizHistoryResponse> getOrganizerQuizHistory() {
        Optional<Users> currentUser = authenticationService.getCurrentUser();
        if (currentUser.isEmpty()) {
            return Collections.emptyList();
        }

        Long userId = currentUser.get().getUserId();
        String role = currentUser.get().getRole();

        // Samo ORGANIZER i ADMIN mogu vidjeti svoju povijest
        if (!"ORGANIZER".equals(role) && !"ADMIN".equals(role)) {
            return Collections.emptyList();
        }

        String queryParams = "organizer_id=eq." + userId + "&order=date.desc";
        List<QuizRow> quizzes = supabaseRepository.findAll("quiz", queryParams, QuizRow.class);

        List<QuizHistoryResponse> history = new ArrayList<>();

        for (QuizRow quiz : quizzes) {
            QuizHistoryResponse response = mapQuizToHistory(quiz);

            // Dohvati sve timove koji su sudjelovali na ovom kvizu
            List<Team> teams = supabaseRepository.findAll(
                    "team",
                    "quiz_id=eq." + quiz.getQuizId() + "&order=rank.asc",
                    Team.class
            );

            List<TeamHistoryResponse> teamResponses = teams.stream()
                    .map(this::mapTeamToHistory)
                    .toList();

            response.setTeams(teamResponses);
            history.add(response);
        }

        return history;
    }

    /**
     * Dohvaća sve timove koje je kreirao određeni korisnik
     */
    private List<Team> getTeamsByCreator(Long userId) {
        String queryParams = "created_by=eq." + userId;
        return supabaseRepository.findAll("team", queryParams, Team.class);
    }

    /**
     * Dohvaća sve timove gdje je korisnik član
     * Traži u JSON array-u `members` kolone
     */
    private List<Team> getTeamsByMember(String username) {
        // Ovo je kompleksnija pretraga jer trebamo pretraživati JSON array
        // Supabase REST API ima ograničenja za tekstualnu pretragu u JSON
        // Alternativa: Dohvati sve timove i filtriraj u kodu

        String queryParams = "order=team_id.desc";
        List<Team> allTeams = supabaseRepository.findAll("team", queryParams, Team.class);

        return allTeams.stream()
                .filter(team -> team.getMembers() != null &&
                        Arrays.asList(team.getMembers()).contains(username))
                .toList();
    }

    /**
     * Mapira QuizRow objekt na QuizHistoryResponse
     */
    private QuizHistoryResponse mapQuizToHistory(QuizRow quiz) {
        QuizHistoryResponse response = new QuizHistoryResponse();
        response.setQuizId(quiz.getQuizId());
        response.setQuizName(quiz.getQuizName());
        response.setQuizTheme(quiz.getQuizTheme());
        response.setDate(quiz.getDate());
        response.setTime(quiz.getTime());
        response.setDescription(quiz.getDescription());
        response.setStatus(quiz.getStatus());
        response.setNumberOfRounds(quiz.getNumberOfRounds());
        response.setMaxPoints(quiz.getMaxPoints());
        response.setLocationId(quiz.getLocationId());

        // Dohvati lokaciju ako postoji
        if (quiz.getLocationId() != null) {
            Optional<Location> location = supabaseRepository.findByColumn(
                    "location", "location_id", quiz.getLocationId(), Location.class
            );
            location.ifPresent(loc -> response.setLocationName(loc.getLocationName()));
        }

        return response;
    }

    /**
     * Mapira Team objekt na TeamHistoryResponse
     */
    private TeamHistoryResponse mapTeamToHistory(Team team) {
        TeamHistoryResponse response = new TeamHistoryResponse();
        response.setTeamId(team.getTeamId());
        response.setTeamName(team.getTeamName());
        response.setNumberOfMembers(team.getNumberOfMembers());
        response.setApplicationStatus(team.getApplicationStatus());
        response.setPoints(team.getPoints());
        response.setRank(team.getRank());
        response.setMembers(team.getMembers());
        return response;
    }
}

