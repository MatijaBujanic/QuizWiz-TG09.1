package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Quiz {

    @JsonProperty("quiz_id")
    private Integer quizId;

    @JsonProperty("quiz_name")
    private String quizName;

    @JsonProperty("quiz_theme")
    private String quizTheme;

    @JsonProperty("application_type")
    private String applicationType;

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String status;

    @JsonProperty("number_of_rounds")
    private Integer numberOfRounds;

    @JsonProperty("max_points")
    private Integer maxPoints;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("organizer_id")
    private Integer organizerId;

    @JsonProperty("location_id")
    private Integer locationId;

    public Quiz() {}

    public Quiz(Integer quizId, String quizName, String quizTheme,
                String applicationType, LocalDate date, LocalTime time,
                String description, String status,
                Integer numberOfRounds, Integer maxPoints,
                LocalDateTime createdAt, Integer organizerId, Integer locationId) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.quizTheme = quizTheme;
        this.applicationType = applicationType;
        this.date = date;
        this.time = time;
        this.description = description;
        this.status = status;
        this.numberOfRounds = numberOfRounds;
        this.maxPoints = maxPoints;
        this.createdAt = createdAt;
        this.organizerId = organizerId;
        this.locationId = locationId;
    }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }

    public String getQuizTheme() { return quizTheme; }
    public void setQuizTheme(String quizTheme) { this.quizTheme = quizTheme; }

    public String getApplicationType() { return applicationType; }
    public void setApplicationType(String applicationType) { this.applicationType = applicationType; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getOrganizerId() { return organizerId; }
    public void setOrganizerId(Integer organizerId) { this.organizerId = organizerId; }

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
}
