package com.example.smartdarzi.models;

public class TailorShop {
    private final String name;
    private final String phone;
    private final String address;
    private final String chowk;
    private final String mapQuery;

    public TailorShop(String name, String phone, String address, String chowk, String mapQuery) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.chowk = chowk;
        this.mapQuery = mapQuery;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getChowk() { return chowk; }
    public String getMapQuery() { return mapQuery; }
}
