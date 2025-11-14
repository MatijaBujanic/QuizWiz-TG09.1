package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Team {

    @JsonProperty("team_id")
    private Integer teamId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("number_of_members")
    private Integer numberOfMembers;

    @JsonProperty("application_status")
    private String applicationStatus; 

    private Integer points;
    private Integer rank;

    @JsonProperty("sent_at")
    private LocalDateTime sentAt;

    @JsonProperty("approved_at")
    private LocalDateTime approvedAt;

    @JsonProperty("created_by")
    private Integer createdBy; 

    @JsonProperty("quiz_id")
    private Integer quizId;    

    public Team() {}

    public Team(Integer teamId, String teamName, Integer numberOfMembers,
                String applicationStatus, Integer points, Integer rank,
                LocalDateTime sentAt, LocalDateTime approvedAt,
                Integer createdBy, Integer quizId) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.numberOfMembers = numberOfMembers;
        this.applicationStatus = applicationStatus;
        this.points = points;
        this.rank = rank;
        this.sentAt = sentAt;
        this.approvedAt = approvedAt;
        this.createdBy = createdBy;
        this.quizId = quizId;
    }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getNumberOfMembers() { return numberOfMembers; }
    public void setNumberOfMembers(Integer numberOfMembers) { this.numberOfMembers = numberOfMembers; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }
}
