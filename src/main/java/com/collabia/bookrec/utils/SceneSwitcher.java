package com.collabia.bookrec.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneSwitcher {
    
    private static final Logger LOGGER = Logger.getLogger(SceneSwitcher.class.getName());
    private static final Map<String, FXMLLoader> loaderCache = new HashMap<>();
    private static final String STYLESHEET_PATH = "/com/collabia/bookrec/styles/application.css";
    private static final double TRANSITION_DURATION = 300.0; // milliseconds

    private SceneSwitcher() {
        // Private constructor to prevent instantiation
    }

    /**
     * Switches to a new scene with fade transition animation.
     *
     * @param stage The stage to switch scenes on
     * @param fxmlPath The path to the FXML file (relative to resources)
     * @param controllerData Optional data to pass to the controller (can be null)
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void switchTo(Stage stage, String fxmlPath, Object controllerData) throws IOException {
        FXMLLoader loader = getLoader(fxmlPath);
        Parent root = loader.load();
        
        // Pass data to controller if it implements DataReceiver
        if (controllerData != null) {
            Object controller = loader.getController();
            if (controller instanceof DataReceiver) {
                ((DataReceiver) controller).receiveData(controllerData);
            }
        }
        
        Scene newScene = new Scene(root);
        
        // Apply stylesheet
        applyStylesheet(newScene);
        
        // Apply fade transition
        applyFadeTransition(stage, newScene);
    }

    /**
     * Switches to a new scene without passing data.
     *
     * @param stage The stage to switch scenes on
     * @param fxmlPath The path to the FXML file (relative to resources)
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void switchTo(Stage stage, String fxmlPath) throws IOException {
        switchTo(stage, fxmlPath, null);
    }

    /**
     * Gets a cached FXMLLoader or creates a new one.
     *
     * @param fxmlPath The path to the FXML file
     * @return FXMLLoader instance
     */
    private static FXMLLoader getLoader(String fxmlPath) {
        // Create new loader each time to avoid controller reuse issues
        // but keep the pattern for potential future optimization
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        return loader;
    }

    /**
     * Applies the application stylesheet to the scene.
     *
     * @param scene The scene to apply the stylesheet to
     */
    private static void applyStylesheet(Scene scene) {
        try {
            String stylesheet = SceneSwitcher.class.getResource(STYLESHEET_PATH).toExternalForm();
            scene.getStylesheets().add(stylesheet);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load stylesheet: " + STYLESHEET_PATH, e);
        }
    }

    /**
     * Applies a fade transition when switching scenes.
     *
     * @param stage The stage to apply the transition to
     * @param newScene The new scene to transition to
     */
    private static void applyFadeTransition(Stage stage, Scene newScene) {
        Scene currentScene = stage.getScene();
        
        if (currentScene != null && currentScene.getRoot() != null) {
            // Fade out current scene
            FadeTransition fadeOut = new FadeTransition(Duration.millis(TRANSITION_DURATION), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                // Set new scene
                stage.setScene(newScene);
                
                // Fade in new scene
                Parent newRoot = newScene.getRoot();
                newRoot.setOpacity(0.0);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(TRANSITION_DURATION), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            // No current scene, just set the new one
            stage.setScene(newScene);
        }
    }

    /**
     * Clears the FXML loader cache.
     * Useful for freeing memory or forcing reload of FXML files.
     */
    public static void clearCache() {
        loaderCache.clear();
    }

    /**
     * Pre-loads an FXML file into the cache.
     *
     * @param fxmlPath The path to the FXML file to cache
     */
    public static void preloadScene(String fxmlPath) {
        try {
            getLoader(fxmlPath);
            LOGGER.info("Preloaded scene: " + fxmlPath);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to preload scene: " + fxmlPath, e);
        }
    }
}
