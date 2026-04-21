package com.example.smartdarzi.models;

public class CarouselItem {
    private int imageResource;
    private String title;
    private String subtitle;

    public CarouselItem(int imageResource, String title, String subtitle) {
        this.imageResource = imageResource;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}

