package com.example.smartdarzi.models;

public class Service {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private double rating;
    private int turnaroundDays;
    private int stockQuantity;

    public Service(int id, String name, String description, double price, String category, double rating, int turnaroundDays, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.turnaroundDays = turnaroundDays;
        this.stockQuantity = stockQuantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public int getTurnaroundDays() { return turnaroundDays; }
    public int getStockQuantity() { return stockQuantity; }
}
