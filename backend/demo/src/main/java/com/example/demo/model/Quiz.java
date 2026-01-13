package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

public class Quiz {

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

    @JsonProperty("organizer_id")
    private Integer organizerId;

    @JsonProperty("location_id")
    private Integer locationId;

    public Quiz() {
    }

    public String getQuizName() {
        return quizName;
    }
    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizTheme() {
        return quizTheme;
    }
    public void setQuizTheme(String quizTheme) {
        this.quizTheme = quizTheme;
    }

    public String getApplicationType() {
        return applicationType;
    }
    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNumberOfRounds() {
        return numberOfRounds;
    }
    public void setNumberOfRounds(Integer numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }
    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }
    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
