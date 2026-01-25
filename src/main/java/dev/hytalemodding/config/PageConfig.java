package dev.hytalemodding.config;

import java.util.List;

public class PageConfig {
    private String title;
    private String buttonTitle;
    private List<String> paragraphs;

    public PageConfig() {
    }

    public PageConfig(String title, List<String> paragraphs) {
        this.title = title;
        this.paragraphs = paragraphs;
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
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
