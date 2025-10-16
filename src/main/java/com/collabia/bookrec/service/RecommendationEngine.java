package com.collabia.bookrec.service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.collabia.bookrec.dao.BookDAO;
import com.collabia.bookrec.model.Book;
import com.collabia.bookrec.model.User;

public class RecommendationEngine {

    private final BookDAO bookDAO;

    public RecommendationEngine(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    /**
     * Recommends books for a user based on a content-based filtering algorithm.
     * <p>
     * The recommendation score for each book is calculated using the formula:
     * {@code score = genreOverlapCount + authorBonus + ratingBonus}
     * where:
     * <ul>
     *   <li>{@code genreOverlapCount}: The number of genres shared between the book and the user's favorite genres.</li>
     *   <li>{@code authorBonus}: A bonus of 1 if the book's author has also written a book the user has liked, 0 otherwise.</li>
     *   <li>{@code ratingBonus}: The book's average rating, normalized and weighted. Calculated as {@code (averageRating / 5.0) * 0.3}. Assumes ratings are on a 1-5 scale.</li>
     * </ul>
     * Books the user has already read are excluded from the recommendations.
     * <p>
     * Complexity:
     * Let N be the total number of books and M be the number of books the user has liked.
     * The complexity is O(N * log(N) + M) due to fetching liked books' authors, iterating through all books to score them, and sorting the scored books.
     *
     * @param user  The user for whom to generate recommendations.
     * @param limit The maximum number of recommended books to return.
     * @return A list of recommended books, sorted by relevance score in descending order.
     */
    public List<Book> recommendForUser(User user, int limit) {
        List<Book> allBooks = bookDAO.findAll();
        Set<String> readBookIds = user.getReadBooks() == null ? Collections.emptySet() : new HashSet<>(user.getReadBooks());
        Set<String> userFavoriteGenres = user.getFavoriteGenres() == null ? Collections.emptySet() : new HashSet<>(user.getFavoriteGenres());

        // Get authors of books the user liked to give an author bonus.
        List<String> likedBooks = user.getLikedBooks() == null ? Collections.emptyList() : user.getLikedBooks();
        Set<String> likedAuthors = likedBooks.stream()
                .map(bookDAO::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Book::getAuthor)
                .collect(Collectors.toSet());

        return allBooks.stream()
                .filter(book -> !readBookIds.contains(book.getId()))
                .map(book -> {
                    double score = 0.0;

                    // 1. Genre Overlap
                    Set<String> bookGenres = new HashSet<>(book.getGenres());
                    bookGenres.retainAll(userFavoriteGenres);
                    score += bookGenres.size();

                    // 2. Author Match Bonus
                    if (likedAuthors.contains(book.getAuthor())) {
                        score += 1.0;
                    }

                    // 3. Weighted Rating
                    if (book.getAverageRating() > 0) {
                        score += (book.getAverageRating() / 5.0) * 0.3;
                    }

                    return new AbstractMap.SimpleEntry<>(book, score);
                })
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
