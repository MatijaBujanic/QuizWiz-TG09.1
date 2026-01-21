package com.example.demo.dto;

public class RateQuizRequest {

    private Integer rating; // 1-5 zvjezdica

    public RateQuizRequest() {}

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
