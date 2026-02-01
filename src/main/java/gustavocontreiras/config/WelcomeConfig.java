package gustavocontreiras.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.Arrays;
import java.util.List;

public class WelcomeConfig {
    public static final BuilderCodec<WelcomeConfig> CODEC = BuilderCodec.builder(WelcomeConfig.class, WelcomeConfig::new)
            .append(new KeyedCodec<>("BackButtonText", Codec.STRING),
                    (config, value) -> config.backButtonText = value,
                    (config) -> config.backButtonText).add()
            .append(new KeyedCodec<>("NextButtonText", Codec.STRING),
                    (config, value) -> config.nextButtonText = value,
                    (config) -> config.nextButtonText).add()
            .append(new KeyedCodec<>("DoneButtonText", Codec.STRING),
                    (config, value) -> config.doneButtonText = value,
                    (config) -> config.doneButtonText).add()
            .append(new KeyedCodec<>("PageCounterText", Codec.STRING),
                    (config, value) -> config.pageCounterText = value,
                    (config) -> config.pageCounterText).add()
            .append(new KeyedCodec<>("MenuWidth", Codec.INTEGER),
                    (config, value) -> config.menuWidth = value,
                    (config) -> config.menuWidth).add()
            .append(new KeyedCodec<>("ContainerWidth", Codec.INTEGER),
                    (config, value) -> config.containerWidth = value,
                    (config) -> config.containerWidth).add()
            .append(new KeyedCodec<>("ContainerHeight", Codec.INTEGER),
                    (config, value) -> config.containerHeight = value,
                    (config) -> config.containerHeight).add()
            .append(new KeyedCodec<>("FontSize", Codec.INTEGER),
                    (config, value) -> config.fontSize = value,
                    (config) -> config.fontSize).add()
            .append(new KeyedCodec<>("AlwaysShow", Codec.BOOLEAN),
                    (config, value) -> config.alwaysShow = value,
                    (config) -> config.alwaysShow).add()
            .append(new KeyedCodec<>("Debug", Codec.BOOLEAN),
                    (config, value) -> config.debug = value,
                    (config) -> config.debug).add()
            .append(new KeyedCodec<>("ShowPageCounter", Codec.BOOLEAN),
                    (config, value) -> config.showPageCounter = value,
                    (config) -> config.showPageCounter).add()
            .append(new KeyedCodec<>("AllowExitOnAnyPage", Codec.BOOLEAN),
                    (config, value) -> config.allowExitOnAnyPage = value,
                    (config) -> config.allowExitOnAnyPage).add()
            .append(new KeyedCodec<>("HideInWorlds", new ArrayCodec<>(Codec.STRING, String[]::new)),
                    (config, value) -> config.hideInWorlds = value,
                    (config) -> config.hideInWorlds).add()
            .append(new KeyedCodec<>("Pages", new ArrayCodec<>(PageConfig.CODEC, PageConfig[]::new)),
                    (config, value) -> config.pages = value,
                    (config) -> config.pages).add()
            .build();

    private String backButtonText = "Back";
    private String nextButtonText = "Next";
    private String doneButtonText = "Done";
    private int menuWidth = 260;
    private int containerWidth = 900;
    private int containerHeight = 500;
    private int fontSize = 16;
    private boolean alwaysShow = false;
    private boolean debug = false;
    private boolean showPageCounter = true;
    private String pageCounterText = "Page";
    private boolean allowExitOnAnyPage = false;
    private String[] hideInWorlds = new String[0];
    private PageConfig[] pages = new PageConfig[0];

    public WelcomeConfig() {
    }

