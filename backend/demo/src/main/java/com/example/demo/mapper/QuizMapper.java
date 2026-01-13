package com.example.demo.mapper;

import com.example.demo.dto.CreateQuizRequest;
import com.example.demo.dto.QuizResponse;
import com.example.demo.model.Quiz;
import com.example.demo.model.QuizRow;

import java.time.LocalTime;

public final class QuizMapper {
    private QuizMapper() {}

    public static Quiz toModel(CreateQuizRequest r) {
        Quiz q = new Quiz();
        q.setQuizName(r.getQuizName());
        q.setQuizTheme(r.getQuizTheme());
        q.setApplicationType(r.getApplicationType());
        q.setDate(r.getDate());
        q.setTime(r.getTime());
        q.setDescription(r.getDescription());
        q.setStatus(r.getStatus());
        q.setNumberOfRounds(r.getNumberOfRounds());
        q.setMaxPoints(r.getMaxPoints());
        q.setOrganizerId(r.getOrganizerId());
        q.setLocationId(r.getLocationId());
        return q;
    }

    public static QuizResponse toResponse(Quiz q) {
        QuizResponse r = new QuizResponse();
        r.setQuizName(q.getQuizName());
        r.setQuizTheme(q.getQuizTheme());
        r.setApplicationType(q.getApplicationType());
        r.setDate(q.getDate());
        r.setTime(q.getTime());
        r.setDescription(q.getDescription());
        r.setStatus(q.getStatus());
        r.setNumberOfRounds(q.getNumberOfRounds());
        r.setMaxPoints(q.getMaxPoints());
        r.setOrganizerId(q.getOrganizerId());
        r.setLocationId(q.getLocationId());
        return r;
    }

    public static QuizRow toRow(CreateQuizRequest r) {
        QuizRow q = new QuizRow();
        q.setQuizName(r.getQuizName());
        q.setQuizTheme(r.getQuizTheme());
        q.setApplicationType(r.getApplicationType());
        q.setDate(r.getDate());
        q.setTime(r.getTime());
        q.setDescription(r.getDescription());
        q.setStatus(r.getStatus());
        q.setNumberOfRounds(r.getNumberOfRounds());
        q.setMaxPoints(r.getMaxPoints());
        q.setOrganizerId(r.getOrganizerId());
        q.setLocationId(r.getLocationId());
        return q;
    }

    public static QuizResponse toResponse(QuizRow q) {
        QuizResponse r = new QuizResponse();
        r.setQuizId(q.getQuizId());
        r.setQuizName(q.getQuizName());
        r.setQuizTheme(q.getQuizTheme());
        r.setApplicationType(q.getApplicationType());
        r.setDate(q.getDate());
        r.setTime(q.getTime());
        r.setDescription(q.getDescription());
        r.setStatus(q.getStatus());
        r.setNumberOfRounds(q.getNumberOfRounds());
        r.setMaxPoints(q.getMaxPoints());
        r.setCreatedAt(q.getCreatedAt());
        r.setOrganizerId(q.getOrganizerId());
        r.setLocationId(q.getLocationId());
        return r;
    }


}
