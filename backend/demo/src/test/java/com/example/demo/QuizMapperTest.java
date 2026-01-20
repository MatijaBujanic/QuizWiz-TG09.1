package com.example.demo;

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
        quiz.setQuizName("Povijesni Quiz");
        quiz.setQuizTheme("Svjetska povijest");
        quiz.setMaxPoints(50);
        quiz.setOrganizerId(5);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals("Povijesni Quiz", response.getQuizName());
        assertEquals("Svjetska povijest", response.getQuizTheme());
        assertEquals(50, response.getMaxPoints());
        assertEquals(5, response.getOrganizerId());
    }

    @Test
    void testQuizResponseHasCorrectQuizName() {
        // Test mappera koji ispravno prepisuje quiz naziv
        Quiz quiz = new Quiz();
        quiz.setQuizName("Engleski jezik");

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals("Engleski jezik", response.getQuizName());
    }

    @Test
    void testQuizResponseHasCorrectMaxPoints() {
        // Test mappera koji ispravno prepisuje max bodove
        Quiz quiz = new Quiz();
        quiz.setMaxPoints(150);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals(150, response.getMaxPoints());
    }

    @Test
    void testQuizResponseHasCorrectOrganizerId() {
        // Test mappera koji ispravno prepisuje ID organizatora
        Quiz quiz = new Quiz();
        quiz.setOrganizerId(7);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals(7, response.getOrganizerId());
    }

    @Test
    void testQuizMapperWithNegativeValues() {
        // Test mappera s negativnim vrijednostima
        Quiz quiz = new Quiz();
        quiz.setMaxPoints(-50);
        quiz.setNumberOfRounds(-1);
        quiz.setOrganizerId(-5);

        QuizResponse response = QuizMapper.toResponse(quiz);

        assertEquals(-50, response.getMaxPoints());
        assertEquals(-1, response.getNumberOfRounds());
        assertEquals(-5, response.getOrganizerId());
    }
}
