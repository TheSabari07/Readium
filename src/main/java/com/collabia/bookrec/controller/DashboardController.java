package com.collabia.bookrec.controller;

import com.collabia.bookrec.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class DashboardController {

    @FXML
    private TextField searchField;

    @FXML
    private Button profileButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button recommendationsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private GridPane booksGridPane;

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    @FXML
    public void initialize() {
        // Initialize controller
        // Load books and populate grid
        loadBooks();
    }

    private void loadBooks() {
        // TODO: Load books from service and populate booksGridPane
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
        }
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        // Navigate to profile view
        // TODO: Implement navigation to profile
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Handle logout
        // TODO: Implement logout logic
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        // Refresh dashboard
        // TODO: Implement dashboard refresh
    }

    @FXML
    private void handleRecommendations(ActionEvent event) {
        // Show recommendations
        // TODO: Implement recommendations view
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        // Navigate to settings
        // TODO: Implement navigation to settings
    }
}
