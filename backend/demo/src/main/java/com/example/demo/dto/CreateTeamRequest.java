package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateTeamRequest {

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("quiz_id")
    private Integer quizId;

    private String[] members;


    public CreateTeamRequest() {}

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public String[] getMembers() { return members; }
    public void setMembers(String[] members) { this.members = members; }
}