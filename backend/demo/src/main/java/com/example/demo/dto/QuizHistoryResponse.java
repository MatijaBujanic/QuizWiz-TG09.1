package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class QuizHistoryResponse {

    @JsonProperty("quiz_id")
    private Integer quizId;

    @JsonProperty("quiz_name")
    private String quizName;

    @JsonProperty("quiz_theme")
    private String quizTheme;

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String status;

    @JsonProperty("number_of_rounds")
    private Integer numberOfRounds;

    @JsonProperty("max_points")
    private Integer maxPoints;

    @JsonProperty("location_id")
    private Integer locationId;

    @JsonProperty("location_name")
    private String locationName;

    // Lista timova koji su sudjelovali (samo ako je organizer)
    private List<TeamHistoryResponse> teams;

    // Tim na kojem je korisnik sudjelovao (samo ako je user)
    @JsonProperty("user_team")
    private TeamHistoryResponse userTeam;

    public QuizHistoryResponse() {}

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }

    public String getQuizTheme() { return quizTheme; }
    public void setQuizTheme(String quizTheme) { this.quizTheme = quizTheme; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getNumberOfRounds() { return numberOfRounds; }
    public void setNumberOfRounds(Integer numberOfRounds) { this.numberOfRounds = numberOfRounds; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public List<TeamHistoryResponse> getTeams() { return teams; }
    public void setTeams(List<TeamHistoryResponse> teams) { this.teams = teams; }

    public TeamHistoryResponse getUserTeam() { return userTeam; }
    public void setUserTeam(TeamHistoryResponse userTeam) { this.userTeam = userTeam; }
}

