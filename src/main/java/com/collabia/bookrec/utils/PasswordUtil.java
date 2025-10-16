package com.collabia.bookrec.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param plainTextPassword The password to hash.
     * @return The hashed password.
     * @throws IllegalArgumentException if the password is null or empty.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainTextPassword The plain text password to verify.
     * @param hashedPassword    The hashed password to compare against.
     * @return true if the password matches the hash, false otherwise.
     * @throws IllegalArgumentException if either password is null or empty.
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain text password cannot be null or empty.");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty.");
        }
        
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // BCrypt.checkpw throws IllegalArgumentException for invalid hash format.
            // In this case, the password does not match.
            return false;
        }
    }
}
