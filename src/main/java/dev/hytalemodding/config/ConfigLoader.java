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

    /**
     * Checks if debug mode is enabled by reading the existing config file.
     * Returns false if config doesn't exist or debug property is not set.
     */
    private static boolean isDebugEnabled(Path configPath) {
        try {
            if (Files.exists(configPath)) {
                String jsonContent = Files.readString(configPath);
                WelcomeConfig config = GSON.fromJson(jsonContent, WelcomeConfig.class);
                return config != null && Boolean.TRUE.equals(config.getDebug());
            }
        } catch (Exception e) {
            // Ignore errors, just return false
        }
        return false;
    }

    private static void debugMessage(Player player, boolean debug, String message) {
        if (debug && player != null) {
            player.sendMessage(Message.raw("[WelcomeWindow] " + message));
        }
    }

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

            // Check if debug mode is enabled (before we start loading)
            boolean debug = isDebugEnabled(configPath);

            // Create directory structure if it doesn't exist
            Path configDir = configPath.getParent();
            if (configDir != null && !Files.exists(configDir)) {
                Files.createDirectories(configDir);
                debugMessage(player, debug, "Created config directory: " + configDir.toAbsolutePath());
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

                debugMessage(player, debug, "Created default config file at: " + configPath.toAbsolutePath());
                debugMessage(player, debug, "Created backup config file at: " + backupPath.toAbsolutePath());
                debugMessage(player, debug, "You can now edit config.json and keep config.json.bak as backup.");
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
                debugMessage(player, debug, "Config file is empty or invalid. Using defaults.");
                return getDefaultConfig();
            }

            debugMessage(player, debug, "Config loaded successfully from: " + configPath.toAbsolutePath());

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
        defaultConfig.setAlwaysShow(true);
        defaultConfig.setDebug(false);

        PageConfig page1 = new PageConfig();
        page1.setTitle("Welcome to Hytale");
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
