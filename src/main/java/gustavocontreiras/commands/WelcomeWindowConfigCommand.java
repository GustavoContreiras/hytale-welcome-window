package gustavocontreiras.commands;

import au.ellie.hyui.builders.ButtonBuilder;
import au.ellie.hyui.builders.CheckBoxBuilder;
import au.ellie.hyui.builders.DropdownBoxBuilder;
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
import gustavocontreiras.config.PageConfig;
import gustavocontreiras.config.WelcomeConfig;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;
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
            Config<WelcomeConfig> config = WelcomeWindowPlugin.getInstance().getWelcomeConfig();
            EditorState state = new EditorState(config.get());
            openConfigEditor(player, store, state);
        }, world);
    }

    private void openConfigEditor(Player player, Store<EntityStore> store, EditorState state) {
        Ref<EntityStore> ref = player.getReference();
        if (ref == null) {
            return;
        }

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        String html = """
            <div class="page-overlay">
                <div class="container" data-hyui-title="WelcomeWindow Config" style="anchor-width: 900; anchor-height: 620;">
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
                            <div style="layout-mode: left;">
                                <div style="layout-mode: top; flex-weight: 1;">
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
                                <div style="layout-mode: top; flex-weight: 1; padding-left: 16;">
                                    <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Pages</p>

                                    <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                        <button id="openPagesBtn" style="anchor-horizontal: 1;">Open Pages Configuration</button>
                                    </div>
                                </div>
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

        // Wire existing text fields from state
        wireTextField(page, "backButtonText", state.backButtonText.get(), state.backButtonText);
        wireTextField(page, "nextButtonText", state.nextButtonText.get(), state.nextButtonText);
        wireTextField(page, "doneButtonText", state.doneButtonText.get(), state.doneButtonText);
        wireTextField(page, "pageCounterText", state.pageCounterText.get(), state.pageCounterText);

        // Wire existing number fields from state
        wireNumberField(page, "menuWidth", state.menuWidth.get().intValue(), state.menuWidth);
        wireNumberField(page, "containerWidth", state.containerWidth.get().intValue(), state.containerWidth);
        wireNumberField(page, "containerHeight", state.containerHeight.get().intValue(), state.containerHeight);
        wireNumberField(page, "fontSize", state.fontSize.get().intValue(), state.fontSize);

        // Wire existing checkbox fields from state
        wireCheckBox(page, "alwaysShow", state.alwaysShow.get(), state.alwaysShow);
        wireCheckBox(page, "debug", state.debug.get(), state.debug);
        wireCheckBox(page, "showPageCounter", state.showPageCounter.get(), state.showPageCounter);
        wireCheckBox(page, "allowExitOnAnyPage", state.allowExitOnAnyPage.get(), state.allowExitOnAnyPage);

        // Wire "Open Pages Configuration" button
        page.getById("openPagesBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Open Pages Configuration");
            });

            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                openPagesEditor(player, store, state);
            });
        });

        // Save button
        page.getById("saveBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Save");
            });

            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                Config<WelcomeConfig> config = WelcomeWindowPlugin.getInstance().getWelcomeConfig();
                WelcomeConfig cfg = config.get();

                cfg.setBackButtonText(state.backButtonText.get());
                cfg.setNextButtonText(state.nextButtonText.get());
                cfg.setDoneButtonText(state.doneButtonText.get());
                cfg.setPageCounterText(state.pageCounterText.get());

                cfg.setMenuWidth(state.menuWidth.get().intValue());
                cfg.setContainerWidth(state.containerWidth.get().intValue());
                cfg.setContainerHeight(state.containerHeight.get().intValue());
                cfg.setFontSize(state.fontSize.get().intValue());

                cfg.setAlwaysShow(state.alwaysShow.get());
                cfg.setDebug(state.debug.get());
                cfg.setShowPageCounter(state.showPageCounter.get());
                cfg.setAllowExitOnAnyPage(state.allowExitOnAnyPage.get());

                // Save page data from state
                List<PageConfig> updatedPages = new ArrayList<>();
                for (int i = 0; i < state.pageTitles.size(); i++) {
                    PageConfig pc = new PageConfig();
                    pc.setTitle(state.pageTitles.get(i).get());
                    pc.setButtonTitle(state.pageButtonTitles.get(i).get());

                    List<String> paras = new ArrayList<>();
                    for (AtomicReference<String> paraRef : state.pageParagraphs.get(i)) {
                        paras.add(paraRef.get());
                    }
                    pc.setParagraphs(paras);
                    updatedPages.add(pc);
                }
                cfg.setPages(updatedPages);

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

    private void openPagesEditor(Player player, Store<EntityStore> store, EditorState state) {
        Ref<EntityStore> ref = player.getReference();
        if (ref == null) {
            return;
        }

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        int selectedIdx = state.selectedPageIndex.get();

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("""
            <div class="page-overlay">
                <div class="container" data-hyui-title="Pages Configuration" style="anchor-width: 900; anchor-height: 700;">
                    <div style="layout-mode: top; flex-weight: 1; padding: 8;">
                        <div style="layout-mode: topscrolling; flex-weight: 1;">

                            <div style="layout-mode: left; anchor-bottom: 8; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 150; anchor-height: 30; vertical-align: middle;">Select Page</p>
                                <div style="layout-mode: left; flex-weight: 1;">
                                    <select id="pageSelector" style="flex-weight: 1; anchor-left: 0;"></select>
                                    <button id="addPageBtn" style="anchor-height: 30; padding-left: 4">Add page</button>
                                    <button id="removePageBtn" style="anchor-height: 30; padding-left: 4">Remove page</button>
                                </div>
                            </div>
            """);

        if (selectedIdx >= 0 && selectedIdx < state.pageTitles.size()) {
            htmlBuilder.append("""
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 150; anchor-height: 30; vertical-align: middle;">Title</p>
                                <input id="pageTitle" type="text" style="flex-weight: 1" />
                            </div>
                            <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                <p style="font-size: 14; anchor-width: 150; anchor-height: 30; vertical-align: middle;">Button Title</p>
                                <input id="pageButtonTitle" type="text" style="flex-weight: 1" />
                            </div>

                            <p style="font-size: 14; color: #cccccc; anchor-bottom: 4;">Paragraphs</p>
                """);

            List<AtomicReference<String>> paragraphs = state.pageParagraphs.get(selectedIdx);
            for (int i = 0; i < paragraphs.size(); i++) {
                htmlBuilder.append("""
                            <div style="layout-mode: center; anchor-top: 2;">
                                <input id="para_%d" type="text" style="flex-weight: 1;" />
                                <button id="removePara_%d" style="anchor-max-width: 100; padding-left: 4;">-</button>
                            </div>
                    """.formatted(i, i));
            }
        }

        htmlBuilder.append("""

                        </div>
                        <div style="layout-mode: center; anchor-top: 8;">
                            <button id="backBtn" style="anchor-height: 30;">Back</button>
                            <button id="addPara" style="anchor-height: 30; padding-left: 4;">Add paragraph</button>
                        </div>
                    </div>
                </div>
                <button id="escBtn" class="back-button"></button>
            </div>
            """);

        String html = htmlBuilder.toString();

        PageBuilder page = PageBuilder.detachedPage()
            .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
            .fromHtml(html);

        // Wire page selector dropdown
        page.getById("pageSelector", DropdownBoxBuilder.class).ifPresent(dropdown -> {
            for (int i = 0; i < state.pageTitles.size(); i++) {
                String label = "Page " + (i + 1) + ": " + state.pageTitles.get(i).get();
                dropdown.addEntry(String.valueOf(i), label);
            }

            if (selectedIdx >= 0) {
                dropdown.withValue(String.valueOf(selectedIdx));
            }

            dropdown.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                try {
                    int newIndex = Integer.parseInt(value);
                    state.selectedPageIndex.set(newIndex);
                } catch (NumberFormatException e) {
                    return;
                }
                openPagesEditor(player, store, state);
            });
        });

        // Wire "Add page" button
        page.getById("addPageBtn", ButtonBuilder.class).ifPresent(button -> {
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                state.pageTitles.add(new AtomicReference<>("New page"));
                state.pageButtonTitles.add(new AtomicReference<>("New page"));
                List<AtomicReference<String>> paras = new ArrayList<>();
                paras.add(new AtomicReference<>("New page paragraph"));
                state.pageParagraphs.add(paras);
                state.selectedPageIndex.set(state.pageTitles.size() - 1);
                openPagesEditor(player, store, state);
            });
        });

        // Wire "Remove page" button
        page.getById("removePageBtn", ButtonBuilder.class).ifPresent(button -> {
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                if (selectedIdx >= 0 && selectedIdx < state.pageTitles.size()) {
                    String pageLabel = "Page " + (selectedIdx + 1) + ": " + state.pageTitles.get(selectedIdx).get();
                    openRemovePageConfirmation(player, store, state, selectedIdx, pageLabel);
                }
            });
        });

        // Wire page-specific fields if a page is selected
        if (selectedIdx >= 0 && selectedIdx < state.pageTitles.size()) {

            wireTextField(page, "pageTitle",
                state.pageTitles.get(selectedIdx).get(),
                state.pageTitles.get(selectedIdx));

            wireTextField(page, "pageButtonTitle",
                state.pageButtonTitles.get(selectedIdx).get(),
                state.pageButtonTitles.get(selectedIdx));

            List<AtomicReference<String>> paragraphs = state.pageParagraphs.get(selectedIdx);

            for (int i = 0; i < paragraphs.size(); i++) {
                final int paraIndex = i;

                wireTextField(page, "para_" + i,
                    paragraphs.get(i).get(),
                    paragraphs.get(i));

                page.getById("removePara_" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        paragraphs.remove(paraIndex);
                        openPagesEditor(player, store, state);
                    });
                });
            }

            page.getById("addPara", ButtonBuilder.class).ifPresent(button -> {
                button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                    paragraphs.add(new AtomicReference<>(""));
                    openPagesEditor(player, store, state);
                });
            });
        }

        // Back button - return to main config editor
        page.getById("backBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Back");
            });

            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                openConfigEditor(player, store, state);
            });
        });

        page.open(playerRef, store);
    }

    private void openRemovePageConfirmation(Player player, Store<EntityStore> store, EditorState state, int pageIndex, String pageLabel) {
        Ref<EntityStore> ref = player.getReference();
        if (ref == null) {
            return;
        }

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        String html = """
            <div class="page-overlay">
                <div class="container" data-hyui-title="Remove Page" style="anchor-width: 450; anchor-height: 180;">
                    <div style="layout-mode: top; flex-weight: 1; padding: 8;">
                        <div style="layout-mode: top; flex-weight: 1;">
                            <p style="font-size: 14; anchor-bottom: 8;">Are you sure you want to remove "%s"?</p>
                        </div>
                        <div style="layout-mode: center; anchor-top: 8;">
                            <button id="cancelRemoveBtn" style="anchor-horizontal: 1; padding-right: 4;">Cancel</button>
                            <button id="confirmRemoveBtn" style="anchor-horizontal: 1; padding-left: 4;">Confirm</button>
                        </div>
                    </div>
                </div>
                <button id="escBtn" class="back-button"></button>
            </div>
            """.formatted(pageLabel);

        PageBuilder page = PageBuilder.detachedPage()
            .withLifetime(CustomPageLifetime.CanDismissOrCloseThroughInteraction)
            .fromHtml(html);

        page.getById("cancelRemoveBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Cancel");
            });
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                openPagesEditor(player, store, state);
            });
        });

        page.getById("confirmRemoveBtn", ButtonBuilder.class).ifPresent(button -> {
            button.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + ".Text", "Confirm");
            });
            button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                state.pageTitles.remove(pageIndex);
                state.pageButtonTitles.remove(pageIndex);
                state.pageParagraphs.remove(pageIndex);
                state.selectedPageIndex.set(state.pageTitles.isEmpty() ? -1 : 0);
                openPagesEditor(player, store, state);
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

    private static class EditorState {
        final AtomicReference<String> backButtonText;
        final AtomicReference<String> nextButtonText;
        final AtomicReference<String> doneButtonText;
        final AtomicReference<String> pageCounterText;

        final AtomicReference<Double> menuWidth;
        final AtomicReference<Double> containerWidth;
        final AtomicReference<Double> containerHeight;
        final AtomicReference<Double> fontSize;

        final AtomicReference<Boolean> alwaysShow;
        final AtomicReference<Boolean> debug;
        final AtomicReference<Boolean> showPageCounter;
        final AtomicReference<Boolean> allowExitOnAnyPage;

        final List<AtomicReference<String>> pageTitles;
        final List<AtomicReference<String>> pageButtonTitles;
        final List<List<AtomicReference<String>>> pageParagraphs;
        final AtomicReference<Integer> selectedPageIndex;

        EditorState(WelcomeConfig cfg) {
            this.backButtonText = new AtomicReference<>(cfg.getBackButtonText());
            this.nextButtonText = new AtomicReference<>(cfg.getNextButtonText());
            this.doneButtonText = new AtomicReference<>(cfg.getDoneButtonText());
            this.pageCounterText = new AtomicReference<>(cfg.getPageCounterText());

            this.menuWidth = new AtomicReference<>((double) cfg.getMenuWidth());
            this.containerWidth = new AtomicReference<>((double) cfg.getContainerWidth());
            this.containerHeight = new AtomicReference<>((double) cfg.getContainerHeight());
            this.fontSize = new AtomicReference<>((double) cfg.getFontSize());

            this.alwaysShow = new AtomicReference<>(cfg.getAlwaysShow());
            this.debug = new AtomicReference<>(cfg.getDebug());
            this.showPageCounter = new AtomicReference<>(cfg.getShowPageCounter());
            this.allowExitOnAnyPage = new AtomicReference<>(cfg.getAllowExitOnAnyPage());

            this.pageTitles = new ArrayList<>();
            this.pageButtonTitles = new ArrayList<>();
            this.pageParagraphs = new ArrayList<>();

            for (PageConfig pageConfig : cfg.getPages()) {
                this.pageTitles.add(new AtomicReference<>(pageConfig.getTitle()));
                this.pageButtonTitles.add(new AtomicReference<>(pageConfig.getButtonTitle()));

                List<AtomicReference<String>> paras = new ArrayList<>();
                for (String p : pageConfig.getParagraphs()) {
                    paras.add(new AtomicReference<>(p));
                }
                this.pageParagraphs.add(paras);
            }

            this.selectedPageIndex = new AtomicReference<>(cfg.getPages().isEmpty() ? -1 : 0);
        }
    }
}
