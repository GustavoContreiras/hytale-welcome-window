package dev.hytalemodding.events;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class WelcomeWindowEvent {

    private static PageBuilder welcomePage;
    private static PageBuilder levelPage;

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Message.raw("[WelcomeWindow] start"));

        Ref<EntityStore> ref = player.getReference();

        if (ref == null) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef == null) {
            return;
        }

        // Classes like "page-overlay" and "container" are built-in HyUI styles
        welcomePage = PageBuilder.detachedPage()
            .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
            .fromHtml("""
                <div class="page-overlay">
                    <div class="container" data-hyui-title="Bem-vindo ao HyTibia I">
                        <div style="layout-mode: top; flex-weight:1;">
                            <p>Aqui estão alguns comandos disponíveis:</p>
                            <p>/help - exibe todos comandos disponíveis</p>
                            <p>/modlist - exibe todos mods instalados</p>
                            <p>/lvl gui - painel para atribuir pontos de level</p>
                            <p>/hidearmor - esconda seus equipamentos na sua skin</p>
                        </div>
                        <div style="layout-mode: center;">
                            <button id="nextBtn" style="anchor-horizontal:1;">Próximo</button>
                        </div>
                    </div>
                </div>
            """);

        levelPage = PageBuilder.detachedPage()
            .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
            .fromHtml("""
                <div class="page-overlay">
                    <div class="container" data-hyui-title="Level">
                        <div style="layout-mode: top; flex-weight:1;">
                            <p>Ao matar criaturas, você ganha experiência.</p>
                        </div>
                        <div style="layout-mode: center;">
                            <button id="backBtn" style="anchor-horizontal:1; padding-right: 4;">Voltar</button>
                            <button id="nextBtn" style="anchor-horizontal:1; padding-left: 4;">Próximo</button>
                        </div>
                    </div>
                    <button id="closeBtn" class="back-button"></button>
                </div>
            """);

        // Add event listeners for this specific player
        welcomePage.getById("nextBtn", ButtonBuilder.class).ifPresent(button -> {
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                levelPage.open(playerRef, store);
            });
        });

        levelPage.getById("backBtn", ButtonBuilder.class).ifPresent(button -> {
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                welcomePage.open(playerRef, store);
            });
        });

        // Open the page for this player
        welcomePage.open(playerRef, store);
    }

}