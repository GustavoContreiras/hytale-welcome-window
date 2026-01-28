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
            .append(new KeyedCodec<>("Pages", new ArrayCodec<>(PageConfig.CODEC, PageConfig[]::new)),
                    (config, value) -> config.pages = value,
                    (config) -> config.pages).add()
            .build();

    private String backButtonText = "Back";
    private String nextButtonText = "Next";
    private String doneButtonText = "Finish";
    private int menuWidth = 150;
    private int containerWidth = 800;
    private int containerHeight = 500;
    private int fontSize = 18;
    private boolean alwaysShow = true;
    private boolean debug = false;
    private boolean showPageCounter = true;
    private String pageCounterText = "Page";
    private boolean allowExitOnAnyPage = false;
    private PageConfig[] pages = new PageConfig[0];

    public WelcomeConfig() {
    }

    /**
     * Creates a WelcomeConfig with default values and sample pages for reference.
     */
    public static WelcomeConfig createWithSamplePages() {
        WelcomeConfig config = new WelcomeConfig();

        PageConfig welcomePage = new PageConfig();
        welcomePage.setTitle("Welcome to Hytale");
        welcomePage.setButtonTitle("Commands");
        welcomePage.setParagraphs(Arrays.asList(
                "Here are some available commands:",
                "",
                "/help - shows all available commands",
                "/welcome - shows this welcome window"
        ));

        PageConfig dyingPage = new PageConfig();
        dyingPage.setTitle("Dying");
        dyingPage.setButtonTitle("Dying");
        dyingPage.setParagraphs(Arrays.asList(
                "When you are killed you loose part of your equipments."
        ));

        config.pages = new PageConfig[] { welcomePage, dyingPage };
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

    public List<PageConfig> getPages() {
        return Arrays.asList(pages);
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages.toArray(new PageConfig[0]);
    }
}
