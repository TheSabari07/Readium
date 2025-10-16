package bookrec.model;

public class Settings {
    private String theme; // "Light" or "Dark"
    
    public Settings() {
        this.theme = "Light"; // Default theme
    }
    
    public Settings(String theme) {
        this.theme = theme;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public boolean isDarkMode() {
        return "Dark".equalsIgnoreCase(theme);
    }
}
