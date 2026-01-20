package com.example.demo;

import com.example.demo.controller.QuizController;
import com.example.demo.repository.SupabaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizControllerTest {

    @Test
    void testPingEndpoint() {
        // Provjera vraca li ping endpoint "pong"
        SupabaseRepository repository = mock(SupabaseRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();
        QuizController controller = new QuizController(repository, objectMapper);

        String result = controller.ping();

        assertEquals("pong", result);
    }

    @Test
    void testPingEndpointNotNull() {
        // Test provjera vraća li ping endpoint null vrijednost
        SupabaseRepository repository = mock(SupabaseRepository.class);
        ObjectMapper objectMapper = new ObjectMapper();
        QuizController controller = new QuizController(repository, objectMapper);

        String result = controller.ping();

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }
}
