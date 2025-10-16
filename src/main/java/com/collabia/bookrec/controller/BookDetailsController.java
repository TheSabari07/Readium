package com.collabia.bookrec.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class BookDetailsController {

    @FXML
    private Button backButton;

    @FXML
    private ImageView bookCoverImage;

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label bookAuthorLabel;

    @FXML
    private Label bookRatingLabel;

    @FXML
    private Label bookGenresLabel;

    @FXML
    private Text bookDescriptionText;

    @FXML
    private Button addToReadButton;

    @FXML
    private Button addToLikedButton;

    @FXML
    public void initialize() {
        // Initialize controller
        // Load book details
    }

    public void setBook(String bookId) {
        // TODO: Load and display book details by ID
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Navigate back to previous view
        // TODO: Implement back navigation
    }

    @FXML
    private void handleAddToRead(ActionEvent event) {
        // Mark book as read
        // TODO: Implement add to read list
    }

    @FXML
    private void handleAddToLiked(ActionEvent event) {
        // Add book to liked list
        // TODO: Implement add to liked list
    }
}
