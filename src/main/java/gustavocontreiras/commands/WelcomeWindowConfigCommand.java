package gustavocontreiras.commands;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.CheckBoxBuilder;
import au.ellie.hyui.builders.NumberFieldBuilder;
import au.ellie.hyui.builders.PageBuilder;
import au.ellie.hyui.builders.TextFieldBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import gustavocontreiras.WelcomeWindowPlugin;
import gustavocontreiras.config.WelcomeConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class WelcomeWindowConfigCommand extends AbstractAsyncCommand {

    public WelcomeWindowConfigCommand(String name, String description) {
        super(name, description);
        this.setPermissionGroup(GameMode.Creative);
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        var sender = context.sender();
        if (!(sender instanceof Player player)) {
            context.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        // Check if player is OP
        Set<String> groups = PermissionsModule.get().getGroupsForUser(player.getUuid());
        if (!groups.contains("OP")) {
            context.sendMessage(Message.raw("[WelcomeWindow] You need OP permissions to edit the config."));
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
            openConfigEditor(player, store);
        }, world);
    }

    private void openConfigEditor(Player player, Store<EntityStore> store) {
        Ref<EntityStore> ref = player.getReference();
        if (ref == null) {
            return;
        }

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        Config<WelcomeConfig> config = WelcomeWindowPlugin.getInstance().getWelcomeConfig();
        WelcomeConfig cfg = config.get();

        // Current values in AtomicReferences for lambda capture
        AtomicReference<String> backButtonText = new AtomicReference<>(cfg.getBackButtonText());
        AtomicReference<String> nextButtonText = new AtomicReference<>(cfg.getNextButtonText());
        AtomicReference<String> doneButtonText = new AtomicReference<>(cfg.getDoneButtonText());
        AtomicReference<String> pageCounterText = new AtomicReference<>(cfg.getPageCounterText());

        AtomicReference<Double> menuWidth = new AtomicReference<>((double) cfg.getMenuWidth());
        AtomicReference<Double> containerWidth = new AtomicReference<>((double) cfg.getContainerWidth());
        AtomicReference<Double> containerHeight = new AtomicReference<>((double) cfg.getContainerHeight());
        AtomicReference<Double> fontSize = new AtomicReference<>((double) cfg.getFontSize());

        AtomicReference<Boolean> alwaysShow = new AtomicReference<>(cfg.getAlwaysShow());
        AtomicReference<Boolean> debug = new AtomicReference<>(cfg.getDebug());
        AtomicReference<Boolean> showPageCounter = new AtomicReference<>(cfg.getShowPageCounter());
        AtomicReference<Boolean> allowExitOnAnyPage = new AtomicReference<>(cfg.getAllowExitOnAnyPage());

        String html = """
            <div class="page-overlay">
                <div class="container" data-hyui-title="WelcomeWindow Config" style="anchor-width: 900; anchor-height: 600;">
                    <div style="layout-mode: top; flex-weight: 1; padding: 8;">
                        <div style="layout-mode: topscrolling; flex-weight: 1;">

                            <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Text Settings</p>

                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Back Button Text</p>
                                <input id="backButtonText" type="text" style="flex-weight: 1" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Next Button Text</p>
                                <input id="nextButtonText" type="text" style="flex-weight: 1" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Done Button Text</p>
                                <input id="doneButtonText" type="text" style="flex-weight: 1" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Page Counter Text</p>
                                <input id="pageCounterText" type="text" style="flex-weight: 1" />
                            </div>

                            <div style="anchor-height: 8;"></div>
                            <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Size Settings</p>

                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Menu Width</p>
                                <input id="menuWidth" type="number" style="flex-weight: 1; anchor-height: 30;" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Container Width</p>
                                <input id="containerWidth" type="number" style="flex-weight: 1; anchor-height: 30;" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Container Height</p>
                                <input id="containerHeight" type="number" style="flex-weight: 1; anchor-height: 30;" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Font Size</p>
                                <input id="fontSize" type="number" style="flex-weight: 1; anchor-height: 30;" />
                            </div>

                            <div style="anchor-height: 8;"></div>
                            <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Toggle Settings</p>

                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Always Show</p>
                                <input id="alwaysShow" type="checkbox" value="false" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Debug</p>
                                <input id="debug" type="checkbox" value="false" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Show Page Counter</p>
                                <input id="showPageCounter" type="checkbox" value="false" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Allow Exit On Any Page</p>
                                <input id="allowExitOnAnyPage" type="checkbox" value="false" />
                            </div>

                        </div>
                        <div style="layout-mode: center; anchor-top: 8;">
                        <button id="cancelBtn" style="anchor-horizontal: 1; padding-right: 4;">Cancel</button>
                        <button id="saveBtn" style="anchor-horizontal: 1; padding-left: 4;">Save</button>
                        </div>
                    </div>
                </div>
                <button id="escBtn" class="back-button"></button>
            </div>
            """;

        PageBuilder page = PageBuilder.detachedPage()
            .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
            .fromHtml(html);

        // Text fields
        wireTextField(page, "backButtonText", cfg.getBackButtonText(), backButtonText);
        wireTextField(page, "nextButtonText", cfg.getNextButtonText(), nextButtonText);
        wireTextField(page, "doneButtonText", cfg.getDoneButtonText(), doneButtonText);
        wireTextField(page, "pageCounterText", cfg.getPageCounterText(), pageCounterText);

        // Number fields
        wireNumberField(page, "menuWidth", cfg.getMenuWidth(), menuWidth);
        wireNumberField(page, "containerWidth", cfg.getContainerWidth(), containerWidth);
        wireNumberField(page, "containerHeight", cfg.getContainerHeight(), containerHeight);
        wireNumberField(page, "fontSize", cfg.getFontSize(), fontSize);

        // Checkbox fields
        wireCheckBox(page, "alwaysShow", cfg.getAlwaysShow(), alwaysShow);
        wireCheckBox(page, "debug", cfg.getDebug(), debug);
        wireCheckBox(page, "showPageCounter", cfg.getShowPageCounter(), showPageCounter);
        wireCheckBox(page, "allowExitOnAnyPage", cfg.getAllowExitOnAnyPage(), allowExitOnAnyPage);

        // Save button
        page.getById("saveBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Save");
            });

            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                cfg.setBackButtonText(backButtonText.get());
                cfg.setNextButtonText(nextButtonText.get());
                cfg.setDoneButtonText(doneButtonText.get());
                cfg.setPageCounterText(pageCounterText.get());

                cfg.setMenuWidth(menuWidth.get().intValue());
                cfg.setContainerWidth(containerWidth.get().intValue());
                cfg.setContainerHeight(containerHeight.get().intValue());
                cfg.setFontSize(fontSize.get().intValue());

                cfg.setAlwaysShow(alwaysShow.get());
                cfg.setDebug(debug.get());
                cfg.setShowPageCounter(showPageCounter.get());
                cfg.setAllowExitOnAnyPage(allowExitOnAnyPage.get());

                config.save();

                player.sendMessage(Message.raw("[WelcomeWindow] Config saved successfully."));
                player.getPageManager().setPage(player.getReference(), store, Page.None);
            });
        });

        // Cancel button
        page.getById("cancelBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Cancel");
            });

            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                player.getPageManager().setPage(player.getReference(), store, Page.None);
            });
        });

        page.open(playerRef, store);
    }

    private void wireTextField(PageBuilder page, String id, String initialValue, AtomicReference<String> holder) {
        page.getById(id, TextFieldBuilder.class).ifPresent(field -> {
            field.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Value", initialValue);
            });
            field.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                holder.set(value);
            });
        });
    }

    private void wireNumberField(PageBuilder page, String id, int initialValue, AtomicReference<Double> holder) {
        page.getById(id, NumberFieldBuilder.class).ifPresent(field -> {
            field.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Value", (double) initialValue);
            });
            field.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                holder.set(value);
            });
        });
    }

    private void wireCheckBox(PageBuilder page, String id, boolean initialValue, AtomicReference<Boolean> holder) {
        page.getById(id, CheckBoxBuilder.class).ifPresent(field -> {
            field.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + " #CheckBox.Value", initialValue);
            });
            field.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                holder.set(value);
            });
        });
    }
}
