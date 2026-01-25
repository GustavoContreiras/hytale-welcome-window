package dev.hytalemodding.commands;

import dev.hytalemodding.events.WelcomeWindowEvent;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import java.util.concurrent.CompletableFuture;

public class WelcomeWindowCommand extends AbstractAsyncCommand {

    public WelcomeWindowCommand(String name, String description) {
        super(name, description);
        this.setPermissionGroup(GameMode.Adventure); // Anyone can use this command
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
            WelcomeWindowEvent.openWelcomeWindow(player);
        }, world);
    }
}
