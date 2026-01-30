package gustavocontreiras.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.Arrays;
import java.util.List;

public class ContentElement {
    public static final BuilderCodec<ContentElement> CODEC = BuilderCodec.builder(ContentElement.class, ContentElement::new)
            .append(new KeyedCodec<>("Element", Codec.STRING),
                    (el, value) -> el.element = value,
                    (el) -> el.element).add()
            .append(new KeyedCodec<>("Content", Codec.STRING),
                    (el, value) -> el.content = value,
                    (el) -> el.content).add()
            .append(new KeyedCodec<>("Style", Codec.STRING),
                    (el, value) -> el.style = value,
                    (el) -> el.style).add()
            .append(new KeyedCodec<>("Id", Codec.STRING),
                    (el, value) -> el.id = value,
                    (el) -> el.id).add()
            .append(new KeyedCodec<>("Width", Codec.INTEGER),
                    (el, value) -> el.width = value,
                    (el) -> el.width).add()
            .append(new KeyedCodec<>("Height", Codec.INTEGER),
                    (el, value) -> el.height = value,
                    (el) -> el.height).add()
            .build();

    private String element = "p";
    private String content = "";
    private String style = "";
    private String id = "";
    private int width = 0;
    private int height = 0;

    public ContentElement() {
    }

    public ContentElement(String element, String content) {
        this.element = element;
        this.content = content;
    }

    public ContentElement(String element, String content, String style) {
        this.element = element;
        this.content = content;
        this.style = style;
    }

    public ContentElement(String element, String content, String style, String id) {
        this.element = element;
        this.content = content;
        this.style = style;
        this.id = id;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Renders this element to an HTML string.
     *
     * @param defaultFontSize the default font size to apply if no style is set
     * @return HTML string
     */
    public String toHtml(int defaultFontSize) {
        StringBuilder sb = new StringBuilder();

        if ("img".equals(element)) {
            boolean hasStyle = style != null && !style.isEmpty();
            if (hasStyle) {
                sb.append("<div style=\"").append(style).append("\">");
            }
            boolean isDynamic = content != null && content.startsWith("http");
            sb.append("<img");
            if (isDynamic) {
                sb.append(" class=\"dynamic-image\"");
            }
            if (content != null && !content.isEmpty()) {
                sb.append(" src=\"").append(content).append("\"");
            }
            if (id != null && !id.isEmpty()) {
                sb.append(" id=\"").append(id).append("\"");
            }
            if (width > 0) {
                sb.append(" width=\"").append(width).append("\"");
            }
            if (height > 0) {
                sb.append(" height=\"").append(height).append("\"");
            }
            sb.append(" />");
            if (hasStyle) {
                sb.append("</div>");
            }
            sb.append("\n");
            return sb.toString();
        }

        sb.append("<").append(element);

        if (id != null && !id.isEmpty()) {
            sb.append(" id=\"").append(id).append("\"");
        }

        if (style != null && !style.isEmpty()) {
            sb.append(" style=\"").append(style).append("\"");
        } else {
            sb.append(" style=\"font-size: ").append(defaultFontSize).append(";\"");
        }

        sb.append(">");

        if (content != null && !content.isEmpty()) {
            sb.append(content);
        }

        sb.append("</").append(element).append(">\n");
        return sb.toString();
    }
}
