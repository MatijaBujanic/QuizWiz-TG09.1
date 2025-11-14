package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppliesTo {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("team_id")
    private Integer teamId;

    @JsonProperty("role_in_team")
    private String roleInTeam;

    public AppliesTo() {}

    public AppliesTo(Integer userId, Integer teamId, String roleInTeam) {
        this.userId = userId;
        this.teamId = teamId;
        this.roleInTeam = roleInTeam;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public String getRoleInTeam() { return roleInTeam; }
    public void setRoleInTeam(String roleInTeam) { this.roleInTeam = roleInTeam; }
}
