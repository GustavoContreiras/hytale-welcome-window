package dev.hytalemodding.events;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.HudBuilder;
import au.ellie.hyui.builders.PageBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.hytalemodding.config.ConfigLoader;
import dev.hytalemodding.config.PageConfig;
import dev.hytalemodding.config.WelcomeConfig;

import java.util.ArrayList;
import java.util.List;

public class WelcomeWindowEvent {

    public static void onPlayerReady(PlayerReadyEvent event) {
        Player player = event.getPlayer();
        // player.sendMessage(Message.raw("[WelcomeWindow] start"));

        // Show HUD question mark button
        // WelcomeWindowEvent.showHudButton(player);

        // Load configuration to check alwaysShow setting
        WelcomeConfig config = ConfigLoader.loadConfig(player);
        boolean alwaysShow = config.getAlwaysShow() != null ? config.getAlwaysShow() : true;

        // Show window if alwaysShow is true, or if it's the player's first spawn
        if (alwaysShow || player.isFirstSpawn()) {
            WelcomeWindowEvent.openWelcomeWindow(player);
        }
    }

    public static void openWelcomeWindow(Player player) {

        Ref<EntityStore> ref = player.getReference();

        if (ref == null) {
            return;
        }

        Store<EntityStore> store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        if (playerRef == null) {
            return;
        }

        // Load configuration from file
        WelcomeConfig config = ConfigLoader.loadConfig(player);
        
        if (config.getPages() == null || config.getPages().isEmpty()) {
            player.sendMessage(Message.raw("[WelcomeWindow] No pages configured. Using default."));
            return;
        }

        // Build pages from config
        List<PageBuilder> pages = buildPagesFromConfig(config, player, playerRef, store);

        if (pages.isEmpty()) {
            player.sendMessage(Message.raw("[WelcomeWindow] Failed to build pages."));
            return;
        }

        // Open the first page
        pages.get(0).open(playerRef, store);
    }

