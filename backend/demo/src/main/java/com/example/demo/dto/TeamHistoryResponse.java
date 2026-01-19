package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamHistoryResponse {

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

    private String[] members;

    public TeamHistoryResponse() {}

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

    public String[] getMembers() { return members; }
    public void setMembers(String[] members) { this.members = members; }
}

