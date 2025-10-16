package com.collabia.bookrec.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import com.collabia.bookrec.dao.BookDAO;
import com.collabia.bookrec.dao.RatingDAO;
import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.Book;
import com.collabia.bookrec.model.Rating;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.utils.BookCardFactory;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProfileController {
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private FlowPane favoriteGenresPane;
    
    @FXML
    private FlowPane readBooksPane;
    
    @FXML
    private FlowPane likedBooksPane;
    
    @FXML
    private Button editProfileButton;
    
    @FXML
    private Button exportHistoryButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label readBooksCountLabel;
    
    @FXML
    private Label likedBooksCountLabel;
    
    private User currentUser;
    private UserDAO userDAO;
    private BookDAO bookDAO;
    private RatingDAO ratingDAO;
    
    public ProfileController() {
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO();
        this.ratingDAO = new RatingDAO();
    }
    
    @FXML
    private void initialize() {
        // Initialize empty state
    }
    
    public void setUser(User user) {
        this.currentUser = user;
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        if (currentUser == null) return;
        
        nameLabel.setText(currentUser.getName());
        emailLabel.setText(currentUser.getEmail());
        
        displayFavoriteGenres();
        displayReadBooks();
        displayLikedBooks();
    }
    
    private void displayFavoriteGenres() {
        favoriteGenresPane.getChildren().clear();
        
        List<String> genres = currentUser.getFavoriteGenres();
        if (genres == null || genres.isEmpty()) {
            Label noGenresLabel = new Label("No favorite genres selected");
            noGenresLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            favoriteGenresPane.getChildren().add(noGenresLabel);
            return;
        }
        
        for (String genre : genres) {
            Label genreLabel = new Label(genre);
            genreLabel.setStyle(
                "-fx-background-color: #667eea; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 5 10; " +
                "-fx-background-radius: 15; " +
                "-fx-font-size: 12px;"
            );
            FlowPane.setMargin(genreLabel, new Insets(5));
            favoriteGenresPane.getChildren().add(genreLabel);
        }
    }
    
    private void displayReadBooks() {
        readBooksPane.getChildren().clear();
        
        List<String> readBookIds = currentUser.getReadBooks();
        if (readBookIds == null || readBookIds.isEmpty()) {
            showEmptyMessage(readBooksPane, "No books read yet");
            readBooksCountLabel.setText("0 books");
            return;
        }
        
        readBooksCountLabel.setText(readBookIds.size() + " books");
        
        for (String bookId : readBookIds) {
            Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                VBox bookCard = BookCardFactory.createSmallBookCard(book, () -> openBookDetails(book));
                readBooksPane.getChildren().add(bookCard);
            }
        }
    }
    
    private void displayLikedBooks() {
        likedBooksPane.getChildren().clear();
        
        List<String> likedBookIds = currentUser.getLikedBooks();
        if (likedBookIds == null || likedBookIds.isEmpty()) {
            showEmptyMessage(likedBooksPane, "No liked books yet");
            likedBooksCountLabel.setText("0 books");
            return;
        }
        
        likedBooksCountLabel.setText(likedBookIds.size() + " books");
        
        for (String bookId : likedBookIds) {
            Book book = bookDAO.getBookById(bookId);
            if (book != null) {
                VBox bookCard = BookCardFactory.createSmallBookCard(book, () -> openBookDetails(book));
                likedBooksPane.getChildren().add(bookCard);
            }
        }
    }
    
    private void showEmptyMessage(FlowPane pane, String message) {
        Label emptyLabel = new Label(message);
        emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-padding: 20;");
        pane.getChildren().add(emptyLabel);
    }
    
    private void openBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_details.fxml"));
            Parent root = loader.load();
            
            BookDetailsController controller = loader.getController();
            controller.setBook(book);
            controller.setUser(currentUser);
            
            Stage stage = new Stage();
            stage.setTitle(book.getTitle() + " - Details");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load book details.");
        }
    }
    
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_profile.fxml"));
            Parent root = loader.load();
            
            EditProfileController controller = loader.getController();
            controller.setUser(currentUser);
            controller.setProfileController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to open edit profile dialog.");
        }
    }
    
    public void refreshProfile() {
        // Reload user from database
        userDAO.findById(currentUser.getId().toHexString()).ifPresent(user -> {
            this.currentUser = user;
            loadUserProfile();
        });
    }
    
    @FXML
    private void handleExportHistory() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Reading History");
        fileChooser.setInitialFileName("reading_history_" + LocalDate.now() + ".json");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        
        Stage stage = (Stage) exportHistoryButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            exportToJson(file);
        }
    }
    
    private void exportToJson(File file) {
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            
            // User info
            json.append("  \"user\": {\n");
            json.append("    \"name\": \"").append(escapeJson(currentUser.getName())).append("\",\n");
            json.append("    \"email\": \"").append(escapeJson(currentUser.getEmail())).append("\",\n");
            json.append("    \"exportDate\": \"").append(LocalDate.now().toString()).append("\"\n");
            json.append("  },\n");
            
            // Read books
            json.append("  \"readBooks\": [\n");
            List<String> readBookIds = currentUser.getReadBooks();
            if (readBookIds != null && !readBookIds.isEmpty()) {
                for (int i = 0; i < readBookIds.size(); i++) {
                    Book book = bookDAO.getBookById(readBookIds.get(i));
                    if (book != null) {
                        json.append("    {\n");
                        json.append("      \"id\": \"").append(book.getId()).append("\",\n");
                        json.append("      \"title\": \"").append(escapeJson(book.getTitle())).append("\",\n");
                        json.append("      \"author\": \"").append(escapeJson(book.getAuthor())).append("\",\n");
                        json.append("      \"genres\": ").append(listToJson(book.getGenres())).append("\n");
                        json.append("    }");
                        if (i < readBookIds.size() - 1) json.append(",");
                        json.append("\n");
                    }
                }
            }
            json.append("  ],\n");
            
            // Ratings and reviews
            json.append("  \"ratings\": [\n");
            List<Rating> userRatings = ratingDAO.getRatingsByUserId(currentUser.getId().toHexString());
            if (userRatings != null && !userRatings.isEmpty()) {
                for (int i = 0; i < userRatings.size(); i++) {
                    Rating rating = userRatings.get(i);
                    Book book = bookDAO.getBookById(rating.getBookId());
                    if (book != null) {
                        json.append("    {\n");
                        json.append("      \"bookTitle\": \"").append(escapeJson(book.getTitle())).append("\",\n");
                        json.append("      \"bookAuthor\": \"").append(escapeJson(book.getAuthor())).append("\",\n");
                        json.append("      \"rating\": ").append(rating.getRating()).append(",\n");
                        json.append("      \"review\": ");
                        if (rating.getReviewText() != null) {
                            json.append("\"").append(escapeJson(rating.getReviewText())).append("\"");
                        } else {
                            json.append("null");
                        }
                        json.append(",\n");
                        json.append("      \"date\": ");
                        if (rating.getDate() != null) {
                            json.append("\"").append(rating.getDate().toString()).append("\"");
                        } else {
                            json.append("null");
                        }
                        json.append("\n");
                        json.append("    }");
                        if (i < userRatings.size() - 1) json.append(",");
                        json.append("\n");
                    }
                }
            }
            json.append("  ]\n");
            json.append("}\n");
            
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(json.toString());
            }
            
            showSuccess("Reading history exported successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to export reading history: " + e.getMessage());
        }
    }
    
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(escapeJson(list.get(i))).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setUser(currentUser);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Readium - Dashboard");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to navigate to dashboard.");
        }
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
