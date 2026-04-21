package com.example.smartdarzi.models;

public class DesignItem {

    private final int imageResId;
    private final String name;
    private final double price;
    private final float rating;
    private String matchReason;

    public DesignItem(int imageResId, String name, double price, float rating) {
        this(imageResId, name, price, rating, null);
    }

    public DesignItem(int imageResId, String name, double price, float rating, String matchReason) {
        this.imageResId = imageResId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.matchReason = matchReason;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public String getMatchReason() {
        return matchReason;
    }
}

