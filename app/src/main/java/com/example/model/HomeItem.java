package com.example.model;

public class HomeItem {
    private final int imageResId;
    private final String name;
    private final String tex;

    public HomeItem(int imageResId, String name, String tex) {
        this.imageResId = imageResId;
        this.name = name;
        this.tex = tex;
    }

    public String getTex() {
        return tex;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }
}
