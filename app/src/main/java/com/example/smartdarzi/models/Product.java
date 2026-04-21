package com.example.smartdarzi.models;

import java.io.Serializable;

public class Product implements Serializable {

    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final int imageResId;
    private final String categoryName;
    private final int turnaroundDays;
    private final float rating;
    private final int stockQuantity;

    public Product(int id, String name, String description, double price,
                   int imageResId, String categoryName, int turnaroundDays,
                   float rating, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.categoryName = categoryName;
        this.turnaroundDays = turnaroundDays;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getCategoryName() { return categoryName; }
    public int getTurnaroundDays() { return turnaroundDays; }
    public float getRating() { return rating; }
    public int getStockQuantity() { return stockQuantity; }
}

