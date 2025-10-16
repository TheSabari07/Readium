package com.collabia.bookrec.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.collabia.bookrec.dao.BookDAO;
import com.collabia.bookrec.model.Book;
import com.collabia.bookrec.model.User;

/**
 * RecommendationEngine provides content-based book recommendations for users.
 * Uses a hybrid scoring algorithm combining genre overlap, author preferences, and ratings.
 */
public class RecommendationEngine {

    private final BookDAO bookDAO;

    /**
     * Constructs a RecommendationEngine with the specified BookDAO.
     *
     * @param bookDAO the data access object for retrieving books
     */
    public RecommendationEngine(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public RecommendationEngine() {
        this.bookDAO = null;
    }

    public List<Book> getRecommendations(User user, List<Book> availableBooks) {
        if (user == null || availableBooks == null) {
            return new ArrayList<>();
        }

        Set<String> readBookIds = new HashSet<>(user.getReadBooks() != null ? user.getReadBooks() : new ArrayList<>());
        List<String> favoriteGenres = user.getFavoriteGenres() != null ? user.getFavoriteGenres() : new ArrayList<>();

        // Filter out books already read and score remaining books
        return availableBooks.stream()
                .filter(book -> !readBookIds.contains(book.getId()))
                .map(book -> new ScoredBook(book, calculateScore(book, favoriteGenres)))
                .sorted(Comparator.comparingDouble(ScoredBook::getScore).reversed())
                .map(ScoredBook::getBook)
                .collect(Collectors.toList());
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
        if (bookDAO == null) {
            return new ArrayList<>();
        }

        List<Book> allBooks = bookDAO.getAllBooks();
        Set<String> readBookIds = new HashSet<>(user.getReadBooks() != null ? user.getReadBooks() : new ArrayList<>());
        Set<String> preferredGenres = new HashSet<>(user.getFavoriteGenres() != null ? user.getFavoriteGenres() : new ArrayList<>());
        
        // Derive preferred authors from books the user has read and liked
        Set<String> preferredAuthors = new HashSet<>();
        if (user.getReadBooks() != null) {
            for (String bookId : user.getReadBooks()) {
                Optional<Book> readBook = allBooks.stream()
                        .filter(b -> b.getId().equals(bookId))
                        .findFirst();
                if (readBook.isPresent()) {
                    preferredAuthors.add(readBook.get().getAuthor());
                }
            }
        }

        // List to store books with their calculated scores
        List<BookScore> bookScores = new ArrayList<>();

        // Calculate score for each unread book
        for (Book book : allBooks) {
            // Skip books already read by the user
            if (readBookIds.contains(book.getId())) {
                continue;
            }

            // Calculate genre overlap count
            int genreOverlapCount = 0;
            for (String genre : book.getGenres()) {
                if (preferredGenres.contains(genre)) {
                    genreOverlapCount++;
                }
            }

            // Check author match
            int authorMatch = preferredAuthors.contains(book.getAuthor()) ? 1 : 0;

            // Normalize rating (assuming rating is out of 5)
            double normalizedAvgRating = book.getRating() / 5.0;

            // Calculate final score
            double score = genreOverlapCount + authorMatch + (normalizedAvgRating * 0.3);

            bookScores.add(new BookScore(book, score));
        }

        // Sort by score in descending order
        bookScores.sort(Comparator.comparingDouble(BookScore::getScore).reversed());

        // Extract top N books
        List<Book> recommendations = new ArrayList<>();
        int count = Math.min(limit, bookScores.size());
        for (int i = 0; i < count; i++) {
            recommendations.add(bookScores.get(i).getBook());
        }

        return recommendations;
    }

    private double calculateScore(Book book, List<String> favoriteGenres) {
        double score = 0.0;

        // Base score from rating (0-5 range)
        score += book.getRating() * 10;

        // Genre overlap bonus (up to 30 points)
        if (book.getGenres() != null && !favoriteGenres.isEmpty()) {
            long genreMatches = book.getGenres().stream()
                    .filter(favoriteGenres::contains)
                    .count();
            score += genreMatches * 15;
        }

        return score;
    }

    private static class ScoredBook {
        private final Book book;
        private final double score;

        public ScoredBook(Book book, double score) {
            this.book = book;
            this.score = score;
        }

        public Book getBook() {
            return book;
        }

        public double getScore() {
            return score;
        }
    }

    /**
     * Inner class to hold a book and its calculated recommendation score.
     */
    private static class BookScore {
        private final Book book;
        private final double score;

        public BookScore(Book book, double score) {
            this.book = book;
            this.score = score;
        }

        public Book getBook() {
            return book;
        }

        public double getScore() {
            return score;
        }
    }
}
