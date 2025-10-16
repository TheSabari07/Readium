package bookrec.dao;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bookrec.model.Settings;

public class SettingsDAO {
    private static final String SETTINGS_DIR = System.getProperty("user.home") + "/.readium";
    private static final String SETTINGS_FILE = SETTINGS_DIR + "/settings.json";
    private final Gson gson;
    
    public SettingsDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ensureSettingsDirectoryExists();
    }
    
    private void ensureSettingsDirectoryExists() {
        File dir = new File(SETTINGS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public Settings loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) {
            return new Settings(); // Return default settings
        }
        
        try (FileReader reader = new FileReader(file)) {
            Settings settings = gson.fromJson(reader, Settings.class);
            return settings != null ? settings : new Settings();
        } catch (IOException e) {
            e.printStackTrace();
            return new Settings();
        }
    }
    
    public void saveSettings(Settings settings) {
        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
