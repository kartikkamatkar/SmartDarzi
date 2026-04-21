package com.example.smartdarzi.models;

public class Measurement {

    private final int id;
    private final int userId;
    private final String chest;
    private final String waist;
    private final String hip;
    private final String shoulder;
    private final String sleeveLength;
    private final String height;
    private final String notes;

    public Measurement(int id, int userId, String chest, String waist, String hip,
                       String shoulder, String sleeveLength, String height, String notes) {
        this.id = id;
        this.userId = userId;
        this.chest = chest;
        this.waist = waist;
        this.hip = hip;
        this.shoulder = shoulder;
        this.sleeveLength = sleeveLength;
        this.height = height;
        this.notes = notes;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getChest() { return chest; }
    public String getWaist() { return waist; }
    public String getHip() { return hip; }
    public String getShoulder() { return shoulder; }
    public String getSleeveLength() { return sleeveLength; }
    public String getHeight() { return height; }
    public String getNotes() { return notes; }
}
