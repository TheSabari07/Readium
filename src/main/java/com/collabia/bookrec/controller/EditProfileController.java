package com.collabia.bookrec.controller;

import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProfileController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private ListView<String> genresListView;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private User currentUser;
    private UserDAO userDAO;
    private ProfileController profileController;
    
    private static final List<String> AVAILABLE_GENRES = Arrays.asList(
        "Fiction", "Non-Fiction", "Mystery", "Thriller", "Romance",
        "Science Fiction", "Fantasy", "Biography", "History", "Self-Help",
        "Horror", "Adventure", "Poetry", "Drama", "Comedy"
    );
    
    public EditProfileController() {
        this.userDAO = new UserDAO();
    }
    
    @FXML
    private void initialize() {
        // Setup genres list with multiple selection
        genresListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        genresListView.getItems().addAll(AVAILABLE_GENRES);
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        populateFields();
    }
    
    public void setProfileController(ProfileController controller) {
        this.profileController = controller;
    }
    
    private void populateFields() {
        if (currentUser == null) return;
        
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        
        // Select user's favorite genres
        List<String> favoriteGenres = currentUser.getFavoriteGenres();
        if (favoriteGenres != null) {
            for (String genre : favoriteGenres) {
                int index = genresListView.getItems().indexOf(genre);
                if (index >= 0) {
                    genresListView.getSelectionModel().select(index);
                }
            }
        }
    }
    
    @FXML
    private void handleSave() {
        // Validate input
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (name.isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }
        
        if (email.isEmpty() || !email.contains("@")) {
            showError("Please enter a valid email address.");
            return;
        }
        
        // Update user object
        currentUser.setName(name);
        currentUser.setEmail(email);
        
        // Get selected genres
        List<String> selectedGenres = new ArrayList<>(genresListView.getSelectionModel().getSelectedItems());
        currentUser.setFavoriteGenres(selectedGenres);
        
        // Save to database
        boolean success = userDAO.updateUser(currentUser);
        
        if (success) {
            showSuccess("Profile updated successfully!");
            
            // Refresh parent controller
            if (profileController != null) {
                profileController.refreshProfile();
            }
            
            // Close dialog
            closeDialog();
        } else {
            showError("Failed to update profile. Please try again.");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
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
