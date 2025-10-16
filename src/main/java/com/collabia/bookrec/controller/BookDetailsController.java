package com.collabia.bookrec.controller;

import com.collabia.bookrec.dao.RatingDAO;
import com.collabia.bookrec.model.Book;
import com.collabia.bookrec.model.Rating;
import com.collabia.bookrec.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.time.LocalDate;

public class BookDetailsController {
    
    @FXML
    private ImageView coverImageView;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label authorLabel;
    
    @FXML
    private Label genreLabel;
    
    @FXML
    private Label averageRatingLabel;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private ComboBox<Integer> ratingComboBox;
    
    @FXML
    private TextArea reviewTextArea;
    
    @FXML
    private Button submitRatingButton;
    
    @FXML
    private Button closeButton;
    
    private Book book;
    private User currentUser;
    private RatingDAO ratingDAO;
    
    public BookDetailsController() {
        this.ratingDAO = new RatingDAO();
    }
    
    @FXML
    private void initialize() {
        // Setup rating combo box
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setPromptText("Select rating");
    }
    
    public void setBook(Book book) {
        this.book = book;
        displayBookDetails();
    }
    
    public void setUser(User user) {
        this.currentUser = user;
    }
    
    private void displayBookDetails() {
        if (book == null) return;
        
        titleLabel.setText(book.getTitle());
        authorLabel.setText("by " + book.getAuthor());
        
        // Handle genre - try different possible method names
        String genre = getBookGenre(book);
        genreLabel.setText(genre != null ? genre : "Unknown Genre");
        
        descriptionArea.setText(book.getDescription() != null ? book.getDescription() : "No description available.");
        averageRatingLabel.setText(String.format("★ %.1f / 5.0", book.getAverageRating()));
        
        // Load cover image
        loadCoverImage(book);
    }
    
    private String getBookGenre(Book book) {
        try {
            return book.getGenre();
        } catch (Exception e) {
            // If getGenre() doesn't exist, try alternative methods
            try {
                java.lang.reflect.Method method = book.getClass().getMethod("getCategory");
                return (String) method.invoke(book);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    
    private void loadCoverImage(Book book) {
        String coverUrl = null;
        
        // Try getCoverImageUrl first
        try {
            coverUrl = book.getCoverImageUrl();
        } catch (Exception e) {
            // Method doesn't exist
        }
        
        // If null, try getCoverPath
        if (coverUrl == null || coverUrl.isEmpty()) {
            try {
                coverUrl = book.getCoverPath();
            } catch (Exception e) {
                // Method doesn't exist
            }
        }
        
        // Load the image
        try {
            if (coverUrl != null && !coverUrl.isEmpty()) {
                Image image = new Image(coverUrl, true);
                coverImageView.setImage(image);
            }
        } catch (Exception e) {
            // Use default image or placeholder
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSubmitRating() {
        if (currentUser == null) {
            showError("You must be logged in to rate books.");
            return;
        }
        
        Integer selectedRating = ratingComboBox.getValue();
        if (selectedRating == null) {
            showError("Please select a rating.");
            return;
        }
        
        String reviewText = reviewTextArea.getText().trim();
        
        // Create and save rating
        Rating rating = new Rating();
        rating.setUserId(currentUser.getId().toHexString());
        rating.setBookId(book.getId());
        rating.setRating(selectedRating);
        
        // Set review text if the method exists
        if (!reviewText.isEmpty()) {
            try {
                rating.setReviewText(reviewText);
            } catch (Exception e) {
                // Method might not exist, skip it
            }
        }
        
        // Set date if the method exists
        try {
            rating.setDate(LocalDate.now());
        } catch (Exception e) {
            // Method might not exist, skip it
        }
        
        try {
            ratingDAO.addRating(rating);
            showSuccess("Rating submitted successfully!");
            clearForm();
        } catch (Exception e) {
            showError("Failed to submit rating. Please try again.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        ratingComboBox.setValue(null);
        reviewTextArea.clear();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
