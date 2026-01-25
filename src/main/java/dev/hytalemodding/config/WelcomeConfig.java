package dev.hytalemodding.config;

import java.util.List;

public class WelcomeConfig {
    private String backButtonText;
    private String nextButtonText;
    private String doneButtonText;
    private Integer menuWidth;
    private Integer containerWidth;
    private Integer containerHeight;
    private Integer fontSize;
    private Boolean alwaysShow;
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

    public String getDoneButtonText() {
        return doneButtonText;
    }

    public void setDoneButtonText(String nextButtonText) {
        this.doneButtonText = nextButtonText;
    }

    public Integer getMenuWidth() {
        return menuWidth;
    }

    public void setMenuWidth(Integer menuWidth) {
        this.menuWidth = menuWidth;
    }

    public Integer getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(Integer containerWidth) {
        this.containerWidth = containerWidth;
    }

    public Integer getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(Integer containerHeight) {
        this.containerHeight = containerHeight;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public List<PageConfig> getPages() {
        return pages;
    }

    public void setPages(List<PageConfig> pages) {
        this.pages = pages;
    }
}
