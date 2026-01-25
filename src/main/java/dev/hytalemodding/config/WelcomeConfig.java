package dev.hytalemodding.config;

import java.util.List;

public class WelcomeConfig {
    private String backButtonText;
    private String nextButtonText;
    private List<PageConfig> pages;

    public WelcomeConfig() {
    }

    public WelcomeConfig(String backButtonText, String nextButtonText, List<PageConfig> pages) {
        this.backButtonText = backButtonText;
        this.nextButtonText = nextButtonText;
        this.pages = pages;
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

    public List<PageConfig> getPages() {
        return pages;
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages;
    }
}
