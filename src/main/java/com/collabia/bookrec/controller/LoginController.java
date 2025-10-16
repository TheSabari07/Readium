package com.collabia.bookrec.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink signupLink;

    @FXML
    public void initialize() {
        // Initialize controller
        // Set up validators, bindings, etc.
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Handle login button click
        String email = emailField.getText();
        String password = passwordField.getText();
        // TODO: Implement login logic
    }

    @FXML
    private void handleSignupLink(ActionEvent event) {
        // Navigate to signup view
        // TODO: Implement navigation to signup
    }
}
