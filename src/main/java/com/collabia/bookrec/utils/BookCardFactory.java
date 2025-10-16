package com.collabia.bookrec.utils;

import com.collabia.bookrec.model.Book;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BookCardFactory {
    
    private static final String DEFAULT_COVER = "/images/default-book-cover.png";
    
    public static VBox createBookCard(Book book, Runnable onClickAction) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        card.setPrefWidth(180);
        card.setMaxWidth(180);
        card.setCursor(Cursor.HAND);
        
        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(5);
        card.setEffect(shadow);
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 8;");
            DropShadow hoverShadow = new DropShadow();
            hoverShadow.setColor(Color.rgb(0, 0, 0, 0.2));
            hoverShadow.setRadius(10);
            card.setEffect(hoverShadow);
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            card.setEffect(shadow);
        });
        
        // Click action
        card.setOnMouseClicked(e -> {
            if (onClickAction != null) {
                onClickAction.run();
            }
        });
        
        // Book cover image - use getCoverPath() or getCoverImageUrl()
        String coverUrl = getCoverUrl(book);
        ImageView coverImage = createCoverImage(coverUrl);
        
        // Book title (truncate if too long)
        Label titleLabel = new Label(truncateText(book.getTitle(), 25));
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(160);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Book author
        Label authorLabel = new Label(truncateText(book.getAuthor(), 30));
        authorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        authorLabel.setWrapText(true);
        authorLabel.setMaxWidth(160);
        authorLabel.setAlignment(Pos.CENTER);
        
        // Rating display
        Label ratingLabel = createRatingLabel(book.getAverageRating());
        
        card.getChildren().addAll(coverImage, titleLabel, authorLabel, ratingLabel);
        
        return card;
    }
    
    private static String getCoverUrl(Book book) {
        // Try getCoverImageUrl first, fallback to getCoverPath
        try {
            String url = book.getCoverImageUrl();
            if (url != null && !url.isEmpty()) {
                return url;
            }
        } catch (Exception e) {
            // Method doesn't exist, try coverPath
        }
        
        try {
            String path = book.getCoverPath();
            if (path != null && !path.isEmpty()) {
                return path;
            }
        } catch (Exception e) {
            // Neither method exists
        }
        
        return null;
    }
    
    private static ImageView createCoverImage(String coverUrl) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(140);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 5;");
        
        try {
            if (coverUrl != null && !coverUrl.isEmpty()) {
                Image image = new Image(coverUrl, true);
                imageView.setImage(image);
            } else {
                // Use default cover
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            // Fallback to default if image fails to load
            setDefaultImage(imageView);
        }
        
        return imageView;
    }
    
    private static void setDefaultImage(ImageView imageView) {
        try {
            Image defaultImage = new Image(BookCardFactory.class.getResourceAsStream(DEFAULT_COVER));
            imageView.setImage(defaultImage);
        } catch (Exception ex) {
            // Create a placeholder rectangle
            imageView.setStyle("-fx-background-color: #e0e0e0;");
        }
    }
    
    private static Label createRatingLabel(double rating) {
        Label ratingLabel = new Label();
        
        if (rating > 0) {
            // Create star rating display
            StringBuilder stars = new StringBuilder();
            int fullStars = (int) rating;
            boolean hasHalfStar = (rating - fullStars) >= 0.5;
            
            for (int i = 0; i < fullStars && i < 5; i++) {
                stars.append("★");
            }
            if (hasHalfStar && fullStars < 5) {
                stars.append("½");
            }
            
            int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
            for (int i = 0; i < emptyStars; i++) {
                stars.append("☆");
            }
            
            ratingLabel.setText(stars.toString() + " " + String.format("%.1f", rating));
            ratingLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #FFA500;");
        } else {
            ratingLabel.setText("No ratings yet");
            ratingLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #999;");
        }
        
        return ratingLabel;
    }
    
    private static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
