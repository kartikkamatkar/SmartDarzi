package com.example.smartdarzi.models;

public class TailorService {

    private final int id;
    private final String serviceName;
    private final String description;
    private final double price;
    private final int turnaroundDays;

    public TailorService(int id, String serviceName, String description, double price, int turnaroundDays) {
        this.id = id;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.turnaroundDays = turnaroundDays;
    }

    public int getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getTurnaroundDays() {
        return turnaroundDays;
    }
}

