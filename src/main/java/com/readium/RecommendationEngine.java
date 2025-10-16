package com.readium;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {
    
    public List<Book> getRecommendations(User user, List<Book> books) {
        return books.stream()
            .filter(book -> !user.getReadBooks().contains(book.getId()))
            .sorted(Comparator
                .comparingInt((Book book) -> -countGenreMatches(book, user))
                .thenComparingDouble((Book book) -> -book.getRating()))
            .collect(Collectors.toList());
    }
    
    private int countGenreMatches(Book book, User user) {
        return (int) book.getGenres().stream()
            .filter(genre -> user.getFavoriteGenres().contains(genre))
            .count();
    }
}
