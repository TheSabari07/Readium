package com.collabia.bookrec.controller;

import java.io.IOException;
import java.util.List;

import com.collabia.bookrec.dao.BookDAO;
import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.model.Book;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.service.AuthService;
import com.collabia.bookrec.service.RecommendationEngine;
import com.collabia.bookrec.utils.BookCardFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    
    @FXML
    private Label sectionTitleLabel;
    
    @FXML
    private ProgressIndicator loadingIndicator;

    private User currentUser;
    private AuthService authService;
    private RecommendationEngine recommendationEngine;
    private BookDAO bookDAO;
    private List<Book> currentBooks;

    public DashboardController() {
        UserDAO userDAO = new UserDAO();
        this.authService = new AuthService(userDAO);
        this.bookDAO = new BookDAO();
        this.recommendationEngine = new RecommendationEngine(bookDAO);
    }

    @FXML
    public void initialize() {
        // Setup search field listener for live filtering
        setupSearchListener();
        
        // Hide loading indicator initially
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
    }
    
    private void setupSearchListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                // If search is cleared, show recommendations again
                loadRecommendedBooks();
            } else {
                // Perform live search
                performSearch(newValue.trim());
            }
        });
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateWelcomeMessage();
        loadRecommendedBooks();
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome back, " + currentUser.getName() + "!");
        }
    }

    private void loadRecommendedBooks() {
        if (currentUser == null) return;
        
        sectionTitleLabel.setText("Recommended for You");
        showLoading(true);
        
        // Run recommendation in background to avoid UI freeze
        new Thread(() -> {
            try {
                // Get 12 recommended books for the user
                List<Book> recommendedBooks = recommendationEngine.recommendForUser(currentUser, 12);
                
                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    currentBooks = recommendedBooks;
                    displayBooks(recommendedBooks);
                    showLoading(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load recommendations");
                    showLoading(false);
                });
                e.printStackTrace();
            }
        }).start();
    }
    
    private void performSearch(String query) {
        sectionTitleLabel.setText("Search Results for \"" + query + "\"");
        showLoading(true);
        
        new Thread(() -> {
            try {
                // Search books by title or author
                List<Book> searchResults = bookDAO.searchByTitleOrAuthor(query);
                
                Platform.runLater(() -> {
                    currentBooks = searchResults;
                    displayBooks(searchResults);
                    showLoading(false);
                    
                    if (searchResults.isEmpty()) {
                        showNoResultsMessage();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Search failed. Please try again.");
                    showLoading(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void displayBooks(List<Book> books) {
        // Clear existing content
        booksGridPane.getChildren().clear();
        
        if (books == null || books.isEmpty()) {
            showNoResultsMessage();
            return;
        }
        
        // Configure grid
        int columns = 4;
        int row = 0;
        int col = 0;
        
        for (Book book : books) {
            // Create book card
            VBox bookCard = createBookCard(book);
            
            // Add to grid
            booksGridPane.add(bookCard, col, row);
            GridPane.setMargin(bookCard, new Insets(10));
            
            // Update position
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createBookCard(Book book) {
        return BookCardFactory.createBookCard(book, () -> openBookDetails(book));
    }
    
    private void openBookDetails(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/book_details.fxml"));
            Parent root = loader.load();
            
            // Pass book to BookDetailsController
            BookDetailsController controller = loader.getController();
            controller.setBook(book);
            controller.setUser(currentUser);
            
            // Open in new window or same window based on preference
            Stage stage = new Stage();
            stage.setTitle(book.getTitle() + " - Details");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load book details.");
        }
    }
    
    private void showNoResultsMessage() {
        booksGridPane.getChildren().clear();
        
        VBox noResultsBox = new VBox(10);
        noResultsBox.setAlignment(Pos.CENTER);
        noResultsBox.setPadding(new Insets(40));
        
        Label noResultsLabel = new Label("No books found");
        noResultsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
        
        Label suggestionLabel = new Label("Try a different search term");
        suggestionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
        
        noResultsBox.getChildren().addAll(noResultsLabel, suggestionLabel);
        booksGridPane.add(noResultsBox, 0, 0, 4, 1);
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
        booksGridPane.setDisable(show);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        // Navigate to profile view
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Readium - Profile");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load profile.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Confirm logout
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login page.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                navigateToLogin();
            }
        });
    }
    
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Readium - Login");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to logout.");
        }
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        // Refresh dashboard
        searchField.clear();
        loadRecommendedBooks();
    }

    @FXML
    private void handleRecommendations(ActionEvent event) {
        // Show recommendations (same as dashboard)
        handleDashboard(event);
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        // Navigate to settings
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) settingsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Readium - Settings");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load settings.");
        }
    }
}
