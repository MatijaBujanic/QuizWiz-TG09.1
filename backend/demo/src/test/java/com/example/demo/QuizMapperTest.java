package com.example.demo;

import com.example.demo.dto.CreateQuizRequest;
import com.example.demo.mapper.QuizMapper;
import com.example.demo.model.Quiz;
import com.example.demo.dto.QuizResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuizMapperTest {

    @Test
    void testQuizMapperNull() {
        // Test mapera koji obrađuje null vrijednosti u Quizu
        Quiz quiz = new Quiz();
        quiz.setQuizName(null);
        quiz.setQuizTheme(null);
        quiz.setStatus(null);
        quiz.setNumberOfRounds(null);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertNull(response.getQuizName());
        assertNull(response.getQuizTheme());
        assertNull(response.getStatus());
        assertNull(response.getNumberOfRounds());
    }

    @Test
    void testQuizMapperWithData() {
        // Test mapera koji ispravno preslikava podatke kviza u response
        Quiz quiz = new Quiz();
        quiz.setQuizName("History Quiz");
        quiz.setQuizTheme("World History");
        quiz.setMaxPoints(50);
        quiz.setOrganizerId(5);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals("History Quiz", response.getQuizName());
        assertEquals("World History", response.getQuizTheme());
        assertEquals(50, response.getMaxPoints());
        assertEquals(5, response.getOrganizerId());
    }
}
