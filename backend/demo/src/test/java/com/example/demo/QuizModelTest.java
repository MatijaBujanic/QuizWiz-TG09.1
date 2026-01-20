package com.example.demo;

import com.example.demo.model.Quiz;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuizModelTest {

    @Test
    void testQuizCreation() {
        // Kreiranje Quiz-a
        Quiz quiz = new Quiz();
        quiz.setQuizName("Kviz znanja");
        quiz.setQuizTheme("Opce znanje");
        quiz.setApplicationType("Standard");
        quiz.setStatus("Active");
        quiz.setNumberOfRounds(3);
        quiz.setMaxPoints(100);
        quiz.setOrganizerId(1);

        // provjera rezutata
        assertEquals("Kviz znanja", quiz.getQuizName());
        assertEquals("Opce znanje", quiz.getQuizTheme());
        assertEquals("Standard", quiz.getApplicationType());
        assertEquals("Active", quiz.getStatus());
        assertEquals(3, quiz.getNumberOfRounds());
        assertEquals(100, quiz.getMaxPoints());
        assertEquals(1, quiz.getOrganizerId());
    }

    @Test
    void testQuizEdgeCases() {
        // Rubni slučajevi s null i praznim vrijednostima
        Quiz quiz = new Quiz();
        quiz.setQuizName(null);
        quiz.setQuizTheme("");
        quiz.setNumberOfRounds(0);
        quiz.setMaxPoints(0);
        quiz.setOrganizerId(null);

        // Provjera rubnih slučajeva
        assertNull(quiz.getQuizName());
        assertEquals("", quiz.getQuizTheme());
        assertEquals(0, quiz.getNumberOfRounds());
        assertEquals(0, quiz.getMaxPoints());
        assertNull(quiz.getOrganizerId());
    }

}
