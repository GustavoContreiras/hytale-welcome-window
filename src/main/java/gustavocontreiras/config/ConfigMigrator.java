package gustavocontreiras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Migrates old config files (camelCase keys) to new format (PascalCase keys).
 * This ensures backwards compatibility when upgrading from older versions.
 */
public class ConfigMigrator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Mapping of old camelCase keys to new PascalCase keys
    private static final Map<String, String> KEY_MIGRATIONS = Map.ofEntries(
            // WelcomeConfig keys
            Map.entry("backButtonText", "BackButtonText"),
            Map.entry("nextButtonText", "NextButtonText"),
            Map.entry("doneButtonText", "DoneButtonText"),
            Map.entry("menuWidth", "MenuWidth"),
            Map.entry("containerWidth", "ContainerWidth"),
            Map.entry("containerHeight", "ContainerHeight"),
            Map.entry("fontSize", "FontSize"),
            Map.entry("alwaysShow", "AlwaysShow"),
            Map.entry("debug", "Debug"),
            Map.entry("pages", "Pages"),
            // PageConfig keys
            Map.entry("title", "Title"),
            Map.entry("buttonTitle", "ButtonTitle"),
            Map.entry("paragraphs", "Paragraphs")
    );

    /**
     * Migrates the config file at the given path if it contains old-style keys.
     * Should be called before the config is loaded by the framework.
     *
     * @param dataDirectory The plugin's data directory
     */
    public static void migrateIfNeeded(Path dataDirectory) {
        // New config location (used by the framework) - e.g., WelcomeWindow/config.json
        Path configPath = dataDirectory.resolve("config.json");

        // Old config location (used by old ConfigLoader) - e.g., mods/WelcomeWindow/config.json
        Path oldConfigPath = Path.of("mods", "WelcomeWindow", "config.json");

        // Find which config file to migrate (prefer old location if it exists)
        Path pathToMigrate = null;
        if (Files.exists(oldConfigPath)) {
            pathToMigrate = oldConfigPath;
        } else if (Files.exists(configPath)) {
            pathToMigrate = configPath;
        }

        if (pathToMigrate == null) {
            return;
        }

        try {
            String content = Files.readString(pathToMigrate);
            JsonObject root = GSON.fromJson(content, JsonObject.class);

            if (root == null) {
                return;
            }

            // Check if migration is needed (look for any old-style key)
            if (!needsMigration(root)) {
                return;
            }

            // Migrate the config
            JsonObject migrated = migrateObject(root);
            String migratedContent = GSON.toJson(migrated);

            // Write to the NEW location (dataDirectory)
            Files.createDirectories(dataDirectory);
            Files.writeString(configPath, migratedContent);

            // Delete old config if it was in a different location
            if (!pathToMigrate.toAbsolutePath().normalize().equals(configPath.toAbsolutePath().normalize())) {
                Files.deleteIfExists(pathToMigrate);
            }

        } catch (Exception e) {
            System.err.println("[WelcomeWindow] Config migration failed: " + e.getMessage());
        }
    }

    private static boolean needsMigration(JsonObject obj) {
        for (String key : obj.keySet()) {
            if (KEY_MIGRATIONS.containsKey(key)) {
                return true;
            }
            // Check nested objects (like pages array)
            JsonElement element = obj.get(key);
            if (element.isJsonArray()) {
                for (JsonElement item : element.getAsJsonArray()) {
                    if (item.isJsonObject() && needsMigration(item.getAsJsonObject())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a .bak file with default config values for reference.
     * Always regenerates to ensure it reflects the current defaults.
     *
     * @param dataDirectory The plugin's data directory
     */
    public static void createBackupWithDefaults(Path dataDirectory) {
        Path bakPath = dataDirectory.resolve("config.json.bak");

        try {
            Files.createDirectories(dataDirectory);

            // Get defaults with sample pages from WelcomeConfig
            WelcomeConfig defaults = WelcomeConfig.createWithSamplePages();
            JsonObject root = new JsonObject();

            root.addProperty("BackButtonText", defaults.getBackButtonText());
            root.addProperty("NextButtonText", defaults.getNextButtonText());
            root.addProperty("DoneButtonText", defaults.getDoneButtonText());
            root.addProperty("MenuWidth", defaults.getMenuWidth());
            root.addProperty("ContainerWidth", defaults.getContainerWidth());
            root.addProperty("ContainerHeight", defaults.getContainerHeight());
            root.addProperty("FontSize", defaults.getFontSize());
            root.addProperty("AlwaysShow", defaults.getAlwaysShow());
            root.addProperty("Debug", defaults.getDebug());

            // Pages from defaults
            JsonArray pages = new JsonArray();
            for (PageConfig page : defaults.getPages()) {
                JsonObject pageObj = new JsonObject();
                pageObj.addProperty("Title", page.getTitle());
                pageObj.addProperty("ButtonTitle", page.getButtonTitle());
                JsonArray paragraphs = new JsonArray();
                for (String p : page.getParagraphs()) {
                    paragraphs.add(p);
                }
                pageObj.add("Paragraphs", paragraphs);
                pages.add(pageObj);
            }
            root.add("Pages", pages);

            String content = GSON.toJson(root);
            Files.writeString(bakPath, content);

        } catch (Exception e) {
            System.err.println("[WelcomeWindow] Failed to create backup config: " + e.getMessage());
        }
    }

    private static JsonObject migrateObject(JsonObject original) {
        JsonObject migrated = new JsonObject();

        for (String key : original.keySet()) {
            String newKey = KEY_MIGRATIONS.getOrDefault(key, key);
            JsonElement value = original.get(key);

            if (value.isJsonArray()) {
                JsonArray migratedArray = new JsonArray();
                for (JsonElement item : value.getAsJsonArray()) {
                    if (item.isJsonObject()) {
                        migratedArray.add(migrateObject(item.getAsJsonObject()));
                    } else {
                        migratedArray.add(item);
                    }
                }
                migrated.add(newKey, migratedArray);
            } else if (value.isJsonObject()) {
                migrated.add(newKey, migrateObject(value.getAsJsonObject()));
            } else {
                migrated.add(newKey, value);
            }
        }

        return migrated;
    }
}
