package com.collabia.bookrec.service;

import java.util.ArrayList;
import java.util.Optional;

import com.collabia.bookrec.dao.UserDAO;
import com.collabia.bookrec.exceptions.UnauthorizedException;
import com.collabia.bookrec.model.User;
import com.collabia.bookrec.utils.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO;
    private static User currentUser;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user.
     *
     * @param name     The user's name.
     * @param email    The user's email.
     * @param password The user's plain text password.
     * @return The newly created User.
     * @throws IllegalArgumentException if the email already exists.
     */
    public User register(String name, String email, String password) {
        if (userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists.");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(name, email, hashedPassword, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        userDAO.createUser(newUser);
        return newUser;
    }

    /**
     * Logs in a user.
     *
     * @param email    The user's email.
     * @param password The user's plain text password.
     * @return The logged-in User.
     * @throws UnauthorizedException if login fails.
     */
    public User login(String email, String password) {
        Optional<User> userOptional = userDAO.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        
        setCurrentUser(user);
        return user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        AuthService.currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        AuthService.currentUser = currentUser;
    }
}
