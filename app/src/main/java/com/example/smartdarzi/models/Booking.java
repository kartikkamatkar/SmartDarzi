package com.example.smartdarzi.models;

public class Booking {
    private final int id;
    private final int userId;
    private final String serviceName;
    private final String appointmentDate;
    private final String status;
    private final double totalPrice;
    private String tailorName;
    private String date;
    private String time;

    public Booking(int id, int userId, String serviceName, String appointmentDate, String status, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.date = appointmentDate;
        this.time = "";
        this.tailorName = "Pending";
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getServiceName() { return serviceName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public double getPrice() { return totalPrice; }
    public String getDate() { return date != null ? date : appointmentDate; }
    public String getTime() { return time != null ? time : ""; }
    public String getTailorName() { return tailorName; }
    public void setTailorName(String tailorName) { this.tailorName = tailorName; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
}
