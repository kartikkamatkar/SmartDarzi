package com.example.smartdarzi.models;

public class Category {

    private final int id;
    private final String name;
    private final int imageResId;
    private final int cardColorRes;

    public Category(int id, String name, int imageResId, int cardColorRes) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.cardColorRes = cardColorRes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getCardColorRes() {
        return cardColorRes;
    }
}

