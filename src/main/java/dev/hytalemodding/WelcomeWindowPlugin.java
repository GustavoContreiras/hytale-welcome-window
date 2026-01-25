package dev.hytalemodding;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.hytalemodding.commands.WelcomeWindowCommand;
import dev.hytalemodding.events.WelcomeWindowEvent;

import javax.annotation.Nonnull;

public class WelcomeWindowPlugin extends JavaPlugin {

    public WelcomeWindowPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new WelcomeWindowCommand("welcome", "Opens the welcome window"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, WelcomeWindowEvent::onPlayerReady);
    }
}