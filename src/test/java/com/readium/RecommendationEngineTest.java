package com.readium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationEngineTest {

    private RecommendationEngine recommendationEngine;
    private List<Book> books;
    private User user;

    @BeforeEach
    void setUp() {
        recommendationEngine = new RecommendationEngine();

        // Mock books
        books = Arrays.asList(
            new Book("1", "Book A", "Author X", Arrays.asList("Fantasy", "Adventure"), 4.5),
            new Book("2", "Book B", "Author Y", Arrays.asList("Science Fiction"), 4.0),
            new Book("3", "Book C", "Author X", Arrays.asList("Fantasy"), 3.5),
            new Book("4", "Book D", "Author Z", Arrays.asList("Romance"), 4.8),
            new Book("5", "Book E", "Author Y", Arrays.asList("Science Fiction", "Adventure"), 4.2)
        );

        // Mock user
        user = new User();
        user.setFavoriteGenres(Arrays.asList("Fantasy", "Science Fiction"));
        user.setReadBooks(Arrays.asList("1", "3"));
    }

    @Test
    void testRecommendationsBasedOnGenreAndAuthor() {
        List<Book> recommendations = recommendationEngine.getRecommendations(user, books);

        assertEquals(3, recommendations.size());
        assertEquals("Book E", recommendations.get(0).getTitle());
        assertEquals("Book B", recommendations.get(1).getTitle());
        assertEquals("Book D", recommendations.get(2).getTitle());
    }

    @Test
    void testExclusionOfReadBooks() {
        List<Book> recommendations = recommendationEngine.getRecommendations(user, books);

        assertTrue(recommendations.stream().noneMatch(book -> book.getId().equals("1")));
        assertTrue(recommendations.stream().noneMatch(book -> book.getId().equals("3")));
    }

    @Test
    void testRecommendationsUpdateAfterUserReadsNewBook() {
        user.getReadBooks().add("2");

        List<Book> recommendations = recommendationEngine.getRecommendations(user, books);

        assertEquals(2, recommendations.size());
        assertEquals("Book E", recommendations.get(0).getTitle());
        assertEquals("Book D", recommendations.get(1).getTitle());
    }
}
