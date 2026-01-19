package com.example.demo.service;

import com.example.demo.dto.QuizRatingResponse;
import com.example.demo.model.QuizRating;
import com.example.demo.repository.SupabaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizRatingService {

    private final SupabaseRepository supabaseRepository;

    public QuizRatingService(SupabaseRepository supabaseRepository) {
        this.supabaseRepository = supabaseRepository;
    }

    /**
     * Korisnik ocjenjuje kviz (1-5 zvjezdica)
     */
    public QuizRating rateQuiz(Integer quizId, Integer userId, Integer rating) {
        // Validacija
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Provjeri postoji li već ocjena od ovog korisnika
        String queryParams = "quiz_id=eq." + quizId + "&user_id=eq." + userId;
        List<QuizRating> existing = supabaseRepository.findAll("quizrating", queryParams, QuizRating.class);

        QuizRating quizRating = new QuizRating();
        quizRating.setQuizId(quizId);
        quizRating.setUserId(userId);
        quizRating.setRating(rating);

        if (!existing.isEmpty()) {
            // Ažuriraj postojeću ocjenu koristeći UPSERT
            quizRating.setRatingId(existing.get(0).getRatingId());
            return supabaseRepository.upsert("quizrating", quizRating);
        } else {
            // Kreiraj novu ocjenu
            return supabaseRepository.save("quizrating", quizRating, true);
        }
    }

    /**
     * Dohvaća prosječnu ocjenu i broj ocjena za kviz
     */
    public QuizRatingResponse getQuizRating(Integer quizId, Integer userId) {
        String queryParams = "quiz_id=eq." + quizId;
        List<QuizRating> ratings = supabaseRepository.findAll("quizrating", queryParams, QuizRating.class);

        QuizRatingResponse response = new QuizRatingResponse();
        response.setQuizId(quizId);
        response.setRatingCount(ratings.size());

        if (ratings.isEmpty()) {
            response.setAverageRating(0.0);
            response.setUserRating(null);
        } else {
            // Izračunaj prosjek
            double average = ratings.stream()
                    .mapToInt(QuizRating::getRating)
                    .average()
                    .orElse(0.0);
            response.setAverageRating(Math.round(average * 10) / 10.0); // Zaokruži na 1 decimalu

            // Pronađi ocjenu trenutnog korisnika
            if (userId != null) {
                Optional<QuizRating> userRating = ratings.stream()
                        .filter(r -> r.getUserId().equals(userId))
                        .findFirst();
                response.setUserRating(userRating.map(QuizRating::getRating).orElse(null));
            }
        }

        return response;
    }

    /**
     * Dohvaća samo prosječnu ocjenu (bez user-specific podataka)
     */
    public QuizRatingResponse getQuizRating(Integer quizId) {
        return getQuizRating(quizId, null);
    }

    /**
     * Briše ocjenu korisnika
     */
    public void deleteRating(Integer quizId, Integer userId) {
        String queryParams = "quiz_id=eq." + quizId + "&user_id=eq." + userId;
        List<QuizRating> existing = supabaseRepository.findAll("quizrating", queryParams, QuizRating.class);

        if (!existing.isEmpty()) {
            supabaseRepository.delete("quizrating", "rating_id", existing.get(0).getRatingId());
        }
    }
}