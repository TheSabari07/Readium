package com.collabia.bookrec.controller;

import java.io.IOException;

import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.service.AuthService;
import com.collabia.bookrec.utils.ToastUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button signupButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Label emailErrorLabel;
    
    @FXML
    private Label passwordErrorLabel;
    
    @FXML
    private Button loginLink;
    
    private AuthService authService;
    
    public SignupController() {
        UserDAO userDAO = new UserDAO();
        this.authService = new AuthService(userDAO);
    }
    
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        
        // Real-time validation
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateEmailInline();
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePasswordInline();
        });
        
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePasswordMatchInline();
        });
    }
    
    @FXML
    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Clear previous errors
        hideAllErrors();
        
        // Validate all inputs
        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }
        
        // Attempt registration
        User user = authService.register(name, email, password);
        
        if (user != null) {
            // Registration successful
            ToastUtil.showSuccess(signupButton.getScene().getWindow(), 
                "Account created successfully! Please login.");
            
            // Navigate to login after a short delay
            navigateToLogin();
        } else {
            // Registration failed (email already exists)
            showError("An account with this email already exists.");
        }
    }
    
    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return false;
        }
        
        if (name.length() < 2) {
            showError("Name must be at least 2 characters long.");
            return false;
        }
        
        if (!isValidEmail(email)) {
            showEmailError("Please enter a valid email address.");
            return false;
        }
        
        if (password.length() < 6) {
            showPasswordError("Password must be at least 6 characters long.");
            return false;
        }
        
        if (!password.equals(confirmPassword)) {
            showPasswordError("Passwords do not match.");
            return false;
        }
        
        return true;
    }
    
    private void validateEmailInline() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: red;");
            showEmailError("Invalid email format");
        } else {
            emailField.setStyle("");
            emailErrorLabel.setVisible(false);
        }
    }
    
    private void validatePasswordInline() {
        String password = passwordField.getText();
        if (!password.isEmpty() && password.length() < 6) {
            passwordField.setStyle("-fx-border-color: red;");
            showPasswordError("Min 6 characters");
        } else {
            passwordField.setStyle("");
            passwordErrorLabel.setVisible(false);
        }
    }
    
    private void validatePasswordMatchInline() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (!confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
            showPasswordError("Passwords don't match");
        } else {
            confirmPasswordField.setStyle("");
            passwordErrorLabel.setVisible(false);
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
    
    private void showEmailError(String message) {
        emailErrorLabel.setText(message);
        emailErrorLabel.setVisible(true);
    }
    
    private void showPasswordError(String message) {
        passwordErrorLabel.setText(message);
        passwordErrorLabel.setVisible(true);
    }
    
    private void hideAllErrors() {
        errorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
    }
    
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Readium - Login");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load login page.");
        }
    }
    
    @FXML
    private void handleLoginLink() {
        navigateToLogin();
    }
}
