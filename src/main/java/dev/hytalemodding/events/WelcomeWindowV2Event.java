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
import dev.hytalemodding.config.ConfigLoader;
import dev.hytalemodding.config.PageConfig;
import dev.hytalemodding.config.WelcomeConfig;

import java.util.ArrayList;
import java.util.List;

public class WelcomeWindowV2Event {

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

        // Load configuration from file
        WelcomeConfig config = ConfigLoader.loadConfig(player);
        
        if (config.getPages() == null || config.getPages().isEmpty()) {
            player.sendMessage(Message.raw("[WelcomeWindow] No pages configured. Using default."));
            return;
        }

        // Build pages from config
        List<PageBuilder> pages = buildPagesFromConfig(config, playerRef, store);

        if (pages.isEmpty()) {
            player.sendMessage(Message.raw("[WelcomeWindow] Failed to build pages."));
            return;
        }

        // Open the first page
        pages.get(0).open(playerRef, store);
    }

    private static List<PageBuilder> buildPagesFromConfig(WelcomeConfig config, PlayerRef playerRef, Store<EntityStore> store) {
        List<PageBuilder> pages = new ArrayList<>();
        List<PageConfig> pageConfigs = config.getPages();
        String backButtonText = config.getBackButtonText() != null ? config.getBackButtonText() : "Back";
        String nextButtonText = config.getNextButtonText() != null ? config.getNextButtonText() : "Next";

        List<String> pagesTitles = new ArrayList<>();

        pageConfigs.forEach(pageConfig -> pagesTitles.add(pageConfig.getTitle()));

        // Build all pages
        for (int i = 0; i < pageConfigs.size(); i++) {
            PageConfig pageConfig = pageConfigs.get(i);
            String pageTitle = pageConfig.getTitle();
            List<String> pageParagraphs = pageConfig.getParagraphs();
            boolean isFirstPage = (i == 0);
            boolean isLastPage = (i == pageConfigs.size() - 1);
            boolean allowDismiss = pageConfig.isAllowDismiss();

            // Build HTML content
            StringBuilder html = new StringBuilder();
            html.append("<div class=\"page-overlay\">\n");
            html.append("<div class=\"container\" data-hyui-title=\"" + pageTitle + "\">\n");
            html.append("""
                <div style="layout-mode: center; flex-weight: 1;">
                    <div style="layout-mode: top; flex-weight: 0.2; anchor-min-width: 150; anchor-max-width: 150; anchor-left: 0;">
            """);
            
            for (int j = 0; j < pagesTitles.size(); j++) {
                html.append("<button id=\"Button"+j+"\" style=\"anchor-horizontal: 1;\">" + pagesTitles.get(j).split(" ")[0] + "</button>");
            }

            html.append("""
                    </div>
                    <div style="layout-mode: top; flex-weight: 1;">
                        <div style="layout-mode: top; flex-weight: 1;">
            """);

            for (int k = 0; k < pageParagraphs.size(); k++) {
                html.append("<p>" + pageParagraphs.get(k) + "</p>");
            }
  
            html.append("""
                            </div>
                        <div style="layout-mode: center; flex-weight: 0; anchor-bottom: 0;">
            """);

            if (!isFirstPage) {
                html.append("<button id=\"backBtn"+i+"\" style=\"anchor-horizontal:1;\">" + backButtonText + "</button>\n");
            }

            if (!isLastPage) {
                html.append("<button id=\"nextBtn"+i+"\" style=\"anchor-horizontal:1;\">" + nextButtonText + "</button>\n");
            }

            html.append("""
                        </div>
                    </div>
                </div>
            """);
            html.append("    </div>\n");

            if (allowDismiss) {
                html.append("<button id=\"closeBtn"+i+"\" class=\"back-button\"></button>\n");
            }
            
            html.append("</div>");

            // Create page with appropriate lifetime
            CustomPageLifetime lifetime = allowDismiss
                ? CustomPageLifetime.CanDismissOrCloseThroughInteraction
                : CustomPageLifetime.CantClose;
            
            PageBuilder page = PageBuilder.detachedPage()
                .withLifetime(lifetime)
                .fromHtml(html.toString());

            pages.add(page);
        }

        // Set up navigation between pages
        for (int i = 0; i < pages.size(); i++) {
            PageBuilder currentPage = pages.get(i);

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
        }

        return pages;
    }

    private static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

}