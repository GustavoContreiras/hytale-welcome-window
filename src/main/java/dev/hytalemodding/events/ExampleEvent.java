package dev.hytalemodding.events;

import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;

public class ExampleEvent {

    private static PageBuilder page;

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("Welcome " + player.getDisplayName()));

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

}