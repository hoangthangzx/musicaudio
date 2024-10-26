package com.example.model;

import java.util.List;

public class Mp3File {
    private String name;
    private String path;
    private String duration;
    private String size;
    private String date;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Mp3File(String name, String path, String duration, String size, String date) {
        this.name = name;
        this.path = path;
        this.duration = duration;
        this.size = size;
        this.date = date;
    }

    public Mp3File() {
    }

    public Mp3File(String path) {
        this.path = path;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public String getDuration() { return duration; }
    public String getSize() { return size; }
    public String getDate() { return date; }
    public static int countSelectedFiles(List<Mp3File> mp3Files) {
        int count = 0;
        for (Mp3File mp3File : mp3Files) {
            if (mp3File.isSelected()) {
                count++;
            }
        }
        return count;
    }
}
