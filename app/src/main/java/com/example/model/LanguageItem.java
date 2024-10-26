package com.example.model;


public class LanguageItem {
    private int imageResId;
    private String languageName;
    private String languageCode; // Add this field
    private boolean isSelected;

    public LanguageItem(int imageResId, String languageName, String languageCode, boolean isSelected) {
        this.imageResId = imageResId;
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}
