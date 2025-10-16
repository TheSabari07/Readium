package bookrec.service;

import bookrec.model.User;

public class AuthService {
    
    private static User currentUser;
    
    public static void login(User user) {
        currentUser = user;
    }
    
    public static void logout() {
        currentUser = null;
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
}
