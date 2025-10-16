package com.collabia.bookrec.controller;

import java.io.IOException;

import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.service.AuthService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signupLink;

    private AuthService authService;

    public LoginController() {
        UserDAO userDAO = new UserDAO();
        this.authService = new AuthService(userDAO);
    }

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);

        // Add real-time validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                validateEmailInline();
            }
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                errorLabel.setVisible(false);
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Clear previous errors
        errorLabel.setVisible(false);

        // Validate inputs
        if (!validateInputs(email, password)) {
            return;
        }

        // Attempt login
        User user = authService.login(email, password);

        if (user != null) {
            // Login successful
            openDashboard(user);
        } else {
            // Login failed
            showError("Invalid email or password. Please try again.");
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return false;
        }

        if (!isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return false;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.");
            return false;
        }

        return true;
    }

    private void validateEmailInline() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: red;");
        } else {
            emailField.setStyle("");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Pass user to DashboardController
            DashboardController controller = loader.getController();
            controller.setUser(user);

            // Get current stage and switch scene
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Readium - Dashboard");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard. Please try again.");
        }
    }

    @FXML
    private void handleSignupLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/signup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signupLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Readium - Sign Up");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load signup page.");
        }
    }
}
