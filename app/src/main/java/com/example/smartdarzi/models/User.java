package com.example.smartdarzi.models;

public class User {

    private final int id;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String password;

    public User(int id, String fullName, String email, String phone, String password) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return fullName;
    }
}