    /**
     * Creates a WelcomeConfig with default values and sample pages for reference.
     */
    public static WelcomeConfig createWithSamplePages() {
        WelcomeConfig config = new WelcomeConfig();

        // Page 1: Commands
        PageConfig commandsPage = new PageConfig();
        commandsPage.setTitle("Welcome to Hytale");
        commandsPage.setButtonTitle("Commands");
        ContentElement imgElement = new ContentElement("img",
                "welcome.png",
                "layout-mode: center;");
        imgElement.setWidth(482);
        imgElement.setHeight(134);
        commandsPage.setElements(Arrays.asList(
            imgElement,
            new ContentElement("p", ""),
            new ContentElement("p", "/help - shows all available commands"),
            new ContentElement("p", "/welcome - shows this window"),
            new ContentElement("p", "/modlist - shows all installed mods"),
            new ContentElement("p", "/lvl gui - panel to assign level points"),
            new ContentElement("p", "/simpleclaims - claim an area of the map just for you"),
            new ContentElement("p", "/hidearmor - hide your equipment on your skin"),
            new ContentElement("p", "/sit - allows changing the character's sitting positions"),
            new ContentElement("p", ""),
            new ContentElement("p", "Press ENTER or / to open the chat and execute these commands")
        ));

        // Page 2: Level
        PageConfig levelPage = new PageConfig();
        levelPage.setTitle("Level");
        levelPage.setButtonTitle("Level");
        levelPage.setElements(Arrays.asList(
                new ContentElement("p", "By killing creatures, you gain experience."),
                new ContentElement("p", ""),
                new ContentElement("p", "When you level up, type /lvl gui to assign your level points."),
                new ContentElement("p", ""),
                new ContentElement("p", "You can improve your health, mana, stamina, damage, defense,"),
                new ContentElement("p", "mining, woodcutting, oxygen, and ammo capacity.")
        ));

        // Page 3: Map
        PageConfig mapPage = new PageConfig();
        mapPage.setTitle("Map");
        mapPage.setButtonTitle("Map");
        mapPage.setElements(Arrays.asList(
                new ContentElement("p", "Places you visit will be permanently saved on your map."),
                new ContentElement("p", ""),
                new ContentElement("p", "Press M to view it.")
        ));

        // Page 4: Inventory
        PageConfig inventoryPage = new PageConfig();
        inventoryPage.setTitle("Inventory");
        inventoryPage.setButtonTitle("Inventory");
        inventoryPage.setElements(Arrays.asList(
                new ContentElement("p", "Press TAB to:"),
                new ContentElement("p", "- Equip armor pieces and off-hand items"),
                new ContentElement("p", "- Craft tools and crafting tables"),
                new ContentElement("p", "- Organize your items"),
                new ContentElement("p", ""),
                new ContentElement("p", "Press Shift + Left Mouse Button to transfer quickly."),
                new ContentElement("p", "Press Shift + Right Mouse Button to select half of the"),
                new ContentElement("p", "amount."),
                new ContentElement("p", "Press Right Mouse Button to select a single unit."),
                new ContentElement("p", ""),
                new ContentElement("p", "Items are automatically organized and stacked when placed"),
                new ContentElement("p", "in your inventory."),
                new ContentElement("p", ""),
                new ContentElement("p", "You can hide your armor on your skin using the /hidearmor command")
        ));

        // Page 5: Parry
        PageConfig parryPage = new PageConfig();
        parryPage.setTitle("Parry");
        parryPage.setButtonTitle("Parry");
        parryPage.setElements(Arrays.asList(
                new ContentElement("p", "When you block at the exact moment you are being attacked,"),
                new ContentElement("p", "the enemy will be staggered and vulnerable to a counterattack.")
        ));

        // Page 6: Durability
        PageConfig durabilityPage = new PageConfig();
        durabilityPage.setTitle("Durability");
        durabilityPage.setButtonTitle("Durability");
        durabilityPage.setElements(Arrays.asList(
                new ContentElement("p", "Weapon, tool, and equipment durability has been disabled.")
        ));

        // Page 7: Claiming
        PageConfig claimingPage = new PageConfig();
        claimingPage.setTitle("Claiming");
        claimingPage.setButtonTitle("Claiming");
        claimingPage.setElements(Arrays.asList(
                new ContentElement("p", "Type /simpleclaims to:"),
                new ContentElement("p", "- View areas already claimed by other players"),
                new ContentElement("p", "- View areas protected from destruction"),
                new ContentElement("p", "- Claim an area of the map for yourself"),
                new ContentElement("p", ""),
                new ContentElement("p", "You can also type '/simpleclaims claim' to claim the area"),
                new ContentElement("p", "you are in, or '/simpleclaims unclaim' to release it.")
        ));

        // Page 8: Death
        PageConfig deathPage = new PageConfig();
        deathPage.setTitle("Death");
        deathPage.setButtonTitle("Death");
        deathPage.setElements(Arrays.asList(
                new ContentElement("p", "When you die, some of your equipment, tools, and weapons may be"),
                new ContentElement("p", "lost."),
                new ContentElement("p", ""),
                new ContentElement("p", "A coffin containing your items will remain where you died until"),
                new ContentElement("p", "someone retrieves them.")
        ));

        // Page 9: Fishing
        PageConfig fishingPage = new PageConfig();
        fishingPage.setTitle("Fishing");
        fishingPage.setButtonTitle("Fishing");
        fishingPage.setElements(Arrays.asList(
                new ContentElement("p", "Craft a fishing rod through your inventory."),
                new ContentElement("p", ""),
                new ContentElement("p", "Place the bait in the water by right-clicking and wait"),
                new ContentElement("p", "a few moments."),
                new ContentElement("p", ""),
                new ContentElement("p", "When pulling the bait out of the water, you may have caught a fish.")
        ));

        // Page 10: Mount
        PageConfig mountPage = new PageConfig();
        mountPage.setTitle("Mount");
        mountPage.setButtonTitle("Mount");
        mountPage.setElements(Arrays.asList(
                new ContentElement("p", "Approach a horse and press F to mount it."),
                new ContentElement("p", ""),
                new ContentElement("p", "Type '/mount dismount' to return to walking on foot.")
        ));

        config.pages = new PageConfig[] {
                commandsPage, levelPage, mapPage, inventoryPage, parryPage,
                durabilityPage, claimingPage, deathPage, fishingPage, mountPage
        };
        return config;
    }

