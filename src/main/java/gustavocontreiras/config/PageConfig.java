package gustavocontreiras.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.Arrays;
import java.util.List;

public class PageConfig {
    public static final BuilderCodec<PageConfig> CODEC = BuilderCodec.builder(PageConfig.class, PageConfig::new)
            .append(new KeyedCodec<>("Title", Codec.STRING),
                    (config, value) -> config.title = value,
                    (config) -> config.title).add()
            .append(new KeyedCodec<>("ButtonTitle", Codec.STRING),
                    (config, value) -> config.buttonTitle = value,
                    (config) -> config.buttonTitle).add()
            .append(new KeyedCodec<>("Paragraphs", Codec.STRING_ARRAY),
                    (config, value) -> config.paragraphs = value,
                    (config) -> config.paragraphs).add()
            .build();

    private String title = "";
    private String buttonTitle = "";
    private String[] paragraphs = new String[0];

    public PageConfig() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    public List<String> getParagraphs() {
        return Arrays.asList(paragraphs);
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs.toArray(new String[0]);
    }
}
