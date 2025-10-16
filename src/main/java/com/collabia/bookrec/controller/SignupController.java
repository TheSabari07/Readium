package com.collabia.bookrec.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignupController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button signupButton;

    @FXML
    private Hyperlink loginLink;

    @FXML
    public void initialize() {
        // Initialize controller
        // Set up validators, bindings, etc.
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        // Handle signup button click
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        // TODO: Implement signup logic and validation
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        // Navigate to login view
        // TODO: Implement navigation to login
    }
}
