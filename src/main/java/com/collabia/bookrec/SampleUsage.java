package com.collabia.bookrec;

import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.db.MongoDBConnection;
import com.collabia.bookrec.exceptions.UnauthorizedException;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.service.AuthService;
import com.mongodb.client.MongoDatabase;

public class SampleUsage {

    public static void main(String[] args) {
        // NOTE: Ensure MongoDB is running.
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            db.getCollection("users").drop(); // Clean up for a fresh run
        } catch (Exception e) {
            System.err.println("Could not connect to MongoDB. Please ensure it's running.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        AuthService authService = new AuthService(userDAO);

        // 1. Register a new user
        System.out.println("--- Registering User ---");
        try {
            authService.register("Jane Doe", "jane.doe@example.com", "securePassword123");
            System.out.println("Registration successful for jane.doe@example.com");
        } catch (IllegalArgumentException e) {
            System.err.println("Registration failed: " + e.getMessage());
        }

        // 2. Log in with the registered user
        System.out.println("\n--- Logging In ---");
        try {
            User loggedInUser = authService.login("jane.doe@example.com", "securePassword123");
            System.out.println("Login successful. Welcome, " + loggedInUser.getName());
        } catch (UnauthorizedException e) {
            System.err.println("Login failed: " + e.getMessage());
        }

        // 3. Access the current user from the in-memory session
        System.out.println("\n--- Checking Session ---");
        User currentUser = AuthService.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Current user from session: " + currentUser.getName());
        } else {
            System.out.println("No user is currently logged in.");
        }

        // Close MongoDB connection
        MongoDBConnection.close();
    }
}
