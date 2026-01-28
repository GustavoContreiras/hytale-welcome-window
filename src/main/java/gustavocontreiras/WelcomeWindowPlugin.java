package gustavocontreiras;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import gustavocontreiras.commands.WelcomeWindowCommand;
import gustavocontreiras.config.ConfigMigrator;
import gustavocontreiras.config.WelcomeConfig;
import gustavocontreiras.events.WelcomeWindowEvent;

import javax.annotation.Nonnull;

public class WelcomeWindowPlugin extends JavaPlugin {

    private static WelcomeWindowPlugin instance;
    private final Config<WelcomeConfig> config;

    public WelcomeWindowPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        // Migrate old config format before loading
        ConfigMigrator.migrateIfNeeded(this.getDataDirectory());

        // Now register the config (will be loaded in preLoad phase)
        this.config = this.withConfig("config", WelcomeConfig.CODEC);
    }

    @Override
    protected void setup() {
        // If config has no pages (first run), populate with sample pages
        if (config.get().getPages().isEmpty()) {
            WelcomeConfig defaults = WelcomeConfig.createWithSamplePages();
            config.get().setBackButtonText(defaults.getBackButtonText());
            config.get().setNextButtonText(defaults.getNextButtonText());
            config.get().setDoneButtonText(defaults.getDoneButtonText());
            config.get().setMenuWidth(defaults.getMenuWidth());
            config.get().setContainerWidth(defaults.getContainerWidth());
            config.get().setContainerHeight(defaults.getContainerHeight());
            config.get().setFontSize(defaults.getFontSize());
            config.get().setAlwaysShow(defaults.getAlwaysShow());
            config.get().setDebug(defaults.getDebug());
            config.get().setPages(defaults.getPages());
        }
        config.save();
        ConfigMigrator.createBackupWithDefaults(this.getDataDirectory());
        this.getCommandRegistry().registerCommand(new WelcomeWindowCommand("welcome", "Opens the welcome window"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, WelcomeWindowEvent::onPlayerReady);
    }

    public static WelcomeWindowPlugin getInstance() {
        return instance;
    }

    public Config<WelcomeConfig> getWelcomeConfig() {
        return config;
    }
}