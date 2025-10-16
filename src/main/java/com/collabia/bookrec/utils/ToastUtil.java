package com.collabia.bookrec.utils;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class ToastUtil {
    
    public static void showSuccess(Window owner, String message) {
        showToast(owner, message, "-fx-background-color: #4CAF50; -fx-text-fill: white;");
    }
    
    public static void showError(Window owner, String message) {
        showToast(owner, message, "-fx-background-color: #f44336; -fx-text-fill: white;");
    }
    
    public static void showInfo(Window owner, String message) {
        showToast(owner, message, "-fx-background-color: #2196F3; -fx-text-fill: white;");
    }
    
    private static void showToast(Window owner, String message, String style) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        
        Label toastLabel = new Label(message);
        toastLabel.setStyle(style + " -fx-padding: 10 20; -fx-background-radius: 5; " +
                          "-fx-font-size: 14px;");
        
        StackPane root = new StackPane(toastLabel);
        root.setStyle("-fx-background-color: transparent;");
        root.setAlignment(Pos.BOTTOM_CENTER);
        
        Scene scene = new Scene(root);
        scene.setFill(null);
        toastStage.setScene(scene);
        
        // Position toast at bottom center
        toastStage.setX(owner.getX() + owner.getWidth() / 2 - 150);
        toastStage.setY(owner.getY() + owner.getHeight() - 100);
        
        toastStage.show();
        
        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(e -> toastStage.close());
        fadeOut.play();
    }
}
