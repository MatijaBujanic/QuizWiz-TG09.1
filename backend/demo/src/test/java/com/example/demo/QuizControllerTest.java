package com.example.demo;

import com.example.demo.controller.QuizController;
import com.example.demo.dto.QuizResponse;
import com.example.demo.model.QuizRow;
import com.example.demo.repository.SupabaseRepository;
import com.example.demo.service.QuizRatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizControllerTest {

    @Test
    void testPingEndpoint() {
        // Provjera vraca li ping endpoint "pong"
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        String result = controller.ping();

        assertEquals("pong", result);
    }

    @Test
    void testPingEndpointNotNull() {
        // Test provjera vraća li ping endpoint null vrijednost
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        String result = controller.ping();

        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    void testListQuizzesWithoutFilters() {
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        List<QuizRow> mockQuizzes = List.of(new QuizRow());
        when(repository.findAll("quiz", "order=date.asc,time.asc", QuizRow.class)).thenReturn(mockQuizzes);

        List<QuizResponse> result = controller.list(null, null, null, null, null, null);

        assertNotNull(result);
        verify(repository).findAll("quiz", "order=date.asc,time.asc", QuizRow.class);
    }

    @Test
    void testListQuizzesWithNameFilter() {
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        List<QuizRow> mockQuizzes = List.of(new QuizRow());
        when(repository.findAll(eq("quiz"), anyString(), eq(QuizRow.class))).thenReturn(mockQuizzes);

        List<QuizResponse> result = controller.list("test", null, null, null, null, null);

        assertNotNull(result);
        verify(repository).findAll(eq("quiz"), contains("quiz_name=ilike.*test*"), eq(QuizRow.class));
    }

    @Test
    void testGetQuizById() {
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        QuizRow mockQuiz = new QuizRow();
        when(repository.findByColumn("quiz", "quiz_id", 1, QuizRow.class)).thenReturn(Optional.of(mockQuiz));

        ResponseEntity<QuizResponse> result = controller.get(1, null);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
    }

    @Test
    void testGetQuizByIdNotFound() {
        SupabaseRepository repository = mock(SupabaseRepository.class);
        QuizRatingService ratingService = mock(QuizRatingService.class);
        QuizController controller = new QuizController(repository, ratingService);

        when(repository.findByColumn("quiz", "quiz_id", 1, QuizRow.class)).thenReturn(Optional.empty());

        ResponseEntity<QuizResponse> result = controller.get(1, null);

        assertEquals(404, result.getStatusCodeValue());
    }
}
