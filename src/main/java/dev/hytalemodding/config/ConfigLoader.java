package dev.hytalemodding.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConfigLoader {
    private static final String CONFIG_PATH = "mods/WelcomeWindow/config.json";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static WelcomeConfig loadConfig(Player player) {
        try {
            String pathStr = CONFIG_PATH;
            
            // Remove leading slash if present (Unix-style absolute path)
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }
            
            // Normalize path separators for current OS
            String normalizedPath = pathStr.replace("/", System.getProperty("file.separator"));
            
            // Resolve path relative to current working directory (server root)
            Path configPath = Paths.get(System.getProperty("user.dir")).resolve(normalizedPath);
            
            // Create directory structure if it doesn't exist
            Path configDir = configPath.getParent();
            if (configDir != null && !Files.exists(configDir)) {
                Files.createDirectories(configDir);
                if (player != null) {
                    player.sendMessage(Message.raw("[WelcomeWindow] Created config directory: " + configDir.toAbsolutePath()));
                }
            }
            
            // Create backup path
            Path backupPath = configPath.resolveSibling("config.json.bak");
            
            // Create config file with default values if it doesn't exist
            if (!Files.exists(configPath)) {
                WelcomeConfig defaultConfig = getDefaultConfig();
                String defaultJson = GSON.toJson(defaultConfig);
                
                // Create both config.json and config.json.bak with the same default content
                Files.writeString(configPath, defaultJson);
                Files.writeString(backupPath, defaultJson);
                
                if (player != null) {
                    player.sendMessage(Message.raw("[WelcomeWindow] Created default config file at: " + configPath.toAbsolutePath()));
                    player.sendMessage(Message.raw("[WelcomeWindow] Created backup config file at: " + backupPath.toAbsolutePath()));
                    player.sendMessage(Message.raw("[WelcomeWindow] You can now edit config.json and keep config.json.bak as backup."));
                }
                return defaultConfig;
            }

            // Load existing config file
            String jsonContent = Files.readString(configPath);
            
            // Always update backup file with default content from code (overwrite if exists)
            WelcomeConfig defaultConfig = getDefaultConfig();
            String defaultJson = GSON.toJson(defaultConfig);
            Files.writeString(backupPath, defaultJson);
            
            WelcomeConfig config = GSON.fromJson(jsonContent, WelcomeConfig.class);
            
            // Validate config
            if (config == null) {
                if (player != null) {
                    player.sendMessage(Message.raw("[WelcomeWindow] Config file is empty or invalid. Using defaults."));
                }
                return getDefaultConfig();
            }
            
            if (player != null) {
                player.sendMessage(Message.raw("[WelcomeWindow] Config loaded successfully from: " + configPath.toAbsolutePath()));
            }
            
            return config;
        } catch (IOException e) {
            if (player != null) {
                player.sendMessage(Message.raw("[WelcomeWindow] Error reading/writing config file: " + e.getMessage()));
            }
            return getDefaultConfig();
        } catch (Exception e) {
            if (player != null) {
                player.sendMessage(Message.raw("[WelcomeWindow] Error parsing config file: " + e.getMessage()));
            }
            return getDefaultConfig();
        }
    }

    private static WelcomeConfig getDefaultConfig() {
        WelcomeConfig defaultConfig = new WelcomeConfig();
        defaultConfig.setBackButtonText("Back");
        defaultConfig.setNextButtonText("Next");
        defaultConfig.setDoneButtonText("Finish");
        defaultConfig.setMenuWidth(150);
        defaultConfig.setContainerWidth(800);
        defaultConfig.setContainerHeight(500);
        defaultConfig.setFontSize(18);
        
        PageConfig page1 = new PageConfig();
        page1.setTitle("Welcome");
        page1.setButtonTitle("Welcome");
        page1.setParagraphs(List.of(
            "Here are some available commands:",
            "",
            "/help - shows all available commands",
            "/welcome - shows this welcome window"
        ));
        
        PageConfig page2 = new PageConfig();
        page2.setTitle("Dying");
        page2.setButtonTitle("Dying");
        page2.setParagraphs(List.of(
            "When you are killed you loose part of your equipments."
        ));
        
        defaultConfig.setPages(List.of(page1, page2));
        return defaultConfig;
    }
}
