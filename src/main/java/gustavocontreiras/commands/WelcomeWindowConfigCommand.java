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
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import gustavocontreiras.WelcomeWindowPlugin;
import gustavocontreiras.config.ContentElement;
import gustavocontreiras.config.PageConfig;
import gustavocontreiras.config.WelcomeConfig;
import gustavocontreiras.events.WelcomeWindowEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;
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

        String hideInWorldsHtml = state.alwaysShow.get() ? """
                                    <div style="layout-mode: left; flex-weight: 1; anchor-bottom: 2; anchor-height: 30;">
                                        <p style="font-size: 14; anchor-width: 110; anchor-height: 30; vertical-align: middle;">Hide In Worlds</p>
                                        <input id="hideInWorlds" placeholder="e.g World2, World3" type="text" style="flex-weight: 1; anchor-left: 4;" />
                                    </div>
                """ : "";

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
                            <div style="layout-mode: left;">
                                <div style="layout-mode: top; flex-weight: 1;">
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
                                </div>
                                <div style="layout-mode: top; flex-weight: 1; anchor-left: 16;">
                                    <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Pages</p>

                                    <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                        <button id="openPagesBtn" style="anchor-horizontal: 1;">Open Pages Configuration</button>
                                    </div>
                                </div>
                            </div>

                            <div style="anchor-height: 8;"></div>
                            <p style="font-size: 16; color: #ffffff; anchor-bottom: 4;">Toggle Settings</p>

                            <div style="layout-mode: left; flex-weight: 1; ">
                                <div style="layout-mode: left; anchor-bottom: 4; anchor-height: 30;">
                                    <p style="font-size: 14; anchor-width: 220; anchor-height: 30; vertical-align: middle;">Always Show</p>
                                    <input id="alwaysShow" type="checkbox" value="false" />
                                </div>
                                """ + hideInWorldsHtml + """
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
        // Custom handler for alwaysShow: re-renders the page to show/hide the hideInWorlds field
        page.getById("alwaysShow", CheckBoxBuilder.class).ifPresent(field -> {
            field.editElementAfter((commandBuilder, selector) -> {
                commandBuilder.set(selector + " #CheckBox.Value", state.alwaysShow.get());
            });
            field.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                state.alwaysShow.set(value);
                store.getExternalData().getWorld().execute(() -> openConfigEditor(player, store, state));
            });
        });
        wireCheckBox(page, "debug", state.debug.get(), state.debug);
        wireCheckBox(page, "showPageCounter", state.showPageCounter.get(), state.showPageCounter);
        wireCheckBox(page, "allowExitOnAnyPage", state.allowExitOnAnyPage.get(), state.allowExitOnAnyPage);

        // Wire hideInWorlds field (only visible when alwaysShow is enabled)
        if (state.alwaysShow.get()) {
            wireTextField(page, "hideInWorlds", state.hideInWorlds.get(), state.hideInWorlds);
        }

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

                // Parse comma-separated world names and save
                List<String> hideInWorldsList = new ArrayList<>();
                String hideInWorldsStr = state.hideInWorlds.get();
                if (hideInWorldsStr != null && !hideInWorldsStr.trim().isEmpty()) {
                    for (String worldName : hideInWorldsStr.split(",")) {
                        String trimmed = worldName.trim();
                        if (!trimmed.isEmpty()) {
                            hideInWorldsList.add(trimmed);
                        }
                    }
                }
                cfg.setHideInWorlds(hideInWorldsList);

                // Save page data from state
                List<PageConfig> updatedPages = new ArrayList<>();
                for (int i = 0; i < state.pageTitles.size(); i++) {
                    PageConfig pc = new PageConfig();
                    pc.setTitle(state.pageTitles.get(i).get());
                    pc.setButtonTitle(state.pageButtonTitles.get(i).get());

                    List<ContentElement> elements = new ArrayList<>();
                    for (EditorElement editorEl : state.pageElements.get(i)) {
                        ContentElement el = new ContentElement(
                                editorEl.element.get(),
                                editorEl.content.get(),
                                editorEl.style.get(),
                                editorEl.id.get()
                        );
                        el.setWidth(editorEl.width.get().intValue());
                        el.setHeight(editorEl.height.get().intValue());
                        elements.add(el);
                    }
                    pc.setElements(elements);
                    updatedPages.add(pc);
                }
                cfg.setPages(updatedPages);

                config.save();

                player.sendMessage(Message.raw("[WelcomeWindow] Config saved successfully."));
                WelcomeWindowEvent.openWelcomeWindow(player);
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
                <div class="container" data-hyui-title="Pages Configuration" style="anchor-width: 1400; anchor-height: 900;">
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

                            <p style="font-size: 14; color: #cccccc; anchor-bottom: 4;">Elements</p>
                """);

            List<EditorElement> elements = state.pageElements.get(selectedIdx);
            for (int i = 0; i < elements.size(); i++) {
                boolean isImg = "img".equals(elements.get(i).element.get());
                if (isImg) {
                    htmlBuilder.append("""
                            <div style="layout-mode: left; anchor-top: 2;">
                                <button id="elUp_%d" style="anchor-min-width: 100; anchor-height: 38;">UP</button>
                                <button id="elDown_%d" style="anchor-min-width: 100; padding-left: 4; anchor-height: 38;">DOWN</button>
                                <select id="elTag_%d" style="padding-left: 4; anchor-width: 80;"></select>
                                <input id="elChild_%d" type="text" style="flex-weight: 1; padding-left: 4;" data-hyui-tooltiptext="Image name or URL.\n\nTo use local images:\n1. Open the .jar with a zip manager (e.g. 7-Zip)\n2. Put the image in Common/UI/Custom/\n3. The file name must end with @2x.png\n4. Use the name without @2x\n   (e.g. myimage@2x.png -> myimage.png)\n\nYou can also use URLs:\nhttps://example.com/image.png"/>
                                <p style="font-size: 12; anchor-width: 20; padding-left: 4;">W</p>
                                <input id="elWidth_%d" type="number" style="anchor-width: 60; anchor-height: 30;" />
                                <p style="font-size: 12; anchor-width: 16; padding-left: 4;">H</p>
                                <input id="elHeight_%d" type="number" style="anchor-width: 60; anchor-height: 30;" />
                                <select id="elStyle_%d" style="anchor-width: 100; padding-left: 4;"></select>
                                <button id="removeEl_%d" style="anchor-min-width: 100; padding-left: 4; anchor-height: 38;">-</button>
                            </div>
                    """.formatted(i, i, i, i, i, i, i, i));
                } else {
                    htmlBuilder.append("""
                            <div style="layout-mode: left; anchor-top: 2;">
                                <button id="elUp_%d" style="anchor-min-width: 100; anchor-height: 38;">UP</button>
                                <button id="elDown_%d" style="anchor-min-width: 100; padding-left: 4; anchor-height: 38;">DOWN</button>
                                <select id="elTag_%d" style="padding-left: 4; anchor-width: 80;"></select>
                                <input id="elChild_%d" type="text" style="flex-weight: 2; padding-left: 4;" />
                                <input id="elStyle_%d" type="text" style="flex-weight: 1; padding-left: 4;" data-hyui-tooltiptext="Set it as HyUI HTML 'style' property\n\nExample:\nfont-size: 12; anchor-height: 30;"/>
                                <button id="removeEl_%d" style="anchor-min-width: 100; padding-left: 4; anchor-height: 38;">-</button>
                            </div>
                    """.formatted(i, i, i, i, i, i));
                }
            }
        }

        htmlBuilder.append("""

                        </div>
                        <div style="layout-mode: center; anchor-top: 8;">
                            <button id="backBtn" style="anchor-height: 30;">Back</button>
                            <button id="addElement" style="anchor-height: 30; padding-left: 4;">Add element</button>
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
                List<EditorElement> els = new ArrayList<>();
                els.add(new EditorElement("p", "New page content", "", "", 0, 0));
                state.pageElements.add(els);
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

            List<EditorElement> elements = state.pageElements.get(selectedIdx);

            for (int i = 0; i < elements.size(); i++) {
                final int elIndex = i;
                EditorElement editorEl = elements.get(i);
                boolean isImg = "img".equals(editorEl.element.get());

                wireTagDropdown(page, "elTag_" + i, editorEl.element.get(), editorEl.element, player, store, state);
                wireTextField(page, "elChild_" + i, editorEl.content.get(), editorEl.content);

                if (isImg) {
                    wireNumberField(page, "elWidth_" + i, editorEl.width.get().intValue(), editorEl.width);
                    wireNumberField(page, "elHeight_" + i, editorEl.height.get().intValue(), editorEl.height);
                    wireStyleDropdown(page, "elStyle_" + i, editorEl.style.get(), editorEl.style);
                } else {
                    wireTextField(page, "elStyle_" + i, editorEl.style.get(), editorEl.style);
                }

                page.getById("elUp_" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        if (elIndex > 0) {
                            EditorElement temp = elements.get(elIndex);
                            elements.set(elIndex, elements.get(elIndex - 1));
                            elements.set(elIndex - 1, temp);
                            openPagesEditor(player, store, state);
                        }
                    });
                });

                page.getById("elDown_" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        if (elIndex < elements.size() - 1) {
                            EditorElement temp = elements.get(elIndex);
                            elements.set(elIndex, elements.get(elIndex + 1));
                            elements.set(elIndex + 1, temp);
                            openPagesEditor(player, store, state);
                        }
                    });
                });

                page.getById("removeEl_" + i, ButtonBuilder.class).ifPresent(button -> {
                    button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                        elements.remove(elIndex);
                        openPagesEditor(player, store, state);
                    });
                });
            }

            page.getById("addElement", ButtonBuilder.class).ifPresent(button -> {
                button.addEventListener(CustomUIEventBindingType.Activating, clickEvent -> {
                    elements.add(new EditorElement("p", "", "", "", 0, 0));
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
                state.pageElements.remove(pageIndex);
                state.selectedPageIndex.set(state.pageTitles.isEmpty() ? -1 : 0);
                openPagesEditor(player, store, state);
            });
        });

        page.open(playerRef, store);
    }

    private void wireTagDropdown(PageBuilder page, String id, String initialValue, AtomicReference<String> holder,
                                 Player player, Store<EntityStore> store, EditorState state) {
        page.getById(id, DropdownBoxBuilder.class).ifPresent(dropdown -> {
            dropdown.addEntry("p", "p");
            dropdown.addEntry("img", "img");
            dropdown.withValue(initialValue);
            dropdown.addEventListener(CustomUIEventBindingType.ValueChanged, (value, ctx) -> {
                holder.set(value);
                openPagesEditor(player, store, state);
            });
        });
    }

    private void wireStyleDropdown(PageBuilder page, String id, String initialValue, AtomicReference<String> holder) {
        page.getById(id, DropdownBoxBuilder.class).ifPresent(dropdown -> {
            dropdown.addEntry("layout-mode: left;", "Left");
            dropdown.addEntry("layout-mode: center;", "Center");
            dropdown.addEntry("layout-mode: right;", "Right");
            String value = initialValue != null && !initialValue.isEmpty() ? initialValue : "layout-mode: center;";
            dropdown.withValue(value);
            if (holder.get() == null || holder.get().isEmpty()) {
                holder.set(value);
            }
            dropdown.addEventListener(CustomUIEventBindingType.ValueChanged, (v, ctx) -> {
                holder.set(v);
            });
        });
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

    static class EditorElement {
        final AtomicReference<String> element;
        final AtomicReference<String> content;
        final AtomicReference<String> style;
        final AtomicReference<String> id;
        final AtomicReference<Double> width;
        final AtomicReference<Double> height;

        EditorElement(String element, String content, String style, String id, int width, int height) {
            this.element = new AtomicReference<>(element);
            this.content = new AtomicReference<>(content);
            this.style = new AtomicReference<>(style);
            this.id = new AtomicReference<>(id);
            this.width = new AtomicReference<>((double) width);
            this.height = new AtomicReference<>((double) height);
        }
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
        final AtomicReference<String> hideInWorlds;

        final List<AtomicReference<String>> pageTitles;
        final List<AtomicReference<String>> pageButtonTitles;
        final List<List<EditorElement>> pageElements;
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
            this.hideInWorlds = new AtomicReference<>(String.join(", ", cfg.getHideInWorlds()));

            this.pageTitles = new ArrayList<>();
            this.pageButtonTitles = new ArrayList<>();
            this.pageElements = new ArrayList<>();

            for (PageConfig pageConfig : cfg.getPages()) {
                this.pageTitles.add(new AtomicReference<>(pageConfig.getTitle()));
                this.pageButtonTitles.add(new AtomicReference<>(pageConfig.getButtonTitle()));

                List<EditorElement> els = new ArrayList<>();
                for (ContentElement el : pageConfig.getElements()) {
                    els.add(new EditorElement(
                            el.getElement(),
                            el.getContent() != null ? el.getContent() : "",
                            el.getStyle() != null ? el.getStyle() : "",
                            el.getId() != null ? el.getId() : "",
                            el.getWidth(),
                            el.getHeight()
                    ));
                }
                this.pageElements.add(els);
            }

            this.selectedPageIndex = new AtomicReference<>(cfg.getPages().isEmpty() ? -1 : 0);
        }
    }
}
