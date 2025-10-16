package com.collabia.bookrec.controller;

import java.io.IOException;

import com.collabia.bookrec.dao.SettingsDAO;
import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.Settings;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.service.AuthService;
import com.collabia.bookrec.util.PasswordUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class SettingsController {
    
    @FXML
    private ToggleButton themeToggle;
    
    @FXML
    private PasswordField oldPasswordField;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button changePasswordButton;
    
    @FXML
    private Button logoutButton;
    
    private SettingsDAO settingsDAO;
    private UserDAO userDAO;
    private AuthService authService;
    private Settings settings;
    
    @FXML
    public void initialize() {
        settingsDAO = new SettingsDAO();
        userDAO = new UserDAO();
        authService = new AuthService(userDAO);
        settings = settingsDAO.loadSettings();
        
        // Set theme toggle state
        themeToggle.setSelected(settings.isDarkMode());
        updateThemeToggleText();
        applyTheme();
    }
    
    @FXML
    private void handleThemeToggle() {
        String newTheme = themeToggle.isSelected() ? "Dark" : "Light";
        settings.setTheme(newTheme);
        settingsDAO.saveSettings(settings);
        updateThemeToggleText();
        applyTheme();
    }
    
    private void updateThemeToggleText() {
        themeToggle.setText(settings.isDarkMode() ? "Dark Mode" : "Light Mode");
    }
    
    private void applyTheme() {
        Scene scene = themeToggle.getScene();
        if (scene != null) {
            scene.getStylesheets().clear();
            if (settings.isDarkMode()) {
                scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
            } else {
                scene.getStylesheets().add(getClass().getResource("/css/light-theme.css").toExternalForm());
            }
        }
    }
    
    @FXML
    private void handleChangePassword() {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validation
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All password fields are required.");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New passwords do not match.");
            return;
        }
        
        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password must be at least 6 characters.");
            return;
        }
        
        // Get current user
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in.");
            return;
        }
        
        // Verify old password
        if (!PasswordUtil.verifyPassword(oldPassword, currentUser.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Old password is incorrect.");
            return;
        }
        
        // Update password
        try {
            String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
            currentUser.setPassword(hashedNewPassword);
            userDAO.update(currentUser);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully.");
            clearPasswordFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update password: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        authService.logout();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Readium - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login screen.");
        }
    }
    
    private void clearPasswordFields() {
        oldPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
