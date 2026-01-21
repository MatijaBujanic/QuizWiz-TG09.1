package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizRatingResponse {

    @JsonProperty("quiz_id")
    private Integer quizId;

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("user_rating")
    private Integer userRating; // Ocjena trenutnog korisnika (null ako nije ocijenio)

    public QuizRatingResponse() {}

    // Getters & Setters
    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }
}