package gustavocontreiras.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

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
            .append(new KeyedCodec<>("Elements", new ArrayCodec<>(ContentElement.CODEC, ContentElement[]::new)),
                    (config, value) -> config.elements = value,
                    (config) -> config.elements).add()
            .build();

    private String title = "";
    private String buttonTitle = "";
    private ContentElement[] elements = new ContentElement[0];

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

    public List<ContentElement> getElements() {
        return Arrays.asList(elements);
    }

    public void setElements(List<ContentElement> elements) {
        this.elements = elements.toArray(new ContentElement[0]);
    }
}