    private static List<PageBuilder> buildPagesFromConfig(WelcomeConfig config, Player player, PlayerRef playerRef, Store<EntityStore> store) {
        List<PageBuilder> pages = new ArrayList<>();
        List<PageConfig> pageConfigs = config.getPages();
        String backButtonText = config.getBackButtonText() != null ? config.getBackButtonText() : "Back";
        String nextButtonText = config.getNextButtonText() != null ? config.getNextButtonText() : "Next";
        String doneButtonText = config.getDoneButtonText() != null ? config.getDoneButtonText() : "Done";
        int menuWidth = config.getMenuWidth() != null ? config.getMenuWidth() : 150;
        int containerWidth = config.getContainerWidth() != null ? config.getContainerWidth() : 800;
        int containerHeight = config.getContainerHeight() != null ? config.getContainerHeight() : 500;
        int fontSize = config.getFontSize() != null ? config.getFontSize() : 20;

        List<String> pagesButtonTitles = new ArrayList<>();

        pageConfigs.forEach(pageConfig -> pagesButtonTitles.add(pageConfig.getButtonTitle()));

        // Build all pages
        for (int i = 0; i < pageConfigs.size(); i++) {
            PageConfig pageConfig = pageConfigs.get(i);
            String currentPageTitle = pageConfig.getTitle();
            List<String> pageParagraphs = pageConfig.getParagraphs();
            boolean isFirstPage = (i == 0);
            boolean isLastPage = (i == pageConfigs.size() - 1);

            // Build HTML content
            StringBuilder html = new StringBuilder();
            html.append("<div class=\"page-overlay\">\n");
            html.append("<div class=\"container\" data-hyui-title=\"" + currentPageTitle + "\"  style=\"anchor-width: " + containerWidth + "; anchor-height: " + containerHeight + ";\">\n");
            html.append("<div style=\"layout-mode: center; flex-weight: 1;\">\n");
            html.append("<div style=\"layout-mode: topscrolling; anchor-min-width: " + menuWidth + "; anchor-max-width: " + menuWidth + "; anchor-left: 0;\">\n");
            
            for (int j = 0; j < pagesButtonTitles.size(); j++) {
                String pageButtonTitle = pagesButtonTitles.get(j);
                html.append("<button id=\"Button"+j+"\" style=\"anchor-horizontal: 1; anchor-top: 4;\">" + pageButtonTitle + "</button>\n");
            }

            html.append("""
                    </div>
                    <div style="layout-mode: top; flex-weight: 1;">
                        <div style="layout-mode: topscrolling; flex-weight: 1; padding: 0;">
            """);

            for (int k = 0; k < pageParagraphs.size(); k++) {
                html.append("<p style=\"font-size: " + fontSize + ";\">" + pageParagraphs.get(k) + "</p>\n");
            }
  
            html.append("""
                            </div>
                        <div style="layout-mode: center; flex-weight: 0; anchor-bottom: 0;">
            """);

            if (!isFirstPage) {
                html.append("<button id=\"backBtn"+i+"\" style=\"anchor-horizontal:1; padding-right: 4;\">" + backButtonText + "</button>\n");
            }

            if (!isLastPage) {
                html.append("<button id=\"nextBtn"+i+"\" style=\"anchor-horizontal:1;\">" + nextButtonText + "</button>\n");
            } else {
                html.append("<button id=\"doneBtn"+i+"\" style=\"anchor-horizontal:1;\">" + doneButtonText + "</button>\n");
            }

            html.append("""
                        </div>
                    </div>
                </div>
            """);
            html.append("    </div>\n");

            if (isLastPage) {
                html.append("<button id=\"escBtn"+i+"\" class=\"back-button\"></button>\n");
            }
            
            html.append("</div>");

            // Create page with appropriate lifetime
            CustomPageLifetime lifetime = isLastPage
                ? CustomPageLifetime.CanDismissOrCloseThroughInteraction
                : CustomPageLifetime.CantClose;

            // System.out.println(html.toString());
            
            PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(lifetime)
                .fromHtml(html.toString());

            pages.add(page);
        }

        // Set up navigation between pages
        for (int i = 0; i < pages.size(); i++) {
            PageBuilder currentPage = pages.get(i);

            for (int j = 0; j < pages.size(); j++) {
                // Setup pages menu buttons
                PageBuilder page = pages.get(j);
                currentPage.getById("Button" + j, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        page.open(playerRef, store);
                    });
                });
            }

            // Set up next button
            if (i < pages.size() - 1) {
                PageBuilder nextPage = pages.get(i + 1);
                currentPage.getById("nextBtn" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        nextPage.open(playerRef, store);
                    });
                });
            }

            // Set up back button
            if (i > 0) {
                PageBuilder previousPage = pages.get(i - 1);
                currentPage.getById("backBtn" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        previousPage.open(playerRef, store);
                    });
                });
            }

            // Set up done button
            if (i == pages.size() - 1) {
                currentPage.getById("doneBtn" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        player.getPageManager().setPage(player.getReference(), store, Page.None);
                    });
                });
            }
        }

        return pages;
    }

    // public static void showHudButton(Player player) {
    //     Ref<EntityStore> ref = player.getReference();

    //     if (ref == null) {
    //         return;
    //     }

    //     Store<EntityStore> store = ref.getStore();
    //     PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

    //     if (playerRef == null) {
    //         return;
    //     }

    //     // Create HUD button in top right corner
    //     String hudHtml = """
    //         <div style="anchor-top: 10; anchor-horizontal: 1; padding-right: 10;">
    //             <button id="helpBtn" style="font-size: 24; padding: 8; min-width: 32; min-height: 32;">?</button>
    //         </div>
    //         """;

    //     // Create HUD with click event listener to open WelcomeWindow
    //     HudBuilder.hudForPlayer(playerRef)
    //         .fromHtml(hudHtml)
    //         .addEventListener("helpBtn", CustomUIEventBindingType.Activating, ctx -> {
    //             WelcomeWindowEvent.openWelcomeWindow(player);
    //         })
    //         .show(store);
    // }
}