package dev.hytalemodding.config;

import java.util.List;

public class PageConfig {
    private String title;
    private List<String> paragraphs;
    private boolean allowDismiss;

    public PageConfig() {
    }

    public PageConfig(String title, List<String> paragraphs, boolean allowDismiss) {
        this.title = title;
        this.paragraphs = paragraphs;
        this.allowDismiss = allowDismiss;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public boolean isAllowDismiss() {
        return allowDismiss;
    }

    public void setAllowDismiss(boolean allowDismiss) {
        this.allowDismiss = allowDismiss;
    }
}
