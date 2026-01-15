package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateTeamRequest {

    @JsonProperty("team_name")
    private String teamName;

    // Članovi tima - može biti null ako ne mijenjamo
    private String[] members;

    public UpdateTeamRequest() {}

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String[] getMembers() { return members; }
    public void setMembers(String[] members) { this.members = members; }
}