    public String getBackButtonText() {
        return backButtonText;
    }

    public void setBackButtonText(String backButtonText) {
        this.backButtonText = backButtonText;
    }

    public String getNextButtonText() {
        return nextButtonText;
    }

    public void setNextButtonText(String nextButtonText) {
        this.nextButtonText = nextButtonText;
    }

    public String getDoneButtonText() {
        return doneButtonText;
    }

    public void setDoneButtonText(String doneButtonText) {
        this.doneButtonText = doneButtonText;
    }

    public int getMenuWidth() {
        return menuWidth;
    }

    public void setMenuWidth(int menuWidth) {
        this.menuWidth = menuWidth;
    }

    public int getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    public int getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(int containerHeight) {
        this.containerHeight = containerHeight;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean getShowPageCounter() {
        return showPageCounter;
    }

    public void setShowPageCounter(boolean showPageCounter) {
        this.showPageCounter = showPageCounter;
    }

    public String getPageCounterText() {
        return pageCounterText;
    }

    public void setPageCounterText(String pageCounterText) {
        this.pageCounterText = pageCounterText;
    }

    public boolean getAllowExitOnAnyPage() {
        return allowExitOnAnyPage;
    }

    public void setAllowExitOnAnyPage(boolean allowExitOnAnyPage) {
        this.allowExitOnAnyPage = allowExitOnAnyPage;
    }

    public List<String> getHideInWorlds() {
        return Arrays.asList(hideInWorlds);
    }

    public void setHideInWorlds(List<String> hideInWorlds) {
        this.hideInWorlds = hideInWorlds.toArray(new String[0]);
    }

    public List<PageConfig> getPages() {
        return Arrays.asList(pages);
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages.toArray(new PageConfig[0]);
    }
}
