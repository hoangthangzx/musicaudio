package com.example.model;

public class voidchangerItem {
    private final int imageResId; // ID của hình ảnh theme
    private final String name; // Tên của theme

    // Constructor để khởi tạo các thuộc tính
    public voidchangerItem(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    // Phương thức getter để lấy ID của hình ảnh
    public int getImageResId() {
        return imageResId;
    }

    // Phương thức getter để lấy tên của theme
    public String getName() {
        return name;
    }
}
