package dev.hytalemodding.commands;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

/**
 * Example command demonstrating HyUI usage with HYUIML (HTML-like syntax).
 *
 * Usage: /ui
 *
 * HyUI Components:
 * - PageBuilder: Full-screen UIs
 * - HudBuilder: Persistent on-screen elements (HUDs)
 * - ButtonBuilder, LabelBuilder, ImageBuilder, etc.
 *
 * See docs/HyUI - Hytale Mods - CurseForge.html for full documentation.
 */
public class UIExampleCommand extends AbstractAsyncCommand {

    private final PageBuilder page;

    public UIExampleCommand() {
        super("ui", "Opens an example UI window");
        this.setPermissionGroup(GameMode.Adventure); // Anyone can use this command

        // Pre-build the UI using HYUIML (HTML-like syntax)
        // Classes like "page-overlay" and "container" are built-in HyUI styles
        page = PageBuilder.detachedPage()
                .withLifetime(CustomPageLifetime.CanDismiss)
                .fromHtml("""
                        <div class="page-overlay">
                            <div class="container" data-hyui-title="Example Menu">
                                <div class="container-contents">
                                    <p>Welcome to the UI!</p>
                                    <button id="greetBtn">Say Hello</button>
                                    <button id="closeBtn">Close</button>
                                </div>
                            </div>
                        </div>
                        """);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        var sender = context.sender();
        if (!(sender instanceof Player player)) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        Ref<EntityStore> ref = player.getReference();
        if (ref == null || !ref.isValid()) {
            context.sendMessage(Message.raw("Player not in world."));
            return CompletableFuture.completedFuture(null);
        }

        Store<EntityStore> store = ref.getStore();
        World world = store.getExternalData().getWorld();

        return CompletableFuture.runAsync(() -> {
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef == null) return;

            // Add event listeners for this specific player
            page.getById("greetBtn", ButtonBuilder.class).ifPresent(button -> {
                button.addEventListener(CustomUIEventBindingType.Activating, event -> {
                    context.sendMessage(Message.raw("Hello from HyUI!"));
                });
            });

            page.getById("closeBtn", ButtonBuilder.class).ifPresent(button -> {
                button.addEventListener(CustomUIEventBindingType.Activating, event -> {
                    context.sendMessage(Message.raw("Press ESC to close the menu."));
                });
            });

            // Open the page for this player
            page.open(playerRef, store);
        }, world);
    }
}
